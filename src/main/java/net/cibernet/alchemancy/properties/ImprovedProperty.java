package net.cibernet.alchemancy.properties;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.Tool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class ImprovedProperty extends Property
{
	private static final HashMap<Tool, Tool> UPGRADED_TOOLS = new HashMap<>();

	@Override
	public <T> Object modifyDataComponent(ItemStack stack, DataComponentType<? extends T> dataType, T data)
	{
		if(dataType == DataComponents.TOOL && data instanceof Tool tool)
		{
			if(!UPGRADED_TOOLS.containsKey(tool))
			{
				List<Tool.Rule> rules = new ArrayList<>();
				for (Tool.Rule rule : tool.rules()) {
					if(rule.correctForDrops().isPresent() && rule.correctForDrops().get())
						rules.add(new Tool.Rule(rule.blocks(), Optional.of(rule.speed().isPresent() ? Math.max(rule.speed().get(), 8) : 8), rule.correctForDrops()));
				}
				rules.add(Tool.Rule.deniesDrops(BlockTags.INCORRECT_FOR_DIAMOND_TOOL));
				UPGRADED_TOOLS.put(tool, new Tool(rules, tool.defaultMiningSpeed(), tool.damagePerBlock()));
			}
			return UPGRADED_TOOLS.get(tool);
		}
		else if (dataType == DataComponents.MAX_DAMAGE && data instanceof Integer i && i < 1600)
			return Math.max((int) data, Math.min(1600, (int)data * 2));
		return super.modifyDataComponent(stack, dataType, data);
	}

	@Override
	public int getColor(ItemStack stack) {
		return 0x4AEDD9;
	}

	@Override
	public int getPriority() {
		return Priority.LOWER;
	}
}
