package net.cibernet.alchemancy.mixin.accessors;

import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.world.level.block.AbstractCauldronBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AbstractCauldronBlock.class)
public interface AbstractCauldronAccessor
{
	@Accessor("interactions")
	CauldronInteraction.InteractionMap getInteractions();
}
