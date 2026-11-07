package com.ashbisoficial.stagelite.block;

import com.ashbisoficial.stagelite.blockentity.ConsolaBlockEntity;
import com.ashbisoficial.stagelite.light.GroupManager;
import com.ashbisoficial.stagelite.light.SceneManager;
import com.ashbisoficial.stagelite.light.ShowManager;
import com.ashbisoficial.stagelite.registry.StageLiteBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
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
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;

import java.util.List;

public class ConsolaBlock extends BaseEntityBlock {

    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    public ConsolaBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ConsolaBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return createTickerHelper(type, StageLiteBlockEntities.CONSOLA.get(), ConsolaBlockEntity::tick);
    }

    @Override
    public boolean isSignalSource(BlockState state) {
        return true;
    }

    @Override
    public int getSignal(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return level.getBlockEntity(pos) instanceof ConsolaBlockEntity consola ? consola.getRedstoneOutput() : 0;
    }

    @Override
    public int getDirectSignal(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return getSignal(state, level, pos, direction);
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean movedByPiston) {
        if (level.isClientSide) {
            return;
        }
        int power = level.getBestNeighborSignal(pos);
        if (level.getBlockEntity(pos) instanceof ConsolaBlockEntity consola) {
            consola.onIncomingRedstoneChanged(power);
        }
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!level.isClientSide && level.getBlockEntity(pos) instanceof ConsolaBlockEntity consola
                && player instanceof ServerPlayer serverPlayer && level instanceof ServerLevel serverLevel) {
            List<String> groups = GroupManager.get(serverLevel).getGroupNames();
            List<String> scenes = SceneManager.get(serverLevel).getSceneNames();
            List<String> shows = ShowManager.get(serverLevel).getShowNames();
            NetworkHooks.openScreen(serverPlayer, consola, buf -> {
                buf.writeBlockPos(pos);
                writeStringList(buf, groups);
                writeStringList(buf, scenes);
                writeStringList(buf, shows);
            });
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    private static void writeStringList(net.minecraft.network.FriendlyByteBuf buf, List<String> values) {
        buf.writeVarInt(values.size());
        for (String value : values) {
            buf.writeUtf(value);
        }
    }
}
