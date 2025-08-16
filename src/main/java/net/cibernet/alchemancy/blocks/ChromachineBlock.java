package net.cibernet.alchemancy.blocks;

import net.cibernet.alchemancy.client.screen.ChromaTintingScreen;
import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
import net.cibernet.alchemancy.registries.AlchemancyItems;
import net.cibernet.alchemancy.registries.AlchemancyProperties;
import net.cibernet.alchemancy.registries.AlchemancyTags;
import net.cibernet.alchemancy.util.ClientUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class ChromachineBlock extends Block {
	public ChromachineBlock(Properties properties) {
		super(properties);
	}

	@Override
	protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {

		ItemStack stack = player.getMainHandItem();

		if(stack.isEmpty())
			return InteractionResult.PASS;
		if(stack.is(AlchemancyTags.Items.CANNOT_TINT))
		{
			player.displayClientMessage(Component.translatable("block.alchemancy.chromachine.cannot_tint"), true);
			return InteractionResult.PASS;
		}
		if(stack.is(AlchemancyTags.Items.IMMUNE_TO_INFUSIONS))
		{
			player.displayClientMessage(Component.translatable("block.alchemancy.chromachine.cannot_infuse"), true);
			return InteractionResult.PASS;
		}
		if(AlchemancyProperties.DISGUISED.get().hasData(stack))
		{
			player.displayClientMessage(Component.translatable("block.alchemancy.chromachine.cannot_tint_disguised"), true);
			return InteractionResult.PASS;
		}

		if (level.isClientSide())
			ClientUtil.openChromachineScreen(stack);
		return InteractionResult.sidedSuccess(level.isClientSide());
	}
}
