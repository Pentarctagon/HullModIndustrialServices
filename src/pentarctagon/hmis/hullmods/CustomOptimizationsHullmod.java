package pentarctagon.hmis.hullmods;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.impl.campaign.ids.Stats;
import pentarctagon.hmis.data.campaign.rulecmd.utils.Costs;
import pentarctagon.hmis.industries.HullModServices;

@SuppressWarnings("unused")
public class CustomOptimizationsHullmod
extends BaseHullMod
{
	@Override
	public void applyEffectsBeforeShipCreation(ShipAPI.HullSize hullSize, MutableShipStatsAPI stats, String id)
	{
		int maxSmodsSetting = Costs.getBaseSmods();
		if(maxSmodsSetting < HullModServices.MAX_SMODS)
		{
			stats.getDynamic().getMod(Stats.MAX_PERMANENT_HULLMODS_MOD).modifyFlat(HullModServices.ID, 1f);
		}
		else
		{
			stats.getDynamic().getMod(Stats.MAX_PERMANENT_HULLMODS_MOD).modifyFlat(HullModServices.ID, 0f);
		}
	}

	@Override
	public boolean isApplicableToShip(ShipAPI ship)
	{
		return false;
	}
}
