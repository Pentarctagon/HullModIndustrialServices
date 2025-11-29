package pentarctagon.hmis.achievements;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.PlayerMarketTransaction;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.listeners.ColonyInteractionListener;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import org.magiclib.achievements.MagicAchievement;
import pentarctagon.hmis.data.campaign.rulecmd.utils.Constants;

public class TechnologicalMarvel
extends MagicAchievement
implements ColonyInteractionListener
{
	@Override
	public void onSaveGameLoaded(boolean isComplete)
	{
		super.onSaveGameLoaded(isComplete);
		if(isComplete)
		{
			return;
		}
		Global.getSector().getListenerManager().addListener(this, true);
	}

	@Override
	public void onDestroyed()
	{
		super.onDestroyed();
		Global.getSector().getListenerManager().removeListener(this);
	}

	@Override
	public void	reportPlayerClosedMarket(MarketAPI market)
	{
		if(isComplete())
		{
			return;
		}

		for(FleetMemberAPI data : Global.getSector().getPlayerFleet().getFleetData().getMembersInPriorityOrder())
		{
			if(data.getVariant().getSMods().size() >= Constants.MAX_SAFE_SMODS)
			{
				completeAchievement();
			}
		}
	}

	@Override
	public void reportPlayerMarketTransaction(PlayerMarketTransaction transaction) {}

	@Override
	public void reportPlayerOpenedMarket(MarketAPI market) {}

	@Override
	public void	reportPlayerOpenedMarketAndCargoUpdated(MarketAPI market) {}
}
