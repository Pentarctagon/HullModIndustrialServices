package pentarctagon.hmis.hullmods;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.impl.campaign.ids.Stats;
import pentarctagon.hmis.Utils;
import pentarctagon.hmis.constants.Other;
import pentarctagon.hmis.industries.HullModServices;

@SuppressWarnings("unused")
public class CustomOptimizationsHullmod
extends BaseHullMod
{
	@Override
	public void applyEffectsBeforeShipCreation(ShipAPI.HullSize hullSize, MutableShipStatsAPI stats, String id)
	{
		int maxSmodsSetting = Utils.getBaseSmods();
		float bonusSmods = stats.getDynamic().getMod(Stats.MAX_PERMANENT_HULLMODS_MOD).getFlatBonus();
		if(maxSmodsSetting+bonusSmods < Other.MAX_SMODS)
		{
			stats.getDynamic().getMod(Stats.MAX_PERMANENT_HULLMODS_MOD).modifyFlat(Other.HULL_MOD_SERVICES, 1f);
		}
		else
		{
			stats.getDynamic().getMod(Stats.MAX_PERMANENT_HULLMODS_MOD).modifyFlat(Other.HULL_MOD_SERVICES, 0f);
		}
	}

	@Override
	public boolean isApplicableToShip(ShipAPI ship)
	{
		return false;
	}
}
