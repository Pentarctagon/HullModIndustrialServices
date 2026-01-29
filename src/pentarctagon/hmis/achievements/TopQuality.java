package pentarctagon.hmis.achievements;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.listeners.EconomyTickListener;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.util.Misc;
import org.magiclib.achievements.MagicAchievement;
import pentarctagon.hmis.Utils;

import java.math.BigDecimal;

public class TopQuality
extends MagicAchievement
implements EconomyTickListener
{
	@Override
	public void reportEconomyTick(int iterIndex)
	{
		if(isComplete())
		{
			return;
		}

		for(MarketAPI market : Misc.getFactionMarkets(Factions.PLAYER))
		{
			if(Utils.getAdjustedQuality(market).compareTo(new BigDecimal("1.6")) != -1)
			{
				completeAchievement();
			}
		}
	}

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
	public void reportEconomyMonthEnd(){}
}
