package net.cibernet.alchemancy.properties;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.gameevent.GameEvent;
import net.neoforged.neoforge.common.IShearable;
import net.neoforged.neoforge.common.ItemAbility;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

import java.util.List;
import java.util.Set;

public class ShearingProperty extends ToolProperty
{
	public ShearingProperty(int color, TagKey<Block> allowedBlocks, Set<ItemAbility> abilities) {
		super(color, allowedBlocks, abilities);
	}

	public ShearingProperty(int color, List<RuleFunc> toolRules, Set<ItemAbility> abilities) {
		super(color, toolRules, abilities);
	}

	@Override
	public void onRightClickEntity(PlayerInteractEvent.EntityInteract event)
	{
		Entity entity = event.getTarget();
		Player player = event.getEntity();
		ItemStack stack = entity.getWeaponItem();

		if(entity instanceof IShearable target)
		{
			if(entity.level().isClientSide())
				event.setCancellationResult(InteractionResult.CONSUME);
			else
			{
				BlockPos pos = entity.blockPosition();
				if (target.isShearable(player, stack, entity.level(), pos)) {
					target.onSheared(player, stack, entity.level(), pos)
							.forEach(drop -> target.spawnShearedDrop(entity.level(), pos, drop));
					entity.gameEvent(GameEvent.SHEAR, player);
					stack.hurtAndBreak(1, player, event.getHand() == net.minecraft.world.InteractionHand.MAIN_HAND ? net.minecraft.world.entity.EquipmentSlot.MAINHAND : net.minecraft.world.entity.EquipmentSlot.OFFHAND);
				}
				event.setCancellationResult(InteractionResult.SUCCESS);
			}
			event.setCanceled(true);
		}
	}
}
