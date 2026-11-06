package com.ashbisoficial.stagelite.registry;

import com.ashbisoficial.stagelite.StageLite;
import com.ashbisoficial.stagelite.block.ConsolaBlock;
import com.ashbisoficial.stagelite.block.EstroboBlock;
import com.ashbisoficial.stagelite.block.FocoBlock;
import com.ashbisoficial.stagelite.block.MovingHeadBlock;
import com.ashbisoficial.stagelite.block.TrussBlock;
import com.ashbisoficial.stagelite.block.WashBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class StageLiteBlocks {

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, StageLite.MODID);

    public static final RegistryObject<Block> FOCO = BLOCKS.register(
            "foco",
            () -> new FocoBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_BLACK)
                    .strength(2.0f, 6.0f)
                    .sound(SoundType.METAL)
                    .noOcclusion()
            )
    );

    public static final RegistryObject<Block> CONSOLA = BLOCKS.register(
            "consola",
            () -> new ConsolaBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_GRAY)
                    .strength(3.5f, 6.0f)
                    .sound(SoundType.METAL)
            )
    );

    public static final RegistryObject<Block> PANEL = BLOCKS.register(
            "panel",
            () -> new WashBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_BLACK)
                    .strength(1.5f, 6.0f)
                    .sound(SoundType.METAL)
                    .noOcclusion()
            )
    );

    public static final RegistryObject<Block> ESTROBO = BLOCKS.register(
            "estrobo",
            () -> new EstroboBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.SNOW)
                    .strength(2.0f, 6.0f)
                    .sound(SoundType.METAL)
                    .noOcclusion()
            )
    );

    public static final RegistryObject<Block> CABEZA_MOVIL = BLOCKS.register(
            "cabeza_movil",
            () -> new MovingHeadBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_BLACK)
                    .strength(2.5f, 6.0f)
                    .sound(SoundType.METAL)
                    .noOcclusion()
            )
    );

    public static final RegistryObject<Block> TRUSS = BLOCKS.register(
            "truss",
            () -> new TrussBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_GRAY)
                    .strength(3.0f, 6.0f)
                    .sound(SoundType.METAL)
            )
    );
}
