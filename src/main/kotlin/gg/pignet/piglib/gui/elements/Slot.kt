package gg.pignet.piglib.gui.elements

import org.bukkit.inventory.ItemStack

/**
 * A class representing a slot in an inventory.
 * @see Element
 */
open class Slot : Element() {
    private var itemSupplier: (() -> ItemStack)? = null
    var itemStack: ItemStack? = null

    fun item(item: ItemStack) {
        itemStack = item
    }
}