package com.ashbisoficial.stagelite;

import com.ashbisoficial.stagelite.registry.StageLiteCreativeTabs;
import com.mojang.logging.LogUtils;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(StageLite.MODID)
public class StageLite {

    public static final String MODID = "stagelite";
    public static final Logger LOGGER = LogUtils.getLogger();

    public StageLite() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        StageLiteCreativeTabs.CREATIVE_MODE_TABS.register(modEventBus);
    }
}
