package pentarctagon.hmis.dmods;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.PlayerMarketTransaction;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.listeners.ColonyInteractionListener;
import pentarctagon.hmis.HullModIndustrialServices;
import pentarctagon.hmis.data.campaign.rulecmd.utils.Costs;
import pentarctagon.hmis.data.campaign.rulecmd.utils.LunaHelper;
import pentarctagon.hmis.industries.HullModServices;

public class RestorationCostListener
implements ColonyInteractionListener
{
	// formula as of writing:
	// (baseShipHullCost * baseRestoreCostMult) * (baseRestoreCostMultPerDMod^dmodCount)
	private final float defaultMult;
	private final float defaultMultPerDmod;

	public RestorationCostListener()
	{
		defaultMult = Global.getSettings().getFloat("baseRestoreCostMult");
		defaultMultPerDmod = Global.getSettings().getFloat("baseRestoreCostMultPerDMod");
	}

	@Override
	public void reportPlayerOpenedMarket(MarketAPI market)
	{
		if(LunaHelper.getBoolean("hmis_decrease-restoration", true) && market.hasIndustry(HullModServices.ID))
		{
			// at ship quality of 150% or greater, restoration doesn't cost more if the ship has multiple dmods
			if(market.getShipQualityFactor() >= 1.5)
			{
				Global.getSettings().setFloat("baseRestoreCostMultPerDMod", 1f);
			}

			float adjustedQuality = Costs.getAdjustedQuality(market);

			// if ship quality is over 100%, decrease restoration cost by the amount over 100%
			// ie:
			// default of 1.2 and a ship quality of 150%
			// 1.2 - (1.5 - 1) = 0.7 aka 70% of baseShipHullCost instead of 120%
			if(adjustedQuality > 1f)
			{
				float mult = Global.getSettings().getFloat("baseRestoreCostMult") - (adjustedQuality - 1);
				if(market.getIndustry(HullModServices.ID).isImproved())
				{
					mult -= 0.2f;
				}
				mult = Math.max(mult, LunaHelper.getFloat("hmis_decrease-restoration-cap", 0.7f));
				Global.getSettings().setFloat("baseRestoreCostMult", mult);
			}
		}
	}

	@Override
	public void	reportPlayerClosedMarket(MarketAPI market)
	{
		if(LunaHelper.getBoolean("hmis_decrease-restoration", true) && market.hasIndustry(HullModServices.ID))
		{
			Global.getSettings().setFloat("baseRestoreCostMult", defaultMult);
			Global.getSettings().setFloat("baseRestoreCostMultPerDMod", defaultMultPerDmod);
		}
	}

	@Override
	public void reportPlayerMarketTransaction(PlayerMarketTransaction transaction) {}

	@Override
	public void	reportPlayerOpenedMarketAndCargoUpdated(MarketAPI market) {}
}
