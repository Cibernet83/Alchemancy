package net.cibernet.alchemancy.events.handler;

import net.cibernet.alchemancy.entity.ai.ScareGoal;
import net.cibernet.alchemancy.item.components.InfusedPropertiesComponent;
import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
import net.cibernet.alchemancy.mixin.accessors.LivingEntityAccessor;
import net.cibernet.alchemancy.network.S2CInventoryTickPayload;
import net.cibernet.alchemancy.properties.Property;
import net.cibernet.alchemancy.properties.special.AuxiliaryProperty;
import net.cibernet.alchemancy.properties.voidborn.VoidtouchProperty;
import net.cibernet.alchemancy.registries.AlchemancyItems;
import net.cibernet.alchemancy.registries.AlchemancyProperties;
import net.cibernet.alchemancy.registries.AlchemancyTags;
import net.cibernet.alchemancy.util.CommonUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ServerboundPlayerInputPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.HitResult;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ComputeFovModifierEvent;
import net.neoforged.neoforge.common.CommonHooks;
import net.neoforged.neoforge.event.ItemAttributeModifierEvent;
import net.neoforged.neoforge.event.ItemStackedOnOtherEvent;
import net.neoforged.neoforge.event.VanillaGameEvent;
import net.neoforged.neoforge.event.enchanting.GetEnchantmentLevelEvent;
import net.neoforged.neoforge.event.entity.EntityInvulnerabilityCheckEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.ProjectileImpactEvent;
import net.neoforged.neoforge.event.entity.item.ItemTossEvent;
import net.neoforged.neoforge.event.entity.living.*;
import net.neoforged.neoforge.event.entity.player.*;
import net.neoforged.neoforge.event.furnace.FurnaceFuelBurnTimeEvent;
import net.neoforged.neoforge.event.level.BlockDropsEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Predicate;

@EventBusSubscriber
public class PropertyEventHandler
{
	@SubscribeEvent
	public static void onEntityInvulnerableCheck(EntityInvulnerabilityCheckEvent event)
	{
		ItemStack stack = ItemStack.EMPTY;
		if(event.getEntity() instanceof ItemEntity itemEntity)
			stack = itemEntity.getItem();
		else if(event.getEntity() instanceof ItemSupplier itemSupplier)
			stack = itemSupplier.getItem();

		if(!stack.isEmpty())
		{
			if (event.getSource().is(DamageTypeTags.IS_EXPLOSION) && InfusedPropertiesHelper.hasProperty(stack, AlchemancyProperties.BLAST_RESISTANT))
				event.setInvulnerable(true);
			if(event.getSource().is(DamageTypes.FELL_OUT_OF_WORLD) && InfusedPropertiesHelper.hasProperty(stack, AlchemancyProperties.VOIDBORN))
				event.setInvulnerable(true);
		}
		if(event.getEntity() instanceof LivingEntity living)
		{
			if (InfusedPropertiesHelper.hasItemWithProperty(living, AlchemancyProperties.VOIDBORN, true) &&
					(event.getSource().is(DamageTypes.FELL_OUT_OF_WORLD) ||
					event.getSource().is(VoidtouchProperty.VOIDTOUCH_DAMAGE_KEY)))
				event.setInvulnerable(true);
		}
	}

	@SubscribeEvent
	public static void onLivingHeal(LivingHealEvent event)
	{
		for (EquipmentSlot slot : EquipmentSlot.values()) {
			ItemStack stack = event.getEntity().getItemBySlot(slot);
			InfusedPropertiesHelper.forEachProperty(stack, propertyHolder -> propertyHolder.value().modifyHeal(event.getEntity(), stack, slot, event));
		}
	}

