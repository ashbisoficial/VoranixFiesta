package com.ashbisoficial.stagelite.block;

import com.ashbisoficial.stagelite.blockentity.EstroboBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/** Estroboscopio: parpadeo con frecuencia configurable (SPEC.md 4.1.4). */
public class EstroboBlock extends AbstractLightBlock {

    public EstroboBlock(Properties properties) {
        super(properties);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new EstroboBlockEntity(pos, state);
    }
}
