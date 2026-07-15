package com.harveyhmb.items_displayed_more_compat.compat;

import com.harveyhmb.items_displayed_more_compat.ItemsDisplayedMoreCompat;
import net.freedinner.items_displayed.item.custom.DebugBlockItem;
import net.freedinner.items_displayed.util.ModTemplates;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Function;

public class ToolTrimsModCompatBlocks {
    public static final Block LINEAR_TOOL_TRIM = registerBlock("linear_tool_trim",
            key -> ModTemplates.defaultArmorTrimBlock(key, SoundType.GRAVEL));
    public static final Block FROST_TOOL_TRIM = registerBlock("frost_tool_trim",
            key -> ModTemplates.defaultArmorTrimBlock(key, SoundType.SNOW));
    public static final Block CHARGE_TOOL_TRIM = registerBlock("charge_tool_trim",
            key -> ModTemplates.defaultArmorTrimBlock(key, SoundType.DEEPSLATE));
    public static final Block TRACKS_TOOL_TRIM = registerBlock("tracks_tool_trim",
            key -> ModTemplates.defaultArmorTrimBlock(key, SoundType.STONE));

    public static final List<Block> TRIMS = List.of(
            LINEAR_TOOL_TRIM,
            FROST_TOOL_TRIM,
            CHARGE_TOOL_TRIM,
            TRACKS_TOOL_TRIM
    );

    private static Block registerBlock(String name, Function<ResourceKey<@NotNull Block>,Block> function){
        ResourceKey<@NotNull Block> key=ResourceKey.create(BuiltInRegistries.BLOCK.key(), ItemsDisplayedMoreCompat.id(name));
        Block block=function.apply(key);
        Block registeredBlock= Registry.register(BuiltInRegistries.BLOCK, ItemsDisplayedMoreCompat.id(name), block);
        registerDebugItem(name,registeredBlock);

        return registeredBlock;
    }

    private static void registerDebugItem(String name,Block block){
        DebugBlockItem debugBlockItem=new DebugBlockItem(
                block, new Item.Properties().setId(ResourceKey.create(BuiltInRegistries.ITEM.key(), ItemsDisplayedMoreCompat.id(name)))
        );

        Registry.register(BuiltInRegistries.ITEM, ItemsDisplayedMoreCompat.id(name), debugBlockItem);
    }

    public static void registerBlocks(){
        ItemsDisplayedMoreCompat.LOGGER.info("Registering blocks");
    }
}
