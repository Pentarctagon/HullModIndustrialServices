package pentarctagon.hmis.npc.smods.hullcheck;

import com.fs.starfarer.api.combat.ShipVariantAPI;
import com.fs.starfarer.api.combat.WeaponAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.impl.campaign.ids.HullMods;

public class ArmoredWeaponMountsEvaluator
implements HullModEvaluator
{
	@Override
	public String getId()
	{
		return HullMods.ARMOREDWEAPONS;
	}

	@Override
	public boolean evaluate(FleetMemberAPI ship, boolean smod)
	{
		ShipVariantAPI variant = ship.getVariant();
		if(!baseEval(variant, smod))
		{
			return false;
		}
		// has enough armor for the 10% bonus to be meaningful
		if(variant.getHullSpec().getArmorRating() > 750)
		{
			return true;
		}
		// has enough non-missile weapon slots for the fire rate increase to be meaningful
		long nonMissile = variant.getFittedWeaponSlots().stream().filter(slot -> variant.getWeaponSpec(slot).getType() != WeaponAPI.WeaponType.MISSILE).count();
		if(nonMissile / ((float)variant.getFittedWeaponSlots().size()) >= 0.75f)
		{
			return true;
		}
		return false;
	}
}
