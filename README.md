## Warning ##
I mention a few spoilery things below, so if you haven't played through the game yet, stop reading this and go finish experiencing the vanilla game without mods first. Or don't, up to you, but if anyone complains about spoilers... well, too bad.

## Explanation ##
This mod changes how s-mods work:
* The button to build-in hullmods on the refit screen is removed. Instead, adding s-mods requires building the Hull Mod Services structure, which requires that the Orbital Works industry is already be built and the colony be size 5+. The Hull Mod Services structure will also not function if the colony's stability drops below 7. This structure is (currently) only present on any colony if the player builds it.
* If those conditions are met, then a new option "Manage hull mods" will be added to the main screen of the colony (aka the one that shows up right after you click on the colony, which has the "Open the comm directory" option). Clicking that will open a dialog showing the ships in your fleet. Clicking on a ship will show you a list of the s-mods the ship currently has, any hullmods which can be built in as s-mods, and any hullmods that came with the ship which can be enhanced (ie: Militarized Subsystems on the Kite (A) variant or Energy Bolt Coherer on the various Lion's Guard ship variants).
* Removing an s-mod costs 1 story point.
* Enhancing a hullmod costs 1 story point.
* Adding an s-mod uses the formula `smod_count^2` for the base story point cost and `max(smod_count-2, 0)^2` for the base credit cost. See the table further down for what that calculates out to.
* The cost of adding an s-mod is reduced by the percent ship quality over 100% of the market you're at, capped at 90% cost reduction. Additionally:
  * The Hull Mod Services structure increases ship quality by 20%.
  * Assigning an Alpha core increases ship quality by a further 10%.
  * Improving the Hull Mod Services structure with story points adds a further 20% cost reduction (no effect on ship quality).
* The cost of adding an s-mod is increased if the market's ship quality is below 100%.

## D-Mods ##
The cost of restoring d-mods, which as per the wiki uses the formula `(baseShipHullCost*baseRestoreCostMult) * (baseRestoreCostMultPerDMod^dmodCount)`, is tweaked:
* `baseRestoreCostMult` is reduced by the market's ship quality over 100% capped at 150%. As of 0.98 `baseRestoreCostMult` is 1.2, so it can be reduced to 0.7.
* `baseRestoreCostMultPerDMod` is reduced to 1 if the market's ship quality is 150%+ which means the cost of restoring a ship doesn't increase if it has multiple d-mods. As of 0.98 the default for `baseRestoreCostMultPerDMod` is 1.2.
  These are applied when you open a market (aka click on a planet) and reset back to the default when you close the market, so if you make changes that affect the ship quality value (ie: adding a nanoforge to your Orbital Works) you will need to close and reopen the market to see them applied to the restoration cost.
* A market having ship quality under 100% does not increase d-mod restoration cost.

## Penalty Hullmods ##
* Maintenance Nightmare: added to any ship with 4+ s-mods. Increases maintenance costs by 50%.
* Overburdened: added to any ship with 5-10 s-mods. Decreases nearly all ship stats by 15% per s-mod above 4.
* Parade Piece: added to any ship with 11 s-mods. Decreases nearly all ship stats by 99%.

## Achievements ##
The following achievements are added via MagicLib:
* Technological Marvel - add a 4th s-mod to a ship.
* Pointless Vanity Project - reach the max of 11 s-mods on a ship.

## Why ##
* For vanilla:
  * It always seemed strange to me that s-mods are something you see very rarely on non-player fleets, yet the player can start adding s-mods for a story point each at any market where the faction isn't actively trying to kill you on sight. Story points are also not that hard to get (especially once you know what you're doing) and there's no hard limit to how many you can get.
  * Not being able to remove s-mods when you can remove d-mods felt inconsistent.
  * Not being able to remove s-mods, regardless of the reason, felt like it unnecessarily punishes experimenting with different builds. Sure I can build or buy another of the same type of ship and fit it with slightly different s-mods, but that's not fun, it's just grind.
  * I screwed up multiple ships when I first started playing by putting unremovable s-mods onto ships without really understanding how the game's systems worked or what all the stats really meant or affected.
  * The s-mod limits don't really have any explanation. Out of game, sure, it's obvious it's a balancing thing. I think it'd be better to have that reflected in the gameplay itself.
* For mods, I've played Progressive S-Mods and Ship Mastery System and enjoyed them both, but:
  * I didn't especially like there being a new resource added which needed to be used. It meant that no matter where I was in the game or what resources I had available, if I wanted to try a new type of ship then I needed to grind for XP before I could start adding s-mods to it.
  * The additional power scaling (mostly thinking of Ship Mastery System here) was interesting and also exceptionally overpowered if you min-max it, but also more than I was really looking for.

