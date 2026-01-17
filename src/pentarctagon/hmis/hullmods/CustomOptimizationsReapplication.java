package pentarctagon.hmis.hullmods;

import com.fs.starfarer.api.EveryFrameScript;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.ShipVariantAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import pentarctagon.hmis.constants.Luna;
import pentarctagon.hmis.constants.Other;
import pentarctagon.hmis.data.campaign.rulecmd.utils.LunaHelper;

import java.util.Collection;

public class CustomOptimizationsReapplication
implements EveryFrameScript
{
	@Override
	public boolean isDone()
	{
		return false;
	}

	@Override
	public boolean runWhilePaused()
	{
		return true;
	}

	@Override
	public void advance(float amount)
	{
		if(Global.getSector() == null || Global.getSector().getCampaignUI() == null || !LunaHelper.getBoolean(Luna.HMIS_THIRD_SMOD, true))
		{
			return;
		}

		for(FleetMemberAPI ship : Global.getSector().getPlayerFleet().getFleetData().getMembersInPriorityOrder())
		{
			ShipVariantAPI variant = ship.getVariant();
			Collection<String> hullmods = variant.getHullMods();
			if(
				!hullmods.isEmpty() &&
				hullmods.contains(Other.HMIS_CUSTOM_OPTIMIZATIONS) &&
				!hullmods.stream().skip(hullmods.size()-1).findFirst().get().equals(Other.HMIS_CUSTOM_OPTIMIZATIONS)
			)
			{
				variant.removePermaMod(Other.HMIS_CUSTOM_OPTIMIZATIONS);
				variant.addPermaMod(Other.HMIS_CUSTOM_OPTIMIZATIONS, false);
			}
		}
	}
}
