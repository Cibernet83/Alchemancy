package net.cibernet.alchemancy.mixin;

import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
import net.cibernet.alchemancy.registries.AlchemancyProperties;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractContainerMenu.class)
public abstract class AbstractContainerMenuMixin
{
	@Shadow public abstract ItemStack getCarried();

	@Inject(method = "doClick", at = @At("HEAD"), cancellable = true)
	public void doClick(int slotId, int button, ClickType clickType, Player player, CallbackInfo ci)
	{
		if(slotId == -999 && InfusedPropertiesHelper.hasProperty(getCarried(), AlchemancyProperties.STICKY))
			ci.cancel();
	}
}
