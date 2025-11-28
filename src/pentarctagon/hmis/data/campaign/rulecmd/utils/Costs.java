package pentarctagon.hmis.data.campaign.rulecmd.utils;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.ShipVariantAPI;

public class Costs
{
	/**
	 * To add the Nth s-mod, it costs N^2 story points
	 */
	public static int addSmodStoryPointCost(ShipVariantAPI ship)
	{
		int count = ship.getSMods().size();
		return (int)Math.pow(count+1, 2);
	}

	/**
	 * To add the Nth s-mod, it costs (N-2)^2 million credits, where N can't be less than 0.
	 * Therefore, adding s-mods won't cost any credits until the 3rd s-mod
	 */
	public static int addSmodCreditCost(ShipVariantAPI ship, boolean isEnhanceOnly)
	{
		int count = ship.getSMods().size();
		int base = Math.max(count-1, 0);
		double squared = Math.pow(base, 2);
		int cost = (int)squared*1_000_000;
		return isEnhanceOnly ? cost/2 : cost;
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
