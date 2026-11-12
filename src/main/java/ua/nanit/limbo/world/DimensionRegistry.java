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

package ua.nanit.limbo.world;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.nbt.*;
import org.checkerframework.checker.nullness.qual.Nullable;
import ua.nanit.limbo.protocol.MetadataWriter;
import ua.nanit.limbo.protocol.registry.Version;
import ua.nanit.limbo.server.LimboServer;
import ua.nanit.limbo.server.data.NamespacedKey;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.function.BiFunction;

@RequiredArgsConstructor
@Getter
public final class DimensionRegistry {

    private final LimboServer server;

    private CompoundBinaryTag codec_1_16;
    private CompoundBinaryTag codec_1_16_2;
    private CompoundBinaryTag codec_1_17;
    private CompoundBinaryTag codec_1_18_2;
    private CompoundBinaryTag codec_1_19;
    private CompoundBinaryTag codec_1_19_1;
    private CompoundBinaryTag codec_1_19_4;
    private CompoundBinaryTag codec_1_20;
    private CompoundBinaryTag codec_1_20_5;
    private CompoundBinaryTag codec_1_21;
    private CompoundBinaryTag codec_1_21_2;
    private CompoundBinaryTag codec_1_21_4;
    private CompoundBinaryTag codec_1_21_5;
    private CompoundBinaryTag codec_1_21_6;
    private CompoundBinaryTag codec_1_21_7;
    private CompoundBinaryTag codec_1_21_9;
    private CompoundBinaryTag codec_1_21_11;
    private CompoundBinaryTag codec_26_1;
    private CompoundBinaryTag codec_26_2;

    private CompoundBinaryTag tags_1_20_5;
    private CompoundBinaryTag tags_1_21;
    private CompoundBinaryTag tags_1_21_2;
    private CompoundBinaryTag tags_1_21_4;
    private CompoundBinaryTag tags_1_21_5;
    private CompoundBinaryTag tags_1_21_6;
    private CompoundBinaryTag tags_1_21_7;
    private CompoundBinaryTag tags_1_21_9;
    private CompoundBinaryTag tags_1_21_11;
    private CompoundBinaryTag tags_26_1;
    private CompoundBinaryTag tags_26_2;

    public void load() throws IOException {
        codec_1_16 = readCompoundBinaryTag("/dimension/codec_1_16.nbt");
        codec_1_16_2 = readCompoundBinaryTag("/dimension/codec_1_16_2.nbt");
        codec_1_17 = readCompoundBinaryTag("/dimension/codec_1_17.nbt");
        codec_1_18_2 = readCompoundBinaryTag("/dimension/codec_1_18_2.nbt");
        codec_1_19 = readCompoundBinaryTag("/dimension/codec_1_19.nbt");
        codec_1_19_1 = readCompoundBinaryTag("/dimension/codec_1_19_1.nbt");
        codec_1_19_4 = readCompoundBinaryTag("/dimension/codec_1_19_4.nbt");
        codec_1_20 = readCompoundBinaryTag("/dimension/codec_1_20.nbt");
        codec_1_20_5 = readCompoundBinaryTag("/dimension/codec_1_20_5.nbt");
        codec_1_21 = readCompoundBinaryTag("/dimension/codec_1_21.nbt");
        codec_1_21_2 = readCompoundBinaryTag("/dimension/codec_1_21_2.nbt");
        codec_1_21_4 = readCompoundBinaryTag("/dimension/codec_1_21_4.nbt");
        codec_1_21_5 = readCompoundBinaryTag("/dimension/codec_1_21_5.nbt");
        codec_1_21_6 = readCompoundBinaryTag("/dimension/codec_1_21_6.nbt");
        codec_1_21_7 = readCompoundBinaryTag("/dimension/codec_1_21_7.nbt");
        codec_1_21_9 = readCompoundBinaryTag("/dimension/codec_1_21_9.nbt");
        codec_1_21_11 = readCompoundBinaryTag("/dimension/codec_1_21_11.nbt");
        codec_26_1 = readCompoundBinaryTag("/dimension/codec_26_1.nbt");
        codec_26_2 = readCompoundBinaryTag("/dimension/codec_26_2.nbt");

        tags_1_20_5 = readCompoundBinaryTag("/dimension/tags_1_20_5.nbt");
        tags_1_21 = readCompoundBinaryTag("/dimension/tags_1_21.nbt");
        tags_1_21_2 = readCompoundBinaryTag("/dimension/tags_1_21_2.nbt");
        tags_1_21_4 = readCompoundBinaryTag("/dimension/tags_1_21_4.nbt");
        tags_1_21_5 = readCompoundBinaryTag("/dimension/tags_1_21_5.nbt");
        tags_1_21_6 = readCompoundBinaryTag("/dimension/tags_1_21_6.nbt");
        tags_1_21_7 = readCompoundBinaryTag("/dimension/tags_1_21_7.nbt");
        tags_1_21_9 = readCompoundBinaryTag("/dimension/tags_1_21_9.nbt");
        tags_1_21_11 = readCompoundBinaryTag("/dimension/tags_1_21_11.nbt");
        tags_26_1 = readCompoundBinaryTag("/dimension/tags_26_1.nbt");
        tags_26_2 = readCompoundBinaryTag("/dimension/tags_26_2.nbt");
    }

