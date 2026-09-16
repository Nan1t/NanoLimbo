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

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import lombok.*;
import net.kyori.adventure.nbt.CompoundBinaryTag;
import net.kyori.adventure.nbt.LongArrayBinaryTag;
import ua.nanit.limbo.protocol.ByteMessage;
import ua.nanit.limbo.protocol.MetadataWriter;
import ua.nanit.limbo.protocol.PacketOut;
import ua.nanit.limbo.protocol.registry.Version;
import ua.nanit.limbo.world.VersionedDimension;

import java.util.BitSet;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PacketChunkWithLight implements PacketOut {

    private int x;
    private int z;
    private VersionedDimension dimension;

    @Override
    public void encode(@NonNull ByteMessage buf, @NonNull Version version) {
        buf.writeInt(this.x);
        buf.writeInt(this.z);

        writeHeightmaps(buf, version, Map.of(HeightMapType.MOTION_BLOCKING, new long[37]));
        writeBlocksData(buf, version);

        // Skip block entities
        buf.writeVarInt(0);

        writeLightData(buf, version);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }

    private void writeBlocksData(@NonNull ByteMessage buf, @NonNull Version version) {
        int sections = this.dimension.getChunkSections(version);
        SinglePaletteFactory singlePaletteFactory = new SinglePaletteFactory(0);
        ByteBuf section = createEmptySection(version, singlePaletteFactory, singlePaletteFactory);
        try {
            buf.writeVarInt(section.readableBytes() * sections);
            for (int i = 0; i < sections; i++) {
                buf.writeBytes(section.duplicate());
            }
        } finally {
            section.release();
        }
    }

    private static ByteBuf createEmptySection(@NonNull Version version,
                                              @NonNull PaletteFactory blocksPalette,
                                              @NonNull PaletteFactory biomesPalette) {
        ByteMessage buf = new ByteMessage(ByteBufAllocator.DEFAULT.heapBuffer());
        buf.writeShort(0); // no empty blocks count
        if (version.moreOrEqual(Version.V26_1)) {
            buf.writeShort(0); // fluid count
        }
        long[] storage = new long[0];
        writePalette(buf, version, blocksPalette, storage);
        writePalette(buf, version, biomesPalette, storage);
        return buf;
    }

    private static void writePalette(@NonNull ByteMessage buf,
                                     @NonNull Version version,
                                     @NonNull PaletteFactory paletteFactory,
                                     long[] storage) {
        buf.writeByte(paletteFactory.getId());
        paletteFactory.writeData(buf, version);
        if (version.moreOrEqual(Version.V1_21_5)) {
            for (long data : storage) {
                buf.writeLong(data);
            }
            return;
        }
        buf.writeVarInt(storage.length);
        for (long data : storage) {
            buf.writeLong(data);
        }
    }

    private void writeLightData(@NonNull ByteMessage buf, @NonNull Version version) {
        int sections = this.dimension.getChunkSections(version);
        BitSet emptyBlocksYMask = new BitSet(sections + 2);
        emptyBlocksYMask.set(0, sections + 2);

        buf.writeBitSet(null, version); // Sky y mask
        buf.writeBitSet(null, version); // Block y mask
        buf.writeBitSet(null, version); // Empty sky y mask
        buf.writeBitSet(emptyBlocksYMask, version); // Empty block y mask

        // Skip sky updates
        buf.writeVarInt(0);
        // Skip block updates
        buf.writeVarInt(0);
    }

    private static void writeHeightmaps(@NonNull ByteMessage buf,
                                        @NonNull Version version,
                                        @NonNull Map<HeightMapType, long[]> heightMaps) {
        if (version.moreOrEqual(Version.V1_21_5)) {
            buf.writeVarInt(heightMaps.size());
            for (Map.Entry<HeightMapType, long[]> entry : heightMaps.entrySet()) {
                buf.writeVarInt(entry.getKey().ordinal());
                long[] bitSet = entry.getValue();
                buf.writeVarInt(bitSet.length);
                for (long l : bitSet) {
                    buf.writeLong(l);
                }
            }
            return;
        }
        CompoundBinaryTag.Builder tagBuilder = CompoundBinaryTag.builder();
        for (Map.Entry<HeightMapType, long[]> entry : heightMaps.entrySet()) {
            tagBuilder.put(entry.getKey().name(), LongArrayBinaryTag.longArrayBinaryTag(entry.getValue()));
        }
        CompoundBinaryTag rootTag = CompoundBinaryTag.builder()
                .put("root", tagBuilder.build()).build();
        buf.writeCompoundTag(rootTag, version);
    }

    public enum HeightMapType {
        WORLD_SURFACE_WG,
        WORLD_SURFACE,
        OCEAN_FLOOR_WG,
        OCEAN_FLOOR,
        MOTION_BLOCKING,
        MOTION_BLOCKING_NO_LEAVES
    }

    public interface PaletteFactory extends MetadataWriter {

        int getId();

    }

    @RequiredArgsConstructor
    public static class SinglePaletteFactory implements PaletteFactory {

        private final int data;

        @Override
        public int getId() {
            return 0;
        }

        @Override
        public void writeData(@NonNull ByteMessage message, @NonNull Version version) {
            message.writeVarInt(this.data);
        }
    }

}
