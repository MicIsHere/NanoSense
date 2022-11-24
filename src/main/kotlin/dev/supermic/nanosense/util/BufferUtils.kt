package dev.supermic.nanosense.util

import java.nio.Buffer

fun Buffer.skip(count: Int) {
    this.position(position() + count)
}