package pentarctagon.hmis.npc.smods.hullcheck;

import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipVariantAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.impl.campaign.ids.HullMods;

import java.util.Map;

public class HeavyArmorEvaluator
implements HullModEvaluator
{
	private static final Map<ShipAPI.HullSize, Integer> armorBySize = Map.of(
		ShipAPI.HullSize.FRIGATE, 150,
		ShipAPI.HullSize.DESTROYER, 300,
		ShipAPI.HullSize.CRUISER, 400,
		ShipAPI.HullSize.CAPITAL_SHIP, 500
	);

	@Override
	public String getId()
	{
		return HullMods.HEAVYARMOR;
	}

	@Override
	public boolean evaluate(FleetMemberAPI ship, boolean smod)
	{
		ShipVariantAPI variant = ship.getVariant();
		if(!baseEval(variant, smod))
		{
			return false;
		}
		// has low enough armor that relatively speaking this adds a lot of armor or has so much armor let's just stack on even more
		float armor = variant.getHullSpec().getArmorRating();
		Integer modEffect = armorBySize.get(variant.getHullSize());
		if(modEffect > armor/2 || armor > 1000)
		{
			return true;
		}
		return false;
	}
}
