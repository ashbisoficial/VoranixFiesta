package com.ashbisoficial.stagelite.block;

import com.ashbisoficial.stagelite.blockentity.MovingHeadBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/** Cabeza movil (SPEC.md 4.1.3). Reusa toda la logica de AbstractLightBlock. */
public class MovingHeadBlock extends AbstractLightBlock {

    public MovingHeadBlock(Properties properties) {
        super(properties);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new MovingHeadBlockEntity(pos, state);
    }
}
