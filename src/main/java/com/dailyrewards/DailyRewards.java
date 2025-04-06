package com.dailyrewards;

import java.io.IOException;
import java.util.logging.Logger;

import org.bukkit.plugin.java.JavaPlugin;

import com.dailyrewards.commands.daily;
import com.dailyrewards.data.dailyData;
import com.dailyrewards.data.dailyRewards;
import java.util.Objects;

public class DailyRewards extends JavaPlugin {
    private static DailyRewards plugin;
    public static DailyRewards getPlugin(){return plugin;}

    private static final Logger LOGGER = Logger.getLogger(DailyRewards.class.getName());
    public void onEnable(){
        plugin = this;
        LOGGER.info("Daily Rewards has been enabled!");

        Objects.requireNonNull(getCommand("daily")).setExecutor(new daily());


        //Load
        try {
            dailyData.load();
            dailyRewards.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
