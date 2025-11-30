package pentarctagon.hmis.data.campaign.rulecmd.utils;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.combat.ShipVariantAPI;
import lunalib.lunaSettings.LunaSettings;
import pentarctagon.hmis.industries.HullModServices;

public class Costs
{
	/**
	 * To add the Nth s-mod, it costs N^2 story points
	 */
	public static int addSmodStoryPointCost(ShipVariantAPI ship, int added)
	{
		if(added == 0)
		{
			return 0;
		}

		int count = ship.getSMods().size();
		int squared = (int)Math.pow(count+added, 2);

		float modifier = getCostMultiplier();
		Double lunaMultiplier = LunaSettings.getDouble("pentarctagon_HullModIndustrialServices", "hmis_story-point-multiplier");
		lunaMultiplier = lunaMultiplier == null ? 1d : lunaMultiplier;

		// if quality is under 100%, increase cost by the percent difference
		// ie: 75% quality returns -0.25 -> 0.25 -> 1.25 * cost = +25% cost
		// if quality is over 100%, decrease cost by the difference
		// ie: 160% quality returns 0.6 -> -0.4 -> 0.4 * cost = -60% cost
		float calculated;
		if(modifier < 0)
		{
			calculated = (Math.abs(modifier)+1)*squared;
		}
		else
		{
			calculated = Math.abs(modifier-1)*squared;
		}

		return (int)(calculated*lunaMultiplier);
	}

	/**
	 * To add the Nth s-mod, it costs (N-2)^2 million credits, where N can't be less than 0.
	 * Therefore, adding s-mods won't cost any credits until the 3rd s-mod
	 */
	public static int addSmodCreditCost(ShipVariantAPI ship, boolean isEnhanceOnly, int added)
	{
		if(added == 0)
		{
			return 0;
		}

		int count = ship.getSMods().size();
		int base = Math.max(count+added-2, 0);
		int squared = (int)Math.pow(base, 2);
		int cost = squared*1_000_000;
		int enhanceReduction = isEnhanceOnly ? cost/2 : cost;

		float modifier = getCostMultiplier();
		Double lunaMultiplier = LunaSettings.getDouble("pentarctagon_HullModIndustrialServices", "hmis_credits-multiplier");
		lunaMultiplier = lunaMultiplier == null ? 1d : lunaMultiplier;

		// if quality is under 100%, increase cost by the percent difference
		// ie: 75% quality returns -0.25 -> 0.25 -> 1.25 * cost = +25% cost
		// if quality is over 100%, decrease cost by the difference
		// ie: 160% quality returns 0.6 -> -0.4 -> 0.4 * cost = -60% cost
		float calculated;
		if(modifier < 0)
		{
			calculated = (Math.abs(modifier)+1)*enhanceReduction;
		}
		else
		{
			calculated = Math.abs(modifier-1)*enhanceReduction;
		}

		return (int)(calculated*lunaMultiplier);
	}

	/**
	 * Increases cost based on the production quality.
	 * Under 100% increases costs, over 100% decreases costs, by the percent under or over.
	 * Under 100% returns a negative value, over 100% returns a positive value.
	 */
	private static float getCostMultiplier()
	{
		MarketAPI market = Global.getSector().getCampaignUI().getCurrentInteractionDialog().getInteractionTarget().getMarket();
		float productionQuality = market.getShipQualityFactor();
		if(market.getIndustry(HullModServices.ID).isImproved())
		{
			productionQuality += 0.2f;
		}
		float reduced = productionQuality-1;

		// no, this isn't going to be completely free
		if(reduced > 0.9)
		{
			return 0.9f;
		}
		else
		{
			return reduced;
		}
	}

	public static int getPlayerCredits()
	{
		return (int)Global.getSector().getPlayerFleet().getCargo().getCredits().get();
	}
	public static int getPlayerStoryPoints()
	{
		return Global.getSector().getPlayerPerson().getStats().getStoryPoints();
	}
}
