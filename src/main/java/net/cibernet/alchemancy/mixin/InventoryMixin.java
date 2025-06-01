package net.cibernet.alchemancy.mixin;

import net.cibernet.alchemancy.data.save.InfusionCodexSaveData;
import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
import net.cibernet.alchemancy.properties.Property;
import net.cibernet.alchemancy.registries.AlchemancyItems;
import net.cibernet.alchemancy.registries.AlchemancyProperties;
import net.cibernet.alchemancy.registries.AlchemancyTags;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(Inventory.class)
public class InventoryMixin {

	@Shadow @Final public Player player;

	@Inject(method = "setItem", at = @At("TAIL"))
	public void setItem(int index, ItemStack stack, CallbackInfo ci) {
		if(player.level().isClientSide() && !stack.isEmpty() && !InfusionCodexSaveData.bypassesUnlocks()) {
			for (Holder<Property> propertyHolder : InfusionCodexSaveData.getPropertiesToUnlock(stack)) {
				InfusionCodexSaveData.unlock(propertyHolder);
			}

			List<Holder<Property>> dormants = List.of();
			if(stack.has(AlchemancyItems.Components.INNATE_PROPERTIES))
				dormants = AlchemancyProperties.getDormantProperties(stack);

			if(stack.is(AlchemancyTags.Items.CODEX_DISCOVERY_ON_PICKUP) || InfusedPropertiesHelper.getInnateProperties(stack).stream().anyMatch(dormants::contains))
				InfusionCodexSaveData.discoverItem(stack);
		}
	}

}
