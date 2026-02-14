package pentarctagon.hmis.data.campaign.rulecmd.intro;

import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.listeners.EconomyTickListener;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.impl.campaign.ids.Industries;
import com.fs.starfarer.api.util.Misc;
import pentarctagon.hmis.constants.Other;

public class HullModServicesAlphaListener
implements EconomyTickListener
{
	@Override
	public void reportEconomyTick(int iterIndex){}

	@Override
	public void reportEconomyMonthEnd()
	{
		for(MarketAPI market : Misc.getFactionMarkets(Factions.PLAYER))
		{
			if(
				market.hasIndustry(Industries.ORBITALWORKS) &&
				market.hasIndustry(Other.HULL_MOD_SERVICES) &&
				Commodities.ALPHA_CORE.equals(market.getIndustry(Other.HULL_MOD_SERVICES).getAICoreId())
			)
			{
				MemoryAPI memory = market.getMemory();
				if(memory.contains(Other.ALPHA_MONTH_COUNTER))
				{
					// counter should only increment between choices
					// otherwise mutliple requests will be shown at once
					if(!memory.getBoolean(Other.ALPHA_FIRST_REQUEST) && memory.getInt(Other.ALPHA_MONTH_COUNTER) < 1)
					{
						memory.set(Other.ALPHA_MONTH_COUNTER, memory.getInt(Other.ALPHA_MONTH_COUNTER) + 1);
					}
				}
				else
				{
					memory.set(Other.ALPHA_MONTH_COUNTER, 0);
				}
			}
		}
	}
}