    @NonNull
    private CompoundBinaryTag readCompoundBinaryTag(@NonNull String resPath) throws IOException {
        try (InputStream in = this.server.getClass().getResourceAsStream(resPath)) {
            if (in == null) {
                throw new IllegalStateException("Input stream is null!");
            }
            return BinaryTagIO.unlimitedReader().read(in, BinaryTagIO.Compression.GZIP);
        }
    }

    @NonNull
    private CompoundBinaryTag getRegistryByVersion(@NonNull Version version) {
        if (version.moreOrEqual(Version.V26_2)) {
            return this.codec_26_2;
        } else if (version.moreOrEqual(Version.V26_1)) {
            return this.codec_26_1;
        } else if (version.moreOrEqual(Version.V1_21_11)) {
            return this.codec_1_21_11;
        } else if (version.equals(Version.V1_21_9)) {
            return this.codec_1_21_9;
        } else if (version.equals(Version.V1_21_7)) {
            return this.codec_1_21_7;
        } else if (version.equals(Version.V1_21_6)) {
            return this.codec_1_21_6;
        } else if (version.equals(Version.V1_21_5)) {
            return this.codec_1_21_5;
        } else if (version.equals(Version.V1_21_4)) {
            return this.codec_1_21_4;
        } else if (version.equals(Version.V1_21_2)) {
            return this.codec_1_21_2;
        } else if (version.equals(Version.V1_21)) {
            return this.codec_1_21;
        } else if (version.equals(Version.V1_20_5)) {
            return this.codec_1_20_5;
        } else if (version.moreOrEqual(Version.V1_20)) {
            return this.codec_1_20;
        } else if (version.equals(Version.V1_19_4)) {
            return this.codec_1_19_4;
        } else if (version.moreOrEqual(Version.V1_19_1)) {
            return this.codec_1_19_1;
        } else if (version.equals(Version.V1_19)) {
            return this.codec_1_19;
        } else if (version.equals(Version.V1_18_2)) {
            return this.codec_1_18_2;
        } else if (version.moreOrEqual(Version.V1_17)) {
            return this.codec_1_17;
        } else if (version.moreOrEqual(Version.V1_16_2)) {
            return this.codec_1_16_2;
        } else {
            return this.codec_1_16;
        }
    }

    @Nullable
    public Dimension findDimension(@NonNull Version version, @NonNull NamespacedKey dimensionKey) {
        CompoundBinaryTag codec = getRegistryByVersion(version);
        BiFunction<Integer, CompoundBinaryTag, Dimension> findModernDimension = (index, dimensionTag) -> {
            String name = dimensionTag.getString("name");
            if (name.equals(dimensionKey.toString())) {
                CompoundBinaryTag elementTag = (CompoundBinaryTag) dimensionTag.get("element");

                int id = dimensionTag.getInt("id");
                if (elementTag != null) {
                    int height = elementTag.getInt("height");
                    return new Dimension(dimensionKey, id, height, codec, elementTag);
                }
            }

            return null;
        };

        Dimension modern = findDefaultDimension(codec, findModernDimension);
        if (modern != null) {
            return modern;
        }

        BiFunction<Integer, CompoundBinaryTag, Dimension> findLegacyDimension = (index, dimensionTag) -> {
            String name = dimensionTag.getString("name");
            if (name.equals(dimensionKey.toString())) {
                int height = dimensionTag.getInt("height");
                return new Dimension(dimensionKey, index, height, codec, dimensionTag);
            }

            return null;
        };
        return findDefaultDimension(codec, findLegacyDimension);
    }

