package jp.houlab.mochidsuki.pin;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.bukkit.scoreboard.Team;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ピンの場所を記録する
 * @author Mochidsuki
 */
public class Utilities {
    /**
     * 通常ピン
     */
    static public HashMap<Player, Location> pin = new HashMap<>();

    /**
     * 危険ピン
     */
    static public HashMap<Player, Location> pinRed = new HashMap<>();

    static final private Map<UUID, Set<UUID>> showGlowPlayerListMap = new ConcurrentHashMap<>();

    public static void addGlowing(Player player,Player glowPlayer) {

        Protocol.setGlowing(glowPlayer,player);
        showGlowPlayerListMap.computeIfAbsent(player.getUniqueId(), k -> new HashSet<>()).add(glowPlayer.getUniqueId());
        updatePlayerMetaData(glowPlayer);
    }

    public static void removeGlowing(Player player,Player glowingPlayer) {
        if(showGlowPlayerListMap.containsKey(player.getUniqueId())) {
            showGlowPlayerListMap.get(player.getUniqueId()).remove(glowingPlayer.getUniqueId());
        }
        updatePlayerMetaData(glowingPlayer);
    }

    public static Set<UUID> getGlowPlayerList(Player player) {
        return showGlowPlayerListMap.getOrDefault(player.getUniqueId(), Collections.emptySet());
    }

    public static void updatePlayerMetaData(Player player){
        final boolean originalSilentState = player.isSilent();
        player.setSilent(!originalSilentState);
        player.setSilent(originalSilentState);
    }

    /*
    private static final Set<Team> showGlowTeamSet = new HashSet<>();

    public static void addGlowingTeam(Team team) {
        if(team != null) {
            showGlowTeamSet.add(team);
        }
    }

    public static void removeGlowingTeam(Team team) {
        if(team != null) {
            showGlowTeamSet.remove(team);
        }
    }

    public static Set<Team> getShowGlowTeamSet(){
        return showGlowTeamSet;
    }

    public static boolean isGlowingTeam(Team team){
        if(team != null) {
            return showGlowTeamSet.contains(team);
        }else {
            return false;
        }
    }

    public static void updateGlowingTeamMember(Team team){
        if(isGlowingTeam(team)){
            for(String name : team.getEntries()){
                if(Bukkit.getOfflinePlayer(name).isOnline()) {
                    Player player = Bukkit.getPlayer(name);
                    if (player != null) {
                        for(String otherName: team.getEntries()){
                            if(Bukkit.getOfflinePlayer(otherName).isOnline()) {
                                Player otherPlayer = Bukkit.getPlayer(otherName);
                                if(otherPlayer != null && getGlowPlayerList(player).contains(otherPlayer.getUniqueId())) {
                                    addGlowing(player,otherPlayer);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

     */

}
