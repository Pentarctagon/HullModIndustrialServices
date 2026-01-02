package pentarctagon.hmis.npc.smods.listener;

import com.fs.starfarer.api.campaign.listeners.EconomyTickListener;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import pentarctagon.hmis.HullModIndustrialServices;

public class UpdatePlayerBlueprints
implements EconomyTickListener
{
	@Override
	public void reportEconomyTick(int iterIndex){}

	@Override
	public void reportEconomyMonthEnd()
	{
		// I assume it's technically possible for the inflator to trigger at the same moment as the month ends
		synchronized(HullModIndustrialServices.class)
		{
			HullModIndustrialServices.vanillaHullmods.get(Factions.PLAYER).clear();
			HullModIndustrialServices.populateFactionHullmods(Factions.PLAYER);
		}
	}
}
