package net.cibernet.alchemancy.mixin;

import net.cibernet.alchemancy.util.CommonUtils;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public class PlayerMixin
{
	@Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;isSpectator()Z", ordinal = 1, shift = At.Shift.BEFORE))
	public void tickAfterSpectatorCheck(CallbackInfo ci)
	{
		CommonUtils.tickInventoryItemProperties(((Player)(Object) this));
	}

}
