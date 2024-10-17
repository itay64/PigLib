package gg.pignet.piglib.extension

import io.papermc.paper.entity.TeleportFlag
import org.bukkit.Location
import org.bukkit.Sound
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerTeleportEvent
import org.bukkit.inventory.ItemStack

fun Player.tp(newLocation: Location) = teleport(newLocation, PlayerTeleportEvent.TeleportCause.PLUGIN, TeleportFlag.EntityState.RETAIN_PASSENGERS)
fun Player.tp(entity: Entity) = teleport(entity.location, PlayerTeleportEvent.TeleportCause.PLUGIN, TeleportFlag.EntityState.RETAIN_PASSENGERS)
fun Player.tpAsync(newLocation: Location) = teleportAsync(newLocation, PlayerTeleportEvent.TeleportCause.PLUGIN, TeleportFlag.EntityState.RETAIN_PASSENGERS)
fun Player.tpAsync(entity: Entity) = teleportAsync(entity.location, PlayerTeleportEvent.TeleportCause.PLUGIN, TeleportFlag.EntityState.RETAIN_PASSENGERS)

fun Player.playSound(bukkitSound: Sound) = playSound(location, bukkitSound, 1F, 1F)
fun Player.playSound(customSound: String) = playSound(location, customSound, 1F, 1F)

fun Player.feed(){ foodLevel = 20 }
fun Player.resetWalkSpeed(){ walkSpeed = 0.2F }
fun Player.resetFlySpeed(){ flySpeed = 0.1F }

fun Player.addToInvOrDrop(itemStack: ItemStack) {
    inventory.addItem(itemStack).values.forEach {
        location.world.dropItem(location, itemStack)
    }
}

fun Player.removeActivePotionEffects() { activePotionEffects.forEach { removePotionEffect(it.type) } }


