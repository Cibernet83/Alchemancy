package net.cibernet.alchemancy.properties.special;

import net.cibernet.alchemancy.properties.Property;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.registries.DeferredItem;

import java.util.Collection;
import java.util.List;

public class SoundEffectProperty extends Property {
	private final int color;
	private final SoundEvent sound;
	private final boolean hidden;

	public SoundEffectProperty(int color, SoundEvent sound, boolean hidden) {
		this.color = color;
		this.sound = sound;
		this.hidden = hidden;
	}

	@Override
	public void onRightClickItem(PlayerInteractEvent.RightClickItem event)
	{
		Level level = event.getLevel();
		if(!level.isClientSide())
		{
			playSound(event.getEntity(), level, event.getEntity().getEyePosition());
		}
	}

	@Override
	public void onActivation(@org.jetbrains.annotations.Nullable Entity source, Entity target, ItemStack stack, DamageSource damageSource)
	{
		Level level = target.level();
		if(!level.isClientSide())
			playSound(target, level, target.getEyePosition());
	}

	private void playSound(Entity user, Level level, Vec3 eyePosition) {


		level.playSound(null, eyePosition.x, eyePosition.y, eyePosition.z, sound, SoundSource.RECORDS, 3.0F, 1);

		if(level instanceof ServerLevel serverLevel)
			serverLevel.sendParticles(ParticleTypes.NOTE, eyePosition.x(), eyePosition.y(), eyePosition.z(), 1,0, 0.0D, 0.0D, 1);

	}

	@Override
	public int getColor(ItemStack stack) {
		return color;
	}

	@Override
	public Collection<ItemStack> populateCreativeTab(DeferredItem<Item> capsuleItem, Holder<Property> holder) {
		return hidden ? List.of() : super.populateCreativeTab(capsuleItem, holder);
	}
}
