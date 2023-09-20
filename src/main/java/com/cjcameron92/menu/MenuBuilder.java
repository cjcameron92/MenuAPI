package com.cjcameron92.menu;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Consumer;

import static com.cjcameron92.menu.Menu.computeSlotIndex;


/**
 * A builder class for creating menus with slots and shapes.
 */
public class MenuBuilder {

    private final Set<MenuSlot> slots = new HashSet<>();

    @Nullable
    private String[] shape;

    @Nullable
    private Menu fallbackMenu;

    /**
     * Adds a menu slot to the menu builder.
     *
     * @param menuSlot The menu slot to add.
     * @return The menu builder instance for method chaining.
     */
    public MenuBuilder add(MenuSlot menuSlot) {
        this.slots.add(menuSlot);
        return this;
    }

    /**
     * Adds a menu slot to the menu builder using a slot index, item stack, and event consumer.
     *
     * @param slot     The slot index to add the menu slot.
     * @param itemStack The item stack for the menu slot.
     * @param consumer The event consumer for handling clicks on the menu slot.
     * @return The menu builder instance for method chaining.
     */
    public MenuBuilder add(int slot, ItemStack itemStack, Consumer<InventoryClickEvent> consumer) {
        return add(new MenuSlot(slot, itemStack, consumer));
    }

    /**
     * Adds a menu slot to the menu builder using a slot index, item stack, and event consumer.
     *
     * @param slot     The slot index to add the menu slot.
     * @param itemStack The item stack for the menu slot.
     * @return The menu builder instance for method chaining.
     */
    public MenuBuilder add(int slot, ItemStack itemStack) {
        return add(slot, itemStack, $ -> {});
    }


    /**
     * Adds a menu slot to the menu builder using a character slot, item stack, and event consumer.
     *
     * @param slot     The character representing the slot.
     * @param itemStack The item stack for the menu slot.
     * @param consumer The event consumer for handling clicks on the menu slot.
     * @return The menu builder instance for method chaining.
     */
    public MenuBuilder add(char slot, ItemStack itemStack, Consumer<InventoryClickEvent> consumer) {
        return add(computeSlotIndex.apply(shape, slot), itemStack, consumer);
    }

    /**
     * Adds a menu slot to the menu builder using a character slot, item stack, and event consumer.
     *
     * @param slot     The character representing the slot.
     * @param itemStack The item stack for the menu slot.
     * @return The menu builder instance for method chaining.
     */
    public MenuBuilder add(char slot, ItemStack itemStack) {
        return add(slot, itemStack, $ -> {});
    }

    /**
     * Sets the shape of the menu.
     *
     * @param shape The shape of the menu as an array of strings.
     * @return The menu builder instance for method chaining.
     */
    public MenuBuilder shape(String... shape) {
        this.shape = shape;
        return this;
    }

    /**
     * Sets the fallback menu for the menu builder.
     *
     * @param menu The fallback menu.
     * @return The menu builder instance for method chaining.
     */
    public MenuBuilder fallback(Menu menu) {
        this.fallbackMenu = menu;
        return this;
    }

    /**
     * Registers the menu for a player with a given title and plugin.
     *
     * @param player        The player to register the menu for.
     * @param inventoryTitle The title of the menu.
     * @param plugin        The plugin responsible for the menu.
     * @return The registered menu.
     */
    public Menu register(Player player, String inventoryTitle, Plugin plugin) {
        return this.register(player, MiniMessage.miniMessage().deserialize(inventoryTitle).decoration(TextDecoration.ITALIC, false), plugin);
    }

    /**
     * Registers the menu for a player with a given component title and plugin.
     *
     * @param player     The player to register the menu for.
     * @param component  The title of the menu as a component.
     * @param plugin     The plugin responsible for the menu.
     * @return The registered menu.
     */
    public Menu register(Player player, Component component, Plugin plugin) {
        Menu menu;
        if (shape != null) {
            menu = new Menu(plugin, player, shape, component, fallbackMenu);
        } else menu = new Menu(plugin, player, calculateInventorySize(this.slots.size()) / 9, component, fallbackMenu);

        this.slots.forEach(menuSlot -> menu.setItem(menuSlot.slot(), menuSlot));
        return menu;
    }

    /**
     * Registers the menu for a player with a given component title and plugin, entries, mapping function, page, hasBack, and events.
     *
     * @param player The player to register the menu for.
     * @param plugin The plugin responsible for the menu.
     * @param name  The title of the menu as a component.
     * @param entries Entries
     * @param function Function
     * @param page Page
     * @param hasBack hasBack
     * @param events Events
     * @return The registeredMenu
     * @param <E> Type
     */
    public static <E> Menu createPagedMenu(Player player, Plugin plugin, Component name, List<E> entries, BiFunction<E, Integer, ItemStack> function, int page, boolean hasBack, List<Consumer<InventoryClickEvent>> events) {
        final int size = entries.size();

        int pages = (int) Math.ceil(size / 45.0D);

        if (page > pages) {
            page = 1;
        }
        int index = page * 45;

        if (index > size) {
            index = size;
        }

        final int inventorySize = ((int) Math.ceil((index - (page - 1) * 45) / 9.0D) * 9) + 9;

        final MenuBuilder builder = newBuilder();
        int slot = 0;

        for (int i = (page - 1) * 45; i < index; ++i) {
            final ItemStack item = function.apply(entries.get(i), i);
            builder.add(slot++, item, events.get(0));
        }
        if (page < pages) {
            final ItemStack nextPage = new ItemStack(Material.GREEN_STAINED_GLASS_PANE, 1);
            final ItemMeta itemMeta = nextPage.getItemMeta();
            itemMeta.displayName(MiniMessage.miniMessage().deserialize("<green>Next Page").decoration(TextDecoration.ITALIC, false));
            nextPage.setItemMeta(itemMeta);

            builder.add(inventorySize - 2, nextPage, events.get(1));
        }
        if (page > 1) {
            final ItemStack previousPage = new ItemStack(Material.RED_STAINED_GLASS_PANE, 1);
            final ItemMeta itemMeta = previousPage.getItemMeta();
            itemMeta.displayName(MiniMessage.miniMessage().deserialize("<red>Previous Page").decoration(TextDecoration.ITALIC, false));
            previousPage.setItemMeta(itemMeta);
            builder.add(inventorySize - 8, previousPage, events.get(2));
        } else if (hasBack) {
            if (events.get(3) != null) {
                final ItemStack previousPage = new ItemStack(Material.BARRIER, 1);
                final ItemMeta itemMeta = previousPage.getItemMeta();
                itemMeta.displayName(MiniMessage.miniMessage().deserialize("<red>Return Back").decoration(TextDecoration.ITALIC, false));
                previousPage.setItemMeta(itemMeta);
                builder.add(inventorySize - 8, previousPage, events.get(3));
            }
        }

        final ItemStack currentPage = new ItemStack(Material.BOOK, 1);
        final ItemMeta itemMeta = currentPage.getItemMeta();
        itemMeta.displayName(MiniMessage.miniMessage().deserialize("<yellow>Page: " + page).decoration(TextDecoration.ITALIC, false));
        currentPage.setItemMeta(itemMeta);
        builder.add(inventorySize - 5, currentPage);

        return builder.register(player, name, plugin);
    }
    private static int calculateInventorySize(int itemCount) {
        if (itemCount < 9) {
            return 9;
        } else if (itemCount > 54) {
            return 54;
        } else {
            return ((itemCount + 8) / 9) * 9;
        }
    }

    public static MenuBuilder newBuilder() {
        return new MenuBuilder();
    }

}
