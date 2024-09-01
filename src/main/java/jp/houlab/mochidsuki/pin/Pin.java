package jp.houlab.mochidsuki.pin;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;


public final class Pin extends JavaPlugin {

    static public FileConfiguration config;
    static public Plugin plugin;

    static public ProtocolManager protocolManager;
    @Override
    public void onEnable() {
        // Plugin startup logic
        saveDefaultConfig();
        config = getConfig();

        //everyticks
        new EveryTicks().runTaskTimer(this,0,1);

        //plugin
        plugin = this;

        //ProtocolLib
        protocolManager = ProtocolLibrary.getProtocolManager();

        //Event
        getServer().getPluginManager().registerEvents(new Listener(),this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
