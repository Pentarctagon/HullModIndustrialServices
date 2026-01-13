package pentarctagon.hmis.dmods;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.PlayerMarketTransaction;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.listeners.ColonyInteractionListener;
import pentarctagon.hmis.Utils;
import pentarctagon.hmis.constants.Luna;
import pentarctagon.hmis.constants.Other;
import pentarctagon.hmis.data.campaign.rulecmd.utils.LunaHelper;

import java.math.BigDecimal;

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
		if(LunaHelper.getBoolean(Luna.HMIS_DECREASE_RESTORATION, true) && market.hasIndustry(Other.HULL_MOD_SERVICES))
		{
			// at ship quality of 150% or greater, restoration doesn't cost more if the ship has multiple dmods
			if(market.getShipQualityFactor() >= 1.5)
			{
				Global.getSettings().setFloat("baseRestoreCostMultPerDMod", 1f);
			}

			BigDecimal adjustedQuality = Utils.getAdjustedQuality(market);

			// if ship quality is over 100%, decrease restoration cost by the amount over 100%
			// ie:
			// default of 1.2 and a ship quality of 150%
			// 1.2 - (1.5 - 1) = 0.7 aka 70% of baseShipHullCost instead of 120%
			//noinspection ComparatorResultComparison
			if(adjustedQuality.compareTo(new BigDecimal(1)) == 1)
			{
				BigDecimal mult = new BigDecimal(Global.getSettings().getFloat("baseRestoreCostMult")).subtract(adjustedQuality.subtract(new BigDecimal(1)));
				if(market.getIndustry(Other.HULL_MOD_SERVICES).isImproved())
				{
					mult = mult.subtract(new BigDecimal("0.2"));
				}
				float fmult = Math.max(mult.floatValue(), LunaHelper.getFloat(Luna.HMIS_DECREASE_RESTORATION_CAP, 0.7f));
				Global.getSettings().setFloat("baseRestoreCostMult", fmult);
			}
		}
	}

	@Override
	public void	reportPlayerClosedMarket(MarketAPI market)
	{
		if(LunaHelper.getBoolean(Luna.HMIS_DECREASE_RESTORATION, true) && market.hasIndustry(Other.HULL_MOD_SERVICES))
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
