package net.cibernet.alchemancy.util;

import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;

public enum PropertyFunction implements StringRepresentable {
	ATTRIBUTE_MODIFIER("attribute_modifier"),
	ON_ATTACK("on_attack"),
	MODIFY_DAMAGE("modify_damage"),
	ON_KILL("on_kill"),
	ON_CRIT("on_crit"),
	ACTIVATE("activate"),
	ACTIVATE_BY_BLOCK("activate_by_block"),
	WHEN_HIT_HELD("when_hit_held"),
	WHEN_HIT_WORN("when_hit_worn"),
	WHEN_HIT_EQUIPPED("when_hit_equipped"),
	WHEN_HIT_USING("when_hit_using"),
	WHEN_HIT_WORN_OR_USING("when_hit_worn_or_using"),
	RECEIVE_DAMAGE_HELD("receive_damage_held"),
	RECEIVE_DAMAGE_WORN("receive_damage_worn"),
	RECEIVE_DAMAGE_EQUIPPED("receive_damage_equipped"),
	RECEIVE_DAMAGE_USING("receive_damage_using"),
	RECEIVE_DAMAGE_WORN_OR_USING("receive_damage_worn_or_using"),
	WHILE_WORN_HELMET("while_worn_helmet"),
	WHILE_WORN_CHESTPLATE("while_worn_chestplate"),
	WHILE_WORN_LEGGINGS("while_worn_leggings"),
	WHILE_WORN_BOOTS("while_worn_boots"),
	WHILE_WORN_LOWER("while_worn_lower"),
	WHILE_WORN_MIDDLE("while_worn_middle"),
	WHILE_WORN("while_worn"),
	WHILE_EQUIPPED("while_equipped"),
	WHILE_HELD("while_held"),
	WHILE_HELD_MAINHAND("while_held_mainhand"),
	WHILE_HELD_OFFHAND("while_held_offhand"),
	WHILE_IN_INVENTORY("while_in_inventory"),
	WHILE_ROOTED("while_rooted"),
	WHEN_SHOT("when_shot"),
	WHEN_SHOT_FROM_DISPENSER("when_shot_from_dispenser"),
	WHEN_DROPPED("when_dropped"),
	WHEN_USED("when_used"),
	WHEN_USED_BLOCK("when_used_block"),
	WHEN_USED_ENTITY("when_used_entity"),
	WHILE_USING("while_using"),
	AFTER_USE("after_use"),
	BLOCK_DESTROYED("block_destroyed"),
	ON_DESTROYED("on_destroy"),
	STACKED_OVER("stacked_over"),
	STACKED_ON("stacked_on"),
	PICK_UP("pick_up"),
	PICK_UP_WHILE_EQUIPPED("pick_up_while_equipped"),
	PICK_UP_WHILE_WORN("pick_up_while_worn"),
	PICK_UP_WHILE_HELD("pick_up_while_held"),
	DURABILITY_CONSUMED("durability_consumed"),
	OTHER("other_effects");
	public final String localizationKey;

	public static final StringRepresentable.EnumCodec<PropertyFunction> CODEC = StringRepresentable.fromEnum(PropertyFunction::values);

	PropertyFunction(String localizationKey) {
		this.localizationKey = localizationKey;
	}

	@Override
	public String getSerializedName() {
		return localizationKey;
	}
}
