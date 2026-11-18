## Explanation ##
This mod adds the Hull Mod Services colony structure which requires a size 5+ colony and an Orbital Works in order to be built. It also won't function if the colony's stability drops below 7. A colony with this structure built and functional will have a new "Manage hull mods" option added to the main colony menu. From there you can do the following (all using credits):
* Add s-mods to ships in your fleet.
* Remove s-mods from ships.
* Enhance built-in hullmods (ie: Militarized Subsystems on the Kite (A) variant or Energy Bolt Coherer on the various Lion's Guard ship variants).

The cost of doing these actions is reduced by the ship quality percentage of the colony over 100%. So if the colony has 125% ship quality, it reduces the cost by 25%. The cost will also increase if the colony's ship quality drops below 100%.

This cost adjustment also applies to the credits costs in the vanilla "Change s-mods" dialog in the refit screen. The "Change s-mods" dialog does mostly replace the need for this functionality with the exception of adding new s-mods with credits - the "Change s-mods" dialog only allows changing and removing s-mods with credits.

**NOTE:** to avoid gaming the system, the ship quality from the faction doctrine does not immediately take effect when considering d-mod restoration costs or adding/removing s-mods. Instead, it adjusts by 2.5% approximately every week towards the new value. So if the faction ship quality doctrine is changed from 0% to 25%, it will take about 10 weeks for that 25% ship quality to be used for s-mod and d-mod cost reductions.

Additionally:
* At 150% ship quality, the option to make unrestorable ships (ie: the automated ships guarding the PK device in the Scythe of Orion quest) restorable for 500k credits becomes available.
* At 150% ship quality and with the Tri-Tachyon deal in place post colony crisis, the option to integrate an AI core for 50k credits or de-integrate an AI core for 150k credits becomes available.

The structure itself has the following effects:
* +20% ship quality
* Assigning an alpha core gives another +10% ship quality
* Improving the structure with story points reduces s-mod related costs by 20% (does not affect ship quality)

The following core worlds have this structure added:
* Chicomoztoc
* Raesvelg
* Kazeron
* Culann (with an Alpha Core)
* Sindria

Making use of this structure on these worlds requires either having 50+ reputation with the respective faction or currently being commissioned by them.

### Hint ###
For otherwise vanilla games, there are the following sources of faction ship quality:
* 25% - 10 stability
* 50% - Pristine Nanoforge (20% for corrupted nanoforge)
* 20% - Orbital Works
* 20% - Hull Mod Services
* 10% - Alpha core installed in Hull Mod Services

This adds up to 125%, with the faction ship quality doctrine ranging from 0% to 50%.

## NPC s-mods ##
This causes non-civilian ships in NPC fleets to have s-mods based on their faction's ship quality. This includes fleets spawned by your colonies. The formula is +1 s-mod per 20% ship quality over 100%, with each additional percent adding a 5% chance of getting an extra s-mod. For example, with just this mod added, Tri-Tachyon will have a ship quality of 145%, so each of their ships will be guaranteed 2 s-mods and a 25% chance of getting a third s-mod. 

The hullmods chosen to be s-mods will first prioritize hullmods that are already on the ship by default. Then, if there aren't enough such hullmods, randomly add s-mods from the list of the faction's known hullmods based on whether they'd potentially be useful for the ship. For example, adding Adaptive Phase Coils to phase ships, Expanded Crew Deck if it has fighter bays, etc. For the sake of simplicity, only vanilla hullmods will be considered and only the subset of those that aren't purely logistical (ie: Expanded Cargo Holds) or are usually detrimental without building around them (ie: Shield Shunt). This does not mean that the s-mods or regular hullmods that are added are the most optimal possible choice, just that they provide some sort of benefit without being detrimental.

After that, the ordnance points that have been freed up by s-modding hullmods that were already on the ship will be used to add regular hullmods from the same subset of vanilla hullmods. Any ordnance points left over after that will be added first to vents, or if the maximum number of vents have been reached, then added to capacitors. This does mean that there might still be ordnance points left over, but it should only be a few at most and I don't want to make this any more complicated.

Increasing the s-mod limit in the Luna settings beyond 3 will only affect modded factions. For example, at least when I wrote this, UAF spec ops fleets have a quality of 200%, and so depending on the s-mod cap may have up to 5 s-mods. This can also be disabled in the Luna settings. Either way, this setting should only affect s-mods that would have been added by this mod and not s-mods that get added as part of the base game or by other mods.

## Importing/Exporting certified domain-grade parts ##
Markets that have a Heavy Industry or Orbital Works and under 100% ship quality will "import" one unit per 5% ship quality under 100%, with demand capped at 5 (+25% ship quality). This cap can be changed in the Luna settings, but is in place to prevent perpetual shortages (since there are colonies that would need way more than 5 units).

Non-player markets that have the Hull Mod Services structure and have at least 110% ship quality will export one unit per 10% ship quality above 100%.

Player markets will export under the same conditions, with the one additional requirement that the player have defeated either the Persean League blockade or all three Hegemony AI inspections. Since as per the description of normal domain-grade parts, bearing a faction seal is what makes them valuable (aka why you buy them for 10k credits but can only sell them for 250 credits per unit), and defeating either of these crises is how you prove you're now firmly established a relevant polity in the sector.

This can be disabled via a Luna setting. The max demand can be adjusted via a Luna setting, but increasing it will tend to just create perpetual shortages and inflate the value unless there are additional sources of ship quality available from other mods.

## XIV upgrade package ##
At one of your own colonies where you have a Hull Mod Services structure, you can upgrade any base hull into its XIV variant (ie: Dominator -> XIV Dominator) in exchange for 100/200/300/400 domain grade parts.

## Deconstruction module ##
After encountering the Threat and when having a Pristine Nanoforge installed in the colony's Orbital Works, an option is available by clicking on the Hull Mod Services structure to create the Deconstruction Module colony item. This costs 25 fragment fabricators and 10 threat processing units.

Once created, installing it in the Hull Mod Services structure requires the colony to have the Heavy Batteries structure as well as at least a tier two orbital station (any type). When installed it provides another option that lets you choose how many units of ship production to sacrifice in exchange for the same number of units of domain grade parts being added to the colony's storage at the end of each month. This only starts after the first full month. This also decreases the colony's base ground defense value by 10 is draws a lot of Pather interest.

This can be disabled via the Luna settings.

NOTE: units of ship production affect more than how much money the colony makes by exporting them:
* Each unit of ship production counts for 25,000 credits worth of custom production capacity. So if you have six units of ship production and decide to spend two units on domain grade parts, your custom production capacity will drop from 150,000 credits to 100,000 credits.
* Faction-wide ship quality is taken from the colony with the highest ship production, not the colony with the highest quality. If you have a colony that produces 6 units of ships and has 120% quality and another colony that produces 4 units of ships at 140% quality, all your other colonies will get their quality bonuses from the colony with 120% quality rather than the colony with 140% quality.

## Compatibility ##
No compatibility issues that I know of.

## Dependencies ##
* LunaLib (optional): for the usual settings, version checker, etc.
* MagicLib (required): for achievements.

## Credits ##
* A lot of the initial code was taken from the Progressive S-Mods mod which I then modified/refactored/added to as needed, which helped a lot. It would have taken me a lot longer to figure all this out myself from scratch, especially the UI code.

## AI Discloser ##
No AI was used to make this mod (in case the reused/recolored icons didn't give it away). I'm sure this could all be done a lot faster and easier with a good AI agent, but I'm doing this as a hobby and having an agent do most/all of the work seems like it'd make it a pretty boring hobby.
