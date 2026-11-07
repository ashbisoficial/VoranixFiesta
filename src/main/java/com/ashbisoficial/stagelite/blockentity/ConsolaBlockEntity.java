package com.ashbisoficial.stagelite.blockentity;

import com.ashbisoficial.stagelite.light.GroupManager;
import com.ashbisoficial.stagelite.light.Scene;
import com.ashbisoficial.stagelite.light.SceneManager;
import com.ashbisoficial.stagelite.light.Show;
import com.ashbisoficial.stagelite.light.ShowManager;
import com.ashbisoficial.stagelite.menu.ConsolaMenu;
import com.ashbisoficial.stagelite.registry.StageLiteBlockEntities;
import com.ashbisoficial.stagelite.registry.StageLiteBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

/**
 * La consola no guarda su propio "look" (eso lo tiene cada foco): guarda con que show
 * esta reproduciendo (si hay uno), que show queda "armado" para la proxima senal de
 * redstone, y el pulso de redstone de salida (SPEC.md 4.3.2/4.3.3). Necesita tick solo
 * para avanzar los pasos del show activo y para apagar el pulso de redstone a tiempo.
 */
public class ConsolaBlockEntity extends BlockEntity implements MenuProvider {

    private static final int PULSE_TICKS = 10; // medio segundo

    private String armedShowName = "";
    private boolean playing = false;
    private String playingShowName = "";
    private int currentStepIndex = -1;
    private long stepEndGameTime = 0L;
    private long pulseUntilGameTime = -1L;
    private int incomingRedstonePower = 0;

    public ConsolaBlockEntity(BlockPos pos, BlockState state) {
        super(StageLiteBlockEntities.CONSOLA.get(), pos, state);
    }

    public String getArmedShowName() {
        return armedShowName;
    }

    public boolean isPlaying() {
        return playing;
    }

    public String getPlayingShowName() {
        return playingShowName;
    }

    public int getRedstoneOutput() {
        return (level != null && pulseUntilGameTime >= 0 && level.getGameTime() < pulseUntilGameTime) ? 15 : 0;
    }

    public void startShow(String showName) {
        if (!(level instanceof ServerLevel serverLevel) || showName == null || showName.isBlank()) {
            return;
        }
        if (ShowManager.get(serverLevel).get(showName) == null) {
            return;
        }
        armedShowName = showName;
        playingShowName = showName;
        currentStepIndex = -1;
        stepEndGameTime = 0L;
        playing = true;
        setChanged();
    }

    public void stopShow() {
        playing = false;
        setChanged();
    }

    public void arm(String showName) {
        this.armedShowName = showName == null ? "" : showName;
        setChanged();
    }

    /** Lo llama ConsolaBlock.neighborChanged en el flanco de subida de la redstone entrante. */
    public void onIncomingRedstoneChanged(int newPower) {
        boolean rising = newPower > 0 && incomingRedstonePower <= 0;
        incomingRedstonePower = newPower;
        if (rising) {
            startShow(armedShowName);
        }
    }

    public void tickServer(ServerLevel level) {
        tickShow(level);
        tickPulse(level);
    }

    private void tickShow(ServerLevel level) {
        if (!playing) {
            return;
        }
        Show show = ShowManager.get(level).get(playingShowName);
        if (show == null) {
            stopShow();
            return;
        }
        if (currentStepIndex < 0 || level.getGameTime() >= stepEndGameTime) {
            if (!advanceToNextStep(level, show)) {
                stopShow();
            }
        }
    }

    private boolean advanceToNextStep(ServerLevel level, Show show) {
        List<Show.ShowStep> steps = show.getSteps();
        for (int attempt = 0; attempt < steps.size(); attempt++) {
            currentStepIndex++;
            if (currentStepIndex >= steps.size()) {
                if (!show.isLoop()) {
                    return false;
                }
                currentStepIndex = 0;
            }
            Show.ShowStep step = steps.get(currentStepIndex);
            if (!step.isEmpty()) {
                applyStep(level, step);
                stepEndGameTime = level.getGameTime() + step.getDurationTicks();
                return true;
            }
        }
        return false;
    }

    private void applyStep(ServerLevel level, Show.ShowStep step) {
        Scene scene = SceneManager.get(level).get(step.getSceneName());
        if (scene == null) {
            return;
        }
        GroupManager groups = GroupManager.get(level);
        var positions = groups.getPositions(scene.getGroup());
        int fadeDuration = Show.TRANSITION_FUNDIDO.equals(step.getTransition()) ? step.getDurationTicks() : 0;
        int size = positions.size();
        int index = 0;
        for (BlockPos pos : positions) {
            if (level.getBlockEntity(pos) instanceof AbstractLightBlockEntity foco) {
                foco.applyGroupLook(scene.getColor(), scene.getIntensity(), scene.getEffect(), scene.getSpeed(), index, size, fadeDuration);
            }
            index++;
        }
        pulse(level);
    }

    /** Lo llama LoadScenePacket cuando se carga una escena directamente (sin show de por medio). */
    public void pulseFromScene(ServerLevel level) {
        pulse(level);
    }

    /** Salida de redstone al activar una escena/paso (SPEC.md 4.3.2). */
    private void pulse(ServerLevel level) {
        pulseUntilGameTime = level.getGameTime() + PULSE_TICKS;
        level.updateNeighborsAt(getBlockPos(), StageLiteBlocks.CONSOLA.get());
        setChanged();
    }

    private void tickPulse(ServerLevel level) {
        if (pulseUntilGameTime >= 0 && level.getGameTime() >= pulseUntilGameTime) {
            pulseUntilGameTime = -1L;
            level.updateNeighborsAt(getBlockPos(), StageLiteBlocks.CONSOLA.get());
            setChanged();
        }
    }

    public static void tick(Level level, BlockPos pos, BlockState state, ConsolaBlockEntity entity) {
        if (!level.isClientSide && level instanceof ServerLevel serverLevel) {
            entity.tickServer(serverLevel);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putString("ArmedShow", armedShowName);
        tag.putBoolean("Playing", playing);
        tag.putString("PlayingShow", playingShowName);
        tag.putInt("CurrentStep", currentStepIndex);
        tag.putLong("StepEndGameTime", stepEndGameTime);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        armedShowName = tag.getString("ArmedShow");
        playing = tag.getBoolean("Playing");
        playingShowName = tag.getString("PlayingShow");
        currentStepIndex = tag.getInt("CurrentStep");
        stepEndGameTime = tag.getLong("StepEndGameTime");
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.stagelite.consola");
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return new ConsolaMenu(containerId, inventory, getBlockPos(), List.of(), List.of(), List.of());
        }
        List<String> groups = GroupManager.get(serverLevel).getGroupNames();
        List<String> scenes = SceneManager.get(serverLevel).getSceneNames();
        List<String> shows = ShowManager.get(serverLevel).getShowNames();
        return new ConsolaMenu(containerId, inventory, getBlockPos(), groups, scenes, shows);
    }
}
