package gg.pignet.gingerlib.gui

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryOpenEvent

/**
 * A listener class to handle UI-related events.
 */
class UIListener : Listener {
    /**
     * Handles the click event on inventories.
     *
     * @param event The InventoryClickEvent to handle.
     */
    @EventHandler
    fun onClick(event: InventoryClickEvent) {
        UIManager.handleUIClick(event)
    }

    /**
     * Handles the open event of inventories.
     *
     * @param event The InventoryOpenEvent to handle.
     */
    @EventHandler
    fun onOpen(event: InventoryOpenEvent) {
        UIManager.handleUIOpen(event)
    }

    /**
     * Handles the close event of inventories.
     *
     * @param event The InventoryCloseEvent to handle.
     */
    @EventHandler
    fun onClose(event: InventoryCloseEvent) {
        UIManager.handleUIClose(event)
    }
}