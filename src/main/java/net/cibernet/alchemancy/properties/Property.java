package net.cibernet.alchemancy.properties;

import com.mojang.serialization.Codec;
import net.cibernet.alchemancy.blocks.blockentities.RootedItemBlockEntity;
import net.cibernet.alchemancy.crafting.ForgePropertyRecipe;
import net.cibernet.alchemancy.crafting.ForgeRecipeGrid;
import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
import net.cibernet.alchemancy.registries.AlchemancyProperties;
import net.cibernet.alchemancy.util.ColorUtils;
import net.cibernet.alchemancy.util.InfusionPropertyDispenseBehavior;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryFixedCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.stats.Stats;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.item.Equipable;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.ItemAbility;
import net.neoforged.neoforge.common.util.TriState;
import net.neoforged.neoforge.event.ItemAttributeModifierEvent;
import net.neoforged.neoforge.event.ItemStackedOnOtherEvent;
import net.neoforged.neoforge.event.enchanting.GetEnchantmentLevelEvent;
import net.neoforged.neoforge.event.entity.ProjectileImpactEvent;
import net.neoforged.neoforge.event.entity.item.ItemTossEvent;
import net.neoforged.neoforge.event.entity.living.*;
import net.neoforged.neoforge.event.entity.player.CriticalHitEvent;
import net.neoforged.neoforge.event.entity.player.ItemEntityPickupEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.level.BlockDropsEvent;
import net.neoforged.neoforge.registries.DeferredItem;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

public abstract class Property
{
	public static final Codec<Holder<Property>> CODEC = RegistryFixedCodec.create(AlchemancyProperties.REGISTRY.getRegistryKey());
	public static final Codec<List<Holder<Property>>> LIST_CODEC = Codec.withAlternative(Codec.list(Property.CODEC), Property.CODEC, List::of);

	public static final StreamCodec<RegistryFriendlyByteBuf, Holder<Property>> STREAM_CODEC = ByteBufCodecs.holderRegistry(AlchemancyProperties.REGISTRY.getRegistryKey());
	public ResourceLocation getKey()
	{
		return AlchemancyProperties.getKeyFor(this);
	}

	public static Property simple(UnaryOperator<Style> style, Supplier<Integer> colorSupplier)
	{
		return new Property() {
			@Override
			public int getColor(ItemStack stack) {
				return colorSupplier.get();
			}

			@Override
			public Component getDisplayText(ItemStack stack) {
				return super.getDisplayText(stack).copy().withStyle(style);
			}
		};
	}

	public static Property simple(int color)
	{
		return simple((style) -> style, () -> color);
	}

	public static Property simpleSine(float time, boolean bold, int colorA, int colorB)
	{
		return simple((style) -> style.withBold(bold), () -> ColorUtils.sineColorsOverTime(time, colorA, colorB));
	}

	public static Property simpleFlashing(boolean bold, double time, int... colors)
	{
		return simple((style) -> style.withBold(bold),() -> ColorUtils.flashColorsOverTime(time, colors));
	}

	public static Property simpleInterpolated(boolean bold, float time, int... colors)
	{
		return simple((style) -> style.withBold(bold),() -> ColorUtils.interpolateColorsOverTime(time, colors));
	}

	public Holder<Property> asHolder()
	{
		return AlchemancyProperties.getHolder(this);
	}

	public void onAttack(@Nullable Entity user, ItemStack weapon, DamageSource damageSource, LivingEntity target)
	{

	}

	public void onDamageReceived(LivingEntity user, ItemStack weapon, EquipmentSlot slot, DamageSource damageSource)
	{

	}

	public void onEquippedTick(LivingEntity user, EquipmentSlot slot, ItemStack stack)
	{

	}

	public void modifyCriticalAttack(Player user, ItemStack weapon, CriticalHitEvent event)
	{
		if(event.isCriticalHit())
			onCriticalAttack(user, weapon, event.getTarget());
	}

	public void modifyAttackDamage(Entity user, ItemStack weapon, LivingDamageEvent.Pre event) {
		onAttack(user, weapon, event.getSource(), event.getEntity());
	}

