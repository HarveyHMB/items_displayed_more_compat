package com.harveyhmb.items_displayed_more_compat.compat;

import com.harveyhmb.items_displayed_more_compat.ItemsDisplayedMoreCompat;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.freedinner.items_displayed.ItemsDisplayed;
import net.freedinner.items_displayed.block.ModBlocks;
import net.freedinner.items_displayed.block.custom.ArmorTrimBlock;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.geysermc.event.subscribe.Subscribe;
import org.geysermc.geyser.api.GeyserApi;
import org.geysermc.geyser.api.block.custom.CustomBlockData;
import org.geysermc.geyser.api.block.custom.CustomBlockPermutation;
import org.geysermc.geyser.api.block.custom.CustomBlockState;
import org.geysermc.geyser.api.block.custom.component.*;
import org.geysermc.geyser.api.block.custom.nonvanilla.JavaBlockState;
import org.geysermc.geyser.api.block.custom.nonvanilla.JavaBoundingBox;
import org.geysermc.geyser.api.event.EventRegistrar;
import org.geysermc.geyser.api.event.lifecycle.GeyserDefineCustomBlocksEvent;
import org.geysermc.geyser.api.block.custom.NonVanillaCustomBlockData;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

public class GeyserCompat implements EventRegistrar {
    private static final List<Block> ITEMS_DISPLAYED_TRIMS = List.of(
            ModBlocks.BOLT_ARMOR_TRIM,
            ModBlocks.COAST_ARMOR_TRIM,
            ModBlocks.DUNE_ARMOR_TRIM,
            ModBlocks.EYE_ARMOR_TRIM,
            ModBlocks.FLOW_ARMOR_TRIM,
            ModBlocks.HOST_ARMOR_TRIM,
            ModBlocks.RAISER_ARMOR_TRIM,
            ModBlocks.RIB_ARMOR_TRIM,
            ModBlocks.SENTRY_ARMOR_TRIM,
            ModBlocks.SHAPER_ARMOR_TRIM,
            ModBlocks.SILENCE_ARMOR_TRIM,
            ModBlocks.SNOUT_ARMOR_TRIM,
            ModBlocks.SPIRE_ARMOR_TRIM,
            ModBlocks.TIDE_ARMOR_TRIM,
            ModBlocks.VEX_ARMOR_TRIM,
            ModBlocks.WARD_ARMOR_TRIM,
            ModBlocks.WAYFINDER_ARMOR_TRIM,
            ModBlocks.WILD_ARMOR_TRIM
    );

    private List<CustomBlockPermutation> createFacingPermutations() {
        List<CustomBlockPermutation> permutations = new ArrayList<>();

        Map<String, Integer> rotationsByFacing = Map.of(
                "north", 0,
                "east", 90,
                "south", 180,
                "west", 270
        );

        for (Map.Entry<String, Integer> entry : rotationsByFacing.entrySet()) {
            CustomBlockComponents rotated = CustomBlockComponents.builder()
                    .transformation(new TransformationComponent(0, entry.getValue(), 0))
                    .build();

            String condition = String.format("query.block_property('FACING') == '%s'", entry.getKey());
            permutations.add(new CustomBlockPermutation(rotated, condition));
        }

        return permutations;
    }

    private void defBlock(Block block, BoxComponent hitbox, JavaBoundingBox northSouthCollision, JavaBoundingBox eastWestCollision, GeyserDefineCustomBlocksEvent event)
    {
        Identifier javaIdentifier = BuiltInRegistries.BLOCK.getKey(block);
        CustomBlockComponents components = CustomBlockComponents.builder()
                .collisionBoxes(hitbox)
                .selectionBox(hitbox)
                .geometry(GeometryComponent.builder()
                        .identifier("geometry.items_displayed_more_compat." + javaIdentifier.getPath())
                        .build())
                .materialInstance("*", MaterialInstance.builder()
                        .texture(javaIdentifier.getPath())
                        .renderMethod("opaque")
                        .build())
                .destructibleByMining(0.5f)
                .build();

        NonVanillaCustomBlockData blockData = NonVanillaCustomBlockData.builder()
                .namespace(javaIdentifier.getNamespace())
                .name(javaIdentifier.getPath())
                .stringProperty("FACING", List.of("north", "south", "east", "west"))
                .components(components)
                .permutations(createFacingPermutations())
                .build();

        event.register(blockData);

        int groupId = Block.BLOCK_STATE_REGISTRY.getId(block.defaultBlockState()); // shared across all facings

        for (String facing : List.of("north", "south", "east", "west")) {
            BlockState state = block.defaultBlockState().setValue(ArmorTrimBlock.FACING, Direction.valueOf(facing.toUpperCase()));
            int stateId = Block.BLOCK_STATE_REGISTRY.getId(state); // verify this call in your IDE

            JavaBoundingBox collision = (facing.equals("north") || facing.equals("south"))
                    ? northSouthCollision
                    : eastWestCollision;

            JavaBlockState javaState = JavaBlockState.builder()
                    .identifier(javaIdentifier + "[facing=" + facing + "]")
                    .javaId(stateId)
                    .stateGroupId(groupId) // or a shared group id, see below
                    .blockHardness(0.5f)
                    .canBreakWithHand(true)
                    .collision(new JavaBoundingBox[]{ collision })
                    .build();

            CustomBlockState bedrockState = blockData.blockStateBuilder()
                    .stringProperty("FACING", facing)
                    .build();

            event.registerOverride(javaState, bedrockState);
        }
    }

    @Subscribe
    public void onDefineCustomBlocks(GeyserDefineCustomBlocksEvent event) {
        JavaBoundingBox trimNorthSouthCollision = new JavaBoundingBox(0.5, 0.125, 0.5, 0.5, 0.25, 0.75);
        JavaBoundingBox trimEastWestCollision = new JavaBoundingBox(0.5, 0.125, 0.5, 0.75, 0.25, 0.5);
        BoxComponent trimBedrockCollision = new BoxComponent(-4, 0, -6, 8, 4, 12);

        List<Block> trims = new ArrayList<>(ITEMS_DISPLAYED_TRIMS);
        if (FabricLoader.getInstance().isModLoaded("tooltrims"))
        {
            trims.addAll(ToolTrimsModCompatBlocks.TRIMS);
        }
        if (FabricLoader.getInstance().isModLoaded("more_armor_trims"))
        {
            trims.addAll(MoreArmorTrimsModCompatBlocks.TRIMS);
        }
        for (Block trim : trims)
        {
            defBlock(trim, trimBedrockCollision, trimNorthSouthCollision, trimEastWestCollision, event);
        }
    }

    public static void registerEventsStatic()
    {
        ServerLifecycleEvents.SERVER_STARTING.register((server) -> {
            new GeyserCompat().registerEvents();
        });
    }

    public void registerEvents()
    {
        GeyserApi.api().eventBus().register(this, this);
    }
}
