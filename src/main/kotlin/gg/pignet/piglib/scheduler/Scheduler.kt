package gg.pignet.piglib.scheduler

import gg.pignet.piglib.PigLib
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.scheduler.BukkitTask

fun sync(runnable: BukkitRunnable.() -> Unit): BukkitTask {
    return createBukkitRunnable(runnable).runTask(PigLib.plugin)
}

fun async(runnable: BukkitRunnable.() -> Unit): BukkitTask {
    return createBukkitRunnable(runnable).runTaskAsynchronously(PigLib.plugin)
}

fun delay(value: Int, unit: TimeUnit = TimeUnit.TICKS, async: Boolean = false, runnable: BukkitRunnable.() -> Unit): BukkitTask {
    return if (async) {
        createBukkitRunnable(runnable).runTaskLaterAsynchronously(PigLib.plugin, unit.toTicks(value.toLong()))
    } else {
        createBukkitRunnable(runnable).runTaskLater(PigLib.plugin, unit.toTicks(value.toLong()))
    }
}

fun delay(ticks: Int = 1, runnable: BukkitRunnable.() -> Unit): BukkitTask {
    return delay(ticks, TimeUnit.TICKS, false, runnable)
}

fun delay(ticks: Int = 1, async: Boolean, runnable: BukkitRunnable.() -> Unit): BukkitTask {
    return delay(ticks, TimeUnit.TICKS, async, runnable)
}

fun repeat(delay: Int, period: Int, unit: TimeUnit = TimeUnit.TICKS, async: Boolean = false, runnable: BukkitRunnable.() -> Unit): BukkitTask {
    return if (async) {
        createBukkitRunnable(runnable).runTaskTimerAsynchronously(PigLib.plugin, unit.toTicks(delay.toLong()), unit.toTicks(period.toLong()))
    } else {
        createBukkitRunnable(runnable).runTaskTimer(PigLib.plugin, unit.toTicks(delay.toLong()), unit.toTicks(period.toLong()))
    }
}

fun repeat(periodTicks: Int = 1, runnable: BukkitRunnable.() -> Unit): BukkitTask {
    return repeat(periodTicks, periodTicks, TimeUnit.TICKS, false, runnable)
}

fun repeat(periodTicks: Int = 1, async: Boolean, runnable: BukkitRunnable.() -> Unit): BukkitTask {
    return repeat(periodTicks, periodTicks, TimeUnit.TICKS, async, runnable)
}

fun repeat(period: Int, unit: TimeUnit, runnable: BukkitRunnable.() -> Unit): BukkitTask {
    return repeat(period, period, unit, false, runnable)
}

fun repeat(period: Int, unit: TimeUnit, async: Boolean, runnable: BukkitRunnable.() -> Unit): BukkitTask {
    return repeat(period, period, unit, async, runnable)
}

fun repeat(delayTicks: Int, periodTicks: Int, async: Boolean, runnable: BukkitRunnable.() -> Unit): BukkitTask {
    return repeat(delayTicks, periodTicks, TimeUnit.TICKS, async, runnable)
}

fun repeat(delay: Int, period: Int, unit: TimeUnit, runnable: BukkitRunnable.() -> Unit): BukkitTask {
    return repeat(delay, period, unit, false, runnable)
}

private fun createBukkitRunnable(runnable: BukkitRunnable.() -> Unit): BukkitRunnable {
    return object : BukkitRunnable() {
        override fun run() {
            this.runnable()
        }
    }
}

