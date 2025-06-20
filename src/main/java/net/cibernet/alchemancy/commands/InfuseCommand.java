package net.cibernet.alchemancy.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
import net.cibernet.alchemancy.item.components.PropertyDataComponent;
import net.cibernet.alchemancy.properties.Property;
import net.cibernet.alchemancy.properties.data.IDataHolder;
import net.cibernet.alchemancy.registries.AlchemancyItems;
import net.cibernet.alchemancy.registries.AlchemancyProperties;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.CompoundTagArgument;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.NbtTagArgument;
import net.minecraft.commands.arguments.ResourceArgument;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import java.util.Collection;

public class InfuseCommand {

	private static final DynamicCommandExceptionType ERROR_NOT_LIVING_ENTITY = new DynamicCommandExceptionType(
			p_304205_ -> Component.translatableEscape("commands.alchemancy:infuse.failed.entity", p_304205_)
	);
	private static final DynamicCommandExceptionType ERROR_NO_ITEM = new DynamicCommandExceptionType(
			p_304207_ -> Component.translatableEscape("commands.alchemancy:infuse.failed.itemless", p_304207_)
	);
	private static final SimpleCommandExceptionType ERROR_NOTHING_HAPPENED = new SimpleCommandExceptionType(Component.translatable("commands.alchemancy:infuse.failed"));


	public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext context) {


		dispatcher.register(Commands.literal("infuse").requires(commandSourceStack -> commandSourceStack.hasPermission(2))
				.then(Commands.argument("targets", EntityArgument.entities())
						.then(
								Commands.argument("property", AlchemancyResourceArgument.resource(context, AlchemancyProperties.REGISTRY_KEY))
										.executes(
												commandContext -> infuse(
														commandContext.getSource(),
														EntityArgument.getEntities(commandContext, "targets"),
														ResourceArgument.getResource(commandContext, "property", AlchemancyProperties.REGISTRY_KEY),
														new CompoundTag()
												)
										)
										.then(Commands.argument("propertyData", CompoundTagArgument.compoundTag()).executes(
												commandContext -> infuse(
														commandContext.getSource(),
														EntityArgument.getEntities(commandContext, "targets"),
														ResourceArgument.getResource(commandContext, "property", AlchemancyProperties.REGISTRY_KEY),
														CompoundTagArgument.getCompoundTag(commandContext, "propertyData")
												)
										)))));

	}

	private static int infuse(CommandSourceStack source, Collection<? extends Entity> targets, Holder.Reference<Property> property, CompoundTag propertyData) throws CommandSyntaxException {

		int i = 0;
		for (Entity entity : targets) {
			if (entity instanceof LivingEntity livingentity) {
				ItemStack itemstack = livingentity.getMainHandItem();
				if (!itemstack.isEmpty()) {

					InfusedPropertiesHelper.addProperty(itemstack, property);

					if(!propertyData.isEmpty() && property.value() instanceof IDataHolder<?>) {
						PropertyDataComponent.Mutable data = new PropertyDataComponent.Mutable(itemstack.getOrDefault(AlchemancyItems.Components.PROPERTY_DATA, PropertyDataComponent.EMPTY));
						data.setDataNbt(property, propertyData);
						itemstack.set(AlchemancyItems.Components.PROPERTY_DATA, data.toImmutable());
					}

					i++;
				} else if (targets.size() == 1) {
					throw ERROR_NO_ITEM.create(livingentity.getName().getString());
				}
			} else if (targets.size() == 1) {
				throw ERROR_NOT_LIVING_ENTITY.create(entity.getName().getString());
			}
		}

		if (i == 0) {
			throw ERROR_NOTHING_HAPPENED.create();
		} else {
			if (targets.size() == 1) {
				source.sendSuccess(
						() -> Component.translatable(
								"commands.alchemancy:infuse.success.single", property.value().getName(), targets.iterator().next().getDisplayName()
						),
						true
				);
			} else {
				source.sendSuccess(
						() -> Component.translatable("commands.alchemancy:infuse.success.multiple", property.value().getName(), targets.size()),
						true
				);
			}

			return i;
		}
	}
}
