package pentarctagon.hmis.items;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.econ.Industry;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.listeners.EconomyTickListener;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.impl.campaign.ids.Submarkets;
import pentarctagon.hmis.constants.Other;
import pentarctagon.hmis.industries.HullModServices;

public class DeconstructionModuleMonthly
implements EconomyTickListener
{
	@Override
	public void reportEconomyTick(int iterIndex){}

	@Override
	public void reportEconomyMonthEnd()
	{
		for(MarketAPI market : Global.getSector().getEconomy().getMarketsCopy())
		{
			if(!Factions.PLAYER.equals(market.getFaction().getId()))
			{
				continue;
			}

			HullModServices hmis = (HullModServices) market.getIndustry(Other.HULL_MOD_SERVICES);
			if(hmis != null)
			{
				if(hmis.getSpecialItem() != null && Other.HMIS_DECONSTRUCTION_MODULE.equals(hmis.getSpecialItem().getId()))
				{
					if(market.getMemory().getBoolean(Other.HMIS_DECONSTRUCTION_MODULE_ACTIVE_LAST_MONTH))
					{
						market.getMemory().set(Other.HMIS_DECONSTRUCTION_MODULE_ACTIVE_THIS_MONTH, true);
					}
					market.getMemory().set(Other.HMIS_DECONSTRUCTION_MODULE_ACTIVE_LAST_MONTH, true);
				}
				else
				{
					market.getMemory().set(Other.HMIS_DECONSTRUCTION_MODULE_ACTIVE_THIS_MONTH, false);
					market.getMemory().set(Other.HMIS_DECONSTRUCTION_MODULE_ACTIVE_LAST_MONTH, false);
				}

				if(market.getMemory().getBoolean(Other.HMIS_DECONSTRUCTION_MODULE_ACTIVE_THIS_MONTH))
				{
					market.getSubmarket(Submarkets.SUBMARKET_STORAGE).getCargo().addCommodity(Commodities.PARTS, hmis.getDomainGradePartsProduction());
				}
			}
			else
			{
				market.getMemory().unset(Other.HMIS_DECONSTRUCTION_MODULE_ACTIVE_THIS_MONTH);
				market.getMemory().unset(Other.HMIS_DECONSTRUCTION_MODULE_ACTIVE_LAST_MONTH);
			}
		}
	}
}
