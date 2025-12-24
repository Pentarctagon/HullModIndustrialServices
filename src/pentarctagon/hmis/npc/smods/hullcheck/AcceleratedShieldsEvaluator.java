package pentarctagon.hmis.npc.smods.hullcheck;

import com.fs.starfarer.api.combat.ShieldAPI;
import com.fs.starfarer.api.combat.ShipVariantAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.impl.campaign.ids.HullMods;

public class AcceleratedShieldsEvaluator
implements HullModEvaluator
{
	@Override
	public String getId()
	{
		return HullMods.ACCELERATED_SHIELDS;
	}

	@Override
	public boolean evaluate(FleetMemberAPI ship, boolean smod)
	{
		ShipVariantAPI variant = ship.getVariant();
		if(!baseEval(variant, smod))
		{
			return false;
		}
		// has no shield or shield was removed
		if(variant.getHullSpec().getShieldType() == ShieldAPI.ShieldType.NONE || variant.hasHullMod(HullMods.SHIELD_SHUNT) || ship.isPhaseShip())
		{
			return false;
		}
		return true;
	}
}
