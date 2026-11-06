package com.ashbisoficial.stagelite.menu;

import com.ashbisoficial.stagelite.registry.StageLiteBlocks;
import com.ashbisoficial.stagelite.registry.StageLiteMenus;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class ConsolaMenu extends AbstractContainerMenu {

    private final BlockPos pos;
    private final List<String> groupNames;
    private final ContainerLevelAccess access;

    public ConsolaMenu(int containerId, Inventory inventory, BlockPos pos, List<String> groupNames) {
        super(StageLiteMenus.CONSOLA.get(), containerId);
        this.pos = pos;
        this.groupNames = groupNames;
        this.access = ContainerLevelAccess.create(inventory.player.level(), pos);
    }

    public BlockPos getPos() {
        return pos;
    }

    public List<String> getGroupNames() {
        return groupNames;
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(access, player, StageLiteBlocks.CONSOLA.get());
    }

    @Override
    public ItemStack quickMoveStack(Player player, int slotIndex) {
        return ItemStack.EMPTY;
    }
}
