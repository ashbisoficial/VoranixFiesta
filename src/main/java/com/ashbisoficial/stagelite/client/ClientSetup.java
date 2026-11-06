package com.ashbisoficial.stagelite.client;

import com.ashbisoficial.stagelite.StageLite;
import com.ashbisoficial.stagelite.client.render.FocoBlockEntityRenderer;
import com.ashbisoficial.stagelite.client.screen.ConsolaScreen;
import com.ashbisoficial.stagelite.client.screen.FocoScreen;
import com.ashbisoficial.stagelite.registry.StageLiteBlockEntities;
import com.ashbisoficial.stagelite.registry.StageLiteMenus;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

/**
 * Forge solo carga esta clase en el cliente fisico (value = Dist.CLIENT), asi que es seguro
 * referenciar aqui clases de renderizado/GUI que no existen en un servidor dedicado.
 */
@Mod.EventBusSubscriber(modid = StageLite.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientSetup {

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        MenuScreens.register(StageLiteMenus.FOCO.get(), FocoScreen::new);
        MenuScreens.register(StageLiteMenus.CONSOLA.get(), ConsolaScreen::new);
    }

    @SubscribeEvent
    public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(StageLiteBlockEntities.FOCO.get(), FocoBlockEntityRenderer::new);
    }
}
