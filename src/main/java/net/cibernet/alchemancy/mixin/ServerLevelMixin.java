package net.cibernet.alchemancy.mixin;

import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
import net.cibernet.alchemancy.registries.AlchemancyProperties;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(ServerLevel.class)
public abstract class ServerLevelMixin
{

	@Inject(method = "findLightningRod", at = @At("HEAD"), cancellable = true)
	public void findLightningRod(BlockPos pos, CallbackInfoReturnable<Optional<BlockPos>> cir)
	{
		Vec3 vecPos = pos.getCenter();
		Player nearestPlayer = ((ServerLevel)(Object)this).getNearestPlayer(vecPos.x, vecPos.y, vecPos.z, 128, (entity) -> {
			if(entity instanceof LivingEntity player)
				for (EquipmentSlot slot : EquipmentSlot.values()) {
					if(InfusedPropertiesHelper.hasProperty(player.getItemBySlot(slot), AlchemancyProperties.CONDUCTIVE))
						return true;
				}
			return false;
		});

		if(nearestPlayer != null)
			cir.setReturnValue(Optional.of(nearestPlayer.blockPosition().above(1)));
	}
}
