package builder.model

import builder.util.Flow

class TableCell(context: Context, curPage: Page, fromLine: String) {
    var textElements = textElements(context, curPage, Flow(mutableListOf(fromLine)))
}