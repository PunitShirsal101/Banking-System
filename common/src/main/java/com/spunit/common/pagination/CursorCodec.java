package com.spunit.common.pagination;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Objects;
import java.util.UUID;

/**
 * Simple Base64 cursor codec for keyset pagination.
 * Encodes lastId and optional sortKey as a base64-encoded string: "<uuid>|<sortKey>".
 */
public final class CursorCodec {
    private CursorCodec() {}

    public static String encode(UUID lastId, String sortKey) {
        Objects.requireNonNull(lastId, "lastId");
        String raw = lastId + "|" + (sortKey == null ? "" : sortKey);
        return Base64.getUrlEncoder().withoutPadding()
                .encodeToString(raw.getBytes(StandardCharsets.UTF_8));
    }

    public static Token decode(String cursor) {
        if (cursor == null || cursor.isEmpty()) return null;
        String decoded = new String(Base64.getUrlDecoder().decode(cursor), StandardCharsets.UTF_8);
        int idx = decoded.indexOf('|');
        if (idx < 0) throw new IllegalArgumentException("Invalid cursor format");
        UUID lastId = UUID.fromString(decoded.substring(0, idx));
        String sortKey = decoded.length() > idx + 1 ? decoded.substring(idx + 1) : "";
        return new Token(lastId, sortKey.isEmpty() ? null : sortKey);
    }

    public record Token(UUID lastId, String sortKey) {}
}
