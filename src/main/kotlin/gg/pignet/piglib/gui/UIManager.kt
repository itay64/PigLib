package gg.pignet.piglib.gui

import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryOpenEvent
import gg.pignet.piglib.gui.elements.Button
import java.util.*

/**
 * Singleton object to manage user interfaces (UIs).
 */
object UIManager {
    val guis: MutableMap<UUID, GUI> = mutableMapOf()

    /**
     * Registers the opening of a GUI for a specific player.
     *
     * @param gui The GUI being opened.
     * @param player The player who opened the GUI.
     */
    fun registerGUIOpen(gui: GUI, player: Player) {
        guis[player.uniqueId] = gui
    }

    /**
     * Handles the click event on UI elements.
     *
     * @param event The InventoryClickEvent to handle.
     */
    fun handleUIClick(event: InventoryClickEvent) {
        val gui = guis[event.whoClicked.uniqueId]
        gui ?: return
        val slot = event.slot
        val element = gui.elements[slot] ?: return

        if (element is Button) {
            event.isCancelled = !element.canPickup()
            element.click(event)
        }
    }

    /**
     * Handles the close event of UIs.
     *
     * @param event The InventoryCloseEvent to handle.
     */
    fun handleUIClose(event: InventoryCloseEvent) {
        val gui = guis[event.player.uniqueId]
        gui ?: return

        gui.onCloseListener?.invoke()
        guis.remove(event.player.uniqueId)
    }

    fun handleUIClose(player: Player) {
        val gui = guis[player.uniqueId]
        gui ?: return

        guis.remove(player.uniqueId)
    }

    /**
     * Handles the open event of UIs.
     *
     * @param event The InventoryOpenEvent to handle.
     */
    fun handleUIOpen(event: InventoryOpenEvent) {
        val gui = guis[event.player.uniqueId]
        gui ?: return
        gui.onOpenListener?.invoke()
    }
}