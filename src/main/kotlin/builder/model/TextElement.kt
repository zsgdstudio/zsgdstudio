package builder.model

import builder.util.Flow
import builder.util.isNotLetterOrDigit
import builder.util.isNotWhitespace

abstract class TextElement {}

fun textElements(context: Context, curPage: Page, fromLines: Flow<String>) : List<TextElement> {
    var isBold = false
    var isItalic = false
    var isStrike = false
    var isCode = false
    var hasBr = false

    val res = ArrayList<TextElement>()

    while (fromLines.hasNext()) {
        val line = fromLines.next()

        if (line.endsWith("  ")) hasBr = true
        val fromLine = Flow(line.trim().toMutableList())

        while (fromLine.hasNext()) {
            when {
                isNextAmbivalentBold(fromLine) -> {
                    fromLine.next()
                    fromLine.next()
                    if (isBold) res.add(BOLD_FINISH) else res.add(BOLD_START)
                    isBold = !isBold
                }
                isNextBoldStart(fromLine) -> {
                    fromLine.next()
                    fromLine.next()
                    res.add(BOLD_START)
                    if (isBold) context.addWarning("double bold(** or __) opening tag", line)
                    isBold = true
                }
                isNextBoldFinish(fromLine) -> {
                    fromLine.next()
                    fromLine.next()
                    res.add(BOLD_FINISH)
                    if (!isBold) context.addWarning("bold(** or __) closing tag without opening", line)
                    isBold = false
                }
                isNextAmbivalentItalic(fromLine) -> {
                    fromLine.next()
                    if (isItalic) res.add(ITALIC_FINISH) else res.add(ITALIC_START)
                    isItalic = !isItalic
                }
                isNextItalicStart(fromLine) -> {
                    fromLine.next()
                    res.add(ITALIC_START)
                    if (isItalic) context.addWarning("double italic(* or _) opening tag", line)
                    isItalic = true
                }
                isNextItalicFinish(fromLine) -> {
                    fromLine.next()
                    res.add(ITALIC_FINISH)
                    if (!isItalic) context.addWarning("italic(* or _) closing tag without opening", line)
                    isItalic = false
                }
                isNextStrike(fromLine) -> {
                    fromLine.next()
                    fromLine.next()
                    if (isStrike) res.add(STRIKE_FINISH) else res.add(STRIKE_START)
                    isStrike = !isStrike
                }
                isNextCode(fromLine) -> {
                    fromLine.next()
                    if (isCode) res.add(CODE_FINISH) else res.add(CODE_START)
                    isCode = !isCode
                }
                isNextLink(fromLine) -> {
                    res.add(link(context, curPage, fromLine))
                }
                else -> {
                    val lastPlainText = if (res.isNotEmpty() && res.last() is PlainText)
                        res.last() as PlainText
                    else
                        PlainText().also { res.add(it) }
                    lastPlainText.content += fromLine.next()
                }
            }
        }

        if (hasBr) {
            res.add(BR)
            hasBr = false
        }
    }

    if (isBold || isItalic || isStrike || isCode) {
        var message = "non closed format tag(s): "
        if (isBold) message += "bold(** or __) "
        if (isItalic) message += "italic(* or _) "
        if (isStrike) message += "strike(~~) "
        if (isCode) message += "code(`) "
        context.addWarning(message, fromLines.toString())
    }

    return res
}

fun isNextAmbivalentBold(flow: Flow<Char>): Boolean {
    return isNextBoldStart(flow) && isNextBoldFinish(flow)
}

fun isNextBoldStart(flow: Flow<Char>): Boolean {
    return flow.testPeekBack(1) { it.isNotLetterOrDigit() }
            && (
                flow.isPeek(1, '*') && flow.isPeek(2, '*')
                || flow.isPeek(1, '_') && flow.isPeek(2, '_')
            )
            && flow.testPeek(3) { it.isNotWhitespace() }
}

fun isNextBoldFinish(flow: Flow<Char>): Boolean {
    return flow.testPeekBack(1) { it.isNotWhitespace() }
            && (
                flow.isPeek(1, '*') && flow.isPeek(2, '*')
                || flow.isPeek(1, '_') && flow.isPeek(2, '_')
            )
            && flow.testPeek(3) { it.isNotLetterOrDigit() }
}

fun isNextAmbivalentItalic(flow: Flow<Char>): Boolean {
    return isNextItalicStart(flow) && isNextItalicFinish(flow)
}

fun isNextItalicStart(flow: Flow<Char>): Boolean {
    return flow.testPeekBack(1) { it.isNotLetterOrDigit() }
            && (flow.isPeek(1, '*') || flow.isPeek(1, '_'))
            && flow.testPeek(2) { it.isNotWhitespace() }
}

fun isNextItalicFinish(flow: Flow<Char>): Boolean {
    return flow.testPeekBack(1) { it.isNotWhitespace() }
            && (flow.isPeek(1, '*') || flow.isPeek(1, '_'))
            && flow.testPeek(2) { it.isNotLetterOrDigit() }
}

fun isNextStrike(flow: Flow<Char>): Boolean {
    return flow.isPeek(1, '~') && flow.isPeek(2, '~')
}

fun isNextCode(flow: Flow<Char>): Boolean {
    return flow.isPeek(1, '`')
}