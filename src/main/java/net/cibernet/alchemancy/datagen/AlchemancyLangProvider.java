package net.cibernet.alchemancy.datagen;

import net.cibernet.alchemancy.Alchemancy;
import net.cibernet.alchemancy.client.data.CodexEntryReloadListenener;
import net.cibernet.alchemancy.properties.Property;
import net.cibernet.alchemancy.registries.AlchemancyProperties;
import net.cibernet.alchemancy.util.PropertyFunction;
import net.minecraft.core.Holder;
import net.minecraft.data.PackOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.animal.Cod;
import net.neoforged.neoforge.common.data.LanguageProvider;

import java.util.ArrayList;
import java.util.List;

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
		addCodexFunction(AlchemancyProperties.WET, PropertyFunction.WHILE_ROOTED, "Waters {item Farmland} in a 4-block radius. Extinguishes all entities standing inside of the item.");
		addCodexFunction(AlchemancyProperties.WET, PropertyFunction.RECEIVE_DAMAGE_WORN, "Increases the amount of {shock Electric} damage received by 10%.");

		addCodexFlavor(AlchemancyProperties.FROSTED, "Cooling it down");
		addCodexFunction(AlchemancyProperties.FROSTED, PropertyFunction.ON_ATTACK, "Freezes the target for 12 seconds.");
		addCodexFunction(AlchemancyProperties.FROSTED, PropertyFunction.WHILE_WORN, "Constantly freezes the user.");
		addCodexFunction(AlchemancyProperties.FROSTED, PropertyFunction.WHILE_ROOTED, "Freezes all entities standing inside of the item.");

		addCodexFlavor(AlchemancyProperties.SHOCKING, "A shock to the system");
		addCodexFunction(AlchemancyProperties.SHOCKING, PropertyFunction.ON_ATTACK, "Deal 4 points of chaining {shock Electric} damage to the target and nearby entities. The amount of damage dealt decays over distance and for each consecutive hit.");
		addCodexFunction(AlchemancyProperties.SHOCKING, PropertyFunction.WHILE_ROOTED, "Constantly emits an electrical field that deals 3 points of chaining {shock Electric} damage to nearby entities. The amount of damage dealt decays over distance and for each consecutive hit.");

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

		addCodexFlavor(AlchemancyProperties.ENERGIZED, "Need more power");
		addCodexFunction(AlchemancyProperties.ENERGIZED, PropertyFunction.RECEIVE_DAMAGE_EQUIPPED, "Increases the user's {attribute Movement Speed}, {attribute Mining Speed}, and {attribute Attack Speed} by 35% for 60 seconds after taking {shock Electric} damage.");
		addCodexFunction(AlchemancyProperties.ENERGIZED, PropertyFunction.WHILE_WORN_BOOTS, "For 60 seconds after taking {shock Electric} damage, {item Redstone} powers blocks the user is standing on for half a second.");
		addCodexFunction(AlchemancyProperties.ENERGIZED, PropertyFunction.WHEN_USED_BLOCK, "{item Redstone} powers the targeted block for half a second.");
		addCodexFunction(AlchemancyProperties.ENERGIZED, PropertyFunction.WHEN_SHOT, "{item Redstone} powers the impacted block for half a second.");
		addCodexFunction(AlchemancyProperties.ENERGIZED, PropertyFunction.ACTIVATE_BY_BLOCK, "{item Redstone} powers the adjacent blocks for 1 second.");

		addCodexFlavor(AlchemancyProperties.BOUNCY, "Slime Boots who?");
		addCodexFunction(AlchemancyProperties.BOUNCY, PropertyFunction.ON_ATTACK, "Bounces the target back a short distance.");
		addCodexFunction(AlchemancyProperties.BOUNCY, PropertyFunction.WHEN_HIT_EQUIPPED, "Bounces the user away from the damage source for a short distance.");
		addCodexFunction(AlchemancyProperties.BOUNCY, PropertyFunction.ACTIVATE, "Bounces the user away from the target for a short distance.");
		addCodexFunction(AlchemancyProperties.BOUNCY, PropertyFunction.ON_FALL, "Bounces the user upwards with reduced force of which they hit the ground, nullifying all fall damage.");
		addCodexFunction(AlchemancyProperties.BOUNCY, PropertyFunction.WHEN_SHOT, "Bounces off of blocks on impact if the Projectile is going at a high enough speed.");

		addCodexFlavor(AlchemancyProperties.SLIPPERY, "Warned you about the stairs");
		addCodexFunction(AlchemancyProperties.SLIPPERY, PropertyFunction.ON_ATTACK, "Makes the user drop their held item.");
		addCodexFunction(AlchemancyProperties.SLIPPERY, PropertyFunction.ACTIVATE, "Makes the user drop their held item.");
		addCodexFunction(AlchemancyProperties.SLIPPERY, PropertyFunction.WHILE_WORN_BOOTS, "Reduces the user's movement friction. Makes the user trip when going down {item Stairs}.");

		addCodexFlavor(AlchemancyProperties.BUOYANT, "Don't forget your floaties");
		addCodexFunction(AlchemancyProperties.BUOYANT, PropertyFunction.WHILE_EQUIPPED, "Floats the user upwards while inside of a liquid.");
		addCodexFunction(AlchemancyProperties.BUOYANT, PropertyFunction.WHEN_SHOT, "Floats the Projectile upwards while inside of a liquid.");
		addCodexFunction(AlchemancyProperties.BUOYANT, PropertyFunction.WHEN_DROPPED, "Floats the item upwards while inside of a liquid.");

		addCodexFlavor(AlchemancyProperties.HEAVY, "Hard to carry");
		addCodexFunction(AlchemancyProperties.HEAVY, PropertyFunction.WHILE_EQUIPPED, "Reduces the user's {attribute Movement Speed} and {attribute Jump Height}, as well as increasing their falling speed.");
		addCodexFunction(AlchemancyProperties.HEAVY, PropertyFunction.WHEN_SHOT, "Increases the projectile's falling speed.");
		addCodexFunction(AlchemancyProperties.HEAVY, PropertyFunction.WHEN_DROPPED, "Increases the item's falling speed.");

		addCodexFlavor(AlchemancyProperties.ANTIGRAV, "Like drifting through space");
		addCodexFunction(AlchemancyProperties.ANTIGRAV, PropertyFunction.WHILE_WORN, "Reduces the user's {attribute Gravity} by 100%");
		addCodexFunction(AlchemancyProperties.ANTIGRAV, PropertyFunction.WHEN_SHOT, "Disables the Projectile's {attribute Gravity}, causing it to fly in a straight line.");
		addCodexFunction(AlchemancyProperties.ANTIGRAV, PropertyFunction.WHEN_DROPPED, "Disables the item's {attribute Gravity}, causing it to fly in a straight line.");

		addCodexFlavor(AlchemancyProperties.LIGHTWEIGHT, "Lighter than a feather");
		addCodexFunction(AlchemancyProperties.LIGHTWEIGHT, PropertyFunction.WHILE_WORN_BOOTS, "Allows the user to walk on {item Powder Snow}.");
		addCodexFunction(AlchemancyProperties.LIGHTWEIGHT, PropertyFunction.WHEN_SHOT, "Lowers the projectile's falling speed.");
		addCodexFunction(AlchemancyProperties.LIGHTWEIGHT, PropertyFunction.WHEN_DROPPED, "Lowers the item's falling speed.");

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

		addCodexFlavor(AlchemancyProperties.FIRESTARTING, ""); //TODO
		addCodexFunction(AlchemancyProperties.FIRESTARTING, PropertyFunction.WHEN_USED_BLOCK, "Creates {item Fire} at the targeted block. Allows the item to light {item Candles} and {item Campfires}.");
		addCodexFunction(AlchemancyProperties.FIRESTARTING, PropertyFunction.WHEN_SHOT_FROM_DISPENSER, "Creates {item Fire} at the block in front. Light {item Candles} and {item Campfires} in front of the {item Dispenser}.");

		addCodexFlavor(AlchemancyProperties.BRUSHING, ""); //TODO
		addCodexFunction(AlchemancyProperties.BRUSHING, PropertyFunction.WHEN_USED_BLOCK, "Allows the item to brush off {item Suspicious Blocks}.");
		addCodexFunction(AlchemancyProperties.BRUSHING, PropertyFunction.WHEN_USED_ENTITY, "Can brush Armadillos to obtain {item Armadillo Scute}.");
		addCodexFunction(AlchemancyProperties.BRUSHING, PropertyFunction.WHEN_SHOT_FROM_DISPENSER, "Brushes an Armadillo in front of the {item Dispenser} to obtain {item Armadillo Scute}.");

		addCodexFlavor(AlchemancyProperties.SCOPING, ""); //TODO
		addCodexFunction(AlchemancyProperties.SCOPING, PropertyFunction.WHILE_HELD, "Zooms the user's vision in while crouching.");
		addCodexFunction(AlchemancyProperties.SCOPING, PropertyFunction.WHILE_WORN_HELMET, "Zooms the user's vision in while crouching.");

		addCodexFlavor(AlchemancyProperties.THROWABLE, ""); //TODO
		addCodexFunction(AlchemancyProperties.THROWABLE, PropertyFunction.WHEN_USED, "Throws the item like a Projectile, triggering {function when_shot} effects when appropriate. The thrown item takes 10 durability points or is destroyed when it hits a block or an entity.");
		addCodexFunction(AlchemancyProperties.THROWABLE, PropertyFunction.WHEN_SHOT_FROM_DISPENSER, "Throws the item like a Projectile, triggering {function when_shot} effects when appropriate. The thrown item takes 10 durability points or is destroyed when it hits a block or an entity.");

		addCodexFlavor(AlchemancyProperties.WAYFINDING, "Always know the way back");
		addCodexFunction(AlchemancyProperties.WAYFINDING, PropertyFunction.VISUAL, "Rotates the item to point towards the tracked position, or if none is present the user's spawn point, the exit portal in {end The End}, or the place the user entered the dimension from.");
		addCodexFunction(AlchemancyProperties.WAYFINDING, PropertyFunction.WHEN_USED_BLOCK, "Saves the targeted {item Lodestone}'s position as a tracked position if none is present.");
		addCodexFunction(AlchemancyProperties.WAYFINDING, PropertyFunction.WHEN_USED_ENTITY, "Saves the targeted Player as a tracked position if none is present.");

		addCodexFlavor(AlchemancyProperties.HEADWEAR, "Anything is a hat if you're brave enough");
		addCodexFunction(AlchemancyProperties.HEADWEAR, PropertyFunction.OTHER, "Allows the item to be worn in the Helmet slot.");
		addCodexFunction(AlchemancyProperties.HEADWEAR, PropertyFunction.WHEN_SHOT_FROM_DISPENSER, "Equips the item onto the Helmet slot of an Entity in front of the {item Dispenser}.");

		addCodexFlavor(AlchemancyProperties.SADDLED, ""); //TODO
		addCodexFunction(AlchemancyProperties.SADDLED, PropertyFunction.WHEN_USED_ENTITY, "Makes the user ride the target. {system Infusions} that attract mobs can be applied to the held item to steer the ridden entity.");

		addCodexFlavor(AlchemancyProperties.GLIDER, "Falling with style");
		addCodexFunction(AlchemancyProperties.GLIDER, PropertyFunction.WHILE_WORN_CHESTPLATE, "Lets the user glide as if they were wearing an {item Elytra} as long as the item is more than 1 point of durability away from breaking.");

		addCodexFlavor(AlchemancyProperties.CRAFTY, "Crafting on the go");
		addCodexFunction(AlchemancyProperties.CRAFTY, PropertyFunction.WHEN_USED, "Opens the {item Crafting Table} interface");

		addCodexFlavor(AlchemancyProperties.ASSEMBLING, "Autocrafting on the go");
		addCodexFunction(AlchemancyProperties.ASSEMBLING, PropertyFunction.WHILE_EQUIPPED, "Attempts to craft as many replicas of the item by consuming resources in the user's inventory until the item reaches its stack limit.");

		addCodexFlavor(AlchemancyProperties.ASSIMILATING, ""); //TODO
		addCodexFunction(AlchemancyProperties.ASSIMILATING, PropertyFunction.WHILE_EQUIPPED, "Automatically absorbs any other items like it in the user's inventory when low on durability, replenishing the item's durability in the process. If {property alchemancy:assembling} is present, the item will be able to absorb ingredients used to craft itself inside of the user's inventory as long as they can all create a new item.");
		addCodexFunction(AlchemancyProperties.ASSIMILATING, PropertyFunction.STACKED_OVER, "Absorbs similar items stacked over the item to replenish its durability.");

		addCodexFlavor(AlchemancyProperties.SMELTING, "Heating up your tools can be a good idea");
		addCodexFunction(AlchemancyProperties.SMELTING, PropertyFunction.BLOCK_DESTROYED, "Smelts all dropped items at the cost of 1 {fire Fuel} point per item.");
		addCodexFunction(AlchemancyProperties.SMELTING, PropertyFunction.ON_KILL, "Smelts all dropped items at the cost of 1 {fire Fuel} point per item.");
		addCodexFunction(AlchemancyProperties.SMELTING, PropertyFunction.STACKED_OVER, "If the stacked item is a valid {item Furnace} fuel, it is consumed and used to replenish {fire Fuel}.");
		addCodexFunction(AlchemancyProperties.SMELTING, PropertyFunction.OTHER, "{fire Fuel} is automatically restored upon running out if a {item Furnace} fuel is stored inside of the item by the use of {property alchemancy:hollow}.");


		addCodexFlavor(AlchemancyProperties.SWIFT, "Run like the wind");
		addCodexFunction(AlchemancyProperties.SWIFT, PropertyFunction.WHILE_WORN_LEGGINGS, "Increases the user's {attribute Movement Speed} by 55%.");
		addCodexFunction(AlchemancyProperties.SWIFT, PropertyFunction.WHILE_HELD_MAINHAND, "Increases the user's {attribute Attack Speed} by 25%.");
		addCodexFunction(AlchemancyProperties.SWIFT, PropertyFunction.WHILE_USING, "Halves the item's use time.");

		addCodexFlavor(AlchemancyProperties.SLUGGISH, ""); //TODO
		addCodexFunction(AlchemancyProperties.SLUGGISH, PropertyFunction.WHILE_WORN, "Reduces the user's {attribute Movement Speed} by 55%.");
		addCodexFunction(AlchemancyProperties.SLUGGISH, PropertyFunction.ATTRIBUTE_MODIFIER, "Reduces {attribute Attack Speed} by 55%.");
		addCodexFunction(AlchemancyProperties.SLUGGISH, PropertyFunction.ON_ATTACK, "Applies Slowness II to the target for 10 seconds.");
		addCodexFunction(AlchemancyProperties.SLUGGISH, PropertyFunction.WHILE_USING, "Doubles the item's use time.");

		addCodexFlavor(AlchemancyProperties.POISONOUS, "Not venomous");
		addCodexFunction(AlchemancyProperties.POISONOUS, PropertyFunction.ON_ATTACK, "Applies Poison to the target for 5 seconds.");

		addCodexFlavor(AlchemancyProperties.DECAYING, "Withering away");
		addCodexFunction(AlchemancyProperties.DECAYING, PropertyFunction.ON_ATTACK, "Applies Wither II to the target for 5 seconds.");
		addCodexFunction(AlchemancyProperties.DECAYING, PropertyFunction.WHILE_EQUIPPED, "Loses durability every second.");

		addCodexFlavor(AlchemancyProperties.TIPSY, "Better lay off the Pufferfish for a while");
		addCodexFunction(AlchemancyProperties.TIPSY, PropertyFunction.ON_ATTACK, "Applies Nausea to the target for 10 seconds.");
		addCodexFunction(AlchemancyProperties.TIPSY, PropertyFunction.WHILE_EQUIPPED, "Applies Nausea to the user for 10 seconds.");

		addCodexFlavor(AlchemancyProperties.BLINDING, "Turning off the lights");
		addCodexFunction(AlchemancyProperties.BLINDING, PropertyFunction.ON_ATTACK, "Applies Blindness to the target for 10 seconds.");
		addCodexFunction(AlchemancyProperties.BLINDING, PropertyFunction.WHILE_WORN_HELMET, "Applies Blindness to the user for 10 seconds.");

		addCodexFlavor(AlchemancyProperties.GLOWING_AURA, "A beacon of light");
		addCodexFunction(AlchemancyProperties.GLOWING_AURA, PropertyFunction.ON_ATTACK, "Applies Glowing to the target for 30 seconds.");
		addCodexFunction(AlchemancyProperties.GLOWING_AURA, PropertyFunction.WHILE_EQUIPPED, "Applies Glowing to the user for as long as its equipped.");
		addCodexFunction(AlchemancyProperties.GLOWING_AURA, PropertyFunction.WHEN_SHOT, "Enshrouds the Projectile in a glowing aura.");
		addCodexFunction(AlchemancyProperties.GLOWING_AURA, PropertyFunction.WHEN_DROPPED, "Enshrouds the item in a glowing aura.");

		addCodexFlavor(AlchemancyProperties.NOCTURNAL, "I am vengeance, I am the night");
		addCodexFunction(AlchemancyProperties.NOCTURNAL, PropertyFunction.MODIFY_DAMAGE, "Increases damage dealt by 40% while under direct moonlight.");
		addCodexFunction(AlchemancyProperties.NOCTURNAL, PropertyFunction.WHILE_WORN_HELMET, "Applies Night Vision to the user for 15 seconds.");

		addCodexFlavor(AlchemancyProperties.AQUATIC, "Swim with the fishes");
		addCodexFunction(AlchemancyProperties.AQUATIC, PropertyFunction.WHILE_WORN_HELMET, "Increases {attribute Oxygen Bonus} value by 2, granting a similar effect to {enchantment minecraft:respiration} II.");
		addCodexFunction(AlchemancyProperties.AQUATIC, PropertyFunction.WHILE_WORN_CHESTPLATE, "Increases {attribute Mining Speed} by 200% while underwater.");
		addCodexFunction(AlchemancyProperties.AQUATIC, PropertyFunction.WHILE_HELD_MAINHAND, "Increases {attribute Mining Speed} by 200% while underwater.");
		addCodexFunction(AlchemancyProperties.AQUATIC, PropertyFunction.WHILE_WORN_LEGGINGS, "Increases {attribute Swim Speed} by 55%.");
		addCodexFunction(AlchemancyProperties.AQUATIC, PropertyFunction.WHILE_WORN_BOOTS, "Increases {attribute Walking Speed} by 55% while underwater.");
		addCodexFunction(AlchemancyProperties.AQUATIC, PropertyFunction.WHEN_SHOT, "Allows Projectiles to fly through water as they would outside of it.");

		addCodexFlavor(AlchemancyProperties.LEAPING, "A hop, a skip, and a jump");
		addCodexFunction(AlchemancyProperties.LEAPING, PropertyFunction.WHILE_WORN_LOWER, "Increases the user's {attribute Jump Height}, increasing the height of each consecutive jump for up to 4 jumps in a row. Increases {attribute Safe Fall Distance} by 7 blocks.");

		addCodexFlavor(AlchemancyProperties.OMINOUS, "I have a bad feeling about this");
		addCodexFunction(AlchemancyProperties.OMINOUS, PropertyFunction.WHILE_EQUIPPED, "Applies Bad Omen to the user for as long as the item is equipped.");
		addCodexFunction(AlchemancyProperties.OMINOUS, PropertyFunction.ON_ATTACK, "Applies Bad Omen to the target for half a second.");

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

		addCodexFlavor(AlchemancyProperties.CALCAREOUS, "[kal-kair-ee-uhs] Adjective: contains calcium");
		addCodexFunction(AlchemancyProperties.CALCAREOUS, PropertyFunction.WHILE_WORN_LEGGINGS, "Reduces the user's {attribute Fall Damage} by 50% and increases their {attribute Safe Fall Distance} by 10 blocks.");
		addCodexFunction(AlchemancyProperties.CALCAREOUS, PropertyFunction.ACTIVATE, "Clears the target's Potion Effects and {property alchemancy:chromatize} tint.");

		addCodexFlavor(AlchemancyProperties.COZY, "Nice and warm");
		addCodexFunction(AlchemancyProperties.COZY, PropertyFunction.WHILE_WORN, "Increases the rate at which the user thaws out, enough to prevent {item Powder Snow} from having any effect.");
		addCodexFunction(AlchemancyProperties.COZY, PropertyFunction.WHILE_ROOTED, "Swiftly thaws out all entities standing inside of the item.");

		addCodexFlavor(AlchemancyProperties.WAXED, ""); //TODO
		addCodexFunction(AlchemancyProperties.WAXED, PropertyFunction.RECEIVE_DAMAGE_WORN, "Prevents the user from taking {fire Fire} damage up to 50 times before the {system Infusion} is removed.");

		addCodexFlavor(AlchemancyProperties.FIRE_RESISTANT, "Someone remembered to put on sunscreen");
		addCodexFunction(AlchemancyProperties.FIRE_RESISTANT, PropertyFunction.WHILE_WORN, "Reduces the user's amount of time set on fire by 75%, limiting their time set on fire to at most 12 seconds.");
		addCodexFunction(AlchemancyProperties.FIRE_RESISTANT, PropertyFunction.WHEN_DROPPED, "Makes the item immune to fire.");

		addCodexFlavor(AlchemancyProperties.BLAST_RESISTANT, "All of a sudden explosions feel safe");
		addCodexFunction(AlchemancyProperties.BLAST_RESISTANT, PropertyFunction.RECEIVE_DAMAGE_WORN_OR_USING, "Reduces damage dealt by explosions by 50%.");

		addCodexFlavor(AlchemancyProperties.MAGIC_RESISTANT, "Hard counters Arcane Barrage");
		addCodexFunction(AlchemancyProperties.MAGIC_RESISTANT, PropertyFunction.RECEIVE_DAMAGE_WORN_OR_USING, "Reduces incoming {arcane Magic} damage by 15%.");

		addCodexFlavor(AlchemancyProperties.INSULATED, "A key part of any Energized build");
		addCodexFunction(AlchemancyProperties.INSULATED, PropertyFunction.RECEIVE_DAMAGE_WORN_OR_USING, "Reduces incoming {shock Electric} damage by 25%.");

		addCodexFlavor(AlchemancyProperties.WARDING, "Become an Armored Bastion");
		addCodexFunction(AlchemancyProperties.WARDING, PropertyFunction.RECEIVE_DAMAGE_WORN_OR_USING, "Reduces ALL incoming damage by 15%.");

		addCodexFlavor(AlchemancyProperties.ETERNAL, "Hasn't aged a day");
		addCodexFunction(AlchemancyProperties.ETERNAL, PropertyFunction.WHEN_DROPPED, "Prevents the item from despawning.");

		//TODO MUFFLED

		addCodexFlavor(AlchemancyProperties.SOULBIND, "Trapped Spirits");
		addCodexFunction(AlchemancyProperties.SOULBIND, PropertyFunction.WHILE_IN_INVENTORY, "Has a 2% chance every second of the soul trapped inside of the item of escaping, removing the {system Infusion} and potentially damaging the holder.");
		addCodexFunction(AlchemancyProperties.SOULBIND, PropertyFunction.OTHER, "Can be used to create multiple new {system Infusions}.");

		addCodexFlavor(AlchemancyProperties.SPIRIT_BOND, "Your souls, unite");
		addCodexFunction(AlchemancyProperties.SPIRIT_BOND, PropertyFunction.ON_HEAL, "Restores 10 durability points for each health point restored.");
		addCodexFunction(AlchemancyProperties.SPIRIT_BOND, PropertyFunction.RECEIVE_DAMAGE_EQUIPPED, "Loses 1 durability point for each health point lost.");

		addCodexFlavor(AlchemancyProperties.VENGEFUL, "An eye for an eye");
		addCodexFunction(AlchemancyProperties.VENGEFUL, PropertyFunction.MODIFY_DAMAGE, "Deals 85% more damage against the entity that last damaged the user, and 35% less damage to everyone else.");

		addCodexFlavor(AlchemancyProperties.LOYAL, "Always by your side");
		addCodexFunction(AlchemancyProperties.LOYAL, PropertyFunction.WHEN_DROPPED, "Slowly floats back to the dropper after a short period of time.");
		addCodexFunction(AlchemancyProperties.LOYAL, PropertyFunction.WHEN_SHOT, "Floats back to the shooter after impacting a block or an entity.");

		addCodexFlavor(AlchemancyProperties.RELENTLESS, "Not giving up");
		addCodexFunction(AlchemancyProperties.RELENTLESS, PropertyFunction.DURABILITY_CONSUMED, "Grants a chance to not consume durability relative to how much has been used up, with a maximum chance of 80%.");
		addCodexFunction(AlchemancyProperties.RELENTLESS, PropertyFunction.RECEIVE_DAMAGE_WORN_OR_USING, "Reduces all incoming damage relative to how much health the user has lost, with a maximum reduction of 20%.");

		addCodexFlavor(AlchemancyProperties.VAMPIRIC, "It doesn't suck... it scrapes");
		addCodexFunction(AlchemancyProperties.VAMPIRIC, PropertyFunction.MODIFY_DAMAGE, "Heals the user for 20% of the damage dealt.");
		addCodexFunction(AlchemancyProperties.VAMPIRIC, PropertyFunction.WHILE_EQUIPPED, "has a 40% chance every second of consuming 1 point of durability under broad daylight, with a 10% chance on top of that of instead setting the user ablaze for 4 seconds.");

		addCodexFlavor(AlchemancyProperties.ENERGY_SAPPER, "A symbiotic relationship");
		addCodexFunction(AlchemancyProperties.ENERGY_SAPPER, PropertyFunction.ON_ATTACK, "Consumes a portion of the target player's Hunger.");
		addCodexFunction(AlchemancyProperties.ENERGY_SAPPER, PropertyFunction.WHILE_EQUIPPED, "Has a 20% chance every second to consume some of the user's Hunger in order to replenish the item's durability by 1 point, as long as the item has 10 or more points of durability consumed.");

		addCodexFlavor(AlchemancyProperties.LIGHT_SEEKING, "Steals the spotlight");
		addCodexFunction(AlchemancyProperties.LIGHT_SEEKING, PropertyFunction.WHEN_SHOT, "Homes in on the nearest entity on fire or with the Glowing effect in a 24-block radius.");
		addCodexFunction(AlchemancyProperties.LIGHT_SEEKING, PropertyFunction.WHEN_DROPPED, "Pulls itself towards the nearest entity on fire or with the Glowing effect in a 24-block radius.");

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

		addCodexFlavor(AlchemancyProperties.HOME_RUN, "And the crowd goes wild!");
		addCodexFunction(AlchemancyProperties.HOME_RUN, PropertyFunction.MODIFY_DAMAGE, "Knocks the target back a great distance if the attack deals at least 80% of the user's {attribute Attack Damage} stat.");
		addCodexFunction(AlchemancyProperties.HOME_RUN, PropertyFunction.ACTIVATE_BY_BLOCK, "Knocks the target back a great distance away from the item.");
		addCodexFunction(AlchemancyProperties.HOME_RUN, PropertyFunction.ATTRIBUTE_MODIFIER, "Sets the item's base {attribute Attack Speed} to 0.2");

		addCodexFlavor(AlchemancyProperties.WARPED, "A glimpse into my twisted mind");
		addCodexFunction(AlchemancyProperties.WARPED, PropertyFunction.OTHER, "Alters every compatible {system Infusion} on the item at the end of the {system Infusion Process}, often turning them into an opposite, or related counterpart.");

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

		addCodexFlavor(AlchemancyProperties.REVEALED, "Know what's coming");
		addCodexFunction(AlchemancyProperties.REVEALED, PropertyFunction.VISUAL, "Shows the item's {system Dormant Properties} in its tooltip, making it easier to distinguish what {system Infusions} the item will provide.");

		addCodexFlavor(AlchemancyProperties.REVEALING, "An Alchemancer's greatest asset");
		addCodexFunction(AlchemancyProperties.REVEALING, PropertyFunction.WHILE_WORN_HELMET, "Shows every item's {system Dormant Properties} in their tooltips, making it easier to distinguish what {system Infusions} the item will provide.");

		addCodexFlavor(AlchemancyProperties.SCRAMBLED, "§kPractically illegible");
		addCodexFunction(AlchemancyProperties.SCRAMBLED, PropertyFunction.VISUAL, "Obfuscates the item's tooltip, making its important traits and information hard to make out.");

		addCodexFlavor(AlchemancyProperties.TINTED, "Bring color to the world");
		addCodexFunction(AlchemancyProperties.TINTED, PropertyFunction.WHEN_USED_BLOCK, "If used on a water-filled {item Cauldron}, the item's tint will be cleared, removing this {system Infusion} in the process.");
		addCodexFunction(AlchemancyProperties.TINTED, PropertyFunction.VISUAL, "Applies a colored tint to the item.");
		addCodexFunction(AlchemancyProperties.TINTED, PropertyFunction.OTHER, "When a {item Dye} is {system Infused} onto an item, its color will be blended with the item's current tint. " +
				"If a {item Chroma Lens} is used instead, the item's tint will be overridden without consuming the lens. " +
				"When multiple {item Chroma Lenses} are {system Infused} at a time, the item's tint color will gradually cycle through each of the {system Infused} colors.");

		addCodexFlavor(AlchemancyProperties.SEETHROUGH, "Partially invisible");
		addCodexFunction(AlchemancyProperties.SEETHROUGH, PropertyFunction.VISUAL, "Makes the item visually transparent.");

		addCodexFlavor(AlchemancyProperties.CONCEALED, "Completely invisible");
		addCodexFunction(AlchemancyProperties.CONCEALED, PropertyFunction.VISUAL, "Prevents the item from rendering outside of the inventory screen.");

		addCodexFlavor(AlchemancyProperties.DISGUISED, "Intruder alert! Red Spy is in the base");
		addCodexFunction(AlchemancyProperties.DISGUISED, PropertyFunction.STACKED_OVER, "Sets the item's disguise to the one being stacked on top of it if no disguise has been assigned yet.");
		addCodexFunction(AlchemancyProperties.DISGUISED, PropertyFunction.WHEN_USED, "While crouching, and if no disguise has been set yet, sets the item's disguise to the one in the user's offhand.");
		addCodexFunction(AlchemancyProperties.DISGUISED, PropertyFunction.VISUAL, "Allows the item to take up the appearance of any other item.");

		addCodexFlavor(AlchemancyProperties.FLATTENED, "Great for starting a poster collection");
		addCodexFunction(AlchemancyProperties.FLATTENED, PropertyFunction.WHEN_USED_BLOCK, "Places the item onto a solid surface, which can then be picked back up by punching it.");
		addCodexFunction(AlchemancyProperties.FLATTENED, PropertyFunction.VISUAL, "Makes the item appear completely flat.");

		addCodexFlavor(AlchemancyProperties.SPARKLING, "Pretty particles");
		addCodexFunction(AlchemancyProperties.SPARKLING, PropertyFunction.WHILE_EQUIPPED, "Causes the user to emit an aura of particles. Some {system Infusions} disable this behavior, but instead have their own effects be overridden.");
		addCodexFunction(AlchemancyProperties.FLATTENED, PropertyFunction.OTHER, "The particles emitted can be determined by the {system Infusions} added to the source of {property alchemancy:sparkling} when {system Infused}.");

		addCodexFlavor(AlchemancyProperties.NONLETHAL, "As deadly as a pillow");
		addCodexFunction(AlchemancyProperties.NONLETHAL, PropertyFunction.MODIFY_DAMAGE, "Negates all damage dealt by this item, while still triggering {function on_attack} effects.");

		addCodexFlavor(AlchemancyProperties.FLIMSY, "Barely sturdier than a wet noodle");
		addCodexFunction(AlchemancyProperties.FLIMSY, PropertyFunction.OTHER, "Makes the item incapable of breaking blocks.");

		addCodexFlavor(AlchemancyProperties.DEAD, "A husk of its former self");
		addCodexFunction(AlchemancyProperties.DEAD, PropertyFunction.OTHER, "Disables most of the item's base attributes and abilities, including {attribute Attribute Modifiers}, block-breaking efficiencies, right-click functionality, food effects, and {system Innate Properties}. Does not negate the effects of {system Infusions}");

		addCodexFlavor(AlchemancyProperties.UNDEAD, "Nearly almost dead but not quite");
		addCodexFunction(AlchemancyProperties.UNDEAD, PropertyFunction.DURABILITY_CONSUMED, "Damaging the item restores durability points instead of consuming them, breaking once the item gets fully repaired.");

		addCodexFlavor(AlchemancyProperties.INFECTED, "You don't want it on your lawn");
		addCodexFunction(AlchemancyProperties.INFECTED, PropertyFunction.WHILE_IN_INVENTORY, "Slowly spreads itself onto other items in the user's inventory. Has a 0.2% chance every second to turn into {property alchemancy:dead}.");
		addCodexFunction(AlchemancyProperties.INFECTED, PropertyFunction.ON_ATTACK, "Spreads itself onto a random item in the target's inventory.");
		addCodexFunction(AlchemancyProperties.INFECTED, PropertyFunction.VISUAL, "Tints the item with an ugly color.");

		addCodexFlavor(AlchemancyProperties.SANITIZED, "Squeaky clean");
		addCodexFunction(AlchemancyProperties.SANITIZED, PropertyFunction.OTHER, "Prevents the item from becoming {property alchemancy:infected}.");
	}

	protected void addCodexFlavor(Holder<Property> propertyHolder, String text) {

		String translationKey = "infusion_codex.%s.flavor".formatted(propertyHolder.getRegisteredName());
		add(translationKey, text);
		CodexEntryProvider.ENTRIES.put(propertyHolder, new CodexEntryReloadListenener.CodexEntry(Component.translatable(translationKey), new ArrayList<>()));
	}

	protected void addCodexFunction(Holder<Property> propertyHolder, PropertyFunction function, String text) {
		add("infusion_codex.%s.%s".formatted(propertyHolder.getRegisteredName(), function.localizationKey), text);

		if (CodexEntryProvider.ENTRIES.containsKey(propertyHolder)) {
			CodexEntryProvider.ENTRIES.get(propertyHolder).functions().add(function);
			if (function == PropertyFunction.WHILE_ROOTED)
				CodexEntryProvider.addRelatedProperties(AlchemancyProperties.ROOTED, List.of(propertyHolder));
			else if (function == PropertyFunction.ON_ATTACK) {
				CodexEntryProvider.addRelatedProperties(AlchemancyProperties.GUST_JET, List.of(propertyHolder));
				CodexEntryProvider.addRelatedProperties(AlchemancyProperties.JAGGED, List.of(propertyHolder));
			} else if (function == PropertyFunction.WHEN_SHOT) {
				CodexEntryProvider.addRelatedProperties(AlchemancyProperties.THROWABLE, List.of(propertyHolder));
				CodexEntryProvider.addRelatedProperties(AlchemancyProperties.SHARPSHOOTING, List.of(propertyHolder));
			}
			if (function == PropertyFunction.WHEN_HIT_WORN_OR_USING ||
					function == PropertyFunction.WHEN_HIT_USING ||
					function == PropertyFunction.RECEIVE_DAMAGE_USING ||
					function == PropertyFunction.RECEIVE_DAMAGE_WORN_OR_USING) {
				CodexEntryProvider.addRelatedProperties(propertyHolder, List.of(
						AlchemancyProperties.ROCKET_POWERED, //TODO Maybe make this data driven or something
						AlchemancyProperties.SHARPSHOOTING,
						AlchemancyProperties.SHIELDING,
						AlchemancyProperties.CEASELESS_VOID,
						AlchemancyProperties.BRUSHING,
						AlchemancyProperties.EDIBLE,
						AlchemancyProperties.MAGNETIC));
			}
			if (function == PropertyFunction.WHILE_WORN_HELMET || function == PropertyFunction.WHILE_WORN ||
					function == PropertyFunction.WHILE_EQUIPPED ||
					function == PropertyFunction.RECEIVE_DAMAGE_EQUIPPED ||
					function == PropertyFunction.RECEIVE_DAMAGE_WORN ||
					function == PropertyFunction.RECEIVE_DAMAGE_WORN_OR_USING ||
					function == PropertyFunction.WHEN_HIT_EQUIPPED ||
					function == PropertyFunction.WHEN_HIT_WORN ||
					function == PropertyFunction.WHEN_HIT_WORN_OR_USING ||
					function == PropertyFunction.PICK_UP_WHILE_EQUIPPED) {
				CodexEntryProvider.addRelatedProperties(AlchemancyProperties.HEADWEAR, List.of(propertyHolder));
			}
		}
	}
}
