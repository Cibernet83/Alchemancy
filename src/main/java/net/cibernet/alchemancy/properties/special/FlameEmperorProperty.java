package net.cibernet.alchemancy.properties.special;

import net.cibernet.alchemancy.Alchemancy;
import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
import net.cibernet.alchemancy.properties.Property;
import net.cibernet.alchemancy.registries.AlchemancyProperties;
import net.cibernet.alchemancy.util.ColorUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.player.CriticalHitEvent;
import net.neoforged.neoforge.event.level.BlockDropsEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@EventBusSubscriber
public class FlameEmperorProperty extends Property {
	private static final AttributeModifier SPEED_MOD = new AttributeModifier(ResourceLocation.fromNamespaceAndPath(Alchemancy.MODID, "flame_emperor_property_modifier"), 1F, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);


	@SubscribeEvent
	public static void onPlayerTick(PlayerTickEvent.Pre event) {
		Player player = event.getEntity();
		ItemStack stack = player.getMainHandItem();
		var speedMod = player.getAttribute(Attributes.BLOCK_BREAK_SPEED);

		if (speedMod == null) return;

		if (player.isOnFire() && InfusedPropertiesHelper.hasProperty(stack, AlchemancyProperties.FLAME_EMPEROR)) {
			if (!speedMod.hasModifier(SPEED_MOD.id()))
				speedMod.addTransientModifier(SPEED_MOD);
		} else speedMod.removeModifier(SPEED_MOD.id());
	}

	@Override
	public void modifyAttackDamage(Entity user, ItemStack weapon, LivingDamageEvent.Pre event) {
		if (user.isOnFire())
			event.setNewDamage(Math.min(event.getOriginalDamage() * 3, event.getNewDamage() * 1.25f));
		super.modifyAttackDamage(user, weapon, event);
	}

	@Override
	public void onAttack(@Nullable Entity user, ItemStack weapon, DamageSource damageSource, LivingEntity target) {
		target.setRemainingFireTicks(Math.max(target.getRemainingFireTicks(), user != null && user.isOnFire() ? 120 : 30));
	}

	@Override
	public void modifyCriticalAttack(Player user, ItemStack weapon, CriticalHitEvent event) {
		if (user.isOnFire())
			event.setDamageMultiplier(Math.min(event.getDamageMultiplier(), event.getVanillaMultiplier() * 1.5f));
	}

	@Override
	public void modifyBlockDrops(Entity breaker, ItemStack tool, EquipmentSlot slot, List<ItemEntity> drops, BlockDropsEvent event) {

		if (!breaker.isOnFire()) return;

		Level level = breaker.level();
		for (ItemEntity drop : drops) {
			ItemStack stack = drop.getItem();
			RecipeHolder<? extends AbstractCookingRecipe> recipe = level.getRecipeManager().getRecipeFor(RecipeType.SMELTING, new SingleRecipeInput(stack), level).orElse(null);
			if (recipe == null)
				recipe = level.getRecipeManager().getRecipeFor(RecipeType.BLASTING, new SingleRecipeInput(stack), level).orElse(null);

			if (recipe != null) {
				ItemStack result = recipe.value().getResultItem(level.registryAccess()).copy();
				result.setCount(result.getCount() * stack.getCount());
				drop.setItem(result);
			}
		}
	}

	@Override
	public Component getDisplayText(ItemStack stack) {
		return super.getDisplayText(stack).copy().withStyle(ChatFormatting.BOLD);
	}

	@Override
	public int getColor(ItemStack stack) {
		return ColorUtils.interpolateColorsOverTime(0.25f, 0xE54C00, 0xFD8421, 0xFFB32F, 0xFFF87E);
	}
}
