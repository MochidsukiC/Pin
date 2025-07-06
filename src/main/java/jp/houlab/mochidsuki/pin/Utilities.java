package jp.houlab.mochidsuki.pin;

import org.bukkit.Location;
import org.bukkit.entity.Player;

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
        new Protocol().setGlowing(glowPlayer,player);
        showGlowPlayerListMap.computeIfAbsent(player.getUniqueId(), k -> new HashSet<>()).add(glowPlayer.getUniqueId());
    }

    public static void removeGlowing(Player player,Player glowingPlayer) {
        if(showGlowPlayerListMap.containsKey(player.getUniqueId())) {
            showGlowPlayerListMap.get(player.getUniqueId()).remove(glowingPlayer.getUniqueId());
        }
    }

    public static Set<UUID> getGlowPlayerList(Player player) {
        return showGlowPlayerListMap.getOrDefault(player.getUniqueId(), Collections.emptySet());
    }

}
