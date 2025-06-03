package net.cibernet.alchemancy.datagen;

import net.cibernet.alchemancy.Alchemancy;
import net.cibernet.alchemancy.client.data.CodexEntryReloadListenener;
import net.cibernet.alchemancy.properties.Property;
import net.cibernet.alchemancy.registries.AlchemancyProperties;
import net.cibernet.alchemancy.util.PropertyFunction;
import net.minecraft.core.Holder;
import net.minecraft.data.PackOutput;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.common.data.LanguageProvider;

import java.util.ArrayList;

public class AlchemancyLangProvider extends LanguageProvider {

	public AlchemancyLangProvider(PackOutput output) {
		super(output, Alchemancy.MODID, "en_us");
	}

	@Override
	protected void addTranslations() {
		addCodexFlavor(AlchemancyProperties.BURNING, "Turning up the heat");
		addCodexFunction(AlchemancyProperties.BURNING, PropertyFunction.ON_ATTACK, "Sets the target on fire for 5 seconds. Duration is multiplied by the item's level of {enchantment minecraft:fire_aspect}.");
		addCodexFunction(AlchemancyProperties.BURNING, PropertyFunction.WHILE_WORN, "Constantly sets the user on fire every half a second.");
		addCodexFunction(AlchemancyProperties.BURNING, PropertyFunction.WHILE_ROOTED, "Sets entities standing inside of the item on fire for 4 seconds. Duration is multiplied by the item's level of {enchantment minecraft:fire_aspect}.");
		addCodexFunction(AlchemancyProperties.BURNING, PropertyFunction.WHEN_SHOT, "Sets the shot projectile on fire for 4 seconds.");

		addCodexFlavor(AlchemancyProperties.WET, "Soak it in");
		addCodexFunction(AlchemancyProperties.WET, PropertyFunction.ON_ATTACK, "Reduces the target's time on fire by half a second.");
		addCodexFunction(AlchemancyProperties.WET, PropertyFunction.WHILE_WORN, "Reduces the user's amount of time set on fire by half.");
		addCodexFunction(AlchemancyProperties.WET, PropertyFunction.WHILE_ROOTED, "Extinguishes all entities standing inside of the item.");
		addCodexFunction(AlchemancyProperties.WET, PropertyFunction.RECEIVE_DAMAGE_WORN, "Increases the amount of {shock Electric} damage received by 10%.");

		addCodexFlavor(AlchemancyProperties.FROSTED, "Cooling it down");
		addCodexFunction(AlchemancyProperties.FROSTED, PropertyFunction.ON_ATTACK, "Freezes the target for 12 seconds.");
		addCodexFunction(AlchemancyProperties.FROSTED, PropertyFunction.WHILE_WORN, "Constantly freezes the user.");
		addCodexFunction(AlchemancyProperties.FROSTED, PropertyFunction.WHILE_ROOTED, "Freezes all entities standing inside of the item.");

		addCodexFlavor(AlchemancyProperties.SHOCKING, "A shock to the system");
		addCodexFunction(AlchemancyProperties.SHOCKING, PropertyFunction.ON_ATTACK, "Deal 5 points of {shock Electric} damage to the target and all other nearby entities.");
		addCodexFunction(AlchemancyProperties.SHOCKING, PropertyFunction.WHILE_ROOTED, "Constantly emits an electrical field that deals 5 points of {shock Electric} damage to nearby entities.");

		addCodexFlavor(AlchemancyProperties.PHOTOSYNTHETIC, "A growing grass boy's favorite meal");
		addCodexFunction(AlchemancyProperties.PHOTOSYNTHETIC, PropertyFunction.WHILE_IN_INVENTORY, "Repairs the item for 1 durability point every 30 seconds while the user is under direct sunlight.");
		addCodexFunction(AlchemancyProperties.PHOTOSYNTHETIC, PropertyFunction.WHILE_ROOTED, "Repairs the item for 1 durability point every 15 seconds while under direct sunlight.");

		addCodexFlavor(AlchemancyProperties.FLAMMABLE, "All we need now is a spark");
		addCodexFunction(AlchemancyProperties.FLAMMABLE, PropertyFunction.ON_ATTACK, "While the user is on fire, sets the target on fire for the same amount of time as the user.");
		addCodexFunction(AlchemancyProperties.FLAMMABLE, PropertyFunction.WHILE_EQUIPPED, "Constantly increases the user's time on fire after being set on fire once. Has a 1% chance every tick to turn into {property alchemancy:charred}");
		addCodexFunction(AlchemancyProperties.FLAMMABLE, PropertyFunction.OTHER, "Allows the item to be used as {item Furnace} fuel, letting it smelt 1 item for every 33 durability points it has left. Increases the item's fuel efficiency by 50% if it's aleady a {item Furnace} fuel.");

		addCodexFlavor(AlchemancyProperties.CHARRED, "A little overcooked...");
		addCodexFunction(AlchemancyProperties.CHARRED, PropertyFunction.OTHER, "Allows the item to be used as {item Furnace} fuel, letting it smelt 1 item for every 16 durability points it has left. Increases the item's fuel efficiency by 200% if it's aleady a {item Furnace} fuel.");

		addCodexFlavor(AlchemancyProperties.STURDY, "Rock and Stone!");
		addCodexFunction(AlchemancyProperties.STURDY, PropertyFunction.OTHER, "Increases the item's total durability by 20%.");

		addCodexFlavor(AlchemancyProperties.BRITTLE, "Shatter me like glass");
		addCodexFunction(AlchemancyProperties.BRITTLE, PropertyFunction.WHEN_SHOT, "Breaks the item on impact, triggering {function on_destroy} effects.");
		addCodexFunction(AlchemancyProperties.BRITTLE, PropertyFunction.WHEN_DROPPED, "Breaks the item after hitting the ground with enough force, triggering {function on_destroy} effects.");

		addCodexFlavor(AlchemancyProperties.RUSTY, ""); //TODO
		addCodexFunction(AlchemancyProperties.RUSTY, PropertyFunction.OTHER, "Causes the item to build up rust over time or when breaking blocks, increasing its mining speed and its chances of consuming double durability.");

		addCodexFlavor(AlchemancyProperties.FERROUS, ""); //TODO
		addCodexFunction(AlchemancyProperties.FERROUS, PropertyFunction.WHILE_WORN, "Increases the amount of {shock Electric} damage received by 25%.");
		addCodexFunction(AlchemancyProperties.FERROUS, PropertyFunction.ATTRIBUTE_MODIFIER, "Increases the item's total durability by 150 points.");

		addCodexFlavor(AlchemancyProperties.GILDED, "All that glitters");
		addCodexFunction(AlchemancyProperties.GILDED, PropertyFunction.WHILE_HELD_MAINHAND, "Increases the user's {attribute Mining Speed} by 50%.");
		addCodexFunction(AlchemancyProperties.GILDED, PropertyFunction.WHILE_WORN, "Prevents Piglins from immediately attacking the user.");
		addCodexFunction(AlchemancyProperties.GILDED, PropertyFunction.WHEN_DROPPED, "Makes Piglins want to pick up the item.");

		addCodexFlavor(AlchemancyProperties.LUSTROUS, "Funding for Schaffrillas is provided by");
		addCodexFunction(AlchemancyProperties.LUSTROUS, PropertyFunction.ATTRIBUTE_MODIFIER, "Sets the item's tool material to {item Diamond} tier. Doubles the item's durability up to a maximum total of 1600.");

		addCodexFlavor(AlchemancyProperties.WEALTHY, "Kaching!");
		addCodexFunction(AlchemancyProperties.WEALTHY, PropertyFunction.WHILE_EQUIPPED, "Makes Villagers and Pillagers follow the user.");
		addCodexFunction(AlchemancyProperties.WEALTHY, PropertyFunction.ATTRIBUTE_MODIFIER, "Increases the item's {enchantment minecraft:fortune} and {enchantment minecraft:looting} levels by 1.");

		addCodexFlavor(AlchemancyProperties.REINFORCED, "Tougher than the rest of them");
		addCodexFunction(AlchemancyProperties.REINFORCED, PropertyFunction.ATTRIBUTE_MODIFIER, "Increases the item's {attribute Armor} value by 3 and its {attribute Armor Toughness} by 1.");

		addCodexFlavor(AlchemancyProperties.PRISTINE, "More than just a pretty face");
		addCodexFunction(AlchemancyProperties.PRISTINE, PropertyFunction.DURABILITY_CONSUMED, "Consumes 1 {property alchemancy:pristine} point instead of taking damage. The Infusion is removed after 100 {property alchemancy:pristine} points are consumed.");

		addCodexFlavor(AlchemancyProperties.HELLBENT, "Stop at nothing");
		addCodexFunction(AlchemancyProperties.HELLBENT, PropertyFunction.MODIFY_DAMAGE, "Makes attacks always crit, triggering {function on_crit} effects.");
		addCodexFunction(AlchemancyProperties.HELLBENT, PropertyFunction.WHEN_SHOT, "Triggers {function on_crit} effects when hitting an entity.");
		addCodexFunction(AlchemancyProperties.HELLBENT, PropertyFunction.BLOCK_DESTROYED, "Increases the user's {attribute Mining Speed} for each block of the same type destroyed, up to an additional total of 20%. The speed boost is lost when a different type of block is mined or another item is held.");

		addCodexFlavor(AlchemancyProperties.DEPTH_DWELLER, "Yearn for the mines");
		addCodexFunction(AlchemancyProperties.DEPTH_DWELLER, PropertyFunction.WHILE_WORN_LOWER, "Increases the user's {attribute Movement Speed} relative to how low down they are in the world starting at Y=10, with a maximum increase of 50%. While in {nether The Nether}, the {attribute Movement Speed} boost is always 200%");
		addCodexFunction(AlchemancyProperties.DEPTH_DWELLER, PropertyFunction.WHILE_HELD_MAINHAND, "Increases the user's {attribute Mining Speed} relative to how low down they are in the world starting at Y=10, with a maximum increase of 50%. While in {nether The Nether}, the {attribute Mining Speed} boost is always 200%");

		addCodexFlavor(AlchemancyProperties.MALLEABLE, "Great for Stop Motion Animation");
		addCodexFunction(AlchemancyProperties.MALLEABLE, PropertyFunction.ON_DESTROYED, "The item drops an {item alchemancy:unshaped_clay}, which can be combined with a {item Clay Ball} or smelted down to restore the item.");

		addCodexFlavor(AlchemancyProperties.HARDENED, "Tough as nails");
		addCodexFunction(AlchemancyProperties.HARDENED, PropertyFunction.DURABILITY_CONSUMED, "Halves the amount of durability consumed by the item, or has a 10% chance not to consume any durability if the amount consumed is 1. ");
		addCodexFunction(AlchemancyProperties.HARDENED, PropertyFunction.WHEN_SHOT, "Allows the projectile to break certain blocks on impact, such as {item Glass}, {item Ice} or {item Decorated Pots}.");

		addCodexFlavor(AlchemancyProperties.CRACKED, "Falling apart");
		addCodexFunction(AlchemancyProperties.CRACKED, PropertyFunction.DURABILITY_CONSUMED, "40% chance to consume double durability.");
		addCodexFunction(AlchemancyProperties.CRACKED, PropertyFunction.ACTIVATE, "Breaks the item or consumes 1 durability point.");

		addCodexFlavor(AlchemancyProperties.MINING, ""); //TODO
		addCodexFunction(AlchemancyProperties.MINING, PropertyFunction.ATTRIBUTE_MODIFIER, "Allows the item to break Pickaxe-related blocks, starting at wooden tier if the item isn't already a tool.");
		addCodexFunction(AlchemancyProperties.MINING, PropertyFunction.WHEN_SHOT, "Breaks Pickaxe-related blocks on impact. The faster the projectile goes, the more likely it is to continue its trajectory after breaking a block.");

		addCodexFlavor(AlchemancyProperties.CHOPPING, ""); //TODO
		addCodexFunction(AlchemancyProperties.CHOPPING, PropertyFunction.ATTRIBUTE_MODIFIER, "Allows the item to break Axe-related blocks, starting at wooden tier if the item isn't already a tool.");
		addCodexFunction(AlchemancyProperties.CHOPPING, PropertyFunction.WHEN_USED_BLOCK, "Strips {item Logs} and removes wax from {item Copper Blocks}");
		addCodexFunction(AlchemancyProperties.CHOPPING, PropertyFunction.WHEN_SHOT, "Breaks Axe-related blocks on impact. The faster the projectile goes, the more likely it is to continue its trajectory after breaking a block.");

		addCodexFlavor(AlchemancyProperties.DIGGING, "Diggy diggy hole, digging a hole");
		addCodexFunction(AlchemancyProperties.DIGGING, PropertyFunction.ATTRIBUTE_MODIFIER, "Allows the item to break Shovel-related blocks, starting at wooden tier if the item isn't already a tool.");
		addCodexFunction(AlchemancyProperties.DIGGING, PropertyFunction.WHEN_USED_BLOCK, "Can turn {item Dirt} into {item Dirt Path} and put out {item Campfires}");
		addCodexFunction(AlchemancyProperties.DIGGING, PropertyFunction.WHEN_SHOT, "Breaks Shovel-related blocks on impact. The faster the projectile goes, the more likely it is to continue its trajectory after breaking a block.");

		addCodexFlavor(AlchemancyProperties.REAPING, ""); //TODO
		addCodexFunction(AlchemancyProperties.REAPING, PropertyFunction.ATTRIBUTE_MODIFIER, "Allows the item to break Hoe-related blocks, starting at wooden tier if the item isn't already a tool.");
		addCodexFunction(AlchemancyProperties.REAPING, PropertyFunction.WHEN_USED_BLOCK, "Can till {item Dirt} into {item Farmland}");
		addCodexFunction(AlchemancyProperties.REAPING, PropertyFunction.WHEN_SHOT, "Breaks Hoe-related blocks on impact. The faster the projectile goes, the more likely it is to continue its trajectory after breaking a block.");

		addCodexFlavor(AlchemancyProperties.SHEARING, "Beats paper");
		addCodexFunction(AlchemancyProperties.SHEARING, PropertyFunction.ATTRIBUTE_MODIFIER, "Allows the item to swiftly break Leaves, Cobwebs, and Wool.");
		addCodexFunction(AlchemancyProperties.SHEARING, PropertyFunction.WHEN_USED_ENTITY, "Can shear Sheep.");
		addCodexFunction(AlchemancyProperties.SHEARING, PropertyFunction.WHEN_SHOT_FROM_DISPENSER, "Can shear Sheep in front of the {item Dispenser}.");

		addCodexFlavor(AlchemancyProperties.SLASHING, "Got my sword. Got my hat. What else do I need?");
		addCodexFunction(AlchemancyProperties.SLASHING, PropertyFunction.ATTRIBUTE_MODIFIER, "Allows the item to swiftly break bamboo and cobwebs.");
		addCodexFunction(AlchemancyProperties.SLASHING, PropertyFunction.MODIFY_DAMAGE, "Performs a sweeping attack when standing still.");

		addCodexFlavor(AlchemancyProperties.SHARPSHOOTING, "Nothing gets past my bow");
		addCodexFunction(AlchemancyProperties.SHARPSHOOTING, PropertyFunction.WHEN_USED, "Fires {item Arrows} as if it were a {item Bow}.");

		addCodexFlavor(AlchemancyProperties.SHIELDING, "Block with your sword, just like the good old days");
		addCodexFunction(AlchemancyProperties.SHIELDING, PropertyFunction.RECEIVE_DAMAGE_USING, "Reduces blockable damage coming from in front by 50%.");

		addCodexFlavor(AlchemancyProperties.GRAPPLING, "Get over here!");
		addCodexFunction(AlchemancyProperties.GRAPPLING, PropertyFunction.ON_ATTACK, "Pulls targets towards you, instead of knocking them back.");

		addCodexFlavor(AlchemancyProperties.SPIKING, "Forward Aerial");
		addCodexFunction(AlchemancyProperties.SPIKING, PropertyFunction.WHILE_WORN_BOOTS, "Sets your movement friction to a constant value. ");
		addCodexFunction(AlchemancyProperties.SPIKING, PropertyFunction.ON_ATTACK, "Knocks targets down, instead of knocking them back.");

		addCodexFlavor(AlchemancyProperties.LAUNCHING, "To the moon");
		addCodexFunction(AlchemancyProperties.LAUNCHING, PropertyFunction.ON_CRIT, "Launches targets upwards, instead of knocking them back.");

		addCodexFlavor(AlchemancyProperties.SHARP, "Cutting edge");
		addCodexFunction(AlchemancyProperties.SHARP, PropertyFunction.ATTRIBUTE_MODIFIER, "Increases {attribute Attack Damage} by 30%.");

		addCodexFlavor(AlchemancyProperties.WEAK, "Doesn't even lift");
		addCodexFunction(AlchemancyProperties.WEAK, PropertyFunction.ATTRIBUTE_MODIFIER, "Reduces {attribute Armor} value by 1.");
		addCodexFunction(AlchemancyProperties.WEAK, PropertyFunction.MODIFY_DAMAGE, "Reduces damage dealt by 50%.");
		addCodexFunction(AlchemancyProperties.WEAK, PropertyFunction.WHILE_WORN, "Increases incoming damage by 20%.");

		addCodexFlavor(AlchemancyProperties.DENSE, "Stomping... Koopas");
		addCodexFunction(AlchemancyProperties.DENSE, PropertyFunction.MODIFY_DAMAGE, "Increases damage dealt equal to the amount of time the user has been falling for.");
		addCodexFunction(AlchemancyProperties.DENSE, PropertyFunction.ON_FALL, "Applies damage to nearby entities equal to the amount of time the user has been falling for. If landing after falling for more than 6 blocks, the item {activate Activates} on the user.");

		addCodexFlavor(AlchemancyProperties.GAMBLING, "...aw dang it");
		addCodexFunction(AlchemancyProperties.GAMBLING, PropertyFunction.MODIFY_DAMAGE, "has a 33% chance of dealing double damage, 33% chance of dealing normal damage, and 33% chance of damaging the user instead.");

		addCodexFlavor(AlchemancyProperties.ARCANE, "Join the glorious evolution");
		addCodexFunction(AlchemancyProperties.ARCANE, PropertyFunction.MODIFY_DAMAGE, "Causes the item to deal {arcane Magic} damage, making its attacks bypass {attribute Armor}.");
		addCodexFunction(AlchemancyProperties.ARCANE, PropertyFunction.ATTRIBUTE_MODIFIER, "Increases the item's {attribute Enchantability} by 18.");

		addCodexFlavor(AlchemancyProperties.RESIZED, "Honey, I shrunk the tools");
		addCodexFunction(AlchemancyProperties.RESIZED, PropertyFunction.VISUAL, "Scales the item's size equal to its {property alchemancy:resized} value");
		addCodexFunction(AlchemancyProperties.RESIZED, PropertyFunction.ATTRIBUTE_MODIFIER, "Increases the item's {attribute Attack Damage}, and reduces its {attribute Attack Speed} proportionally to its {property alchemancy:resized} value");

		addCodexFlavor(AlchemancyProperties.FERAL, "Standing here, I realize");
		addCodexFunction(AlchemancyProperties.FERAL, PropertyFunction.WHILE_HELD, "Increases the user's {attribute Attack Speed} by 45%.");

		addCodexFlavor(AlchemancyProperties.EXPLODING, "An earth-shattering kaboom");
		addCodexFunction(AlchemancyProperties.EXPLODING, PropertyFunction.ON_CRIT, "Creates an explosion around the target, greatly damaging the item or destroying it in the process.");

		addCodexFlavor(AlchemancyProperties.WIND_CHARGED, "Do the windy thing");
		addCodexFunction(AlchemancyProperties.WIND_CHARGED, PropertyFunction.ON_CRIT, "Creates a burst of wind around the target, greatly damaging the item or destroying it in the process.");

		addCodexFlavor(AlchemancyProperties.SMITING, "Behold the God of Thunder!");
		addCodexFunction(AlchemancyProperties.SMITING, PropertyFunction.ON_CRIT, "Creates a {item Lightning Bolt} at the target's position, greatly damaging the item or destroying it in the process.");

		addCodexFlavor(AlchemancyProperties.CRACKLING, "Be the light of the party");
		addCodexFunction(AlchemancyProperties.CRACKLING, PropertyFunction.ON_CRIT, "Creates a Firework explosion at the target's position, damaging all nearby entities, including the user. The explosion's effects depend on the {item Firework Rocket} or {item Firework Star} used to obtain this {system Infusion}.");

//		addCodexFlavor(AlchemancyProperties., "");
//		addCodexFunction(AlchemancyProperties., PropertyFunction., "");

		addCodexFlavor(AlchemancyProperties.SOULBIND, "Trapped Spirits");
		addCodexFunction(AlchemancyProperties.INFUSION_CODEX, PropertyFunction.WHILE_IN_INVENTORY, "Has a 2% chance every second of the soul trapped inside of the item of escaping, removing the {system Infusion} and potentially damaging the holder.");
		addCodexFunction(AlchemancyProperties.INFUSION_CODEX, PropertyFunction.OTHER, "Can be used to create multiple new {system Infusions}.");

		addCodexFlavor(AlchemancyProperties.SPIRIT_BOND, "Your souls, unite");
		addCodexFunction(AlchemancyProperties.SPIRIT_BOND, PropertyFunction.ON_HEAL, "Restores 10 durability points for each health point restored.");
		addCodexFunction(AlchemancyProperties.SPIRIT_BOND, PropertyFunction.RECEIVE_DAMAGE_EQUIPPED, "Loses 1 durability point for each health point lost.");

		addCodexFlavor(AlchemancyProperties.VENGEFUL, "An eye for an eye");
		addCodexFunction(AlchemancyProperties.VENGEFUL, PropertyFunction.MODIFY_DAMAGE, "Deals 85% more damage against the entity that last damaged the user, and 35% less damage to everyone else.");

		addCodexFlavor(AlchemancyProperties.LOYAL, "Always by your side");
		addCodexFunction(AlchemancyProperties.LOYAL, PropertyFunction.WHEN_DROPPED, "Slowly floats back to the dropper after a short period of time.");
		addCodexFunction(AlchemancyProperties.LOYAL, PropertyFunction.WHEN_SHOT, "Floats back to the shooter after impacting a block or an entity.");

		addCodexFlavor(AlchemancyProperties.INFUSION_CODEX, "Quite the inception");
		addCodexFunction(AlchemancyProperties.INFUSION_CODEX, PropertyFunction.WHEN_USED, "Opens the {item Infusion Codex}'s index menu.");
		addCodexFunction(AlchemancyProperties.INFUSION_CODEX, PropertyFunction.STACKED_OVER, "Inspects the targeted item, opening an {item Infusion Codex} index menu filtered to only show its applied {system Infusions}, {system Innate Properties}, and {property alchemancy:revealed} or {property alchemancy:awakened} {system Properties}");

		addCodexFlavor(AlchemancyProperties.ETERNAL_GLOW, "Light up the skies");
		addCodexFunction(AlchemancyProperties.ETERNAL_GLOW, PropertyFunction.WHILE_EQUIPPED, "Automatically place down a {item Glowing Orb} at your feet when the light level is low enough.");

		addCodexFlavor(AlchemancyProperties.PHASE_STEP, "Physics are but a mere suggestion");
		addCodexFunction(AlchemancyProperties.PHASE_STEP, PropertyFunction.WHILE_EQUIPPED, "Grants you the ability to phase through blocks, including those below you. Use with caution.");

		addCodexFlavor(AlchemancyProperties.FRIENDLY, "Friendly-fire is overrated anyways");
		addCodexFunction(AlchemancyProperties.FRIENDLY, PropertyFunction.WHILE_EQUIPPED, "Prevents your attacks from damaging Passive Mobs and other Players.");

		addCodexFlavor(AlchemancyProperties.DEATH_WARD, "Born again!");
		addCodexFunction(AlchemancyProperties.DEATH_WARD, PropertyFunction.WHILE_EQUIPPED, "Saves the user from dying at the cost of 500 durability points, or the item itself.");

		addCodexFlavor(AlchemancyProperties.ROCKET_POWERED, "Pchooooo!");
		addCodexFunction(AlchemancyProperties.ROCKET_POWERED, PropertyFunction.WHILE_USING, "Propels the user forwards at high speeds, consuming 2 durability points or the item itself every second.");
		addCodexFunction(AlchemancyProperties.ROCKET_POWERED, PropertyFunction.WHILE_WORN_BOOTS, "Propels the user upwards at high speeds while jumping, consuming 2 durability points or the item itself every second.");

		addCodexFlavor(AlchemancyProperties.WAYWARD_WARP, "Always skip the way back");
		addCodexFunction(AlchemancyProperties.WAYWARD_WARP, PropertyFunction.WHEN_USED_BLOCK, "Saves the targeted {item Lodestone}'s position as a destination if none is present.");
		addCodexFunction(AlchemancyProperties.WAYWARD_WARP, PropertyFunction.WHEN_USED_ENTITY, "Saves the targeted Player as a destination if none is present.");
		addCodexFunction(AlchemancyProperties.WAYWARD_WARP, PropertyFunction.AFTER_USE, "Teleports the user to the saved destination if on the same dimension, consuming 10 durability points or the item in the process.");

		addCodexFlavor(AlchemancyProperties.FLAME_STEP, "Like TRON, but for arsonists");
		addCodexFunction(AlchemancyProperties.FLAME_STEP, PropertyFunction.WHILE_WORN_BOOTS, "Creates a trail of short-lasting {item Fire} while sprinting. Extends the duration of {item Fire} under the player while standing still.");

		addCodexFlavor(AlchemancyProperties.BINDING, "Frost Jailer's signature move");
		addCodexFunction(AlchemancyProperties.BINDING, PropertyFunction.STACKED_OVER, "Applies or removes {property alchemancy:unmovable} from the item, preventing it from being dropped or moved into a different slot.");

		addCodexFlavor(AlchemancyProperties.UNMOVABLE, "Locked into place");
		addCodexFunction(AlchemancyProperties.UNMOVABLE, PropertyFunction.OTHER, "Prevents the item from being dropped or moved into a different slot. Can be removed by stacking an item with {property alchemancy:binding} over this one.");

		addCodexFlavor(AlchemancyProperties.INFUSION_CLEANSE, "The most absorbent material I've ever used");
		addCodexFunction(AlchemancyProperties.INFUSION_CLEANSE, PropertyFunction.STACKED_OVER, "Removes all {system Infusions} from the target item.");

		addCodexFlavor(AlchemancyProperties.FLAME_EMPEROR, "The air is getting warmer around you");
		addCodexFunction(AlchemancyProperties.FLAME_EMPEROR, PropertyFunction.WHILE_HELD_MAINHAND, "Doubles the user's {attribute Mining Speed} while on fire.");
		addCodexFunction(AlchemancyProperties.FLAME_EMPEROR, PropertyFunction.MODIFY_DAMAGE, "Increases damage by 25%, with a limit of 3 times the attack's base damage, while the user is on fire.");
		addCodexFunction(AlchemancyProperties.FLAME_EMPEROR, PropertyFunction.ON_ATTACK, "Sets the target on fire for 3 seconds, or 6 seconds if the user is also on fire.");
		addCodexFunction(AlchemancyProperties.FLAME_EMPEROR, PropertyFunction.BLOCK_DESTROYED, "Smelts all dropped items while the user is on fire.");

		addCodexFlavor(AlchemancyProperties.GUST_JET, "Blow their socks off");
		addCodexFunction(AlchemancyProperties.GUST_JET, PropertyFunction.WHILE_USING, "Blows the user backwards unless they're crouching while standing on solid ground. Knocks all entities in front of the user back, with a 20% chance every tick of triggering {function on_attack} effects on them. Consumes 1 durability point or the item itself every 2 seconds.");
		addCodexFunction(AlchemancyProperties.GUST_JET, PropertyFunction.WHILE_WORN_BOOTS, "Blows the user a moderate distance away from the ground.. Knocks all entities under the user back, with a 20% chance every tick of triggering {function on_attack} effects on them. Consumes 1 durability point or the item itself every 2 seconds.");

		addCodexFlavor(AlchemancyProperties.BLINKING, "Quick on our feet, hard to defeat!");
		addCodexFunction(AlchemancyProperties.BLINKING, PropertyFunction.WHILE_WORN, "Phase up to 10 blocks forward after initiating a sprint, consuming 5 durability points or the item itself.");
		addCodexFunction(AlchemancyProperties.BLINKING, PropertyFunction.ACTIVATE, "The user phases up to 10 blocks in the direction they're looking, consuming 5 durability points or the item itself.");

		addCodexFlavor(AlchemancyProperties.VAULTPICKING, "Hack the system");
		addCodexFunction(AlchemancyProperties.VAULTPICKING, PropertyFunction.WHEN_USED_BLOCK, "Resets a {item Trial Vault}'s inner mechanisms at the cost of 50 durability points or the item, allowing only the user to insert another key and gather additional loot.");

		addCodexFlavor(AlchemancyProperties.WARPED, "A glimpse into my twisted mind");
		addCodexFunction(AlchemancyProperties.WARPED, PropertyFunction.OTHER, "Alters every compatible {system Infusion} on the item at the end of the {system Infusion Process}, often turning them into an opposite, or related counterpart.");

		addCodexFlavor(AlchemancyProperties.HOME_RUN, "And the crowd goes wild!");
		addCodexFunction(AlchemancyProperties.HOME_RUN, PropertyFunction.MODIFY_DAMAGE, "Knocks the target back a great distance if the attack deals at least 80% of the user's {attribute Attack Damage} stat.");
		addCodexFunction(AlchemancyProperties.HOME_RUN, PropertyFunction.ATTRIBUTE_MODIFIER, "Sets the item's base {attribute Attack Speed} to 0.2");

		addCodexFlavor(AlchemancyProperties.DIRTY, "Filthy");
		addCodexFunction(AlchemancyProperties.DIRTY, PropertyFunction.OTHER, "It's dirt. Get it off, ew.");

		addCodexFlavor(AlchemancyProperties.AWKWARD, "A tad strange");
		addCodexFunction(AlchemancyProperties.AWKWARD, PropertyFunction.OTHER, "Can be used create various new {system Infusions} related to Potion Effects.");

		addCodexFlavor(AlchemancyProperties.LIMIT_BREAK, "This is to go even further beyond!");
		addCodexFunction(AlchemancyProperties.LIMIT_BREAK, PropertyFunction.ATTRIBUTE_MODIFIER, "Grants an additional {system Infusion Slot}");

		addCodexFlavor(AlchemancyProperties.AWAKENED, "Unlock your True Potential");
		addCodexFunction(AlchemancyProperties.AWAKENED, PropertyFunction.OTHER, "Makes {system Dormant Properties} act as if they were {system Infused} onto the item, triggering all related effects.");

		addCodexFlavor(AlchemancyProperties.PARADOXICAL, "Quite the conundrum");
		addCodexFunction(AlchemancyProperties.PARADOXICAL, PropertyFunction.OTHER, "Prevents {system Property Interactions} from affecting the item during the {system Infusion} process.");
	}

	protected void addCodexFlavor(Holder<Property> propertyHolder, String text) {

		String translationKey = "infusion_codex.%s.flavor".formatted(propertyHolder.getRegisteredName());
		add(translationKey, text);
		CodexEntryProvider.ENTRIES.put(propertyHolder, new CodexEntryReloadListenener.CodexEntry(Component.translatable(translationKey), new ArrayList<>()));
	}
	protected void addCodexFunction(Holder<Property> propertyHolder, PropertyFunction function, String text) {
		add("infusion_codex.%s.%s".formatted(propertyHolder.getRegisteredName(), function.localizationKey), text);

		if(CodexEntryProvider.ENTRIES.containsKey(propertyHolder))
			CodexEntryProvider.ENTRIES.get(propertyHolder).functions().add(function);
	}
}
