package com.ga16.uhcrun;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.DyeColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import com.ga16.uhcrun.blockpopulator.LobbyPopulator;
import com.ga16.uhcrun.events.BlockListener;
import com.ga16.uhcrun.events.ChatListener;
import com.ga16.uhcrun.events.MobListener;
import com.ga16.uhcrun.events.PlayerListener;
import com.ga16.uhcrun.events.WorldListener;

@SuppressWarnings("deprecation")
public final class UHCRun extends JavaPlugin
{	
	public Logger logger = Bukkit.getLogger();
	
	public boolean frFR = false;
	
	private int taskId;
	
	private int countdown = 11;
	private int seconds = 0;
	private int minutes = 20;
	private int oldSeconds = 0;
	private int oldMinutes = 20;
	public boolean started = false;
	public boolean solo = true;
	private int teams = 2;
	public int teamSize = 2;
	
	private boolean partialSetup = false;
	public boolean setup = false;
	private boolean timeUp = false;
	
	public Scoreboard board;
	public Objective timer;
	public Objective health;
	public Objective healthUnderName;
	public Scoreboard scoreBoard = null;
	public Objective timerObj = null;
	public Objective healthObj = null;
	public Objective healthUnderNameObj = null;
	
	public Score playersLeft;
	public int playersInt = 0;
	public Score teamsLeft;
	public int teamsInt = 0;
	
	private Player setupSender;
	public World world;
	
	public Inventory teamInventory = null;
	public Inventory languageInventory = null;
	
	private ArrayList<String> redTeamPlayers = new ArrayList<String>();
	private ArrayList<String> lightBlueTeamPlayers = new ArrayList<String>();
	private ArrayList<String> blackTeamPlayers = new ArrayList<String>();
	private ArrayList<String> whiteTeamPlayers = new ArrayList<String>();
	private ArrayList<String> blueTeamPlayers = new ArrayList<String>();
	private ArrayList<String> cyanTeamPlayers = new ArrayList<String>();
	private ArrayList<String> purpleTeamPlayers = new ArrayList<String>();
	private ArrayList<String> pinkTeamPlayers = new ArrayList<String>();
	private ArrayList<String> grayTeamPlayers = new ArrayList<String>();
	private ArrayList<String> greenTeamPlayers = new ArrayList<String>();
	private ArrayList<String> orangeTeamPlayers = new ArrayList<String>();
	private ArrayList<String> yellowTeamPlayers = new ArrayList<String>();
	
	public ArrayList<String> players = new ArrayList<String>();
	
	@Override
	public void onEnable(){
		Bukkit.getPluginManager().registerEvents(new BlockListener(this), this);
		Bukkit.getPluginManager().registerEvents(new PlayerListener(this), this);
		Bukkit.getPluginManager().registerEvents(new MobListener(this), this);
		Bukkit.getPluginManager().registerEvents(new WorldListener(this), this);
		Bukkit.getPluginManager().registerEvents(new ChatListener(this), this);
	}
	
