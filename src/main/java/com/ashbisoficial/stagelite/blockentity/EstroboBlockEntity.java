package com.ashbisoficial.stagelite.blockentity;

import com.ashbisoficial.stagelite.light.Effects;
import com.ashbisoficial.stagelite.menu.FocoMenu;
import com.ashbisoficial.stagelite.registry.StageLiteBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Estroboscopio: como el foco, pero con el efecto "parpadeo" activado desde el principio
 * (SPEC.md 4.1.4, "parpadeo con frecuencia configurable" -> la velocidad se ajusta
 * agrupandolo y usando la consola, o tambien reacciona a los efectos de grupo).
 */
public class EstroboBlockEntity extends AbstractLightBlockEntity {

    public EstroboBlockEntity(BlockPos pos, BlockState state) {
        super(StageLiteBlockEntities.ESTROBO.get(), pos, state);
        getLightState().setEffect(Effects.PARPADEO);
    }

    @Override
    protected String getContainerTranslationKey() {
        return "container.stagelite.estrobo";
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
        return new FocoMenu(containerId, inventory, getBlockPos());
    }
}
