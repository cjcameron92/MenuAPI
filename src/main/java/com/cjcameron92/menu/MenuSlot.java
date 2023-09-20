package com.cjcameron92.menu;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;

/**
 * Represents a slot within a menu.
 */
public record MenuSlot(int slot, ItemStack itemStack, Consumer<InventoryClickEvent> consumer) {

    /**
     * Fires the associated consumer when a click event occurs in the menu slot.
     *
     * @param event The inventory click event.
     */
    public void fire(InventoryClickEvent event) {
        consumer.accept(event);
    }
}