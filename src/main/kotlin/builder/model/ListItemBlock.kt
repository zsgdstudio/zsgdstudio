package builder.model

import builder.util.Flow

class ListItemBlock(context: Context, curPage: Page, fromLines: Flow<String>) : Block() {
    val subBlocks: List<Block> = blocks(context, curPage, fromLines)
}

fun isListItemLine(line: String): Boolean {
    return line.startsWith("  ")
}