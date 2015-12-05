package com.ga16.uhcrun.events;

import java.util.Iterator;
import java.util.Random;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import com.ga16.uhcrun.UHCRun;

public class MobListener implements Listener
{
	public static UHCRun plugin;
	public Random rand = new Random();
	
	public MobListener(UHCRun instance){
		plugin = instance;

	}
	
	@EventHandler
	public void onMobDeath(EntityDeathEvent event){
		EntityType mob = event.getEntityType();
		if(mob == EntityType.CHICKEN){
			event.getDrops().clear();
			event.getDrops().add(new ItemStack(Material.COOKED_CHICKEN, 3));
			event.getDrops().add(new ItemStack(Material.ARROW, 4));
		}else if(mob == EntityType.COW){
			event.getDrops().clear();
			event.getDrops().add(new ItemStack(Material.COOKED_BEEF, 3));
			event.getDrops().add(new ItemStack(Material.LEATHER, 1));
		}else if(mob == EntityType.PIG){
			event.getDrops().clear();
			event.getDrops().add(new ItemStack(Material.GRILLED_PORK, 3));
			event.getDrops().add(new ItemStack(Material.LEATHER, 1));
		}else if(mob == EntityType.SHEEP){
			event.getDrops().clear();
			event.getDrops().add(new ItemStack(Material.COOKED_MUTTON, 3));
			event.getDrops().add(new ItemStack(Material.STRING, 1));
		}else if(mob == EntityType.RABBIT){
			event.getDrops().clear();
			event.getDrops().add(new ItemStack(Material.COOKED_RABBIT, 3));
		}else if(mob == EntityType.SQUID){
			event.getDrops().add(new ItemStack(Material.COOKED_FISH, 3));
		}else if(mob == EntityType.SPIDER || mob == EntityType.CAVE_SPIDER){
			event.getDrops().add(new ItemStack(Material.STRING, rand.nextInt(2) + 1));
		}else if(mob == EntityType.SKELETON){
			event.getDrops().add(new ItemStack(Material.ARROW, rand.nextInt(6) + 1));
		}else if(mob == EntityType.CREEPER){
			event.getDrops().add(new ItemStack(Material.SULPHUR, 1));
		}else if(mob == EntityType.WITCH){
			Iterator<ItemStack> drops = event.getDrops().iterator();
			while(drops.hasNext()){
				ItemStack current = drops.next();
				if(current.getType() == Material.GLOWSTONE_DUST)
					drops.remove();
			}
		}else{
			return;
		}
	}
}