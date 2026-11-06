package com.ashbisoficial.stagelite.menu;

import com.ashbisoficial.stagelite.registry.StageLiteBlocks;
import com.ashbisoficial.stagelite.registry.StageLiteMenus;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;

public class MovingHeadMenu extends AbstractContainerMenu {

    private final BlockPos pos;
    private final ContainerLevelAccess access;

    public MovingHeadMenu(int containerId, Inventory inventory, BlockPos pos) {
        super(StageLiteMenus.CABEZA_MOVIL.get(), containerId);
        this.pos = pos;
        this.access = ContainerLevelAccess.create(inventory.player.level(), pos);
    }

    public BlockPos getPos() {
        return pos;
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(access, player, StageLiteBlocks.CABEZA_MOVIL.get());
    }

    @Override
    public ItemStack quickMoveStack(Player player, int slotIndex) {
        return ItemStack.EMPTY;
    }
}