So, the goals I have for this mod are:
* Use vanilla resources for adding s-mods: credits and story points.
* For practical purposes, don't have a hard s-mod limit. Do what you want, but there are penalties for trying to build too many hullmods into a ship.
* Allow removing s-mods, to make it less punishing to try different builds.
* Have the cost of fixing d-mods be affected by how much you've invested in your colony's industrial capacity. If your faction has the capacity to field fleets of ships of such quality that they rarely have any d-mods after construction, it should be cheaper to fix d-mods as well.
* S-mods shouldn't be immediately available to some random captain with no influence, accomplishments, or money (aka you, the player character). They're powerful upgrades which can significantly improve a ship's effectiveness and as such gaining them should be more of a part of the player's progression through the game.
* Keep this mod focused just on stuff related to s-mods and d-mods.
* Keep this mod vanilla-friendly.

## Compatibility ##
I don't know of any outright incompatibilities, but you could probably get some strange results mixing this with Progressive S-Mods or Ship Mastery System. It also doesn't use the game's max s-mod limit, so while things like the Domain Refurbishment Barge from Random Assortment of Things (RAT) won't break anything they also won't do anything either.

It should be safe to add to an existing save file, but not safe to remove.

## Dependencies ##
* MagicLib: for achievements
* LunaLib: for the usual settings, version checker, etc
* LazyLib: needed by LunaLib

## Credits ##
* A lot of the initial code was taken from the Progressive S-Mods mod which I then modified/refactored/added to as needed, which helped a lot. It would have taken me a lot longer to figure all this out myself from scratch.

## FAQ ##
* Does this mean I need to have a colony to add s-mods to my fleet? - Yes, for now.
* How does this interact with the Best of the Best skill? - The part of that skill that increases your s-mod limit doesn't do anything. Probably don't take that skill for now until I figure out what to do with it.
* Why isn't LunaLib an optional dependency? - Because I get the feeling I'll be adding a bunch more configurable settings in the future and I don't want deal with whether LunaLib is available or not.

## Future Plans ##
* Add this structure to certain Core worlds (ie: Culann, Chicomoztoc, Kazeron).
* Alternate ways to get s-mods - For example, as a reward for consecutive months serving as a commissioned fleet of a faction or a reward from completing missions for high value contacts, you'd be able to add one or more s-mods to your fleet for free at one of that faction's colonies (or a friendly faction's colony which has the Hull Mod Services structure if they have no such colony).
* Figure out a way to handle the ship quality value set in the faction doctrine. Currently it's possible to game it a bit by temporarily setting the quality to max -> adding s-mods/removing d-mods -> setting it back to what it was before.
* Figure out what to do with the Best of the Best skill, ideally in a way that's compatible with Second in Command.
* Potentially related, see about using the game's normal s-mod limit and allowing other modded content to affect the s-mod limit like normal.
* No idea if this is possible or what it would involve, but have s-mods be a "commodity", which would influence how many s-mods you'd see on the ships of other factions during gameplay. Obviously s-mods can't be traded like food or ore, but it'd represent factions paying to send ships to have s-mods added. Potentially very profitable, but also problematic if you become hostile to them and need to fight fleets full of ships which they paid you to have s-mods added to. Probably not a super relevant downside for vanilla but could maybe play well with Nexerelin or something.
* Allow restoring d-mods from `unrestorable` ships (ie: the automated ships guarding the PK device in the Scythe of Orion quest). The disabled automated Onslaught Mk.1 found orbiting some random planet in the Abyss after fighting the Threat for centuries before the Collapse? Sure, you can remove its d-mods. Remnant automated ships? Sure, you can remove their d-mods. Fractal ships piloted by Omega AI cores wielding weapons that defy any attempt to explain how they work? Sure, slot that Reality Disruptor right into any large energy slot. Exotic matter that defies our laws of physics gathered by killing entities from another dimension in the Abyss? Sure, spend a story point, give Engineering a few hours, and you have weapons and hullmods that can be easily equipped to any ship in your fleet. A few human crewed Hegemony XIV ships that got jerry-rigged to be piloted by AI cores during the first AI War? Absolutely mysterious technology, no way we can fix those d-mods.

## Base s-mod costs table ##
Credits are in millions. In the LunaLib settings you can also set a multiplier to apply to the story point and/or credit costs.

| S-mod count | Story Points | Credits |
|-------------|--------------|---------|
| 1           | 1            | 0       |
| 2           | 4            | 0       |
| 3           | 9            | 1       |
| 4           | 16           | 4       |
| 5           | 25           | 9       |
| 6           | 36           | 16      |
| 7           | 49           | 25      |
| 8           | 64           | 36      |
| 9           | 81           | 49      |
| 10          | 100          | 64      |
| 11          | 121          | 81      |