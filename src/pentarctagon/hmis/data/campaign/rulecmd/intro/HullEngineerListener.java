package pentarctagon.hmis.data.campaign.rulecmd.intro;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CommDirectoryEntryAPI;
import com.fs.starfarer.api.campaign.PlayerMarketTransaction;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.listeners.ColonyInteractionListener;
import com.fs.starfarer.api.characters.ImportantPeopleAPI;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.impl.campaign.ids.Ranks;
import com.fs.starfarer.api.impl.campaign.ids.Voices;
import pentarctagon.hmis.constants.Other;

import java.util.List;

public class HullEngineerListener
implements ColonyInteractionListener
{
	@Override
	public void reportPlayerOpenedMarket(MarketAPI market)
	{
		List<CommDirectoryEntryAPI> entries = market.getCommDirectory().getEntriesCopy();
		PersonAPI engineer = null;
		for(CommDirectoryEntryAPI entry : entries)
		{
			if(entry.getEntryData() instanceof PersonAPI person)
			{
				if(person.getMemory().getBoolean(Other.HMIS_ENGINEER))
				{
					engineer = person;
				}
			}
		}

		// if the market has a hull engineer and shouldn't anymore, remove them
		if(!market.isPlayerOwned() || !market.hasIndustry(Other.HULL_MOD_SERVICES) || market.getIndustry(Other.HULL_MOD_SERVICES).isBuilding())
		{
			if(engineer != null)
			{
				ImportantPeopleAPI ip = Global.getSector().getImportantPeople();
				ip.removePerson(engineer);
				market.removePerson(engineer);
				market.getCommDirectory().removePerson(engineer);
			}
		}
		// else if it should have a hull engineer and doesn't, add one
		else
		{
			if(engineer == null)
			{
				ImportantPeopleAPI ip = Global.getSector().getImportantPeople();
				engineer = Global.getSector().getFaction(Factions.PLAYER).createRandomPerson();

				engineer.setRankId(Ranks.CITIZEN);
				engineer.setPostId("hmis_hull_engineer");
				engineer.setVoice(Voices.SPACER);
				engineer.getMemory().set(Other.HMIS_ENGINEER, true);

				market.getCommDirectory().addPerson(engineer);
				market.addPerson(engineer);

				ip.addPerson(engineer);
				ip.getData(engineer).getLocation().setMarket(market);
			}
		}
	}

	@Override
	public void reportPlayerClosedMarket(MarketAPI market){}

	@Override
	public void reportPlayerOpenedMarketAndCargoUpdated(MarketAPI market){}

	@Override
	public void reportPlayerMarketTransaction(PlayerMarketTransaction transaction){}
}
