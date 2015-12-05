package com.ga16.uhcrun.blockpopulator;

import java.io.InputStream;
import java.util.Random;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.block.Skull;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.generator.BlockPopulator;

import com.ga16.uhcrun.UHCRun;

public class LobbyPopulator extends BlockPopulator {

	public String filename = "Lobby.schematic";
	
	public static UHCRun plugin;

	public LobbyPopulator(UHCRun instance){
		plugin = instance;
	}
	
	public static LobbyPopulator lobbyPopulator = new LobbyPopulator(plugin);

	@SuppressWarnings("deprecation")
	@Override
	public void populate(World world, Random rand, Chunk chunk) {

		if (chunk.getX() == 0 && chunk.getZ() == 0) {
			try {
				InputStream is = plugin.getClass().getClassLoader().getResourceAsStream(filename);
				SchematicsManager man = new SchematicsManager();
				man.loadGzipedSchematic(is);

				int width = man.getWidth();
				int height = man.getHeight();
				int length = man.getLength();

				int starty = 139;
				int endy = starty + height;
				
				boolean skull1 = false, skull2 = false, skull3 = false, sign1 = false;

				for (int x = 0; x < width; x++) {
					for (int z = 0; z < length; z++) {
						int realX = x + chunk.getX() * 16;
						int realZ = z + chunk.getZ() * 16;

						for (int y = starty; y <= endy && y < 255; y++) {

							int rely = y - starty;
							int id = man.getBlockIdAt(x, rely, z);
							byte data = man.getMetadataAt(x, rely, z);
							
							if(id == -82 && world.getBlockAt(realX, y, realZ) != null){
								world.getBlockAt(realX, y, realZ).setTypeIdAndData(174, data, true);
							}
							
							if(id == -90 && world.getBlockAt(realX, y, realZ) != null){
								world.getBlockAt(realX, y, realZ).setTypeIdAndData(166, data, true);
							}
							
							if(id == -112 && world.getBlockAt(realX, y, realZ) != null){
								world.getBlockAt(realX, y, realZ).setTypeIdAndData(144, data, true);
							}
							
							if (id > -1 && world.getBlockAt(realX, y, realZ) != null){
								world.getBlockAt(realX, y, realZ).setTypeIdAndData(id, data, true);
							}
							
							if(world.getBlockAt(realX, y, realZ).getType() == Material.WALL_SIGN){
								Sign sign = (Sign) world.getBlockAt(realX, y, realZ).getState();
								if(!sign1){
									sign.setLine(1, "End of parkour");
									sign.setLine(3,  "Good game!");
									sign.update();
									sign1 = true;
								}else
									continue;
							}
							
							if(world.getBlockAt(realX, y, realZ).getType() == Material.SKULL){
								Skull skull = (Skull) world.getBlockAt(realX, y, realZ).getState();
								if(!skull1){
									skull.setOwner("MHF_ArrowRight");
									skull.setRotation(BlockFace.SOUTH_EAST);
									skull.update();
									skull1 = true;
								}else if(skull1 && !skull2){
									skull.setOwner("MHF_ArrowLeft");
									skull.setRotation(BlockFace.SOUTH_EAST);
									skull.update();
									skull2 = true;
								}else if(skull1 && skull2 && !skull3){
									skull.setOwner("MHF_Steve");
									skull.update();
									skull3 = true;
								}else
									continue;
							}
						}
					}
				}
				
				if(world.getBlockAt(17, 175, 11).getType() == Material.AIR)
					world.getBlockAt(17, 175, 11).setType(Material.LADDER);
				if(world.getBlockAt(17, 176, 11).getType() == Material.AIR)
					world.getBlockAt(17, 176, 11).setType(Material.LADDER);
				if(world.getBlockAt(17, 177, 11).getType() == Material.AIR)
					world.getBlockAt(17, 177, 11).setType(Material.LADDER);
				if(world.getBlockAt(17, 178, 11).getType() == Material.AIR)
					world.getBlockAt(17, 178, 11).setType(Material.LADDER);
				
				Location loc1 = new Location(world, 0, 130, 0);
				Location loc2 = new Location(world, 30, 190, 44);
				int minX = Math.min(loc1.getBlockX(), loc2.getBlockX());
				int minY = Math.min(loc1.getBlockY(), loc2.getBlockY());
				int minZ = Math.min(loc1.getBlockZ(), loc2.getBlockZ());
				int maxX = Math.max(loc1.getBlockX(), loc2.getBlockX());
				int maxY = Math.max(loc1.getBlockY(), loc2.getBlockY());
				int maxZ = Math.max(loc1.getBlockZ(), loc2.getBlockZ());

				for(int x = minX; x <= maxX; x++){
					for(int y = minY; y <= maxY; y++){
						for(int z = minZ; z <= maxZ; z++){
							for(Entity entity : world.getEntities()){
								if(entity instanceof Item)
									entity.remove();
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