package com.ashbisoficial.stagelite.block;

import com.ashbisoficial.stagelite.blockentity.WashBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/** Panel LED: luz de area suave, sin haz marcado (SPEC.md 4.1.2). */
public class WashBlock extends AbstractLightBlock {

    public WashBlock(Properties properties) {
        super(properties);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new WashBlockEntity(pos, state);
    }
}
