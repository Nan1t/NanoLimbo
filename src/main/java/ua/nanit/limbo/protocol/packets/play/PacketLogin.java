/*
 * Copyright (C) 2020 Nan1t
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package ua.nanit.limbo.protocol.packets.play;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import ua.nanit.limbo.protocol.ByteMessage;
import ua.nanit.limbo.protocol.PacketOut;
import ua.nanit.limbo.protocol.registry.Version;
import ua.nanit.limbo.server.data.NamespacedKey;
import ua.nanit.limbo.world.VersionedDimension;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PacketLogin implements PacketOut {

    private int entityId;
    private boolean hardcore;
    private int gameMode = 2;
    private int previousGameMode = -1;
    private VersionedDimension dimension;
    private String levelType = "flat";
    private long seed;
    private int difficulty = 0;
    private int maxPlayers;
    private int viewDistance = 2;
    private int simulationDistance = 2;
    private boolean reducedDebugInfo;
    private boolean enableRespawnScreen;
    private boolean debug;
    private boolean flat;
    private boolean normalRespawn = true;
    private boolean limitedCrafting;
    private int portalCooldown;
    private int seaLevel;
    private boolean onlineMode;
    private boolean secureProfile;

    public void encode(@NonNull ByteMessage msg, @NonNull Version version) {
        msg.writeInt(this.entityId);

        if (version.moreOrEqual(Version.V1_16_2)) {
            msg.writeBoolean(this.hardcore);
        }
        if (version.less(Version.V1_20_2)) {
            if (version.lessOrEqual(Version.V1_7_6)) {
                msg.writeByte(this.gameMode == 3 ? 1 : this.gameMode);
            } else {
                msg.writeByte(this.gameMode);
            }
        }
        if (version.moreOrEqual(Version.V1_16)) {
            if (version.less(Version.V1_20_2)) {
                msg.writeByte(this.previousGameMode);
            }

            msg.writeNamespacedKeyArray(new NamespacedKey[]{this.dimension.getKey()});

            if (version.less(Version.V1_20_2)) {
                msg.writeCompoundTag(this.dimension.getCodec(version), version);
            }
        }

        if (version.moreOrEqual(Version.V1_16)) {
            if (version.moreOrEqual(Version.V1_16_2) && version.less(Version.V1_19)) {
                msg.writeCompoundTag(this.dimension.getDefaultCodec(version), version);
            } else if (version.less(Version.V1_20_2)) {
                msg.writeNamespacedKey(this.dimension.getKey());
            }
            if (version.less(Version.V1_20_2)) {
                msg.writeNamespacedKey(this.dimension.getKey());
            }
        } else if (version.moreOrEqual(Version.V1_9)) {
            msg.writeInt(this.dimension.getLegacyDimensionId());
        } else {
            msg.writeByte(this.dimension.getLegacyDimensionId());
        }
        if (version.moreOrEqual(Version.V1_15)) {
            if (version.less(Version.V1_20_2)) {
                msg.writeLong(this.seed);
            }
        }
        if (version.less(Version.V1_14)) {
            msg.writeByte(this.difficulty);
        }
        if (version.moreOrEqual(Version.V1_16_2)) {
            msg.writeVarInt(this.maxPlayers);
        } else {
            msg.writeByte(this.maxPlayers);
        }
        if (version.less(Version.V1_16)) {
            msg.writeString(this.levelType);
        }
        if (version.moreOrEqual(Version.V1_14)) {
            msg.writeVarInt(this.viewDistance);
        }
        if (version.moreOrEqual(Version.V1_18)) {
            msg.writeVarInt(this.simulationDistance);
        }
        if (version.moreOrEqual(Version.V1_8)) {
            msg.writeBoolean(this.reducedDebugInfo);
        }
        if (version.moreOrEqual(Version.V1_15)) {
            msg.writeBoolean(this.normalRespawn);
        }
        if (version.moreOrEqual(Version.V1_20_2)) {
            msg.writeBoolean(this.limitedCrafting);
            if (version.moreOrEqual(Version.V1_20_5)) {
                msg.writeVarInt(this.dimension.getId(version));
            } else {
                msg.writeNamespacedKey(this.dimension.getKey());
            }
            msg.writeNamespacedKey(this.dimension.getKey());
            msg.writeLong(this.seed);
            msg.writeByte(this.gameMode);
            msg.writeByte(this.previousGameMode);
        }
        if (version.moreOrEqual(Version.V1_16)) {
            msg.writeBoolean(this.debug);
            msg.writeBoolean(this.flat);
        }
        if (version.moreOrEqual(Version.V1_19)) {
            msg.writeBoolean(false);
        }
        if (version.moreOrEqual(Version.V1_20)) {
            msg.writeVarInt(this.portalCooldown);
        }
        if (version.moreOrEqual(Version.V1_21_2)) {
            msg.writeVarInt(this.seaLevel);
        }
        if (version.moreOrEqual(Version.V26_2)) {
            msg.writeBoolean(this.onlineMode);
        }
        if (version.moreOrEqual(Version.V1_20_5)) {
            msg.writeBoolean(this.secureProfile);
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }
}