	@SubscribeEvent
	public static void onLivingDamage(LivingDamageEvent.Pre event)
	{

		if(event.getSource().is(AlchemancyTags.DamageTypes.TRIGGERS_ON_HIT_EFFECTS) && event.getSource().getDirectEntity() instanceof LivingEntity user)
		{
			ItemStack stack = user.getMainHandItem();
			InfusedPropertiesHelper.forEachProperty(stack, holder -> holder.value().modifyAttackDamage(user, stack, event));
		}
		else if (event.getSource().is(AlchemancyTags.DamageTypes.TRIGGERS_ON_PROJECTILE_HIT_EFFECTS) && event.getSource().getDirectEntity() instanceof Projectile projectile)
		{
			ItemStack stack = getProjectileItemStack(projectile);
			if(!stack.isEmpty())
				InfusedPropertiesHelper.forEachProperty(stack, propertyHolder -> propertyHolder.value().modifyAttackDamage(projectile, stack, event));
		}

		for (EquipmentSlot slot : EquipmentSlot.values()) {

			var currentDamage = event.getNewDamage();

			ItemStack stack = event.getEntity().getItemBySlot(slot);
			InfusedPropertiesHelper.forEachProperty(stack, propertyHolder -> propertyHolder.value().modifyDamageReceived(event.getEntity(), stack, slot, event));

			if(currentDamage >= Integer.MAX_VALUE)
				event.setNewDamage(currentDamage);
		}
	}

	@SubscribeEvent
	public static void onLivingDeath(LivingDeathEvent event)
	{
		if(event.getSource().is(AlchemancyTags.DamageTypes.TRIGGERS_ON_HIT_EFFECTS) && event.getSource().getDirectEntity() instanceof LivingEntity user)
		{
			ItemStack stack = user.getMainHandItem();
			InfusedPropertiesHelper.forEachProperty(stack, holder -> holder.value().onKill(event.getEntity(), user, stack, event));
		}

		for (EquipmentSlot slot : EquipmentSlot.values()) {
			ItemStack stack = event.getEntity().getItemBySlot(slot);
			InfusedPropertiesHelper.forEachProperty(stack, propertyHolder -> propertyHolder.value().onUserDeath(event.getEntity(), stack, slot, event));
		}
	}

	@SubscribeEvent
	public static void onIncomingDamage(LivingIncomingDamageEvent event)
	{
		if(event.getSource().is(AlchemancyTags.DamageTypes.TRIGGERS_ON_HIT_EFFECTS) && event.getSource().getDirectEntity() instanceof LivingEntity user)
		{
			ItemStack stack = user.getMainHandItem();
			InfusedPropertiesHelper.forEachProperty(stack, propertyHolder -> propertyHolder.value().onIncomingAttack(user, stack, event.getEntity(), event));
		}
		else if (event.getSource().is(AlchemancyTags.DamageTypes.TRIGGERS_ON_PROJECTILE_HIT_EFFECTS) && event.getSource().getDirectEntity() instanceof Projectile projectile)
		{
			ItemStack stack = getProjectileItemStack(projectile);
			if(!stack.isEmpty())
				InfusedPropertiesHelper.forEachProperty(stack, propertyHolder -> propertyHolder.value().onIncomingAttack(projectile, stack, event.getEntity(), event));
		}

		for (EquipmentSlot slot : EquipmentSlot.values()) {
			ItemStack stack = event.getEntity().getItemBySlot(slot);
			InfusedPropertiesHelper.forEachProperty(stack, propertyHolder -> propertyHolder.value().onIncomingDamageReceived(event.getEntity(), stack, slot, event.getSource(), event));
		}
	}

	@SubscribeEvent
	public static void onCriticalHit(CriticalHitEvent event)
	{
		ItemStack stack = event.getEntity().getMainHandItem();
		InfusedPropertiesHelper.forEachProperty(stack, holder -> holder.value().modifyCriticalAttack(event.getEntity(), stack, event));

	}

	@SubscribeEvent
	public static void onKnockBack(LivingKnockBackEvent event)
	{
		LivingEntity target = event.getEntity();

		for (EquipmentSlot slot : EquipmentSlot.values()) {
			ItemStack stack = target.getItemBySlot(slot);
			InfusedPropertiesHelper.forEachProperty(stack, propertyHolder -> propertyHolder.value().modifyKnockBackReceived(target, stack, slot, event));
		}

		if(target.getLastHurtByMobTimestamp() == target.tickCount && target.getLastHurtByMob() != null)
		{
			LivingEntity user = target.getLastHurtByMob();
			ItemStack weapon = user.getMainHandItem();
			InfusedPropertiesHelper.forEachProperty(weapon, propertyHolder -> propertyHolder.value().modifyKnockBackApplied(user, weapon, target, event));
		}
	}

