package com.ashbisoficial.stagelite.block;

import com.ashbisoficial.stagelite.blockentity.AbstractLightBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;

/**
 * Base comun de todos los "focos" de StageLite que se orientan en las 6 direcciones y
 * reaccionan a redstone (foco normal, panel LED, estroboscopio). La cabeza movil usa su
 * propia pantalla y por eso no extiende esto (ver MovingHeadBlock).
 */
public abstract class AbstractLightBlock extends BaseEntityBlock {

    public static final DirectionProperty FACING = BlockStateProperties.FACING;
    public static final BooleanProperty LIT = BlockStateProperties.LIT;

    protected AbstractLightBlock(Properties properties) {
        super(properties.lightLevel(state -> state.getValue(LIT) ? 13 : 0));
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(LIT, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, LIT);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(FACING, context.getClickedFace());
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        // Ninguno de estos focos necesita tick: solo reaccionan a cambios de redstone.
        return null;
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean movedByPiston) {
        if (level.isClientSide) {
            return;
        }
        int power = level.getBestNeighborSignal(pos);
        if (level.getBlockEntity(pos) instanceof AbstractLightBlockEntity foco) {
            foco.onRedstonePowerChanged(power);
        }
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!level.isClientSide && level.getBlockEntity(pos) instanceof MenuProvider menuProvider
                && player instanceof ServerPlayer serverPlayer) {
            NetworkHooks.openScreen(serverPlayer, menuProvider, pos);
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (!level.isClientSide && state.getBlock() != newState.getBlock()
                && level.getBlockEntity(pos) instanceof AbstractLightBlockEntity foco) {
            foco.unregisterFromGroup();
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }
}
