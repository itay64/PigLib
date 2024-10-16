package gg.pignet.piglib.gui

import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.OfflinePlayer
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta
class ItemStackBuilder(val material: Material) {

    var name: Component? = null

    private var lore: MutableList<Component> = mutableListOf()

    fun addLore(vararg lore: Component) = this.lore.addAll(lore.asList())

    var skullOwner: OfflinePlayer? = null


    fun build(): ItemStack =
        ItemStack(material).apply {
            val meta = itemMeta.apply {
                name?.let { displayName(it) }
                if (this@ItemStackBuilder.lore.isNotEmpty()) { lore(this@ItemStackBuilder.lore) }
            }
            skullOwner?.let { if (meta is SkullMeta){ meta.setOwningPlayer(skullOwner) } }

            itemMeta = meta
        }


    companion object {
        fun builder(material: Material, factory: ItemStackBuilder.() -> Unit): ItemStack = ItemStackBuilder(material).apply(factory).build()
    }
}