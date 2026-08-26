/*
 * SPDX-License-Identifier: MIT
 */

package io.github.janguenter.bluemap.productivebees.adapter.bluemap522;

import de.bluecolored.bluemap.core.resources.pack.resourcepack.ResourcePack;
import de.bluecolored.bluemap.core.resources.pack.resourcepack.ResourcePackExtension;
import io.github.janguenter.bluemap.productivebees.activation.AddonRuntime;
import io.github.janguenter.bluemap.productivebees.profile.ExactArtifactDetector;
import io.github.janguenter.bluemap.productivebees.profile.ProductiveBees13135Profile;

import java.nio.file.Path;

/** Exact-artifact admission hook; family routing deliberately remains stock. */
final class ProfileResourceExtension implements ResourcePackExtension {

    private final ResourcePack resourcePack;
    private final AddonRuntime runtime;

    ProfileResourceExtension(ResourcePack resourcePack, AddonRuntime runtime) {
        this.resourcePack = resourcePack;
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

        // SCAFFOLD_NOT_IMPLEMENTED: validate installed resources, register the
        // family renderer, route only owned hosts, then call runtime.activate().
        if (resourcePack.getBlockStates() == null) {
            runtime.fail("resource-pack-unavailable");
            return;
        }
        runtime.inactive("family-renderer-not-implemented");
    }
}
