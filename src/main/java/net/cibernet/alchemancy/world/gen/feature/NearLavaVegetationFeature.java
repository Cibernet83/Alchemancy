package net.cibernet.alchemancy.world.gen.feature;

import com.mojang.serialization.Codec;
import net.cibernet.alchemancy.registries.AlchemancyTags;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NetherForestVegetationConfig;

public class NearLavaVegetationFeature  extends Feature<NetherForestVegetationConfig> {
	public NearLavaVegetationFeature(Codec<NetherForestVegetationConfig> p_66361_) {
		super(p_66361_);
	}

	@Override
	public boolean place(FeaturePlaceContext<NetherForestVegetationConfig> p_160068_) {
		WorldGenLevel worldgenlevel = p_160068_.level();
		BlockPos blockpos = p_160068_.origin();
		BlockState blockstate = worldgenlevel.getBlockState(blockpos.below());
		NetherForestVegetationConfig netherforestvegetationconfig = p_160068_.config();
		RandomSource randomsource = p_160068_.random();

		if (BlockPos.findClosestMatch(blockpos, 3, 3, pos -> worldgenlevel.getBlockState(pos).is(AlchemancyTags.Blocks.REQUIRED_FOR_BLAZEBLOOM_GENERATION)).isEmpty())
			return false;


		int i = blockpos.getY();
		if (i >= worldgenlevel.getMinBuildHeight() + 1 && i + 1 < worldgenlevel.getMaxBuildHeight()) {
			int j = 0;

			for (int k = 0; k < netherforestvegetationconfig.spreadWidth * netherforestvegetationconfig.spreadWidth; k++) {
				BlockPos blockpos1 = blockpos.offset(
						randomsource.nextInt(netherforestvegetationconfig.spreadWidth) - randomsource.nextInt(netherforestvegetationconfig.spreadWidth),
						randomsource.nextInt(netherforestvegetationconfig.spreadHeight) - randomsource.nextInt(netherforestvegetationconfig.spreadHeight),
						randomsource.nextInt(netherforestvegetationconfig.spreadWidth) - randomsource.nextInt(netherforestvegetationconfig.spreadWidth)
				);
				BlockState blockstate1 = netherforestvegetationconfig.stateProvider.getState(randomsource, blockpos1);
				if (worldgenlevel.isEmptyBlock(blockpos1)
						&& blockpos1.getY() > worldgenlevel.getMinBuildHeight()
						&& blockstate1.canSurvive(worldgenlevel, blockpos1)) {
					worldgenlevel.setBlock(blockpos1, blockstate1, 2);
					j++;
				}
			}

			return j > 0;
		} else {
			return false;
		}

	}
}
