package pentarctagon.hmis.npc.smods;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.ModSpecAPI;
import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.loading.HullModSpecAPI;
import pentarctagon.hmis.HullModIndustrialServices;
import pentarctagon.hmis.npc.smods.hullcheck.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * This class is for managing the process of adding additional s-mods to fleets that otherwise wouldn't have them (or wouldn't have as many).<br>
 * This has a few parts:<br>
 * * The hullmods each faction knows - the player faction's known blueprints are updated at month end.<br>
 * * The hullmods that should never be built in, even if the game allows it.<br>
 * * An evaluator class per hullmod that determines if the hullmod is helpful on a particular ship.<br>
 * * A list of factions that should be ignored entirely.<br>
 * <br>
 * With that setup, when a new fleet is spawned, it will get no additional s-mods if any of the following are true:<br>
 * * It's part of a faction that's set as ignored.<br>
 * * Its faction doesn't know any hullmods with an evaluator setup.<br>
 * * Faction ship quality at the colony it spawned at is too low.<br>
 * * Adding s-mods has been disabled in the Luna settings.<br>
 * <br>
 * If a fleet is eligible to have s-mods added, then the list of hullmod evaluators is randomized and the ships updated in this order:<br>
 * * Any non-s-mod hullmod that a ship has which has an s-mod effect, has a non-zero ordnance cost, and isn't excluded from being built-in is made an s-mod.<br>
 * * If there are available s-mod slots remaining, use the evaluators to fill the remaining s-mod slots.<br>
 * * Since s-modding hullmods that the ship already had is done first, this means there are ordnance points that have been freed up, so use the evaluators to determine regular hullmods to add to use the ordnance points.<br>
 * * It's unlikely that adding hullmods will always exactly use the remaining ordnance points, so add any remaining ordnance points first to vents, then if vents are maxed add to capacitors.<br>
 */
public class HullCheckManager
{
	private static final ArrayList<HullModEvaluator> hullModEvaluators = new ArrayList<>();
	private static final ArrayList<String> evaluatorIds = new ArrayList<>();
	static List<HullModEvaluator> getEvaluators()
	{
		return hullModEvaluators;
	}

	/**
	 * Add evaluators used to determine additional hullmods to add, either s-modded or non-s-modded.
	 */
	public static synchronized void addEvaluators(HullModEvaluator... evaluators)
	{
		for(HullModEvaluator eval : evaluators)
		{
			hullModEvaluators.add(eval);
			evaluatorIds.add(eval.getId());
		}
	}

	private static final HashSet<String> neverBuildIn = new HashSet<>();
	static boolean isNeverBuildIn(String id)
	{
		return neverBuildIn.contains(id);
	}

	/**
	 * Provide a list of hullmod IDs that should never be s-modded even if the hullmod is already present on the ship.
	 * These hullmods should be added before calling populateFactionHullmods() or populateAllFactionsHullmods().
	 */
	public static void addNeverBuildIn(String... ids)
	{
		neverBuildIn.addAll(Arrays.asList(ids));
	}

	private static final HashMap<String, List<String>> factionHullmods = new HashMap<>();
	/**
	 * Repopulates the list of hullmods available to s-mod for the provided faction.
	 * Ignores 0 cost hullmods, hullmods with the no_build_in tag, and hullmods explicitly set to never be built in via addNeverBuildIn().
	 * @param faction The faction id.
	 */
	public static void populateFactionHullmods(String faction)
	{
		synchronized(HullModIndustrialServices.class)
		{
			factionHullmods.put(
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
						      // hullmods that significantly alter how a ship needs to be built or don't make sense on NPC fleets
						      // non-vanilla hullmods
						      return spec.getFrigateCost() > 0 &&
								      !spec.hasTag("no_build_in") &&
								      !neverBuildIn.contains(id);
					      })
					      .collect(Collectors.toList())
			);
		}
	}
	/**
	 * Repopulates the list of hullmods available to s-mod for all factions.
	 */
	public static void populateAllFactionsHullmods()
	{
		for(FactionAPI faction : Global.getSector().getAllFactions())
		{
			HullCheckManager.populateFactionHullmods(faction.getId());
		}
	}
	static boolean factionKnowsEvaluatableHullmods(String id)
	{
		// in case they update at the exact same time this is being evaluated
		synchronized(HullModIndustrialServices.class)
		{
			return Collections.disjoint(evaluatorIds, factionHullmods.get(id));
		}
	}
	static boolean factionHasHullmod(String factionId, String hullmodId)
	{
		synchronized(HullModIndustrialServices.class)
		{
			return factionHullmods.get(factionId).contains(hullmodId);
		}
	}

	private static final HashSet<String> ignoreFactions = new HashSet<>();
	public static void addIgnoredFaction(String... ids)
	{
		ignoreFactions.addAll(Arrays.asList(ids));
	}

	/**
	 * Whether the provided faction ID is ignored when determining whether to add s-mods to the fleet.
	 */
	public static boolean isIgnoredFaction(String id)
	{
		return ignoreFactions.contains(id);
	}
}
