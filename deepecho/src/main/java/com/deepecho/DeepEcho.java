package com.deepecho;

import com.deepecho.listeners.SonarListener;
import org.bukkit.plugin.java.JavaPlugin;

public class DeepEcho extends JavaPlugin {

    private static DeepEcho instance;

    @Override
    public void onEnable() {

        instance = this;

        getServer().getPluginManager().registerEvents(
                new SonarListener(this), this
        );

        getLogger().info("DeepEcho attivo. Il sottosuolo ora risponde...");
    }

    public static DeepEcho getInstance() {
        return instance;
    }
}
