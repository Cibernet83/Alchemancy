package net.cibernet.alchemancy.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.cibernet.alchemancy.events.handler.MobTemptHandler;
import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
import net.cibernet.alchemancy.mixin.accessors.EntityAccessor;
import net.cibernet.alchemancy.properties.ThrowableProperty;
import net.cibernet.alchemancy.registries.AlchemancyProperties;
import net.minecraft.core.Holder;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ItemSteerable;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin
{

	@Shadow protected abstract float getRiddenSpeed(Player player);

	@Shadow public abstract double getAttributeValue(Holder<Attribute> attribute);

	@Shadow public abstract Vec3 handleRelativeFrictionAndCalculateMovement(Vec3 deltaMovement, float friction);

	@Shadow public abstract void travel(Vec3 travelVector);

	@WrapOperation(method = "createLivingAttributes", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ai/attributes/AttributeSupplier;builder()Lnet/minecraft/world/entity/ai/attributes/AttributeSupplier$Builder;"))
	private static AttributeSupplier.Builder createLivingAttributes(Operation<AttributeSupplier.Builder> original)
	{
		AttributeSupplier.Builder result = original.call();

		if(!result.hasAttribute(Attributes.ATTACK_DAMAGE))
			result.add(Attributes.ATTACK_DAMAGE, 1);

		return result;
	}

	@Inject(method = "getRiddenInput", at = @At("HEAD"), cancellable = true)
	public void getRiddenInput(Player player, Vec3 travelVector, CallbackInfoReturnable<Vec3> cir)
	{
		MobTemptHandler.performIfTempted(alchemancy$self(), player, EquipmentSlotGroup.HAND, () ->
				cir.setReturnValue(new Vec3(0, -Mth.sin(player.getXRot() * (float) (Math.PI / 180.0)), 1)));
	}

	@Inject(method = "tickRidden", at = @At("RETURN"))
	public void riddenTick(Player player, Vec3 travelVector, CallbackInfo ci)
	{
		LivingEntity self = alchemancy$self();

		if(!(self instanceof ItemSteerable))
			MobTemptHandler.performIfTempted(self, player, EquipmentSlotGroup.HAND, () ->
			{
				self.setYRot(player.getYRot());
				self.setXRot(player.getXRot() * 0.5F);
				self.yRotO = self.yBodyRot = self.yHeadRot = self.getYRot();


				float f2 = self.level().getBlockState(self.getBlockPosBelowThatAffectsMyMovement()).getFriction(self.level(), self.getBlockPosBelowThatAffectsMyMovement(), self);
				Vec3 frictionTravelVector = EntityAccessor.invokeGetInputVector(travelVector, f2, self.getYRot()).add(self.getDeltaMovement());
				Vec3 collidedTravelVector = ((EntityAccessor)self).invokeCollide(frictionTravelVector);

				if(!Mth.equal(frictionTravelVector.x, collidedTravelVector.x) || !Mth.equal(frictionTravelVector.z, collidedTravelVector.z))
					self.horizontalCollision = true;
			});
	}

	@Inject(method = "getRiddenSpeed", at = @At("HEAD"), cancellable = true)
	public void getRiddenSpeed(Player player, CallbackInfoReturnable<Float> cir)
	{
		MobTemptHandler.performIfTempted(alchemancy$self(), player, EquipmentSlotGroup.HAND, () ->
			cir.setReturnValue((float) (getAttributeValue(Attributes.MOVEMENT_SPEED) * 0.225f)));
	}

	@Unique
	private LivingEntity alchemancy$self()
	{
		return (LivingEntity) (Object) this;
	}
}
