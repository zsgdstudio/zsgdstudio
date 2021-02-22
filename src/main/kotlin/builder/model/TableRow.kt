package builder.model

class TableRow(context: Context, curPage: Page, fromLine: String) {
    var cells: MutableList<TableCell> = ArrayList()

    init {
        fromLine.split('|').forEach { cells.add(TableCell(context, curPage, it.trim())) }
    }
}