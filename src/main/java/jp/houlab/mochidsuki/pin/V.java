package jp.houlab.mochidsuki.pin;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.HashMap;

/**
 * ピンの場所を記録する
 * @author Mochidsuki
 */
public class V {
    /**
     * 通常ピン
     */
    static public HashMap<Player, Location> pin = new HashMap<>();

    /**
     * 危険ピン
     */
    static public HashMap<Player, Location> pinRed = new HashMap<>();
}
