package com.dailyrewards.commands;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.dailyrewards.DailyRewards;
import com.dailyrewards.data.dailyData;
import com.dailyrewards.data.dailyRewards;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import net.luckperms.api.query.QueryOptions;

public class daily implements TabExecutor {

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command,
            @NotNull String label, @NotNull String @NotNull [] args) {
        if (sender instanceof Player p) {
            List<String> validArgs = new ArrayList<>();
            if (args.length == 1) {
                List<String> arg = new ArrayList<>(List.of("claim"));
                if (p.hasPermission("dailyreward.edit")) {
                    arg.addAll(List.of("add", "remove", "edit", "list", "help", "version"));
                }
                StringUtil.copyPartialMatches(args[0], arg, validArgs);
                return validArgs;
            } else if (args.length == 2) {
                List<String> arg = new ArrayList<>();
                switch (args[0]) {
                    case "claim":
                    case "remove":
                        Map<String, List<ItemStack>> argMap = dailyRewards.getAllRewards();
                        for (Map.Entry<String, List<ItemStack>> e : argMap.entrySet()) {
                            if (e.getKey() != null) {
                                arg.add(e.getKey());
                            }
                        }
                        break;
                    case "edit":
                        arg = List.of("add", "remove");
                }
                StringUtil.copyPartialMatches(args[1],arg,validArgs);
                return validArgs;
            }else if(args.length == 3){
                if (args[0].equals("edit") && args[1].equals("remove")){
                    List<String> arg = new ArrayList<>();
                    Map<String, List<ItemStack>> argMap = dailyRewards.getAllRewards();
                        for (Map.Entry<String, List<ItemStack>> e : argMap.entrySet()) {
                            if (e.getKey() != null) {
                                arg.add(e.getKey());
                            }
                        }
                        StringUtil.copyPartialMatches(args[2],arg,validArgs);
                        return validArgs;
                }
            }
        }
        return List.of();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label,
            @NotNull String @NotNull [] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            if (args.length > 0) {
                switch (args[0]) {
                    case "claim":
                        if (args.length > 1) {
                            Map<String, List<ItemStack>> rewards = dailyRewards.getAllRewards();
                            if (rewards != null) {
                                for (Map.Entry<String, List<ItemStack>> e : rewards.entrySet()) {
                                    if (args[1].equals(e.getKey())) {
                                        LuckPerms lp = LuckPermsProvider.get();
                                        User user = lp.getUserManager().getUser(p.getUniqueId());
                                        if (user != null) {
                                            QueryOptions queryOptions = lp.getContextManager().getQueryOptions(user)
                                                    .orElse(QueryOptions.defaultContextualOptions());
                                            Collection<Group> groups = user.getInheritedGroups(queryOptions);
                                            if (groups.stream().anyMatch(group -> group.getName().equals(e.getKey()))) {
                                                if (dailyData.getClaim(p, e.getKey()) != null) {
                                                    if (System.currentTimeMillis() >= dailyData.getClaim(p, e.getKey())
                                                            + 86400000) {
                                                        List<ItemStack> items = e.getValue();
                                                        int es = 0;
                                                        for (ItemStack item : p.getInventory().getContents()) {
                                                            if (item == null || item.getType() == Material.AIR) {
                                                                es++;
                                                            }
                                                        }
                                                        if (items.size() <= es) {
                                                            for (ItemStack reward : items) {
                                                                if (reward != null) {
                                                                    p.getInventory().addItem(reward);
                                                                }
                                                            }
                                                            p.sendMessage("§aSuccessfully claimed this reward!");
                                                            try {
                                                                dailyData.addClaim(p, e.getKey());
                                                            } catch (IOException e1) {
                                                                e1.printStackTrace();
                                                            }
                                                        } else {
                                                            p.sendMessage(
                                                                    "§cThere is not enough room in your inventory!");
                                                        }
                                                    } else {
                                                        long timeRemaining = (dailyData.getClaim(p, e.getKey())
                                                                + 86400000) - System.currentTimeMillis();
                                                        long minutesRemaining = timeRemaining / 1000 / 60;
                                                        p.sendMessage("§cYou must wait " + minutesRemaining
                                                                + " minutes before using this again!");
                                                    }
                                                } else {
                                                    List<ItemStack> items = e.getValue();
                                                    int es = 0;
                                                    for (ItemStack item : p.getInventory().getContents()) {
                                                        if (item == null || item.getType() == Material.AIR) {
                                                            es++;
                                                        }
                                                    }
                                                    if (items.size() <= es) {
                                                        for (ItemStack reward : items) {
                                                            if (reward != null) {
                                                                p.getInventory().addItem(reward);
                                                            }
                                                        }
                                                        p.sendMessage("§aSuccessfully claimed this reward!");
                                                        try {
                                                            dailyData.addClaim(p, e.getKey());
                                                        } catch (IOException e1) {
                                                            e1.printStackTrace();
                                                        }
                                                    } else {
                                                        p.sendMessage(
                                                                "§cThere is not enough room in your inventory!");
                                                    }
                                                }
                                            } else {
                                                p.sendMessage(
                                                        "§cYou do not have the proper rank to claim this!");
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        break;
                    case "add":
                        if (p.hasPermission("dailyreward.edit")) {
                            if (args.length > 1) {
                                List<ItemStack> rewards = new ArrayList<>();
                                for (ItemStack item : p.getInventory().getContents()) {
                                    if (item != null) {
                                        rewards.add(item);
                                    }
                                }
                                try {
                                    dailyRewards.addReward(args[1], rewards);
                                } catch (IOException e) {
                                    Logger.getLogger(DailyRewards.getPlugin().getClass().getName())
                                            .warning("CANNOT ADD REWARD " + args[1] + ", " + rewards);
                                }
                                p.sendMessage("§aSuccessfully created daily reward " + args[1]);
                            } else {
                                p.sendMessage("§cPlease specify a reward name!");
                            }
                        } else {
                            p.sendMessage("§cYou do not have permission to do this!");
                        }
                        break;
                    case "remove":
                        if (p.hasPermission("dailyreward.edit")) {
                            if (args.length > 1) {
                                if (dailyRewards.hasReward(args[1])) {
                                    try {
                                        dailyRewards.removeReward(args[1]);
                                        p.sendMessage("§aSuccessfully removed reward " + args[1] + "!");
                                    } catch (IOException e) {
                                        Logger.getLogger(DailyRewards.getPlugin().getClass().getName())
                                                .warning("CANNOT REMOVE REWARD " + args[1]);
                                    }
                                }
                            } else {
                                p.sendMessage("§cPlease specify a reward name!");
                            }
                        } else {
                            p.sendMessage("§cYou do not have permission to do this!");
                        }
                        break;
                    case "edit":
                        if (args.length > 1) {
                            if (args.length > 2) {
                                switch (args[1]) {
                                    case "remove":
                                        if (p.getInventory().getItemInMainHand() != null && p.getInventory().getItemInMainHand().getType() != Material.AIR) {
                                            try {
                                                dailyRewards.removeItemFromReward(args[2],
                                                        p.getInventory().getItemInMainHand());
                                            } catch (IOException e) {
                                                Logger.getLogger(DailyRewards.getPlugin().getClass().getName())
                                                        .warning("CANNOT REMOVE REWARD ITEM" + args[2]
                                                                + p.getInventory().getItemInMainHand());
                                            }
                                            ItemStack item = p.getInventory().getItemInMainHand();
                                            p.sendMessage(
                                                    "§aSuccessfully removed "
                                                            + (item.getItemMeta().hasDisplayName()
                                                                    ? item.getItemMeta().getDisplayName()
                                                                    : item.getType().toString().toLowerCase()
                                                                            .replace("_", " "))
                                                            + " from rewards " + args[2]);
                                        } else {
                                            p.sendMessage("§cPlease select a valid item!");
                                        }
                                        break;
                                    case "add":
                                        if (p.getInventory().getItemInMainHand() != null && p.getInventory().getItemInMainHand().getType() != Material.AIR) {
                                            try {
                                                dailyRewards.addItemToReward(args[2],
                                                        p.getInventory().getItemInMainHand());
                                            } catch (IOException e) {
                                                Logger.getLogger(DailyRewards.getPlugin().getClass().getName())
                                                        .warning("CANNOT ADD REWARD ITEM" + args[2]
                                                                + p.getInventory().getItemInMainHand());
                                            }
                                            ItemStack item = p.getInventory().getItemInMainHand();
                                            p.sendMessage(
                                                    "§aSuccessfully added "
                                                            + (item.getItemMeta().hasDisplayName()
                                                                    ? item.getItemMeta().getDisplayName()
                                                                    : item.getType().toString().toLowerCase()
                                                                            .replace("_", " "))
                                                            + " to rewards " + args[2]);
                                        } else {
                                            p.sendMessage("§cPlease select a valid item!");
                                        }
                                        break;
                                }
                            } else {
                                p.sendMessage("§cPlease specify a name!");
                            }
                        } else {
                            p.sendMessage("§cPlease specify an action!");
                        }
                        break;
                    case "list":
                        Map<String, List<ItemStack>> rewards = dailyRewards.getAllRewards();
                        if (rewards != null) {
                            if (!rewards.isEmpty()) {
                                p.sendMessage("§9All rewards:");
                                for (Map.Entry<String, List<ItemStack>> e : rewards.entrySet()) {
                                    if (e.getKey() != null) {
                                        System.out.println(e.getValue());
                                        p.sendMessage("§b" + e.getKey() + ":");
                                        if (e.getValue() != null && !e.getValue().isEmpty()) {
                                            for (ItemStack item : e.getValue()) {
                                                if (item != null) {
                                                    p.sendMessage("§a" + (item.getItemMeta().hasDisplayName()
                                                            ? item.getItemMeta().getDisplayName()
                                                            : item.getType().toString().toLowerCase().replace("_",
                                                                    " ")));
                                                }
                                                p.sendMessage("----------------------------");
                                            }
                                        }
                                    }
                                }
                            } else {
                                p.sendMessage("§bThere are no rewards!");
                            }
                        }
                        break;
                    case "help":
                        p.sendMessage("§bAll commands:");
                        p.sendMessage("§a/daily claim <reward_name> -> Claims a daily reward");
                        p.sendMessage("----------------------------");
                        p.sendMessage(
                                "§a/daily add <reward_name (must also be the name of the luckperms group)> -> Adds a daily reward (uses all of the items in your inventory)");
                        p.sendMessage("----------------------------");
                        p.sendMessage("§a/daily remove <reward_name> -> Removes a daily reward");
                        p.sendMessage("----------------------------");
                        p.sendMessage(
                                "§a/daily edit add <reward_name> -> Adds an item to a daily reward (uses the item in your main hand)");
                        p.sendMessage("----------------------------");
                        p.sendMessage(
                                "§a/daily edit remove <reward_name> -> Removes an item from a daily reward (uses the item in your main hand)");
                        p.sendMessage("----------------------------");
                        p.sendMessage("§a/daily list -> Lists all current daily rewards and what they contian");
                        p.sendMessage("----------------------------");
                        p.sendMessage("§a/daily version -> Displays the version of this plugin");
                        p.sendMessage("----------------------------");
                        p.sendMessage("§a/daily help -> Displays info about all commands");
                        break;
                    case "version":
                        p.sendMessage("§aVersion:");
                        p.sendMessage(DailyRewards.getPlugin().getDescription().getVersion());
                        break;
                    default:
                        p.sendMessage("§cPlease specify an action!");
                        break;
                }
            }
        }
        return true;
    }
}
