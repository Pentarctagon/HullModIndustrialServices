package pentarctagon.hmis.hullmods;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;

@SuppressWarnings("unused")
public class MaintenanceNightmare
extends BaseHullMod
{
	private static final float SUPPLY_USE_MULT = 1.5f;

	@Override
	public void applyEffectsBeforeShipCreation(ShipAPI.HullSize hullSize, MutableShipStatsAPI stats, String id)
	{
		stats.getSuppliesPerMonth().modifyMult(id, SUPPLY_USE_MULT);
	}

	@Override
	public String getDescriptionParam(int index, ShipAPI.HullSize hullSize)
	{
		if(index == 0)
		{
			return (int)((SUPPLY_USE_MULT - 1f) * 100f) + "%";
		}
		return null;
	}
}
