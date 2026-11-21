package pentarctagon.hmis;

import com.fs.starfarer.api.BaseModPlugin;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.impl.campaign.ids.*;
import com.fs.starfarer.api.util.Misc;
import org.apache.log4j.Logger;
import pentarctagon.hmis.constants.Other;
import pentarctagon.hmis.data.campaign.rulecmd.intro.HullEngineerListener;
import pentarctagon.hmis.doctrine.listener.PlayerFactionShipQuality;
import pentarctagon.hmis.industries.HullModServicesOptions;
import pentarctagon.hmis.industries.QualityDemandConfig;
import pentarctagon.hmis.items.DeconstructionModuleMonthly;
import pentarctagon.hmis.listener.SmodsCostListener;
import pentarctagon.hmis.npc.smods.AddSmodsListener;
import pentarctagon.hmis.npc.smods.HullCheckManager;
import pentarctagon.hmis.npc.smods.hullcheck.*;
import pentarctagon.hmis.npc.smods.listener.UpdatePlayerBlueprints;

import java.lang.invoke.MethodHandles;

// TODO: require player to be at the colony when creating a deconstruction module

// TODO later: probably need to adjust the Sindrian dialog depending on the usurpers quest outcome
// TODO later: watch NPC fleets spawning and check that the s-mod adding functionality is still working for modded faction fleets
@SuppressWarnings("unused")
public class HullModIndustrialServices
extends BaseModPlugin
{
	private static final Logger log = Logger.getLogger(MethodHandles.lookup().lookupClass());

	@Override
	public void beforeGameSave()
	{
		// save to faction memory
		MemoryAPI memory = Global.getSector().getFaction(Factions.PLAYER).getMemory();
		memory.set(Other.HMIS_FACTION_QUALITY_LAST_TICK, PlayerFactionShipQuality.getQualityOnLastTick().toString());
		memory.set(Other.HMIS_FACTION_QUALITY_LAST_TICK_TIMESTAMP, PlayerFactionShipQuality.getLastTimestamp());
	}

	@Override
	public void onGameLoad(boolean newGame)
	{
		Global.getSector().getListenerManager().addListener(new SmodsCostListener(), true);
		Global.getSector().getListenerManager().addListener(new HullEngineerListener(), true);
		Global.getSector().addTransientListener(new AddSmodsListener());
		Global.getSector().getListenerManager().addListener(new UpdatePlayerBlueprints(), true);
		Global.getSector().getListenerManager().addListener(new PlayerFactionShipQuality(), true);
		Global.getSector().getEconomy().addUpdateListener(new QualityDemandConfig());
		Global.getSector().getListenerManager().addListener(new HullModServicesOptions(), true);
		Global.getSector().getListenerManager().addListener(new DeconstructionModuleMonthly(), true);

		Misc.getFactionMarkets(Factions.HEGEMONY)
		    .stream()
		    .filter(market -> (market.getId().equals("chicomoztoc") || market.getId().equals("raesvelg")) && !market.hasIndustry(Other.HULL_MOD_SERVICES))
		    .findFirst()
		    .ifPresent(market -> market.addIndustry(Other.HULL_MOD_SERVICES));

		Misc.getFactionMarkets(Factions.PERSEAN)
		    .stream()
		    .filter(market -> market.getId().equals("kazeron") && !market.hasIndustry(Other.HULL_MOD_SERVICES))
		    .findFirst()
		    .ifPresent(market -> market.addIndustry(Other.HULL_MOD_SERVICES));

		Misc.getFactionMarkets(Factions.TRITACHYON)
		    .stream()
		    .filter(market -> market.getId().equals("culann"))
		    .findFirst()
		    .ifPresent(market -> {
				if(!market.hasIndustry(Other.HULL_MOD_SERVICES))
				{
					market.addIndustry(Other.HULL_MOD_SERVICES);
				}
				if(market.getIndustry(Other.HULL_MOD_SERVICES).getAICoreId() == null)
				{
					market.getIndustry(Other.HULL_MOD_SERVICES).setAICoreId(Commodities.ALPHA_CORE);
				}
		    });

		Misc.getFactionMarkets(Factions.DIKTAT)
		    .stream()
		    .filter(market -> market.getId().equals("sindria") && !market.hasIndustry(Other.HULL_MOD_SERVICES))
		    .findFirst()
		    .ifPresent(market -> market.addIndustry(Other.HULL_MOD_SERVICES));

		/*
		 * the idea is that, for adding additional s-mods, it's fairly easy to determine if these are always or almost always just a straight upgrade
		 * ie: adding s-modded accelerated shields to a ship that didn't previously have any s-mods is just an improvement
		 *     it doesn't evaluate whether a particular hullmod is an optimal choice, just that it's better than not having it at all
		 */
		HullCheckManager.addEvaluators(
			new HardenedShieldsEvaluator(),
			new AcceleratedShieldsEvaluator(),
			new ExtendedShieldsEvaluator(),
			new ShieldConversionFrontEvaluator(),
			new StabilizedShieldsEvaluator(),
			new ArmoredWeaponMountsEvaluator(),
			new AutomatedRepairUnitEvaluator(),
			new HeavyArmorEvaluator(),
			new ReinforcedBulkheadsEvaluator(),
			new ResistantFluxConduitsEvaluator(),
			new AdvancedOpticsEvaluator(),
			new AdvancedTurretGyrosEvaluator(),
			new ECCMPackageEvaluator(),
			new ExpandedMagazinesEvaluator(),
			new ExpandedMissileRacksEvaluator(),
			new IntegratedPointDefenseEvaluator(),
			new MissileAutoloaderEvaluator(),
			new AuxiliaryThrustersEvaluator(),
			new UnstableInjectorEvaluator(),
			new DefensiveTargetingArrayEvaluator(),
			new ExpandedDeckCrewEvaluator(),
			new FluxCoilAdjunctEvaluator(),
			new FluxDistributorEvaluator(),
			new HardenedSubsystemsEvaluator(),
			new AdaptivePhaseCoilsEvaluator(),
			new InsulatedEngineAssemblyEvaluator(),
			new PhaseAnchorEvaluator(),
			new ECMPackageEvaluator(),
			new NavRelayEvaluator(),
			new EfficiencyOverhaulEvaluator(),
			new SolarShieldingEvaluator()
		);

		HullCheckManager.addNeverBuildIn(
			// just adding shield shunt without respeccing the rest of the ship almost always just makes it worse
			HullMods.SHIELD_SHUNT,
			// logistics hullmods than NPC fleets don't benefit from
			HullMods.ADDITIONAL_BERTHING,
			HullMods.AUGMENTEDENGINES,
			HullMods.AUXILIARY_FUEL_TANKS,
			HullMods.CONVERTED_BAY,
			HullMods.EXPANDED_CARGO_HOLDS,
			HullMods.MILITARIZED_SUBSYSTEMS,
			HullMods.SURVEYING_EQUIPMENT,
			// NPC fleets don't use command points
			HullMods.OPERATIONS_CENTER,
			// NPC fleets don't care about crew losses
			HullMods.BLAST_DOORS,
			// reducing the range of beam weapons that much while remaining effective is very niche
			HullMods.HIGH_SCATTER_AMP,
			// very weapon dependent and I don't want to deal with the complexity
			HullMods.BALLISTIC_RANGEFINDER,
			// assume most ships will have one or the other already, no real benefit to s-modding
			HullMods.DEDICATED_TARGETING_CORE,
			HullMods.INTEGRATED_TARGETING_UNIT,
			// no active venting and severely reduced PPT requires a very aggressive build/fleet
			// also not normally allowed to be built in anyway
			HullMods.SAFETYOVERRIDES,
			// rarely ever actually worth it
			HullMods.CONVERTED_HANGAR,
			// reduces crew loss, which NPC fleets don't care about
			HullMods.RECOVERY_SHUTTLES,
			// I doubt the AI would actually make use of this effectively on its own
			HullMods.ESCORT_PACKAGE,
			// no purpose for NPC fleets, but might show up in known hullmods list for Player faction
			HullMods.NEURAL_INTEGRATOR,
			HullMods.NEURAL_INTERFACE,
			// not sure this is relevant for NPC fleets
			HullMods.HIRESSENSORS,
			// shield is terrible so probably just makes the ship worse by itself
			HullMods.MAKESHIFT_GENERATOR
		);

		HullCheckManager.addIgnoredFaction(Factions.OMEGA, Factions.THREAT, Factions.DWELLER);
		HullCheckManager.populateAllFactionsHullmods();
	}
}