	public void modifyDamageReceived(LivingEntity user, ItemStack weapon, EquipmentSlot slot, LivingDamageEvent.Pre event) {
		onDamageReceived(user, weapon, slot, event.getSource());
	}

	public void onIncomingAttack(Entity user, ItemStack weapon, LivingEntity target, LivingIncomingDamageEvent event)
	{
		if(event.isCanceled())
			onAttack(user, weapon, event.getSource(), target);
	}

	public void onIncomingDamageReceived(Entity user, ItemStack stack, EquipmentSlot slot, DamageSource source, LivingIncomingDamageEvent event)
	{
	}

	public void modifyHeal(LivingEntity user, ItemStack stack, EquipmentSlot slot, LivingHealEvent event)
	{
		onHeal(user, stack, slot, event.getAmount());
	}

	public void onHeal(LivingEntity user, ItemStack stack, EquipmentSlot slot, float amount)
	{

	}

	public void onActivation(@Nullable Entity source, Entity target, ItemStack stack, DamageSource damageSource)
	{
		onCriticalAttack(source instanceof Player player ? player : null, stack, target);
		if(source != null && target instanceof LivingEntity livingTarget)
			onAttack(source, stack, damageSource, livingTarget);
	}

	public final void onActivation(@Nonnull Entity source, Entity target, ItemStack stack)
	{
		onActivation(source, target, stack, activationDamageSource(source.level(), source, source.position()));
	}

	public final void onActivationByBlock(Level level, BlockPos position, Entity target, ItemStack stack)
	{
		onActivation(null, target, stack, activationDamageSource(level, null, position.getCenter()));
	}

	public static DamageSource activationDamageSource(Level level, @Nullable Entity source, Vec3 position)
	{
		return new DamageSource(level.holderOrThrow(DamageTypes.GENERIC), source, source, position);
	}

	public void onCriticalAttack(@Nullable Player user, ItemStack weapon, Entity target)
	{

	}

	public void modifyBlockDrops(Entity breaker, ItemStack tool, List<ItemEntity> drops, BlockDropsEvent event) {

	}

	public void modifyLivingDrops(LivingEntity dropsSource, ItemStack weapon, LivingEntity user, Collection<ItemEntity> drops, LivingDropsEvent event)
	{

	}

	public void modifyLivingExperienceDrops(Player user, ItemStack weapon, LivingEntity entity, LivingExperienceDropEvent event)
	{
	}

	public abstract int getColor(ItemStack stack);

	public Component getDisplayText(ItemStack stack)
	{
		return Component.translatable(getLanguageKey()).withColor(getColor(stack));
	}

	public Component getName(ItemStack stack)
	{
		return Component.translatable(getLanguageKey()).withColor(getColor(stack));
	}

	public Component getName()
	{
		return getName(ItemStack.EMPTY);
	}

	public String getRawName()
	{
		return Component.translatable(getLanguageKey()).getString();
	}

	public String getLanguageKey()
	{
		return "property." + getKey().toLanguageKey();
	}

	public int getPriority()
	{
		return Priority.NORMAL;
	}

	@Override
	public String toString() {
		return getKey().toString();
	}

