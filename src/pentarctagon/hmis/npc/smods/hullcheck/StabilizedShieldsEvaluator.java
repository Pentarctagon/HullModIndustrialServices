package pentarctagon.hmis.npc.smods.hullcheck;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.ShieldAPI;
import com.fs.starfarer.api.combat.ShipVariantAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.impl.campaign.ids.HullMods;

public class StabilizedShieldsEvaluator
implements HullModEvaluator
{
	@Override
	public String getId()
	{
		return HullMods.STABILIZEDSHIELDEMITTER;
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
		// at least as efficient as adding vents
		// ie: 3 OP to halve shield upkeep for frigate with 100 upkeep, so:
		//     3*10 = 30
		//     100/2 = 50
		//     3 vents = +30 dissipation
		//     halved upkeep saves 50 flux generation
		//     therefore, it's worthwhile
		int ordnance = Global.getSettings().getHullModSpec(getId()).getCostFor(ship.getVariant().getHullSize());
		float upkeep = ship.getVariant().getHullSpec().getShieldSpec().getUpkeepCost();
		if(ordnance*10 > upkeep/2)
		{
			return false;
		}
		return true;
	}
}