	@SubscribeEvent
	public static void onPlayerTickPost(PlayerTickEvent.Post event)
	{
		if(event.getEntity() instanceof ServerPlayer serverPlayer)
			S2CInventoryTickPayload.sendPacket(serverPlayer);
	}

	@SubscribeEvent
	public static void onEntityTick(EntityTickEvent.Pre event)
	{
		var user = event.getEntity();
		if (user.level().isClientSide() && user instanceof LocalPlayer localPlayer)
			localPlayer.connection.send(new ServerboundPlayerInputPacket(localPlayer.xxa, localPlayer.zza, ((LivingEntityAccessor) user).isJumping(), localPlayer.isShiftKeyDown()));

		if(user instanceof LivingEntity living && !(living instanceof Player))
			for (EquipmentSlot slot : EquipmentSlot.values()) {
				ItemStack stack = living.getItemBySlot(slot);
				InfusedPropertiesHelper.forEachProperty(stack, propertyHolder -> propertyHolder.value().onEquippedTick(living, slot, stack));
			}
		else if(user instanceof ItemEntity itemEntity)
		{
			ItemStack stack = itemEntity.getItem();
			InfusedPropertiesHelper.forEachProperty(stack, propertyHolder -> propertyHolder.value().onEntityItemTick(stack, itemEntity));
		}
		else if(user instanceof Projectile projectile)
		{
			ItemStack stack = getProjectileItemStack(projectile);
			if(stack != null && !stack.isEmpty())
				InfusedPropertiesHelper.forEachProperty(stack, propertyHolder -> propertyHolder.value().onProjectileTick(stack, projectile));
		}
	}

	@SubscribeEvent
	public static void onProjectileImpact(ProjectileImpactEvent event)
	{
		Projectile projectile = event.getProjectile();
		ItemStack stack = getProjectileItemStack(projectile);
		if(!stack.isEmpty())
			InfusedPropertiesHelper.forEachProperty(stack, propertyHolder ->
			{
				if(propertyHolder.equals(AlchemancyProperties.LOYAL) || AlchemancyProperties.LOYAL.value().canTriggerImpactEffects(projectile, event.getRayTraceResult()))
					propertyHolder.value().onProjectileImpact(stack, projectile, event.getRayTraceResult(), event);
			});
	}

	public static ItemStack getProjectileItemStack(Projectile entity)
	{
		ItemStack result = null;
		if(entity instanceof ItemSupplier itemSupplier)
			result = itemSupplier.getItem();
		else if(entity instanceof AbstractArrow arrow)
			result =  arrow.getPickupItemStackOrigin();
		return result == null ? ItemStack.EMPTY : result;
	}

	@SubscribeEvent
	public static void onItemPickUp(ItemEntityPickupEvent.Pre event)
	{
		InfusedPropertiesHelper.forEachProperty(event.getItemEntity().getItem(), propertyHolder -> propertyHolder.value().onItemPickedUp(event.getPlayer(), event.getItemEntity().getItem(), event.getItemEntity()));
		for (EquipmentSlot slot : EquipmentSlot.values()) {
			ItemStack stack = event.getPlayer().getItemBySlot(slot);
			InfusedPropertiesHelper.forEachProperty(stack, propertyHolder -> propertyHolder.value().onPickUpAnyItem(event.getPlayer(), stack, slot, event.getItemEntity(), !event.getItemEntity().hasPickUpDelay(), event));
		}
	}

	@SubscribeEvent
	public static void onItemToss(ItemTossEvent event)
	{
		InfusedPropertiesHelper.forEachProperty(event.getEntity().getItem(), propertyHolder -> propertyHolder.value().onItemTossed(event.getPlayer(), event.getEntity().getItem(), event.getEntity(), event));
	}

