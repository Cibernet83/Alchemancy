package net.cibernet.alchemancy.properties.special;

import net.cibernet.alchemancy.properties.Property;
import net.cibernet.alchemancy.properties.SparklingProperty;
import net.cibernet.alchemancy.properties.data.IDataHolder;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.ItemStackedOnOtherEvent;

public class AirWalkingProperty extends Property implements IDataHolder<Double> {

	public static final DustParticleOptions PARTICLES = new DustParticleOptions(Vec3.fromRGB24(0x47FFBE).toVector3f(), 1.5f);

	@Override
	public void onStackedOverMe(ItemStack carriedItem, ItemStack stackedOnItem, Player player, ClickAction clickAction, ItemStackedOnOtherEvent event) {
		removeData(stackedOnItem);
	}

	@Override
	public void onItemPickedUp(Player player, ItemStack stack, ItemEntity itemEntity) {
		removeData(stack);
	}

	@Override
	public void onInventoryTick(Entity user, ItemStack stack, Level level, int inventorySlot, boolean isCurrentItem) {
		if(inventorySlot != 36)
			removeData(stack);
	}

	@Override
	public void onEquippedTick(LivingEntity user, EquipmentSlot slot, ItemStack stack) {

		if (!(slot == EquipmentSlot.FEET || slot == EquipmentSlot.BODY) || user.isShiftKeyDown()) {
			removeData(stack);
			return;
		}

		if(getData(stack) == null || user.onGround())
			setData(stack, user.getY() + user.getDeltaMovement().y);
		else if (getData(stack) != null) {

			double y = getData(stack);
			var vec = user.getDeltaMovement();

			if(user.getY() + vec.y <= y)
			{
				user.resetFallDistance();
				user.setOnGround(user.getY() <= y);
				user.setDeltaMovement(new Vec3(vec.x, Math.min(Math.max(y - user.getY(), vec.y), 1), vec.z));

				playParticles(user, y, stack, 5);

//				for(int i = 0; i < 5; i++)
//					user.level().addParticle(SparklingProperty.getParticles(stack).orElse(PARTICLES), user.getRandomX(1), y, user.getRandomZ(1), 0, 0, 0);
			}
		}

	}

	public static void playParticles(Entity user, double y, ItemStack stack, int amount) {
		if (user.level() instanceof ServerLevel serverLevel)
			serverLevel.sendParticles(SparklingProperty.getParticles(stack).orElse(PARTICLES), user.getX(), y, user.getZ(), amount, user.getBbWidth() * 0.5f, 0, user.getBbWidth() * 0.5f, 0);
	}

	@Override
	public int getColor(ItemStack stack) {
		return 0x47FFBE;
	}

	@Override
	public Double readData(CompoundTag tag) {
		return tag.getDouble("target_y");
	}

	@Override
	public CompoundTag writeData(Double data) {
		return new CompoundTag(){{
			if(data != null)
				putDouble("target_y", data);
		}};
	}

	@Override
	public Double getDefaultData() {
		return null;
	}
}
