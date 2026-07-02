package com.harveyhmb.items_displayed_more_compat;

import com.harveyhmb.items_displayed_more_compat.compat.ToolTrimsModCompatBlocks;
import com.harveyhmb.items_displayed_more_compat.event.ModEvents;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ItemsDisplayedMoreCompat implements ModInitializer {
    public static final String MOD_ID = "items_displayed_more_compat";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        if (FabricLoader.getInstance().isModLoaded("tooltrims"))
        {
            ToolTrimsModCompatBlocks.registerBlocks();
        }
        ModEvents.registerServerEventHandlers();
    }

    public static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(MOD_ID, path);
    }
}
