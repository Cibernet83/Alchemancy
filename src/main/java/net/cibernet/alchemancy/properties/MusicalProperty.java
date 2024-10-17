package net.cibernet.alchemancy.properties;

import net.cibernet.alchemancy.Alchemancy;
import net.cibernet.alchemancy.blocks.blockentities.RootedItemBlockEntity;
import net.cibernet.alchemancy.events.handler.GeneralEventHandler;
import net.cibernet.alchemancy.registries.AlchemancyBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;

public class MusicalProperty extends Property
{
	public static final HashMap<TagKey<Item>, SoundEvent> ON_CLICK_SOUNDS = new HashMap<>()
	{{
		for(NoteBlockInstrument instrument : NoteBlockInstrument.values())
		{
			put(ItemTags.create(ResourceLocation.fromNamespaceAndPath(Alchemancy.MODID, "musical_instruments/" + instrument.getSerializedName())), instrument.getSoundEvent().value());
		}
	}};

	@Override
	public void onEquippedTick(LivingEntity user, EquipmentSlot slot, ItemStack stack)
	{
		Level level = user.level();
		if(slot == EquipmentSlot.FEET && user.onGround())
		{
			double hSpeed = user.getKnownMovement().horizontalDistance();

			if(hSpeed > 0.05 && user.tickCount % (int) (8 - Math.min(7, (hSpeed * 10))) == 0)
			{
				if(!level.isClientSide() || (user.isControlledByLocalInstance() || (user instanceof Player player && player.isLocalPlayer())))
				{
					BlockPos standingOnPos = user.blockPosition();
					standingOnPos = user.level().getBlockState(standingOnPos).getCollisionShape(user.level(), standingOnPos).isEmpty() ? user.getBlockPosBelowThatAffectsMyMovement() : standingOnPos;
					playSound(user, level, user.position(), level.getBlockState(standingOnPos).instrument().getSoundEvent().value(), (float) ((hSpeed * 80 - 12) / 12.0f));
				}

			}
		}
	}

	@Override
	public void onRightClickItem(PlayerInteractEvent.RightClickItem event)
	{
		Level level = event.getLevel();
		if(!level.isClientSide())
			playSound(event.getEntity(), level, event.getEntity().getEyePosition(), getInstrumentFromItem(event.getItemStack()), event.getEntity().getXRot() / -90f);
	}

	@Override
	public void onRightClickBlock(PlayerInteractEvent.RightClickBlock event)
	{
		Level level = event.getLevel();
		if(!level.isClientSide())
			playSound(event.getEntity(), level, event.getEntity().getEyePosition(), getInstrumentFromItem(event.getItemStack()), event.getEntity().getXRot() / -90f);
	}

	private void playSound(@Nullable Entity source, Level pLevel, Vec3 pPos, SoundEvent sound, float i)
	{
		float f = (float)Math.pow(2.0D, i);
		pLevel.playSound(null, pPos.x, pPos.y, pPos.z, sound, SoundSource.RECORDS, 3.0F, f);

		if(!pLevel.isClientSide())
			((ServerLevel)pLevel).sendParticles(ParticleTypes.NOTE, pPos.x(), pPos.y(), pPos.z(), 1,0, 0.0D, 0.0D, (double)i / 24.0D);
	}

	@Override
	public void onRootedTick(RootedItemBlockEntity root, List<LivingEntity> entitiesInBounds)
	{
		if(root.getTickCount() % 20 == 0)
			playSound(null, root.getLevel(), root.getBlockPos().getBottomCenter(), getInstrumentFromItem(root.getItem()), (root.getLevel().random.nextInt(24) - 12) / 12f);
	}

	@Override
	public void onRootedAnimateTick(RootedItemBlockEntity root, RandomSource random)
	{
		if(root.getTickCount() % 20 == 0)
		{
			Vec3 pos = root.getBlockPos().getBottomCenter();
			Level level = root.getLevel();

			level.addParticle(ParticleTypes.NOTE, pos.x, pos.y + random.nextDouble(), pos.z, 0.0D, 0.0D, 0.0D);
		}
	}

	public static SoundEvent getInstrumentFromItem(ItemStack stack)
	{
		for(TagKey<Item> tag : ON_CLICK_SOUNDS.keySet())
			if(stack.is(tag))
				return ON_CLICK_SOUNDS.get(tag);

		if(stack.getItem() instanceof BlockItem blockItem)
			return blockItem.getBlock().defaultBlockState().instrument().getSoundEvent().value();

		return NoteBlockInstrument.HARP.getSoundEvent().value();
	}

	@Override
	public int getColor(ItemStack stack) {
		return 0xB38EF3;
	}
}
