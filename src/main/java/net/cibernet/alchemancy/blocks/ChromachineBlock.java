package net.cibernet.alchemancy.blocks;

import net.cibernet.alchemancy.client.screen.ChromaTintingScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
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

		if (level.isClientSide())
			Minecraft.getInstance().setScreen(new ChromaTintingScreen(stack));
		return InteractionResult.sidedSuccess(level.isClientSide());
	}
}
