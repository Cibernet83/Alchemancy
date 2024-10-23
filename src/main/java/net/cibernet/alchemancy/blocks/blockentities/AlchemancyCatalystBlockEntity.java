package net.cibernet.alchemancy.blocks.blockentities;

import net.cibernet.alchemancy.registries.AlchemancyBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class AlchemancyCatalystBlockEntity extends BlockEntity
{
	private float spinOffset = 0;
	private String crystalTexture = DyeColor.LIME.getName();
	private int tint = 0xFFFFFFFF;

	@OnlyIn(Dist.CLIENT)
	private float rotationTime = 0;
	@OnlyIn(Dist.CLIENT)
	private float prevRotationTime = 0;

	private int animationTicks = 0;

	public AlchemancyCatalystBlockEntity(BlockPos pos, BlockState blockState) {
		super(AlchemancyBlockEntities.ALCHEMANCY_CATALYST.get(), pos, blockState);
	}

	@Override
	protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries)
	{
		super.loadAdditional(tag, registries);

		if(tag.contains("spin_offset", Tag.TAG_FLOAT))
			spinOffset = tag.getFloat("spin_offset");

		if(tag.contains("crystal_texture", Tag.TAG_STRING))
			setCrystalTexture(tag.getString("crystal_texture"));

		if(tag.contains("tint", Tag.TAG_INT))
			setTint(tag.getInt("tint"));

		animationTicks = tag.getInt("animation_ticks");
	}

	@Override
	protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries)
	{
		super.saveAdditional(tag, registries);
		tag.putFloat("spin_offset", spinOffset);
		tag.putString("crystal_texture", getCrystalTexture());
		tag.putInt("tint", getTint());

		tag.putInt("animation_ticks", animationTicks);
	}

	public float getSpinOffset() {
		return spinOffset;
	}

	public void randomizeSpinOffset(RandomSource randomSource) {
		this.spinOffset = randomSource.nextFloat() * 360;
	}

	public int getTint() {
		return tint;
	}

	public String getCrystalTexture() {
		return crystalTexture;
	}

	public void setTint(int tint) {
		this.tint = tint;
		notifyColorUpdate();

	}

	public void setCrystalTexture(String crystalTexture) {
		this.crystalTexture = crystalTexture;
		notifyColorUpdate();
	}

	public void setCrystalTexture(DyeColor dyeColor)
	{
		setCrystalTexture(dyeColor.getName());
	}

	public void notifyColorUpdate()
	{
		if(hasLevel())
			level.markAndNotifyBlock(getBlockPos(), level.getChunkAt(getBlockPos()), getBlockState(), getBlockState(), 2, 1);
	}


	@Override
	public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
		CompoundTag tag = new CompoundTag();
		saveAdditional(tag, registries);

		return tag;
	}

	@Override
	public Packet<ClientGamePacketListener> getUpdatePacket() {
		// Will get tag from #getUpdateTag
		return ClientboundBlockEntityDataPacket.create(this);
	}

	public static void clientTick(Level level, BlockPos pos, BlockState state, AlchemancyCatalystBlockEntity crystal)
	{
		crystal.prevRotationTime = crystal.rotationTime;
		crystal.rotationTime += crystal.getAnimationSpeed();
		crystal.animationTicks = Math.max(0, crystal.animationTicks - 1);
	}

	public static void serverTick(Level level, BlockPos pos, BlockState state, AlchemancyCatalystBlockEntity crystal)
	{
		crystal.animationTicks = Math.max(0, crystal.animationTicks - 1);
	}

	private final int ANIMATION_LENGTH = 10;

	public void playAnimation(boolean startup)
	{
		animationTicks = ANIMATION_LENGTH;
		level.markAndNotifyBlock(getBlockPos(), level.getChunkAt(getBlockPos()), getBlockState(), getBlockState(), 2, 1);
		level.playSound(null, getBlockPos(), startup ? SoundEvents.BEACON_ACTIVATE : SoundEvents.RESPAWN_ANCHOR_CHARGE, SoundSource.BLOCKS, 1, 1);
	}

	public float getAnimationSpeed()
	{
		return Mth.lerp(getAnimationProgressLeft(0), 0.05f, 0.5f);
	}

	@OnlyIn(Dist.CLIENT)
	public float getRotationTime(float partialTicks)
	{
		return Mth.lerp(partialTicks, prevRotationTime, rotationTime);
	}

	public float getAnimationProgressLeft(float partialTick) {
		return Math.max(0, animationTicks - partialTick) / ANIMATION_LENGTH;
	}
}
