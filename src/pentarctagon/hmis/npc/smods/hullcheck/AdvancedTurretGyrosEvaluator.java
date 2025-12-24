package pentarctagon.hmis.npc.smods.hullcheck;

import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipVariantAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.impl.campaign.ids.HullMods;

public class AdvancedTurretGyrosEvaluator
implements HullModEvaluator
{
	@Override
	public String getId()
	{
		return HullMods.TURRETGYROS;
	}

	@Override
	public boolean evaluate(FleetMemberAPI ship, boolean smod)
	{
		ShipVariantAPI variant = ship.getVariant();
		if(!baseEval(variant, smod))
		{
			return false;
		}
		// is large enough to give a meaningful damage bonus
		if(variant.getHullSize() == ShipAPI.HullSize.CAPITAL_SHIP || variant.getHullSize() == ShipAPI.HullSize.CRUISER)
		{
			return true;
		}
		return false;
	}
}
