package org.hypno.spigotMail;

import java.util.Arrays;

public enum PackageType {
    EMPTY((byte) 0x00),
    ITEM((byte) 0x01),
    BOMB((byte) 0x02);

    private final byte byteRepresentation;

    PackageType(byte byteRepresentation) {
        this.byteRepresentation = byteRepresentation;
    }

    public byte getByteRepresentation() {
        return this.byteRepresentation;
    }

    public static PackageType fromByte(byte byteRepresentation) {
        return Arrays.stream(PackageType.values())
                .filter(pt -> pt.byteRepresentation == byteRepresentation)
                .findFirst()
                .orElse(PackageType.EMPTY);
    }
}
