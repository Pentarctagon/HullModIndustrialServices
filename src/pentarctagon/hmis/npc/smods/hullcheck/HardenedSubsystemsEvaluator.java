package pentarctagon.hmis.npc.smods.hullcheck;

import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipVariantAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.impl.campaign.ids.HullMods;

public class HardenedSubsystemsEvaluator
implements HullModEvaluator
{
	@Override
	public String getId()
	{
		return HullMods.HARDENED_SUBSYSTEMS;
	}

	@Override
	public boolean evaluate(FleetMemberAPI ship, boolean smod)
	{
		ShipVariantAPI variant = ship.getVariant();
		if(!baseEval(variant, smod))
		{
			return false;
		}
		if(variant.getHullSize() == ShipAPI.HullSize.FRIGATE || variant.getHullSpec().isPhase() || variant.hasHullMod(HullMods.SAFETYOVERRIDES))
		{
			return true;
		}
		return false;
	}
}
