package pentarctagon.hmis.industries;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.econ.EconomyAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.combat.MutableStat;
import com.fs.starfarer.api.impl.campaign.ids.Industries;
import com.fs.starfarer.api.impl.campaign.ids.Stats;
import pentarctagon.hmis.data.campaign.rulecmd.utils.LunaHelper;

public class QualityDemandConfig
implements EconomyAPI.EconomyUpdateListener
{
	@Override
	public void commodityUpdated(String commodityId)
	{
		if(commodityId.equals("ship_quality"))
		{
			for(MarketAPI market : Global.getSector().getEconomy().getMarketsCopy())
			{
				if(!LunaHelper.getBoolean("hmis_quality-export", true))
				{
					market.getStats().getDynamic().getMod(Stats.PRODUCTION_QUALITY_MOD).unmodify(HullModServices.ID+"_imported");
					continue;
				}

				if(market.hasIndustry(Industries.ORBITALWORKS) || market.hasIndustry(Industries.HEAVYINDUSTRY))
				{
					int maxDemand = market.getCommodityData(commodityId).getMaxDemand();
					int available = market.getCommodityData(commodityId).getAvailable();
					float importedQuality = available*5/100.0f;
					if(maxDemand > 0)
					{
						market.getStats().getDynamic().getMod(Stats.PRODUCTION_QUALITY_MOD).modifyFlat(HullModServices.ID+"_imported", importedQuality, "Hull Mod Services - imported ship quality");
					}
					else
					{
						market.getStats().getDynamic().getMod(Stats.PRODUCTION_QUALITY_MOD).unmodify(HullModServices.ID+"_imported");
					}
				}
			}
		}
	}

	@Override
	public void economyUpdated()
	{
		if(!LunaHelper.getBoolean("hmis_quality-export", true))
		{
			return;
		}

		for(MarketAPI market : Global.getSector().getEconomy().getMarketsCopy())
		{
			if(market.hasIndustry(Industries.ORBITALWORKS) || market.hasIndustry(Industries.HEAVYINDUSTRY))
			{
				// not sure how it makes any sense, but saw some cases of negative ship quality
				float marketQuality = market.getShipQualityFactor() < 0 ? 0 : market.getShipQualityFactor();

				MutableStat.StatMod qualityMod = market.getStats().getDynamic().getMod(Stats.PRODUCTION_QUALITY_MOD).getFlatBonus(HullModServices.ID+"_imported");
				float importedQuality = qualityMod == null ? 0 : qualityMod.getValue();
				float baseQuality = marketQuality - importedQuality;

				// if ship quality is under 100%, add a demand per 5% under 100%
				if(baseQuality < 1)
				{
					// Java's handling of floating point variables is truly bizarre
					int qualityDeficit = (int)Math.abs((baseQuality*100) - 100);
					int demand = Math.min(qualityDeficit / 5, LunaHelper.getInteger("hmis_quality-import-cap", 5));
					if(market.hasIndustry(Industries.ORBITALWORKS))
					{
						market.getIndustry(Industries.ORBITALWORKS).getDemand("ship_quality").getQuantity().modifyFlat(HullModServices.ID, demand);
					}
					else if(market.hasIndustry(Industries.HEAVYINDUSTRY))
					{
						market.getIndustry(Industries.HEAVYINDUSTRY).getDemand("ship_quality").getQuantity().modifyFlat(HullModServices.ID, demand);
					}
				}
				else
				{
					if(market.hasIndustry(Industries.ORBITALWORKS))
					{
						market.getIndustry(Industries.ORBITALWORKS).getDemand("ship_quality").getQuantity().unmodifyFlat(HullModServices.ID);
					}
					else if(market.hasIndustry(Industries.HEAVYINDUSTRY))
					{
						market.getIndustry(Industries.HEAVYINDUSTRY).getDemand("ship_quality").getQuantity().unmodifyFlat(HullModServices.ID);
					}
				}
			}
		}
	}

	@Override
	public boolean isEconomyListenerExpired()
	{
		return false;
	}
}
