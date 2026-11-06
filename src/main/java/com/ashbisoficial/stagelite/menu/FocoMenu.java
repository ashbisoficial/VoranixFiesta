package com.ashbisoficial.stagelite.menu;

import com.ashbisoficial.stagelite.registry.StageLiteBlocks;
import com.ashbisoficial.stagelite.registry.StageLiteMenus;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;

/**
 * Menu sin slots: la pantalla del foco no mueve items, solo edita su configuracion
 * (ver FocoScreen y SetFocoConfigPacket). La posicion del bloque es el unico dato que necesita.
 */
public class FocoMenu extends AbstractContainerMenu {

    private final BlockPos pos;
    private final ContainerLevelAccess access;

    public FocoMenu(int containerId, Inventory inventory, BlockPos pos) {
        super(StageLiteMenus.FOCO.get(), containerId);
        this.pos = pos;
        this.access = ContainerLevelAccess.create(inventory.player.level(), pos);
    }

    public BlockPos getPos() {
        return pos;
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(access, player, StageLiteBlocks.FOCO.get());
    }

    @Override
    public net.minecraft.world.item.ItemStack quickMoveStack(Player player, int slotIndex) {
        return net.minecraft.world.item.ItemStack.EMPTY;
    }
}