	@SubscribeEvent
	public static void onAttributeModification(ItemAttributeModifierEvent event)
	{
		InfusedPropertiesHelper.forEachProperty(event.getItemStack(), propertyHolder -> propertyHolder.value().applyAttributes(event));
	}

	@SubscribeEvent
	public static void onLivingDrops(LivingDropsEvent event)
	{
		if(event.getSource().getDirectEntity() instanceof LivingEntity user)
		{
			ItemStack weapon = user.getMainHandItem();
			InfusedPropertiesHelper.forEachProperty(weapon, propertyHolder -> propertyHolder.value().modifyLivingDrops(event.getEntity(), weapon, user, event.getDrops(), event));
		}
	}

	@SubscribeEvent
	public static void onLivingXpDrops(LivingExperienceDropEvent event)
	{
		Player user = event.getAttackingPlayer();
		if(user != null)
		{
			for(EquipmentSlot slot : EquipmentSlot.values())
			{
				ItemStack item = user.getItemBySlot(slot);
				InfusedPropertiesHelper.forEachProperty(item, propertyHolder -> propertyHolder.value().modifyLivingExperienceDrops(user, item, slot, event.getEntity(), event));
			}

			AuxiliaryProperty.triggerAuxiliaryEffects(user, (propertyHolder, stack) -> propertyHolder.value().modifyLivingExperienceDrops(user, stack, EquipmentSlot.MAINHAND, event.getEntity(), event));
		}
	}

	@SubscribeEvent
	public static void onBlockDrops(BlockDropsEvent event)
	{

		ItemStack tool = event.getBreaker() instanceof LivingEntity living && (!living.getMainHandItem().isEmpty() && ItemStack.isSameItem(event.getTool(), living.getMainHandItem())) ? living.getMainHandItem() : event.getTool(); //ServerPlayerGameMode uses a copied stack instead of the actual stack held by the player
		InfusedPropertiesHelper.forEachProperty(tool, propertyHolder -> propertyHolder.value().modifyBlockDrops(event.getBreaker(), tool, EquipmentSlot.MAINHAND, event.getDrops(), event));

		if(event.getBreaker() instanceof Player player)
			AuxiliaryProperty.triggerAuxiliaryEffects(player, (propertyHolder, stack) -> propertyHolder.value().modifyBlockDrops(player, stack, EquipmentSlot.MAINHAND, event.getDrops(), event));

	}

	@SubscribeEvent
	public static void onItemUseTick(LivingEntityUseItemEvent.Tick event)
	{
		InfusedPropertiesHelper.forEachProperty(event.getItem(), propertyHolder -> propertyHolder.value().onItemUseTick(event.getEntity(), event.getItem(), event));
	}

	@SubscribeEvent
	public static void onEffectAdded(MobEffectEvent.Added event)
	{
		for (EquipmentSlot slot : EquipmentSlot.values()) {
			ItemStack stack = event.getEntity().getItemBySlot(slot);
			InfusedPropertiesHelper.forEachProperty(stack, propertyHolder -> propertyHolder.value().onMobEffectAdded(stack, slot, event.getEntity(), event));
		}
	}

	@SubscribeEvent
	public static void onEffectApplicable(MobEffectEvent.Applicable event)
	{
		for (EquipmentSlot slot : EquipmentSlot.values()) {
			ItemStack stack = event.getEntity().getItemBySlot(slot);
			InfusedPropertiesHelper.forEachProperty(stack, propertyHolder -> propertyHolder.value().isMobEffectApplicable(stack, slot, event.getEntity(), event));
		}
	}

	@SubscribeEvent
	public static void onRightClickItem(PlayerInteractEvent.RightClickItem event)
	{
		if(CommonUtils.calculateHitResult(event.getEntity()).getType() != HitResult.Type.BLOCK)
		{
			InfusedPropertiesHelper.forEachProperty(event.getItemStack(), propertyHolder -> propertyHolder.value().onRightClickItem(event));
			InfusedPropertiesHelper.forEachProperty(event.getItemStack(), propertyHolder -> propertyHolder.value().onRightClickItemPost(event));
		}
	}

