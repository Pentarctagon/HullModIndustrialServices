package pentarctagon.hmis.data.campaign.rulecmd.ui;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CustomDialogDelegate;
import com.fs.starfarer.api.campaign.CustomUIPanelPlugin;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.combat.HullModEffect;
import com.fs.starfarer.api.combat.ShipVariantAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.loading.HullModSpecAPI;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.CustomPanelAPI;
import pentarctagon.hmis.data.campaign.rulecmd.ui.plugin.BuildInPlugin;
import pentarctagon.hmis.data.campaign.rulecmd.utils.Constants;
import pentarctagon.hmis.data.campaign.rulecmd.utils.Costs;
import pentarctagon.hmis.data.campaign.rulecmd.utils.Sizing;
import pentarctagon.hmis.data.campaign.rulecmd.utils.ui.BuildInHullModDialogCreator;
import pentarctagon.hmis.data.campaign.rulecmd.utils.ui.HullModButtonData;
import pentarctagon.hmis.data.campaign.rulecmd.utils.ui.PanelCreator;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * The dialog for adding s-mods, enhancing mods, or removing s-mods
 */
public class BuildInHullModDelegate
implements CustomDialogDelegate
{
	private CustomDialogDelegate.CustomDialogCallback callback;
	private final ShipVariantAPI selectedVariant;
	private final FleetMemberAPI fleetMember;
	private final float shipScrollPanelY;
	private final BuildInPlugin plugin;
	private final List<HullModButtonData> buttonData = new ArrayList<>();
	private final InteractionDialogAPI dialog;
	private final float dWidth;

	public BuildInHullModDelegate(FleetMemberAPI fleetMember, ShipVariantAPI selectedVariant, InteractionDialogAPI dialog, float shipScrollPanelY, float dWidth)
	{
		this.plugin = new BuildInPlugin(fleetMember, selectedVariant);
		this.selectedVariant = selectedVariant;
		this.fleetMember = fleetMember;
		this.dialog = dialog;
		this.shipScrollPanelY = shipScrollPanelY;
		this.dWidth = dWidth;
		// First generate HullModButtonData for all built in S-mods (so they can be removed)
		buttonData.addAll(listHullMods(selectedVariant, true));
		// Then add the HullModButtonData for all not built hullmods (so they can be added)
		buttonData.addAll(listHullMods(selectedVariant, false));
	}

	@Override
	public void createCustomDialog(CustomPanelAPI panel, CustomDialogDelegate.CustomDialogCallback callback)
	{
		String titleString = "Choose hull mods to build in";
		float titleHeight = 90f;
		float shipInfoPanelPad = 40f;
		float yPad = 5f;

		// Set the callback function, so customDialogConfirm can call it and create the ship list again
		// Without calling the callback, it's not possible to create the ship list in customDialogConfirm (I think)
		this.callback = callback;
		BuildInHullModDialogCreator.shouldRecreateShipPanel = true;

		// credits and story points the player currently has
		// don't need to store the return value since these don't change while the dialog is open
		PanelCreator.createLabelWithVariables(panel, "Credits: %,d", Color.WHITE, 30f, Alignment.LMID, Costs.getPlayerCredits());
		PanelCreator.createLabelWithVariables(panel, "Story points: %s", Color.WHITE, 50f, Alignment.LMID, Costs.getPlayerStoryPoints());
		// Initialize the S-mod counter and costs at the top. This tracks the "pending" values that will be set when the player hits confirm.
		LabelWithVariables countLabel = PanelCreator.createLabelWithVariables(panel, "S-mods: %s/%s", Color.WHITE, 70f, Alignment.LMID, selectedVariant.getSMods().size(), Constants.MAX_SMODS).created();
		LabelWithVariables currentCostsLabel = PanelCreator.createLabelWithVariables(panel, "Cost: %s story points, %,d credits", Color.WHITE, 90f, Alignment.LMID, 0, 0).created();

		// Static text at the top of the screen. The function positions it at the top of panel
		CustomPanelAPI titlePanel = PanelCreator.createTitle(panel, titleString, titleHeight).panel();

		// Panel with the ship picture and name
		CustomPanelAPI shipInfoPanel = PanelCreator.createShipInfoPanel(panel, fleetMember, selectedVariant, callback, shipScrollPanelY);
		shipInfoPanel.getPosition().belowMid(titlePanel, shipInfoPanelPad);

		// A scrollable list of hullmods. First the mods that are built in (for removal), then the mods that are not built in (so they can be selected for building in)
		float hullModPanelHeight = panel.getPosition().getHeight() - shipInfoPanel.getPosition().getHeight() - titlePanel.getPosition().getHeight() - shipInfoPanelPad - yPad;

		PanelCreator.PanelCreatorData<List<HullModButton>> createdButtonsData = PanelCreator.createHullModPanel(panel, hullModPanelHeight, dWidth, buttonData, plugin);
		createdButtonsData.panel().getPosition().belowMid(shipInfoPanel, yPad);

		plugin.init(createdButtonsData, countLabel, currentCostsLabel);
	}

	/**
	 * Player clicked cancel, don't do anything
	 */
	@Override
	public void customDialogCancel()
	{
		recreateShipPanel();
	}

	/**
	 * Player clicked confirm, apply selected changes
	 */
	@Override
	public void customDialogConfirm()
	{
		for(HullModButton button : plugin.getSelected())
		{
			// if a built-in mod is selected, remove it
			// else, make it built-in
			if(button.getData().isBuiltIn())
			{
				selectedVariant.removePermaMod(button.getData().id());
				dialog.getTextPanel().addPara("Removed " + button.getData().name()).setHighlight(button.getData().name());
			}
			else
			{
				String hullModName = Global.getSettings().getHullModSpec(button.getData().id()).getDisplayName();

				if(button.getData().isEnhanceOnly())
				{
					selectedVariant.getSModdedBuiltIns().add(button.getData().id());
					dialog.getTextPanel().addPara("Enhanced " + hullModName).setHighlight(hullModName);
				}
				else
				{
					selectedVariant.addPermaMod(button.getData().id(), true);
					dialog.getTextPanel().addPara("Built in " + hullModName).setHighlight(hullModName);

				}
			}
		}

		checkAndApplyParadePiece();
		checkAndApplyOverburdened();
		checkAndApplyMaintenanceNightmare();

		Global.getSector().getPlayerFleet().getCargo().getCredits().subtract(plugin.getCreditCost());
		Global.getSector().getPlayerPerson().getStats().spendStoryPoints(plugin.getStoryPointCost(), false, null, false, null);

		recreateShipPanel();
	}

	// add/remove parade piece hullmod
	private void checkAndApplyParadePiece()
	{
		if(selectedVariant.getSMods().size() >= Constants.MAX_SMODS && !selectedVariant.hasHullMod(Constants.PARADE_PIECE))
		{
			selectedVariant.addPermaMod(Constants.PARADE_PIECE);
			dialog.getTextPanel().addPara(selectedVariant.getFullDesignationWithHullNameForShip() + "'s hull is completely useless in your fleet").setHighlight(selectedVariant.getFullDesignationWithHullNameForShip());
		}
		else if(selectedVariant.getSMods().size() < Constants.MAX_SMODS && selectedVariant.hasHullMod(Constants.PARADE_PIECE))
		{
			selectedVariant.removePermaMod(Constants.PARADE_PIECE);
			dialog.getTextPanel().addPara(selectedVariant.getFullDesignationWithHullNameForShip() + "'s hull is no longer completely useless in your fleet").setHighlight(selectedVariant.getFullDesignationWithHullNameForShip());
		}
	}
	// add/remove overburdened hullmod
	private void checkAndApplyOverburdened()
	{
		if(selectedVariant.getSMods().size() > Constants.MAX_SAFE_SMODS && selectedVariant.getSMods().size() < Constants.MAX_SMODS && !selectedVariant.hasHullMod(Constants.OVERBURDENED))
		{
			selectedVariant.addPermaMod(Constants.OVERBURDENED);
			dialog.getTextPanel().addPara(selectedVariant.getFullDesignationWithHullNameForShip() + "'s hull is overburdened").setHighlight(selectedVariant.getFullDesignationWithHullNameForShip());
		}
		else if((selectedVariant.getSMods().size() <= Constants.MAX_SAFE_SMODS && selectedVariant.hasHullMod(Constants.OVERBURDENED)) || selectedVariant.hasHullMod(Constants.PARADE_PIECE))
		{
			selectedVariant.removePermaMod(Constants.OVERBURDENED);
			dialog.getTextPanel().addPara(selectedVariant.getFullDesignationWithHullNameForShip() + "'s hull is no longer overburdened").setHighlight(selectedVariant.getFullDesignationWithHullNameForShip());
		}
	}
	// add/remove maintenance nightmare hullmod
	private void checkAndApplyMaintenanceNightmare()
	{
		if(selectedVariant.getSMods().size() >= Constants.MAX_SAFE_SMODS && !selectedVariant.hasHullMod(Constants.MAINTENANCE_NIGHTMARE))
		{
			selectedVariant.addPermaMod(Constants.MAINTENANCE_NIGHTMARE);
			dialog.getTextPanel().addPara(selectedVariant.getFullDesignationWithHullNameForShip() + " now requires extensive maintenance").setHighlight(selectedVariant.getFullDesignationWithHullNameForShip());
		}
		else if(selectedVariant.getSMods().size() < Constants.MAX_SAFE_SMODS && selectedVariant.hasHullMod(Constants.MAINTENANCE_NIGHTMARE))
		{
			selectedVariant.removePermaMod(Constants.MAINTENANCE_NIGHTMARE);
			dialog.getTextPanel().addPara(selectedVariant.getFullDesignationWithHullNameForShip() + " no longer requires extensive maintenance").setHighlight(selectedVariant.getFullDesignationWithHullNameForShip());
		}
	}

	@Override
	public String getCancelText()
	{
		return "Cancel";
	}

	@Override
	public String getConfirmText()
	{
		return "Confirm";
	}

	@Override
	public CustomUIPanelPlugin getCustomPanelPlugin()
	{
		return plugin;
	}

	@Override
	public boolean hasCancelButton()
	{
		return true;
	}

	/**
	 * Close this dialog and re-show the dialog with the list of ships in your fleet, either on confirm or cancel
	 */
	private void recreateShipPanel()
	{
		// Don't recreate ship selection dialog if switching to module selection dialog
		if(BuildInHullModDialogCreator.shouldRecreateShipPanel)
		{
			BuildInHullModDialogCreator.shouldRecreateShipPanel = false;
			callback.dismissCustomDialog(1);
			dialog.showCustomDialog(Sizing.PANEL_WIDTH, Sizing.panelHeight(), new SelectShipDelegate(dialog));
		}
	}

	/**
	 * returnBuiltIn = true -> returns list of built-in S-mods.
	 * returnBuiltIn = false -> returns list of hullmods that can be built in (including ones that are enhanceOnly).
	 * Meant to be called twice to get the full list of hullmods that can be changed via this dialog.
	 */
	public static List<HullModButtonData> listHullMods(ShipVariantAPI selectedVariant, boolean returnBuiltIn)
	{
		Collection<String> enhancedAlready = selectedVariant.getSModdedBuiltIns();
		List<String> ids = new ArrayList<>();
		// index of first player-added hullmod
		// comes after all the hullmods that are enhance-only
		int firstIndexToBeCounted = 0;

		// just get all s-mods
		// else, bet s-mods that come with the ship and can be enhanced
		if(returnBuiltIn)
		{
			ids = new ArrayList<>(selectedVariant.getSMods());
		}
		else
		{
			if(selectedVariant.getHullSpec() != null)
			{
				for(String id : selectedVariant.getHullSpec().getBuiltInMods())
				{
					HullModSpecAPI hullMod = Global.getSettings().getHullModSpec(id);
					HullModEffect effect = hullMod.getEffect();
					// there is an s-mod effect (aka if the hullmod does anything - this doesn't exclude hullmods that have no bonus effect for being built in
					// the effect isn't a penalty (aka it's not a d-mod)
					// the hullmod wasn't an enhancement (can't remove those)
					if(effect.hasSModEffect() && !effect.isSModEffectAPenalty() && !enhancedAlready.contains(id))
					{
						ids.add(id);
					}
				}
			}

			firstIndexToBeCounted = ids.size();

			// add hullmods added by the player via the refit screen
			for(String id : selectedVariant.getNonBuiltInHullmods())
			{
				HullModSpecAPI hullMod = Global.getSettings().getHullModSpec(id);
				if(!hullMod.isHidden() && !hullMod.isHiddenEverywhere())
				{
					ids.add(id);
				}
			}
		}

		// for every hullmod in the list, create a button for it
		List<HullModButtonData> buttonData = new ArrayList<>();
		for(int i = 0; i < ids.size(); i++)
		{
			HullModSpecAPI hullMod = Global.getSettings().getHullModSpec(ids.get(i));
			boolean isEnhanceOnly = i < firstIndexToBeCounted;
			buttonData.add(
				new HullModButtonData(
					hullMod.getId(),
					hullMod.getDisplayName() + (isEnhanceOnly ? "*" : ""),
					hullMod.getSpriteName(),
					"",
					hullMod.getDescription(selectedVariant.getHullSize()),
					hullMod.getEffect(),
					selectedVariant.getHullSize(),
					0,
					0,
					isEnhanceOnly,
					returnBuiltIn
				)
			);
		}
		return buttonData;
	}
}
