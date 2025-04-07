package net.cibernet.alchemancy.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
import net.cibernet.alchemancy.properties.special.BindingProperty;
import net.cibernet.alchemancy.registries.AlchemancyProperties;
import net.minecraft.core.NonNullList;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(AbstractContainerMenu.class)
public abstract class ContainerMenuMixin
{
	@Shadow @Final public NonNullList<Slot> slots;

	@Shadow public abstract ItemStack getCarried();

	@Shadow public abstract void slotsChanged(Container container);

	@WrapMethod(method = "doClick")
	public void doClick(int slotId, int button, ClickType clickType, Player player, Operation<Void> original)
	{
		original.call(slotId, button, clickType, player);
		
		if(button != 0 && slotId >= 0 && slotId < slots.size() && InfusedPropertiesHelper.hasProperty(getCarried(), AlchemancyProperties.BINDING))
			BindingProperty.toggleBind(slots.get(slotId).getItem());
	}
}
