package com.ashbisoficial.stagelite.registry;

import com.ashbisoficial.stagelite.StageLite;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class StageLiteItems {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, StageLite.MODID);

    public static final RegistryObject<Item> FOCO = ITEMS.register(
            "foco",
            () -> new BlockItem(StageLiteBlocks.FOCO.get(), new Item.Properties())
    );

    public static final RegistryObject<Item> CONSOLA = ITEMS.register(
            "consola",
            () -> new BlockItem(StageLiteBlocks.CONSOLA.get(), new Item.Properties())
    );

    public static final RegistryObject<Item> PANEL = ITEMS.register(
            "panel",
            () -> new BlockItem(StageLiteBlocks.PANEL.get(), new Item.Properties())
    );

    public static final RegistryObject<Item> ESTROBO = ITEMS.register(
            "estrobo",
            () -> new BlockItem(StageLiteBlocks.ESTROBO.get(), new Item.Properties())
    );

    public static final RegistryObject<Item> CABEZA_MOVIL = ITEMS.register(
            "cabeza_movil",
            () -> new BlockItem(StageLiteBlocks.CABEZA_MOVIL.get(), new Item.Properties())
    );

    public static final RegistryObject<Item> TRUSS = ITEMS.register(
            "truss",
            () -> new BlockItem(StageLiteBlocks.TRUSS.get(), new Item.Properties())
    );
}
