package gg.pignet.piglib.scheduler

enum class TimeUnit(private val javaTimeUnit: java.util.concurrent.TimeUnit?) {

    DAYS(java.util.concurrent.TimeUnit.DAYS),
    HOURS(java.util.concurrent.TimeUnit.HOURS),
    MINUTES(java.util.concurrent.TimeUnit.MINUTES),
    SECONDS(java.util.concurrent.TimeUnit.SECONDS),
    TICKS(null),
    MILLISECONDS(java.util.concurrent.TimeUnit.MILLISECONDS);

    companion object {
        private const val TICKS_PER_SECOND = 20
        private const val TICKS_PER_MINUTE = TICKS_PER_SECOND * 60
        private const val TICKS_PER_HOUR = TICKS_PER_MINUTE * 60
        private const val TICKS_PER_DAY = TICKS_PER_HOUR * 24
    }

    fun toDays(duration: Long): Long {
        return javaTimeUnit?.toDays(duration) ?: (duration / TICKS_PER_DAY)
    }

    fun toHours(duration: Long): Long {
        return javaTimeUnit?.toHours(duration) ?: (duration / TICKS_PER_HOUR)
    }

    fun toMinutes(duration: Long): Long {
        return javaTimeUnit?.toMinutes(duration) ?: (duration / TICKS_PER_MINUTE)
    }

    fun toSeconds(duration: Long): Long {
        return javaTimeUnit?.toSeconds(duration) ?: (duration / TICKS_PER_SECOND)
    }

    fun toTicks(duration: Long): Long {
        return (javaTimeUnit?.toMillis(duration)?.div(50)) ?: duration
    }

    fun toMillis(duration: Long): Long {
        return javaTimeUnit?.toMillis(duration) ?: (duration * 50)
    }

}