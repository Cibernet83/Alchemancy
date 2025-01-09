package net.cibernet.alchemancy.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.cibernet.alchemancy.events.handler.InfusedLootHandler;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LootTable.class)
public class LootTableMixin
{
	@Inject(method = "fill", at = @At(value = "INVOKE",
			target = "Lnet/minecraft/world/level/storage/loot/LootTable;shuffleAndSplitItems(Lit/unimi/dsi/fastutil/objects/ObjectArrayList;ILnet/minecraft/util/RandomSource;)V", shift = At.Shift.AFTER))
	public void fill(Container container, LootParams params, long seed, CallbackInfo ci, @Local ObjectArrayList<ItemStack> objectArrayList)
	{
		InfusedLootHandler.infuseRandomItems(params.getLevel(), params.getLuck(), objectArrayList);
	}

}
