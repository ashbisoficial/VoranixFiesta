package com.ashbisoficial.stagelite.blockentity;

import com.ashbisoficial.stagelite.menu.FocoMenu;
import com.ashbisoficial.stagelite.registry.StageLiteBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;

public class FocoBlockEntity extends AbstractLightBlockEntity {

    public FocoBlockEntity(BlockPos pos, BlockState state) {
        super(StageLiteBlockEntities.FOCO.get(), pos, state);
    }

    @Override
    protected String getContainerTranslationKey() {
        return "container.stagelite.foco";
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
        return new FocoMenu(containerId, inventory, getBlockPos());
    }
}
