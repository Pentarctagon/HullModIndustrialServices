package pentarctagon.hmis;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.econ.Industry;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipVariantAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.impl.campaign.ids.Industries;
import com.fs.starfarer.api.impl.campaign.ids.Stats;
import com.fs.starfarer.api.loading.IndustrySpecAPI;
import pentarctagon.hmis.constants.Luna;
import pentarctagon.hmis.constants.Other;
import pentarctagon.hmis.data.campaign.rulecmd.utils.LunaHelper;
import pentarctagon.hmis.doctrine.listener.PlayerFactionShipQuality;

import java.math.BigDecimal;

public class Utils
{
	/**
	 * added == true  -> adding an s-mod
	 * added == false -> removing s-mod or enhancing default hullmod
	 */
	public static int getSmodCreditCost(ShipVariantAPI ship, boolean enhancedRemoved)
	{
		BigDecimal qualityModifier = getCostMultiplier();

		int cost;
		if(enhancedRemoved)
		{
			cost = shipSizeEnhanceRemoveCost(ship);
		}
		else
		{
			cost = shipSizeSmodCost(ship);
		}

		double lunaMultiplier = LunaHelper.getDouble(Luna.HMIS_CREDITS_MULTIPLIER, 1d);

		return qualityModifier.multiply(new BigDecimal(cost)).multiply(new BigDecimal(lunaMultiplier)).intValue();
	}

	/**
	 * Increases cost based on the production quality.
	 * Under 100% increases costs, over 100% decreases costs, by the percent under or over.
	 * Under 100% returns a negative value, over 100% returns a positive value.
	 */
	private static BigDecimal getCostMultiplier()
	{
		MarketAPI market = getCurrentMarket();
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

	private static int shipSizeSmodCost(ShipVariantAPI ship)
	{
		ShipAPI.HullSize size = ship.getHullSize();
		if(size == ShipAPI.HullSize.FRIGATE)
		{
			return 100_000;
		}
		if(size == ShipAPI.HullSize.DESTROYER)
		{
			return 200_000;
		}
		if(size == ShipAPI.HullSize.CRUISER)
		{
			return 300_000;
		}
		if(size == ShipAPI.HullSize.CAPITAL_SHIP)
		{
			return 500_000;
		}
		return 100_000;
	}

	private static int shipSizeEnhanceRemoveCost(ShipVariantAPI ship)
	{
		ShipAPI.HullSize size = ship.getHullSize();
		if(size == ShipAPI.HullSize.FRIGATE)
		{
			return 100_000;
		}
		if(size == ShipAPI.HullSize.DESTROYER)
		{
			return 115_000;
		}
		if(size == ShipAPI.HullSize.CRUISER)
		{
			return 130_000;
		}
		if(size == ShipAPI.HullSize.CAPITAL_SHIP)
		{
			return 150_000;
		}
		return 100_000;
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
