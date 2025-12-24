package pentarctagon.hmis.npc.smods.hullcheck;

import com.fs.starfarer.api.combat.ShipVariantAPI;
import com.fs.starfarer.api.combat.WeaponAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.impl.campaign.ids.HullMods;

public class ExpandedMagazinesEvaluator
implements HullModEvaluator
{
	@Override
	public String getId()
	{
		return HullMods.MAGAZINES;
	}

	@Override
	public boolean evaluate(FleetMemberAPI ship, boolean smod)
	{
		ShipVariantAPI variant = ship.getVariant();
		if(!baseEval(variant, smod))
		{
			return false;
		}
		// has non-missile weapons with regenerating ammo
		long charges = 0;
		for(String slot : variant.getFittedWeaponSlots())
		{
			if(variant.getWeaponSpec(slot).getType() != WeaponAPI.WeaponType.MISSILE &&
				variant.getWeaponSpec(slot).getReloadSize() > 0 &&
				variant.getWeaponSpec(slot).usesAmmo()
			)
			{
				charges++;
			}
		}
		if(charges >= 1)
		{
			return true;
		}
		return false;
	}
}