	@SubscribeEvent
	public static void onRightClickItemOnBlock(UseItemOnBlockEvent event)
	{
		if(event.getUsePhase() == UseItemOnBlockEvent.UsePhase.ITEM_AFTER_BLOCK && event.getPlayer() != null)
		{
			InfusedPropertiesHelper.forEachProperty(event.getItemStack(), propertyHolder -> propertyHolder.value().onRightClickBlock(event));

			if(!event.isCanceled())
			{
				PlayerInteractEvent.RightClickItem clickEvent = new PlayerInteractEvent.RightClickItem(event.getPlayer(), event.getHand());
				InfusedPropertiesHelper.forEachProperty(event.getItemStack(), propertyHolder -> propertyHolder.value().onRightClickItem(clickEvent));
				InfusedPropertiesHelper.forEachProperty(event.getItemStack(), propertyHolder -> propertyHolder.value().onRightClickItemPost(clickEvent));

				if (clickEvent.isCanceled())
					event.cancelWithResult(resultToItemResult(clickEvent.getCancellationResult()));
			}
		}
	}

	private static ItemInteractionResult resultToItemResult(InteractionResult result) {
		return switch (result) {
			case SUCCESS, SUCCESS_NO_ITEM_USED -> ItemInteractionResult.SUCCESS;
			case CONSUME -> ItemInteractionResult.CONSUME;
			case CONSUME_PARTIAL -> ItemInteractionResult.CONSUME_PARTIAL;
			case PASS -> ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
			case FAIL -> ItemInteractionResult.FAIL;
		};
	}

	@SubscribeEvent
	public static void onRightClickEntity(PlayerInteractEvent.EntityInteract event)
	{
		InfusedPropertiesHelper.forEachProperty(event.getItemStack(), propertyHolder -> propertyHolder.value().onRightClickEntity(event));

		if(event.getTarget() instanceof LivingEntity target)
			for (EquipmentSlot slot : EquipmentSlot.values()) {
				if(slot.isArmor() && InfusedPropertiesHelper.hasProperty(target.getItemBySlot(slot), AlchemancyProperties.SADDLED) && event.getEntity().startRiding(target))
				{
					event.setCancellationResult(InteractionResult.SUCCESS);
					event.setCanceled(true);
				}
			}
	}

	@SubscribeEvent
	public static void onStopUsingItem(LivingEntityUseItemEvent.Stop event)
	{
		ItemStack stack = event.getItem();
		InfusedPropertiesHelper.forEachProperty(stack, propertyHolder -> propertyHolder.value().onStopUsingItem(stack, event.getEntity(), event));
	}

	@SubscribeEvent
	public static void onItemStacking(ItemStackedOnOtherEvent event)
	{
		if(event.isCanceled()) return;

		ItemStack carriedItem = event.getCarriedItem();
		ItemStack stackedOnItem = event.getStackedOnItem();
		AtomicBoolean canceled = new AtomicBoolean(false);
		InfusedPropertiesHelper.forEachProperty(carriedItem, propertyHolder -> propertyHolder.value().onStackedOverItem(carriedItem, stackedOnItem, event.getPlayer(), event.getClickAction(), event.getCarriedSlotAccess(), event.getSlot(), canceled));
		if(!canceled.get())
			InfusedPropertiesHelper.forEachProperty(stackedOnItem, propertyHolder -> propertyHolder.value().onStackedOverMe(carriedItem, stackedOnItem, event.getPlayer(), event.getClickAction(), event.getCarriedSlotAccess(), event.getSlot(), canceled));
		event.setCanceled(canceled.get());
	}

	@SubscribeEvent
	public static void onLivingJump(LivingEvent.LivingJumpEvent event)
	{
		for (EquipmentSlot slot : EquipmentSlot.values())
		{
			ItemStack stack = event.getEntity().getItemBySlot(slot);
			InfusedPropertiesHelper.forEachProperty(stack, propertyHolder -> propertyHolder.value().onJump(event.getEntity(), stack, slot, event));
		}
	}

