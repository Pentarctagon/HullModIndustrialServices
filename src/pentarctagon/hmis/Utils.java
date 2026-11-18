package pentarctagon.hmis;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.econ.Industry;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.impl.campaign.ids.Industries;
import com.fs.starfarer.api.impl.campaign.ids.Stats;
import com.fs.starfarer.api.loading.IndustrySpecAPI;
import pentarctagon.hmis.constants.Luna;
import pentarctagon.hmis.constants.Other;
import pentarctagon.hmis.data.campaign.rulecmd.utils.LunaHelper;
import pentarctagon.hmis.doctrine.listener.PlayerFactionShipQuality;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.HashSet;

public class Utils
{
	/**
	 * added == true  -> adding an s-mod
	 * added == false -> removing s-mod or enhancing default hullmod
	 */
	public static float setSmodCreditCost(ShipAPI.HullSize ship, MarketAPI market)
	{
		BigDecimal qualityModifier = getCostMultiplier(market);

		int cost = shipSizeSmodCost(ship);

		double lunaMultiplier = LunaHelper.getDouble(Luna.HMIS_CREDITS_MULTIPLIER, 1d);

		return qualityModifier.multiply(new BigDecimal(cost)).multiply(new BigDecimal(lunaMultiplier)).intValue();
	}

	/**
	 * Increases cost based on the production quality.
	 * Under 100% increases costs, over 100% decreases costs, by the percent under or over.
	 * Under 100% returns a negative value, over 100% returns a positive value.
	 */
	private static BigDecimal getCostMultiplier(MarketAPI market)
	{
		BigDecimal adjustedQuality = getAdjustedQuality(market);

		if(market.getIndustry(Other.HULL_MOD_SERVICES).isImproved())
		{
			adjustedQuality = adjustedQuality.add(new BigDecimal("0.2"));
		}
		BigDecimal reduced = adjustedQuality.subtract(new BigDecimal(2)).abs();

		// no, this isn't going to be completely free
		//noinspection ComparatorResultComparison
		if(reduced.compareTo(new BigDecimal("0.1")) == -1)
		{
			return new BigDecimal("0.1");
		}
		else
		{
			return reduced;
		}
	}

	public static BigDecimal getAdjustedQuality(MarketAPI market)
	{
		if(market.isPlayerOwned())
		{
			BigDecimal doctrineQuality = getPlayerFactionDoctrineQuality();
			return new BigDecimal(market.getShipQualityFactor()).subtract(doctrineQuality).add(PlayerFactionShipQuality.getQualityOnLastTick());
		}
		else
		{
			return new BigDecimal(market.getShipQualityFactor());
		}
	}

	public static BigDecimal getPlayerFactionDoctrineQuality()
	{
		return new BigDecimal(Global.getSector().getPlayerFaction().getProduction().getFaction().getDoctrine().getShipQualityContribution());
	}

	public static int shipSizeSmodCost(ShipAPI.HullSize size)
	{
		if(size == ShipAPI.HullSize.FRIGATE)
		{
			return Global.getSettings().getInt("changeSmodsCost_FRIGATE");
		}
		if(size == ShipAPI.HullSize.DESTROYER)
		{
			return Global.getSettings().getInt("changeSmodsCost_DESTROYER");
		}
		if(size == ShipAPI.HullSize.CRUISER)
		{
			return Global.getSettings().getInt("changeSmodsCost_CRUISER");
		}
		if(size == ShipAPI.HullSize.CAPITAL_SHIP)
		{
			return Global.getSettings().getInt("changeSmodsCost_CAPITAL_SHIP");
		}
		return 100_000_000;
	}

	public static int getPlayerCommodityAmount(String commodity)
	{
		return (int) Global.getSector().getPlayerFleet().getCargo().getCommodityQuantity(commodity);
	}

