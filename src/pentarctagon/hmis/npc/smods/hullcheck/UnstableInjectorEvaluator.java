package pentarctagon.hmis.npc.smods.hullcheck;

import com.fs.starfarer.api.combat.ShipVariantAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.impl.campaign.ids.HullMods;

public class UnstableInjectorEvaluator
implements HullModEvaluator
{
	@Override
	public String getId()
	{
		return HullMods.UNSTABLE_INJECTOR;
	}

	@Override
	public boolean evaluate(FleetMemberAPI ship, boolean smod)
	{
		ShipVariantAPI variant = ship.getVariant();
		if(!baseEval(variant, smod))
		{
			return false;
		}
		// has no fighters
		if(ship.getHullSpec().getFighterBays() > 0)
		{
			return false;
		}
		return true;
	}
}
