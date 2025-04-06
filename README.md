# DailyRewards
A rank based daily rewards plugin for minecraft!

# How to use
## Installing
1. Install [LuckPerms](https://download.luckperms.net/1575/bukkit/loader/LuckPerms-Bukkit-5.4.158.jar) by clicking the link.
2. Install [DailyRewards](https://github.com/BlockMaster047/DailyRewards/releases/download/release/dailyrewards-1.0-SNAPSHOT.jar) by clicking the link.
3. Add both of them to your server and start it.

## Adding rewards
1. Use the command `/lp creategroup <name>` with the name being the name of your rank to create the rank with LuckPerms.
2. Get the items you want to add to the reward in your inventory and remove everything else.
3. Use the command `/daily add <name>` with the name being the **SAME** as what you did for the previous command to create the reward.

## Removing Rewards
- Use the command `/daily remove <name>` with the name being the name of the reward you want to remove to delete the reward

## Editing rewards
### Adding Items
1. Hold the item you want to add to the reward in your main hand
2. Use the command `/daily edit add <name>` with the name being the name of the reward you want to edit to add the item to the reward.
### Removing Items
1. Hold the item you want to be removed in your main hand
2. Use the command `/daily edit remove <name>` with the name being the name of the reward you want to edit to remove the item from the reward.

## Claiming rewards
- In order to claim the reward you need to have the LuckPerms rank.  You can give someone the rank with `/lp user <username> parent set <reward name>` with the username being the players username and the reward name being the reward name.
- If you have the correct rank you can use the command `/daily claim <reward name>` to claim it with the reward name being the reward name.

## Other Commands
- `/daily list` -> Lists all of the current rewards
- `/daily version` -> Displays the current version
- `/daily help` -> Displays information about all of the commands
