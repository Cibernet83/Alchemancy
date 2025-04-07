package net.cibernet.alchemancy.mixin.client;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
import net.cibernet.alchemancy.properties.special.BindingProperty;
import net.cibernet.alchemancy.registries.AlchemancyProperties;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(CreativeModeInventoryScreen.class)
public class CreativeScreenMixin
{
	@WrapMethod(method = "slotClicked")
	public void slotClicked(Slot slot, int slotId, int mouseButton, ClickType type, Operation<Void> original)
	{
		original.call(slot, slotId, mouseButton, type);
		ItemStack carried = ((CreativeModeInventoryScreen)(Object)this).getMenu().getCarried();

		if(mouseButton != 0 && slotId >= 0 && InfusedPropertiesHelper.hasProperty(carried, AlchemancyProperties.BINDING))
			BindingProperty.toggleBind(slot.getItem());
	}
}
