package builder.model

import builder.util.Flow
import builder.util.absoluteNormal
import java.nio.file.Path

abstract class Link(val text: String, val image: Boolean) : TextElement() {}

fun link(context: Context, curPage: Page, fromLine: Flow<Char>): Link {
    var image = false

    var text = ""
    if (fromLine.isPeek(1, '!')) {
        fromLine.checkNext('!').checkNext('[')
        image = true
    } else {
        fromLine.checkNext('[')
    }
    while (!fromLine.isPeek(1, ']'))
        text += fromLine.next()
    fromLine.checkNext(']')

    var pathStr = ""
    fromLine.checkNext('(')
    while (!fromLine.isPeek(1, ')'))
        pathStr += fromLine.next()
    fromLine.checkNext(')')

    return if (pathStr.startsWith("http") || pathStr.startsWith("mailto:")) {
         WebLink(text, image, pathStr)
    } else {
        val pagePath = curPage.dir.srcPath.resolve(Path.of(pathStr)).absoluteNormal()
        PageLink(context, curPage, text, image, pagePath)
    }
}

fun isNextLink(flow: Flow<Char>): Boolean {
    return flow.isPeek(1, '[') ||
            flow.isPeek(1, '!') && flow.isPeek(2, '[')
}