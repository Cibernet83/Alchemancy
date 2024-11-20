package net.cibernet.alchemancy.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.AdvancementProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class AdvancementDatagen extends AdvancementProvider
{
	/**
	 * Constructs an advancement provider using the generators to write the
	 * advancements to a file.
	 *
	 * @param output             the target directory of the data generator
	 * @param registries         a future of a lookup for registries and their objects
	 * @param existingFileHelper a helper used to find whether a file exists
	 * @param subProviders       the generators used to create the advancements
	 */
	public AdvancementDatagen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries, ExistingFileHelper existingFileHelper, List<AdvancementGenerator> subProviders)
	{
		super(output, registries, existingFileHelper, subProviders);
	}
}
