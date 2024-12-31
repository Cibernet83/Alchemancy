package net.cibernet.alchemancy.properties;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShearsItem;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;
import net.neoforged.neoforge.event.entity.ProjectileImpactEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.entity.player.UseItemOnBlockEvent;
import org.apache.logging.log4j.util.TriConsumer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

public class ToolProperty extends Property
{
	public final int color;
	public final List<RuleFunc> rules;
	public final Set<ItemAbility> abilities;

	private static final HashMap<Tool, Tool> CACHED_TOOLS = new HashMap<>();
	private final Tool DEFAULT;

	public static final HashMap<ItemAbility, TriConsumer<Player, Level, BlockPos>> INTERACTION_EFFECTS = new HashMap<>() //bit annoying that these're hardcoded into each separate tool class instead of being included with the item abilities but w/e
	{{
		put(ItemAbilities.AXE_STRIP, ((player, level, pos) ->
				level.playSound(player, pos, SoundEvents.AXE_STRIP, SoundSource.BLOCKS, 1.0F, 1.0F)));
		put(ItemAbilities.AXE_SCRAPE, ((player, level, pos) ->
		{
			level.playSound(player, pos, SoundEvents.AXE_SCRAPE, SoundSource.BLOCKS, 1.0F, 1.0F);
			level.levelEvent(player, 3005, pos, 0);
		}));
		put(ItemAbilities.AXE_WAX_OFF, ((player, level, pos) ->
		{
			level.playSound(player, pos, SoundEvents.AXE_WAX_OFF, SoundSource.BLOCKS, 1.0F, 1.0F);
			level.levelEvent(player, 3004, pos, 0);
		}));
		put(ItemAbilities.SHOVEL_FLATTEN, ((player, level, pos) ->
				level.playSound(player, pos, SoundEvents.SHOVEL_FLATTEN, SoundSource.BLOCKS, 1.0F, 1.0F)));
		put(ItemAbilities.SHOVEL_DOUSE, ((player, level, pos) ->
		{
			if (!level.isClientSide()) {
				level.levelEvent(null, 1009, pos, 0);
			}
		}));
		put(ItemAbilities.HOE_TILL, ((player, level, pos) ->
				level.playSound(player, pos, SoundEvents.HOE_TILL, SoundSource.BLOCKS, 1.0F, 1.0F)));
		put(ItemAbilities.FIRESTARTER_LIGHT, ((player, level, pos) ->
				level.playSound(player, pos, SoundEvents.FLINTANDSTEEL_USE, SoundSource.BLOCKS, 1.0F, level.getRandom().nextFloat() * 0.4F + 0.8F)));

	}};
	
	public ToolProperty(int color, TagKey<Block> allowedBlocks, Set<ItemAbility> abilities)
	{
		this(color, List.of((miningSpeed) -> Tool.Rule.minesAndDrops(allowedBlocks, miningSpeed)), abilities);
	}

	public ToolProperty(int color, List<RuleFunc> toolRules, Set<ItemAbility> abilities)
	{
		this.color = color;
		this.abilities = abilities;
		rules = toolRules;

		List<Tool.Rule> defaultRules = new ArrayList<>(rules.stream().map(ruleFunc -> ruleFunc.apply(2f)).toList());
		defaultRules.add(Tool.Rule.deniesDrops(BlockTags.INCORRECT_FOR_WOODEN_TOOL));
		DEFAULT = new Tool(defaultRules, 1, 1);
	}

	public static List<RuleFunc> getRulesFor(Tool tool)
	{
		ArrayList<RuleFunc> result = new ArrayList<>();
		for (Tool.Rule rule : tool.rules()) {
			result.add((miningSpeed) -> rule);
		}
		return result;
	}

	public static List<RuleFunc> getShearsRules()
	{
		return getRulesFor(ShearsItem.createToolProperties());
	}

	public static List<RuleFunc> getSwordRules()
	{
		return getRulesFor(SwordItem.createToolProperties());
	}

	@Override
	public void onProjectileImpact(ItemStack stack, Projectile projectile, HitResult rayTraceResult, ProjectileImpactEvent event)
	{
		if(rayTraceResult.getType() == HitResult.Type.BLOCK && rayTraceResult instanceof BlockHitResult blockHitResult)
		{
			Level level = projectile.level();
			Tool tool = stack.get(DataComponents.TOOL);
			BlockState blockHit = level.getBlockState(blockHitResult.getBlockPos());

			if(tool != null && tool.isCorrectForDrops(blockHit) && tool.getMiningSpeed(blockHit) <= projectile.getKnownMovement().length())
			{
				level.destroyBlock(blockHitResult.getBlockPos(), true, projectile);
			}
		}
	}

	@Override
	public void onRightClickBlock(UseItemOnBlockEvent event)
	{
		Level level = event.getLevel();
		BlockPos pos = event.getPos();
		BlockState state = level.getBlockState(pos);
		Player player = event.getPlayer();

		for (ItemAbility ability : abilities) {

			BlockState modifiedState = state.getToolModifiedState(event.getUseOnContext(), ability, false);

			if(modifiedState == null)
				continue;

			ItemStack itemstack = event.getItemStack();
			if (player instanceof ServerPlayer) {
				CriteriaTriggers.ITEM_USED_ON_BLOCK.trigger((ServerPlayer)player, pos, itemstack);
			}

			if(INTERACTION_EFFECTS.containsKey(ability))
				INTERACTION_EFFECTS.get(ability).accept(player, level, pos);

			level.setBlock(pos, modifiedState, 11);
			level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(player, modifiedState));
			if(itemstack.isDamageableItem())
				itemstack.hurtAndBreak(1, player, LivingEntity.getSlotForHand(event.getHand()));

			event.cancelWithResult(ItemInteractionResult.sidedSuccess(level.isClientSide));
			event.setCanceled(true);
			return;
		}
	}

	@Override
	public <T> Object modifyDataComponent(ItemStack stack, DataComponentType<? extends T> dataType, T data)
	{
		if(!this.rules.isEmpty() && dataType == DataComponents.TOOL)
		{
			if(data instanceof Tool tool)
			{
				if (!CACHED_TOOLS.containsKey(tool)) {
					float miningSpeed = 1;

					for (Tool.Rule rule : tool.rules()) {
						if (rule.correctForDrops().isPresent() && rule.correctForDrops().get() && rule.speed().isPresent() && rule.speed().get() > miningSpeed)
							miningSpeed = rule.speed().get();
					}

					List<Tool.Rule> rules = new ArrayList<>(tool.rules());
					final float finalMiningSpeed = miningSpeed;
					rules.addAll(this.rules.stream().map(ruleFunc -> ruleFunc.apply(finalMiningSpeed)).toList());
					CACHED_TOOLS.put(tool, new Tool(rules, tool.defaultMiningSpeed(), tool.damagePerBlock()));
				}
				return CACHED_TOOLS.get(tool);
			} else return DEFAULT;
		}
		return super.modifyDataComponent(stack, dataType, data);
	}

	@Override
	public boolean modifyAcceptAbility(ItemStack stack, ItemAbility itemAbility, boolean original, boolean result) {
		return result || abilities.contains(itemAbility);
	}

	@Override
	public int getColor(ItemStack stack) {
		return color;
	}

	public interface RuleFunc extends Function<Float, Tool.Rule> {}
}
