package com.ashbisoficial.stagelite.light;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Indice persistente (por dimension) de escenas guardadas, por nombre. */
public class SceneManager extends SavedData {

    private static final String DATA_NAME = "stagelite_scenes";

    private final Map<String, Scene> scenes = new LinkedHashMap<>();

    public static SceneManager get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(SceneManager::load, SceneManager::new, DATA_NAME);
    }

    public void save(String name, Scene scene) {
        if (name == null || name.isBlank()) {
            return;
        }
        scenes.put(name, scene);
        setDirty();
    }

    public Scene get(String name) {
        return scenes.get(name);
    }

    public List<String> getSceneNames() {
        return List.copyOf(scenes.keySet());
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        ListTag list = new ListTag();
        for (Map.Entry<String, Scene> entry : scenes.entrySet()) {
            CompoundTag sceneTag = entry.getValue().save(new CompoundTag());
            sceneTag.putString("Name", entry.getKey());
            list.add(sceneTag);
        }
        tag.put("Scenes", list);
        return tag;
    }

    public static SceneManager load(CompoundTag tag) {
        SceneManager manager = new SceneManager();
        ListTag list = tag.getList("Scenes", Tag.TAG_COMPOUND);
        for (int i = 0; i < list.size(); i++) {
            CompoundTag sceneTag = list.getCompound(i);
            manager.scenes.put(sceneTag.getString("Name"), Scene.load(sceneTag));
        }
        return manager;
    }
}
