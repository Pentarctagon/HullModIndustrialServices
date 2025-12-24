package pentarctagon.hmis.npc.smods.hullcheck;

import com.fs.starfarer.api.combat.ShipVariantAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.impl.campaign.ids.HullMods;

public class AdvancedOpticsEvaluator
implements HullModEvaluator
{
	@Override
	public String getId()
	{
		return HullMods.ADVANCEDOPTICS;
	}

	@Override
	public boolean evaluate(FleetMemberAPI ship, boolean smod)
	{
		ShipVariantAPI variant = ship.getVariant();
		if(!baseEval(variant, smod))
		{
			return false;
		}
		// has enough beam weapons for the range increase to be relevant
		long beam = variant.getFittedWeaponSlots().stream().filter(slot -> variant.getWeaponSpec(slot).isBeam()).count();
		if(beam >= 4)
		{
			return true;
		}
		return false;
	}
}
