package gg.pignet.gingerlib.gui.elements

import gg.pignet.gingerlib.gui.elements.Element
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