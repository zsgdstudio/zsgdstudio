package builder.model

import builder.util.Flow

abstract class Block {}

fun blocks(context: Context, curPage: Page, fromLines: Flow<String>): List<Block> {
    check(fromLines.hasNext()) {"block lines are empty"}

    val subBlocks = ArrayList<Block>()

    while (fromLines.hasNext()) {
        val peekLine = fromLines.peek() ?: ""
        when {
            peekLine.isBlank() -> {fromLines.next(); continue}
            isTableBlockLine(peekLine) -> subBlocks.add(TableBlock(context, curPage, fromLines))
            isQuoteBlockLine(peekLine) -> subBlocks.add(QuoteBlock(context, curPage, fromLines))
            isHeaderBlockLine(peekLine) -> subBlocks.add(HeaderBlock(context, curPage, fromLines))
            isOrderedListFirstLine(peekLine) || isUnorderedListFirstLine(peekLine) -> subBlocks.add(ListBlock(context, curPage, fromLines))
            else -> subBlocks.add(PlainBlock(context, curPage, fromLines))
        }
    }

    return subBlocks
}