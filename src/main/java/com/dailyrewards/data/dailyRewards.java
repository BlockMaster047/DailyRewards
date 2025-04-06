package com.dailyrewards.data;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import com.dailyrewards.DailyRewards;
import org.bukkit.inventory.ItemStack;
import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

public class dailyRewards {

    private static Map<String, List<ItemStack>> rewards = new HashMap<>();

    public static void addReward(String name, List<ItemStack> items) throws IOException {
        if (!rewards.containsKey(name)) {
            rewards.put(name, items);
            save();
        }
    }

    public static @Nullable List<ItemStack> getRewards(String name) {
        if (rewards.containsKey(name)) {
            return rewards.get(name);
        }
        return null;
    }

    public static void addItemToReward(String name, ItemStack item) throws IOException {
        if (rewards.containsKey(name)) {
            List<ItemStack> items = rewards.get(name);
            if (item != null) {
                items.add(item);
                rewards.replace(name, items);
                save();
            }
        }
    }

    public static void removeItemFromReward(String name, ItemStack item) throws IOException {
        if (rewards.containsKey(name)) {
            if (item != null) {
                List<ItemStack> items = rewards.get(name);
                items.remove(item);
                rewards.replace(name, items);
                save();
            }
        }
    }

    public static void removeReward(String name) throws IOException {
        if (rewards.containsKey(name)) {
            rewards.remove(name);
            save();
        }
    }

    public static Map<String, List<ItemStack>> getAllRewards() {
        return rewards;
    }

    public static boolean hasReward(String name) {
        return rewards.containsKey(name);
    }

    public static void save() throws IOException {
        Gson gson = new Gson();
        File file = new File(DailyRewards.getPlugin().getDataFolder().getAbsolutePath() + "/data/dailyRewards.json");
        file.getParentFile().mkdirs();
        file.createNewFile();
        String encodedObject;
        Map<String, String> saveData = new HashMap<>();
        for (Map.Entry<String, List<ItemStack>> e : rewards.entrySet()) {
            if (e.getValue() != null) {
                List<ItemStack> items = e.getValue();
                byte[] serializedObject = ItemStack.serializeItemsAsBytes(items);
                encodedObject = new String(Base64.getEncoder().encode(serializedObject));
                saveData.put(e.getKey(), encodedObject);

                Writer writer = new FileWriter(file);
                gson.toJson(saveData, writer);
                writer.flush();
                writer.close();
            }
        }
    }

    public static void load() throws IOException {
        Gson gson = new Gson();
        File file = new File(DailyRewards.getPlugin().getDataFolder().getAbsolutePath() + "/data/dailyRewards.json");
        if (file.exists()){
            Reader reader = new FileReader(file);
            Type type = new TypeToken<Map<String,String>>() {}.getType();
            Map<String,String> encodedObject = gson.fromJson(reader,type);
            for(Map.Entry<String,String> e : encodedObject.entrySet()){
                if(e.getValue() != null){
                    byte[] serializedObject = Base64.getDecoder().decode(e.getValue());
                    ItemStack[] items = ItemStack.deserializeItemsFromBytes(serializedObject);
                    rewards.put(e.getKey(), new ArrayList<ItemStack>(Arrays.asList(items)));
                    reader.close();
                }
            }
        }
    }
}
