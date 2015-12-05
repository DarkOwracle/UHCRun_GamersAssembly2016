package com.ga16.uhcrun.blockpopulator;

import java.io.InputStream;
import java.util.Random;

import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;

import com.ga16.uhcrun.UHCRun;

public class NetherPopulator extends BlockPopulator {

	public String filename = "Nether.schematic";
	
	public static UHCRun plugin;
	
	public NetherPopulator(UHCRun instance){
		plugin = instance;

	}

	@SuppressWarnings("deprecation")
	@Override
	public void populate(World world, Random rand, Chunk chunk) {

		if (chunk.getX() % 20 == 0 && chunk.getZ() % 20 == 0) {

			try {
				InputStream is = plugin.getClass().getClassLoader().getResourceAsStream(filename);
				SchematicsManager man = new SchematicsManager();
				man.loadGzipedSchematic(is);

				int width = man.getWidth();
				int height = man.getHeight();
				int length = man.getLength();

				int starty = 10;
				int endy = starty + height;
				
				boolean chest1Filled = false, chest2Filled = false, chest3Filled = false, chest4Filled = false, chest5Filled = false, chest6Filled = false;

				for (int x = 0; x < width; x++) {
					for (int z = 0; z < length; z++) {
						int realX = x + chunk.getX() * 16;
						int realZ = z + chunk.getZ() * 16;

						for (int y = starty; y <= endy && y < 255; y++) {

							int rely = y - starty;
							int id = man.getBlockIdAt(x, rely, z);
							byte data = man.getMetadataAt(x, rely, z);
							
							if(id == -103)
								world.getBlockAt(realX, y, realZ).setTypeIdAndData(153, data, true);
							
							if (id > -1 && world.getBlockAt(realX, y, realZ) != null){
								world.getBlockAt(realX, y, realZ).setTypeIdAndData(id, data, true);
							}
							
							if(world.getBlockAt(realX, y, realZ).getType() == Material.MOB_SPAWNER){
								BlockState state = world.getBlockAt(realX, y, realZ).getState();
								if(state instanceof CreatureSpawner){
									((CreatureSpawner) state).setSpawnedType(EntityType.BLAZE);
								}
							}else if(world.getBlockAt(realX, y, realZ).getType() == Material.CHEST){
								BlockState state = world.getBlockAt(realX, y, realZ).getState();
								if(state instanceof Chest){
									Inventory chest = ((Chest) state).getInventory();
									if(!chest1Filled){
										chest.setItem(7, new ItemStack(Material.DIAMOND, 2));
										chest.setItem(10, new ItemStack(Material.SUGAR_CANE, 6));
										ItemStack Thorns3 = new ItemStack(Material.ENCHANTED_BOOK);
										EnchantmentStorageMeta meta = (EnchantmentStorageMeta) Thorns3.getItemMeta();
										meta.addStoredEnchant(Enchantment.THORNS, 3, false);
										Thorns3.setItemMeta(meta);
										chest.setItem(24, Thorns3);
										chest1Filled = true;
									}else if(chest1Filled && !chest2Filled){
										chest.setItem(1, new ItemStack(Material.GOLD_INGOT, 3));
										chest.setItem(14, new ItemStack(Material.IRON_BARDING, 1));
										chest.setItem(20, new ItemStack(Material.NETHER_STALK, 6));
										
										chest2Filled = true;
									}else if(chest1Filled && chest2Filled && !chest3Filled){
										chest.setItem(3, new ItemStack(Material.FLINT_AND_STEEL, 1));
										chest.setItem(15, new ItemStack(Material.SADDLE, 1));
										chest.setItem(20, new ItemStack(Material.DIAMOND_BARDING, 1));
										chest3Filled = true;
									}else if(chest1Filled && chest2Filled && chest3Filled && !chest4Filled){
										chest.setItem(1, new ItemStack(Material.OBSIDIAN, 1));
										chest.setItem(21, new ItemStack(Material.GOLD_BARDING, 1));
										chest.setItem(26, new ItemStack(Material.DIAMOND, 1));
										chest4Filled = true;
									}else if(chest1Filled && chest2Filled && chest3Filled && chest4Filled && !chest5Filled){
										chest.setItem(16, new ItemStack(Material.IRON_INGOT, 8));
										chest.setItem(20, new ItemStack(Material.GOLD_INGOT, 5));
										chest5Filled = true;
									}else if(chest1Filled && chest2Filled && chest3Filled && chest4Filled && chest5Filled && !chest6Filled){
										chest.setItem(13, new ItemStack(Material.ENDER_PEARL, 1));
										chest6Filled = true;
									}else
										continue;
								}
							}
						}
					}
				}
			} catch(Exception e){
				System.out.println("Could not read the schematic file");
				e.printStackTrace();
			}
		}
	}
}