package pentarctagon.hmis.data.campaign.rulecmd.intro;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.LocationAPI;
import com.fs.starfarer.api.campaign.PlanetAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.listeners.EconomyTickListener;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.impl.campaign.ids.Industries;
import pentarctagon.hmis.constants.Other;

public class InAlphaSystemListener
implements EconomyTickListener
{
	@Override
	public void reportEconomyTick(int iterIndex)
	{
		LocationAPI loc = Global.getSector().getCurrentLocation();
		if(!loc.getPlanets().isEmpty())
		{
			for(PlanetAPI planet : loc.getPlanets())
			{
				if(planet.getFaction().getId().equals(Factions.PLAYER))
				{
					dialog(planet.getMarket());
				}
			}
		}
	}

	private boolean checkFirstRequest(MarketAPI market)
	{
		MemoryAPI memory = market.getMemory();
		return !memory.contains(Other.ALPHA_FIRST_REQUEST) && memory.contains(Other.ALPHA_MONTH_COUNTER) && memory.getInt(Other.ALPHA_MONTH_COUNTER) > 0;
	}

	private void dialog(MarketAPI market)
	{
		if(
			market.hasIndustry(Industries.ORBITALWORKS) &&
			market.hasIndustry(Other.HULL_MOD_SERVICES) &&
			Commodities.ALPHA_CORE.equals(market.getIndustry(Other.HULL_MOD_SERVICES).getAICoreId()) &&
			market.getPeopleCopy().stream().anyMatch(person -> person.getMemory().contains(Other.HULL_ENGINEER))
		)
		{
			// check if a new choice needs to be made
			if(checkFirstRequest(market))
			{
				//Global.getSector().getCampaignUI().showInteractionDialog(new AlphaRequestDialog(market), null);
			}
		}
	}

	@Override
	public void reportEconomyMonthEnd(){}
}
