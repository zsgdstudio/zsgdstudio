package builder.model

import builder.util.Flow

class TableBlock(context: Context, curPage: Page, fromLines: Flow<String>) : Block() {
    var header: TableRow
    var rows: MutableList<TableRow> = ArrayList()

    init {
        val headerString = fromLines.next()
        header = TableRow(context, curPage, headerString)
        val delimiter = TableRow(context, curPage, fromLines.next())
        while (isTableBlockLine(fromLines.peek() ?: "")) {
            rows.add(TableRow(context, curPage, fromLines.next()))
        }

        if (delimiter.cells.size != header.cells.size || rows.any { it.cells.size != header.cells.size })
            context.addWarning("inconsistent row size", headerString)
    }
}

fun isTableBlockLine(line: String): Boolean {
    return line.contains('|')
}