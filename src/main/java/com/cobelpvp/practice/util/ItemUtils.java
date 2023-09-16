package com.cobelpvp.practice.util;

import java.util.function.Predicate;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionType;

public final class ItemUtils {
    public static final Predicate<ItemStack> INSTANT_HEAL_POTION_PREDICATE;

    public static final Predicate<ItemStack> SOUP_PREDICATE;

    public static final Predicate<ItemStack> DEBUFF_POTION_PREDICATE;

    public static final Predicate<ItemStack> EDIBLE_PREDICATE;

    private ItemUtils() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    static {
        INSTANT_HEAL_POTION_PREDICATE = (item -> {
            if (item.getType() != Material.POTION)
                return false;
            PotionType potionType = Potion.fromItemStack(item).getType();
            return (potionType == PotionType.INSTANT_HEAL);
        });
        SOUP_PREDICATE = (item -> (item.getType() == Material.MUSHROOM_SOUP));
        DEBUFF_POTION_PREDICATE = (item -> {
            if (item.getType() == Material.POTION) {
                PotionType type = Potion.fromItemStack(item).getType();
                return (type == PotionType.WEAKNESS || type == PotionType.SLOWNESS || type == PotionType.POISON || type == PotionType.INSTANT_DAMAGE);
            }
            return false;
        });
        EDIBLE_PREDICATE = (item -> item.getType().isEdible());
    }

    public static int countStacksMatching(ItemStack[] items, Predicate<ItemStack> predicate) {
        if (items == null)
            return 0;
        int amountMatching = 0;
        for (ItemStack item : items) {
            if (item != null && predicate.test(item))
                amountMatching++;
        }
        return amountMatching;
    }
}