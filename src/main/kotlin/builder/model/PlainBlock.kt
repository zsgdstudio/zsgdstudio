package builder.model

import builder.util.Flow
import builder.util.emptyFlow

class PlainBlock(context: Context, curPage: Page, fromLines: Flow<String>) : Block() {
    val textElements: List<TextElement>

    init {
        val srcLines = emptyFlow<String>()

        while (fromLines.hasNext()) {
            val peekLine = fromLines.peek() ?: ""
            if (peekLine.isBlank()) break

            if (isPlaintBLockLine(peekLine))
                srcLines.add(fromLines.next())
            else
                break
        }

        textElements = textElements(context, curPage, srcLines)
    }
}

fun isPlaintBLockLine(line: String): Boolean {
    return !isQuoteBlockLine(line) && !isHeaderBlockLine(line) && !isTableBlockLine(line) &&
            !isOrderedListFirstLine(line) && !isUnorderedListFirstLine(line)
}