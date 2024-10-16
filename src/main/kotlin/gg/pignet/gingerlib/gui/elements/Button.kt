package gg.pignet.gingerlib.gui.elements

import org.bukkit.event.inventory.InventoryClickEvent

/**
 * A class representing a button in an inventory slot.
 * @see Slot
 */
class Button : Slot() {
    private var onClickListener: ((InventoryClickEvent) -> Unit)? = null
    private var canPickup = false

    /**
     * Sets the click listener for this button.
     *
     * @param listener The lambda function to be invoked when the button is clicked.
     */
    fun onClick(listener: (InventoryClickEvent) -> Unit) {
        onClickListener = listener
    }

    /**
     * Sets whether this button can be picked up or not.
     *
     * @param state The boolean state indicating whether the button can be picked up.
     */
    fun canPickup(state: Boolean) {
        canPickup = state
    }

    /**
     * Returns whether this button can be picked up or not.
     *
     * @return True if the button can be picked up, false otherwise.
     */
    fun canPickup(): Boolean = canPickup

    /**
     * Handles the click event for this button.
     *
     * @param event The InventoryClickEvent representing the click event.
     */
    fun click(event: InventoryClickEvent) {
        onClickListener?.invoke(event)
    }
}