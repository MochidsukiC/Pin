package jp.houlab.mochidsuki.pin;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Team;

public class EveryTicks extends BukkitRunnable {
    @Override
    public void run() {
        for(Player player : Pin.plugin.getServer().getOnlinePlayers()){
            Protocol protocol = new Protocol();
            Team team = player.getScoreboard().getPlayerTeam(player);
            if(team != null){
                Location[] location = new Location[team.getEntries().size()];
                Location[] locationR = new Location[team.getEntries().size()];
                int i = 0;
                for(String playerName : team.getEntries()){

                    if(Bukkit.getOfflinePlayer(playerName).isOnline()) {

                        Player teammate = Pin.plugin.getServer().getPlayer(playerName);
                        location[i] = V.pin.get(teammate);
                        locationR[i] = V.pinRed.get(teammate);

                        if(teammate != player){
                            protocol.setGlowing(teammate, player);
                        }

                        i++;
                    }
                }
                protocol.pushPin(player,location, EntityType.DRAGON_FIREBALL,0);
                protocol.pushPin(player,locationR,EntityType.FIREBALL,team.getEntries().size());
            }else {
                Location[] location = new Location[1];
                Location[] locationR = new Location[1];

                location[0] = V.pin.get(player);
                locationR[0] = V.pin.get(player);

                protocol.pushPin(player,location, EntityType.DRAGON_FIREBALL,0);
                protocol.pushPin(player,locationR,EntityType.FIREBALL,10);
            }
        }
    }
}