	public static final String SMODS = "SMODS";
	public static final String BUILTIN_SMODS = "BUILTIN_SMODS";
	public static final String PERMAMODS = "PERMAMODS";
	public static final String SUPPRESSEDMODS = "SUPPRESSEDMODS";
	public static final String HULLMODS = "HULLMODS";
	public static HashMap<String, HashSet<String>> getDistinctHullmods(FleetMemberAPI ship)
	{
		HashMap<String, HashSet<String>> hullmods = new HashMap<>();
		hullmods.put(SMODS, new HashSet<>());
		hullmods.put(BUILTIN_SMODS, new HashSet<>());
		hullmods.put(PERMAMODS, new HashSet<>());
		hullmods.put(SUPPRESSEDMODS, new HashSet<>());
		hullmods.put(HULLMODS, new HashSet<>());

		for(String hullmod : ship.getVariant().getSMods())
		{
			hullmods.get(SMODS).add(hullmod);
		}
		for(String hullmod : ship.getVariant().getPermaMods())
		{
			if(!hullmods.get(SMODS).contains(hullmod))
			{
				hullmods.get(PERMAMODS).add(hullmod);
			}
		}
		for(String hullmod : ship.getVariant().getSModdedBuiltIns())
		{
			if(!hullmods.get(SMODS).contains(hullmod) && !hullmods.get(PERMAMODS).contains(hullmod))
			{
				hullmods.get(BUILTIN_SMODS).add(hullmod);
			}
		}
		for(String hullmod : ship.getVariant().getSuppressedMods())
		{
			hullmods.get(SUPPRESSEDMODS).add(hullmod);
		}
		for(String hullmod : ship.getVariant().getHullMods())
		{
			if(!hullmods.get(SMODS).contains(hullmod) && !hullmods.get(PERMAMODS).contains(hullmod) && !hullmods.get(BUILTIN_SMODS).contains(hullmod) && !hullmods.get(SUPPRESSEDMODS).contains(hullmod))
			{
				hullmods.get(HULLMODS).add(hullmod);
			}
		}

		return hullmods;
	}

	public static int getPlayerCredits()
	{
		return (int)Global.getSector().getPlayerFleet().getCargo().getCredits().get();
	}
	public static MarketAPI getCurrentMarket()
	{
		return Global.getSector().getCampaignUI().getCurrentInteractionDialog().getInteractionTarget().getMarket();
	}

	public static int getBaseSmods()
	{
		return Global.getSettings().getInt("maxPermanentHullmods");
	}
	private static int getBonusSmods(FleetMemberAPI ship)
	{
		return (int)ship.getStats().getDynamic().getMod(Stats.MAX_PERMANENT_HULLMODS_MOD).getFlatBonus();
	}

	/**
	 * Base s-mods from global settings plus ship-specific modifier to s-mod limit
	 */
	public static int getTotalSmods(FleetMemberAPI ship)
	{
		return getBaseSmods()+getBonusSmods(ship);
	}

	/**
	 * Some mods such as AotD add upgrades to the Orbital Works, so also need to check if any industry downgrades into an orbital works
	 */
	public static int findShipIndustryValue(MarketAPI market)
	{
		if(market.hasIndustry(Industries.ORBITALWORKS))
		{
			return market.getIndustry(Industries.ORBITALWORKS).isDisrupted() ? Other.NO_SHIP_INDUSTRY : Other.ORBITAL_INDUSTRY;
		}
		if(market.hasIndustry(Industries.HEAVYINDUSTRY))
		{
			return market.getIndustry(Industries.HEAVYINDUSTRY).isDisrupted() ? Other.NO_SHIP_INDUSTRY : Other.HEAVY_INDUSTRY;
		}

		for(Industry ind : market.getIndustries())
		{
			IndustrySpecAPI spec = ind.getSpec();
			if(!spec.getTags().contains(Industries.HEAVYINDUSTRY))
			{
				continue;
			}

			// check for modded industries that upgrade from an orbital works
			while(spec.getDowngrade() != null && !Industries.ORBITALWORKS.equals(spec.getDowngrade()))
			{
				spec = Global.getSettings().getIndustrySpec(spec.getDowngrade());
			}
			if(spec.getDowngrade() != null)
			{
				return ind.isDisrupted() ? Other.NO_SHIP_INDUSTRY : Other.MOD_INDUSTRY;
			}
		}

		return Other.NO_SHIP_INDUSTRY;
	}
	public static Industry findShipIndustry(MarketAPI market)
	{
		if(market.hasIndustry(Industries.ORBITALWORKS))
		{
			return market.getIndustry(Industries.ORBITALWORKS);
		}
		if(market.hasIndustry(Industries.HEAVYINDUSTRY))
		{
			return market.getIndustry(Industries.HEAVYINDUSTRY);
		}

		for(Industry ind : market.getIndustries())
		{
			IndustrySpecAPI spec = ind.getSpec();
			if(!spec.getTags().contains(Industries.HEAVYINDUSTRY))
			{
				continue;
			}

			// check for modded industries that upgrade from an orbital works
			while(spec.getDowngrade() != null && !Industries.ORBITALWORKS.equals(spec.getDowngrade()))
			{
				spec = Global.getSettings().getIndustrySpec(spec.getDowngrade());
			}
			if(spec.getDowngrade() != null)
			{
				return ind;
			}
		}

		return null;
	}
}
