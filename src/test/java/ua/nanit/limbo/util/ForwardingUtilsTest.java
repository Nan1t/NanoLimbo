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

package ua.nanit.limbo.util;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import ua.nanit.limbo.util.ForwardingUtils.LegacyForwarding;

import java.util.UUID;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ForwardingUtilsTest {

    private static final String UUID_TEXT = "11111111-2222-3333-4444-555555555555";
    private static final UUID PLAYER_UUID = UUID.fromString(UUID_TEXT);
    private static final String FLOODGATE = "^Floodgate^>aGVsbG8=!YWJj";
    private static final String PROPERTIES = "[{\"name\":\"textures\",\"value\":\"skin\"}]";

    @ParameterizedTest(name = "{0}")
    @MethodSource("legacyHandshakes")
    void parsesLegacyForwarding(String name, String handshake, LegacyForwarding expected) {
        assertEquals(expected, ForwardingUtils.parseLegacyForwarding(handshake));
    }

    static Stream<Arguments> legacyHandshakes() {
        return Stream.of(
                Arguments.of("plain three fields", handshake("host", "127.0.0.1", UUID_TEXT),
                        new LegacyForwarding("127.0.0.1", PLAYER_UUID, null)),
                Arguments.of("plain four fields", handshake("host", "127.0.0.1", UUID_TEXT, PROPERTIES),
                        new LegacyForwarding("127.0.0.1", PLAYER_UUID, PROPERTIES)),
                Arguments.of("undashed UUID", handshake("host", "127.0.0.1", UUID_TEXT.replace("-", "")),
                        new LegacyForwarding("127.0.0.1", PLAYER_UUID, null)),
                Arguments.of("Floodgate four fields", handshake("host", FLOODGATE, "127.0.0.1", UUID_TEXT),
                        new LegacyForwarding("127.0.0.1", PLAYER_UUID, null)),
                Arguments.of("Floodgate five fields", handshake("host", FLOODGATE, "127.0.0.1", UUID_TEXT.replace("-", ""), PROPERTIES),
                        new LegacyForwarding("127.0.0.1", PLAYER_UUID, PROPERTIES)),
                Arguments.of("plain bare IPv6", handshake("host", "::1", UUID_TEXT),
                        new LegacyForwarding("::1", PLAYER_UUID, null)),
                Arguments.of("plain bracketed IPv6", handshake("host", "[::1]", UUID_TEXT),
                        new LegacyForwarding("[::1]", PLAYER_UUID, null)),
                Arguments.of("empty properties field", handshake("host", "127.0.0.1", UUID_TEXT, ""),
                        new LegacyForwarding("127.0.0.1", PLAYER_UUID, "")),
                Arguments.of("bad plain UUID", handshake("host", "127.0.0.1", "bad-uuid"), null),
                Arguments.of("bad Floodgate UUID", handshake("host", FLOODGATE, "127.0.0.1", "bad-uuid"), null),
                Arguments.of("unmarked shifted fields", handshake("host", "ignored", "127.0.0.1", UUID_TEXT), null),
                Arguments.of("excess plain fields", handshake("host", "127.0.0.1", UUID_TEXT, PROPERTIES, "extra"), null),
                Arguments.of("excess Floodgate fields", handshake("host", FLOODGATE, "127.0.0.1", UUID_TEXT, PROPERTIES, "extra"), null),
                Arguments.of("excess empty plain field", handshake("host", "127.0.0.1", UUID_TEXT, PROPERTIES, ""), null),
                Arguments.of("missing Floodgate UUID", handshake("host", FLOODGATE, "127.0.0.1"), null),
                Arguments.of("vanilla host", "host", null),
                Arguments.of("incomplete plain fields", handshake("host", "127.0.0.1"), null),
                Arguments.of("blank address", handshake("host", "", UUID_TEXT), null),
                Arguments.of("hostname address", handshake("host", "attacker.example", UUID_TEXT), null),
                Arguments.of("JSON object address", handshake("host", "{}", UUID_TEXT), null),
                Arguments.of("JSON array address", handshake("host", "[]", UUID_TEXT), null),
                Arguments.of("marker must start the field", handshake("host", "prefix" + FLOODGATE, "127.0.0.1", UUID_TEXT), null)
        );
    }

    private static String handshake(String... fields) {
        return String.join("\0", fields);
    }
}
