package pentarctagon.hmis.npc.smods;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.FleetInflater;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipVariantAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.loading.HullModSpecAPI;
import org.apache.log4j.Logger;
import pentarctagon.hmis.constants.Luna;
import pentarctagon.hmis.constants.Other;
import pentarctagon.hmis.data.campaign.rulecmd.utils.LunaHelper;
import pentarctagon.hmis.npc.smods.hullcheck.*;

import java.lang.invoke.MethodHandles;
import java.util.*;

public class AddSmodsInflater
implements FleetInflater
{
	private static final Logger log = Logger.getLogger(MethodHandles.lookup().lookupClass());
	private final FleetInflater originalFleetInflater;
	private final Random random = new Random();

	private static final Map<ShipAPI.HullSize, Integer> fluxOrdnanceBySize = Map.of(
		ShipAPI.HullSize.FRIGATE, 10,
		ShipAPI.HullSize.DESTROYER, 20,
		ShipAPI.HullSize.CRUISER, 30,
		ShipAPI.HullSize.CAPITAL_SHIP, 50
	);
	private static final Map<ShipAPI.HullSize, Integer> addHullModsIfOrdnance = Map.of(
		ShipAPI.HullSize.FRIGATE, 5,
		ShipAPI.HullSize.DESTROYER, 10,
		ShipAPI.HullSize.CRUISER, 15,
		ShipAPI.HullSize.CAPITAL_SHIP, 25
	);

	public AddSmodsInflater(FleetInflater originalFleetInflater)
	{
		this.originalFleetInflater = originalFleetInflater;
	}

	@Override
	public void inflate(CampaignFleetAPI fleet)
	{
		originalFleetInflater.inflate(fleet);

		if(getQuality() < 1.0f)
		{
			log.info("[HMIS] Skipping s-mod setup for faction's fleet due to low quality ("+getQuality()+"): "+fleet.getFaction().getId());
			return;
		}
		if(HullCheckManager.factionKnowsEvaluatableHullmods(fleet.getFaction().getId()))
		{
			log.info("[HMIS] Skipping s-mod setup for faction's fleet due to not knowing any hullmods: "+fleet.getFaction().getId());
			return;
		}
		if(LunaHelper.getInteger(Luna.HMIS_NPC_SMODS_CAP, Other.MAX_SMODS) < 1)
		{
			log.info("[HMIS] NPC s-mods disabled");
			return;
		}
		if(HullCheckManager.isIgnoredFaction(fleet.getFaction().getId()))
		{
			log.info("[HMIS] ignoring fleet for faction: "+fleet.getFaction().getId());
			return;
		}

		for(FleetMemberAPI ship : fleet.getFleetData().getMembersListCopy())
		{
			ShipVariantAPI variant = ship.getVariant();
			if(variant.isCivilian())
			{
				continue;
			}

			// copy and shuffle so the choice of hullmods below isn't always the same
			Collections.shuffle(HullCheckManager.getEvaluators());

			StringBuilder results = new StringBuilder();

			results.append(ship.getHullSpec().getHullId())
				.append(":\n");

			// generates a random integer between 0 and 19
			// ie:
			//     quality (1.35 * 100) % 20 == 15, generated number is 10, then ship gets an extra s-mod
			//     quality (1.25 * 100) % 20 == 5 , generated number is 10, then ship doesn't get an extra s-mod
			int extraSmod = random.nextInt(20) < (getQuality() * 100) % 20 && getAverageNumSMods() < LunaHelper.getInteger(Luna.HMIS_NPC_SMODS_CAP, Other.MAX_SMODS) ? 1 : 0;

			// s-mod non-built-in hullmods already on the ship that give a benefit
			for(String id : new ArrayList<>(variant.getNonBuiltInHullmods()))
			{
				if(variant.getSMods().size() >= getAverageNumSMods()+extraSmod)
				{
					break;
				}
				HullModSpecAPI spec = Global.getSettings().getHullModSpec(id);
				if(
					spec.getEffect().hasSModEffect() &&
					!spec.getEffect().isSModEffectAPenalty() &&
					!variant.getPermaMods().contains(id) &&
					!HullCheckManager.isNeverBuildIn(id) &&
					spec.getFrigateCost() > 0 &&
					!spec.hasTag("no_build_in")
				)
				{
					variant.removeMod(spec.getId());
					variant.addPermaMod(spec.getId(), true);
					results.append("removed/s-modded: ").append(id).append("\n");
				}
			}

			// still s-mod slots left over, so add up to the limit
			for(HullModEvaluator eval : HullCheckManager.getEvaluators())
			{
				if(variant.getSMods().size() >= getAverageNumSMods()+extraSmod)
				{
					break;
				}

				if(HullCheckManager.factionHasHullmod(fleet.getFaction().getId(), eval.getId()) && eval.evaluate(ship, true))
				{
					variant.addPermaMod(eval.getId(), true);
					results.append("s-modded: ").append(eval.getId()).append("\n");
				}
			}

			// make use of OP that's been freed up
			int freedOrdnance = variant.getUnusedOP(ship.getCaptain().getStats());
			if(freedOrdnance > 0)
			{
				// add hullmods if enough OP was freed
				for(HullModEvaluator eval : HullCheckManager.getEvaluators())
				{
					int cost = Global.getSettings().getHullModSpec(eval.getId()).getCostFor(variant.getHullSize());
					if(HullCheckManager.factionHasHullmod(fleet.getFaction().getId(), eval.getId()) && cost <= freedOrdnance && eval.evaluate(ship, false))
					{
						variant.addMod(eval.getId());
						results.append("added: ").append(eval.getId()).append("\n");
						freedOrdnance -= cost;
					}

					if(freedOrdnance <= addHullModsIfOrdnance.get(variant.getHullSize()))
					{
						break;
					}
				}

				// after hullmods added, put any remaining OP into vents/capacitors
				if(freedOrdnance > 0)
				{
					int ventOrdnanceRemaining = fluxOrdnanceBySize.get(variant.getHullSize()) - variant.getNumFluxVents();
					int capacitorOrdnanceRemaining = fluxOrdnanceBySize.get(variant.getHullSize()) - variant.getNumFluxCapacitors();

					if(freedOrdnance <= ventOrdnanceRemaining)
					{
						variant.setNumFluxVents(variant.getNumFluxVents() + freedOrdnance);
						results.append("added vents: ").append(freedOrdnance).append("\n");
						freedOrdnance = 0;
					}
					else
					{
						variant.setNumFluxVents(fluxOrdnanceBySize.get(variant.getHullSize()));
						results.append("added vents: ").append(ventOrdnanceRemaining).append("\n");
						freedOrdnance -= ventOrdnanceRemaining;
					}

					if(freedOrdnance <= capacitorOrdnanceRemaining)
					{
						variant.setNumFluxCapacitors(variant.getNumFluxCapacitors() + freedOrdnance);
						results.append("added capacitors: ").append(freedOrdnance).append("\n");
						freedOrdnance = 0;
					}
					else
					{
						variant.setNumFluxCapacitors(fluxOrdnanceBySize.get(variant.getHullSize()));
						results.append("added vents: ").append(capacitorOrdnanceRemaining).append("\n");
						freedOrdnance -= capacitorOrdnanceRemaining;
					}
				}
			}

			results.append("ordnance remaining: ").append(freedOrdnance).append("\n");
			log.debug(results);
		}

		fleet.getFleetData().setSyncNeeded();
		fleet.getFleetData().syncIfNeeded();
	}

	@Override
	public boolean removeAfterInflating()
	{
		return this.originalFleetInflater == null || this.originalFleetInflater.removeAfterInflating();
	}

	@Override
	public void setRemoveAfterInflating(boolean removeAfterInflating)
	{
		if(this.originalFleetInflater != null)
		{
			this.originalFleetInflater.setRemoveAfterInflating(removeAfterInflating);
		}
	}

	@Override
	public Object getParams()
	{
		return this.originalFleetInflater != null ? this.originalFleetInflater.getParams() : null;
	}

	@Override
	public float getQuality()
	{
		return this.originalFleetInflater != null ? this.originalFleetInflater.getQuality() : 1.0F;
	}

	@Override
	public void setQuality(float quality)
	{
		if(this.originalFleetInflater != null)
		{
			this.originalFleetInflater.setQuality(quality);
		}
	}

	@Override
	public int getAverageNumSMods()
	{
		float quality = (originalFleetInflater.getQuality() * 100) - 100;
		if(quality <= 0)
		{
			return 0;
		}
		// cap s-mods for modded factions
		if(quality > 20*LunaHelper.getInteger(Luna.HMIS_NPC_SMODS_CAP, Other.MAX_SMODS))
		{
			return LunaHelper.getInteger(Luna.HMIS_NPC_SMODS_CAP, Other.MAX_SMODS);
		}

		return (int)quality / 20;
	}
}
