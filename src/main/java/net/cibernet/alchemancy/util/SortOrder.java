package net.cibernet.alchemancy.util;

import net.cibernet.alchemancy.data.save.InfusionCodexSaveData;
import net.cibernet.alchemancy.properties.Property;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;

import java.util.Comparator;

public enum SortOrder implements StringRepresentable {
	ALPHABETICAL("alphabetical", (o1, o2) -> 0),
	RECENCY("recency", Comparator.comparingInt(InfusionCodexSaveData::getRecencyIndex)),
	UNLOCK("unlock", Comparator.comparingInt(InfusionCodexSaveData::getUnlockIndex)),
	;
	public final String name;
	public final Component buttonLabel;
	public final Tooltip tooltip;
	public final Comparator<Holder<Property>> sortFunction;


	public static final StringRepresentable.EnumCodec<SortOrder> CODEC = StringRepresentable.fromEnum(SortOrder::values);

	SortOrder(String name, Component buttonLabel, Component tooltip, Comparator<Holder<Property>> sortFunction) {
		this.name = name;
		this.buttonLabel = buttonLabel;
		this.tooltip = CommonUtils.isServerside() ? null : Tooltip.create(Component.translatable("screen.infusion_codex.sort_order", tooltip));
		this.sortFunction = sortFunction;
	}

	SortOrder(String key, Comparator<Holder<Property>> sortFunction) {
		this(key, Component.translatable("screen.infusion_codex.sort_button." + key), Component.translatable("screen.infusion_codex.sort_order." + key), sortFunction);
	}

	@Override
	public String getSerializedName() {
		return name;
	}

}