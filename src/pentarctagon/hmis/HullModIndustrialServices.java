package pentarctagon.hmis;

import com.fs.starfarer.api.BaseModPlugin;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.ModSpecAPI;
import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.impl.campaign.ids.*;
import com.fs.starfarer.api.loading.HullModSpecAPI;
import com.fs.starfarer.api.util.Misc;
import org.apache.log4j.Logger;
import pentarctagon.hmis.constants.Other;
import pentarctagon.hmis.data.campaign.rulecmd.intro.HullEngineerListener;
import pentarctagon.hmis.dmods.RestorationCostListener;
import pentarctagon.hmis.doctrine.listener.PlayerFactionShipQuality;
import pentarctagon.hmis.hullmods.CustomOptimizationsReapplication;
import pentarctagon.hmis.industries.HullModServicesOptions;
import pentarctagon.hmis.industries.QualityDemandConfig;
import pentarctagon.hmis.npc.smods.AddSmodsListener;
import pentarctagon.hmis.npc.smods.listener.UpdatePlayerBlueprints;

import java.lang.invoke.MethodHandles;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

// TODO: add/remove s-mods dialog
//  align pricing to match the costs from the "Change s-mods" vanilla functionality
//  add functionality to have the vanilla "Change s-mods" pricing also be affected by colony ship quality

// TODO: allowing restoration of unrestorable ships - no change

// TODO: AI core integration/de-integration - no change

// TODO: d-mod restoration cost - unsure
//  either reduce the number of DGPs is costs or if possible remove this and instead reduce the cost of purchasing DGPs at this colony based on ship quality

// TODO: NPC s-mods - review if any hullmod updates change when/whether any hullmods should be built-it, otherwise no change

// TODO: increase s-mod limit - remove, don't like it anyway as power creep since it can be used to replace best of the best

// TODO: exporting ship quality - no change

// TODO: XIV upgrade package - review DGP cost (should probably be pretty high), otherwise implement as planned

// TODO: DGP production - remove, already happens in vanilla

// TODO: deconstruction module - unsure
//  don't know yet how to modify its supply value and unsure if it matters since they seem to stockpile pretty fast already
//  maybe instead have a small number added to player storage at the colony each month
@SuppressWarnings("unused")
public class HullModIndustrialServices
extends BaseModPlugin
{
	private static final Logger log = Logger.getLogger(MethodHandles.lookup().lookupClass());

	public static final HashMap<String, List<String>> vanillaHullmods = new HashMap<>();
	public static final Set<String> neverBuildIn = Set.of(
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
		HullMods.SAFETYOVERRIDES,
		// rarely ever actually worth it
		HullMods.CONVERTED_HANGAR,
		// reduces crew loss, which NPC fleets don't care about
		HullMods.RECOVERY_SHUTTLES,
		// commented out in HullMods for some reason
		// I doubt the AI would actually make use of this effectively on its own
		"escort_package",
		// no purpose for NPC fleets, but might show up in known hullmods list for Player faction
		HullMods.NEURAL_INTEGRATOR,
		HullMods.NEURAL_INTERFACE,
		// not in HullMods for some reason
		// not sure this is relevant for NPC fleets
		"hiressensors"
	);

	@Override
	public void beforeGameSave()
	{
		// save to faction memory
		MemoryAPI memory = Global.getSector().getFaction(Factions.PLAYER).getMemory();
		memory.set("$hmis_last_tick_quality", PlayerFactionShipQuality.getQualityOnLastTick().toString());
		memory.set("$hmis_last_tick_timestamp", PlayerFactionShipQuality.getLastTimestamp());
	}

	@Override
	public void onGameLoad(boolean newGame)
	{
		Global.getSector().getListenerManager().addListener(new RestorationCostListener(), true);
		Global.getSector().getListenerManager().addListener(new HullEngineerListener(), true);
		Global.getSector().addTransientListener(new AddSmodsListener());
		Global.getSector().getListenerManager().addListener(new UpdatePlayerBlueprints(), true);
		Global.getSector().getListenerManager().addListener(new PlayerFactionShipQuality(), true);
		Global.getSector().getEconomy().addUpdateListener(new QualityDemandConfig());
		Global.getSector().addTransientScript(new CustomOptimizationsReapplication());
		Global.getSector().getListenerManager().addListener(new HullModServicesOptions(), true);

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

		synchronized(HullModIndustrialServices.class)
		{
			for(FactionAPI faction : Global.getSector().getAllFactions())
			{
				populateFactionHullmods(faction.getId());
			}
		}
	}

	public static void populateFactionHullmods(String faction)
	{
		vanillaHullmods.put(
			faction,
			Global.getSector()
				.getFaction(faction)
				.getKnownHullMods()
				.stream()
				.filter(id -> {
					HullModSpecAPI spec = Global.getSettings().getHullModSpec(id);
					ModSpecAPI source = spec.getSourceMod();

					// ignore:
					// 0 cost hullmods
					// non-smoddable hullmods
					// hullmods that significantly alter how a ship needs to be built
					// non-vanilla hullmods
					return spec.getFrigateCost() > 0 &&
						!spec.hasTag("no_build_in") &&
						!neverBuildIn.contains(id) &&
						(source == null || source.getId() == null || source.getId().isEmpty() || source.getId().equals("starsector"));
				})
				.collect(Collectors.toList())
		);
	}
}
