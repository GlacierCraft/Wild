package com.github.maheevil.wild;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class Wild extends JavaPlugin {

    public static HashMap<UUID,Integer> hashMap = new HashMap<>();
    public File dataFile;

    @Override
    public void onEnable() {
        Gson gson = new Gson();
        try {
            File file = Paths.get(this.getDataFolder() + "tp.json").toFile();
            FileReader fileReader = new FileReader(file);
            Type type = new TypeToken<Map<UUID, Integer>>(){}.getType();
            hashMap = gson.fromJson(fileReader,type);
            this.dataFile = file;
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        getCommand("wild").setExecutor(new WildCommand());
    }

    @Override
    public void onDisable() {
        Gson gson = new Gson();
        String json = gson.toJson(hashMap);
        try {
            FileWriter fileWriter = new FileWriter(dataFile);
            fileWriter.write(json);
            fileWriter.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
