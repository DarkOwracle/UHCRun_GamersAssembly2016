package com.ga16.uhcrun.blockpopulator;

import java.util.Random;

import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.generator.BlockPopulator;

import com.ga16.uhcrun.UHCRun;

public class OrePopulator extends BlockPopulator{

	public static UHCRun plugin;
	
	public OrePopulator(UHCRun instance){
		plugin = instance;
	}
	
	 private static final int[] iterations = new int[]{5, 10, 8, 4, 4, 2};
	 private static final int[] amount = new int[]{4, 10, 8, 8, 8, 4};
	 private static final Material[] type = new Material[]{Material.REDSTONE_ORE, Material.IRON_ORE, Material.LAPIS_ORE, Material.GOLD_ORE, Material.DIAMOND_ORE, Material.OBSIDIAN};
	 private static final int[] maxHeight = new int[]{64, 64, 64, 64, 64, 64};
	
	@Override
	public void populate(World world, Random random, Chunk source) {	
		for(int i = 0; i < type.length; i++){
			for(int j = 0; j < iterations[i]; j++){
				makeOres(source, random, random.nextInt(16), random.nextInt(maxHeight[i]), random.nextInt(16), amount[i], type[i]);
			}
		}
	}
	
	private static void makeOres(Chunk source, Random random, int originX, int originY, int originZ, int amount, Material type) {
        for (int i = 0; i < amount; i++) {
            int x = originX + random.nextInt(amount / 2) - amount / 4;
            int y = originY + random.nextInt(amount / 4) - amount / 8;
            int z = originZ + random.nextInt(amount / 2) - amount / 4;
            x &= 0xf;
            z &= 0xf;
            if (y > 127 || y < 0) {
                continue;
            }
            Block block = source.getBlock(x, y, z);
            if (block.getType() == Material.STONE) {
                block.setType(type, false);
            }
        }
    }
}
