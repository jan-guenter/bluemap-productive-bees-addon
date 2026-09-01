/*
 * SPDX-License-Identifier: MIT
 */

package io.github.janguenter.bluemap.productivebees.adapter.bluemap523;

import de.bluecolored.bluemap.core.resources.pack.resourcepack.ResourcePack;
import de.bluecolored.bluemap.core.resources.pack.resourcepack.ResourcePackExtension;
import de.bluecolored.bluemap.core.map.hires.block.BlockRendererType;
import de.bluecolored.bluemap.core.util.Key;
import de.bluecolored.bluemap.core.world.BlockProperties;
import de.bluecolored.bluemap.core.world.BlockState;
import io.github.janguenter.bluemap.productivebees.activation.AddonRuntime;
import io.github.janguenter.bluemap.productivebees.profile.ExactArtifactDetector;
import io.github.janguenter.bluemap.productivebees.profile.ProductiveBees13135Profile;

import java.nio.file.Path;

import java.util.Map;
import java.util.WeakHashMap;

/** Exact-artifact admission, honey routing, and feeder renderer installation. */
final class ProfileResourceExtension implements ResourcePackExtension {

    private static final Key FEEDER = Key.parse("productivebees:feeder");
    private static final Key HONEY = Key.parse("productivebees:honey");
    private static final Key SYNTHETIC_HONEY = Key.parse("bluemap_productivebees:honey");
    private static final Map<ResourcePack, VariantRendererCatalog> CATALOGS =
            new WeakHashMap<>();

    private final ResourcePack resourcePack;
    private final BlockRendererType renderer;
    private final AddonRuntime runtime;

    ProfileResourceExtension(
            ResourcePack resourcePack,
            BlockRendererType renderer,
            AddonRuntime runtime
    ) {
        this.resourcePack = resourcePack;
        this.renderer = renderer;
        this.runtime = runtime;
    }

    @Override
    public void loadResources(Iterable<Path> roots) {
        if (Boolean.getBoolean("bluemap.productivebees.disabled")) {
            runtime.inactive("operator-disabled");
            return;
        }
        if (!ExactArtifactDetector.matchesAll(roots, ProductiveBees13135Profile.ARTIFACTS)) {
            runtime.inactive("exact-artifact-missing-or-duplicate");
            return;
        }

        if (resourcePack.getBlockStates().get(FEEDER) == null
                || resourcePack.getBlockStates().get(SYNTHETIC_HONEY) == null) {
            runtime.inactive("required-resource-missing");
            return;
        }
        runtime.activate();
    }

    @Override
    public Key getBlockStateKey(Key key) {
        return runtime.active() && HONEY.equals(key) ? SYNTHETIC_HONEY : key;
    }

    @Override
    public void getBlockProperties(BlockState state, BlockProperties.Builder builder) {
        if (runtime.active() && (FEEDER.equals(state.getId()) || HONEY.equals(state.getId()))) {
            builder.culling(false)
                    .occluding(false)
                    .cullingIdentical(false)
                    .randomOffset(false);
        }
    }

    @Override
    public void bake() {
        if (!runtime.active()) {
            return;
        }
        VariantRendererCatalog catalog = VariantRendererCatalog.wrap(
                resourcePack, FEEDER, renderer
        );
        if (catalog.size() == 0) {
            runtime.inactive("feeder-variant-missing");
            return;
        }
        synchronized (CATALOGS) {
            CATALOGS.put(resourcePack, catalog);
        }
        System.out.println("BlueMap Productive Bees add-on active: feeder and honey routes installed.");
    }

    static VariantRendererCatalog catalog(ResourcePack resourcePack) {
        synchronized (CATALOGS) {
            return CATALOGS.get(resourcePack);
        }
    }
}