    @Nullable
    private static <T> T findDefaultDimension(@NonNull CompoundBinaryTag codec,
                                              @NonNull BiFunction<Integer, CompoundBinaryTag, T> function) {
        ListBinaryTag dimensions;
        BinaryTag binaryDimensionType = codec.get("minecraft:dimension_type");
        if (binaryDimensionType instanceof CompoundBinaryTag tag) {
            dimensions = tag.getList("value");
        } else {
            dimensions = codec.getList("dimension");
        }

        for (int i = 0; i < dimensions.size(); i++) {
            BinaryTag dimension = dimensions.get(i);

            CompoundBinaryTag dimensionTag = (CompoundBinaryTag) dimension;

            T result = function.apply(i, dimensionTag);
            if (result != null) {
                return result;
            }
        }

        CompoundBinaryTag defaultDimension = (CompoundBinaryTag) dimensions.get(0);
        return function.apply(0, defaultDimension);
    }

    @NonNull
    public Map<Version, List<MetadataWriter>> createPerVersionRegistries() {
        Map<Version, List<MetadataWriter>> perVersionRegistry = new EnumMap<>(Version.class);
        for (Version version : Version.values()) {
            if (version.less(Version.V1_20_5)) {
                continue;
            }

            perVersionRegistry.put(version, createRegistries(getRegistryByVersion(version)));
        }

        return perVersionRegistry;
    }

    @NonNull
    private static List<MetadataWriter> createRegistries(@NonNull CompoundBinaryTag tags) {
        List<MetadataWriter> cachedRegistriesData = new ArrayList<>();
        for (Map.Entry<String, ? extends BinaryTag> entry : tags) {
            String registryType = entry.getKey();
            CompoundBinaryTag compoundRegistryType = (CompoundBinaryTag) entry.getValue();

            ListBinaryTag values = compoundRegistryType.getList("value");

            cachedRegistriesData.add(createMetadataCodec(
                    registryType,
                    values
            ));
        }

        return cachedRegistriesData;
    }

    @NonNull
    private static MetadataWriter createMetadataCodec(@NonNull String registryType,
                                                      @NonNull ListBinaryTag values) {
        return (msg, version) -> {
            msg.writeString(registryType);

            msg.writeVarInt(values.size());
            for (BinaryTag entry : values) {
                CompoundBinaryTag entryTag = (CompoundBinaryTag) entry;

                String name = entryTag.getString("name");
                BinaryTag element = entryTag.get("element");

                msg.writeString(name);
                if (element instanceof CompoundBinaryTag elementTag) {
                    msg.writeBoolean(true);
                    msg.writeCompoundTag(elementTag, version);
                } else {
                    msg.writeBoolean(false);
                }
            }
        };
    }

    @NonNull
    public Map<String, Map<String, List<Integer>>> createUpdateTags(@NonNull Version version) {
        if (version.moreOrEqual(Version.V26_2)) {
            return parseUpdateTags(this.tags_26_2);
        } else if (version.moreOrEqual(Version.V26_1)) {
            return parseUpdateTags(this.tags_26_1);
        } else if (version.moreOrEqual(Version.V1_21_11)) {
            return parseUpdateTags(this.tags_1_21_11);
        } else if (version.equals(Version.V1_21_9)) {
            return parseUpdateTags(this.tags_1_21_9);
        } else if (version.equals(Version.V1_21_7)) {
            return parseUpdateTags(this.tags_1_21_7);
        } else if (version.equals(Version.V1_21_6)) {
            return parseUpdateTags(this.tags_1_21_6);
        } else if (version.equals(Version.V1_21_5)) {
            return parseUpdateTags(this.tags_1_21_5);
        } else if (version.equals(Version.V1_21_4)) {
            return parseUpdateTags(this.tags_1_21_4);
        } else if (version.equals(Version.V1_21_2)) {
            return parseUpdateTags(this.tags_1_21_2);
        } else if (version.equals(Version.V1_21)) {
            return parseUpdateTags(this.tags_1_21);
        } else {
            return parseUpdateTags(this.tags_1_20_5);
        }
    }

    @NonNull
    private static Map<String, Map<String, List<Integer>>> parseUpdateTags(@NonNull CompoundBinaryTag tags) {
        Map<String, Map<String, List<Integer>>> tagsMap = new HashMap<>();

        for (Map.Entry<String, ? extends BinaryTag> namedTag : tags) {
            Map<String, List<Integer>> subTagsMap = new HashMap<>();

            CompoundBinaryTag subTag = (CompoundBinaryTag) namedTag.getValue();
            for (Map.Entry<String, ? extends BinaryTag> subNamedTag : subTag) {
                List<Integer> idsList = new ArrayList<>();
                ListBinaryTag ids = (ListBinaryTag) subNamedTag.getValue();
                for (BinaryTag id : ids) {
                    idsList.add(((IntBinaryTag) id).value());
                }

                subTagsMap.put(subNamedTag.getKey(), idsList);
            }

            tagsMap.put(namedTag.getKey(), subTagsMap);
        }

        return tagsMap;
    }
}
