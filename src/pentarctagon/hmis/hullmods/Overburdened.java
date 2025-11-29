package pentarctagon.hmis.hullmods;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipVariantAPI;
import pentarctagon.hmis.data.campaign.rulecmd.utils.Constants;

@SuppressWarnings("unused")
public class Overburdened
extends BaseHullMod
{
	private static final float SHIP_STATS_PENALTY = 0.15f;

	// doesn't seem to be a way to get this directly in the description param method
	// requires the apply method be called before the description param method, which fortunately seems to be the case
	private float penalty = 0f;

	@Override
	public void applyEffectsBeforeShipCreation(ShipAPI.HullSize hullSize, MutableShipStatsAPI stats, String id)
	{
		penalty = getStatPenalty(stats.getVariant());
		// ie: 15% penalty represented by multiplying by 0.85 - stat gets worse as it decreases
		float inverted = 1-penalty;
		// ie: 15% penalty represented by multiplying by 1.15 - stat gets worse as it increases
		float added = 1+penalty;

		// making stat worse by decreasing it
		stats.getAcceleration().modifyMult(id, inverted);
		stats.getAutofireAimAccuracy().modifyMult(id, inverted);
		stats.getBallisticAmmoBonus().modifyMult(id, inverted);
		stats.getBallisticAmmoRegenMult().modifyMult(id, inverted);
		stats.getBaseCRRecoveryRatePercentPerDay().modifyMult(id, inverted);
		stats.getMaxSpeed().modifyMult(id, inverted);
		stats.getDeceleration().modifyMult(id, inverted);
		stats.getTurnAcceleration().modifyMult(id, inverted);
		stats.getPeakCRDuration().modifyMult(id, inverted);
		stats.getEnergyWeaponDamageMult().modifyMult(id, inverted);
		stats.getBallisticWeaponDamageMult().modifyMult(id, inverted);
		stats.getMissileWeaponDamageMult().modifyMult(id, inverted);
		stats.getShieldTurnRateMult().modifyMult(id, inverted);
		stats.getShieldUnfoldRateMult().modifyMult(id, inverted);
		stats.getMissileRoFMult().modifyMult(id, inverted);
		stats.getBallisticRoFMult().modifyMult(id, inverted);
		stats.getEnergyRoFMult().modifyMult(id, inverted);
		stats.getEnergyWeaponRangeBonus().modifyMult(id, inverted);
		stats.getBallisticWeaponRangeBonus().modifyMult(id, inverted);
		stats.getMissileWeaponRangeBonus().modifyMult(id, inverted);
		stats.getWeaponTurnRateBonus().modifyMult(id, inverted);
		stats.getWeaponHealthBonus().modifyMult(id, inverted);
		stats.getEngineHealthBonus().modifyMult(id, inverted);
		stats.getArmorBonus().modifyMult(id, inverted);
		stats.getHullBonus().modifyMult(id, inverted);
		stats.getShieldArcBonus().modifyMult(id, inverted);
		stats.getEnergyAmmoBonus().modifyMult(id, inverted);
		stats.getMissileAmmoBonus().modifyMult(id, inverted);
		stats.getZeroFluxSpeedBoost().modifyMult(id, inverted);
		stats.getVentRateMult().modifyMult(id, inverted);
		stats.getMaxBurnLevel().modifyMult(id, inverted);
		stats.getSensorStrength().modifyMult(id, inverted);
		stats.getFluxDissipation().modifyMult(id, inverted);
		stats.getMaxTurnRate().modifyMult(id, inverted);
		stats.getFluxCapacity().modifyMult(id, inverted);
		stats.getMaxCombatReadiness().modifyMult(id, inverted);
		stats.getHullCombatRepairRatePercentPerSecond().modifyMult(id, inverted);
		stats.getMaxCombatHullRepairFraction().modifyMult(id, inverted);
		stats.getZeroFluxMinimumFluxLevel().modifyMult(id, inverted);
		stats.getHardFluxDissipationFraction().modifyMult(id, inverted);
		stats.getFuelMod().modifyMult(id, inverted);
		stats.getCargoMod().modifyMult(id, inverted);
		stats.getHangarSpaceMod().modifyMult(id, inverted);
		stats.getRepairRatePercentPerDay().modifyMult(id, inverted);

		// making stat worse by increasing it
		stats.getEmpDamageTakenMult().modifyMult(id, added);
		stats.getHullDamageTakenMult().modifyMult(id, added);
		stats.getShieldDamageTakenMult().modifyMult(id, added);
		stats.getEngineDamageTakenMult().modifyMult(id, added);
		stats.getWeaponDamageTakenMult().modifyMult(id, added);
		stats.getEnergyWeaponFluxCostMod().modifyMult(id, added);
		stats.getBallisticWeaponFluxCostMod().modifyMult(id, added);
		stats.getMissileWeaponFluxCostMod().modifyMult(id, added);
		stats.getShieldUpkeepMult().modifyMult(id, added);
		stats.getPhaseCloakActivationCostBonus().modifyMult(id, added);
		stats.getPhaseCloakUpkeepCostBonus().modifyMult(id, added);
		stats.getCombatEngineRepairTimeMult().modifyMult(id, added);
		stats.getCombatWeaponRepairTimeMult().modifyMult(id, added);
		stats.getRecoilPerShotMult().modifyMult(id, added);
		stats.getCRPerDeploymentPercent().modifyMult(id, added);
		stats.getCRLossPerSecondPercent().modifyMult(id, added);
		stats.getShieldAbsorptionMult().modifyMult(id, added);
		stats.getMaxRecoilMult().modifyMult(id, added);
		stats.getRecoilDecayMult().modifyMult(id, added);
		stats.getOverloadTimeMod().modifyMult(id, added);
		stats.getCrewLossMult().modifyMult(id, added);
		stats.getMinCrewMod().modifyMult(id, added);
		stats.getFighterRefitTimeMult().modifyMult(id, added);
		stats.getSensorProfile().modifyMult(id, added);
		stats.getSuppliesToRecover().modifyMult(id, added);
		stats.getPhaseCloakCooldownBonus().modifyMult(id, added);
		stats.getSystemCooldownBonus().modifyMult(id, added);
		stats.getSystemFluxCostBonus().modifyMult(id, added);
		stats.getSystemRegenBonus().modifyMult(id, added);
	}

	@Override
	public String getDescriptionParam(int index, ShipAPI.HullSize hullSize)
	{
		if(index == 0)
		{
			return (int)(penalty*100) + "%";
		}
		return null;
	}

	private float getStatPenalty(ShipVariantAPI ship)
	{
		return (ship.getSMods().size() - Constants.MAX_SAFE_SMODS) * SHIP_STATS_PENALTY;
	}
}
