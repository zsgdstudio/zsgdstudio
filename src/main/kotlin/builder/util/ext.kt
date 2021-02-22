package builder.util

import java.nio.file.Path

fun String.trimLeft() : String {
    var startIndex = 0

    while (startIndex < this.length && this[startIndex].isWhitespace()) {
        startIndex++
    }

    return this.substring(startIndex)
}

fun Char?.isNotLetterOrDigit(): Boolean {
    return this?.isLetterOrDigit()?.not() ?: true
}

fun Char?.isNotWhitespace(): Boolean {
    return this?.isWhitespace()?.not() ?: true
}

fun Path.absoluteNormal(): Path {
    return this.toAbsolutePath().normalize()
}