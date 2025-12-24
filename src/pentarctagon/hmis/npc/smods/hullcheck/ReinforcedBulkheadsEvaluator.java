package pentarctagon.hmis.npc.smods.hullcheck;

import com.fs.starfarer.api.combat.ShipVariantAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.impl.campaign.ids.HullMods;

public class ReinforcedBulkheadsEvaluator
implements HullModEvaluator
{
	@Override
	public String getId()
	{
		return HullMods.REINFORCEDHULL;
	}

	@Override
	public boolean evaluate(FleetMemberAPI ship, boolean smod)
	{
		ShipVariantAPI variant = ship.getVariant();
		if(!baseEval(variant, smod))
		{
			return false;
		}
		// has enough hull for the 40% bonus to be meaningful
		if(variant.getHullSpec().getHitpoints() > 5000)
		{
			return true;
		}
		return false;
	}
}
