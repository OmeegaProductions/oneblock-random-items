package dev.azn9.op.util;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.security.SecureRandom;
import java.util.Random;

public class ItemUtil {

    private static final Random RANDOM = new SecureRandom();

    private ItemUtil() {
        throw new IllegalStateException("Utility class");
    }

    public static ItemStack generateRandomItem() {
        boolean isValid;
        Material itemMaterial;

        do {
            itemMaterial = Material.values()[ItemUtil.RANDOM.nextInt(Material.values().length)];
            isValid = itemMaterial.isItem() && !itemMaterial.isLegacy();
        } while (!isValid);

        return new ItemStack(itemMaterial);
    }
}
