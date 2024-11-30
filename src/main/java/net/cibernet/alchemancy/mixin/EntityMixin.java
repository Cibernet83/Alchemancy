package net.cibernet.alchemancy.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalFloatRef;
import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
import net.cibernet.alchemancy.registries.AlchemancyProperties;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public class EntityMixin
{
	@Inject(method = "playSound(Lnet/minecraft/sounds/SoundEvent;FF)V", at = @At("HEAD"))
	public void playSound(SoundEvent sound, float volume, float pitch, CallbackInfo ci, @Local(ordinal = 0, argsOnly = true)LocalFloatRef volumeRef)
	{
		boolean isPlayer = ((Object)this) instanceof Player;

		if(((Object)this) instanceof LivingEntity living)
		{
			float muffleMod = 1;
			for (EquipmentSlot slot : EquipmentSlot.values()) {
				if(slot.isArmor() && InfusedPropertiesHelper.hasInfusedProperty(living.getItemBySlot(slot), AlchemancyProperties.MUFFLED))
				{
					if(isPlayer)
						muffleMod -= 0.25f;
					else
					{
						muffleMod = 0;
						break;
					}
				}
			}
			volumeRef.set(volume * muffleMod);
		}
	}
}
