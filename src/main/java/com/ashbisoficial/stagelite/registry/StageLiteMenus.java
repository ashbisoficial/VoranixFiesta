package com.ashbisoficial.stagelite.registry;

import com.ashbisoficial.stagelite.StageLite;
import com.ashbisoficial.stagelite.menu.FocoMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class StageLiteMenus {

    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(ForgeRegistries.MENU_TYPES, StageLite.MODID);

    public static final RegistryObject<MenuType<FocoMenu>> FOCO = MENUS.register(
            "foco",
            () -> IForgeMenuType.create((windowId, inventory, data) -> new FocoMenu(windowId, inventory, data.readBlockPos()))
    );
}
