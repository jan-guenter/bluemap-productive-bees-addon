/*
 * SPDX-License-Identifier: MIT
 */

package io.github.janguenter.bluemap.productivebees.adapter.bluemap523;

import de.bluecolored.bluemap.core.map.TextureGallery;
import de.bluecolored.bluemap.core.map.hires.RenderSettings;
import de.bluecolored.bluemap.core.map.hires.block.BlockRenderer;
import de.bluecolored.bluemap.core.map.hires.block.BlockRendererType;
import de.bluecolored.bluemap.core.resources.pack.resourcepack.ResourcePack;
import de.bluecolored.bluemap.core.util.Key;
import de.bluecolored.bluemap.core.world.mca.blockentity.BlockEntityType;
import io.github.janguenter.bluemap.addon.adapter.api.bluemap523.RegistryGuard;
import io.github.janguenter.bluemap.addon.adapter.api.bluemap523.ResourceExtensionType;
import io.github.janguenter.bluemap.productivebees.activation.AddonRuntime;

/** BlueMap 5.23 feature-backport registration boundary. */
public final class BlueMap523Adapter {

    private static final AddonRuntime RUNTIME = AddonRuntime.INSTANCE;
    private static final BlockRendererType FEEDER_RENDERER = new BlockRendererType.Impl(
            Key.parse("bluemap_productivebees:feeder"),
            BlueMap523Adapter::createRenderer
    );
    private static final ResourcePack.Extension<ProfileResourceExtension> EXTENSION =
            new ResourceExtensionType<>(
                    Key.parse("bluemap_productivebees:exact_profile"),
                    pack -> new ProfileResourceExtension(pack, FEEDER_RENDERER, RUNTIME)
            );
    private static final BlockEntityType FEEDER_BLOCK_ENTITY = new BlockEntityType.Impl(
            Key.parse("productivebees:feeder"),
            FeederBlockEntityData.class
    );

    private BlueMap523Adapter() {
    }

    /** Registers the narrow feeder renderer, resource hook, and retained NBT projection. */
    public static synchronized boolean install() {
        if (!RegistryGuard.canRegister(BlockRendererType.REGISTRY, FEEDER_RENDERER)
                || !RegistryGuard.canRegister(ResourcePack.Extension.REGISTRY, EXTENSION)
                || !RegistryGuard.canRegister(BlockEntityType.REGISTRY, FEEDER_BLOCK_ENTITY)) {
            RUNTIME.fail("registry-collision");
            return false;
        }
        if (!RegistryGuard.register(BlockRendererType.REGISTRY, FEEDER_RENDERER)
                || !RegistryGuard.register(ResourcePack.Extension.REGISTRY, EXTENSION)
                || !RegistryGuard.register(BlockEntityType.REGISTRY, FEEDER_BLOCK_ENTITY)) {
            RUNTIME.fail("registry-registration-failed");
            return false;
        }
        return true;
    }

    private static BlockRenderer createRenderer(
            ResourcePack pack,
            TextureGallery gallery,
            RenderSettings settings
    ) {
        try {
            return new ProductiveBeesRenderer(
                    pack, gallery, settings, RUNTIME, ProfileResourceExtension.catalog(pack)
            );
        } catch (RuntimeException exception) {
            RUNTIME.inactive("renderer-construction-"
                    + exception.getClass().getSimpleName());
            return BlockRendererType.DEFAULT.create(pack, gallery, settings);
        }
    }
}
