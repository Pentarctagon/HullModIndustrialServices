package pentarctagon.hmis.listener;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.PlayerMarketTransaction;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.listeners.ColonyInteractionListener;
import com.fs.starfarer.api.combat.ShipAPI;
import pentarctagon.hmis.Utils;
import pentarctagon.hmis.constants.Luna;
import pentarctagon.hmis.constants.Other;
import pentarctagon.hmis.data.campaign.rulecmd.utils.LunaHelper;

public class SmodsCostListener
implements ColonyInteractionListener
{
	private final float defaultCostFrigate;
	private final float defaultCostDestroyer;
	private final float defaultCostCruiser;
	private final float defaultCostCapital;

	public SmodsCostListener()
	{
		defaultCostFrigate = Global.getSettings().getInt("changeSmodsCost_FRIGATE");
		defaultCostDestroyer = Global.getSettings().getInt("changeSmodsCost_DESTROYER");
		defaultCostCruiser = Global.getSettings().getInt("changeSmodsCost_CRUISER");
		defaultCostCapital = Global.getSettings().getInt("changeSmodsCost_CAPITAL_SHIP");
	}

	@Override
	public void reportPlayerOpenedMarket(MarketAPI market)
	{
		if(LunaHelper.getBoolean(Luna.HMIS_DECREASE_SMOD_COSTS, true) && market.hasIndustry(Other.HULL_MOD_SERVICES))
		{
			Global.getSettings().setFloat("changeSmodsCost_FRIGATE", Utils.setSmodCreditCost(ShipAPI.HullSize.FRIGATE, market));
			Global.getSettings().setFloat("changeSmodsCost_DESTROYER", Utils.setSmodCreditCost(ShipAPI.HullSize.DESTROYER, market));
			Global.getSettings().setFloat("changeSmodsCost_CRUISER", Utils.setSmodCreditCost(ShipAPI.HullSize.CRUISER, market));
			Global.getSettings().setFloat("changeSmodsCost_CAPITAL_SHIP", Utils.setSmodCreditCost(ShipAPI.HullSize.CAPITAL_SHIP, market));
		}
	}

	@Override
	public void	reportPlayerClosedMarket(MarketAPI market)
	{
		if(LunaHelper.getBoolean(Luna.HMIS_DECREASE_SMOD_COSTS, true) && market.hasIndustry(Other.HULL_MOD_SERVICES))
		{
			Global.getSettings().setFloat("changeSmodsCost_FRIGATE", defaultCostFrigate);
			Global.getSettings().setFloat("changeSmodsCost_DESTROYER", defaultCostDestroyer);
			Global.getSettings().setFloat("changeSmodsCost_CRUISER", defaultCostCruiser);
			Global.getSettings().setFloat("changeSmodsCost_CAPITAL_SHIP", defaultCostCapital);
		}
	}

	@Override
	public void reportPlayerMarketTransaction(PlayerMarketTransaction transaction) {}

	@Override
	public void	reportPlayerOpenedMarketAndCargoUpdated(MarketAPI market) {}
}
