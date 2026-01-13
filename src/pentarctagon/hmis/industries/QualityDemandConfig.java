package pentarctagon.hmis.industries;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.econ.EconomyAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.combat.MutableStat;
import com.fs.starfarer.api.impl.campaign.ids.Industries;
import com.fs.starfarer.api.impl.campaign.ids.Stats;
import pentarctagon.hmis.constants.Luna;
import pentarctagon.hmis.constants.Other;
import pentarctagon.hmis.data.campaign.rulecmd.utils.LunaHelper;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class QualityDemandConfig
implements EconomyAPI.EconomyUpdateListener
{
	@Override
	public void commodityUpdated(String commodityId)
	{
		if(commodityId.equals(Other.SHIP_QUALITY))
		{
			for(MarketAPI market : Global.getSector().getEconomy().getMarketsCopy())
			{
				if(!LunaHelper.getBoolean(Luna.HMIS_QUALITY_EXPORT, true))
				{
					market.getStats().getDynamic().getMod(Stats.PRODUCTION_QUALITY_MOD).unmodify(Other.HULL_MOD_SERVICES+"_imported");
					continue;
				}

				if(market.hasIndustry(Industries.ORBITALWORKS) || market.hasIndustry(Industries.HEAVYINDUSTRY))
				{
					int maxDemand = market.getCommodityData(commodityId).getMaxDemand();
					if(maxDemand > 0)
					{
						int available = market.getCommodityData(commodityId).getAvailable();
						float importedQuality = new BigDecimal(available).multiply(new BigDecimal(5)).divide(new BigDecimal(100), 4, RoundingMode.HALF_UP).floatValue();
						market.getStats().getDynamic().getMod(Stats.PRODUCTION_QUALITY_MOD).modifyFlat(Other.HULL_MOD_SERVICES+"_imported", importedQuality, "Hull Mod Services - imported ship quality");
					}
					else
					{
						market.getStats().getDynamic().getMod(Stats.PRODUCTION_QUALITY_MOD).unmodify(Other.HULL_MOD_SERVICES+"_imported");
					}
				}
			}
		}
	}

	@Override
	public void economyUpdated()
	{
		if(!LunaHelper.getBoolean(Luna.HMIS_QUALITY_EXPORT, true))
		{
			return;
		}

		for(MarketAPI market : Global.getSector().getEconomy().getMarketsCopy())
		{
			if(market.hasIndustry(Industries.ORBITALWORKS) || market.hasIndustry(Industries.HEAVYINDUSTRY))
			{
				// not sure how it makes any sense, but saw some cases of negative ship quality
				BigDecimal marketQuality = market.getShipQualityFactor() < 0 ? new BigDecimal(0) : new BigDecimal(market.getShipQualityFactor());

				MutableStat.StatMod qualityMod = market.getStats().getDynamic().getMod(Stats.PRODUCTION_QUALITY_MOD).getFlatBonus(Other.HULL_MOD_SERVICES+"_imported");
				BigDecimal importedQuality = qualityMod == null ? new BigDecimal(0) : new BigDecimal(qualityMod.getValue());
				BigDecimal baseQuality = marketQuality.subtract(importedQuality);

				// if ship quality is under 100%, add a demand per 5% under 100%
				//noinspection ComparatorResultComparison
				if(baseQuality.compareTo(new BigDecimal(1)) == -1)
				{
					int qualityDeficit = baseQuality.multiply(new BigDecimal(100)).subtract(new BigDecimal(100)).abs().intValue();
					int demand = Math.min(qualityDeficit / 5, LunaHelper.getInteger(Luna.HMIS_QUALITY_IMPORT_CAP, 5));
					if(market.hasIndustry(Industries.ORBITALWORKS))
					{
						market.getIndustry(Industries.ORBITALWORKS).getDemand(Other.SHIP_QUALITY).getQuantity().modifyFlat(Other.HULL_MOD_SERVICES, demand);
					}
					else if(market.hasIndustry(Industries.HEAVYINDUSTRY))
					{
						market.getIndustry(Industries.HEAVYINDUSTRY).getDemand(Other.SHIP_QUALITY).getQuantity().modifyFlat(Other.HULL_MOD_SERVICES, demand);
					}
				}
				else
				{
					if(market.hasIndustry(Industries.ORBITALWORKS))
					{
						market.getIndustry(Industries.ORBITALWORKS).getDemand(Other.SHIP_QUALITY).getQuantity().unmodifyFlat(Other.HULL_MOD_SERVICES);
					}
					else if(market.hasIndustry(Industries.HEAVYINDUSTRY))
					{
						market.getIndustry(Industries.HEAVYINDUSTRY).getDemand(Other.SHIP_QUALITY).getQuantity().unmodifyFlat(Other.HULL_MOD_SERVICES);
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
