## Explanation ##
This mod adds the Hull Mod Services colony structure which requires a size 5+ colony and an Orbital Works in order to be built. It also won't function if the colony's stability drops below 7. A colony with this structure built and functional will have a new "Manage hull mods" option added to the main colony menu. From there you can do the following (all using credits):
* Add s-mods to ships in your fleet.
* Remove s-mods from ships.
* Enhance built-in hullmods (ie: Militarized Subsystems on the Kite (A) variant or Energy Bolt Coherer on the various Lion's Guard ship variants).

The cost of doing these actions is reduced by the ship quality percentage of the colony over 100%. So if the colony has 125% ship quality, it reduces the cost by 25%. The cost will also increase if the colony's ship quality drops below 100%.

**NOTE:** to avoid gaming the system, the ship quality from the faction doctrine is always considered to be 25%, regardless of what it's actually set to. This isn't a good solution, but I'm not sure how to implement something less confusing.

Additionally:
* At 150% ship quality, the option to make unrestorable ships (ie: the automated ships guarding the PK device in the Scythe of Orion quest) restorable for 500k credits becomes available.
* At 150% ship quality and with the Tri-Tachyon deal in place post colony crisis, the option to integrate an AI core for 50k credits or de-integrate an AI core for 150k credits becomes available.

The structure itself has the following effects:
* +20% ship quality
* Assigning an alpha core gives another +10% ship quality
* Improving the structure with story points reduces s-mod related costs as well as ship restoration costs by 20% (does not affect ship quality)

The following core worlds have this structure added:
* Chicomoztoc
* Kazeron
* Culann (with an Alpha Core)
Making use of this structure on these worlds requires either having 50+ reputation with the respective faction or currently being commissioned by them.

### Hint ###
For otherwise vanilla games, you can get to 150% ship quality by:
* 25% - hard coded doctrine bonus
* 25% - 10 stability
* 50% - Pristine Nanoforge
* 20% - Orbital Works
* 20% - Hull Mod Services
* 10% - Alpha core installed in Hull Mod Services

## D-Mods ##
The cost of restoring d-mods, which as per the wiki uses the formula `(baseShipHullCost*baseRestoreCostMult) * (baseRestoreCostMultPerDMod^dmodCount)`, is tweaked for colonies with the new structure:
* `baseRestoreCostMult` is reduced by the colony's ship quality over 100% capped at 150%. As of 0.98 `baseRestoreCostMult` is 1.2, so it can be reduced to 0.7.
* `baseRestoreCostMultPerDMod` is reduced to 1 if the colony's ship quality is 150%+ which means the cost of restoring a ship doesn't increase if it has multiple d-mods. As of 0.98 the default for `baseRestoreCostMultPerDMod` is 1.2.
  These are applied when you open a colony (aka click on a planet) and reset back to the default when you close the colony, so if you make changes that affect the ship quality value (ie: adding a nanoforge to your Orbital Works) you will need to close and reopen the colony to see them applied to the restoration cost.
* A colony having ship quality under 100% does not increase d-mod restoration cost.

## NPC s-mods ##
This causes non-civilian ships in NPC fleets to have s-mods based on their faction's ship quality. This includes fleets spawned by your colonies. The formula is +1 s-mod per 20% ship quality over 100%, with each additional percent adding a 5% chance of getting an extra s-mod. For example, with just this mod added, Tri-Tachyon will have a ship quality of 145%, so each of their ships will be guaranteed 2 s-mods and a 25% chance of getting a third s-mod. 

The hullmods chosen to be s-mods will first prioritize hullmods that are already on the ship by default. Then, if there aren't enough such hullmods, randomly add s-mods from the list of the faction's known hullmods based on whether they'd potentially be useful for the ship. For example, adding Adaptive Phase Coils to phase ships, Expanded Crew Deck if it has fighter bays, etc. For the sake of simplicity, only vanilla hullmods will be considered and only the subset of those that aren't purely logistical (ie: Expanded Cargo Holds) or are usually detrimental without building around them (ie: shield shunt). This does not mean that the s-mods or regular hullmods that are added are the most optimal possible choice, just that they provide some sort of benefit without being detrimental.

After that, the ordnance points that have been freed up by s-modding hullmods that were already on the ship will be used to add regular hullmods from the same subset of vanilla hullmods. Any ordnance points left over after that will be added first to vents, or if the maximum number of vents have been reached, then added to capacitors. This does mean that there might still be ordnance points left over, but it should only be a few at most and I don't want to make this any more complicated.

Increasing the s-mod limit in the Luna settings beyond 3 will only affect modded factions. For example, UAF spec ops fleets have a quality of 200%, and so depending on the s-mod cap may have up to 5 s-mods. This can also be disabled in the Luna settings. Either way, this setting should only affect s-mods that would have been added by this mod and not s-mods that get added as part of the base game or by other mods.

## Increase s-mod limit ##
At markets where you have access to a Hull Mod Services structure and the market's ship quality is at least 100%, there is an option to pay credits based on ship size (500,000/750,000/1,000,000/1,500,000) to allow building in an additional s-mod. This does not stack with the s-mod gained from the Best of the Best skill.

This can be disabled via a Luna setting.

## Compatibility ##
No compatibility issues that I know of.

## Dependencies ##
* LunaLib (optional): for the usual settings, version checker, etc

## Credits ##
* A lot of the initial code was taken from the Progressive S-Mods mod which I then modified/refactored/added to as needed, which helped a lot. It would have taken me a lot longer to figure all this out myself from scratch, especially the UI code.

## Future Plans ##
* Figure out a better way to handle the ship quality value set in the faction doctrine.
