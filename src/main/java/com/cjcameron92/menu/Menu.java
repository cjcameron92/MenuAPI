package com.cjcameron92.menu;

import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Consumer;

@Getter
public class Menu implements Listener {

    private final Plugin plugin;

    private final Player player;

    private final Component inventoryTitle;

    private final int rows;

    private final Inventory inventory;

    private final Map<Integer, MenuSlot> slots;

    @Nullable
    private final Menu fallbackMenu;

    @Nullable
    private String[] shape;

    private boolean firstDraw = true;
    private boolean valid = false;
    private boolean invalidated = false;


    public Menu(Plugin plugin, Player player, String[] shape, Component inventoryTitle, Menu fallbackMenu) {
        this(plugin, player, shape.length, inventoryTitle, fallbackMenu);
        this.shape = shape;
    }

    public Menu(Plugin plugin, Player player, int rows, Component inventoryTitle, Menu fallbackMenu) {
        this.plugin = plugin;
        this.player = player;
        this.inventoryTitle = inventoryTitle;
        this.rows = rows * 9;
        this.inventory = Bukkit.createInventory(player, this.rows, inventoryTitle);
        this.slots = new HashMap<>();

        this.fallbackMenu = fallbackMenu;
    }


    public void setItem(int slot, MenuSlot menuSlot) {
        this.slots.put(slot, menuSlot);
    }

    public void setItem(int slot, ItemStack itemStack, Consumer<InventoryClickEvent> event) {
        this.setItem(slot, new MenuSlot(slot, itemStack, event));
    }

    public void setItem(int slot, ItemStack itemStack) {
        this.setItem(slot, new MenuSlot(slot, itemStack, $ -> {}));
    }


    public void setItem(char c, MenuSlot menuSlot) {
       final int index = computeSlotIndex.apply(shape, c);
       this.setItem(index, menuSlot);
    }

    public void setItem(char slot, ItemStack itemStack, Consumer<InventoryClickEvent> event) {
        this.setItem(slot, new MenuSlot(slot, itemStack, event));
    }

    public void setItem(char slot, ItemStack itemStack) {
        this.setItem(slot, new MenuSlot(slot, itemStack, $ -> {}));
    }

    public void redraw() {
        this.inventory.clear();
        this.slots.forEach((index, item) -> {
            if (index >= 0 && index < rows) {
                this.inventory.setItem(index, item.itemStack());
            }
        });
    }

    public void fire() {
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (this.valid) {
                throw new IllegalStateException("Gui is already opened.");
            }

            this.firstDraw = true;
            this.invalidated = false;

            try {
                redraw();
            } catch (Exception e) {
                e.printStackTrace();
                invalidate();
            }

            this.firstDraw = false;
            Bukkit.getPluginManager().registerEvents(this, this.plugin);

            this.player.openInventory(this.inventory);
            this.valid = true;
        }, 1L);;

    }


    public void close() {
        this.player.closeInventory();
    }

    private void invalidate() {
        this.valid = false;
        this.invalidated = true;
        this.inventory.clear();
        HandlerList.unregisterAll(this);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        if (this.player.equals(event.getPlayer()) && valid)
            invalidate();
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        if (this.player.equals(event.getPlayer()) && valid)
            invalidate();
    }

    @EventHandler
    public void onChangeWorld(PlayerChangedWorldEvent event) {
        if (this.player.equals(event.getPlayer()) && valid)
            invalidate();
    }

    @EventHandler
    public void onTeleport(PlayerTeleportEvent event) {
        if (this.player.equals(event.getPlayer()) && valid)
            invalidate();
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        if (event.getInventory().getHolder() != null) {
            if (event.getInventory().getHolder().equals(this.player)) {
                event.setCancelled(true);
                if (!valid) {
                    close();
                    return;
                }
            }
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getInventory().getHolder() != null) {
            if (event.getInventory().getHolder().equals(this.player)) {
                event.setCancelled(true);
                if (!valid) {
                    System.out.println("not valid?");
                    close();
                    return;
                }

                if (!event.getInventory().equals(this.inventory))
                    return;

                final int slot = event.getRawSlot();

                if (slot != event.getSlot())
                    return;

                final MenuSlot menuSlot = this.slots.get(slot);
                if (menuSlot != null) {
                    menuSlot.fire(event);
                }
            }
        }
    }

    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent event) {
        if (event.getPlayer().equals(player)) {
            if (!event.getInventory().equals(this.inventory)) {
                if (valid) {
                    invalidate();
                    System.out.println("OPEN: invalidated");
                }
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (event.getPlayer().equals(player)) {
            if (valid) {
                invalidate();

                if (!event.getInventory().equals(this.inventory)) {
                    return;
                }

                final Menu fallback = this.fallbackMenu;
                if (fallback == null)
                    return;

                Bukkit.getScheduler().runTaskLater(this.plugin, () -> {
                    if (this.player.isOnline()) {
                        if (!fallback.valid) {
                            fallback.fire();
                        }
                    }

                }, 1L);

            }
        }
    }

    public static final BiFunction<String[], Character, Integer> computeSlotIndex = (shape, character) -> {
        if (shape == null) {
            throw new IllegalStateException("You must defined the menu shape");
        }

        for (int i = 0; i < shape.length; i++) {
            for (int j = 0; j < shape[i].length(); j++) {
                if (character == shape[i].charAt(j)) {
                   return i + j;
                }
            }
        }

        throw new IllegalStateException("Could not find a matching character");
    };
}
