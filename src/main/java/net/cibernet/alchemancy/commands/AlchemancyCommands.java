package net.cibernet.alchemancy.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
import net.cibernet.alchemancy.Alchemancy;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import net.minecraft.commands.synchronization.SingletonArgumentInfo;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

@EventBusSubscriber(modid = Alchemancy.MODID, bus = EventBusSubscriber.Bus.GAME)
public class AlchemancyCommands {

	@SubscribeEvent
	public static void registerCommands(final RegisterCommandsEvent event) {

		CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
		CommandBuildContext context = event.getBuildContext();
		InfuseCommand.register(dispatcher, context);
	}

	public static class Arguments
	{
		public static final DeferredRegister<ArgumentTypeInfo<?, ?>> REGISTRY = DeferredRegister.create(Registries.COMMAND_ARGUMENT_TYPE, Alchemancy.MODID);

		public static final DeferredHolder<ArgumentTypeInfo<?, ?>, AlchemancyResourceArgument.Info<Object>> RESOURCE =
				REGISTRY.register("resource", () -> ArgumentTypeInfos.registerByClass(fixClassType(AlchemancyResourceArgument.class), new AlchemancyResourceArgument.Info<Object>()));


		private static <T extends ArgumentType<?>> Class<T> fixClassType(Class<? super T> type) {
			return (Class<T>)type;
		}
	}
}
