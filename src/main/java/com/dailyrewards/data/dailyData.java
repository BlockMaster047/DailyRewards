package com.dailyrewards.data;

import java.io.*;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Nullable;

import org.bukkit.entity.Player;

import com.dailyrewards.DailyRewards;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

public class dailyData {
    private static Map<UUID, Map<String, Long>> rewards = new HashMap<>();

    public static void addClaim(Player p, String name) throws IOException {
        if (!rewards.containsKey(p.getUniqueId())) {
            rewards.put(p.getUniqueId(), new HashMap<>());
        }
        rewards.get(p.getUniqueId()).put(name, System.currentTimeMillis());
        save();
    }

    public static void removeClaim(Player p, String name) throws IOException {
        if (rewards.containsKey(p.getUniqueId())) {
            rewards.get(p.getUniqueId()).remove(name);
            save();
        }
    }

    public static @Nullable Long getClaim(Player p, String name) {
        if (rewards.containsKey(p.getUniqueId())) {
            return rewards.get(p.getUniqueId()).getOrDefault(name, null);
        }
        return null;
    }

    public static void save() throws IOException {
        Gson gson = new Gson();
        File file = new File(DailyRewards.getPlugin().getDataFolder().getAbsolutePath() + "/data/daily.json");
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        if (!file.exists()) {
            file.createNewFile();
        }
        try (Writer writer = new FileWriter(file)) {
            gson.toJson(rewards, writer);
        }
    }

    public static void load() throws IOException {
        Gson gson = new Gson();
        File file = new File(DailyRewards.getPlugin().getDataFolder().getAbsolutePath() + "/data/daily.json");
        if (file.exists()) {
            try (Reader reader = new FileReader(file)) {
                Type type = new TypeToken<Map<UUID, Map<String, Long>>>() {}.getType();
                Map<UUID, Map<String, Long>> loadedRewards = gson.fromJson(reader, type);

                if (loadedRewards == null) {
                    loadedRewards = new HashMap<>();
                }

                rewards.clear();
                rewards.putAll(loadedRewards);
                DailyRewards.getPlugin().getLogger().info("Daily data loaded successfully.");
            } catch (JsonSyntaxException | JsonIOException e) {
                DailyRewards.getPlugin().getLogger().severe("Failed to load daily data: Invalid JSON format.");
                rewards.clear();
            }
        } else {
            rewards.clear();
            DailyRewards.getPlugin().getLogger().warning("No daily data file found. Starting with an empty rewards map.");
        }
    }
}