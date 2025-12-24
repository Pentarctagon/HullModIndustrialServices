package pentarctagon.hmis.npc.smods.hullcheck;

import com.fs.starfarer.api.combat.ShipVariantAPI;
import com.fs.starfarer.api.combat.WeaponAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.impl.campaign.ids.HullMods;

public class ECCMPackageEvaluator
implements HullModEvaluator
{
	@Override
	public String getId()
	{
		return HullMods.ECCM;
	}

	@Override
	public boolean evaluate(FleetMemberAPI ship, boolean smod)
	{
		ShipVariantAPI variant = ship.getVariant();
		if(!baseEval(variant, smod))
		{
			return false;
		}
		// has missiles
		long missile = variant.getFittedWeaponSlots().stream().filter(slot -> variant.getWeaponSpec(slot).getType() == WeaponAPI.WeaponType.MISSILE).count();
		if(missile > 0)
		{
			return true;
		}
		return false;
	}
}
