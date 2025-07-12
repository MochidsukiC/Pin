package jp.houlab.mochidsuki.pin;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.title.TitlePart;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Team;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.time.Duration;
import java.util.List;

import static jp.houlab.mochidsuki.pin.Listener.playerSelectMessageIndex;
import static jp.houlab.mochidsuki.pin.Pin.config;

/**
 * 毎ティック実行されるクラス
 * @author Mochidsuki
 * ピンを毎秒表示する
 */
public class EveryTicks extends BukkitRunnable {
    /**
     * 実行
     */
    @Override
    public void run() {
        /*
        for(Team team : Utilities.getShowGlowTeamSet()){
            Utilities.updateGlowingTeamMember(team);
        }

         */
        for(Player player : Pin.plugin.getServer().getOnlinePlayers()){

            Team team = player.getScoreboard().getPlayerTeam(player);
            if(team != null){
                Location[] location = new Location[team.getEntries().size()];
                Location[] locationR = new Location[team.getEntries().size()];
                int i = 0;
                for(String playerName : team.getEntries()){

                    if(Bukkit.getOfflinePlayer(playerName).isOnline()) {

                        Player teammate = Pin.plugin.getServer().getPlayer(playerName);
                        location[i] = Utilities.pin.get(teammate);
                        locationR[i] = Utilities.pinRed.get(teammate);

                        if(teammate != null && teammate != player){

                            Protocol.setGlowing(teammate, player);


                        }

                        i++;
                    }
                }
                Protocol.pushPin(player,location, EntityType.DRAGON_FIREBALL,0);
                Protocol.pushPin(player,locationR,EntityType.FIREBALL,team.getEntries().size());
            }else {
                Location[] location = new Location[1];
                Location[] locationR = new Location[1];

                location[0] = Utilities.pin.get(player);
                locationR[0] = Utilities.pin.get(player);

                Protocol.pushPin(player,location, EntityType.DRAGON_FIREBALL,0);
                Protocol.pushPin(player,locationR,EntityType.FIREBALL,10);
            }

            //スニーク+ピンアイテムイベントハンドラー(現在メッセージUI表示に使用)
            if(player.isSneaking() && player.getInventory().getItemInMainHand().getType().equals(Material.matchMaterial(config.getString("PinMaterial"))) && (player.getGameMode().equals(GameMode.ADVENTURE) || player.getGameMode().equals(GameMode.SURVIVAL) || !player.getLocation().clone().add(0,-1,0).getBlock().getType().equals(Material.AIR))){
                int index = playerSelectMessageIndex.getOrDefault(player.getUniqueId(),0);
                int behindIndex = index-1;
                int afterIndex = index+1;
                if(behindIndex < 0){
                    behindIndex = config.getStringList("MessageList").size()-1;
                }
                if(afterIndex > config.getStringList("MessageList").size()-1){
                    afterIndex = 0;
                }

                List<String> messageList = config.getStringList("MessageList");

                Title title = Title.title(Component.text(""),
                        Component.text(String.format("%-8s",messageList.get(behindIndex)).replace(" ", "　")+" ").color(NamedTextColor.WHITE).append(Component.text("["+messageList.get(index)+"]").color(NamedTextColor.YELLOW).decoration(TextDecoration.BOLD,true).decoration(TextDecoration.UNDERLINED,true)).append(Component.text(" " + String.format("%8s",messageList.get(afterIndex)).replace(" ", "　")).color(NamedTextColor.WHITE)),
                        Title.Times.times(Duration.ZERO,Duration.ofMillis(10),Duration.ofSeconds(1))
                );

                player.showTitle(title);
            }
        }
    }
}
