package pentarctagon.hmis.npc.smods.hullcheck;

import com.fs.starfarer.api.combat.ShipVariantAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.impl.campaign.ids.HullMods;

public class DefensiveTargetingArrayEvaluator
implements HullModEvaluator
{
	@Override
	public String getId()
	{
		return HullMods.DEFENSIVE_TARGETING_ARRAY;
	}

	@Override
	public boolean evaluate(FleetMemberAPI ship, boolean smod)
	{
		ShipVariantAPI variant = ship.getVariant();
		if(!baseEval(variant, smod))
		{
			return false;
		}
		// has fighters
		if(ship.getHullSpec().getFighterBays() > 0)
		{
			return true;
		}
		return false;
	}
}
