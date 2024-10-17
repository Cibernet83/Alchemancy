package net.cibernet.alchemancy.registries;

import net.cibernet.alchemancy.Alchemancy;
import net.cibernet.alchemancy.blocks.blockentities.EssenceContainer;
import net.cibernet.alchemancy.essence.Essence;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.Nullable;

public class AlchemancyEssence
{
	private static final ResourceLocation KEY = ResourceLocation.fromNamespaceAndPath(Alchemancy.MODID, "essence");
	public static final DeferredRegister<Essence> REGISTRY = DeferredRegister.create(KEY, Alchemancy.MODID);
	private static final Registry<Essence> SUPPLIER = REGISTRY.makeRegistry(registryBuilder -> registryBuilder.defaultKey(KEY).sync(true));

	public static final DeferredHolder<Essence, Essence> PYRO = REGISTRY.register("pyro", () -> new Essence(0xFF3F00));
	public static final DeferredHolder<Essence, Essence> AERO = REGISTRY.register("aero", () -> new Essence(0xFFF090));
	public static final DeferredHolder<Essence, Essence> TERRA = REGISTRY.register("terra", () -> new Essence(0x916A3A));
	public static final DeferredHolder<Essence, Essence> HYDRO = REGISTRY.register("hydro", () -> new Essence(0x0063DD));
	public static final DeferredHolder<Essence, Essence> ELECTRO = REGISTRY.register("electro", () -> new Essence(0x00FFFA));
	public static final DeferredHolder<Essence, Essence> CRYO = REGISTRY.register("cryo", () -> new Essence(0x72DBFF));
	public static final DeferredHolder<Essence, Essence> PLASMA = REGISTRY.register("plasma", () -> new Essence(0x65FF00));
	public static final DeferredHolder<Essence, Essence> DENDRO = REGISTRY.register("dendro", () -> new Essence(0x91C600));

	@Nullable
	public static Essence getEssence(ResourceLocation key)
	{
		return SUPPLIER.get(key);
	}

	public static ResourceLocation getKeyFor(Essence Essence)
	{
		return SUPPLIER.getKey(Essence);
	}
}
