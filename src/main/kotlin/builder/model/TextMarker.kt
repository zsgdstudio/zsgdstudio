package builder.model

import builder.util.Flow
import builder.util.isNotLetterOrDigit
import builder.util.isNotWhitespace

abstract class TextMarker : TextElement() {}

object BOLD_START : TextMarker() //** __
object BOLD_FINISH: TextMarker()
object ITALIC_START : TextMarker() // * _
object ITALIC_FINISH : TextMarker()
object STRIKE_START: TextMarker() // ~~
object STRIKE_FINISH: TextMarker()
object CODE_START : TextMarker() // `
object CODE_FINISH : TextMarker()
object BR : TextMarker() // two+ spaces at the end of line


fun isNextBoldAmbivalent(flow: Flow<Char>): Boolean {
    val res = isNextBoldStart(flow, false) && isNextBoldFinish(flow, false)
    if (res) {
        flow.next()
        flow.next()
    }
    return res
}

fun isNextBoldStart(flow: Flow<Char>, advance: Boolean): Boolean {
    val res = flow.testPeekBack(1) { it.isNotLetterOrDigit() }
            && (
            flow.isPeek(1, '*') && flow.isPeek(2, '*')
                    || flow.isPeek(1, '_') && flow.isPeek(2, '_')
            )
            && flow.testPeek(3) { it.isNotWhitespace() }
    if (res && advance) {
        flow.next()
        flow.next()
    }
    return res
}

fun isNextBoldFinish(flow: Flow<Char>, advance: Boolean): Boolean {
    val res = flow.testPeekBack(1) { it.isNotWhitespace() }
            && (
            flow.isPeek(1, '*') && flow.isPeek(2, '*')
                    || flow.isPeek(1, '_') && flow.isPeek(2, '_')
            )
            && flow.testPeek(3) { it.isNotLetterOrDigit() }
    if (res && advance) {
        flow.next()
        flow.next()
    }
    return res
}

fun isNextItalicAmbivalent(flow: Flow<Char>): Boolean {
    val res = isNextItalicStart(flow, false) && isNextItalicFinish(flow, false)
    if (res) flow.next()
    return res
}

fun isNextItalicStart(flow: Flow<Char>, advance: Boolean): Boolean {
    val res = flow.testPeekBack(1) { it.isNotLetterOrDigit() }
            && (flow.isPeek(1, '*') || flow.isPeek(1, '_'))
            && flow.testPeek(2) { it.isNotWhitespace() }
    if (res && advance) flow.next()
    return res
}

fun isNextItalicFinish(flow: Flow<Char>, advance: Boolean): Boolean {
    val res = flow.testPeekBack(1) { it.isNotWhitespace() }
            && (flow.isPeek(1, '*') || flow.isPeek(1, '_'))
            && flow.testPeek(2) { it.isNotLetterOrDigit() }
    if (res && advance) flow.next()
    return res
}

fun isNextStrike(flow: Flow<Char>): Boolean {
    val res = flow.isPeek(1, '~') && flow.isPeek(2, '~')
    if (res) {
        flow.next()
        flow.next()
    }
    return res
}

fun isNextCode(flow: Flow<Char>): Boolean {
    val res = flow.isPeek(1, '`')
    if (res) flow.next()
    return res
}