package gg.pignet.piglib.extension

import org.bukkit.Location
import org.bukkit.entity.Entity
import org.bukkit.util.BoundingBox

fun BoundingBox.contains(location: Location) = contains(location.x, location.y, location.z)
fun BoundingBox.contains(entity: Entity) = contains(entity.location)