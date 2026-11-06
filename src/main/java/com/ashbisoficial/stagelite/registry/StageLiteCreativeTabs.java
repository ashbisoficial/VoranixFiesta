package com.ashbisoficial.stagelite.registry;

import com.ashbisoficial.stagelite.StageLite;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class StageLiteCreativeTabs {

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, StageLite.MODID);

    public static final RegistryObject<CreativeModeTab> STAGELITE_TAB = CREATIVE_MODE_TABS.register(
            "stagelite_tab",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.stagelite"))
                    .icon(() -> new ItemStack(StageLiteItems.FOCO.get()))
                    .displayItems((parameters, output) -> {
                        output.accept(StageLiteItems.FOCO.get());
                        output.accept(StageLiteItems.PANEL.get());
                        output.accept(StageLiteItems.ESTROBO.get());
                        output.accept(StageLiteItems.CABEZA_MOVIL.get());
                        output.accept(StageLiteItems.TRUSS.get());
                        output.accept(StageLiteItems.CONSOLA.get());
                    })
                    .build()
    );
}
