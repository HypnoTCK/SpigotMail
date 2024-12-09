package org.hypno.spigotMail;

import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public class PackageDataPersistentDataType implements PersistentDataType<byte[], PackageData> {
    @Override
    public @NotNull Class<byte[]> getPrimitiveType() {
        return byte[].class;
    }

    @Override
    public @NotNull Class<PackageData> getComplexType() {
        return PackageData.class;
    }

    @Override
    public byte @NotNull [] toPrimitive(@NotNull PackageData packageData, @NotNull PersistentDataAdapterContext persistentDataAdapterContext) {
        var outputStream = new ByteArrayOutputStream();
        outputStream.write(packageData.type().getByteRepresentation());
        if (packageData.type() == PackageType.ITEM && packageData.item() != null) {
            outputStream.writeBytes(packageData.item().serializeAsBytes());
        }

        return outputStream.toByteArray();
    }

    @Override
    public @NotNull PackageData fromPrimitive(byte @NotNull [] bytes, @NotNull PersistentDataAdapterContext persistentDataAdapterContext) {
        if (bytes.length == 0) {
            return new PackageData(PackageType.EMPTY, null);
        }
        var inputStream = new ByteArrayInputStream(bytes);

        var type = PackageType.fromByte((byte) inputStream.read());
        ItemStack item = null;
        if (type == PackageType.ITEM) {
            item = ItemStack.deserializeBytes(inputStream.readAllBytes());
        }
        return new PackageData(type, item);
    }
}
