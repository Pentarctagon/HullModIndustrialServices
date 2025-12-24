package pentarctagon.hmis.npc.smods.hullcheck;

import com.fs.starfarer.api.combat.ShipVariantAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.impl.campaign.ids.HullMods;

public class PhaseAnchorEvaluator
implements HullModEvaluator
{
	@Override
	public String getId()
	{
		return HullMods.PHASE_ANCHOR;
	}

	@Override
	public boolean evaluate(FleetMemberAPI ship, boolean smod)
	{
		ShipVariantAPI variant = ship.getVariant();
		if(baseEval(variant, smod))
		{
			return false;
		}
		// can't be s-modded
		if(smod)
		{
			return false;
		}
		return ship.isPhaseShip();
	}
}
