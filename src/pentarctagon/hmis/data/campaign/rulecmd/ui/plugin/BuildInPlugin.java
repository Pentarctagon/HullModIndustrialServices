package pentarctagon.hmis.data.campaign.rulecmd.ui.plugin;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignUIAPI;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipVariantAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.fleet.FleetMemberType;
import com.fs.starfarer.api.loading.HullModSpecAPI;
import com.fs.starfarer.api.mission.FleetSide;
import com.fs.starfarer.api.ui.LabelAPI;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.campaign.fleet.FleetMember;
import com.fs.starfarer.combat.CombatEngine;
import com.fs.starfarer.combat.CombatFleetManager;
import com.fs.starfarer.loading.specs.HullVariantSpec;
import pentarctagon.hmis.data.campaign.rulecmd.utils.ui.PanelCreator;
import pentarctagon.hmis.data.campaign.rulecmd.ui.HullModButton;
import pentarctagon.hmis.data.campaign.rulecmd.ui.LabelWithVariables;
import pentarctagon.hmis.data.campaign.rulecmd.utils.Costs;

import java.util.BitSet;
import java.util.List;

public class BuildInPlugin
extends Selector<HullModButton>
{
	private boolean needRemoveText = true;
	private boolean needEnhanceText = true;
	private boolean needBuildInText = true;
	private LabelWithVariables countLabel;
	private LabelWithVariables currentCostsLabel;
	private final FleetMemberAPI fleetMember;
	private final ShipVariantAPI checkerVariant;
	private final ShipVariantAPI originalVariant;

	public BuildInPlugin(FleetMemberAPI fleetMember, ShipVariantAPI variant)
	{
		this.fleetMember = fleetMember;
		this.originalVariant = variant;
		this.checkerVariant = variant.clone();
	}

	public void init(PanelCreator.PanelCreatorData<List<HullModButton>> data, LabelWithVariables countLabel, LabelWithVariables currentCostsLabel)
	{
		items = data.created();
		this.countLabel = countLabel;
		this.currentCostsLabel = currentCostsLabel;
		update();
	}

	/*
	 * The calculated costs are already shown in the label, so just get them from there
	 */
	public int getCreditCost()
	{
		return currentCostsLabel.getVar(0);
	}

	/**
	 * Set all hullmod buttons as enabled or disabled
	 */
	public void update()
	{
		BitSet unapplicable = disableUnapplicable();

		// get first selected regular hullmod
		int selectedRegularHullmod = -1;
		for(int i = 0; i < items.size(); i++)
		{
			HullModButton item = items.get(i);
			if(item.isSelected() && !item.getData().isEnhanceOnly() && !item.getData().isBuiltIn())
			{
				selectedRegularHullmod = i;
				break;
			}
		}

		for(int i = 0; i < items.size(); i++)
		{
			HullModButton button = items.get(i);
			if(unapplicable.get(i))
			{
				continue;
			}

			if(checkerVariant.getTags().contains("unrestorable"))
			{
				disable(i, "Ship is unrestorable", false);
				continue;
			}

			// disable regular hullmod buttons if s-mod limit reached by clicking another regular hullmod
			int maxSmods = Costs.getTotalSmods(fleetMember);
			if(countLabel.getVar(0) >= maxSmods && !button.getData().isBuiltIn())
			{
				disable(i, "Ship s-mod limit reached", false);
				continue;
			}

			// handle case of: click s-mod -> click hullmod -> click s-mod again
			// otherwise this let's you bypass the s-mod limit
			if(countLabel.getVar(0) > maxSmods && button.getData().isBuiltIn() && selectedRegularHullmod != -1)
			{
				forceDeselect(selectedRegularHullmod);
				update();
				return;
			}

			// enhancing a hullmod or removing an s-mod have the same cost
			// else use the cost for adding a s-mod
			int nextCost = Costs.getSmodCreditCost(checkerVariant, button.getData().isEnhanceOnly() || button.getData().isBuiltIn());
			if(getCreditCost() + nextCost > Costs.getPlayerCredits())
			{
				disable(i, "Not enough credits", false);
				continue;
			}

			enable(i, button.getDefaultDescription());
		}
	}

	/**
	 * Disable an item, but only if it isn't already selected.
	 */
	private void disable(int index, String reason, boolean highlight)
	{
		HullModButton item = items.get(index);
		// Ignore already selected items
		if(item.isSelected())
		{
			return;
		}
		item.disable(reason, highlight);
	}

	/**
	 * Enable an item.
	 */
	private void enable(int index, String reason)
	{
		items.get(index).enable(reason);
	}

	/**
	 * Returns a set of indices of buttons that were disabled by this function.
	 */
	private BitSet disableUnapplicable()
	{
		boolean checkedEntriesChanged = true;
		BitSet disabledIndices = new BitSet();
		InteractionDialogAPI dialog = Global.getSector().getCampaignUI().getCurrentInteractionDialog();
		SectorEntityToken interactionTarget = dialog == null ? null : dialog.getInteractionTarget();

		while(checkedEntriesChanged)
		{
			ShipAPI checkerShip = makeShip(checkerVariant, fleetMember);
			// Since hull mods may have dependencies, some checked entries may need to be unchecked.
			// Since dependencies can be chained, we need to do this in a loop.
			// (# of loops is bounded by # of checked entries as well as longest hull mod dependency chain)
			checkedEntriesChanged = false;
			for(int i = 0; i < items.size(); i++)
			{
				HullModButton button = items.get(i);
				if(button.getData().isEnhanceOnly())
				{
					continue;
				}

				HullModSpecAPI hullMod = Global.getSettings().getHullModSpec(button.getData().id());
				boolean shouldDisable = false;
				String disableText = null;

				if(!hullMod.getEffect().isApplicableToShip(checkerShip) && !button.getData().isBuiltIn() && !originalVariant.hasHullMod(button.getData().id()))
				{
					String reason = hullMod.getEffect().getUnapplicableReason(checkerShip);
					// Can build in any number of logistics hull mods
					// Don't use s-mods in the check ship as we want to be able to tell when incompatibilities arise via forcible removing of non s-mods
					if(reason != null && !reason.startsWith("Maximum of 2 non-built-in \"Logistics\""))
					{
						disableText = shortenText(reason, button.getDescription());
						shouldDisable = true;
					}

				}
				else if(interactionTarget != null && interactionTarget.getMarket() != null)
				{
					String tradeModeString = interactionTarget.getMemory().getString("$tradeMode");
					CampaignUIAPI.CoreUITradeMode tradeMode = tradeModeString == null ? CampaignUIAPI.CoreUITradeMode.NONE : CampaignUIAPI.CoreUITradeMode.valueOf(tradeModeString);
					if(!originalVariant.hasHullMod(button.getData().id()) && !hullMod.getEffect().canBeAddedOrRemovedNow(checkerShip, interactionTarget.getMarket(), tradeMode))
					{
						String reason = hullMod.getEffect().getCanNotBeInstalledNowReason(checkerShip, interactionTarget.getMarket(), tradeMode);
						shouldDisable = true;
						if(reason != null && !button.getData().isBuiltIn())
						{
							// getCanNotBeInstalledNowReason() returns a weird message when trying to build in logistic
							// hullmods while not at a spaceport or station
							reason = reason.replace("Can only be removed at", "Can only be built in at");
						}
						disableText = shortenText(reason, button.getDescription());
					}
				}

				if(hullMod.hasTag("no_build_in"))
				{
					shouldDisable = true;
					disableText = hullMod.getDisplayName() + " can't be built in";
				}

				if(shouldDisable)
				{
					if(button.isSelected())
					{
						forceDeselect(i);
						checkedEntriesChanged = true;
					}

					if(disableText == null)
					{
						disableText = "Can't build in (no reason given, default message)";
					}

					disable(i, disableText, true);
					disabledIndices.set(i);
				}
			}
		}

		return disabledIndices;
	}

	@Override
	protected void onSelected(int index)
	{
		HullModButton hullModButton = items.get(index);
		if(hullModButton.getData().isBuiltIn())
		{
			countLabel.changeVar(0, countLabel.getVar(0) - 1);
		}
		else if(!hullModButton.getData().isEnhanceOnly())
		{
			countLabel.changeVar(0, countLabel.getVar(0) + 1);
		}
		currentCostsLabel.changeVar(0, currentCostsLabel.getVar(0)+Costs.getSmodCreditCost(originalVariant, hullModButton.getData().isEnhanceOnly() || hullModButton.getData().isBuiltIn()));

		if(!hullModButton.getData().isBuiltIn())
		{
			String hullModId = items.get(index).getData().id();
			checkerVariant.addMod(hullModId);
			if(testForDesync())
			{
				forceDeselect(index);
				Global.getSector().getCampaignUI().getMessageDisplay().addMessage("Can't build in due to custom hull mod incompatibility", Misc.getNegativeHighlightColor());
			}
		}
		update();
	}

	/**
	 * Tests if a ship made from checkerVariant still has all the hull mods that checkerVariant does.
	 * If not, this likely means that a mod forcibly removed some other hull mod(s) due to mod incompatibilities,
	 * so adding this hull mod would not be safe.
	 */
	private boolean testForDesync()
	{
		ShipVariantAPI cloneVariant = checkerVariant.clone();
		makeShip(cloneVariant, fleetMember);
		if(cloneVariant.hasHullMod("ML_incompatibleHullmodWarning"))
		{
			return true;
		}
		for(String id : checkerVariant.getHullMods())
		{
			if(!cloneVariant.hasHullMod(id))
			{
				return true;
			}
		}
		return false;
	}

	@Override
	protected void onDeselected(int index)
	{
		HullModButton hullModButton = items.get(index);
		if(hullModButton.getData().isBuiltIn())
		{
			countLabel.changeVar(0, countLabel.getVar(0) + 1);
		}
		else if(!hullModButton.getData().isEnhanceOnly())
		{
			countLabel.changeVar(0, countLabel.getVar(0) - 1);
		}
		currentCostsLabel.changeVar(0, currentCostsLabel.getVar(0)-Costs.getSmodCreditCost(originalVariant, hullModButton.getData().isEnhanceOnly() || hullModButton.getData().isBuiltIn()));

		if(!hullModButton.getData().isBuiltIn())
		{
			String hullModId = items.get(index).getData().id();
			// Don't remove hull mods that were already on the ship
			if(!originalVariant.hasHullMod(hullModId))
			{
				checkerVariant.removeMod(hullModId);
			}
		}
		update();
	}

	@Override
	protected void forceDeselect(int index)
	{
		super.forceDeselect(index);
		HullModButton button = items.get(index);
		if(!button.getData().isEnhanceOnly())
		{
			countLabel.changeVar(0, countLabel.getVar(0) - 1);
			currentCostsLabel.changeVar(0, currentCostsLabel.getVar(0)-Costs.getSmodCreditCost(originalVariant, button.getData().isBuiltIn() || button.getData().isEnhanceOnly()));
		}
		String hullModId = items.get(index).getData().id();
		// Don't remove hull mods that were already on the ship
		if(!originalVariant.hasHullMod(hullModId))
		{
			checkerVariant.removeMod(hullModId);
		}
	}

	public boolean isNeedRemoveText()
	{
		return needRemoveText;
	}
	public void setNeedRemoveText(boolean needRemoveText)
	{
		this.needRemoveText = needRemoveText;
	}

	public boolean isNeedEnhanceText()
	{
		return needEnhanceText;
	}
	public void setNeedEnhanceText(boolean needEnhanceText)
	{
		this.needEnhanceText = needEnhanceText;
	}

	public boolean isNeedBuildInText()
	{
		return needBuildInText;
	}
	public void setNeedBuildInText(boolean needBuildInText)
	{
		this.needBuildInText = needBuildInText;
	}

	/**
	 * If [variant == fleetMember.getVariant()], then this is a base ship.
	 * If [variant != fleetMember.getVariant()], then this is a module.
	 */
	public static ShipAPI makeShip(ShipVariantAPI variant, FleetMemberAPI fleetMember)
	{
		// Create a temporary fleet member
		FleetMember tempFleetMember = new FleetMember(0, (HullVariantSpec) variant, FleetMemberType.SHIP);
		tempFleetMember.setId(fleetMember.getId());
		ShipAPI ship = tempFleetMember.instantiateForCombat(null, 0, (CombatFleetManager) CombatEngine.getInstance().getFleetManager(FleetSide.PLAYER));
		ship.setName(fleetMember.getShipName());
		return ship;
	}

	private static String shortenText(String text, LabelAPI label)
	{
		if(text == null)
		{
			return null;
		}
		float ellipsesWidth = label.computeTextWidth("...");
		float maxWidth = label.getPosition().getWidth() * 0.95f - ellipsesWidth;
		if(label.computeTextWidth(text) <= maxWidth)
		{
			return text;
		}
		int left = 0, right = text.length();

		String newText = text;
		while(right > left)
		{
			int mid = (left + right) / 2;
			newText = text.substring(0, mid);
			if(label.computeTextWidth(newText) > maxWidth)
			{
				right = mid;
			}
			else
			{
				left = mid + 1;
			}
		}
		return newText + "...";
	}
}
