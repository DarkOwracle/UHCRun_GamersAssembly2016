package com.ga16.uhcrun.events;

import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldInitEvent;
import org.bukkit.generator.BlockPopulator;

import com.ga16.uhcrun.UHCRun;
import com.ga16.uhcrun.blockpopulator.LobbyPopulator;
import com.ga16.uhcrun.blockpopulator.NetherPopulator;
import com.ga16.uhcrun.blockpopulator.OrePopulator;

public class WorldListener implements Listener
{
	public static UHCRun plugin;
	
	public WorldListener(UHCRun instance){
		plugin = instance;

	}
	
	@EventHandler
	public void onWorldInit(WorldInitEvent event){
		for(BlockPopulator pop : event.getWorld().getPopulators()){
			if(pop instanceof OrePopulator)
				return;
			else if(pop instanceof NetherPopulator)
				return;
			else if(pop instanceof LobbyPopulator)
				return;
		}
		
		if(event.getWorld().getEnvironment() == World.Environment.NORMAL){
			event.getWorld().getPopulators().add(new OrePopulator(plugin));
			event.getWorld().getPopulators().add(new NetherPopulator(plugin));
			event.getWorld().getPopulators().add(new LobbyPopulator(plugin));
		}
	}
}
