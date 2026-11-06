package com.ashbisoficial.stagelite.registry;

import com.ashbisoficial.stagelite.StageLite;
import com.ashbisoficial.stagelite.blockentity.ConsolaBlockEntity;
import com.ashbisoficial.stagelite.blockentity.EstroboBlockEntity;
import com.ashbisoficial.stagelite.blockentity.FocoBlockEntity;
import com.ashbisoficial.stagelite.blockentity.MovingHeadBlockEntity;
import com.ashbisoficial.stagelite.blockentity.WashBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class StageLiteBlockEntities {

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, StageLite.MODID);

    public static final RegistryObject<BlockEntityType<FocoBlockEntity>> FOCO = BLOCK_ENTITIES.register(
            "foco",
            () -> BlockEntityType.Builder.of(FocoBlockEntity::new, StageLiteBlocks.FOCO.get()).build(null)
    );

    public static final RegistryObject<BlockEntityType<ConsolaBlockEntity>> CONSOLA = BLOCK_ENTITIES.register(
            "consola",
            () -> BlockEntityType.Builder.of(ConsolaBlockEntity::new, StageLiteBlocks.CONSOLA.get()).build(null)
    );

    public static final RegistryObject<BlockEntityType<WashBlockEntity>> PANEL = BLOCK_ENTITIES.register(
            "panel",
            () -> BlockEntityType.Builder.of(WashBlockEntity::new, StageLiteBlocks.PANEL.get()).build(null)
    );

    public static final RegistryObject<BlockEntityType<EstroboBlockEntity>> ESTROBO = BLOCK_ENTITIES.register(
            "estrobo",
            () -> BlockEntityType.Builder.of(EstroboBlockEntity::new, StageLiteBlocks.ESTROBO.get()).build(null)
    );

    public static final RegistryObject<BlockEntityType<MovingHeadBlockEntity>> CABEZA_MOVIL = BLOCK_ENTITIES.register(
            "cabeza_movil",
            () -> BlockEntityType.Builder.of(MovingHeadBlockEntity::new, StageLiteBlocks.CABEZA_MOVIL.get()).build(null)
    );
}
