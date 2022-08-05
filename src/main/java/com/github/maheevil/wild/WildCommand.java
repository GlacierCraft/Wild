package com.github.maheevil.wild;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

public class WildCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(!(sender instanceof Player player)) {
            return false;
        }

        player.sendMessage("[WILD] - Finding a suitable location");

        var uuid = player.getUniqueId();
        if(Wild.hashMap.containsKey(uuid) && Wild.hashMap.get(uuid) >= 3){
            player.sendMessage("[WILD] - Maximum uses exceeded");
            return true;
        }

        var tp = teleportToWild(player);
        AtomicInteger countToAdd = new AtomicInteger();
        countToAdd.set(1);
        if(tp == null){
            player.sendMessage("[WILD] - Could not find a suitable location, try again");
            return true;
        }

        tp.exceptionally((x) -> {
            player.sendMessage("[WILD] - An error occured, Please");
            countToAdd.set(0);
            return null;
        });

        Wild.hashMap.merge(uuid,countToAdd.get(),(uuidKey, value) -> value + countToAdd.get());

        return true;
    }

    public CompletableFuture<Boolean> teleportToWild(Player player){
        Location locationToTp = getSafeLocation(player.getWorld().getSpawnLocation(), player.getWorld());
        return locationToTp != null ? player.teleportAsync(locationToTp, PlayerTeleportEvent.TeleportCause.PLUGIN) : null;
    }

    public Location getSafeLocation(Location spawn, World world){
        var rand = new Random();
        double spawnAreaX = spawn.getX() + 500;
        double spawnAreaZ = spawn.getZ() + 500;
        for (int i = 0; i < 3; i++) {
            var testX = spawnAreaX + rand.nextDouble(4001);
            var testZ = spawnAreaZ + rand.nextDouble(4001);
            var highestBlock = world.getHighestBlockAt((int) testX, (int) testZ);

            if(!highestBlock.isCollidable() || !highestBlock.isSolid()){
                continue;
            }

            return highestBlock.getLocation().add(0,2,0);
        }
        return null;
    }
}
