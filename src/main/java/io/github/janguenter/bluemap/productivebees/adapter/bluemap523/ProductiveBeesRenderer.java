/* SPDX-License-Identifier: MIT */

package io.github.janguenter.bluemap.productivebees.adapter.bluemap523;

import de.bluecolored.bluemap.core.map.TextureGallery;
import de.bluecolored.bluemap.core.map.hires.MaxCapacityReachedException;
import de.bluecolored.bluemap.core.map.hires.RenderSettings;
import de.bluecolored.bluemap.core.map.hires.TileModelView;
import de.bluecolored.bluemap.core.map.hires.block.BlockRenderer;
import de.bluecolored.bluemap.core.map.hires.block.BlockRendererType;
import de.bluecolored.bluemap.core.map.hires.block.ResourceModelRenderer;
import de.bluecolored.bluemap.core.resources.pack.resourcepack.ResourcePack;
import de.bluecolored.bluemap.core.resources.pack.resourcepack.blockstate.Variant;
import de.bluecolored.bluemap.core.util.Key;
import de.bluecolored.bluemap.core.util.math.Color;
import de.bluecolored.bluemap.core.world.BlockState;
import de.bluecolored.bluemap.core.world.block.BlockNeighborhood;
import io.github.janguenter.bluemap.productivebees.activation.AddonRuntime;

import java.util.IdentityHashMap;
import java.util.Map;

/** Restores the feeder's client-rendered slab while leaving every other host stock. */
final class ProductiveBeesRenderer implements BlockRenderer {

    private static final String FEEDER = "productivebees:feeder";
    private static final String HONEY = "productivebees:honey";
    private static final String SYNTHETIC_HONEY = "bluemap_productivebees:honey";
    private static final String DEFAULT_SLAB = "minecraft:smooth_stone_slab";
    private static final float HONEY_RED = 1F;
    private static final float HONEY_GREEN = 201F / 255F;
    private static final float HONEY_BLUE = 22F / 255F;
    private static final ThreadLocal<Boolean> STOCK_FALLBACK =
            ThreadLocal.withInitial(() -> Boolean.FALSE);

    private final ResourcePack resourcePack;
    private final TextureGallery textures;
    private final RenderSettings settings;
    private final AddonRuntime runtime;
    private final VariantRendererCatalog catalog;
    private final ResourceModelRenderer models;
    private final BlockRenderer liquid;
    private final Map<BlockRendererType, BlockRenderer> hosts = new IdentityHashMap<>();

    ProductiveBeesRenderer(
            ResourcePack resourcePack,
            TextureGallery textures,
            RenderSettings settings,
            AddonRuntime runtime,
            VariantRendererCatalog catalog
    ) {
        this.resourcePack = resourcePack;
        this.textures = textures;
        this.settings = settings;
        this.runtime = runtime;
        this.catalog = catalog;
        this.models = new ResourceModelRenderer(resourcePack, textures, settings);
        this.liquid = BlockRendererType.LIQUID.create(resourcePack, textures, settings);
    }

    @Override
    public void render(
            BlockNeighborhood block,
            Variant variant,
            TileModelView target,
            Color mapColor
    ) {
        int safeStart = target.getTileModel().size();
        try {
            String blockId = block.getBlockState().getId().getFormatted();
            if (runtime.active()) {
                if (FEEDER.equals(blockId) && renderFeeder(block, target, mapColor)) {
                    return;
                }
                if (HONEY.equals(blockId) || SYNTHETIC_HONEY.equals(blockId)) {
                    renderHoney(block, variant, target, mapColor);
                    return;
                }
            }
            stock(block, variant, target, mapColor);
        } catch (MaxCapacityReachedException exception) {
            throw exception;
        } catch (RuntimeException exception) {
            target.getTileModel().reset(safeStart);
            stockSafely(block, variant, target, mapColor, safeStart);
        }
    }

    private void renderHoney(
            BlockNeighborhood block,
            Variant variant,
            TileModelView target,
            Color mapColor
    ) {
        int start = target.getTileModel().size();
        liquid.render(block, variant, target, mapColor);
        int end = target.getTileModel().size();
        for (int face = start; face < end; face++) {
            target.getTileModel().setColor(
                    face, HONEY_RED, HONEY_GREEN, HONEY_BLUE
            );
        }
    }

    private boolean renderFeeder(
            BlockNeighborhood block,
            TileModelView target,
            Color mapColor
    ) {
        String selected = DEFAULT_SLAB;
        if (block.getBlockEntity() instanceof FeederBlockEntityData data
                && validId(data.baseBlock())) {
            selected = data.baseBlock();
        }
        String type = switch (block.getBlockState().getProperties().get("type")) {
            case "top" -> "top";
            case "double" -> "double";
            default -> "bottom";
        };
        if (renderSlab(selected, type, block, target, mapColor)) {
            return true;
        }
        return !DEFAULT_SLAB.equals(selected)
                && renderSlab(DEFAULT_SLAB, type, block, target, mapColor);
    }

    private boolean renderSlab(
            String blockId,
            String type,
            BlockNeighborhood block,
            TileModelView target,
            Color mapColor
    ) {
        BlockState slab;
        try {
            slab = new BlockState(
                    Key.parse(blockId),
                    Map.of("type", type, "waterlogged", "false")
            );
        } catch (IllegalArgumentException exception) {
            return false;
        }
        var state = resourcePack.getBlockState(slab);
        if (state == null) {
            return false;
        }
        int start = target.getTileModel().size();
        state.forEach(
                slab,
                block.getX(),
                block.getY(),
                block.getZ(),
                selected -> models.render(block, selected, target, mapColor)
        );
        return target.getTileModel().size() > start;
    }

    private void stock(
            BlockNeighborhood block,
            Variant variant,
            TileModelView target,
            Color mapColor
    ) {
        if (STOCK_FALLBACK.get()) {
            return;
        }
        STOCK_FALLBACK.set(Boolean.TRUE);
        try {
            BlockRendererType type = catalog == null
                    ? BlockRendererType.DEFAULT : catalog.original(variant);
            hosts.computeIfAbsent(
                    type, found -> found.create(resourcePack, textures, settings)
            ).render(block, variant, target, mapColor);
        } finally {
            STOCK_FALLBACK.set(Boolean.FALSE);
        }
    }

    private void stockSafely(
            BlockNeighborhood block,
            Variant variant,
            TileModelView target,
            Color mapColor,
            int safeStart
    ) {
        try {
            stock(block, variant, target, mapColor);
        } catch (RuntimeException exception) {
            target.getTileModel().reset(safeStart);
        }
    }

    private static boolean validId(String value) {
        return value != null && !value.isBlank() && value.length() <= 256;
    }
}
