package jp.houlab.mochidsuki.pin;

import io.papermc.paper.event.player.PlayerTrackEntityEvent;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;

import static jp.houlab.mochidsuki.pin.Pin.config;
import static jp.houlab.mochidsuki.pin.Pin.plugin;
import org.bukkit.event.server.ServerCommandEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.annotation.Repeatable;

import static jp.houlab.mochidsuki.pin.Pin.plugin;

/**
 * リスナークラス
 * @author Mochidsuki
 */
public class Listener implements org.bukkit.event.Listener {
    /**
     * ピンを指す
     * @param event イベント
     */
    @EventHandler
    public void PlayerInteractEvent(PlayerInteractEvent event) {
        if (event.getMaterial() == Material.FILLED_MAP) {
            if (event.getPlayer().isSneaking()) {
                if(event.getPlayer().getCooldown(event.getMaterial()) == 0){
                    Component name = Component.text(event.getPlayer().getName());
                    Component message =Component.text("<");
                    Component afterMessage = Component.text(">" +config.getStringList("MessageList").get(playerSelectMessageIndex.getOrDefault(event.getPlayer().getUniqueId(),0)));
                    if(event.getPlayer().getScoreboard().getPlayerTeam(event.getPlayer()) != null){
                        try {
                            name = name.color(event.getPlayer().getScoreboard().getPlayerTeam(event.getPlayer()).color());
                        }catch (Exception ignored){}
                        message = message.append(name).append(afterMessage);
                        for(String s : event.getPlayer().getScoreboard().getPlayerTeam(event.getPlayer()).getEntries()){
                            if(Bukkit.getOfflinePlayer(s).isOnline()){
                                Bukkit.getPlayer(s).sendMessage(message);
                            }
                        }
                    }else {
                        message = message.append(name).append(afterMessage);
                        event.getPlayer().getServer().sendMessage(message);
                    }
                    event.getPlayer().setCooldown(event.getMaterial(),20);
                }
                if(event.getAction().isRightClick()){
                    return;
                }
            }
            if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                if (event.getPlayer().getInventory().getItemInMainHand().getType() == Material.FILLED_MAP || (event.getPlayer().isSneaking() && (event.getPlayer().getInventory().getItemInMainHand().getType() == Material.WOODEN_SWORD || event.getPlayer().getInventory().getItemInMainHand().getType() == Material.STONE_SWORD || event.getPlayer().getInventory().getItemInMainHand().getType() == Material.IRON_SWORD || event.getPlayer().getInventory().getItemInMainHand().getType() == Material.DIAMOND_SWORD || event.getPlayer().getInventory().getItemInMainHand().getType() == Material.NETHERITE_SWORD))) {
                    //赤ピン
                    if (event.getPlayer().getTargetBlockExact(400) != null) {
                        Utilities.pinRed.put(event.getPlayer(), event.getPlayer().getTargetBlockExact(400).getLocation());
                        if (event.getPlayer().getScoreboard().getPlayerTeam(event.getPlayer()) != null) {
                            for (String name : event.getPlayer().getScoreboard().getPlayerTeam(event.getPlayer()).getEntries()) {//teamplayer全員に実行
                                if (event.getPlayer().getServer().getOfflinePlayer(name).isOnline()) {
                                    Player player = event.getPlayer().getServer().getPlayer(name);
                                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BIT, 50, 0);
                                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BIT, 50, 0.3F);
                                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BIT, 50, 0.6F);
                                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BIT, 50, 1);
                                }
                            }
                        } else {
                            Player player = event.getPlayer();
                            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BIT, 50, 0);
                            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BIT, 50, 0.3F);
                            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BIT, 50, 0.6F);
                            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BIT, 50, 1);
                        }
                    } else {
                        Utilities.pinRed.remove(event.getPlayer());
                        event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.BLOCK_FIRE_EXTINGUISH, 0.5F, 1);
                    }

                }
            } else if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
                //黄ピン
                if (event.getPlayer().getTargetBlockExact(400) != null) {
                    Utilities.pin.put(event.getPlayer(), event.getPlayer().getTargetBlockExact(400).getLocation());

                    if (event.getPlayer().getScoreboard().getPlayerTeam(event.getPlayer()) != null) {
                        for (String name : event.getPlayer().getScoreboard().getPlayerTeam(event.getPlayer()).getEntries()) {//teamplayer全員に実行
                            if (event.getPlayer().getServer().getOfflinePlayer(name).isOnline()) {
                                Player player = event.getPlayer().getServer().getPlayer(name);
                                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_FLUTE, 100, 0);
                            }
                        }
                    } else {
                        Player player = event.getPlayer();
                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_FLUTE, 100, 0);
                    }


                } else {
                    Utilities.pin.remove(event.getPlayer());
                    event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.BLOCK_FIRE_EXTINGUISH, 0.5F, 1);
                }
            }

        }
    }


    public static Map<UUID, Integer> playerSelectMessageIndex = new HashMap<>();

    @EventHandler
    public void PlayerItemHeldEvent(PlayerItemHeldEvent event) {
        if (event.getPlayer().getInventory().getItem(event.getPreviousSlot()) != null && event.getPlayer().getInventory().getItem(event.getPreviousSlot()).getType().equals(Material.matchMaterial(config.getString("PinMaterial")))) {
            Player player = event.getPlayer();
            if (player.isSneaking()) {
                event.setCancelled(true);
                int index = 0;
                if (playerSelectMessageIndex.containsKey(player.getUniqueId())) {
                    index = playerSelectMessageIndex.get(player.getUniqueId());
                }

                if (event.getNewSlot() == event.getPreviousSlot() + 1 || (event.getNewSlot() == 0 && event.getPreviousSlot() == 8)) {
                    if (index == config.getStringList("MessageList").size() - 1) {
                        index = 0;
                    } else {
                        index++;
                    }
                } else if (event.getNewSlot() == event.getPreviousSlot() - 1 || (event.getNewSlot() == 8 && event.getPreviousSlot() == 0)) {
                    if (index == 0) {
                        index = config.getStringList("MessageList").size() - 1;
                    } else {
                        index--;
                    }
                }
                playerSelectMessageIndex.put(player.getUniqueId(), index);
            }
        }
    }

    @EventHandler
    public void PlayerTrackEntityEvent(PlayerTrackEntityEvent event){
        if(event.getEntity() instanceof Player) {
            Player player = event.getPlayer();
            Player loadPlayer = (Player) event.getEntity();
            if(Utilities.getGlowPlayerList(player).contains(loadPlayer.getUniqueId())){
                Protocol.setGlowing(loadPlayer,player);
            }
        }
    }

    @EventHandler
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
        String command = event.getMessage().toLowerCase(); // コマンドを小文字に変換

        // /team コマンドで始まっているか確認
            if (command.startsWith("/team ")) {
            new BukkitRunnable() {

                @Override
                public void run() {
                    for(Player player : event.getPlayer().getServer().getOnlinePlayers()){
                        Utilities.updatePlayerMetaData(player);
                    }
                }
            }.runTaskLater(plugin, 1);

        }
    }
    @EventHandler
    public void onServerCommand(ServerCommandEvent event) {
        CommandSender sender = event.getSender();
        String command = event.getCommand().toLowerCase(); // コマンドを小文字に変換

        // team コマンドで始まっているか確認 (こちらには / が付かない)
        if (command.startsWith("team ")) {
            for(Player player : plugin.getServer().getOnlinePlayers()){
                Utilities.updatePlayerMetaData(player);
            }
            // こちらもキャンセル可能
            // event.setCancelled(true);
        }
    }

}

