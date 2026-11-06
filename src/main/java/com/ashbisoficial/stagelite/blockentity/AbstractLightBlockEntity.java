package com.ashbisoficial.stagelite.blockentity;

import com.ashbisoficial.stagelite.light.GroupManager;
import com.ashbisoficial.stagelite.light.LightState;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

/**
 * Logica comun a todos los "focos" de StageLite (foco normal, panel LED, estroboscopio,
 * cabeza movil, ...): un LightState configurable, reaccion a redstone, el override de
 * Blackout, y el alta/baja en GroupManager. Lo unico que cada bloque concreto decide es
 * como se renderiza (ver los BlockEntityRenderer de cada uno).
 *
 * Todos comparten la propiedad de bloque BlockStateProperties.LIT (de ahi que esta clase
 * pueda tocarla directamente sin conocer el Block concreto).
 */
public abstract class AbstractLightBlockEntity extends BlockEntity implements MenuProvider {

    private final LightState lightState = new LightState();
    private int redstonePower = 0;
    private boolean blackedOut = false;
    private int groupIndex = 0;
    private int groupSize = 1;

    protected AbstractLightBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public LightState getLightState() {
        return lightState;
    }

    public int getRedstonePower() {
        return redstonePower;
    }

    /** Intensidad efectiva (0-100) tras aplicar la potencia de redstone (1-15). 0 si no hay senal. */
    public int getEffectiveIntensity() {
        if (redstonePower <= 0) {
            return 0;
        }
        int scaled = Math.round(lightState.getIntensity() * (redstonePower / 15.0f));
        return Math.max(1, scaled);
    }

    public boolean isOn() {
        return redstonePower > 0 && !blackedOut;
    }

    public boolean isBlackedOut() {
        return blackedOut;
    }

    public int getGroupIndex() {
        return groupIndex;
    }

    public int getGroupSize() {
        return groupSize;
    }

    /** Lo usa la consola para el "Blackout": apaga el haz sin tocar la redstone ni la configuracion. */
    public void setBlackedOut(boolean blackedOut) {
        if (this.blackedOut == blackedOut) {
            return;
        }
        boolean wasOn = isOn();
        this.blackedOut = blackedOut;
        boolean nowOn = isOn();
        updateLitState(wasOn, nowOn);
        setChanged();
        syncToClients();
    }

    public void onRedstonePowerChanged(int newPower) {
        if (newPower == this.redstonePower) {
            return;
        }
        boolean wasOn = isOn();
        this.redstonePower = newPower;
        boolean nowOn = isOn();
        updateLitState(wasOn, nowOn);
        setChanged();
        syncToClients();
    }

    private void updateLitState(boolean wasOn, boolean nowOn) {
        if (wasOn != nowOn && level != null) {
            BlockState state = getBlockState();
            level.setBlock(getBlockPos(), state.setValue(BlockStateProperties.LIT, nowOn), Block.UPDATE_CLIENTS);
        }
    }

    public void applyConfigFromClient(int color, int intensity, String group) {
        String oldGroup = lightState.getGroup();
        lightState.setColor(color);
        lightState.setIntensity(intensity);
        lightState.setGroup(group);
        if (!oldGroup.equals(lightState.getGroup())) {
            moveGroup(oldGroup, lightState.getGroup());
        }
        setChanged();
        syncToClients();
    }

    /**
     * La consola usa esto para aplicar un "look" a todo un grupo de una vez, sin tocarle el
     * grupo. groupIndex/groupSize son la posicion de este foco dentro del grupo en el momento
     * de aplicar el cambio: arcoiris y persecucion los usan para desfasarse unos de otros.
     */
    public void applyGroupLook(int color, int intensity, String effect, float speed, int groupIndex, int groupSize) {
        lightState.setColor(color);
        lightState.setIntensity(intensity);
        lightState.setEffect(effect);
        lightState.setSpeed(speed);
        this.groupIndex = groupIndex;
        this.groupSize = Math.max(1, groupSize);
        setChanged();
        syncToClients();
    }

    private void moveGroup(String oldGroup, String newGroup) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }
        GroupManager groups = GroupManager.get(serverLevel);
        groups.unregister(oldGroup, getBlockPos());
        groups.register(newGroup, getBlockPos());
    }

    /** Lo llama el Block.onRemove correspondiente justo antes de que el bloque desaparezca. */
    public void unregisterFromGroup() {
        if (level instanceof ServerLevel serverLevel) {
            GroupManager.get(serverLevel).unregister(lightState.getGroup(), getBlockPos());
        }
    }

    protected void syncToClients() {
        if (level != null && !level.isClientSide) {
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        lightState.save(tag);
        tag.putInt("RedstonePower", redstonePower);
        tag.putBoolean("BlackedOut", blackedOut);
        tag.putInt("GroupIndex", groupIndex);
        tag.putInt("GroupSize", groupSize);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        lightState.load(tag);
        redstonePower = tag.getInt("RedstonePower");
        blackedOut = tag.getBoolean("BlackedOut");
        groupIndex = tag.getInt("GroupIndex");
        groupSize = Math.max(1, tag.contains("GroupSize") ? tag.getInt("GroupSize") : 1);
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = new CompoundTag();
        saveAdditional(tag);
        return tag;
    }

    @Override
    public void handleUpdateTag(CompoundTag tag) {
        load(tag);
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable(getContainerTranslationKey());
    }

    /** P.ej. "container.stagelite.foco". */
    protected abstract String getContainerTranslationKey();
}
