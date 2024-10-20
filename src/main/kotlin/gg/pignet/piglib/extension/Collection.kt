package gg.pignet.piglib.extension

inline fun <T> Iterable<T>.applyForEach(action: T.() -> Unit) {
    forEach { it.action() }
}