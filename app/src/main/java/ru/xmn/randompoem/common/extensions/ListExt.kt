package ru.xmn.randompoem.common.extensions

import java.util.*

fun <T> List<T>.randomItem(): T {
    val r = Random()
    return if (this.size - 1 == 0) this[0] else this[r.nextInt(this.size - 1)]
}

fun <T> List<T>.randomize(): List<T> {
    return (0 until this.size).map { this.randomItem() }
}