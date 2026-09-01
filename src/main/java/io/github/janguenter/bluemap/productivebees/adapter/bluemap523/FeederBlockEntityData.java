/* SPDX-License-Identifier: MIT */

package io.github.janguenter.bluemap.productivebees.adapter.bluemap523;

import de.bluecolored.bluemap.core.world.mca.blockentity.MCABlockEntity;
import de.bluecolored.bluenbt.NBTName;

/** Stable feeder projection: the client renderer reads only the selected slab registry ID. */
public final class FeederBlockEntityData extends MCABlockEntity {

    @NBTName("baseBlock")
    private String baseBlock;

    public FeederBlockEntityData() {
    }

    public String baseBlock() {
        return baseBlock;
    }
}
