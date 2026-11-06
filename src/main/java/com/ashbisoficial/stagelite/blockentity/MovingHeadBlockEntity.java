package com.ashbisoficial.stagelite.blockentity;

import com.ashbisoficial.stagelite.menu.MovingHeadMenu;
import com.ashbisoficial.stagelite.registry.StageLiteBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Cabeza movil (SPEC.md 4.1.3): como el foco, pero el haz se mueve solo siguiendo un
 * patron (barrido o circulo), ademas de poder quedarse fijo. Simplificacion deliberada:
 * el modelo del bloque no tiene partes que giren; lo que se mueve es la direccion del
 * haz (ver MovingHeadBlockEntityRenderer). Apuntar a una coordenada fija concreta queda
 * fuera de alcance por ahora (necesitaria pedir una posicion en la pantalla).
 */
public class MovingHeadBlockEntity extends AbstractLightBlockEntity {

    public static final String PATTERN_FIJO = "fijo";
    public static final String PATTERN_BARRIDO = "barrido";
    public static final String PATTERN_CIRCULO = "circulo";

    private String pattern = PATTERN_BARRIDO;

    public MovingHeadBlockEntity(BlockPos pos, BlockState state) {
        super(StageLiteBlockEntities.CABEZA_MOVIL.get(), pos, state);
    }

    public String getPattern() {
        return pattern;
    }

    public void applyConfigFromClient(int color, int intensity, String group, String pattern) {
        this.pattern = pattern;
        super.applyConfigFromClient(color, intensity, group);
    }

    @Override
    protected String getContainerTranslationKey() {
        return "container.stagelite.cabeza_movil";
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putString("Pattern", pattern);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        pattern = tag.contains("Pattern") ? tag.getString("Pattern") : PATTERN_BARRIDO;
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
        return new MovingHeadMenu(containerId, inventory, getBlockPos());
    }
}
