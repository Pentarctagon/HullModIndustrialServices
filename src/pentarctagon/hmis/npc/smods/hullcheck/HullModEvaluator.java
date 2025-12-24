package pentarctagon.hmis.npc.smods.hullcheck;

import com.fs.starfarer.api.combat.ShipVariantAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;

public interface HullModEvaluator
{
	String getId();
	boolean evaluate(FleetMemberAPI ship, boolean smod);
	default boolean baseEval(ShipVariantAPI variant, boolean smod)
	{
		// s-mod == true:
		//   doesn't have it - continue
		//   has it as regular mod - continue
		//   has it s-modded - return
		if(smod && (variant.hasHullMod(getId()) || variant.getSMods().contains(getId()) || variant.getSModdedBuiltIns().contains(getId()) || variant.getPermaMods().contains(getId())))
		{
			return false;
		}
		// s-mod == false:
		//   doesn't have it - continue
		//   has it as regular mod - return
		//   has it s-modded - return
		if(!smod && (variant.hasHullMod(getId()) || variant.getSMods().contains(getId())))
		{
			return false;
		}
		return true;
	}
}