	@SubscribeEvent
	public static void onLivingFall(LivingFallEvent event)
	{
		for (EquipmentSlot slot : EquipmentSlot.values())
		{
			ItemStack stack = event.getEntity().getItemBySlot(slot);
			InfusedPropertiesHelper.forEachProperty(stack, propertyHolder -> propertyHolder.value().onFall(event.getEntity(), stack, slot, event));
		}
	}

	@SubscribeEvent
	public static void onFlyingPlayerFall(PlayerFlyableFallEvent event)
	{
		var livingFall = new LivingFallEvent(event.getEntity(), event.getDistance(), event.getMultiplier());
		for (EquipmentSlot slot : EquipmentSlot.values())
		{
			ItemStack stack = event.getEntity().getItemBySlot(slot);
			InfusedPropertiesHelper.forEachProperty(stack, propertyHolder -> propertyHolder.value().onFall(event.getEntity(), stack, slot, livingFall));
		}

		event.setDistance(livingFall.getDistance());
		event.setMultiplier(livingFall.getDamageMultiplier());
	}

	@SubscribeEvent
	public static void onEnchantmentLevel(GetEnchantmentLevelEvent event)
	{
		ItemStack stack = event.getStack();
		InfusedPropertiesHelper.forEachProperty(stack, propertyHolder -> propertyHolder.value().modifyEnchantmentLevels(event));
	}

	@SubscribeEvent
	public static void onFurnaceBurnTime(FurnaceFuelBurnTimeEvent event)
	{
		ItemStack stack = event.getItemStack();
		int burnTime = event.getBurnTime();
		float burnMultiplier = InfusedPropertiesHelper.hasProperty(stack, AlchemancyProperties.FLAMMABLE) ? 1.5f : 1;
		if(InfusedPropertiesHelper.hasProperty(stack, AlchemancyProperties.CHARRED))
			burnMultiplier *= 3;

		if(burnMultiplier != 1)
		{
			if(stack.isDamageableItem())
			{
				float durability = (event.getItemStack().getMaxDamage() - event.getItemStack().getDamageValue()) / 50f;
				if(durability > 1)
					durability = 1 + (durability-1)*0.5f;
				burnMultiplier *= durability;
			}

			event.setBurnTime((int) (Math.max(burnTime, 300) * burnMultiplier));
		}
	}

	@SubscribeEvent
	public static void onEnderManAnger(EnderManAngerEvent event)
	{
		if(InfusedPropertiesHelper.hasProperty(event.getPlayer().getItemBySlot(EquipmentSlot.HEAD), AlchemancyTags.Properties.PREVENTS_ENDERMAN_AGGRO))
			event.setCanceled(true);
	}

	@SubscribeEvent
	public static void modifyFov(ComputeFovModifierEvent event)
	{
		Player player = event.getPlayer();

		if(isScoping(player))
			event.setNewFovModifier(0);
	}

	public static boolean isScoping(Player player) {

		if(player.isShiftKeyDown())
		{
			return InfusedPropertiesHelper.hasItemWithProperty(player, AlchemancyProperties.SCOPING, true, EquipmentSlotGroup.HAND) ||
					InfusedPropertiesHelper.hasProperty(player.getItemBySlot(EquipmentSlot.HEAD), AlchemancyProperties.SCOPING);
		}
		return false;
	}


