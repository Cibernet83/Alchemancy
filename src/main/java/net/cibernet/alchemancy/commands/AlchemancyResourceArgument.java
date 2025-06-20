package net.cibernet.alchemancy.commands;

import com.google.gson.JsonObject;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.cibernet.alchemancy.Alchemancy;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.ResourceArgument;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

import static net.minecraft.commands.SharedSuggestionProvider.matchesSubStr;

public class AlchemancyResourceArgument<T> extends ResourceArgument<T> {

	private final HolderLookup<T> registryLookup;
	private final ResourceKey<? extends Registry> registryKey;

	private AlchemancyResourceArgument(CommandBuildContext context, ResourceKey<? extends Registry<T>> registryKey) {
		super(context, registryKey);
		registryLookup = context.lookupOrThrow(registryKey);
		this.registryKey = registryKey;
	}

	public static <T> AlchemancyResourceArgument<T> resource(CommandBuildContext context, ResourceKey<? extends Registry<T>> registryKey) {
		return new AlchemancyResourceArgument<>(context, registryKey);
	}

	@Override
	public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
		return suggestResource(this.registryLookup.listElementIds().map(ResourceKey::location), builder);
	}

	private static CompletableFuture<Suggestions> suggestResource(Stream<ResourceLocation> resources, SuggestionsBuilder builder) {
		String s = builder.getRemaining().toLowerCase(Locale.ROOT);
		filterResources(resources::iterator, s, p_82966_ -> p_82966_, p_82925_ -> builder.suggest(p_82925_.toString()));
		return builder.buildFuture();
	}

	private static <T> void filterResources(Iterable<T> resources, String input, Function<T, ResourceLocation> locationFunction, Consumer<T> resourceConsumer) {
		boolean flag = input.indexOf(58) > -1;

		for (T t : resources) {
			ResourceLocation resourcelocation = locationFunction.apply(t);
			if (flag) {
				String s = resourcelocation.toString();
				if (matchesSubStr(input, s)) {
					resourceConsumer.accept(t);
				}
			} else if (matchesSubStr(input, resourcelocation.getNamespace())
					|| resourcelocation.getNamespace().equals(Alchemancy.MODID) && matchesSubStr(input, resourcelocation.getPath())) {
				resourceConsumer.accept(t);
			}
		}
	}

	public static class Info<T> implements ArgumentTypeInfo<AlchemancyResourceArgument<T>, AlchemancyResourceArgument.Info<T>.Template> {
		public void serializeToNetwork(AlchemancyResourceArgument.Info<T>.Template template, FriendlyByteBuf buffer) {
			buffer.writeResourceKey(template.registryKey);
		}

		public AlchemancyResourceArgument.Info<T>.Template deserializeFromNetwork(FriendlyByteBuf buffer) {
			return new AlchemancyResourceArgument.Info.Template(buffer.readRegistryKey());
		}

		public void serializeToJson(AlchemancyResourceArgument.Info<T>.Template template, JsonObject json) {
			json.addProperty("registry", template.registryKey.location().toString());
		}

		public AlchemancyResourceArgument.Info<T>.Template unpack(AlchemancyResourceArgument<T> argument) {
			return new AlchemancyResourceArgument.Info.Template(argument.registryKey);
		}

		public final class Template implements ArgumentTypeInfo.Template<AlchemancyResourceArgument<T>> {
			final ResourceKey<? extends Registry<T>> registryKey;

			Template(ResourceKey<? extends Registry<T>> registryKey) {
				this.registryKey = registryKey;
			}

			public AlchemancyResourceArgument<T> instantiate(CommandBuildContext context) {
				return new AlchemancyResourceArgument<>(context, this.registryKey);
			}

			@Override
			public ArgumentTypeInfo<AlchemancyResourceArgument<T>, ?> type() {
				return AlchemancyResourceArgument.Info.this;
			}
		}
	}
}
