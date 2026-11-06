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
}
