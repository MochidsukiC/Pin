package jp.houlab.mochidsuki.pin;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;
import org.jetbrains.annotations.NotNull;

import static jp.houlab.mochidsuki.pin.Pin.plugin;

public class CommandListener implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(command.getName().equalsIgnoreCase("pin")){
            switch (args[0]){
                /*
                case "team":{
                    if(args.length == 2){
                        if(args[1].equalsIgnoreCase("all")){
                            for(Team team : plugin.getServer().getScoreboardManager().getMainScoreboard().getTeams()){
                                Utilities.addGlowingTeam(team);
                            }
                        }
                    }
                }

                 */
            }
        }



        if(label.equals("team")){
            for(Player player : plugin.getServer().getOnlinePlayers()){
                Utilities.updatePlayerMetaData(player);
            }
        }
        return false;
    }
}
