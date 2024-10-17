package net.cibernet.alchemancy.events.handler;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import java.util.HashMap;
import java.util.UUID;

@EventBusSubscriber
public class GeneralEventHandler
{
	@SubscribeEvent
	public static void onLevelTick(LevelTickEvent.Pre event)
	{
		if(event.getLevel().isClientSide())
			event.getLevel().updateSkyBrightness(); //Updating sky brightness on the client so skylight-dependent operations (i.e. photosynthetic check) can work clientside
	}
}
