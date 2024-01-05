package eu.lotusgc.mc.command;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import eu.lotusgc.mc.ext.LotusController;
import eu.lotusgc.mc.main.Main;

public class BuildCMD implements CommandExecutor, Listener{
	
	private static List<Player> allowedPlayers = new ArrayList<>();

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(sender instanceof Player player) {
			LotusController lc = new LotusController();
			if(args.length == 1) {
				Player player2 = Bukkit.getPlayerExact(args[0]);
				if(player2.isOnline()) {
					if(player.hasPermission("lgc.build.others")) {
						if(allowedPlayers.contains(player2)) {
							lc.sendMessageReady(player, "cmd.build.others.self.remove");
							lc.sendMessageReady(player2, "cmd.build.others.recipient.remove");
							allowedPlayers.remove(player2);
						}else {
							lc.sendMessageReady(player, "cmd.build.others.self.add");
							lc.sendMessageReady(player2, "cmd.build.others.recipient.add");
							allowedPlayers.add(player2);
						}
					}else {
						lc.noPerm(player, "lgc.build.others");
					}
				}else {
					lc.sendMessageReady(player, "global.playerOffline");
				}
			}else {
				if(player.hasPermission("lgc.build.self")) {
					if(allowedPlayers.contains(player)) {
						allowedPlayers.remove(player);
						lc.sendMessageReady(player, "cmd.build.self.remove");
					}else {
						allowedPlayers.add(player);
						lc.sendMessageReady(player, "cmd.build.self.add");
					}
				}else {
					lc.noPerm(player, "lgc.build.self");
				}
			}
		}else {
			sender.sendMessage(Main.consoleSend);
		}
		return false;
	}
	
	public static boolean hasPlayer(Player player) {
		return allowedPlayers.contains(player);
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		Player player = event.getPlayer();
		if(allowedPlayers.contains(player)) {
			event.setCancelled(false);
		}else {
			event.setCancelled(true);
			new LotusController().sendMessageReady(player, "event.build.blockpb.cantDoThat");
		}
	}
	
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		Player player = event.getPlayer();
		if(allowedPlayers.contains(player)) {
			event.setCancelled(false);
		}else {
			event.setCancelled(true);
			new LotusController().sendMessageReady(player, "event.build.blockpb.cantDoThat");
		}
	}
	
	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		if(event.getAction() == Action.PHYSICAL) {
			if(player.getLocation().getBlock().getType() != Material.BIG_DRIPLEAF) {
				event.setCancelled(true);
				new LotusController().sendMessageReady(player, "event.build.wheat.cantDoThat");
			}
		}
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		if(allowedPlayers.contains(event.getPlayer())) {
			allowedPlayers.remove(event.getPlayer());
		}
	}
}