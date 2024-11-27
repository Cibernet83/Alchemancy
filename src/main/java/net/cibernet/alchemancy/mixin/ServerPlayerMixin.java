package net.cibernet.alchemancy.mixin;

import net.cibernet.alchemancy.mixin.accessors.LivingEntityAccessor;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayer.class)
public class ServerPlayerMixin
{
	@Inject(method = "setPlayerInput", at = @At("HEAD"))
	public void setPlayerInput(float strafe, float forward, boolean jumping, boolean sneaking, CallbackInfo ci)
	{
		((LivingEntityAccessor)this).setJumping(jumping);
	}
}
