package net.cibernet.alchemancy.essence;

import com.mojang.serialization.Codec;
import net.cibernet.alchemancy.properties.Property;
import net.cibernet.alchemancy.registries.AlchemancyEssence;
import net.cibernet.alchemancy.registries.AlchemancyProperties;
import net.minecraft.core.Holder;
import net.minecraft.resources.RegistryFixedCodec;
import net.minecraft.resources.ResourceLocation;

public class Essence
{
	public static final Codec<Holder<Essence>> CODEC = RegistryFixedCodec.create(AlchemancyEssence.REGISTRY.getRegistryKey());

	public final int color;

	public Essence(int color) {
		this.color = color;
	}

	public ResourceLocation getKey() {
		return AlchemancyEssence.getKeyFor(this);
	}

	@Override
	public String toString() {
		return getKey().toString();
	}
}
