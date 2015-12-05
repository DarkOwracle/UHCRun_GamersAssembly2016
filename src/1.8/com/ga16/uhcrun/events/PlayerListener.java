package com.ga16.uhcrun.events;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.ga16.uhcrun.UHCRun;

public class PlayerListener implements Listener
{
	public static UHCRun plugin;
	
	public static boolean damage = false;
	public static boolean pvp = false;
	public static int disconnectedPlayers = 0;
	public static int disconnectedTeams = 0;
	
	public PlayerListener(UHCRun instance){
		plugin = instance;
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onPlayerKill(PlayerDeathEvent event){
		if(event.getEntity().getLastDamageCause() == null)
			return;
		
		plugin.board.resetScores(Bukkit.getServer().getOfflinePlayer(ChatColor.GOLD + (plugin.frFR ? "Joueurs restant(s): " + Integer.toString(plugin.playersInt) : "Players left: " + Integer.toString(plugin.playersInt))));
		plugin.playersInt = plugin.playersInt - 1;
		plugin.playersLeft = plugin.timer.getScore(Bukkit.getServer().getOfflinePlayer(ChatColor.GOLD + (plugin.frFR ? "Joueurs restant(s): " + Integer.toString(plugin.playersInt) : "Players left: " + Integer.toString(plugin.playersInt))));
		plugin.playersLeft.setScore(1);
		
		if(plugin.playersInt - disconnectedPlayers == 1 || plugin.playersInt - disconnectedPlayers == 0){
			plugin.board.resetScores(Bukkit.getServer().getOfflinePlayer(ChatColor.GOLD + (plugin.frFR ? "Joueurs restant(s): " + Integer.toString(plugin.playersInt) : "Players left: " + Integer.toString(plugin.playersInt))));
			plugin.playersInt = plugin.playersInt - disconnectedPlayers;
			plugin.playersLeft = plugin.timer.getScore(Bukkit.getServer().getOfflinePlayer(ChatColor.GOLD + (plugin.frFR ? "Joueurs restant(s): " + Integer.toString(plugin.playersInt) : "Players left: " + Integer.toString(plugin.playersInt))));
			plugin.playersLeft.setScore(1);
		}
		
		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "playsound mob.wither.death @a ~ ~ ~ 10000");
		
		Player killed = event.getEntity();
		Player killer = killed.getKiller();
		
		if(plugin.players.contains(killed.getDisplayName()))
			plugin.players.remove(killed.getDisplayName());
		
		if(!plugin.solo){
			if(plugin.board.getPlayerTeam(killed) != null && plugin.board.getPlayerTeam(killed).getSize() == 1){
				plugin.board.resetScores(Bukkit.getServer().getOfflinePlayer(ChatColor.GOLD + (plugin.frFR ? "équipes restante(s): " + Integer.toString(plugin.teamsInt) : "Teams left: " + Integer.toString(plugin.teamsInt))));
				plugin.teamsInt = plugin.teamsInt - 1;
				plugin.teamsLeft = plugin.timer.getScore(Bukkit.getServer().getOfflinePlayer(ChatColor.GOLD + (plugin.frFR ? "équipes restante(s): " + Integer.toString(plugin.teamsInt) : "Teams left: " + Integer.toString(plugin.teamsInt))));
				plugin.teamsLeft.setScore(0);
				if((plugin.frFR ? plugin.board.getPlayerTeam(killed).getName() == "Bleue claire" : plugin.board.getPlayerTeam(killed).getName() == "Light blue"))
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), (plugin.frFR ? "say " + ChatColor.GOLD + "L'équipe " + plugin.board.getPlayerTeam(killed).getPrefix() + "Bleue " + plugin.board.getPlayerTeam(killed).getPrefix() + "claire " + ChatColor.GOLD + "est " + ChatColor.GOLD + "�limin�!" : "say " + plugin.board.getPlayerTeam(killed).getPrefix() + "Light " + plugin.board.getPlayerTeam(killed).getPrefix() + "blue " + ChatColor.GOLD + "team " + ChatColor.GOLD + "has " + ChatColor.GOLD + ChatColor.GOLD + "been " + "eliminated!"));
				else
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), (plugin.frFR ? "say " + ChatColor.GOLD + "L'équipe " + plugin.board.getPlayerTeam(killed).getPrefix() + plugin.board.getPlayerTeam(killed).getName() + " " + ChatColor.GOLD + "est " + ChatColor.GOLD + "�limin�!" : "say " + plugin.board.getPlayerTeam(killed).getPrefix() + plugin.board.getPlayerTeam(killed).getName() + " team " + ChatColor.GOLD + "has " + ChatColor.GOLD + ChatColor.GOLD + "been " + "eliminated!"));
			}if(plugin.board.getPlayerTeam(killed) != null)
				plugin.board.getPlayerTeam(killed).removePlayer(killed);
			
			if(plugin.teamsInt - disconnectedTeams == 1 || plugin.teamsInt - disconnectedTeams == 0){
				plugin.board.resetScores(Bukkit.getServer().getOfflinePlayer(ChatColor.GOLD + (plugin.frFR ? "équipes restante(s): " + Integer.toString(plugin.teamsInt) : "Teams left: " + Integer.toString(plugin.teamsInt))));
				plugin.teamsInt = plugin.teamsInt - disconnectedTeams;
				plugin.teamsLeft = plugin.timer.getScore(Bukkit.getServer().getOfflinePlayer(ChatColor.GOLD + (plugin.frFR ? "équipes restante(s): " + Integer.toString(plugin.teamsInt) : "Teams left: " + Integer.toString(plugin.teamsInt))));
				plugin.teamsLeft.setScore(0);
			}
		}
		
		EntityDamageEvent.DamageCause cause = event.getEntity().getLastDamageCause().getCause();
		if((cause == EntityDamageEvent.DamageCause.ENTITY_ATTACK || cause == EntityDamageEvent.DamageCause.PROJECTILE) && killer != null)
			killer.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 200, 1), true);
		
		killed.getWorld().dropItemNaturally(killed.getLocation(), new ItemStack(Material.GOLDEN_APPLE));
		killed.setGameMode(GameMode.SPECTATOR);
	}
	
	@EventHandler
	public void onPlayerDamage(EntityDamageByEntityEvent event)
	{
		if ((event.getDamager() instanceof Player))
		{
			Player player = (Player)event.getDamager();
			if (player.hasPotionEffect(PotionEffectType.INCREASE_DAMAGE)) {
				for (PotionEffect effect : player.getActivePotionEffects()) {
					if (effect.getType().equals(PotionEffectType.INCREASE_DAMAGE))
					{
						int level = effect.getAmplifier() + 1;

						double newDamage = event.getDamage(EntityDamageEvent.DamageModifier.BASE) / (level * 1.3D + 1.0D) + 12 * level;
						double damagePercent = newDamage / event.getDamage(EntityDamageEvent.DamageModifier.BASE);
						try
						{
							event.setDamage(EntityDamageEvent.DamageModifier.ARMOR, event.getDamage(EntityDamageEvent.DamageModifier.ARMOR) * damagePercent);
						}
						catch (Exception localException) {}
						try
						{
							event.setDamage(EntityDamageEvent.DamageModifier.MAGIC, event.getDamage(EntityDamageEvent.DamageModifier.MAGIC) * damagePercent);
						}
						catch (Exception localException1) {}
						try
						{
							event.setDamage(EntityDamageEvent.DamageModifier.RESISTANCE, event.getDamage(EntityDamageEvent.DamageModifier.RESISTANCE) * damagePercent);
						}
						catch (Exception localException2) {}
						try
						{
							event.setDamage(EntityDamageEvent.DamageModifier.BLOCKING, event.getDamage(EntityDamageEvent.DamageModifier.BLOCKING) * damagePercent);
						}
						catch (Exception localException3) {}
						event.setDamage(EntityDamageEvent.DamageModifier.BASE, newDamage);

						break;
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onPlayerEat(PlayerItemConsumeEvent event){
		if(event.getItem().getType().equals(Material.GOLDEN_APPLE))
			event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 200, 1), true);
		else
			return;
	}
	
	@EventHandler
	public void onPlayerCraft(CraftItemEvent event){
		ItemStack item = event.getCurrentItem();
		HumanEntity crafter = event.getWhoClicked();
		
		if(crafter instanceof Player){
			if(item.getType() == Material.WOOD_SWORD){
				event.setCurrentItem(new ItemStack(Material.STONE_SWORD));
			}else if(item.getType() == Material.WOOD_PICKAXE){
				event.setCurrentItem(new ItemStack(Material.STONE_PICKAXE));
			}else if(item.getType() == Material.WOOD_AXE){
				event.setCurrentItem(new ItemStack(Material.STONE_AXE));
			}else if(item.getType() == Material.WOOD_SPADE){
				event.setCurrentItem(new ItemStack(Material.STONE_SPADE));
			}else if(item.getType() == Material.WOOD_HOE){
				event.setCurrentItem(new ItemStack(Material.STONE_HOE));
			}else if(item.getType() == Material.GOLDEN_APPLE && item.getDurability() == 1){
				event.setCancelled(true);
			}else{
				return;
			}
		}
		
	}
	
	@EventHandler
	public void onDamage(EntityDamageEvent event){
		if(!damage && event.getEntity() instanceof Player)
			event.setCancelled(true);
	}
	
	@EventHandler
	public void onDamageByEntity(EntityDamageByEntityEvent event){
		if(event.getDamager() instanceof Player){
			if(event.getEntity() instanceof Player){
				if(!pvp)
					event.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void onInteract(PlayerInteractEvent event){
		Block block = event.getClickedBlock();
		Player player = event.getPlayer();
		if(player.getItemInHand().getType() == Material.NETHER_STAR && plugin.setup && player.getGameMode() == GameMode.SURVIVAL){
			player.openInventory(plugin.teamInventory);
		}
		if(block != null && block.getType() != Material.OBSIDIAN){
			event.getPlayer().removePotionEffect(PotionEffectType.FAST_DIGGING);
			return;
		}else if(block != null && block.getType() == Material.OBSIDIAN){
			event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, 400, 2));
		}else{
			return;
		}
	}
	
	@EventHandler
	public void onPlayerDropItem(PlayerDropItemEvent event){
		if(event.getItemDrop().getItemStack().getType() == Material.NETHER_STAR){
			event.setCancelled(true);
		}else{
			return;
		}
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onInventoryClick(InventoryClickEvent event){
		Player player = (Player) event.getWhoClicked();
		ItemStack clicked = event.getCurrentItem();
		Inventory inventory = event.getInventory();
		if(player.getGameMode() == GameMode.SURVIVAL && plugin.teamInventory != null && inventory.getName().equals(plugin.teamInventory.getName())){
			if(event.getClick().isKeyboardClick())
				event.setCancelled(true);
			if(clicked.getType() == Material.WOOL && clicked.getDurability() == DyeColor.RED.getData()){
				event.setCancelled(true);
				player.closeInventory();
				if(plugin.board.getTeam(plugin.frFR ? "Rouge" : "Red").getSize() < plugin.teamSize){
					if(plugin.board.getPlayerTeam(player) != null){
						if(plugin.board.getPlayerTeam(player).getName() == (plugin.frFR ? "Rouge" : "Red")){
							player.sendMessage(plugin.frFR ? "[GA UHC] Vous etes deja dans l'équipe Rouge!" : "[GA UHC] You are already in the Red team!");
							return;
						}else
							plugin.board.getPlayerTeam(player).removePlayer(player);
					}
					plugin.board.getTeam(plugin.frFR ? "Rouge" : "Red").addPlayer(player);
					player.sendMessage(plugin.frFR ? "[GA UHC] Vous etes dans l'équipe Rouge!" : "[GA UHC] You have joined the Red team!");
					plugin.updateTeamInventory();
					for(OfflinePlayer teamPlayer : plugin.board.getTeam(plugin.frFR ? "Rouge" : "Red").getPlayers()){
						if(!teamPlayer.isOnline())
							continue;
						if(teamPlayer.getPlayer() == player)
							continue;
						teamPlayer.getPlayer().sendMessage(plugin.board.getPlayerTeam(player).getPrefix() + teamPlayer.getPlayer().getDisplayName() + ChatColor.WHITE + (plugin.frFR ? " a rejoint votre équipe!" : " has joined your team!"));
					}
				}else{
					player.sendMessage(plugin.frFR ? "[GA UHC] L'équipe Rouge est pleine!" : "[GA UHC] The Red team is full!");
				}
			}else if(clicked.getType() == Material.WOOL && clicked.getDurability() == DyeColor.LIGHT_BLUE.getData()){
				event.setCancelled(true);
				player.closeInventory();
				if(plugin.board.getTeam(plugin.frFR ? "Bleue claire" : "Light blue").getSize() < plugin.teamSize){
					if(plugin.board.getPlayerTeam(player) != null){
						if(plugin.board.getPlayerTeam(player).getName() == (plugin.frFR ? "Bleue claire" : "Light blue")){
							player.sendMessage(plugin.frFR ? "[GA UHC] Vous etes deja dans l'équipe Bleue claire!" : "[GA UHC] You are already in the Light blue team!");
							return;
						}else
							plugin.board.getPlayerTeam(player).removePlayer(player);
					}
					plugin.board.getTeam(plugin.frFR ? "Bleue claire" : "Light blue").addPlayer(player);
					player.sendMessage(plugin.frFR ? "[GA UHC] Vous etes dans l'équipe Bleue claire!" : "[GA UHC] You have joined the Light blue team!");
					plugin.updateTeamInventory();
					for(OfflinePlayer teamPlayer : plugin.board.getTeam(plugin.frFR ? "Bleue claire" : "Light blue").getPlayers()){
						if(!teamPlayer.isOnline())
							continue;
						if(teamPlayer.getPlayer() == player)
							continue;
						teamPlayer.getPlayer().sendMessage(plugin.board.getPlayerTeam(player).getPrefix() + teamPlayer.getPlayer().getDisplayName() + ChatColor.WHITE + (plugin.frFR ? " a rejoint votre équipe!" : " has joined your team!"));
					}
				}else{
					player.sendMessage(plugin.frFR ? "[GA UHC] L'équipe Bleue claire est pleine!" : "[GA UHC] The Light blue team is full!");
				}
			}else if(clicked.getType() == Material.WOOL && clicked.getDurability() == DyeColor.BLACK.getData()){
				event.setCancelled(true);
				player.closeInventory();
				if(plugin.board.getTeam(plugin.frFR ? "Noire" : "Black").getSize() < plugin.teamSize){
					if(plugin.board.getPlayerTeam(player) != null){
						if(plugin.board.getPlayerTeam(player).getName() == (plugin.frFR ? "Noire" : "Black")){
							player.sendMessage(plugin.frFR ? "[GA UHC] Vous etes deja dans l'équipe Noire!" : "[GA UHC] You are already in the Black team!");
							return;
						}else
							plugin.board.getPlayerTeam(player).removePlayer(player);
					}
					plugin.board.getTeam(plugin.frFR ? "Noire" : "Black").addPlayer(player);
					player.sendMessage(plugin.frFR ? "[GA UHC] Vous etes dans l'équipe Noire!" : "[GA UHC] You have joined the Black team!");
					plugin.updateTeamInventory();
					for(OfflinePlayer teamPlayer : plugin.board.getTeam(plugin.frFR ? "Noire" : "Black").getPlayers()){
						if(!teamPlayer.isOnline())
							continue;
						if(teamPlayer.getPlayer() == player)
							continue;
						teamPlayer.getPlayer().sendMessage(plugin.board.getPlayerTeam(player).getPrefix() + teamPlayer.getPlayer().getDisplayName() + ChatColor.WHITE + (plugin.frFR ? " a rejoint votre équipe!" : " has joined your team!"));
					}
				}else{
					player.sendMessage(plugin.frFR ? "[GA UHC] L'équipe Noire est pleine!" : "[GA UHC] The Black team is full!");
				}
			}else if(clicked.getType() == Material.WOOL && clicked.getDurability() == DyeColor.WHITE.getData()){
				event.setCancelled(true);
				player.closeInventory();
				if(plugin.board.getTeam(plugin.frFR ? "Blanche" : "White").getSize() < plugin.teamSize){
					if(plugin.board.getPlayerTeam(player) != null){
						if(plugin.board.getPlayerTeam(player).getName() == (plugin.frFR ? "Blanche" : "White")){
							player.sendMessage(plugin.frFR ? "[GA UHC] Vous etes deja dans l'équipe Blanche!" : "[GA UHC] You are already in the White team!");
							return;
						}else
							plugin.board.getPlayerTeam(player).removePlayer(player);
					}
					plugin.board.getTeam(plugin.frFR ? "Blanche" : "White").addPlayer(player);
					player.sendMessage(plugin.frFR ? "[GA UHC] Vous etes dans l'équipe Blanche!" : "[GA UHC] You have joined the White team!");
					plugin.updateTeamInventory();
					for(OfflinePlayer teamPlayer : plugin.board.getTeam(plugin.frFR ? "Blanche" : "White").getPlayers()){
						if(!teamPlayer.isOnline())
							continue;
						if(teamPlayer.getPlayer() == player)
							continue;
						teamPlayer.getPlayer().sendMessage(plugin.board.getPlayerTeam(player).getPrefix() + teamPlayer.getPlayer().getDisplayName() + ChatColor.WHITE + (plugin.frFR ? " a rejoint votre équipe!" : " has joined your team!"));
					}
				}else{
					player.sendMessage(plugin.frFR ? "[GA UHC] L'équipe Blanche est pleine!" : "[GA UHC] The White team is full!");
				}
			}else if(clicked.getType() == Material.WOOL && clicked.getDurability() == DyeColor.BLUE.getData()){
				event.setCancelled(true);
				player.closeInventory();
				if(plugin.board.getTeam(plugin.frFR ? "Bleue" : "Blue").getSize() < plugin.teamSize){
					if(plugin.board.getPlayerTeam(player) != null){
						if(plugin.board.getPlayerTeam(player).getName() == (plugin.frFR ? "Bleue" : "Blue")){
							player.sendMessage(plugin.frFR ? "[GA UHC] Vous etes deja dans l'équipe Bleue!" : "[GA UHC] You are already in the Blue team!");
							return;
						}else
							plugin.board.getPlayerTeam(player).removePlayer(player);
					}
					plugin.board.getTeam(plugin.frFR ? "Bleue" : "Blue").addPlayer(player);
					player.sendMessage(plugin.frFR ? "[GA UHC] Vous etes dans l'équipe Bleue!" : "[GA UHC] You have joined the Blue team!");
					plugin.updateTeamInventory();
					for(OfflinePlayer teamPlayer : plugin.board.getTeam(plugin.frFR ? "Bleue" : "Blue").getPlayers()){
						if(!teamPlayer.isOnline())
							continue;
						if(teamPlayer.getPlayer() == player)
							continue;
						teamPlayer.getPlayer().sendMessage(plugin.board.getPlayerTeam(player).getPrefix() + teamPlayer.getPlayer().getDisplayName() + ChatColor.BLUE + (plugin.frFR ? " a rejoint votre équipe!" : " has joined your team!"));
					}
				}else{
					player.sendMessage(plugin.frFR ? "[GA UHC] L'équipe Bleue est pleine!" : "[GA UHC] The Blue team is full!");
				}
			}else if(clicked.getType() == Material.WOOL && clicked.getDurability() == DyeColor.CYAN.getData()){
				event.setCancelled(true);
				player.closeInventory();
				if(plugin.board.getTeam(plugin.frFR ? "Cyan" : "Cyan").getSize() < plugin.teamSize){
					if(plugin.board.getPlayerTeam(player) != null){
						if(plugin.board.getPlayerTeam(player).getName() == (plugin.frFR ? "Cyan" : "Cyan")){
							player.sendMessage(plugin.frFR ? "[GA UHC] Vous etes deja dans l'équipe Cyan!" : "[GA UHC] You are already in the Cyan team!");
							return;
						}else
							plugin.board.getPlayerTeam(player).removePlayer(player);
					}
					plugin.board.getTeam(plugin.frFR ? "Cyan" : "Cyan").addPlayer(player);
					player.sendMessage(plugin.frFR ? "[GA UHC] Vous etes dans l'équipe Cyan!" : "[GA UHC] You have joined the Cyan team!");
					plugin.updateTeamInventory();
					for(OfflinePlayer teamPlayer : plugin.board.getTeam(plugin.frFR ? "Cyan" : "Cyan").getPlayers()){
						if(!teamPlayer.isOnline())
							continue;
						if(teamPlayer.getPlayer() == player)
							continue;
						teamPlayer.getPlayer().sendMessage(plugin.board.getPlayerTeam(player).getPrefix() + teamPlayer.getPlayer().getDisplayName() + ChatColor.AQUA + (plugin.frFR ? " a rejoint votre équipe!" : " has joined your team!"));
					}
				}else{
					player.sendMessage(plugin.frFR ? "[GA UHC] L'équipe Cyan est pleine!" : "[GA UHC] The Cyan team is full!");
				}
			}else if(clicked.getType() == Material.WOOL && clicked.getDurability() == DyeColor.PURPLE.getData()){
				event.setCancelled(true);
				player.closeInventory();
				if(plugin.board.getTeam(plugin.frFR ? "Violette" : "Purple").getSize() < plugin.teamSize){
					if(plugin.board.getPlayerTeam(player) != null){
						if(plugin.board.getPlayerTeam(player).getName() == (plugin.frFR ? "Violette" : "Purple")){
							player.sendMessage(plugin.frFR ? "[GA UHC] Vous etes deja dans l'équipe Violette!" : "[GA UHC] You are already in the Purple team!");
							return;
						}else
							plugin.board.getPlayerTeam(player).removePlayer(player);
					}
					plugin.board.getTeam(plugin.frFR ? "Violette" : "Purple").addPlayer(player);
					player.sendMessage(plugin.frFR ? "[GA UHC] Vous etes dans l'équipe Violette!" : "[GA UHC] You have joined the Purple team!");
					plugin.updateTeamInventory();
					for(OfflinePlayer teamPlayer : plugin.board.getTeam(plugin.frFR ? "Violette" : "Purple").getPlayers()){
						if(!teamPlayer.isOnline())
							continue;
						if(teamPlayer.getPlayer() == player)
							continue;
						teamPlayer.getPlayer().sendMessage(plugin.board.getPlayerTeam(player).getPrefix() + teamPlayer.getPlayer().getDisplayName() + ChatColor.DARK_PURPLE + (plugin.frFR ? " a rejoint votre équipe!" : " has joined your team!"));
					}
				}else{
					player.sendMessage(plugin.frFR ? "[GA UHC] L'équipe Violette est pleine!" : "[GA UHC] The Purple team is full!");
				}
			}else if(clicked.getType() == Material.WOOL && clicked.getDurability() == DyeColor.PINK.getData()){
				event.setCancelled(true);
				player.closeInventory();
				if(plugin.board.getTeam(plugin.frFR ? "Rose" : "Pink").getSize() < plugin.teamSize){
					if(plugin.board.getPlayerTeam(player) != null){
						if(plugin.board.getPlayerTeam(player).getName() == (plugin.frFR ? "Rose" : "Pink")){
							player.sendMessage(plugin.frFR ? "[GA UHC] Vous etes deja dans l'équipe Rose!" : "[GA UHC] You are already in the Pink team!");
							return;
						}else
							plugin.board.getPlayerTeam(player).removePlayer(player);
					}
					plugin.board.getTeam(plugin.frFR ? "Rose" : "Pink").addPlayer(player);
					player.sendMessage(plugin.frFR ? "[GA UHC] Vous etes dans l'équipe Rose!" : "[GA UHC] You have joined the WhPinkite team!");
					plugin.updateTeamInventory();
					for(OfflinePlayer teamPlayer : plugin.board.getTeam(plugin.frFR ? "Rose" : "Pink").getPlayers()){
						if(!teamPlayer.isOnline())
							continue;
						if(teamPlayer.getPlayer() == player)
							continue;
						teamPlayer.getPlayer().sendMessage(plugin.board.getPlayerTeam(player).getPrefix() + teamPlayer.getPlayer().getDisplayName() + ChatColor.LIGHT_PURPLE + (plugin.frFR ? " a rejoint votre équipe!" : " has joined your team!"));
					}
				}else{
					player.sendMessage(plugin.frFR ? "[GA UHC] L'équipe Rose est pleine!" : "[GA UHC] The Pink team is full!");
				}
			}else if(clicked.getType() == Material.WOOL && clicked.getDurability() == DyeColor.GRAY.getData()){
				event.setCancelled(true);
				player.closeInventory();
				if(plugin.board.getTeam(plugin.frFR ? "Grise" : "Gray").getSize() < plugin.teamSize){
					if(plugin.board.getPlayerTeam(player) != null){
						if(plugin.board.getPlayerTeam(player).getName() == (plugin.frFR ? "Grise" : "Gray")){
							player.sendMessage(plugin.frFR ? "[GA UHC] Vous etes deja dans l'équipe Grise!" : "[GA UHC] You are already in the Gray team!");
							return;
						}else
							plugin.board.getPlayerTeam(player).removePlayer(player);
					}
					plugin.board.getTeam(plugin.frFR ? "Grise" : "Gray").addPlayer(player);
					player.sendMessage(plugin.frFR ? "[GA UHC] Vous etes dans l'équipe Grise!" : "[GA UHC] You have joined the Gray team!");
					plugin.updateTeamInventory();
					for(OfflinePlayer teamPlayer : plugin.board.getTeam(plugin.frFR ? "Grise" : "Gray").getPlayers()){
						if(!teamPlayer.isOnline())
							continue;
						if(teamPlayer.getPlayer() == player)
							continue;
						teamPlayer.getPlayer().sendMessage(plugin.board.getPlayerTeam(player).getPrefix() + teamPlayer.getPlayer().getDisplayName() + ChatColor.GRAY + (plugin.frFR ? " a rejoint votre équipe!" : " has joined your team!"));
					}
				}else{
					player.sendMessage(plugin.frFR ? "[GA UHC] L'équipe Grise est pleine!" : "[GA UHC] The Gray team is full!");
				}
			}else if(clicked.getType() == Material.WOOL && clicked.getDurability() == DyeColor.GREEN.getData()){
				event.setCancelled(true);
				player.closeInventory();
				if(plugin.board.getTeam(plugin.frFR ? "Verte" : "Green").getSize() < plugin.teamSize){
					if(plugin.board.getPlayerTeam(player) != null){
						if(plugin.board.getPlayerTeam(player).getName() == (plugin.frFR ? "Verte" : "Green")){
							player.sendMessage(plugin.frFR ? "[GA UHC] Vous etes deja dans l'équipe Verte!" : "[GA UHC] You are already in the Green team!");
							return;
						}else
							plugin.board.getPlayerTeam(player).removePlayer(player);
					}
					plugin.board.getTeam(plugin.frFR ? "Verte" : "Green").addPlayer(player);
					player.sendMessage(plugin.frFR ? "[GA UHC] Vous etes dans l'équipe Verte!" : "[GA UHC] You have joined the Green team!");
					plugin.updateTeamInventory();
					for(OfflinePlayer teamPlayer : plugin.board.getTeam(plugin.frFR ? "Verte" : "Green").getPlayers()){
						if(!teamPlayer.isOnline())
							continue;
						if(teamPlayer.getPlayer() == player)
							continue;
						teamPlayer.getPlayer().sendMessage(plugin.board.getPlayerTeam(player).getPrefix() + teamPlayer.getPlayer().getDisplayName() + ChatColor.GREEN + (plugin.frFR ? " a rejoint votre équipe!" : " has joined your team!"));
					}
				}else{
					player.sendMessage(plugin.frFR ? "[GA UHC] L'équipe Verte est pleine!" : "[GA UHC] The Green team is full!");
				}
			}else if(clicked.getType() == Material.WOOL && clicked.getDurability() == DyeColor.ORANGE.getData()){
				event.setCancelled(true);
				player.closeInventory();
				if(plugin.board.getTeam(plugin.frFR ? "Orange" : "Orange").getSize() < plugin.teamSize){
					if(plugin.board.getPlayerTeam(player) != null){
						if(plugin.board.getPlayerTeam(player).getName() == (plugin.frFR ? "Orange" : "Orange")){
							player.sendMessage(plugin.frFR ? "[GA UHC] Vous etes deja dans l'équipe Orange!" : "[GA UHC] You are already in the Orange team!");
							return;
						}else
							plugin.board.getPlayerTeam(player).removePlayer(player);
					}
					plugin.board.getTeam(plugin.frFR ? "Orange" : "Orange").addPlayer(player);
					player.sendMessage(plugin.frFR ? "[GA UHC] Vous etes dans l'équipe Orange!" : "[GA UHC] You have joined the Orange team!");
					plugin.updateTeamInventory();
					for(OfflinePlayer teamPlayer : plugin.board.getTeam(plugin.frFR ? "Orange" : "Orange").getPlayers()){
						if(!teamPlayer.isOnline())
							continue;
						if(teamPlayer.getPlayer() == player)
							continue;
						teamPlayer.getPlayer().sendMessage(plugin.board.getPlayerTeam(player).getPrefix() + teamPlayer.getPlayer().getDisplayName() + ChatColor.GOLD + (plugin.frFR ? " a rejoint votre équipe!" : " has joined your team!"));
					}
				}else{
					player.sendMessage(plugin.frFR ? "[GA UHC] L'équipe Orange est pleine!" : "[GA UHC] The Orange team is full!");
				}
			}else if(clicked.getType() == Material.WOOL && clicked.getDurability() == DyeColor.YELLOW.getData()){
				event.setCancelled(true);
				player.closeInventory();
				if(plugin.board.getTeam(plugin.frFR ? "Jaune" : "Yellow").getSize() < plugin.teamSize){
					if(plugin.board.getPlayerTeam(player) != null){
						if(plugin.board.getPlayerTeam(player).getName() == (plugin.frFR ? "Jaune" : "Yellow")){
							player.sendMessage(plugin.frFR ? "[GA UHC] Vous etes deja dans l'équipe Jaune!" : "[GA UHC] You are already in the Yellow team!");
							return;
						}else
							plugin.board.getPlayerTeam(player).removePlayer(player);
					}
					plugin.board.getTeam(plugin.frFR ? "Jaune" : "Yellow").addPlayer(player);
					player.sendMessage(plugin.frFR ? "[GA UHC] Vous etes dans l'équipe Jaune!" : "[GA UHC] You have joined the Yellow team!");
					plugin.updateTeamInventory();
					for(OfflinePlayer teamPlayer : plugin.board.getTeam(plugin.frFR ? "Jaune" : "Yellow").getPlayers()){
						if(!teamPlayer.isOnline())
							continue;
						if(teamPlayer.getPlayer() == player)
							continue;
						teamPlayer.getPlayer().sendMessage(plugin.board.getPlayerTeam(player).getPrefix() + teamPlayer.getPlayer().getDisplayName() + ChatColor.YELLOW + (plugin.frFR ? " a rejoint votre équipe!" : " has joined your team!"));
					}
				}else{
					player.sendMessage(plugin.frFR ? "[GA UHC] L'équipe Jaune est pleine!" : "[GA UHC] The Yellow team is full!");
				}
			}
		}else if(plugin.languageInventory != null && inventory.getName().equals(plugin.languageInventory.getName())){
			if(event.getClick().isKeyboardClick())
				event.setCancelled(true);
			if(clicked.getType() == Material.WOOL && clicked.getDurability() == DyeColor.WHITE.getData()){
				player.closeInventory();
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "say The language has been set to English!");
				plugin.frFR = false;
				plugin.FinishSetup();
			}else if(clicked.getType() == Material.WOOL && clicked.getDurability() == DyeColor.BLACK.getData()){
				player.closeInventory();
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "say Le language a été mis en français!");
				plugin.frFR = true;
				plugin.FinishSetup();
			}
		}else if(clicked != null && clicked.getType() != null && clicked.getType() == Material.NETHER_STAR){
			if(event.getClick().isKeyboardClick()){
				event.setCancelled(true);
			}
			else
				event.setCancelled(true);
		}else{
			return;
		}
	}
	
	@EventHandler
	public void onPlayerEmptyBucket(PlayerBucketEmptyEvent event){
		if(event.getBucket() == Material.LAVA_BUCKET && !pvp){
			event.setCancelled(true);
		}
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event){
		if(!plugin.started)
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "tp " + event.getPlayer().getDisplayName() + " 13.5 170 14.5");
		if(plugin.started)
			event.getPlayer().setScoreboard(plugin.scoreBoard);
		if(plugin.started && !plugin.players.contains(event.getPlayer().getDisplayName())){
			event.getPlayer().setGameMode(GameMode.SPECTATOR);
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "tp " + event.getPlayer().getDisplayName() + " 13.5 170 14.5");
		}
		if(plugin.started && !pvp && event.getPlayer().getGameMode() == GameMode.SURVIVAL && plugin.players.contains(event.getPlayer().getDisplayName())){
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), (plugin.frFR ? "say " + ChatColor.GOLD + event.getPlayer().getDisplayName() + " " + ChatColor.GOLD + "s'est " + ChatColor.GOLD + "reconnecté " + ChatColor.GOLD + "avant " + ChatColor.GOLD + "la " + ChatColor.GOLD + "phase " + ChatColor.GOLD + "PvP " + ChatColor.GOLD + "et " + ChatColor.GOLD + "peut " + ChatColor.GOLD + "reprendre " + ChatColor.GOLD + "la " + ChatColor.GOLD + "partie!" : "say " + ChatColor.GOLD + event.getPlayer().getDisplayName() + ChatColor.GOLD + "has " + ChatColor.GOLD + "reconnected " + ChatColor.GOLD + "before " + ChatColor.GOLD + "the " + ChatColor.GOLD + "PvP " + ChatColor.GOLD + "phase " + ChatColor.GOLD + "and " + ChatColor.GOLD + "can " + ChatColor.GOLD + "coontinue " + ChatColor.GOLD + "playing!"));
			disconnectedPlayers = disconnectedPlayers - 1;
			if(!plugin.solo)
				disconnectedTeams = disconnectedTeams - 1;
		}
		if(plugin.started && pvp && event.getPlayer().getGameMode() == GameMode.SURVIVAL && plugin.players.contains(event.getPlayer().getDisplayName())){
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), (plugin.frFR ? "say " + ChatColor.GOLD + event.getPlayer().getDisplayName() + " " + ChatColor.GOLD + "s'est " + ChatColor.GOLD + "reconnecté " + ChatColor.GOLD + "pendant " + ChatColor.GOLD + "la " + ChatColor.GOLD + "phase " + ChatColor.GOLD + "PvP " + ChatColor.GOLD + "et " + ChatColor.GOLD + "est " + ChatColor.GOLD + "�limin�!" : "say " + ChatColor.GOLD + event.getPlayer().getDisplayName() + ChatColor.GOLD + "has " + ChatColor.GOLD + "reconnected " + ChatColor.GOLD + "during " + ChatColor.GOLD + "the " + ChatColor.GOLD + "PvP " + ChatColor.GOLD + "phase " + ChatColor.GOLD + "and " + ChatColor.GOLD + "is " + ChatColor.GOLD + "eliminated!"));
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), (plugin.frFR ? "say " + ChatColor.GOLD + "Son " + ChatColor.GOLD + "Stuff " + ChatColor.GOLD + "est " + ChatColor.GOLD + "en " + ChatColor.GOLD + "0 " + ChatColor.GOLD + "0!" : "say " + ChatColor.GOLD + "His " + ChatColor.GOLD + "gear " + ChatColor.GOLD + "is " + ChatColor.GOLD + "at " + ChatColor.GOLD + "0 " + ChatColor.GOLD + "0!"));
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "tp " + event.getPlayer().getDisplayName() + " 0 100 0");
			event.getPlayer().setHealth(0);
			plugin.board.resetScores(Bukkit.getServer().getOfflinePlayer(ChatColor.GOLD + (plugin.frFR ? "Joueurs restant(s): " + Integer.toString(plugin.playersInt) : "Players left: " + Integer.toString(plugin.playersInt))));
			plugin.playersInt = plugin.playersInt - 1;
			plugin.playersLeft = plugin.timer.getScore(Bukkit.getServer().getOfflinePlayer(ChatColor.GOLD + (plugin.frFR ? "Joueurs restant(s): " + Integer.toString(plugin.playersInt) : "Players left: " + Integer.toString(plugin.playersInt))));
			plugin.playersLeft.setScore(1);
			
			if(plugin.playersInt - disconnectedPlayers == 1 || plugin.playersInt - disconnectedPlayers == 0){
				plugin.board.resetScores(Bukkit.getServer().getOfflinePlayer(ChatColor.GOLD + (plugin.frFR ? "Joueurs restant(s): " + Integer.toString(plugin.playersInt) : "Players left: " + Integer.toString(plugin.playersInt))));
				plugin.playersInt = plugin.playersInt - disconnectedPlayers;
				plugin.playersLeft = plugin.timer.getScore(Bukkit.getServer().getOfflinePlayer(ChatColor.GOLD + (plugin.frFR ? "Joueurs restant(s): " + Integer.toString(plugin.playersInt) : "Players left: " + Integer.toString(plugin.playersInt))));
				plugin.playersLeft.setScore(1);
			}
			
			disconnectedPlayers = disconnectedPlayers - 1;
			if(!plugin.solo)
				disconnectedTeams = disconnectedTeams - 1;
			
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "playsound mob.wither.death @a ~ ~ ~ 10000");
			
			Player killed = event.getPlayer();
			
			if(plugin.players.contains(killed.getDisplayName()))
				plugin.players.remove(killed.getDisplayName());
			
			if(!plugin.solo){
				if(plugin.board.getPlayerTeam(killed) != null && plugin.board.getPlayerTeam(killed).getSize() == 1){
					plugin.board.resetScores(Bukkit.getServer().getOfflinePlayer(ChatColor.GOLD + (plugin.frFR ? "équipes restante(s): " + Integer.toString(plugin.teamsInt) : "Teams left: " + Integer.toString(plugin.teamsInt))));
					plugin.teamsInt = plugin.teamsInt - 1;
					plugin.teamsLeft = plugin.timer.getScore(Bukkit.getServer().getOfflinePlayer(ChatColor.GOLD + (plugin.frFR ? "équipes restante(s): " + Integer.toString(plugin.teamsInt) : "Teams left: " + Integer.toString(plugin.teamsInt))));
					plugin.teamsLeft.setScore(0);
					if((plugin.frFR ? plugin.board.getPlayerTeam(killed).getName() == "Bleue claire" : plugin.board.getPlayerTeam(killed).getName() == "Light blue"))
						Bukkit.dispatchCommand(Bukkit.getConsoleSender(), (plugin.frFR ? "say " + ChatColor.GOLD + "L'équipe " + plugin.board.getPlayerTeam(killed).getPrefix() + "Bleue " + plugin.board.getPlayerTeam(killed).getPrefix() + "claire " + ChatColor.GOLD + "est " + ChatColor.GOLD + "�limin�!" : "say " + plugin.board.getPlayerTeam(killed).getPrefix() + "Light " + plugin.board.getPlayerTeam(killed).getPrefix() + "blue " + ChatColor.GOLD + "team " + ChatColor.GOLD + "has " + ChatColor.GOLD + ChatColor.GOLD + "been " + "eliminated!"));
					else
						Bukkit.dispatchCommand(Bukkit.getConsoleSender(), (plugin.frFR ? "say " + ChatColor.GOLD + "L'équipe " + plugin.board.getPlayerTeam(killed).getPrefix() + plugin.board.getPlayerTeam(killed).getName() + " " + ChatColor.GOLD + "est " + ChatColor.GOLD + "�limin�!" : "say " + plugin.board.getPlayerTeam(killed).getPrefix() + plugin.board.getPlayerTeam(killed).getName() + " team " + ChatColor.GOLD + "has " + ChatColor.GOLD + ChatColor.GOLD + "been " + "eliminated!"));
				}if(plugin.board.getPlayerTeam(killed) != null)
					plugin.board.getPlayerTeam(killed).removePlayer(killed);
				
				if(plugin.teamsInt - disconnectedTeams == 1 || plugin.teamsInt - disconnectedTeams == 0){
					plugin.board.resetScores(Bukkit.getServer().getOfflinePlayer(ChatColor.GOLD + (plugin.frFR ? "équipes restante(s): " + Integer.toString(plugin.teamsInt) : "Teams left: " + Integer.toString(plugin.teamsInt))));
					plugin.teamsInt = plugin.teamsInt - disconnectedTeams;
					plugin.teamsLeft = plugin.timer.getScore(Bukkit.getServer().getOfflinePlayer(ChatColor.GOLD + (plugin.frFR ? "équipes restante(s): " + Integer.toString(plugin.teamsInt) : "Teams left: " + Integer.toString(plugin.teamsInt))));
					plugin.teamsLeft.setScore(0);
				}
			}
			
			killed.getWorld().dropItemNaturally(killed.getLocation(), new ItemStack(Material.GOLDEN_APPLE));
			killed.setGameMode(GameMode.SPECTATOR);
		}
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event){
		if(plugin.started && !pvp && event.getPlayer().getGameMode() == GameMode.SURVIVAL && plugin.players.contains(event.getPlayer().getDisplayName())){
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), (plugin.frFR ? "say " + ChatColor.GOLD + event.getPlayer().getDisplayName() + " " + ChatColor.GOLD + "a " + ChatColor.GOLD + "quitté, " + ChatColor.GOLD + "il " + ChatColor.GOLD + "pourra " + ChatColor.GOLD + "se " + ChatColor.GOLD + "reconnect� " + ChatColor.GOLD + "avant " + ChatColor.GOLD + "la " + ChatColor.GOLD + "phase " + ChatColor.GOLD + "PvP!" : "say " + ChatColor.GOLD + event.getPlayer().getDisplayName() + ChatColor.GOLD + "has " + ChatColor.GOLD + "quit, " + ChatColor.GOLD + "he " + ChatColor.GOLD + "can " + ChatColor.GOLD + "reconnect " + ChatColor.GOLD + "before " + ChatColor.GOLD + "the " + ChatColor.GOLD + "PvP " + ChatColor.GOLD + "phase!"));
			disconnectedPlayers = disconnectedPlayers + 1;
			if(!plugin.solo){
				if(plugin.board.getPlayerTeam(event.getPlayer()) != null && plugin.board.getPlayerTeam(event.getPlayer()).getSize() == 1){
					disconnectedTeams = disconnectedTeams + 1;
				}else if(plugin.board.getPlayerTeam(event.getPlayer()) != null && plugin.board.getPlayerTeam(event.getPlayer()).getSize() > 1){
					int offline = 0;
					for(OfflinePlayer player : plugin.board.getPlayerTeam(event.getPlayer()).getPlayers()){
						if(!player.isOnline()){
							offline = offline + 1;
						}
					}
					
					if(offline == plugin.board.getPlayerTeam(event.getPlayer()).getSize())
						disconnectedTeams = disconnectedTeams + 1;
				}
			}
			
			if(plugin.playersInt - disconnectedPlayers == 1 || plugin.playersInt - disconnectedPlayers == 0){
				plugin.board.resetScores(Bukkit.getServer().getOfflinePlayer(ChatColor.GOLD + (plugin.frFR ? "Joueurs restant(s): " + Integer.toString(plugin.playersInt) : "Players left: " + Integer.toString(plugin.playersInt))));
				plugin.playersInt = plugin.playersInt - disconnectedPlayers;
				plugin.playersLeft = plugin.timer.getScore(Bukkit.getServer().getOfflinePlayer(ChatColor.GOLD + (plugin.frFR ? "Joueurs restant(s): " + Integer.toString(plugin.playersInt) : "Players left: " + Integer.toString(plugin.playersInt))));
				plugin.playersLeft.setScore(1);
			}
			
			if(!plugin.solo){
				if(plugin.teamsInt - disconnectedTeams == 1 || plugin.teamsInt - disconnectedTeams == 0){
					plugin.board.resetScores(Bukkit.getServer().getOfflinePlayer(ChatColor.GOLD + (plugin.frFR ? "équipes restante(s): " + Integer.toString(plugin.teamsInt) : "Teams left: " + Integer.toString(plugin.teamsInt))));
					plugin.teamsInt = plugin.teamsInt - disconnectedTeams;
					plugin.teamsLeft = plugin.timer.getScore(Bukkit.getServer().getOfflinePlayer(ChatColor.GOLD + (plugin.frFR ? "équipes restante(s): " + Integer.toString(plugin.teamsInt) : "Teams left: " + Integer.toString(plugin.teamsInt))));
					plugin.teamsLeft.setScore(0);
				}
			}
		}
		if(plugin.started && pvp && event.getPlayer().getGameMode() == GameMode.SURVIVAL && plugin.players.contains(event.getPlayer().getDisplayName())){
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), (plugin.frFR ? "say " + ChatColor.GOLD + event.getPlayer().getDisplayName() + " " + ChatColor.GOLD + "a " + ChatColor.GOLD + "quitté " + ChatColor.GOLD + "pendant " + ChatColor.GOLD + "la " + ChatColor.GOLD + "phase " + ChatColor.GOLD + "PvP " + ChatColor.GOLD + "et " + ChatColor.GOLD + "est " + ChatColor.GOLD + "�limin�!" : "say " + ChatColor.GOLD + event.getPlayer().getDisplayName() + ChatColor.GOLD + "has " + ChatColor.GOLD + "quit " + ChatColor.GOLD + "during " + ChatColor.GOLD + "the " + ChatColor.GOLD + "PvP " + ChatColor.GOLD + "phase " + ChatColor.GOLD + "and " + ChatColor.GOLD + "is " + ChatColor.GOLD + "eliminated"));
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), (plugin.frFR ? "say " + ChatColor.GOLD + "Son " + ChatColor.GOLD + "Stuff " + ChatColor.GOLD + "est " + ChatColor.GOLD + "en " + ChatColor.GOLD + "0 " + ChatColor.GOLD + "0!" : "say " + ChatColor.GOLD + "His " + ChatColor.GOLD + "gear " + ChatColor.GOLD + "is " + ChatColor.GOLD + "at " + ChatColor.GOLD + "0 " + ChatColor.GOLD + "0!"));
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "tp " + event.getPlayer().getDisplayName() + " 0 100 0");
			event.getPlayer().setHealth(0);
			plugin.board.resetScores(Bukkit.getServer().getOfflinePlayer(ChatColor.GOLD + (plugin.frFR ? "Joueurs restant(s): " + Integer.toString(plugin.playersInt) : "Players left: " + Integer.toString(plugin.playersInt))));
			plugin.playersInt = plugin.playersInt - 1;
			plugin.playersLeft = plugin.timer.getScore(Bukkit.getServer().getOfflinePlayer(ChatColor.GOLD + (plugin.frFR ? "Joueurs restant(s): " + Integer.toString(plugin.playersInt) : "Players left: " + Integer.toString(plugin.playersInt))));
			plugin.playersLeft.setScore(1);
			
			if(plugin.playersInt - disconnectedPlayers == 1 || plugin.playersInt - disconnectedPlayers == 0){
				plugin.board.resetScores(Bukkit.getServer().getOfflinePlayer(ChatColor.GOLD + (plugin.frFR ? "Joueurs restant(s): " + Integer.toString(plugin.playersInt) : "Players left: " + Integer.toString(plugin.playersInt))));
				plugin.playersInt = plugin.playersInt - disconnectedPlayers;
				plugin.playersLeft = plugin.timer.getScore(Bukkit.getServer().getOfflinePlayer(ChatColor.GOLD + (plugin.frFR ? "Joueurs restant(s): " + Integer.toString(plugin.playersInt) : "Players left: " + Integer.toString(plugin.playersInt))));
				plugin.playersLeft.setScore(1);
			}
			
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "playsound mob.wither.death @a ~ ~ ~ 10000");
			
			Player killed = event.getPlayer();
			
			if(plugin.players.contains(killed.getDisplayName()))
				plugin.players.remove(killed.getDisplayName());
			
			if(!plugin.solo){
				if(plugin.board.getPlayerTeam(killed) != null && plugin.board.getPlayerTeam(killed).getSize() == 1){
					plugin.board.resetScores(Bukkit.getServer().getOfflinePlayer(ChatColor.GOLD + (plugin.frFR ? "équipes restante(s): " + Integer.toString(plugin.teamsInt) : "Teams left: " + Integer.toString(plugin.teamsInt))));
					plugin.teamsInt = plugin.teamsInt - 1;
					plugin.teamsLeft = plugin.timer.getScore(Bukkit.getServer().getOfflinePlayer(ChatColor.GOLD + (plugin.frFR ? "équipes restante(s): " + Integer.toString(plugin.teamsInt) : "Teams left: " + Integer.toString(plugin.teamsInt))));
					plugin.teamsLeft.setScore(0);
					if((plugin.frFR ? plugin.board.getPlayerTeam(killed).getName() == "Bleue claire" : plugin.board.getPlayerTeam(killed).getName() == "Light blue"))
						Bukkit.dispatchCommand(Bukkit.getConsoleSender(), (plugin.frFR ? "say " + ChatColor.GOLD + "L'équipe " + plugin.board.getPlayerTeam(killed).getPrefix() + "Bleue " + plugin.board.getPlayerTeam(killed).getPrefix() + "claire " + ChatColor.GOLD + "est " + ChatColor.GOLD + "�limin�!" : "say " + plugin.board.getPlayerTeam(killed).getPrefix() + "Light " + plugin.board.getPlayerTeam(killed).getPrefix() + "blue " + ChatColor.GOLD + "team " + ChatColor.GOLD + "has " + ChatColor.GOLD + ChatColor.GOLD + "been " + "eliminated!"));
					else
						Bukkit.dispatchCommand(Bukkit.getConsoleSender(), (plugin.frFR ? "say " + ChatColor.GOLD + "L'équipe " + plugin.board.getPlayerTeam(killed).getPrefix() + plugin.board.getPlayerTeam(killed).getName() + " " + ChatColor.GOLD + "est " + ChatColor.GOLD + "�limin�!" : "say " + plugin.board.getPlayerTeam(killed).getPrefix() + plugin.board.getPlayerTeam(killed).getName() + " team " + ChatColor.GOLD + "has " + ChatColor.GOLD + ChatColor.GOLD + "been " + "eliminated!"));
				}if(plugin.board.getPlayerTeam(killed) != null)
					plugin.board.getPlayerTeam(killed).removePlayer(killed);
				
				if(plugin.teamsInt - disconnectedTeams == 1 || plugin.teamsInt - disconnectedTeams == 0){
					plugin.board.resetScores(Bukkit.getServer().getOfflinePlayer(ChatColor.GOLD + (plugin.frFR ? "équipes restante(s): " + Integer.toString(plugin.teamsInt) : "Teams left: " + Integer.toString(plugin.teamsInt))));
					plugin.teamsInt = plugin.teamsInt - disconnectedTeams;
					plugin.teamsLeft = plugin.timer.getScore(Bukkit.getServer().getOfflinePlayer(ChatColor.GOLD + (plugin.frFR ? "équipes restante(s): " + Integer.toString(plugin.teamsInt) : "Teams left: " + Integer.toString(plugin.teamsInt))));
					plugin.teamsLeft.setScore(0);
				}
			}
			
			killed.getWorld().dropItemNaturally(killed.getLocation(), new ItemStack(Material.GOLDEN_APPLE));
			killed.setGameMode(GameMode.SPECTATOR);
		}
	}
	
	@EventHandler
	public void onPlayerMovement(PlayerMoveEvent event){
		if(!plugin.started){
			event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, 200, 9));
			Location location = new Location(event.getPlayer().getWorld(), 13.5, 170, 14.5);
			if(event.getPlayer().getLocation().getY() < 130){
				event.setTo(location);
			}
		}
	}
}
