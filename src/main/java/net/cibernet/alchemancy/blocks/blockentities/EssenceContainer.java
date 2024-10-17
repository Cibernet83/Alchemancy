package net.cibernet.alchemancy.blocks.blockentities;

import net.cibernet.alchemancy.essence.Essence;
import net.cibernet.alchemancy.registries.AlchemancyEssence;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

import java.util.ArrayList;
import java.util.HashMap;

public class EssenceContainer
{
	Essence essence = null;
	int amount = 0;

	final int limit;

	public EssenceContainer(int limit)
	{
		this.limit = limit;
	}

	public EssenceContainer(EssenceContainer other)
	{
		this(other.essence, other.limit, other.amount);
	}
	public EssenceContainer(Essence essence, int limit, int amount)
	{
		this(limit);
		replace(essence, amount);
	}


	public boolean isEmpty()
	{
		return essence == null || amount <= 0;
	}

	public void clear()
	{
		essence = null;
		amount = 0;
	}

	public void replace(Essence essence, int amount)
	{
		this.essence = essence;
		this.amount = Mth.clamp(amount, 0, limit);
	}

	public boolean canAdd(EssenceContainer container, boolean exact)
	{
		return canAdd(container.essence, container.amount, exact);
	}

	public boolean canAdd(Essence essence, int amount, boolean exact)
	{
		return isEmpty() || (essence == this.essence && (!exact || this.amount + amount <= limit));
	}
	public boolean add(Essence essence, int amount, boolean exact)
	{
		if(isEmpty())
		{
			replace(essence, amount);
			return true;
		}
		else if(canAdd(essence, amount, exact))
		{
			this.amount = Math.min(limit, this.amount + amount);
			return true;
		}

		return false;
	}

	public boolean add(Essence essence, int amount)
	{
		return add(essence, amount, true);
	}

	public boolean canRemove(Essence essence, int amount, boolean exact)
	{
		return !isEmpty() && essence == this.essence && (!exact || this.amount - amount >= 0);
	}

	public boolean remove(Essence essence, int amount, boolean exact)
	{
		if(canRemove(essence, amount, exact))
		{
			this.amount = Math.max(0, this.amount - amount);
			return true;
		}

		return false;
	}

	public boolean remove(Essence essence, int amount)
	{
		return remove(essence, amount, true);
	}

	public int transferTo(EssenceContainer targetContainer, int amount, boolean exact)
	{
		return transferTo(targetContainer, amount, exact, true);
	}
	public int transferTo(EssenceContainer targetContainer, int amount, boolean exact, boolean depleteThis)
	{
		amount = Mth.clamp(amount, 0, exact ? this.amount : Math.min(this.amount, targetContainer.spaceLeft()));
		if(targetContainer.add(this.essence, amount, exact))
		{
			if(depleteThis)
				this.remove(this.essence, amount, exact);
			return amount;
		}
		else return 0;
	}

	private int spaceLeft() {
		return limit - amount;
	}

	public Essence getEssence() {
		return essence;
	}

	public int getAmount() {
		return amount;
	}

	public int getLimit() {
		return limit;
	}

	public void loadFromTag(CompoundTag tag)
	{
		clear();
		if(tag.contains("id", CompoundTag.TAG_STRING) && tag.contains("amount", CompoundTag.TAG_INT))
			replace(AlchemancyEssence.getEssence(ResourceLocation.read(tag.getString("id")).getOrThrow()), tag.getInt("amount"));
	}

	public CompoundTag saveToTag(CompoundTag tag)
	{
		if(!isEmpty())
		{
			tag.putString("id", essence.getKey().toString());
			tag.putInt("amount", amount);
		}

		return tag;
	}

	public boolean isFull() {
		return amount >= limit;
	}

	public static ArrayList<EssenceContainer> collapseDuplicates(ArrayList<EssenceContainer> list)
	{
		ArrayList<EssenceContainer> collapsedList = new ArrayList<>();

		for (EssenceContainer essenceContainer : list)
		{
			boolean inserted = false;
			for (EssenceContainer collapsedEssenceContainer : collapsedList) {
				if(collapsedEssenceContainer.add(essenceContainer.essence, essenceContainer.amount))
				{
					inserted = true;
					break;
				}
			}

			if(!inserted)
				collapsedList.add(new EssenceContainer(essenceContainer.essence, Integer.MAX_VALUE, essenceContainer.amount));
		}

		return collapsedList;
	}
}
