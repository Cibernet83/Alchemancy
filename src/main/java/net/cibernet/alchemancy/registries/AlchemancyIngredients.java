package net.cibernet.alchemancy.registries;

import com.mojang.serialization.MapCodec;
import net.cibernet.alchemancy.crafting.ingredient.WaterBottleIngredient;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.neoforge.common.crafting.IngredientType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import static net.cibernet.alchemancy.Alchemancy.MODID;

public class AlchemancyIngredients
{

	public static final DeferredRegister<IngredientType<?>> REGISTRY = DeferredRegister.create(NeoForgeRegistries.INGREDIENT_TYPES, MODID);

	public static final DeferredHolder<IngredientType<?>, IngredientType<WaterBottleIngredient>> WATER_BOTTLE = REGISTRY.register("water_bottle", () -> new IngredientType<>(
			MapCodec.unit(new WaterBottleIngredient()),
			StreamCodec.unit(new WaterBottleIngredient())
	));
}
