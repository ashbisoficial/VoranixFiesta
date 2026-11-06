package com.ashbisoficial.stagelite.block;

import com.ashbisoficial.stagelite.blockentity.FocoBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class FocoBlock extends AbstractLightBlock {

    public FocoBlock(Properties properties) {
        super(properties);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new FocoBlockEntity(pos, state);
    }
}
