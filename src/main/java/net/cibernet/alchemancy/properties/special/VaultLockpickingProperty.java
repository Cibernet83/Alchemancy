package net.cibernet.alchemancy.properties.special;

import net.cibernet.alchemancy.item.components.PropertyModifierComponent;
import net.cibernet.alchemancy.mixin.accessors.VaultServerDataAccessor;
import net.cibernet.alchemancy.properties.Property;
import net.cibernet.alchemancy.registries.AlchemancyProperties;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.vault.VaultBlockEntity;
import net.neoforged.neoforge.event.entity.player.UseItemOnBlockEvent;

import java.util.UUID;

public class VaultLockpickingProperty extends Property {

	@Override
	public void onRightClickBlock(UseItemOnBlockEvent event) {

		Player player = event.getPlayer();
		if (player == null || event.getLevel().isClientSide()) return;

		ItemStack stack = event.getItemStack();
		Level level = event.getLevel();
		BlockPos pos = event.getPos();
		UUID playerUuid = event.getPlayer().getUUID();

		if (level.getBlockEntity(pos) instanceof VaultBlockEntity vault) {

			var vaultData = vault.getServerData();
			if (vaultData == null) return;

			var rewarded = ((VaultServerDataAccessor) vaultData).invokeGetRewardedPlayers();
			if (rewarded.contains(playerUuid)) {
				event.getLevel().levelEvent(2001, pos, Block.getId(level.getBlockState(pos)));
				rewarded.removeIf(uuid -> uuid.equals(playerUuid));

				if (PropertyModifierComponent.getOrElse(stack, asHolder(), AlchemancyProperties.Modifiers.PREVENT_CONSUMPTION, stack.isDamageableItem())) {
					int durabilityConsumed = PropertyModifierComponent.getOrElse(stack, asHolder(), AlchemancyProperties.Modifiers.DURABILITY_CONSUMPTION, 50);
					stack.hurtAndBreak(durabilityConsumed, player, event.getHand() == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND);
				} else
					consumeItem(player, stack, event.getHand() == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND);
			}
		}

	}

	@Override
	public int getColor(ItemStack stack) {
		return 0x213542;
	}
}