	public void onRightClickAny(PlayerInteractEvent event) {
	}
	public void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
	}

	public void onRootedTick(RootedItemBlockEntity root, List<LivingEntity> entitiesInBounds) {
	}

	public void onRootedAnimateTick(RootedItemBlockEntity root, RandomSource randomSource) {
	}

	public void onJump(LivingEntity entity, ItemStack stack, EquipmentSlot slot, LivingEvent.LivingJumpEvent event) {
	}

	public void onFall(LivingEntity entity, ItemStack stack, EquipmentSlot slot, LivingFallEvent event) {
	}

	public void onEntityItemTick(ItemStack stack, ItemEntity itemEntity) {

	}

	public void applyAttributes(ItemAttributeModifierEvent event) {

	}

	public void onItemPickedUp(Player player, ItemStack stack, ItemEntity itemEntity) {
	}

	public void onItemTossed(Player player, ItemStack stack, ItemEntity itemEntity, ItemTossEvent event) {
	}

	public void modifyKnockBackReceived(LivingEntity user, ItemStack stack, EquipmentSlot slot, LivingKnockBackEvent event) {
	}

	public void modifyKnockBackApplied(LivingEntity user, ItemStack weapon, LivingEntity target, LivingKnockBackEvent event) {
	}

	public int modifyDurabilityConsumed(ItemStack stack, LivingEntity user, int originalAmount, int resultingAmount) {
		return resultingAmount;
	}

	public TriState isItemInTag(ItemStack stack, TagKey<Item> tagKey)
	{
		return TriState.DEFAULT;
	}

	public <T> Object modifyDataComponent(ItemStack stack, DataComponentType<? extends T> dataType, T data) { //i don't feel safe
		return data;
	}

	public float modifyStepOnFriction(Entity user, ItemStack stack, float originalResult, float result) {
		return result;
	}

	public boolean onFinishUsingItem(LivingEntity user, Level level, ItemStack stack) {
		return false;
	}

	public int modifyUseDuration(ItemStack stack, int original, int result) {
		return result;
	}

	public void onRightClickItem(PlayerInteractEvent.RightClickItem event) {

	}
	public void onRightClickEntity(PlayerInteractEvent.EntityInteract event) {

	}

	public boolean modifyAcceptAbility(ItemStack stack, ItemAbility itemAbility, boolean original, boolean result) {
		return result;
	}

	public void onEntityItemDestroyed(ItemStack stack, Entity itemEntity, DamageSource damageSource) {

	}

	public void modifyEnchantmentLevels(GetEnchantmentLevelEvent event) {

	}

	public void onInventoryTick(Entity user, ItemStack stack, Level level, int inventorySlot, boolean isCurrentItem) {

	}

	public static boolean entityLowOnHealth(LivingEntity entity)
	{
		return entity.getHealth() / entity.getMaxHealth() <= .2f;
	}

	public static EquipmentSlot getEquipmentSlotForItem(ItemStack stack) {
		final EquipmentSlot slot = stack.getEquipmentSlot();
		if (slot != null) return slot; // FORGE: Allow modders to set a non-default equipment slot for a stack; e.g. a non-armor chestplate-slot item
		Equipable equipable = Equipable.get(stack);
		if (equipable != null) {
			return equipable.getEquipmentSlot();
		}

		return EquipmentSlot.MAINHAND;
	}

	public static ItemStack consumeItem(@Nullable Entity user, ItemStack stack, @Nullable EquipmentSlot brokenOnSlot)
	{
		stack.shrink(1);
		if (brokenOnSlot != null && user instanceof Player player)
		{
			player.onEquippedItemBroken(stack.getItem(), brokenOnSlot);
			player.awardStat(Stats.ITEM_BROKEN.get(stack.getItem()));
		}
		return stack;
	}

	public static void modifyEnchantmentLevel(ItemEnchantments.Mutable enchantments, HolderLookup.RegistryLookup<Enchantment> lookup, ResourceKey<Enchantment> enchantmentKey, Function<Integer, Integer> func)
	{
		Optional<Holder.Reference<Enchantment>> enchantment = lookup.get(enchantmentKey);
		enchantment.ifPresent(enchantmentReference -> enchantments.set(enchantmentReference, func.apply(enchantments.getLevel(enchantmentReference))));
	}

	public static double getItemAttackDamage(ItemStack stack)
	{
		return getItemAttackDamage(EquipmentSlotGroup.MAINHAND, stack, 1);
	}

	public static double getItemAttackDamage(EquipmentSlotGroup slots, ItemStack stack, double baseValue)
	{
		AtomicReference<Double> d0 = new AtomicReference<>(baseValue);
		stack.getAttributeModifiers().forEach(slots, ((attributeHolder, attributeModifier) ->
		{
			double d1 = attributeModifier.amount();

			d0.updateAndGet(v -> v + switch (attributeModifier.operation())
			{
				case ADD_VALUE -> d1;
				case ADD_MULTIPLIED_BASE -> d1 * baseValue;
				case ADD_MULTIPLIED_TOTAL -> d1 * d0.get();
			});
		}));
		return d0.get();
	}

	public static void playRootedParticles(RootedItemBlockEntity root, RandomSource random, ParticleOptions particles)
	{
		BlockPos pPos = root.getBlockPos();
		Level pLevel = root.getLevel();

		double d0 = pPos.getX();
		double d1 = pPos.getY();
		double d2 = pPos.getZ();

		double d5 = random.nextDouble();
		double d6 = random.nextDouble();
		double d7 = random.nextDouble();
		pLevel.addParticle(particles, d0 + d5, d1 + d6, d2 + d7, 0.0D, 0.0D, 0.0D);
	}

	public void onKill(LivingEntity target, LivingEntity user, ItemStack stack, LivingDeathEvent event) {
	}

	public void onUserDeath(LivingEntity entity, ItemStack stack, EquipmentSlot slot, LivingDeathEvent event) {
	}

	public boolean onInfusedByDormantProperty(ItemStack stack, ItemStack propertySource, ForgeRecipeGrid grid, List<Holder<Property>> propertiesToAdd)
	{
		return !InfusedPropertiesHelper.hasInfusedProperty(stack, asHolder());
	}

	public void onInfusedByForgeRecipe(ItemStack stack, RecipeHolder<ForgePropertyRecipe> recipe, ForgeRecipeGrid grid)
	{

	}

	public void onStopUsingItem(ItemStack stack, LivingEntity user, LivingEntityUseItemEvent.Stop event) {

	}

	public void onProjectileTick(ItemStack stack, Projectile projectile) {

	}

	public void onProjectileImpact(ItemStack stack, Projectile projectile, HitResult rayTraceResult, ProjectileImpactEvent event) {

	}

	public void onStackedOverItem(ItemStack stackedOnItem, ItemStack carriedItem, Player player, ClickAction clickAction, ItemStackedOnOtherEvent event) {

	}

	public void onStackedOverMe(ItemStack carriedItem, ItemStack stackedOnItem, Player player, ClickAction clickAction, ItemStackedOnOtherEvent event) {

	}

	public void onPickUpAnyItem(Player user, ItemStack stack, EquipmentSlot slot, ItemEntity itemToPickUp, boolean canPickUp, ItemEntityPickupEvent.Pre event) {

	}

	public Collection<ItemStack> populateCreativeTab(DeferredItem<Item> capsuleItem, Holder<Property> holder)
	{
		return List.of(InfusedPropertiesHelper.storeProperties(capsuleItem.toStack(), holder));
	}

	public Optional<UseAnim> modifyUseAnimation(ItemStack stack, UseAnim original, Optional<UseAnim> current) {
		return current;
	}

	public EquipmentSlot modifyWearableSlot(ItemStack stack, @Nullable EquipmentSlot originalSlot, @Nullable EquipmentSlot slot) {
		return slot;
	}

	public void onItemUseTick(LivingEntity user, ItemStack stack, LivingEntityUseItemEvent.Tick event) {

	}

	/**
	 *
	 * @param arrow The AbstractArrow entity sourced from a Property-containing item
	 * @param stack The Property-containing item the entity originates from (the projectile ammo, not the weapon)
	 * @return Whether the shot arrow can clip through blocks
	 */
	public TriState allowArrowClipBlocks(AbstractArrow arrow, ItemStack stack) {
		return TriState.DEFAULT;
	}

	@Nullable
	public ItemInteractionResult onRootedRightClick(RootedItemBlockEntity root, Player user, InteractionHand hand, BlockHitResult hitResult) {
		return null;
	}

	/**
	 * @return whether or not the datagen should generate an entry for this property in the Alchemancer's Journal
	 */
	public boolean hasJournalEntry()
	{
		return true;
	}

	public InfusionPropertyDispenseBehavior.DispenseResult onItemDispense(BlockSource blockSource, Direction direction, ItemStack stack, InfusionPropertyDispenseBehavior.DispenseResult currentResult) {
		return InfusionPropertyDispenseBehavior.DispenseResult.PASS;
	}

	public static class Priority
	{
		public static final int LOWEST = Integer.MAX_VALUE;
		public static final int LOWER = 100;
		public static final int LOW = 50;
		public static final int NORMAL = 0;
		public static final int HIGH = -50;
		public static final int HIGHER = -100;
		public static final int HIGHEST = Integer.MIN_VALUE;
	}

}
