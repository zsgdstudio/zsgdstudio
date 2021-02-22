package builder.model

import builder.util.Flow
import builder.util.emptyFlow
import builder.util.trimLeft

class ListBlock(context: Context, curPage: Page, fromLines: Flow<String>) : Block() {
    var ordered: Boolean = isOrderedListFirstLine(fromLines.peek(1) ?: "")
    var items : MutableList<ListItemBlock> = ArrayList()

    init {
        while (isListFirstLine(fromLines.peek(1) ?: "", ordered)) {
            val itemSrcLines = emptyFlow<String>()
            itemSrcLines.add(trimListFirstLine(fromLines.next(), ordered))
            while (isListItemLine(fromLines.peek(1) ?: "")) {
                itemSrcLines.add(fromLines.next().substring(2))
            }
            items.add(ListItemBlock(context, curPage, itemSrcLines))
        }
    }
}

fun isListFirstLine(line: String, ordered: Boolean): Boolean {
    return if (ordered) isOrderedListFirstLine(line)
    else isUnorderedListFirstLine(line)
}

fun isOrderedListFirstLine(line: String): Boolean {
    val firstWord = line.split(' ').firstOrNull() ?: return false
    if (firstWord.endsWith('.')) {
        firstWord.substring(0, firstWord.length-1).toIntOrNull() ?: return false
        return true
    }
    return false
}

fun isUnorderedListFirstLine(line: String): Boolean {
    return line.startsWith("- ") || line.startsWith("* ") || line.startsWith("+ ")
}

fun trimListFirstLine(line: String, ordered: Boolean): String {
    return if (ordered) trimOrderedListFirstLine(line)
    else trimUnorderedListFirstLine(line)
}

fun trimOrderedListFirstLine(line: String): String {
    val length = line.split(' ').firstOrNull()?.length ?: throw IllegalStateException()
    return line.substring(length)
}

fun trimUnorderedListFirstLine(line: String): String {
    return line.substring(1).trimLeft()
}