	@SubscribeEvent
	public static void onEntityJoinLevel(EntityJoinLevelEvent event)
	{
		if(event.getEntity() instanceof PathfinderMob mob)
		{
			if(!AlchemancyProperties.SCARY.is(AlchemancyTags.Properties.DISABLED) && mob.getType().is(AlchemancyTags.EntityTypes.SCARED_BY_SCARY))
				mob.goalSelector.addGoal(0, new ScareGoal(mob, 2, AlchemancyProperties.SCARY));
			if(!AlchemancyProperties.SEEDED.is(AlchemancyTags.Properties.DISABLED) && mob.getType().is(AlchemancyTags.EntityTypes.AGGROED_BY_SEEDED) && mob.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE))
			{
				mob.goalSelector.addGoal(3, new MeleeAttackGoal(mob, 1.6, true));
				mob.targetSelector.addGoal(0, targetLivingHoldingProperty(mob, AlchemancyProperties.SEEDED, EquipmentSlotGroup.ARMOR));
			}
		}
	}

	public static NearestAttackableTargetGoal<LivingEntity> targetLivingHoldingProperty(Mob mob, Holder<Property> property, EquipmentSlotGroup slotsToCheck) {
		return new NearestAttackableTargetGoal<>(mob, LivingEntity.class, 0, false, false, living -> {
			for (EquipmentSlot slot : EquipmentSlot.values()) {
				if (slotsToCheck.test(slot) && InfusedPropertiesHelper.hasProperty(living.getItemBySlot(slot), property))
					return true;
			}
			return false;
		});
	}

	@SubscribeEvent
	public static void onGetProjectile(LivingGetProjectileEvent event)
	{
		ItemStack shootable = event.getProjectileWeaponItemStack();
		ItemStack projectile = event.getProjectileItemStack();

		if(projectile.isEmpty() && event.getEntity() instanceof Player user)
		{
			Predicate<ItemStack> predicate = ((ProjectileWeaponItem) shootable.getItem()).getAllSupportedProjectiles(shootable);
			for (int i = 0; i < user.getInventory().getContainerSize(); i++) {
				ItemStack stack = user.getInventory().getItem(i);
				ItemStack storedStack = AlchemancyProperties.HOLLOW.get().getData(stack);
				if (!storedStack.isEmpty() && predicate.test(storedStack)) {
					{
						event.setProjectileItemStack(CommonHooks.getProjectile(user, shootable, storedStack));
						storedStack.shrink(1);
						AlchemancyProperties.HOLLOW.get().setData(stack, stack.isEmpty() ? ItemStack.EMPTY : storedStack);
						return;
					}
				}
			}
		}
	}

	private static final HashMap<EquipmentSlot, List<Holder<GameEvent>>> MUFFLED_EVENTS = new HashMap<>()
	{{
		put(EquipmentSlot.MAINHAND, List.of(GameEvent.BLOCK_PLACE, GameEvent.ITEM_INTERACT_START, GameEvent.ITEM_INTERACT_FINISH, GameEvent.ENTITY_PLACE, GameEvent.ENTITY_ACTION, GameEvent.SHEAR, GameEvent.PROJECTILE_SHOOT));
		put(EquipmentSlot.OFFHAND, List.of(GameEvent.BLOCK_PLACE, GameEvent.ITEM_INTERACT_START, GameEvent.ITEM_INTERACT_FINISH, GameEvent.ENTITY_PLACE, GameEvent.ENTITY_ACTION, GameEvent.SHEAR));
		put(EquipmentSlot.HEAD, List.of(GameEvent.EAT, GameEvent.DRINK));
		put(EquipmentSlot.CHEST, List.of(GameEvent.ENTITY_DAMAGE, GameEvent.ENTITY_DIE, GameEvent.ELYTRA_GLIDE, GameEvent.TELEPORT));
		put(EquipmentSlot.LEGS, List.of(GameEvent.ENTITY_DAMAGE, GameEvent.ENTITY_DIE, GameEvent.SPLASH, GameEvent.SWIM, GameEvent.TELEPORT));
		put(EquipmentSlot.FEET, List.of(GameEvent.STEP, GameEvent.HIT_GROUND, GameEvent.SPLASH));
	}};

	@SubscribeEvent
	public static void onVanillaGameEvent(VanillaGameEvent event)
	{
		if(event.getCause() instanceof LivingEntity living)
		{
			for (EquipmentSlot slot : EquipmentSlot.values())
			{
				if(InfusedPropertiesHelper.hasProperty(living.getItemBySlot(slot), AlchemancyProperties.MUFFLED) &&
						(!MUFFLED_EVENTS.containsKey(slot) || MUFFLED_EVENTS.get(slot).contains(event.getVanillaEvent())))
					event.setCanceled(true);
			}
		} else if (event.getCause() instanceof Projectile projectile &&
				InfusedPropertiesHelper.hasProperty(getProjectileItemStack(projectile), AlchemancyProperties.MUFFLED) &&
				event.getVanillaEvent().equals(GameEvent.PROJECTILE_LAND))
			event.setCanceled(true);
	}

	@SubscribeEvent
	public static void onBlockLeftClicked(PlayerInteractEvent.LeftClickBlock event)
	{
		ItemStack stack = event.getItemStack();

		if((event.getEntity().isCreative() && InfusedPropertiesHelper.hasProperty(stack, AlchemancyTags.Properties.DISABLES_BLOCK_ATTACK_IN_CREATIVE)) ||
				InfusedPropertiesHelper.hasProperty(stack, AlchemancyProperties.FLIMSY))
			event.setCanceled(true);

	}

	private static final Component PROPERTY_INGREDIENT_NAME = Component.translatable("item.alchemancy.property_capsule.ingredient");

	@SubscribeEvent
	public static void onItemTooltip(ItemTooltipEvent event)
	{
		ItemStack stack = event.getItemStack();
		boolean hasInfusions = !InfusedPropertiesHelper.getInfusedProperties(stack).isEmpty();

		if(stack.has(AlchemancyItems.Components.INGREDIENT_DISPLAY))
		{
			event.getToolTip().clear();
			if(stack.is(AlchemancyItems.PROPERTY_CAPSULE))
				event.getToolTip().add(PROPERTY_INGREDIENT_NAME);
			else event.getToolTip().add(stack.getDisplayName());
		}

		if(hasInfusions)
			stack.get(AlchemancyItems.Components.INFUSED_PROPERTIES).forEachProperty(holder -> event.getToolTip().add(
					holder.is(AlchemancyTags.Properties.DISABLED) ?
							Component.translatable("property.disabled", Component.translatable(holder.value().getLanguageKey())).withStyle(ChatFormatting.DARK_GRAY) :
							holder.value().getDisplayText(stack)), false);

		if(stack.has(AlchemancyItems.Components.STORED_PROPERTIES))
		{
			InfusedPropertiesComponent storedProperties = stack.get(AlchemancyItems.Components.STORED_PROPERTIES);
			if(hasInfusions && !storedProperties.properties().isEmpty())
				event.getToolTip().add(Component.translatable("item.alchemancy.tooltip.stored_properties").withStyle(ChatFormatting.GRAY));
			storedProperties.forEachProperty(holder -> event.getToolTip().add(
					holder.is(AlchemancyTags.Properties.DISABLED) ?
							Component.translatable("property.disabled", Component.translatable(holder.value().getLanguageKey())).withStyle(ChatFormatting.DARK_GRAY) :
							holder.value().getDisplayText(stack)), false);
		}

		if(event.getEntity() != null && (InfusedPropertiesHelper.hasProperty(stack, AlchemancyProperties.REVEALED) ||
				InfusedPropertiesHelper.hasProperty(event.getEntity().getItemBySlot(EquipmentSlot.HEAD), AlchemancyProperties.REVEALING)))
		{
			List<Holder<Property>> dormantProperties = AlchemancyProperties.getDormantProperties(stack);
			if(!dormantProperties.isEmpty()) {
				event.getToolTip().add(Component.translatable("item.alchemancy.tooltip.dormant_properties").withStyle(ChatFormatting.GRAY));
				for (Holder<Property> dormantProperty : dormantProperties) {
					event.getToolTip().add(dormantProperty.value().getName(stack));
				}
			}
		}

		if(event.getEntity() != null && ((InfusedPropertiesHelper.hasInfusedProperty(stack, AlchemancyProperties.SCRAMBLED)) ||
				InfusedPropertiesHelper.hasProperty(event.getEntity().getItemBySlot(EquipmentSlot.HEAD), AlchemancyProperties.SCRAMBLED)))
		{
			for(int i = 1; i < event.getToolTip().size(); i++)
				event.getToolTip().set(i, event.getToolTip().get(i).copy().withStyle(ChatFormatting.OBFUSCATED));
		}
	}
}
