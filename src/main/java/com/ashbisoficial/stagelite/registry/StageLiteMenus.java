package com.ashbisoficial.stagelite.registry;

import com.ashbisoficial.stagelite.StageLite;
import com.ashbisoficial.stagelite.menu.ConsolaMenu;
import com.ashbisoficial.stagelite.menu.FocoMenu;
import com.ashbisoficial.stagelite.menu.MovingHeadMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.List;

public class StageLiteMenus {

    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(ForgeRegistries.MENU_TYPES, StageLite.MODID);

    public static final RegistryObject<MenuType<FocoMenu>> FOCO = MENUS.register(
            "foco",
            () -> IForgeMenuType.create((windowId, inventory, data) -> new FocoMenu(windowId, inventory, data.readBlockPos()))
    );

    public static final RegistryObject<MenuType<ConsolaMenu>> CONSOLA = MENUS.register(
            "consola",
            () -> IForgeMenuType.create((windowId, inventory, data) -> {
                var pos = data.readBlockPos();
                List<String> groups = readStringList(data);
                List<String> scenes = readStringList(data);
                List<String> shows = readStringList(data);
                return new ConsolaMenu(windowId, inventory, pos, groups, scenes, shows);
            })
    );

    public static final RegistryObject<MenuType<MovingHeadMenu>> CABEZA_MOVIL = MENUS.register(
            "cabeza_movil",
            () -> IForgeMenuType.create((windowId, inventory, data) -> new MovingHeadMenu(windowId, inventory, data.readBlockPos()))
    );

    private static List<String> readStringList(net.minecraft.network.FriendlyByteBuf data) {
        int count = data.readVarInt();
        List<String> values = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            values.add(data.readUtf());
        }
        return values;
    }
}
