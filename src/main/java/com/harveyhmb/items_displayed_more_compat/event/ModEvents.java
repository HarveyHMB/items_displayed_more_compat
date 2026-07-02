package com.harveyhmb.items_displayed_more_compat.event;

import com.harveyhmb.items_displayed_more_compat.ItemsDisplayedMoreCompat;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLevelEvents;

public class ModEvents {
    public static void registerServerEventHandlers() {
        ItemsDisplayedMoreCompat.LOGGER.info("Registering server event handlers");

        ServerLevelEvents.LOAD.register(new LoadServerWorldEvent());
    }
}
