package builder.model

import builder.util.Flow
import builder.util.emptyFlow

class QuoteBlock(context: Context, curPage: Page, fromLines: Flow<String>) : Block() {
    val subBlocks: List<Block>

    init {
        val srcLines = emptyFlow<String>()

        while (fromLines.hasNext()) {
            val peekLine = fromLines.peek() ?: ""
            if (isQuoteBlockLine(peekLine))
                srcLines.add(fromLines.next().substring(1))
            else
                break
        }

        this.subBlocks = blocks(context, curPage, srcLines)
    }
}

fun isQuoteBlockLine(line: String): Boolean {
    return line.startsWith('>')
}