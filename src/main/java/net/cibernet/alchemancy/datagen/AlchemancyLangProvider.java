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

		addCodexFlavor(AlchemancyProperties.GILDED, ""); //TODO
		addCodexFunction(AlchemancyProperties.GILDED, PropertyFunction.WHILE_HELD_MAINHAND, "Increases the user's {attribute Mining Speed} by 50%.");
		addCodexFunction(AlchemancyProperties.GILDED, PropertyFunction.WHILE_WORN, "Prevents Piglins from immediately attacking the user.");
		addCodexFunction(AlchemancyProperties.GILDED, PropertyFunction.WHEN_DROPPED, "Makes Piglins want to pick up the item.");

		addCodexFlavor(AlchemancyProperties.LUSTROUS, ""); //TODO Shiny reference
		addCodexFunction(AlchemancyProperties.LUSTROUS, PropertyFunction.ATTRIBUTE_MODIFIER, "Sets the item's tool material to {item Diamond} tier. Doubles the item's durability up to a maximum total of 1600.");

		addCodexFlavor(AlchemancyProperties.WEALTHY, ""); //TODO
		addCodexFunction(AlchemancyProperties.WEALTHY, PropertyFunction.WHILE_EQUIPPED, "Makes Villagers and Pillagers follow the user.");
		addCodexFunction(AlchemancyProperties.WEALTHY, PropertyFunction.ATTRIBUTE_MODIFIER, "Increases the item's {enchantment minecraft:fortune} and {enchantment minecraft:looting} levels by 1.");

		addCodexFlavor(AlchemancyProperties.REINFORCED, "");
		addCodexFunction(AlchemancyProperties.REINFORCED, PropertyFunction.ATTRIBUTE_MODIFIER, "Increases the item's {attribute Armor} value by 3 and its {attribute Armor Toughness} by 1.");

		addCodexFlavor(AlchemancyProperties.PRISTINE, "");
		addCodexFunction(AlchemancyProperties.PRISTINE, PropertyFunction.DURABILITY_CONSUMED, "Consumes 1 {property alchemancy:pristine} point instead of taking damage. The Infusion is removed after 100 {property alchemancy:pristine} points are consumed.");

		addCodexFlavor(AlchemancyProperties.HELLBENT, "");
		addCodexFunction(AlchemancyProperties.HELLBENT, PropertyFunction.MODIFY_DAMAGE, "Makes attacks always crit, triggering {function on_crit} effects.");
		addCodexFunction(AlchemancyProperties.HELLBENT, PropertyFunction.WHEN_SHOT, "Triggers {function on_crit} effects when hitting an entity.");
		addCodexFunction(AlchemancyProperties.HELLBENT, PropertyFunction.BLOCK_DESTROYED, "Increases the user's {attribute Mining Speed} for each block of the same type destroyed, up to an additional total of 20%. The speed boost is lost when a different type of block is mined or another item is held.");

		addCodexFlavor(AlchemancyProperties.DEPTH_DWELLER, "Yearn for the mines");
		addCodexFunction(AlchemancyProperties.DEPTH_DWELLER, PropertyFunction.WHILE_WORN_LOWER, "Increases the user's {attribute Movement Speed} relative to how low down they are in the world starting at Y=10, with a maximum increase of 50%. While in {nether The Nether}, the {attribute Movement Speed} boost is always 200%");
		addCodexFunction(AlchemancyProperties.DEPTH_DWELLER, PropertyFunction.WHILE_HELD_MAINHAND, "Increases the user's {attribute Mining Speed} relative to how low down they are in the world starting at Y=10, with a maximum increase of 50%. While in {nether The Nether}, the {attribute Mining Speed} boost is always 200%");

		addCodexFlavor(AlchemancyProperties.MALLEABLE, "Great for Stop Motion Animation");
		addCodexFunction(AlchemancyProperties.MALLEABLE, PropertyFunction.ON_DESTROYED, "The item drops an {item alchemancy:unshaped_clay}, which can be combined with a {item Clay Ball} or smelted down to restore the item.");

		addCodexFlavor(AlchemancyProperties.HARDENED, "");
		addCodexFunction(AlchemancyProperties.HARDENED, PropertyFunction.DURABILITY_CONSUMED, "Halves the amount of durability consumed by the item, or has a 10% chance not to consume any durability if the amount consumed is 1. ");
		addCodexFunction(AlchemancyProperties.HARDENED, PropertyFunction.WHEN_SHOT, "Allows the projectile to break certain blocks on impact, such as {item Glass}, {item Ice} or {item Decorated Pots}.");

		addCodexFlavor(AlchemancyProperties.CRACKED, "");
		addCodexFunction(AlchemancyProperties.CRACKED, PropertyFunction.DURABILITY_CONSUMED, "40% chance to consume double durability.");
		addCodexFunction(AlchemancyProperties.CRACKED, PropertyFunction.ACTIVATE, "Breaks the item or consumes 1 durability point.");

		addCodexFlavor(AlchemancyProperties.MINING, "");
		addCodexFunction(AlchemancyProperties.MINING, PropertyFunction.ATTRIBUTE_MODIFIER, "Allows the item to break Pickaxe-related blocks, starting at wooden tier if the item isn't already a tool.");
		addCodexFunction(AlchemancyProperties.MINING, PropertyFunction.WHEN_SHOT, "Breaks Pickaxe-related blocks on impact. The faster the projectile goes, the more likely it is to continue its trajectory after breaking a block.");

		addCodexFlavor(AlchemancyProperties.CHOPPING, "");
		addCodexFunction(AlchemancyProperties.CHOPPING, PropertyFunction.ATTRIBUTE_MODIFIER, "Allows the item to break Axe-related blocks, starting at wooden tier if the item isn't already a tool.");
		addCodexFunction(AlchemancyProperties.CHOPPING, PropertyFunction.WHEN_USED_BLOCK, "Strips {item Logs} and removes wax from {item Copper Blocks}");
		addCodexFunction(AlchemancyProperties.CHOPPING, PropertyFunction.WHEN_SHOT, "Breaks Axe-related blocks on impact. The faster the projectile goes, the more likely it is to continue its trajectory after breaking a block.");

		addCodexFlavor(AlchemancyProperties.DIGGING, "Diggy diggy hole, digging a hole");
		addCodexFunction(AlchemancyProperties.DIGGING, PropertyFunction.ATTRIBUTE_MODIFIER, "Allows the item to break Shovel-related blocks, starting at wooden tier if the item isn't already a tool.");
		addCodexFunction(AlchemancyProperties.DIGGING, PropertyFunction.WHEN_USED_BLOCK, "Can turn {item Dirt} into {item Dirt Path} and put out {item Campfires}");
		addCodexFunction(AlchemancyProperties.DIGGING, PropertyFunction.WHEN_SHOT, "Breaks Shovel-related blocks on impact. The faster the projectile goes, the more likely it is to continue its trajectory after breaking a block.");

		addCodexFlavor(AlchemancyProperties.REAPING, "");
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

//		addCodexFlavor(AlchemancyProperties., "");
//		addCodexFunction(AlchemancyProperties., PropertyFunction., "");

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
