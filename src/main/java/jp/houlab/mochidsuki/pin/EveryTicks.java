package jp.houlab.mochidsuki.pin;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Team;

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
        }
    }
}
