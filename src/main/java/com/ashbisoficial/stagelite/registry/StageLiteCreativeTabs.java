package com.ashbisoficial.stagelite.registry;

import com.ashbisoficial.stagelite.StageLite;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class StageLiteCreativeTabs {

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, StageLite.MODID);

    // Icono y lista de items son temporales: se sustituyen en cuanto exista el Foco (Fase 1).
    public static final RegistryObject<CreativeModeTab> STAGELITE_TAB = CREATIVE_MODE_TABS.register(
            "stagelite_tab",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.stagelite"))
                    .icon(() -> new ItemStack(Items.GLOWSTONE))
                    .displayItems((parameters, output) -> {
                    })
                    .build()
    );
}
