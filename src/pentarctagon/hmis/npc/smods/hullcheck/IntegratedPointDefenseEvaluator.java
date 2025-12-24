package pentarctagon.hmis.npc.smods.hullcheck;

import com.fs.starfarer.api.combat.ShipVariantAPI;
import com.fs.starfarer.api.combat.WeaponAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.impl.campaign.ids.HullMods;

public class IntegratedPointDefenseEvaluator
implements HullModEvaluator
{
	@Override
	public String getId()
	{
		return HullMods.POINTDEFENSEAI;
	}

	@Override
	public boolean evaluate(FleetMemberAPI ship, boolean smod)
	{
		ShipVariantAPI variant = ship.getVariant();
		if(!baseEval(variant, smod))
		{
			return false;
		}
		// s-mod bonus is pretty niche
		if(smod)
		{
			return false;
		}
		// has a PD weapon
		for(String slot : variant.getFittedWeaponSlots())
		{
			if(variant.getWeaponSpec(slot).getAIHints().contains(WeaponAPI.AIHints.PD))
			{
				return true;
			}
		}
		return false;
	}
}
