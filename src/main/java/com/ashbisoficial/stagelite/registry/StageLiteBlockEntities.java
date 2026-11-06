package com.ashbisoficial.stagelite.registry;

import com.ashbisoficial.stagelite.StageLite;
import com.ashbisoficial.stagelite.blockentity.ConsolaBlockEntity;
import com.ashbisoficial.stagelite.blockentity.FocoBlockEntity;
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
}
