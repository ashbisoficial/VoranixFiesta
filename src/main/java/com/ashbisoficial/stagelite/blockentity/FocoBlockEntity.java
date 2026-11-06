package com.ashbisoficial.stagelite.blockentity;

import com.ashbisoficial.stagelite.light.GroupManager;
import com.ashbisoficial.stagelite.light.LightState;
import com.ashbisoficial.stagelite.menu.FocoMenu;
import com.ashbisoficial.stagelite.registry.StageLiteBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.nbt.CompoundTag;

import com.ashbisoficial.stagelite.block.FocoBlock;

public class FocoBlockEntity extends BlockEntity implements MenuProvider {

    private final LightState lightState = new LightState();
    private int redstonePower = 0;
    private boolean blackedOut = false;

    public FocoBlockEntity(BlockPos pos, BlockState state) {
        super(StageLiteBlockEntities.FOCO.get(), pos, state);
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

    /** Lo usa la consola para el "Blackout": apaga el haz sin tocar la redstone ni la configuracion. */
    public void setBlackedOut(boolean blackedOut) {
        if (this.blackedOut == blackedOut) {
            return;
        }
        boolean wasOn = isOn();
        this.blackedOut = blackedOut;
        boolean nowOn = isOn();
        if (wasOn != nowOn && level != null) {
            BlockState state = getBlockState();
            level.setBlock(getBlockPos(), state.setValue(FocoBlock.LIT, nowOn), Block.UPDATE_CLIENTS);
        }
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
        if (wasOn != nowOn && level != null) {
            BlockState state = getBlockState();
            level.setBlock(getBlockPos(), state.setValue(FocoBlock.LIT, nowOn), Block.UPDATE_CLIENTS);
        }
        setChanged();
        syncToClients();
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

    /** La consola (Fase 2) usa esto para aplicar un "look" a todo un grupo de una vez, sin tocarle el grupo. */
    public void applyGroupLook(int color, int intensity, String effect, float speed) {
        lightState.setColor(color);
        lightState.setIntensity(intensity);
        lightState.setEffect(effect);
        lightState.setSpeed(speed);
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

    /** Lo llama FocoBlock.onRemove justo antes de que el bloque desaparezca. */
    public void unregisterFromGroup() {
        if (level instanceof ServerLevel serverLevel) {
            GroupManager.get(serverLevel).unregister(lightState.getGroup(), getBlockPos());
        }
    }

    private void syncToClients() {
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
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        lightState.load(tag);
        redstonePower = tag.getInt("RedstonePower");
        blackedOut = tag.getBoolean("BlackedOut");
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
        return Component.translatable("container.stagelite.foco");
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
        return new FocoMenu(containerId, inventory, getBlockPos());
    }
}
