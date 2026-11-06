package com.ashbisoficial.stagelite.light;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.LongTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Indice persistente (por dimension) de que focos pertenecen a que grupo, para que la
 * consola (Fase 2) pueda encontrarlos. No guarda el "look" del grupo: cada foco sigue
 * siendo dueno de su propio LightState (ver SPEC.md seccion 5).
 */
public class GroupManager extends SavedData {

    private static final String DATA_NAME = "stagelite_groups";

    private final Map<String, Set<BlockPos>> groups = new LinkedHashMap<>();

    public static GroupManager get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(GroupManager::load, GroupManager::new, DATA_NAME);
    }

    public void register(String group, BlockPos pos) {
        if (group == null || group.isBlank()) {
            return;
        }
        groups.computeIfAbsent(group, g -> new LinkedHashSet<>()).add(pos.immutable());
        setDirty();
    }

    public void unregister(String group, BlockPos pos) {
        if (group == null || group.isBlank()) {
            return;
        }
        Set<BlockPos> positions = groups.get(group);
        if (positions != null) {
            positions.remove(pos.immutable());
            if (positions.isEmpty()) {
                groups.remove(group);
            }
            setDirty();
        }
    }

    public Set<BlockPos> getPositions(String group) {
        return groups.getOrDefault(group, Set.of());
    }

    public List<String> getGroupNames() {
        return List.copyOf(groups.keySet());
    }

    /** Todas las posiciones conocidas, de cualquier grupo (para el Blackout/Estrobo general). */
    public Set<BlockPos> getAllPositions() {
        Set<BlockPos> all = new LinkedHashSet<>();
        for (Set<BlockPos> positions : groups.values()) {
            all.addAll(positions);
        }
        return all;
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        ListTag groupsList = new ListTag();
        for (Map.Entry<String, Set<BlockPos>> entry : groups.entrySet()) {
            CompoundTag groupTag = new CompoundTag();
            groupTag.putString("Name", entry.getKey());
            ListTag positionsList = new ListTag();
            for (BlockPos pos : entry.getValue()) {
                positionsList.add(LongTag.valueOf(pos.asLong()));
            }
            groupTag.put("Positions", positionsList);
            groupsList.add(groupTag);
        }
        tag.put("Groups", groupsList);
        return tag;
    }

    public static GroupManager load(CompoundTag tag) {
        GroupManager manager = new GroupManager();
        ListTag groupsList = tag.getList("Groups", Tag.TAG_COMPOUND);
        for (int i = 0; i < groupsList.size(); i++) {
            CompoundTag groupTag = groupsList.getCompound(i);
            String name = groupTag.getString("Name");
            ListTag positionsList = groupTag.getList("Positions", Tag.TAG_LONG);
            Set<BlockPos> positions = new LinkedHashSet<>();
            for (int j = 0; j < positionsList.size(); j++) {
                positions.add(BlockPos.of(((LongTag) positionsList.get(j)).getAsLong()));
            }
            if (!positions.isEmpty()) {
                manager.groups.put(name, positions);
            }
        }
        return manager;
    }
}
