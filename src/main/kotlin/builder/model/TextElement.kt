package builder.model

import builder.util.Flow

abstract class TextElement

class TextElementContext(val curPage: Page) {
    private var isBold = false
    private var isItalic = false
    private var isStrike = false
    private var isCode = false
    private var hasBr = false

    val res = ArrayList<TextElement>()

    fun br() {
        hasBr = true
    }

    fun boldAmbivalent() {
        if (isBold) res.add(BOLD_FINISH) else res.add(BOLD_START)
        isBold = !isBold
    }

    fun boldStart(context: Context, line: String) {
        if (isBold) context.addWarning("${curPage.srcPath}", line, "double bold(** or __) opening tag")
        res.add(BOLD_START)
        isBold = true
    }

    fun boldFinish(context: Context, line: String) {
        if (!isBold) context.addWarning("${curPage.srcPath}",line, "bold(** or __) closing tag without opening")
        res.add(BOLD_FINISH)
        isBold = false
    }

    fun italicAmbivalent() {
        if (isItalic) res.add(ITALIC_FINISH) else res.add(ITALIC_START)
        isItalic = !isItalic
    }

    fun italicStart(context: Context, line: String) {
        if (isItalic) context.addWarning("${curPage.srcPath}", line, "double italic(* or _) opening tag")
        res.add(ITALIC_START)
        isItalic = true
    }

    fun italicFinish(context: Context, line: String) {
        if (!isItalic) context.addWarning("${curPage.srcPath}",line, "italic(* or _) closing tag without opening")
        res.add(ITALIC_FINISH)
        isItalic = false
    }

    fun strike() {
        if (isStrike) res.add(STRIKE_FINISH) else res.add(STRIKE_START)
        isStrike = !isStrike
    }

    fun code() {
        if (isCode) res.add(CODE_FINISH) else res.add(CODE_START)
        isCode = !isCode
    }

    fun appendChar(char: Char) {
        val lastPlainText = if (res.isNotEmpty() && res.last() is PlainText)
            res.last() as PlainText
        else
            PlainText().also { res.add(it) }
        lastPlainText.content += char
    }

    fun finishLine(lastLine: Boolean) {
        if (hasBr) {
            res.add(BR)
            hasBr = false
        } else if (!lastLine) {
            appendChar(' ')
        }
    }

    fun check(context: Context, line: String) {
        if (isBold || isItalic || isStrike || isCode) {
            var message = "non closed format tag(s): "
            if (isBold) message += "bold(** or __) "
            if (isItalic) message += "italic(* or _) "
            if (isStrike) message += "strike(~~) "
            if (isCode) message += "code(`) "
            context.addWarning("${curPage.srcPath}", line, message)
        }
    }
}

fun textElements(context: Context, curPage: Page, fromLines: Flow<String>) : List<TextElement> {
    val teContext = TextElementContext(curPage)
    while (fromLines.hasNext()) {
        val line = fromLines.next()
        if (line.endsWith("  ")) teContext.br()
        val fromLine = Flow(line.trim().toMutableList())
        while (fromLine.hasNext()) {
            when {
                isNextBoldAmbivalent(fromLine) -> teContext.boldAmbivalent()
                isNextBoldStart(fromLine, true) -> teContext.boldStart(context, line)
                isNextBoldFinish(fromLine, true) -> teContext.boldFinish(context, line)
                isNextItalicAmbivalent(fromLine) -> teContext.italicAmbivalent()
                isNextItalicStart(fromLine, true) -> teContext.italicStart(context, line)
                isNextItalicFinish(fromLine, true) -> teContext.italicFinish(context, line)
                isNextStrike(fromLine) -> teContext.strike()
                isNextCode(fromLine) -> teContext.code()
                isNextLink(fromLine) -> {
                    val link = link(context, curPage, fromLine)
                    teContext.res.add(link)
                }
                else -> teContext.appendChar(fromLine.next())
            }
        }
        teContext.finishLine(!fromLines.hasNext())
    }
    teContext.check(context, fromLines.toString())
    return teContext.res
}