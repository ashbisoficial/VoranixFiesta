package com.ashbisoficial.stagelite.blockentity;

import com.ashbisoficial.stagelite.menu.ConsolaMenu;
import com.ashbisoficial.stagelite.registry.StageLiteBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

/**
 * La consola no guarda su propio "look": solo es la puerta de entrada a los grupos
 * (ver GroupManager). No necesita tick ni sincronizacion de datos propios.
 */
public class ConsolaBlockEntity extends BlockEntity implements MenuProvider {

    public ConsolaBlockEntity(BlockPos pos, BlockState state) {
        super(StageLiteBlockEntities.CONSOLA.get(), pos, state);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.stagelite.consola");
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
        List<String> groups = level instanceof ServerLevel serverLevel
                ? com.ashbisoficial.stagelite.light.GroupManager.get(serverLevel).getGroupNames()
                : List.of();
        return new ConsolaMenu(containerId, inventory, getBlockPos(), groups);
    }
}
