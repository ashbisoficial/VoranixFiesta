package com.ashbisoficial.stagelite.blockentity;

import com.ashbisoficial.stagelite.menu.FocoMenu;
import com.ashbisoficial.stagelite.registry.StageLiteBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;

/** Panel LED / Wash: misma configuracion que el foco, pero sin haz marcado (ver WashBlockEntityRenderer). */
public class WashBlockEntity extends AbstractLightBlockEntity {

    public WashBlockEntity(BlockPos pos, BlockState state) {
        super(StageLiteBlockEntities.PANEL.get(), pos, state);
    }

    @Override
    protected String getContainerTranslationKey() {
        return "container.stagelite.panel";
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
        return new FocoMenu(containerId, inventory, getBlockPos());
    }
}
