package com.ga16.uhcrun.events;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import com.ga16.uhcrun.UHCRun;

public class ChatListener implements Listener {

	public static UHCRun plugin;

	public ChatListener(UHCRun instance){
		plugin = instance;

	}

	@EventHandler
	public void onChat(AsyncPlayerChatEvent event){
		Player player = event.getPlayer();
		String message = event.getMessage();
		String playerName = player.getDisplayName();
		
		if(player.getGameMode() == GameMode.SPECTATOR){
			event.setCancelled(true);
			for(Player specs : Bukkit.getOnlinePlayers()){
				if(specs.getGameMode() == GameMode.SPECTATOR)
					specs.sendMessage("[Spec] <" + playerName + "> " + message);
			}
		}
		
		if(!plugin.solo && plugin.setup && player.getGameMode() != GameMode.SPECTATOR){
			if(plugin.board.getPlayerTeam(player) != null)
				playerName = plugin.board.getPlayerTeam(player).getPrefix() + player.getDisplayName();
			if(!message.startsWith("!")){
				event.setCancelled(true);
				for(OfflinePlayer teamMate : plugin.board.getPlayerTeam(player).getPlayers()){
					if(teamMate.isOnline()){
						teamMate.getPlayer().sendMessage(plugin.board.getPlayerTeam(player).getPrefix() + "[" + (plugin.frFR ? "Equipe" : "Team") + "]" + " <" + playerName + "> " + message);
					}
				}
			}else{
				event.setCancelled(true);
				for(Player onlinePlayer : Bukkit.getServer().getOnlinePlayers()){
					onlinePlayer.sendMessage("[" + (plugin.frFR ? "Tous" : "All") + "] <" + playerName + ChatColor.WHITE + "> " + message.substring(1));
				}
			}
		}else{
			event.setCancelled(false);
		}
	}
}
