package pentarctagon.hmis.npc.smods;

import com.fs.starfarer.api.campaign.*;
import pentarctagon.hmis.data.campaign.rulecmd.utils.LunaHelper;

public class AddSmodsListener
extends BaseCampaignEventListener
{
	public AddSmodsListener()
	{
		super(false);
	}

	@Override
	public void reportFleetSpawned(CampaignFleetAPI fleet)
	{
		if(LunaHelper.getBoolean("hmis_npc-smods", true))
		{
			FleetInflater originalFleetInflater = fleet.getInflater();
			if(!(originalFleetInflater instanceof AddSmodsInflater) && originalFleetInflater != null)
			{
				AddSmodsInflater smodsInflater = new AddSmodsInflater(originalFleetInflater);
				fleet.setInflater(smodsInflater);
			}
		}
	}
}
