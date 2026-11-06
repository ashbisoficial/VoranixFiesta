package com.ashbisoficial.stagelite;

import com.ashbisoficial.stagelite.network.StageLiteNetwork;
import com.ashbisoficial.stagelite.registry.StageLiteBlockEntities;
import com.ashbisoficial.stagelite.registry.StageLiteBlocks;
import com.ashbisoficial.stagelite.registry.StageLiteCreativeTabs;
import com.ashbisoficial.stagelite.registry.StageLiteItems;
import com.ashbisoficial.stagelite.registry.StageLiteMenus;
import com.mojang.logging.LogUtils;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(StageLite.MODID)
public class StageLite {

    public static final String MODID = "stagelite";
    public static final Logger LOGGER = LogUtils.getLogger();

    public StageLite() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        StageLiteBlocks.BLOCKS.register(modEventBus);
        StageLiteItems.ITEMS.register(modEventBus);
        StageLiteBlockEntities.BLOCK_ENTITIES.register(modEventBus);
        StageLiteMenus.MENUS.register(modEventBus);
        StageLiteCreativeTabs.CREATIVE_MODE_TABS.register(modEventBus);

        modEventBus.addListener(this::commonSetup);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        StageLiteNetwork.register();
    }
}
