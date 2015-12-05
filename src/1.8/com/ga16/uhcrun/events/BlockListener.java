package com.ga16.uhcrun.events;

import java.util.Random;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.world.PortalCreateEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import com.ga16.uhcrun.UHCRun;

public class BlockListener implements Listener {
	
	public static UHCRun plugin;
	
	private final Random rand = new Random();
	
	public BlockListener(UHCRun instance){
		plugin = instance;

	}
	
	@EventHandler
	public void onLeaveDecay(LeavesDecayEvent event){
		Block block = event.getBlock();
		int i = rand.nextInt(101);
		event.setCancelled(true);
		block.setType(Material.AIR);
		if(i <= 1)
			block.getWorld().dropItem(block.getLocation().add(0.5, 0.5, 0.5), new ItemStack(Material.APPLE, 1)).setVelocity(new Vector(0, 0, 0));
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent event){
		if(plugin.started){
			Material block = event.getBlock().getType();
			if(block == Material.IRON_ORE){
				event.setCancelled(true);
				event.getBlock().setType(Material.AIR);
				event.getBlock().getWorld().dropItem(event.getBlock().getLocation().add(0.5, 0.5, 0.5), new ItemStack(Material.IRON_INGOT, 2)).setVelocity(new Vector(0, 0, 0));
				event.getPlayer().giveExp(2);
			}else if(block == Material.GOLD_ORE){
				event.setCancelled(true);
				event.getBlock().setType(Material.AIR);
				event.getBlock().getWorld().dropItem(event.getBlock().getLocation().add(0.5, 0.5, 0.5), new ItemStack(Material.GOLD_INGOT, 2)).setVelocity(new Vector(0, 0, 0));
				event.getPlayer().giveExp(3);
			}else if(block == Material.DIAMOND_ORE){
				event.setCancelled(true);
				event.getBlock().setType(Material.AIR);
				event.getBlock().getWorld().dropItem(event.getBlock().getLocation().add(0.5, 0.5, 0.5), new ItemStack(Material.DIAMOND, 2)).setVelocity(new Vector(0, 0, 0));
				event.getPlayer().giveExp(5);
			}else if(block == Material.COAL_ORE){
				event.setCancelled(true);
				event.getBlock().setType(Material.AIR);
				event.getBlock().getWorld().dropItem(event.getBlock().getLocation().add(0.5, 0.5, 0.5), new ItemStack(Material.TORCH, 4)).setVelocity(new Vector(0, 0, 0));
				event.getPlayer().giveExp(1);
			}else if(block == Material.SAND){
				event.setCancelled(true);
				event.getBlock().setType(Material.AIR);
				event.getBlock().getWorld().dropItem(event.getBlock().getLocation().add(0.5, 0.5, 0.5), new ItemStack(Material.GLASS, 1)).setVelocity(new Vector(0, 0, 0));
			}else if(block == Material.GRAVEL){
				event.setCancelled(true);
				event.getBlock().setType(Material.AIR);
				if(Math.random() < 0.4){
					event.getBlock().getWorld().dropItem(event.getBlock().getLocation().add(0.5, 0.5, 0.5), new ItemStack(Material.ARROW, 4)).setVelocity(new Vector(0, 0, 0));
				}else if(Math.random() < 0.7){
					event.getBlock().getWorld().dropItem(event.getBlock().getLocation().add(0.5, 0.5, 0.5), new ItemStack(Material.FLINT, 1)).setVelocity(new Vector(0, 0, 0));
				}else{
					event.getBlock().getWorld().dropItem(event.getBlock().getLocation().add(0.5, 0.5, 0.5), new ItemStack(Material.GRAVEL, 1)).setVelocity(new Vector(0, 0, 0));
				}
			}else if(block == Material.LOG || block == Material.LOG_2){
				Location loc = event.getBlock().getLocation();
		        final World world = loc.getWorld();
		        final int x = loc.getBlockX();
		        final int y = loc.getBlockY();
		        final int z = loc.getBlockZ();
		        final int range = 4;
		        final int off = range + 1;
		        
		        if(!validChunk(world, x - off, y - off, z - off, x + off, y + off, z + off))
		        {
		            return;
		        }
		        
		        plugin.getServer().getScheduler().runTask(plugin, new Runnable()
		        {
		            @SuppressWarnings("deprecation")
					@Override
		            public void run()
		            {
		                for(int offX = -range; offX <= range; offX++)
		                {
		                    for(int offY = -range; offY <= range; offY++)
		                    {
		                        for(int offZ = -range; offZ <= range; offZ++)
		                        {
		                            if(world.getBlockTypeIdAt(x + offX, y + offY, z + offZ) == Material.LEAVES.getId() || world.getBlockTypeIdAt(x + offX, y + offY, z + offZ) == Material.LEAVES_2.getId())
		                            {
		                                breakLeaf(world, x + offX, y + offY, z + offZ);
		                            }
		                        }
		                    }
		                }
		            }
		        });
		        breakTree(event.getBlock(), event.getPlayer());
			}else{
				return;
			}
		}else{
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onPortalCreate(PortalCreateEvent event){
		event.setCancelled(true);
	}
	
	public void breakTree(Block log, Player player){
		if(log.getType() != Material.LOG && log.getType() != Material.LOG_2)
			return;
		log.breakNaturally();
		BlockBreakEvent event = null;
		for(BlockFace face : BlockFace.values()){
			event = new BlockBreakEvent(log.getRelative(face), player);
			plugin.getServer().getPluginManager().callEvent(event);
			event = new BlockBreakEvent(log.getRelative(face).getRelative(BlockFace.UP), player);
			plugin.getServer().getPluginManager().callEvent(event);
		}
	}
	
	@SuppressWarnings("deprecation")
	private void breakLeaf(World world, int x, int y, int z)
	{
		Block block = world.getBlockAt(x, y, z);

		byte range = 4;
		byte max = 32;
		int[] blocks = new int[max * max * max];
		int off = range + 1;
		int mul = max * max;
		int div = max / 2;

		if(validChunk(world, x - off, y - off, z - off, x + off, y + off, z + off))
		{
			int offX;
			int offY;
			int offZ;
			int type;

			for(offX = -range; offX <= range; offX++)
			{
				for(offY = -range; offY <= range; offY++)
				{
					for(offZ = -range; offZ <= range; offZ++)
					{
						type = world.getBlockTypeIdAt(x + offX, y + offY, z + offZ);
						blocks[(offX + div) * mul + (offY + div) * max + offZ + div] = ((type == Material.LOG.getId() || type == Material.LOG_2.getId()) ? 0 : ((type == Material.LEAVES.getId() || type == Material.LEAVES_2.getId()) ? -2 : -1));
					}
				}
			}

			for(offX = 1; offX <= 4; offX++)
			{
				for(offY = -range; offY <= range; offY++)
				{
					for(offZ = -range; offZ <= range; offZ++)
					{
						for(type = -range; type <= range; type++)
						{
							if(blocks[(offY + div) * mul + (offZ + div) * max + type + div] == offX - 1)
							{
								if(blocks[(offY + div - 1) * mul + (offZ + div) * max + type + div] == -2)
									blocks[(offY + div - 1) * mul + (offZ + div) * max + type + div] = offX;

								if(blocks[(offY + div + 1) * mul + (offZ + div) * max + type + div] == -2)
									blocks[(offY + div + 1) * mul + (offZ + div) * max + type + div] = offX;

								if(blocks[(offY + div) * mul + (offZ + div - 1) * max + type + div] == -2)
									blocks[(offY + div) * mul + (offZ + div - 1) * max + type + div] = offX;

								if(blocks[(offY + div) * mul + (offZ + div + 1) * max + type + div] == -2)
									blocks[(offY + div) * mul + (offZ + div + 1) * max + type + div] = offX;

								if(blocks[(offY + div) * mul + (offZ + div) * max + (type + div - 1)] == -2)
									blocks[(offY + div) * mul + (offZ + div) * max + (type + div - 1)] = offX;

								if(blocks[(offY + div) * mul + (offZ + div) * max + type + div + 1] == -2)
									blocks[(offY + div) * mul + (offZ + div) * max + type + div + 1] = offX;
							}
						}
					}
				}
			}
		}

		if(blocks[div * mul + div * max + div] < 0)
		{
			Location location = block.getLocation();
			LeavesDecayEvent event = new LeavesDecayEvent(block);
			plugin.getServer().getPluginManager().callEvent(event);
			plugin.world.playEffect(location, Effect.STEP_SOUND, Material.LEAVES.getId());
		}
	}

	public boolean validChunk(World world, int minX, int minY, int minZ, int maxX, int maxY, int maxZ)
	{
		if(maxY >= 0 && minY < world.getMaxHeight())
		{
			minX >>= 4;
		minZ >>= 4;
				maxX >>= 4;
					maxZ >>= 4;

			for(int x = minX; x <= maxX; x++)
			{
				for(int z = minZ; z <= maxZ; z++)
				{
					if(!world.isChunkLoaded(x, z))
					{
						return false;
					}
				}
			}

			return true;
		}

		return false;
	}
}