	@Override
	public void onDisable(){
		
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
		if(command.getName().equalsIgnoreCase("uhcrun")){
			if(args.length > 0){
				if(args[0].equalsIgnoreCase("start")){
					if(started || !setup){
						sender.sendMessage(frFR ? "[GA UHC] Erreur, le jeu est deja en cours ou alors vous avez oubliez de faire la commande /uhcrun setup." : "[GA UHC Error, the game is either in progress or the /uhcrun setup command hasn't been used.");
						return false;
					}

					for(Player player : this.getServer().getOnlinePlayers()){
						player.setScoreboard(scoreBoard);
						if(player.getHealth() > 0 && player.getGameMode() == GameMode.SURVIVAL){
					        player.setHealth(player.getHealth() - 0.0001);
					        player.setExp(0);
						}
					}

					taskId = getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {

						public void run() {
							countdown--;
							if(countdown == 0){
								Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "title @a title {text:\"Go!\",color:dark_green}");
								Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "playsound random.orb @a ~ ~ ~ 10000 2");
								getServer().getScheduler().cancelAllTasks();
								started = true;
								PlayerListener.pvp = false;
								PlayerListener.damage = false;
								PlayerListener.pvp = false;
								PlayerListener.disconnectedPlayers = 0;
								PlayerListener.disconnectedTeams = 0;
								for(Player player : getServer().getOnlinePlayers()){
									if(player.isOnline() && player.getGameMode() == GameMode.SURVIVAL){
										playersInt = playersInt + 1;
										players.add(player.getDisplayName());
									}
								}
								playersLeft = timer.getScore(Bukkit.getServer().getOfflinePlayer(ChatColor.GOLD + (frFR ? "Joueurs restant(s): " + Integer.toString(playersInt) : "Players left: " + Integer.toString(playersInt))));
								playersLeft.setScore(1);
								if(!solo){
									for(Player player : Bukkit.getOnlinePlayers()){
										if(player.getGameMode() == GameMode.SURVIVAL){
											if(board.getPlayerTeam(player) == null){
												SetPlayerRandomTeam(player);
											}
										}
									}
									for(Team team : board.getTeams()){
										if(team.getSize() > 0){
											teamsInt = teamsInt + 1;
										}
									}
									teamsLeft = timer.getScore(Bukkit.getServer().getOfflinePlayer(ChatColor.GOLD + (frFR ? "équipes restante(s): " + Integer.toString(teamsInt) : "Teams left: " + Integer.toString(teamsInt))));
									teamsLeft.setScore(0);
								}
								Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "gamerule naturalRegeneration false");
								Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "setworldspawn 13.5 170 14.5");
								Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "worldborder center 0 0");
								Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "worldborder set 2000");
								Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "worldborder damage amount 1");
								Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "worldborder warning time 10");
								Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "clear @a");
								Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "effect @a clear");
								Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "gamerule doDaylightCycle false");
								Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "weather clear 100000");
								Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "time set 6000");
								start();
								if(solo){
									Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "spreadplayers 0 0 500 1000 false @a");
									for(Player player : Bukkit.getOnlinePlayers()){
										if(player.getGameMode() == GameMode.SPECTATOR)
											Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "tp " + player.getDisplayName() + " 13.5 170 14.5");
									}
									Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "tp @a ~ 140 ~");
								}else{
									int x = randomCoords(-1000, 1000);
									int z = randomCoords(-1000, 1000);
									Location location = new Location(world, x, 140, z);
									int x2 = randomCoords(-1000, 1000);
									int z2 = randomCoords(-1000, 1000);
									Location location2 = new Location(world, x2, 140, z2);
									if(teams > 2){
										int x3 = randomCoords(-1000, 1000);
										int z3 = randomCoords(-1000, 1000);
										Location location3 = new Location(world, x3, 140, z3);
										for(OfflinePlayer player : board.getTeam(frFR ? "Noire" : "Black").getPlayers()){
											player.getPlayer().teleport(location3);
										}
									}if(teams > 3){
										int x4 = randomCoords(-1000, 1000);
										int z4 = randomCoords(-1000, 1000);
										Location location4 = new Location(world, x4, 140, z4);
										for(OfflinePlayer player : board.getTeam(frFR ? "Blanche" : "White").getPlayers()){
											player.getPlayer().teleport(location4);
										}
									}if(teams > 4){
										int x4 = randomCoords(-1000, 1000);
										int z4 = randomCoords(-1000, 1000);
										Location location4 = new Location(world, x4, 140, z4);
										for(OfflinePlayer player : board.getTeam(frFR ? "Bleue" : "Blue").getPlayers()){
											player.getPlayer().teleport(location4);
										}
									}if(teams > 5){
										int x4 = randomCoords(-1000, 1000);
										int z4 = randomCoords(-1000, 1000);
										Location location4 = new Location(world, x4, 140, z4);
										for(OfflinePlayer player : board.getTeam(frFR ? "Cyan" : "Cyan").getPlayers()){
											player.getPlayer().teleport(location4);
										}
									}if(teams > 6){
										int x4 = randomCoords(-1000, 1000);
										int z4 = randomCoords(-1000, 1000);
										Location location4 = new Location(world, x4, 140, z4);
										for(OfflinePlayer player : board.getTeam(frFR ? "Violette" : "Purple").getPlayers()){
											player.getPlayer().teleport(location4);
										}
									}if(teams > 7){
										int x4 = randomCoords(-1000, 1000);
										int z4 = randomCoords(-1000, 1000);
										Location location4 = new Location(world, x4, 140, z4);
										for(OfflinePlayer player : board.getTeam(frFR ? "Rose" : "Pink").getPlayers()){
											player.getPlayer().teleport(location4);
										}
									}if(teams > 8){
										int x4 = randomCoords(-1000, 1000);
										int z4 = randomCoords(-1000, 1000);
										Location location4 = new Location(world, x4, 140, z4);
										for(OfflinePlayer player : board.getTeam(frFR ? "Grise" : "Gray").getPlayers()){
											player.getPlayer().teleport(location4);
										}
									}if(teams > 9){
										int x4 = randomCoords(-1000, 1000);
										int z4 = randomCoords(-1000, 1000);
										Location location4 = new Location(world, x4, 140, z4);
										for(OfflinePlayer player : board.getTeam(frFR ? "Verte" : "Green").getPlayers()){
											player.getPlayer().teleport(location4);
										}
									}if(teams > 10){
										int x4 = randomCoords(-1000, 1000);
										int z4 = randomCoords(-1000, 1000);
										Location location4 = new Location(world, x4, 140, z4);
										for(OfflinePlayer player : board.getTeam(frFR ? "Orange" : "Orange").getPlayers()){
											player.getPlayer().teleport(location4);
										}
									}if(teams > 11){
										int x4 = randomCoords(-1000, 1000);
										int z4 = randomCoords(-1000, 1000);
										Location location4 = new Location(world, x4, 140, z4);
										for(OfflinePlayer player : board.getTeam(frFR ? "Jaune" : "Yellow").getPlayers()){
											player.getPlayer().teleport(location4);
										}
									}
									for(OfflinePlayer player : board.getTeam(frFR ? "Rouge" : "Red").getPlayers()){
										player.getPlayer().teleport(location);
									}
									for(OfflinePlayer player : board.getTeam(frFR ? "Bleue claire" : "Light blue").getPlayers()){
										player.getPlayer().teleport(location2);
									}
								}
								DeleteLobby();
								Bukkit.getScheduler().cancelTask(taskId);
							}else{
								Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "playsound random.orb @a ~ ~ ~ 10000 1");
								Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "title @a title {text:\"" + countdown + "\",color:dark_green}");
							}
						}
					}, 0L, 20L);
				}else if(args[0].equalsIgnoreCase("setup")){
					if(setup){
						sender.sendMessage(frFR ? "[GA UHC] La configuration a deja été faite." : "[GA UHC] The setup has already been completed.");
						return false;
					}
					this.board = Bukkit.getServer().getScoreboardManager().getNewScoreboard();

					health = board.registerNewObjective("health", "health");
					health.setDisplaySlot(DisplaySlot.PLAYER_LIST);

					healthUnderName = board.registerNewObjective("healthUnderName", "health");
					healthUnderName.setDisplayName("HP");
					healthUnderName.setDisplaySlot(DisplaySlot.BELOW_NAME);

					timer = board.registerNewObjective("timer", "dummy");
					timer.setDisplayName(ChatColor.GOLD + "Gamers Assembly 2016 - UHC");
					timer.setDisplaySlot(DisplaySlot.SIDEBAR);

					this.scoreBoard = board;
					this.timerObj = timer;

					setupSender = (Player) sender;

					languageInventory = Bukkit.createInventory(null, 9, "Languages");
					languageInventory.setItem(0, setItemNameAndLore(new ItemStack(Material.WOOL, 1, DyeColor.WHITE.getData()), ChatColor.WHITE + "English", new String[]{}));
					languageInventory.setItem(1, setItemNameAndLore(new ItemStack(Material.WOOL, 1, DyeColor.BLACK.getData()), ChatColor.WHITE + "Fran�ais", new String[]{}));
					setupSender.openInventory(languageInventory);
					sender.sendMessage("[GA UHC] Please select a language.");
					
					partialSetup = true;
				}else if(args[0].equalsIgnoreCase("solo")){
					if(started){
						sender.sendMessage(frFR ? "[GA16 UHC] Erreur, le jeu est deja en cours." : "[GA16 UHC] Error, the game is either in progress.");
						return false;
					}
					solo = true;
					sender.sendMessage(frFR ? "[GA16 UHCn] Mode solo" : "[GA16 UHC] Solo mode");
				}else if(args[0].equalsIgnoreCase("teamsize")){
					try{
						teamSize = Integer.parseInt(args[1]);
						if(teamSize > 0){
							sender.sendMessage(frFR ? "[GA16 UHC] Le nombre de personnes par équipe est " + teamSize : "[GA16 UHC] The number of players per team is " + teamSize);
						}else{
							sender.sendMessage(frFR ? "[GA16 UHC] " + teamSize + " personnes par équipe est impossible!" : "[GA16 UHC] " + teamSize + " players per team is impossible!");
							teamSize = 2;
						}
					}catch(Exception e){
						sender.sendMessage(frFR ? "[GA16 UHC] Merci de spécifier le nombre de personnes par équipe" : "[GA16 UHC] Please specify the number of players per team");
					}
				}else if(args[0].equalsIgnoreCase("teams")){
					if(started || !setup){
						sender.sendMessage(frFR ? "[GA16 UHC] Erreur, le jeu est deja en cours ou alors vous avez oubliez de faire la commande /uhcrun setup." : "[GA16 UHC] Error, the game is either in progress or the /uhcrun setup command hasn't been used.");
						return false;
					}
					solo = false;
					for(Player player : Bukkit.getServer().getOnlinePlayers()){
						if(player.getGameMode() == GameMode.SURVIVAL)
							player.getInventory().setItem(4, setItemNameAndLore(new ItemStack(Material.NETHER_STAR), (frFR ? "Sélécteur d'équipe" : "Team selector"), new String[]{}));
					}
					if(args.length > 0 && !args[1].equalsIgnoreCase("random")){
						try{
							teams = Integer.parseInt(args[1]);
							if(teams < 2){
								sender.sendMessage(frFR ? "[GA16 UHC] Moins de 2 équipes est impossible!" : "[GA16 UHC] Less than 2 teams is impossible!");
								teams = 2;
								updateTeamInventory();
							}else if(teams > 12){
								sender.sendMessage(frFR ? "[GA16 UHC] Le nombre maximum d'équipes est 12! " : "[GA16 UHC] The maximum amount of teams is 12!");
								teams = 12;
								updateTeamInventory();
							}else{
								sender.sendMessage(frFR ? "[GA16 UHC] Le nombre d'équipes est " + teams : "[GA16 UHC] The number of teams is " + teams);
								updateTeamInventory();
							}
						}catch(Exception e){
							sender.sendMessage(frFR ? "[GA16 UHC] Merci de spécifier le nombre d'équipe" : "[GA16 UHC] Please specify the number of teams");
						}
					}else if(args[1].equalsIgnoreCase("random")){
						RandomizeTeams();
					}
				}else if(args[0].equalsIgnoreCase("reset")){
					if(!setup){
						sender.sendMessage(frFR ? "[GA16 UHC] Erreur, vous avez oubliez de faire la commande /uhcrun setup." : "[GA16 UHC] Error, the /uhcrun setup command hasn't been used.");
						return false;
					}
					
					started = false;
					PlayerListener.pvp = false;
					PlayerListener.damage = false;
					PlayerListener.disconnectedPlayers = 0;
					PlayerListener.disconnectedTeams = 0;
					timeUp = false;
					countdown = 11;
					seconds = 0;
					minutes = 20;
					oldSeconds = 0;
					oldMinutes = 20;

					playersInt = 0;
					teamsInt = 0;

					timer.unregister();
					timer = board.registerNewObjective("timer", "dummy");
					timer.setDisplayName(ChatColor.GOLD + "UHC Run");
					timer.setDisplaySlot(DisplaySlot.SIDEBAR);
					
					players.clear();
					
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "clear @a");
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "effect @a clear");

					for(Player player : Bukkit.getOnlinePlayers()){
						if(board.getPlayerTeam(player) != null)
							board.getPlayerTeam(player).removePlayer(player);
						if(player.getGameMode() == GameMode.SPECTATOR)
							player.setGameMode(GameMode.SURVIVAL);
						player.setHealth(20);
						player.setExp(0);
					}
					
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "worldborder set 2000");
					
					DeleteLobby();
					
					Random rand = new Random();
					Chunk chunk = world.getChunkAt(0, 0);
					
					LobbyPopulator.lobbyPopulator.populate(world, rand, chunk);
					
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "tp @a 13.5 170 14.5");
					
					
					
					getServer().getScheduler().cancelAllTasks();
					sender.sendMessage(frFR ? "[GA16 UHC] Reset fini" : "[GA16 UHC] Reset complete");
				}else if(args[0].equalsIgnoreCase("spec")){
					if(started || !setup){
						sender.sendMessage(frFR ? "[GA16 UHC] Erreur, le jeu est deja en cours ou alors vous avez oubliez de faire la commande /uhcrun setup." : "[GA16 UHC] Error, the game is either in progress or the /uhcrun setup command hasn't been used.");
						return false;
					}
					Player player = (Player) sender;
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), (frFR ? "say " + player.getDisplayName() + " est maintenant spectateur!" : "say " + player.getDisplayName() + " is now a spectator!"));
					if(player.isOnline()){
						if(board.getPlayerTeam(player) != null)
							board.getPlayerTeam(player).removePlayer(player);
						player.setGameMode(GameMode.SPECTATOR);
					}
				}else if(args[0].equalsIgnoreCase("lang")){
					if(started || setup){
						sender.sendMessage(frFR ? "[GA16 UHC] Erreur, le jeu est deja en cours ou la configuration a déjà été faite." : "[GA16 UHC] Error, the game is in progress or the setup has already been done.");
						return false;
					}
					
					if(args[1].equalsIgnoreCase("en")){
						if(frFR){
							Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "say The language has been set to English!");
							frFR = false;
						}else
							sender.sendMessage("[GA16 UHC] The language is already in English, finishing the setup now...");
					}else if(args[1].equalsIgnoreCase("fr")){
						if(!frFR){
							Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "say Le language a été mis en français!");
							frFR = true;
						}else
							sender.sendMessage("[GA16 UHC] La langue est déjà en Français, terminaison de la configuration en cours...");
					}else{
						sender.sendMessage(frFR ? "[GA16 UHC] Merci de spécifier la langue (en/fr)" : "[GA16 UHC] Please specify the language (en/fr)");
						return false;
					}
					
					if(partialSetup)
						FinishSetup();
				}else{
					sender.sendMessage(frFR ? "[GA16 UHC] Mauvaise commande" : "[GA16 UHC] Wrong command");
					return false;
				}
			}else{
				sender.sendMessage(ChatColor.GREEN + "==========" + ChatColor.GOLD + "UHCRun" + ChatColor.GREEN + "==========");
				sender.sendMessage(ChatColor.GOLD + "/uhcrun setup: " + (frFR ? "Configure le monde pour que le plugin puisse se lancer" : "Sets up the world for the plugin to activate"));
				sender.sendMessage(ChatColor.GOLD + "/uhcrun start: " + (frFR ? "Démarre la partie" : "Starts the game"));
				sender.sendMessage(ChatColor.GOLD + "/uhcrun reset: " + (frFR ? "Reset la partie" : "Resets the game"));
				sender.sendMessage(ChatColor.GOLD + "/uhcrun solo: " + (frFR ? "Mode solo" : "Solo mode"));
				sender.sendMessage(ChatColor.GOLD + "/uhcrun teams <Teams> (2 - 12): " + (frFR ? "Mode par équipe" : "Team mode"));
				sender.sendMessage(ChatColor.GOLD + "/uhcrun teams random: " + (frFR ? "Met les joueurs dans des équipes aléatoire" : "Puts players in random teams"));
				sender.sendMessage(ChatColor.GOLD + "/uhcrun teamsize <PlayersPerTeam>: " + (frFR ? "Le nombre de joueurs par équipe" : "The number of players per team"));
				sender.sendMessage(ChatColor.GOLD + "/uhcrun spec: " + (frFR ? "Vous devenez un spectateur" : "You will become a spectator"));
				sender.sendMessage(ChatColor.GOLD + "/uhcrun lang <en/fr>: " + (frFR ? "Change la langue" : "Changes the language"));
			}
		}
		return false;
	}
	
	public void start(){
		for(Player player : Bukkit.getOnlinePlayers()){
			if(player.getGameMode() == GameMode.SURVIVAL)
				player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 24000, 0));
		}
		
		getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
			 
			public void run() {
				if(!timeUp){
					Score timeLeft;
					if(seconds == 0 && minutes == 0){
						timeUp = true;
					}	
					if(seconds == 0 && minutes != 0){
						seconds = 60;
						minutes--;
					}
					if(seconds > 0){
						seconds--;
						if(seconds == 59){
							oldSeconds = 0;
							oldMinutes = minutes + 1;
						}else{
							oldSeconds = seconds + 1;
							oldMinutes = minutes;
						}
					}
					board.resetScores(Bukkit.getServer().getOfflinePlayer(ChatColor.GREEN + (frFR ? "Temps restant: " + (oldMinutes < 10 ? "0" : "") + Integer.toString(oldMinutes) + ":" + (oldSeconds < 10 ? "0" : "") + Integer.toString(oldSeconds) : "Time left: " + (oldMinutes < 10 ? "0" : "") + Integer.toString(oldMinutes) + ":" + (oldSeconds < 10 ? "0" : "") + Integer.toString(oldSeconds))));
					timeLeft = timer.getScore(Bukkit.getServer().getOfflinePlayer(ChatColor.GREEN + (frFR ? "Temps restant: " + (minutes < 10 ? "0" : "") + Integer.toString(minutes) + ":" + (seconds < 10 ? "0" : "") + Integer.toString(seconds) : "Time left: " + (minutes < 10 ? "0" : "") + Integer.toString(minutes) + ":" + (seconds < 10 ? "0" : "") + Integer.toString(seconds))));
					timeLeft.setScore(2);
					board.resetScores(Bukkit.getServer().getOfflinePlayer(ChatColor.GREEN + (frFR ? "Temps restant: 0-1:0-1" : "Time left: 0-1:0-1")));
				}
				  
				if(playersInt == 0  && solo){
					Bukkit.getScheduler().cancelAllTasks();
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), (frFR ? "say " + ChatColor.GOLD + "Tous " + ChatColor.GOLD + "les " + ChatColor.GOLD + "joueurs " + ChatColor.GOLD + "sont " + ChatColor.GOLD + "mort!" : "say " + ChatColor.GOLD + "All " + ChatColor.GOLD + "the " + ChatColor.GOLD + "players " + ChatColor.GOLD + "are " + ChatColor.GOLD + "dead!"));

					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "worldborder set 2000");
					
					DeleteLobby();
					
					Random rand = new Random();
					Chunk chunk = world.getChunkAt(0, 0);
					
					LobbyPopulator.lobbyPopulator.populate(world, rand, chunk);
					
					started = false;
					
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "tp @a 13.5 170 14.5");
				}else if(playersInt == 1 && solo){
					Bukkit.getScheduler().cancelAllTasks();
					String winner = null;
					for(Player player : Bukkit.getServer().getOnlinePlayers()){
						GameMode gameMode = player.getGameMode();
						if(gameMode == GameMode.SURVIVAL){
							winner = player.getDisplayName();
						}
					}
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), (frFR ? "say " + ChatColor.GOLD + winner + " " + ChatColor.GOLD + "a " + ChatColor.GOLD + "gagné!" : "say " + ChatColor.GOLD + winner + ChatColor.GOLD + " has " + ChatColor.GOLD + "won!"));
					Bukkit.getPlayer(winner).setGameMode(GameMode.SPECTATOR);
					
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "worldborder set 2000");
					
					DeleteLobby();
					
					Random rand = new Random();
					Chunk chunk = world.getChunkAt(0, 0);
					
					LobbyPopulator.lobbyPopulator.populate(world, rand, chunk);
					
					started = false;
					
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "tp @a 13.5 170 14.5");
				}
				  
				if(!solo){
					if(teamsInt == 0){
						Bukkit.getScheduler().cancelAllTasks();
						
						Bukkit.dispatchCommand(Bukkit.getConsoleSender(), (frFR ? "say " + ChatColor.GOLD + "Tous " + ChatColor.GOLD + "les " + ChatColor.GOLD + "équipes " + ChatColor.GOLD + "sont " + ChatColor.GOLD + "elimin�s!" : "say " + ChatColor.GOLD + "All " + ChatColor.GOLD + "the " + ChatColor.GOLD + "teams " + ChatColor.GOLD + "have " + ChatColor.GOLD + "been " + ChatColor.GOLD + "eliminated!"));
						Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "worldborder set 2000");
						
						DeleteLobby();
						
						Random rand = new Random();
						Chunk chunk = world.getChunkAt(0, 0);
						
						LobbyPopulator.lobbyPopulator.populate(world, rand, chunk);
						
						started = false;
						
						Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "tp @a 13.5 170 14.5");
					}else if(teamsInt == 1){
						Bukkit.getScheduler().cancelAllTasks();
						String teamPrefix = null;
						String team = null;
						for(Player player : Bukkit.getServer().getOnlinePlayers()){
							GameMode gameMode = player.getGameMode();
							if(gameMode == GameMode.SURVIVAL){
								teamPrefix = board.getPlayerTeam(player).getPrefix();
								team = board.getPlayerTeam(player).getName();
								player.setGameMode(GameMode.SPECTATOR);
							}	
						}
						if((frFR ? team == "Bleue claire" : team == "Light blue"))
							Bukkit.dispatchCommand(Bukkit.getConsoleSender(), (frFR ? "say " + ChatColor.GOLD + "L'équipe " + teamPrefix +"Bleue " + teamPrefix + "claire " + ChatColor.GOLD + "a " + ChatColor.GOLD + "gagn�!" : "say " + teamPrefix + "Light " + teamPrefix + "blue " + ChatColor.GOLD + "team " + ChatColor.GOLD + "has " + ChatColor.GOLD + "won!"));
						else
							Bukkit.dispatchCommand(Bukkit.getConsoleSender(), (frFR ? "say " + ChatColor.GOLD + "L'équipe " + teamPrefix + team + " " + ChatColor.GOLD + "a " + ChatColor.GOLD + "gagn�!" : "say " + teamPrefix + team + " " + ChatColor.GOLD + "team " + ChatColor.GOLD + "has " + ChatColor.GOLD + "won!"));

						Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "worldborder set 2000");
						
						DeleteLobby();
						
						Random rand = new Random();
						Chunk chunk = world.getChunkAt(0, 0);
						
						LobbyPopulator.lobbyPopulator.populate(world, rand, chunk);
						
						started = false;
						
						Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "tp @a 13.5 170 14.5");
					}
				}
				if(seconds == 0 && minutes == 19){
					PlayerListener.damage = true;
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), (frFR ? "say " + ChatColor.GOLD + "Les " + ChatColor.GOLD + "dégats " + ChatColor.GOLD + "sont " + ChatColor.GOLD + "activés!" : "say " + ChatColor.GOLD + "Damage " + ChatColor.GOLD + "is " + ChatColor.GOLD + "now " + ChatColor.GOLD + "active!"));
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "playsound random.orb @a ~ ~ ~ 10000 1");
				}else if(seconds == 0 && minutes == 15){
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), (frFR ? "say " + ChatColor.GOLD + "Téléportation " + ChatColor.GOLD + "des " + ChatColor.GOLD + "joueurs " + ChatColor.GOLD + "dans " + ChatColor.GOLD + "15 " + ChatColor.GOLD + "minutes!" : "say " + ChatColor.GOLD + "Players " + ChatColor.GOLD + "will " + ChatColor.GOLD + "be " + ChatColor.GOLD + "teleported " + ChatColor.GOLD + "in " + ChatColor.GOLD + "15 " + ChatColor.GOLD + "minutes!"));
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "playsound random.orb @a ~ ~ ~ 10000 1");
				}else if(seconds == 0 && minutes == 10){
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), (frFR ? "say " + ChatColor.GOLD + "Téléportation " + ChatColor.GOLD + "des " + ChatColor.GOLD + "joueurs " + ChatColor.GOLD + "dans " + ChatColor.GOLD + "10 " + ChatColor.GOLD + "minutes!" : "say " + ChatColor.GOLD + "Players " + ChatColor.GOLD + "will " + ChatColor.GOLD + "be " + ChatColor.GOLD + "teleported " + ChatColor.GOLD + "in " + ChatColor.GOLD + "10 " + ChatColor.GOLD + "minutes!"));
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "playsound random.orb @a ~ ~ ~ 10000 1");
				}else if(seconds == 0 && minutes == 5){
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), (frFR ? "say " + ChatColor.GOLD + "Téléportation " + ChatColor.GOLD + "des " + ChatColor.GOLD + "joueurs " + ChatColor.GOLD + "dans " + ChatColor.GOLD + "5 " + ChatColor.GOLD + "minutes!" : "say " + ChatColor.GOLD + "Players " + ChatColor.GOLD + "will " + ChatColor.GOLD + "be " + ChatColor.GOLD + "teleported " + ChatColor.GOLD + "in " + ChatColor.GOLD + "5 " + ChatColor.GOLD + "minutes!"));
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "playsound random.orb @a ~ ~ ~ 10000 1");
				}else if(seconds == 0 && minutes == 4){
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), (frFR ? "say " + ChatColor.GOLD + "Téléportation " + ChatColor.GOLD + "des " + ChatColor.GOLD + "joueurs " + ChatColor.GOLD + "dans " + ChatColor.GOLD + "4 " + ChatColor.GOLD + "minutes!" : "say " + ChatColor.GOLD + "Players " + ChatColor.GOLD + "will " + ChatColor.GOLD + "be " + ChatColor.GOLD + "teleported " + ChatColor.GOLD + "in " + ChatColor.GOLD + "4 " + ChatColor.GOLD + "minutes!"));
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "playsound random.orb @a ~ ~ ~ 10000 1");
				}else if(seconds == 0 && minutes == 3){
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), (frFR ? "say " + ChatColor.GOLD + "Téléportation " + ChatColor.GOLD + "des " + ChatColor.GOLD + "joueurs " + ChatColor.GOLD + "dans " + ChatColor.GOLD + "3 " + ChatColor.GOLD + "minutes!" : "say " + ChatColor.GOLD + "Players " + ChatColor.GOLD + "will " + ChatColor.GOLD + "be " + ChatColor.GOLD + "teleported " + ChatColor.GOLD + "in " + ChatColor.GOLD + "3 " + ChatColor.GOLD + "minutes!"));
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "playsound random.orb @a ~ ~ ~ 10000 1");
				}else if(seconds == 0 && minutes == 2){
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), (frFR ? "say " + ChatColor.GOLD + "Téléportation " + ChatColor.GOLD + "des " + ChatColor.GOLD + "joueurs " + ChatColor.GOLD + "dans " + ChatColor.GOLD + "2 " + ChatColor.GOLD + "minutes!" : "say " + ChatColor.GOLD + "Players " + ChatColor.GOLD + "will " + ChatColor.GOLD + "be " + ChatColor.GOLD + "teleported " + ChatColor.GOLD + "in " + ChatColor.GOLD + "2 " + ChatColor.GOLD + "minutes!"));
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "playsound random.orb @a ~ ~ ~ 10000 1");
				}else if(seconds == 0 && minutes == 1){
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), (frFR ? "say " + ChatColor.GOLD + "Téléportation " + ChatColor.GOLD + "des " + ChatColor.GOLD + "joueurs " + ChatColor.GOLD + "dans " + ChatColor.GOLD + "1 " + ChatColor.GOLD + "minutes!" : "say " + ChatColor.GOLD + "Players " + ChatColor.GOLD + "will " + ChatColor.GOLD + "be " + ChatColor.GOLD + "teleported " + ChatColor.GOLD + "in " + ChatColor.GOLD + "1 " + ChatColor.GOLD + "minutes!"));
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "playsound random.orb @a ~ ~ ~ 10000 1");
				}else if(seconds == 50 && minutes == 0){
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), (frFR ? "say " + ChatColor.GOLD + "Téléportation " + ChatColor.GOLD + "des " + ChatColor.GOLD + "joueurs " + ChatColor.GOLD + "dans " + ChatColor.GOLD + "50 " + ChatColor.GOLD + "secondes!" : "say " + ChatColor.GOLD + "Players " + ChatColor.GOLD + "will " + ChatColor.GOLD + "be " + ChatColor.GOLD + "teleported " + ChatColor.GOLD + "in " + ChatColor.GOLD + "50 " + ChatColor.GOLD + "seconds!"));
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "playsound random.orb @a ~ ~ ~ 10000 1");
				}else if(seconds == 40 && minutes == 0){
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), (frFR ? "say " + ChatColor.GOLD + "Téléportation " + ChatColor.GOLD + "des " + ChatColor.GOLD + "joueurs " + ChatColor.GOLD + "dans " + ChatColor.GOLD + "40 " + ChatColor.GOLD + "secondes!" : "say " + ChatColor.GOLD + "Players " + ChatColor.GOLD + "will " + ChatColor.GOLD + "be " + ChatColor.GOLD + "teleported " + ChatColor.GOLD + "in " + ChatColor.GOLD + "40 " + ChatColor.GOLD + "seconds!"));
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "playsound random.orb @a ~ ~ ~ 10000 1");
				}else if(seconds == 30 && minutes == 0){
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), (frFR ? "say " + ChatColor.GOLD + "Téléportation " + ChatColor.GOLD + "des " + ChatColor.GOLD + "joueurs " + ChatColor.GOLD + "dans " + ChatColor.GOLD + "30 " + ChatColor.GOLD + "secondes!" : "say " + ChatColor.GOLD + "Players " + ChatColor.GOLD + "will " + ChatColor.GOLD + "be " + ChatColor.GOLD + "teleported " + ChatColor.GOLD + "in " + ChatColor.GOLD + "30 " + ChatColor.GOLD + "seconds!"));
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "playsound random.orb @a ~ ~ ~ 10000 1");
				}else if(seconds == 20 && minutes == 0){
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), (frFR ? "say " + ChatColor.GOLD + "Téléportation " + ChatColor.GOLD + "des " + ChatColor.GOLD + "joueurs " + ChatColor.GOLD + "dans " + ChatColor.GOLD + "20 " + ChatColor.GOLD + "secondes!" : "say " + ChatColor.GOLD + "Players " + ChatColor.GOLD + "will " + ChatColor.GOLD + "be " + ChatColor.GOLD + "teleported " + ChatColor.GOLD + "in " + ChatColor.GOLD + "20 " + ChatColor.GOLD + "seconds!"));
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "playsound random.orb @a ~ ~ ~ 10000 1");
				}else if(seconds == 10 && minutes == 0){
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), (frFR ? "say " + ChatColor.GOLD + "Téléportation " + ChatColor.GOLD + "des " + ChatColor.GOLD + "joueurs " + ChatColor.GOLD + "dans " + ChatColor.GOLD + "10 " + ChatColor.GOLD + "secondes!" : "say " + ChatColor.GOLD + "Players " + ChatColor.GOLD + "will " + ChatColor.GOLD + "be " + ChatColor.GOLD + "teleported " + ChatColor.GOLD + "in " + ChatColor.GOLD + "10 " + ChatColor.GOLD + "seconds!"));
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "playsound random.orb @a ~ ~ ~ 10000 1");
				}else if(seconds == 9 && minutes == 0){
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), (frFR ? "say " + ChatColor.GOLD + "Téléportation " + ChatColor.GOLD + "des " + ChatColor.GOLD + "joueurs " + ChatColor.GOLD + "dans " + ChatColor.GOLD + "9 " + ChatColor.GOLD + "secondes!" : "say " + ChatColor.GOLD + "Players " + ChatColor.GOLD + "will " + ChatColor.GOLD + "be " + ChatColor.GOLD + "teleported " + ChatColor.GOLD + "in " + ChatColor.GOLD + "9 " + ChatColor.GOLD + "seconds!"));
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "playsound random.orb @a ~ ~ ~ 10000 1");
				}else if(seconds == 8 && minutes == 0){
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), (frFR ? "say " + ChatColor.GOLD + "Téléportation " + ChatColor.GOLD + "des " + ChatColor.GOLD + "joueurs " + ChatColor.GOLD + "dans " + ChatColor.GOLD + "8 " + ChatColor.GOLD + "secondes!" : "say " + ChatColor.GOLD + "Players " + ChatColor.GOLD + "will " + ChatColor.GOLD + "be " + ChatColor.GOLD + "teleported " + ChatColor.GOLD + "in " + ChatColor.GOLD + "8 " + ChatColor.GOLD + "seconds!"));
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "playsound random.orb @a ~ ~ ~ 10000 1");
				}else if(seconds == 7 && minutes == 0){
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), (frFR ? "say " + ChatColor.GOLD + "Téléportation " + ChatColor.GOLD + "des " + ChatColor.GOLD + "joueurs " + ChatColor.GOLD + "dans " + ChatColor.GOLD + "7 " + ChatColor.GOLD + "secondes!" : "say " + ChatColor.GOLD + "Players " + ChatColor.GOLD + "will " + ChatColor.GOLD + "be " + ChatColor.GOLD + "teleported " + ChatColor.GOLD + "in " + ChatColor.GOLD + "7 " + ChatColor.GOLD + "seconds!"));
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "playsound random.orb @a ~ ~ ~ 10000 1");
				}else if(seconds == 6 && minutes == 0){
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), (frFR ? "say " + ChatColor.GOLD + "Téléportation " + ChatColor.GOLD + "des " + ChatColor.GOLD + "joueurs " + ChatColor.GOLD + "dans " + ChatColor.GOLD + "6 " + ChatColor.GOLD + "secondes!" : "say " + ChatColor.GOLD + "Players " + ChatColor.GOLD + "will " + ChatColor.GOLD + "be " + ChatColor.GOLD + "teleported " + ChatColor.GOLD + "in " + ChatColor.GOLD + "6 " + ChatColor.GOLD + "seconds!"));
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "playsound random.orb @a ~ ~ ~ 10000 1");
				}else if(seconds == 5 && minutes == 0){
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), (frFR ? "say " + ChatColor.GOLD + "Téléportation " + ChatColor.GOLD + "des " + ChatColor.GOLD + "joueurs " + ChatColor.GOLD + "dans " + ChatColor.GOLD + "5 " + ChatColor.GOLD + "secondes!" : "say " + ChatColor.GOLD + "Players " + ChatColor.GOLD + "will " + ChatColor.GOLD + "be " + ChatColor.GOLD + "teleported " + ChatColor.GOLD + "in " + ChatColor.GOLD + "5 " + ChatColor.GOLD + "seconds!"));
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "playsound random.orb @a ~ ~ ~ 10000 1");
				}else if(seconds == 4 && minutes == 0){
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), (frFR ? "say " + ChatColor.GOLD + "Téléportation " + ChatColor.GOLD + "des " + ChatColor.GOLD + "joueurs " + ChatColor.GOLD + "dans " + ChatColor.GOLD + "4 " + ChatColor.GOLD + "secondes!" : "say " + ChatColor.GOLD + "Players " + ChatColor.GOLD + "will " + ChatColor.GOLD + "be " + ChatColor.GOLD + "teleported " + ChatColor.GOLD + "in " + ChatColor.GOLD + "4 " + ChatColor.GOLD + "seconds!"));
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "playsound random.orb @a ~ ~ ~ 10000 1");
				}else if(seconds == 3 && minutes == 0){
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), (frFR ? "say " + ChatColor.GOLD + "Téléportation " + ChatColor.GOLD + "des " + ChatColor.GOLD + "joueurs " + ChatColor.GOLD + "dans " + ChatColor.GOLD + "3 " + ChatColor.GOLD + "secondes!" : "say " + ChatColor.GOLD + "Players " + ChatColor.GOLD + "will " + ChatColor.GOLD + "be " + ChatColor.GOLD + "teleported " + ChatColor.GOLD + "in " + ChatColor.GOLD + "3 " + ChatColor.GOLD + "seconds!"));
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "playsound random.orb @a ~ ~ ~ 10000 1");
				}else if(seconds == 2 && minutes == 0){
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), (frFR ? "say " + ChatColor.GOLD + "Téléportation " + ChatColor.GOLD + "des " + ChatColor.GOLD + "joueurs " + ChatColor.GOLD + "dans " + ChatColor.GOLD + "2 " + ChatColor.GOLD + "secondes!" : "say " + ChatColor.GOLD + "Players " + ChatColor.GOLD + "will " + ChatColor.GOLD + "be " + ChatColor.GOLD + "teleported " + ChatColor.GOLD + "in " + ChatColor.GOLD + "2 " + ChatColor.GOLD + "seconds!"));
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "playsound random.orb @a ~ ~ ~ 10000 1");
				}else if(seconds == 1 && minutes == 0){
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), (frFR ? "say " + ChatColor.GOLD + "Téléportation " + ChatColor.GOLD + "des " + ChatColor.GOLD + "joueurs " + ChatColor.GOLD + "dans " + ChatColor.GOLD + "1 " + ChatColor.GOLD + "secondes!" : "say " + ChatColor.GOLD + "Players " + ChatColor.GOLD + "will " + ChatColor.GOLD + "be " + ChatColor.GOLD + "teleported " + ChatColor.GOLD + "in " + ChatColor.GOLD + "1 " + ChatColor.GOLD + "seconds!"));
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "playsound random.orb @a ~ ~ ~ 10000 1");
				}else if(seconds == 0 && minutes == 0){
					board.resetScores(Bukkit.getServer().getOfflinePlayer(ChatColor.GREEN + (frFR ? "Temps restant: " + (minutes < 10 ? "0" : "") + Integer.toString(minutes) + ":" + (seconds < 10 ? "0" : "") + Integer.toString(seconds) : "Time left: " + (minutes < 10 ? "0" : "") + Integer.toString(minutes) + ":" + (seconds < 10 ? "0" : "") + Integer.toString(seconds))));
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), (frFR ? "say " + ChatColor.GOLD + "Téléportation " + ChatColor.GOLD + "des " + ChatColor.GOLD + "joueurs!" : "say " + ChatColor.GOLD + "Players " + ChatColor.GOLD + "are " + ChatColor.GOLD + "being " + ChatColor.GOLD + "teleported!"));
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), (frFR ? "say " + ChatColor.GOLD + "Le " + ChatColor.GOLD + "PvP " + ChatColor.GOLD + "et " + ChatColor.GOLD + "les " + ChatColor.GOLD + "dégats " + ChatColor.GOLD + "sont "  + ChatColor.GOLD + "désactivés " + ChatColor.GOLD + "pendant " + ChatColor.GOLD + "10 " + ChatColor.GOLD + "secondes!" : "say " + ChatColor.GOLD + "PvP " + ChatColor.GOLD + "and " + ChatColor.GOLD + "damage " + ChatColor.GOLD + "is " + ChatColor.GOLD + "deactivated " + ChatColor.GOLD + "for "  + ChatColor.GOLD + "10 " + ChatColor.GOLD + "seconds!"));
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "playsound random.orb @a ~ ~ ~ 10000 2");
					minutes = -1;
					seconds = -1;
					PlayerListener.damage = false;
					if(solo){
						Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "spreadplayers 0 0 100 400 false @a");
						Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "tp @a ~ 140 ~");
					}else{
						int x = randomCoords(-400, 400);
						int z = randomCoords(-400, 400);
						Location location = new Location(world, x, 140, z);
						int x2 = randomCoords(-400, 400);
						int z2 = randomCoords(-400, 400);
						Location location2 = new Location(world, x2, 140, z2);
						if(teams > 2){
							int x3 = randomCoords(-400, 400);
							int z3 = randomCoords(-400, 400);
							Location location3 = new Location(world, x3, 140, z3);
							for(OfflinePlayer player : board.getTeam(frFR ? "Noire" : "Black").getPlayers()){
								player.getPlayer().teleport(location3);
							}
						}if(teams > 3){
							int x4 = randomCoords(-400, 400);
							int z4 = randomCoords(-400, 400);
							Location location4 = new Location(world, x4, 140, z4);
							for(OfflinePlayer player : board.getTeam(frFR ? "Blanche" : "White").getPlayers()){
								player.getPlayer().teleport(location4);
							}
						}if(teams > 4){
							int x4 = randomCoords(-400, 400);
							int z4 = randomCoords(-400, 400);
							Location location4 = new Location(world, x4, 140, z4);
							for(OfflinePlayer player : board.getTeam(frFR ? "Bleue" : "Blue").getPlayers()){
								player.getPlayer().teleport(location4);
							}
						}if(teams > 5){
							int x4 = randomCoords(-400, 400);
							int z4 = randomCoords(-400, 400);
							Location location4 = new Location(world, x4, 140, z4);
							for(OfflinePlayer player : board.getTeam(frFR ? "Cyan" : "Cyan").getPlayers()){
								player.getPlayer().teleport(location4);
							}
						}if(teams > 6){
							int x4 = randomCoords(-400, 400);
							int z4 = randomCoords(-400, 400);
							Location location4 = new Location(world, x4, 140, z4);
							for(OfflinePlayer player : board.getTeam(frFR ? "Violette" : "Purple").getPlayers()){
								player.getPlayer().teleport(location4);
							}
						}if(teams > 7){
							int x4 = randomCoords(-400, 400);
							int z4 = randomCoords(-400, 400);
							Location location4 = new Location(world, x4, 140, z4);
							for(OfflinePlayer player : board.getTeam(frFR ? "Rose" : "Pink").getPlayers()){
								player.getPlayer().teleport(location4);
							}
						}if(teams > 8){
							int x4 = randomCoords(-400, 400);
							int z4 = randomCoords(-400, 400);
							Location location4 = new Location(world, x4, 140, z4);
							for(OfflinePlayer player : board.getTeam(frFR ? "Grise" : "Gray").getPlayers()){
								player.getPlayer().teleport(location4);
							}
						}if(teams > 9){
							int x4 = randomCoords(-400, 400);
							int z4 = randomCoords(-400, 400);
							Location location4 = new Location(world, x4, 140, z4);
							for(OfflinePlayer player : board.getTeam(frFR ? "Verte" : "Green").getPlayers()){
								player.getPlayer().teleport(location4);
							}
						}if(teams > 10){
							int x4 = randomCoords(-400, 400);
							int z4 = randomCoords(-400, 400);
							Location location4 = new Location(world, x4, 140, z4);
							for(OfflinePlayer player : board.getTeam(frFR ? "Orange" : "Orange").getPlayers()){
								player.getPlayer().teleport(location4);
							}
						}if(teams > 11){
							int x4 = randomCoords(-400, 400);
							int z4 = randomCoords(-400, 400);
							Location location4 = new Location(world, x4, 140, z4);
							for(OfflinePlayer player : board.getTeam(frFR ? "Jaune" : "Yellow").getPlayers()){
								player.getPlayer().teleport(location4);
							}
						}
						for(OfflinePlayer player : board.getTeam(frFR ? "Rouge" : "Red").getPlayers()){
							player.getPlayer().teleport(location);
						}
						for(OfflinePlayer player : board.getTeam(frFR ? "Bleue claire" : "Light blue").getPlayers()){
							player.getPlayer().teleport(location2);
						}
					}
					for(Player player : Bukkit.getOnlinePlayers()){
						if(player.getGameMode() == GameMode.SPECTATOR)
							Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "tp " + player.getDisplayName() + " 13.5 170 14.5");
					}
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "worldborder set 800");
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "worldborder set 20 600");
					DamageTimer();
				  }
			}
		}, 0L, 20L);
	}
	
	public void DamageTimer(){
		getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
			 
			  public void run() {
				  	PlayerListener.damage = true;
				  	PlayerListener.pvp = true;
				  	Bukkit.dispatchCommand(Bukkit.getConsoleSender(), (frFR ? "say " + ChatColor.GOLD + "Les " + ChatColor.GOLD + "dégats " + ChatColor.GOLD + "sont " + ChatColor.GOLD + "activés!" : "say " + ChatColor.GOLD + "Damage " + ChatColor.GOLD + "is " + ChatColor.GOLD + "now " + ChatColor.GOLD + "active!"));
				  	Bukkit.dispatchCommand(Bukkit.getConsoleSender(), (frFR ? "say " + ChatColor.GOLD + "Le " + ChatColor.GOLD + "PvP " + ChatColor.GOLD + "est " + ChatColor.GOLD + "activé!" : "say " + ChatColor.GOLD + "PvP " + ChatColor.GOLD + "is " + ChatColor.GOLD + "now " + ChatColor.GOLD + "active!"));
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "playsound random.orb @a ~ ~ ~ 10000 1");
				  }
		}, 200L);
	}
	
	private int randomCoords(int min, int max){
		Random rand = new Random();
		int coord = rand.nextInt((max - min) + min);
		return coord;
	}
	
	private ItemStack setItemNameAndLore(ItemStack item, String name, String[] lore) {
		ItemMeta im = item.getItemMeta();
		im.setDisplayName(name);
		im.setLore(Arrays.asList(lore));
        item.setItemMeta(im);
        return item;
    }
	
	public void updateTeamInventory(){
		if(board.getTeam(frFR ? "Rouge" : "Red").getSize() > 0){
			redTeamPlayers.clear();
			for(OfflinePlayer player : board.getTeam(frFR ? "Rouge" : "Red").getPlayers()){
				redTeamPlayers.add(ChatColor.RED + player.getPlayer().getDisplayName());
			}
			teamInventory.setItem(10, setItemNameAndLore(new ItemStack(Material.WOOL, 1, DyeColor.RED.getData()), ChatColor.RED + (frFR ? "équipe Rouge" : "Red Team"), new String[]
			{
				ChatColor.RED + (frFR ? "Joueurs dans l'équipe:" : "Players in this team:"),
				ChatColor.RED + redTeamPlayers.toString()
			}));
		}else{
			teamInventory.setItem(10, setItemNameAndLore(new ItemStack(Material.WOOL, 1, DyeColor.RED.getData()), ChatColor.RED + (frFR ? "équipe Rouge" : "Red Team"), new String[]
				{
					ChatColor.RED + (frFR ? "Il n'y a pas de joueurs dans l'équipe" : "No players are in this team")
				}));
		}if(board.getTeam(frFR ? "Bleue claire" : "Light blue").getSize() > 0){
			lightBlueTeamPlayers.clear();
			for(OfflinePlayer player : board.getTeam(frFR ? "Bleue claire" : "Light blue").getPlayers()){
				lightBlueTeamPlayers.add(ChatColor.BLUE + player.getPlayer().getDisplayName());
			}
			teamInventory.setItem(11, setItemNameAndLore(new ItemStack(Material.WOOL, 1, DyeColor.LIGHT_BLUE.getData()), ChatColor.BLUE + (frFR ? "équipe Bleue claire" : "Light blue Team"), new String[]
			{
				ChatColor.BLUE + (frFR ? "Joueurs dans l'équipe:" : "Players in this team:"),
				ChatColor.BLUE + lightBlueTeamPlayers.toString()
			}));
		}else{
			teamInventory.setItem(11, setItemNameAndLore(new ItemStack(Material.WOOL, 1, DyeColor.LIGHT_BLUE.getData()), ChatColor.BLUE + (frFR ? "équipe Bleue claire" : "Light blue Team"), new String[]
			{
				ChatColor.BLUE + (frFR ? "Il n'y a pas de joueurs dans l'équipe" : "No players are in this team")
			}));
		}if(teams < 3){
			teamInventory.setItem(12, new ItemStack(Material.AIR));
		}if(teams < 4){
			teamInventory.setItem(13, new ItemStack(Material.AIR));
		}if(teams < 5){
			teamInventory.setItem(14, new ItemStack(Material.AIR));
		}if(teams < 6){
			teamInventory.setItem(15, new ItemStack(Material.AIR));
		}if(teams < 7){
			teamInventory.setItem(16, new ItemStack(Material.AIR));
		}if(teams < 8){
			teamInventory.setItem(20, new ItemStack(Material.AIR));
		}if(teams < 9){
			teamInventory.setItem(21, new ItemStack(Material.AIR));
		}if(teams < 10){
			teamInventory.setItem(22, new ItemStack(Material.AIR));
		}if(teams < 11){
			teamInventory.setItem(23, new ItemStack(Material.AIR));
		}if(teams < 12){
			teamInventory.setItem(24, new ItemStack(Material.AIR));
		}if(teams > 2){
			if(board.getTeam(frFR ? "Noire" : "Black").getSize() > 0){
				blackTeamPlayers.clear();
				for(OfflinePlayer player : board.getTeam(frFR ? "Noire" : "Black").getPlayers()){
					blackTeamPlayers.add(ChatColor.BLACK + player.getPlayer().getDisplayName());
				}
				teamInventory.setItem(12, setItemNameAndLore(new ItemStack(Material.WOOL, 1, DyeColor.BLACK.getData()), ChatColor.BLACK + (frFR ? "équipe Noire" : "Black Team"), new String[]
				{
					ChatColor.BLACK + (frFR ? "Joueurs dans l'équipe:" : "Players in this team:"),
					ChatColor.BLACK + blackTeamPlayers.toString()
				}));
			}else{
				teamInventory.setItem(12, setItemNameAndLore(new ItemStack(Material.WOOL, 1, DyeColor.BLACK.getData()), ChatColor.BLACK + (frFR ? "équipe Noire" : "Black Team"), new String[]
				{
					ChatColor.BLACK + (frFR ? "Il n'y a pas de joueurs dans l'équipe" : "No players are in this team")
				}));
			}
		}if(teams > 3){
			if(board.getTeam(frFR ? "Blanche" : "White").getSize() > 0){
				whiteTeamPlayers.clear();
				for(OfflinePlayer player : board.getTeam(frFR ? "Blanche" : "White").getPlayers()){
					whiteTeamPlayers.add(ChatColor.WHITE + player.getPlayer().getDisplayName());
				}
				teamInventory.setItem(13, setItemNameAndLore(new ItemStack(Material.WOOL, 1, DyeColor.WHITE.getData()), ChatColor.WHITE + (frFR ? "équipe Blanche" : "White Team"), new String[]
				{
					ChatColor.WHITE + (frFR ? "Joueurs dans l'équipe:" : "Players in this team:"),
					ChatColor.WHITE + whiteTeamPlayers.toString()
				}));
			}else{
				teamInventory.setItem(13, setItemNameAndLore(new ItemStack(Material.WOOL, 1, DyeColor.WHITE.getData()), ChatColor.WHITE + (frFR ? "équipe Blanche" : "White Team"), new String[]
				{
					ChatColor.WHITE + (frFR ? "Il n'y a pas de joueurs dans l'équipe" : "No players are in this team")
				}));
			}
		}if(teams > 4){
			if(board.getTeam(frFR ? "Bleue" : "Blue").getSize() > 0){
				blueTeamPlayers.clear();
				for(OfflinePlayer player : board.getTeam(frFR ? "Bleue" : "Blue").getPlayers()){
					blueTeamPlayers.add(ChatColor.DARK_BLUE + player.getPlayer().getDisplayName());
				}
				teamInventory.setItem(14, setItemNameAndLore(new ItemStack(Material.WOOL, 1, DyeColor.BLUE.getData()), ChatColor.DARK_BLUE + (frFR ? "équipe Bleue" : "Blue Team"), new String[]
				{
					ChatColor.DARK_BLUE + (frFR ? "Joueurs dans l'équipe:" : "Players in this team:"),
					ChatColor.DARK_BLUE + blueTeamPlayers.toString()
				}));
			}else{
				teamInventory.setItem(14, setItemNameAndLore(new ItemStack(Material.WOOL, 1, DyeColor.BLUE.getData()), ChatColor.DARK_BLUE + (frFR ? "équipe Bleue" : "Blue Team"), new String[]
				{
					ChatColor.DARK_BLUE + (frFR ? "Il n'y a pas de joueurs dans l'équipe" : "No players are in this team")
				}));
			}
		}if(teams > 5){
			if(board.getTeam(frFR ? "Cyan" : "Cyan").getSize() > 0){
				cyanTeamPlayers.clear();
				for(OfflinePlayer player : board.getTeam(frFR ? "Cyan" : "Cyan").getPlayers()){
					cyanTeamPlayers.add(ChatColor.AQUA + player.getPlayer().getDisplayName());
				}
				teamInventory.setItem(15, setItemNameAndLore(new ItemStack(Material.WOOL, 1, DyeColor.CYAN.getData()), ChatColor.AQUA + (frFR ? "équipe Cyan" : "Cyan Team"), new String[]
				{
					ChatColor.AQUA + (frFR ? "Joueurs dans l'équipe:" : "Players in this team:"),
					ChatColor.AQUA + cyanTeamPlayers.toString()
				}));
			}else{
				teamInventory.setItem(15, setItemNameAndLore(new ItemStack(Material.WOOL, 1, DyeColor.CYAN.getData()), ChatColor.AQUA + (frFR ? "équipe Cyan" : "Cyan Team"), new String[]
				{
					ChatColor.AQUA + (frFR ? "Il n'y a pas de joueurs dans l'équipe" : "No players are in this team")
				}));
			}
		}if(teams > 6){
			if(board.getTeam(frFR ? "Violette" : "Purple").getSize() > 0){
				purpleTeamPlayers.clear();
				for(OfflinePlayer player : board.getTeam(frFR ? "Violette" : "Purple").getPlayers()){
					purpleTeamPlayers.add(ChatColor.DARK_PURPLE + player.getPlayer().getDisplayName());
				}
				teamInventory.setItem(16, setItemNameAndLore(new ItemStack(Material.WOOL, 1, DyeColor.PURPLE.getData()), ChatColor.DARK_PURPLE + (frFR ? "équipe Violette" : "Purple Team"), new String[]
				{
					ChatColor.DARK_PURPLE + (frFR ? "Joueurs dans l'équipe:" : "Players in this team:"),
					ChatColor.DARK_PURPLE + purpleTeamPlayers.toString()
				}));
			}else{
				teamInventory.setItem(16, setItemNameAndLore(new ItemStack(Material.WOOL, 1, DyeColor.PURPLE.getData()), ChatColor.DARK_PURPLE + (frFR ? "équipe Violette" : "Purple Team"), new String[]
				{
					ChatColor.DARK_PURPLE + (frFR ? "Il n'y a pas de joueurs dans l'équipe" : "No players are in this team")
				}));
			}
		}if(teams > 7){
			if(board.getTeam(frFR ? "Rose" : "Pink").getSize() > 0){
				pinkTeamPlayers.clear();
				for(OfflinePlayer player : board.getTeam(frFR ? "Rose" : "Pink").getPlayers()){
					pinkTeamPlayers.add(ChatColor.LIGHT_PURPLE + player.getPlayer().getDisplayName());
				}
				teamInventory.setItem(20, setItemNameAndLore(new ItemStack(Material.WOOL, 1, DyeColor.PINK.getData()), ChatColor.LIGHT_PURPLE + (frFR ? "équipe Rose" : "Pink Team"), new String[]
				{
					ChatColor.LIGHT_PURPLE + (frFR ? "Joueurs dans l'équipe:" : "Players in this team:"),
					ChatColor.LIGHT_PURPLE + pinkTeamPlayers.toString()
				}));
			}else{
				teamInventory.setItem(20, setItemNameAndLore(new ItemStack(Material.WOOL, 1, DyeColor.PINK.getData()), ChatColor.LIGHT_PURPLE + (frFR ? "équipe Rose" : "Pink Team"), new String[]
				{
					ChatColor.LIGHT_PURPLE + (frFR ? "Il n'y a pas de joueurs dans l'équipe" : "No players are in this team")
				}));
			}
		}if(teams > 8){
			if(board.getTeam(frFR ? "Grise" : "Gray").getSize() > 0){
				grayTeamPlayers.clear();
				for(OfflinePlayer player : board.getTeam(frFR ? "Grise" : "Gray").getPlayers()){
					grayTeamPlayers.add(ChatColor.GRAY + player.getPlayer().getDisplayName());
				}
				teamInventory.setItem(21, setItemNameAndLore(new ItemStack(Material.WOOL, 1, DyeColor.GRAY.getData()), ChatColor.GRAY + (frFR ? "équipe Grise" : "Gray Team"), new String[]
				{
					ChatColor.GRAY + (frFR ? "Joueurs dans l'équipe:" : "Players in this team:"),
					ChatColor.GRAY + grayTeamPlayers.toString()
				}));
			}else{
				teamInventory.setItem(21, setItemNameAndLore(new ItemStack(Material.WOOL, 1, DyeColor.GRAY.getData()), ChatColor.GRAY + (frFR ? "équipe Grise" : "Gray Team"), new String[]
				{
					ChatColor.GRAY + (frFR ? "Il n'y a pas de joueurs dans l'équipe" : "No players are in this team")
				}));
			}
		}if(teams > 9){
			if(board.getTeam(frFR ? "Verte" : "Green").getSize() > 0){
				greenTeamPlayers.clear();
				for(OfflinePlayer player : board.getTeam(frFR ? "Verte" : "Green").getPlayers()){
					greenTeamPlayers.add(ChatColor.GREEN + player.getPlayer().getDisplayName());
				}
				teamInventory.setItem(22, setItemNameAndLore(new ItemStack(Material.WOOL, 1, DyeColor.GREEN.getData()), ChatColor.GREEN + (frFR ? "équipe Verte" : "Green Team"), new String[]
				{
					ChatColor.GREEN + (frFR ? "Joueurs dans l'équipe:" : "Players in this team:"),
					ChatColor.GREEN + greenTeamPlayers.toString()
				}));
			}else{
				teamInventory.setItem(22, setItemNameAndLore(new ItemStack(Material.WOOL, 1, DyeColor.GREEN.getData()), ChatColor.GREEN + (frFR ? "équipe Verte" : "Green Team"), new String[]
				{
					ChatColor.GREEN + (frFR ? "Il n'y a pas de joueurs dans l'équipe" : "No players are in this team")
				}));
			}
		}if(teams > 10){
			if(board.getTeam(frFR ? "Orange" : "Orange").getSize() > 0){
				orangeTeamPlayers.clear();
				for(OfflinePlayer player : board.getTeam(frFR ? "Orange" : "Orange").getPlayers()){
					orangeTeamPlayers.add(ChatColor.GOLD + player.getPlayer().getDisplayName());
				}
				teamInventory.setItem(23, setItemNameAndLore(new ItemStack(Material.WOOL, 1, DyeColor.ORANGE.getData()), ChatColor.GOLD + (frFR ? "équipe Orange" : "Orange Team"), new String[]
				{
					ChatColor.GOLD + (frFR ? "Joueurs dans l'équipe:" : "Players in this team:"),
					ChatColor.GOLD + orangeTeamPlayers.toString()
				}));
			}else{
				teamInventory.setItem(23, setItemNameAndLore(new ItemStack(Material.WOOL, 1, DyeColor.ORANGE.getData()), ChatColor.GOLD + (frFR ? "équipe Orange" : "Orange Team"), new String[]
				{
					ChatColor.GOLD + (frFR ? "Il n'y a pas de joueurs dans l'équipe" : "No players are in this team")
				}));
			}
		}if(teams == 12){
			if(board.getTeam(frFR ? "Jaune" : "Yellow").getSize() > 0){
				yellowTeamPlayers.clear();
				for(OfflinePlayer player : board.getTeam(frFR ? "Jaune" : "Yellow").getPlayers()){
					yellowTeamPlayers.add(ChatColor.YELLOW + player.getPlayer().getDisplayName());
				}
				teamInventory.setItem(24, setItemNameAndLore(new ItemStack(Material.WOOL, 1, DyeColor.YELLOW.getData()), ChatColor.YELLOW + (frFR ? "équipe Jaune" : "Yellow Team"), new String[]
				{
					ChatColor.YELLOW + (frFR ? "Joueurs dans l'équipe:" : "Players in this team:"),
					ChatColor.YELLOW + yellowTeamPlayers.toString()
				}));
			}else{
				teamInventory.setItem(24, setItemNameAndLore(new ItemStack(Material.WOOL, 1, DyeColor.YELLOW.getData()), ChatColor.YELLOW + (frFR ? "équipe Jaune" : "Yellow Team"), new String[]
				{
					ChatColor.YELLOW + (frFR ? "Il n'y a pas de joueurs dans l'équipe" : "No players are in this team")
				}));
			}
		}
	}
	
	public void DeleteLobby(){
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
					Block block = world.getBlockAt(x, y, z);
					block.setType(Material.AIR);
					for(Entity entity : world.getEntities()){
						if(entity instanceof Item)
							entity.remove();
					}
				}
			}
		}
	}
	
	public void RandomizeTeams(){
		Random random = new Random();
		for(Player player : Bukkit.getOnlinePlayers()){
			if(board.getPlayerTeam(player) != null)
				board.getPlayerTeam(player).removePlayer(player);
		}
		for(Player player : this.getServer().getOnlinePlayers()){
			if(player.getGameMode() == GameMode.SPECTATOR)
				continue;
			
			while(board.getPlayerTeam(player) == null){
				String playerName = player.getDisplayName();
				int randomInt = teams == 12 ? random.nextInt(12) + 1 : teams == 11 ? random.nextInt(11) + 1 : teams == 10 ? random.nextInt(10) + 1 : teams == 9 ? random.nextInt(9) + 1 : teams == 8 ? random.nextInt(8) + 1 : teams == 7 ? random.nextInt(7) + 1 : teams == 6 ? random.nextInt(6) + 1 : teams == 5 ? random.nextInt(5) + 1 : teams == 4 ? random.nextInt(4) + 1 : teams == 3 ? random.nextInt(3) + 1 : random.nextInt(2) + 1;
				
				if(randomInt == 1 && board.getTeam(frFR ? "Rouge" : "Red").getSize() < teamSize){
					board.getTeam(frFR ? "Rouge" : "Red").addPlayer(player);
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), (frFR ? "say " + board.getPlayerTeam(player).getPrefix() + playerName + " est dans l'équipe " + board.getPlayerTeam(player).getPrefix() + board.getPlayerTeam(player).getDisplayName() : "say " + board.getPlayerTeam(player).getPrefix() + playerName + " has joined " + board.getPlayerTeam(player).getPrefix() + board.getPlayerTeam(player).getDisplayName() + " team"));
				}else if(randomInt == 2 && board.getTeam(frFR ? "Bleue claire" : "Light blue").getSize() < teamSize){
					board.getTeam(frFR ? "Bleue claire" : "Light blue").addPlayer(player);
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), (frFR ? "say " + board.getPlayerTeam(player).getPrefix() + playerName + " est dans l'équipe " + board.getPlayerTeam(player).getPrefix() + "Bleue " + board.getPlayerTeam(player).getPrefix() + "claire" : "say " + board.getPlayerTeam(player).getPrefix() + playerName + " has joined " + board.getPlayerTeam(player).getPrefix() + "Light " + board.getPlayerTeam(player).getPrefix() + "blue team"));
				}else if(randomInt == 3 && board.getTeam(frFR ? "Noire" : "Black").getSize() < teamSize){
					board.getTeam(frFR ? "Noire" : "Black").addPlayer(player);
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), (frFR ? "say " + board.getPlayerTeam(player).getPrefix() + playerName + " est dans l'équipe " + board.getPlayerTeam(player).getPrefix() + board.getPlayerTeam(player).getDisplayName() : "say " + board.getPlayerTeam(player).getPrefix() + playerName + " has joined " + board.getPlayerTeam(player).getPrefix() + board.getPlayerTeam(player).getDisplayName() + " team"));
				}else if(randomInt == 4 && board.getTeam(frFR ? "Blanche" : "White").getSize() < teamSize){
					board.getTeam(frFR ? "Blanche" : "White").addPlayer(player);
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), (frFR ? "say " + board.getPlayerTeam(player).getPrefix() + playerName + " est dans l'équipe " + board.getPlayerTeam(player).getPrefix() + board.getPlayerTeam(player).getDisplayName() : "say " + board.getPlayerTeam(player).getPrefix() + playerName + " has joined " + board.getPlayerTeam(player).getPrefix() + board.getPlayerTeam(player).getDisplayName() + " team"));
				}else if(randomInt == 5 && board.getTeam(frFR ? "Bleue" : "Blue").getSize() < teamSize){
					board.getTeam(frFR ? "Bleue" : "Blue").addPlayer(player);
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), (frFR ? "say " + board.getPlayerTeam(player).getPrefix() + playerName + " est dans l'équipe " + board.getPlayerTeam(player).getPrefix() + board.getPlayerTeam(player).getDisplayName() : "say " + board.getPlayerTeam(player).getPrefix() + playerName + " has joined " + board.getPlayerTeam(player).getPrefix() + board.getPlayerTeam(player).getDisplayName() + " team"));
				}else if(randomInt == 6 && board.getTeam(frFR ? "Cyan" : "Cyan").getSize() < teamSize){
					board.getTeam(frFR ? "Cyan" : "Cyan").addPlayer(player);
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), (frFR ? "say " + board.getPlayerTeam(player).getPrefix() + playerName + " est dans l'équipe " + board.getPlayerTeam(player).getPrefix() + board.getPlayerTeam(player).getDisplayName() : "say " + board.getPlayerTeam(player).getPrefix() + playerName + " has joined " + board.getPlayerTeam(player).getPrefix() + board.getPlayerTeam(player).getDisplayName() + " team"));
				}else if(randomInt == 7 && board.getTeam(frFR ? "Violette" : "Purple").getSize() < teamSize){
					board.getTeam(frFR ? "Violette" : "Purple").addPlayer(player);
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), (frFR ? "say " + board.getPlayerTeam(player).getPrefix() + playerName + " est dans l'équipe " + board.getPlayerTeam(player).getPrefix() + board.getPlayerTeam(player).getDisplayName() : "say " + board.getPlayerTeam(player).getPrefix() + playerName + " has joined " + board.getPlayerTeam(player).getPrefix() + board.getPlayerTeam(player).getDisplayName() + " team"));
				}else if(randomInt == 8 && board.getTeam(frFR ? "Rose" : "Pink").getSize() < teamSize){
					board.getTeam(frFR ? "Rose" : "Pink").addPlayer(player);
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), (frFR ? "say " + board.getPlayerTeam(player).getPrefix() + playerName + " est dans l'équipe " + board.getPlayerTeam(player).getPrefix() + board.getPlayerTeam(player).getDisplayName() : "say " + board.getPlayerTeam(player).getPrefix() + playerName + " has joined " + board.getPlayerTeam(player).getPrefix() + board.getPlayerTeam(player).getDisplayName() + " team"));
				}else if(randomInt == 9 && board.getTeam(frFR ? "Grise" : "Gray").getSize() < teamSize){
					board.getTeam(frFR ? "Grise" : "Gray").addPlayer(player);
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), (frFR ? "say " + board.getPlayerTeam(player).getPrefix() + playerName + " est dans l'équipe " + board.getPlayerTeam(player).getPrefix() + board.getPlayerTeam(player).getDisplayName() : "say " + board.getPlayerTeam(player).getPrefix() + playerName + " has joined " + board.getPlayerTeam(player).getPrefix() + board.getPlayerTeam(player).getDisplayName() + " team"));
				}else if(randomInt == 10 && board.getTeam(frFR ? "Verte" : "Green").getSize() < teamSize){
					board.getTeam(frFR ? "Verte" : "Green").addPlayer(player);
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), (frFR ? "say " + board.getPlayerTeam(player).getPrefix() + playerName + " est dans l'équipe " + board.getPlayerTeam(player).getPrefix() + board.getPlayerTeam(player).getDisplayName() : "say " + board.getPlayerTeam(player).getPrefix() + playerName + " has joined " + board.getPlayerTeam(player).getPrefix() + board.getPlayerTeam(player).getDisplayName() + " team"));
				}else if(randomInt == 11 && board.getTeam(frFR ? "Orange" : "Orange").getSize() < teamSize){
					board.getTeam(frFR ? "Orange" : "Orange").addPlayer(player);
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), (frFR ? "say " + board.getPlayerTeam(player).getPrefix() + playerName + " est dans l'équipe " + board.getPlayerTeam(player).getPrefix() + board.getPlayerTeam(player).getDisplayName() : "say " + board.getPlayerTeam(player).getPrefix() + playerName + " has joined " + board.getPlayerTeam(player).getPrefix() + board.getPlayerTeam(player).getDisplayName() + " team"));
				}else if(randomInt == 12 && board.getTeam(frFR ? "Jaune" : "Yellow").getSize() < teamSize){
					board.getTeam(frFR ? "Jaune" : "Yellow").addPlayer(player);
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), (frFR ? "say " + board.getPlayerTeam(player).getPrefix() + playerName + " est dans l'équipe " + board.getPlayerTeam(player).getPrefix() + board.getPlayerTeam(player).getDisplayName() : "say " + board.getPlayerTeam(player).getPrefix() + playerName + " has joined " + board.getPlayerTeam(player).getPrefix() + board.getPlayerTeam(player).getDisplayName() + " team"));
				}
			}
			
			updateTeamInventory();
		}
	}
	
	public void SetPlayerRandomTeam(Player player){
		Random random = new Random();

		while(board.getPlayerTeam(player) == null){
			String playerName = player.getDisplayName();
			int randomInt = teams == 12 ? random.nextInt(12) + 1 : teams == 11 ? random.nextInt(11) + 1 : teams == 10 ? random.nextInt(10) + 1 : teams == 9 ? random.nextInt(9) + 1 : teams == 8 ? random.nextInt(8) + 1 : teams == 7 ? random.nextInt(7) + 1 : teams == 6 ? random.nextInt(6) + 1 : teams == 5 ? random.nextInt(5) + 1 : teams == 4 ? random.nextInt(4) + 1 : teams == 3 ? random.nextInt(3) + 1 : random.nextInt(2) + 1;

			if(randomInt == 1 && board.getTeam(frFR ? "Rouge" : "Red").getSize() < teamSize){
				board.getTeam(frFR ? "Rouge" : "Red").addPlayer(player);
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), (frFR ? "say " + board.getPlayerTeam(player).getPrefix() + playerName + " est dans l'équipe " + board.getPlayerTeam(player).getPrefix() + board.getPlayerTeam(player).getDisplayName() : "say " + board.getPlayerTeam(player).getPrefix() + playerName + " has joined " + board.getPlayerTeam(player).getPrefix() + board.getPlayerTeam(player).getDisplayName() + " team"));
			}else if(randomInt == 2 && board.getTeam(frFR ? "Bleue claire" : "Light blue").getSize() < teamSize){
				board.getTeam(frFR ? "Bleue claire" : "Light blue").addPlayer(player);
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), (frFR ? "say " + board.getPlayerTeam(player).getPrefix() + playerName + " est dans l'équipe " + board.getPlayerTeam(player).getPrefix() + "Bleue " + board.getPlayerTeam(player).getPrefix() + "claire" : "say " + board.getPlayerTeam(player).getPrefix() + playerName + " has joined " + board.getPlayerTeam(player).getPrefix() + "Light " + board.getPlayerTeam(player).getPrefix() + "blue team"));
			}else if(randomInt == 3 && board.getTeam(frFR ? "Noire" : "Black").getSize() < teamSize){
				board.getTeam(frFR ? "Noire" : "Black").addPlayer(player);
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), (frFR ? "say " + board.getPlayerTeam(player).getPrefix() + playerName + " est dans l'équipe " + board.getPlayerTeam(player).getPrefix() + board.getPlayerTeam(player).getDisplayName() : "say " + board.getPlayerTeam(player).getPrefix() + playerName + " has joined " + board.getPlayerTeam(player).getPrefix() + board.getPlayerTeam(player).getDisplayName() + " team"));
			}else if(randomInt == 4 && board.getTeam(frFR ? "Blanche" : "White").getSize() < teamSize){
				board.getTeam(frFR ? "Blanche" : "White").addPlayer(player);
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), (frFR ? "say " + board.getPlayerTeam(player).getPrefix() + playerName + " est dans l'équipe " + board.getPlayerTeam(player).getPrefix() + board.getPlayerTeam(player).getDisplayName() : "say " + board.getPlayerTeam(player).getPrefix() + playerName + " has joined " + board.getPlayerTeam(player).getPrefix() + board.getPlayerTeam(player).getDisplayName() + " team"));
			}else if(randomInt == 5 && board.getTeam(frFR ? "Bleue" : "Blue").getSize() < teamSize){
				board.getTeam(frFR ? "Bleue" : "Blue").addPlayer(player);
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), (frFR ? "say " + board.getPlayerTeam(player).getPrefix() + playerName + " est dans l'équipe " + board.getPlayerTeam(player).getPrefix() + board.getPlayerTeam(player).getDisplayName() : "say " + board.getPlayerTeam(player).getPrefix() + playerName + " has joined " + board.getPlayerTeam(player).getPrefix() + board.getPlayerTeam(player).getDisplayName() + " team"));
			}else if(randomInt == 6 && board.getTeam(frFR ? "Cyan" : "Cyan").getSize() < teamSize){
				board.getTeam(frFR ? "Cyan" : "Cyan").addPlayer(player);
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), (frFR ? "say " + board.getPlayerTeam(player).getPrefix() + playerName + " est dans l'équipe " + board.getPlayerTeam(player).getPrefix() + board.getPlayerTeam(player).getDisplayName() : "say " + board.getPlayerTeam(player).getPrefix() + playerName + " has joined " + board.getPlayerTeam(player).getPrefix() + board.getPlayerTeam(player).getDisplayName() + " team"));
			}else if(randomInt == 7 && board.getTeam(frFR ? "Violette" : "Purple").getSize() < teamSize){
				board.getTeam(frFR ? "Violette" : "Purple").addPlayer(player);
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), (frFR ? "say " + board.getPlayerTeam(player).getPrefix() + playerName + " est dans l'équipe " + board.getPlayerTeam(player).getPrefix() + board.getPlayerTeam(player).getDisplayName() : "say " + board.getPlayerTeam(player).getPrefix() + playerName + " has joined " + board.getPlayerTeam(player).getPrefix() + board.getPlayerTeam(player).getDisplayName() + " team"));
			}else if(randomInt == 8 && board.getTeam(frFR ? "Rose" : "Pink").getSize() < teamSize){
				board.getTeam(frFR ? "Rose" : "Pink").addPlayer(player);
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), (frFR ? "say " + board.getPlayerTeam(player).getPrefix() + playerName + " est dans l'équipe " + board.getPlayerTeam(player).getPrefix() + board.getPlayerTeam(player).getDisplayName() : "say " + board.getPlayerTeam(player).getPrefix() + playerName + " has joined " + board.getPlayerTeam(player).getPrefix() + board.getPlayerTeam(player).getDisplayName() + " team"));
			}else if(randomInt == 9 && board.getTeam(frFR ? "Grise" : "Gray").getSize() < teamSize){
				board.getTeam(frFR ? "Grise" : "Gray").addPlayer(player);
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), (frFR ? "say " + board.getPlayerTeam(player).getPrefix() + playerName + " est dans l'équipe " + board.getPlayerTeam(player).getPrefix() + board.getPlayerTeam(player).getDisplayName() : "say " + board.getPlayerTeam(player).getPrefix() + playerName + " has joined " + board.getPlayerTeam(player).getPrefix() + board.getPlayerTeam(player).getDisplayName() + " team"));
			}else if(randomInt == 10 && board.getTeam(frFR ? "Verte" : "Green").getSize() < teamSize){
				board.getTeam(frFR ? "Verte" : "Green").addPlayer(player);
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), (frFR ? "say " + board.getPlayerTeam(player).getPrefix() + playerName + " est dans l'équipe " + board.getPlayerTeam(player).getPrefix() + board.getPlayerTeam(player).getDisplayName() : "say " + board.getPlayerTeam(player).getPrefix() + playerName + " has joined " + board.getPlayerTeam(player).getPrefix() + board.getPlayerTeam(player).getDisplayName() + " team"));
			}else if(randomInt == 11 && board.getTeam(frFR ? "Orange" : "Orange").getSize() < teamSize){
				board.getTeam(frFR ? "Orange" : "Orange").addPlayer(player);
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), (frFR ? "say " + board.getPlayerTeam(player).getPrefix() + playerName + " est dans l'équipe " + board.getPlayerTeam(player).getPrefix() + board.getPlayerTeam(player).getDisplayName() : "say " + board.getPlayerTeam(player).getPrefix() + playerName + " has joined " + board.getPlayerTeam(player).getPrefix() + board.getPlayerTeam(player).getDisplayName() + " team"));
			}else if(randomInt == 12 && board.getTeam(frFR ? "Jaune" : "Yellow").getSize() < teamSize){
				board.getTeam(frFR ? "Jaune" : "Yellow").addPlayer(player);
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), (frFR ? "say " + board.getPlayerTeam(player).getPrefix() + playerName + " est dans l'équipe " + board.getPlayerTeam(player).getPrefix() + board.getPlayerTeam(player).getDisplayName() : "say " + board.getPlayerTeam(player).getPrefix() + playerName + " has joined " + board.getPlayerTeam(player).getPrefix() + board.getPlayerTeam(player).getDisplayName() + " team"));
			}
		}

		updateTeamInventory();
	}
	
	public void FinishSetup(){
		board.registerNewTeam(frFR ? "Rouge" : "Red");
		board.getTeam(frFR ? "Rouge" : "Red").setAllowFriendlyFire(false);
		board.getTeam(frFR ? "Rouge" : "Red").setPrefix(ChatColor.RED.toString());
		board.registerNewTeam(frFR ? "Bleue claire" : "Light blue");
		board.getTeam(frFR ? "Bleue claire" : "Light blue").setAllowFriendlyFire(false);
		board.getTeam(frFR ? "Bleue claire" : "Light blue").setPrefix(ChatColor.BLUE.toString());
		board.registerNewTeam(frFR ? "Noire" : "Black");
		board.getTeam(frFR ? "Noire" : "Black").setAllowFriendlyFire(false);
		board.getTeam(frFR ? "Noire" : "Black").setPrefix(ChatColor.BLACK.toString());
		board.registerNewTeam(frFR ? "Blanche" : "White");
		board.getTeam(frFR ? "Blanche" : "White").setAllowFriendlyFire(false);
		board.getTeam(frFR ? "Blanche" : "White").setPrefix(ChatColor.WHITE.toString());
		board.registerNewTeam(frFR ? "Bleue" : "Blue");
		board.getTeam(frFR ? "Bleue" : "Blue").setAllowFriendlyFire(false);
		board.getTeam(frFR ? "Bleue" : "Blue").setPrefix(ChatColor.DARK_BLUE.toString());
		board.registerNewTeam(frFR ? "Cyan" : "Cyan");
		board.getTeam(frFR ? "Cyan" : "Cyan").setAllowFriendlyFire(false);
		board.getTeam(frFR ? "Cyan" : "Cyan").setPrefix(ChatColor.AQUA.toString());
		board.registerNewTeam(frFR ? "Violette" : "Purple");
		board.getTeam(frFR ? "Violette" : "Purple").setAllowFriendlyFire(false);
		board.getTeam(frFR ? "Violette" : "Purple").setPrefix(ChatColor.DARK_PURPLE.toString());
		board.registerNewTeam(frFR ? "Rose" : "Pink");
		board.getTeam(frFR ? "Rose" : "Pink").setAllowFriendlyFire(false);
		board.getTeam(frFR ? "Rose" : "Pink").setPrefix(ChatColor.LIGHT_PURPLE.toString());
		board.registerNewTeam(frFR ? "Grise" : "Gray");
		board.getTeam(frFR ? "Grise" : "Gray").setAllowFriendlyFire(false);
		board.getTeam(frFR ? "Grise" : "Gray").setPrefix(ChatColor.GRAY.toString());
		board.registerNewTeam(frFR ? "Verte" : "Green");
		board.getTeam(frFR ? "Verte" : "Green").setAllowFriendlyFire(false);
		board.getTeam(frFR ? "Verte" : "Green").setPrefix(ChatColor.GREEN.toString());
		board.registerNewTeam(frFR ? "Orange" : "Orange");
		board.getTeam(frFR ? "Orange" : "Orange").setAllowFriendlyFire(false);
		board.getTeam(frFR ? "Orange" : "Orange").setPrefix(ChatColor.GOLD.toString());
		board.registerNewTeam(frFR ? "Jaune" : "Yellow");
		board.getTeam(frFR ? "Jaune" : "Yellow").setAllowFriendlyFire(false);
		board.getTeam(frFR ? "Jaune" : "Yellow").setPrefix(ChatColor.YELLOW.toString());
		
		teamInventory = Bukkit.createInventory(null, 36, (frFR ? "équipes" : "Teams"));
		
		updateTeamInventory();
		
		world = setupSender.getWorld();
		
		setup = true;
		setupSender.sendMessage(frFR ? "[GA UHC] Configuration terminée" : "[GA UHC] Setup complete");
	}
}
