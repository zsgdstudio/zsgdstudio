package builder.model

import builder.util.Flow

class HeaderBlock(context: Context, curPage: Page, fromLines: Flow<String>) : Block() {
    val headerLvl: Int
    val textElements : List<TextElement>

    init {
        val headerLine = fromLines.next()
        val fromLine = Flow(headerLine.toMutableList())
        var tempHeaderLevel = 0
        while (fromLine.isPeek(1, '#')) {
            fromLine.next()
            tempHeaderLevel++
        }
        this.headerLvl = tempHeaderLevel
        if (headerLvl > 6)
            context.addWarning("${curPage.srcPath}", headerLine, "header level is more than 6")

        var srcLine = ""
        while (fromLine.hasNext()) {
            srcLine += fromLine.next()
        }
        srcLine = srcLine.trim()
        textElements = textElements(context, curPage, Flow(mutableListOf(srcLine)))
    }
}

fun isHeaderBlockLine(line: String): Boolean {
    return line.startsWith('#')
}