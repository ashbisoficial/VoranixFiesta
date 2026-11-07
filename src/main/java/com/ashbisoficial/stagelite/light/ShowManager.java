package com.ashbisoficial.stagelite.light;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Indice persistente (por dimension) de shows guardados, por nombre. */
public class ShowManager extends SavedData {

    private static final String DATA_NAME = "stagelite_shows";

    private final Map<String, Show> shows = new LinkedHashMap<>();

    public static ShowManager get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(ShowManager::load, ShowManager::new, DATA_NAME);
    }

    public void save(String name, Show show) {
        if (name == null || name.isBlank()) {
            return;
        }
        shows.put(name, show);
        setDirty();
    }

    public Show get(String name) {
        return shows.get(name);
    }

    public List<String> getShowNames() {
        return List.copyOf(shows.keySet());
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        ListTag list = new ListTag();
        for (Map.Entry<String, Show> entry : shows.entrySet()) {
            CompoundTag showTag = entry.getValue().save(new CompoundTag());
            showTag.putString("Name", entry.getKey());
            list.add(showTag);
        }
        tag.put("Shows", list);
        return tag;
    }

    public static ShowManager load(CompoundTag tag) {
        ShowManager manager = new ShowManager();
        ListTag list = tag.getList("Shows", Tag.TAG_COMPOUND);
        for (int i = 0; i < list.size(); i++) {
            CompoundTag showTag = list.getCompound(i);
            manager.shows.put(showTag.getString("Name"), Show.load(showTag));
        }
        return manager;
    }
}
