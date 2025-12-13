package pentarctagon.hmis.data.campaign.rulecmd.utils;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipVariantAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.impl.campaign.ids.Stats;
import pentarctagon.hmis.industries.HullModServices;

public class Costs
{
	/**
	 * added == true  -> adding an s-mod
	 * added == false -> removing s-mod or enhancing default hullmod
	 */
	public static int getSmodCreditCost(ShipVariantAPI ship, boolean enhancedRemoved)
	{
		float qualityModifier = getCostMultiplier();

		int cost;
		if(enhancedRemoved)
		{
			cost = shipSizeEnhanceRemoveCost(ship);
		}
		else
		{
			cost = shipSizeSmodCost(ship);
		}

		double lunaMultiplier = LunaHelper.getDouble("hmis_credits-multiplier", 1d);

		return (int)(cost*qualityModifier*lunaMultiplier);
	}

	/**
	 * Increases cost based on the production quality.
	 * Under 100% increases costs, over 100% decreases costs, by the percent under or over.
	 * Under 100% returns a negative value, over 100% returns a positive value.
	 */
	private static float getCostMultiplier()
	{
		MarketAPI market = getPlayerMarket();
		float adjustedQuality = getAdjustedQuality(market);

		if(market.getIndustry(HullModServices.ID).isImproved())
		{
			adjustedQuality += 0.2f;
		}
		float reduced = Math.abs(adjustedQuality-2);

		// no, this isn't going to be completely free
		if(reduced < 0.1)
		{
			return 0.1f;
		}
		else
		{
			return reduced;
		}
	}

	/**
	 * can game the system by a significant margin by adjusting the ship quality in the player's faction doctrine
	 * so while not a great solution, just ignore the actual value and pretend it's the middle setting (+25%)
	 * ideal solution would be to have changes to faction doctrine ship quality fade in over time rather than taking effect instantly
	 * but no idea if that's possible at all, how to convey that on the UI, etc
	 */
	public static float getAdjustedQuality(MarketAPI market)
	{
		float doctrineQuality = Global.getSector().getPlayerFaction().getProduction().getFaction().getDoctrine().getShipQualityContribution();
		return market.getShipQualityFactor() - doctrineQuality + 0.25f;
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
	public static MarketAPI getPlayerMarket()
	{
		return Global.getSector().getCampaignUI().getCurrentInteractionDialog().getInteractionTarget().getMarket();
	}

	public static int getBaseSmods()
	{
		return Global.getSettings().getInt("maxPermanentHullmods");
	}
	public static int getBonusSmods(FleetMemberAPI ship)
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
}
