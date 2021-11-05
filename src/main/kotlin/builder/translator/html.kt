package builder.translator

import builder.model.*
import java.io.File
import java.nio.file.Path

fun translateToHtml(context: Context) {
    translateDir(context.rootDir)
}

private fun translateDir(dir: Dir) {
    if (!dir.reached) return

    trgPath(dir).toFile().mkdir()

    dir.dirs.forEach { translateDir(it) }
    dir.style?.let { translatePage(it) }
    dir.pages.forEach { translatePage(it) }
}

private fun translatePage(page: Page) {
    if (!page.reached) return

    val trgFile = trgPath(page).toFile()

    if (page.isMd) {
        trgFile.createNewFile()
        val lines: MutableList<String> = ArrayList()
        lines.add("<html>\n")
        translateHead(trgFile, lines, page)
        translateBody(trgFile, lines, page)
        lines.add("</html>\n")
        trgFile.appendText(lines.reduce { s1, s2 -> s1 + s2})
    } else {
        page.srcPath.toFile().copyTo(trgFile, true)
    }
}

private fun translateHead(trgFile: File, lines: MutableList<String>, page: Page) {
    lines.add("<head>\n")
    lines.add("<meta charset=\"utf-8\">\n")
    lines.add("<meta name=\"viewport\" content=\"width=device-width\">\n")
    val style = page.dir.style
    if (style != null) {
        val cssPath = relatePathToCurrentFile(trgFile, trgPath(style))
        lines.add("<link rel=\"stylesheet\" href=\"$cssPath\"/>\n")
    }
    lines.add("</head>\n")
}

private fun translateBody(trgFile: File, lines: MutableList<String>, page: Page) {
    lines.add("<body>\n")

    translateHeader(trgFile, lines, page)

    lines.add("<div class=\"content\">\n")
    translateSider(trgFile, lines, page)
    lines.add("<div class=\"page\">\n")
    translateNextPrev(trgFile, lines, page)
    translateBreadcrumbs(trgFile, lines, page)
    page.blocks.forEach { translateBlock(trgFile, lines, it) }
    translateNextPrev(trgFile, lines, page)
    lines.add("</div>\n")
    lines.add("</div>\n")

    translateFooter(trgFile, lines, page)

    lines.add("</body>\n")
}

private fun translateHeader(trgFile: File, lines: MutableList<String>, page: Page) {
    val header = page.dir.header
    if (header != null) {
        lines.add("<div class=\"header\">\n")
        header.blocks.forEach { translateBlock(trgFile, lines, it) }
        lines.add("</div>\n")
    }
}

private fun translateSider(trgFile: File, lines: MutableList<String>, page: Page) {
    val navigation = page.dir.navigation
    if (navigation != null) {
        lines.add("<div class=\"sider\">\n")
        navigation.blocks.forEach { translateBlock(trgFile, lines, it) }
        lines.add("</div>\n")
    }
}

private fun translateBreadcrumbs(trgFile: File, lines: MutableList<String>, page: Page) {
    val breadcrumbDirs = ArrayList<Dir>()
    var dirPtr: Dir? = page.dir
    while (dirPtr?.breadcrumbName != null) {
        breadcrumbDirs.add(dirPtr)
        dirPtr = dirPtr.parentDir
    }
    if (breadcrumbDirs.isNotEmpty()) {
        lines.add("<div class=\"breadcrumbs\">")
        breadcrumbDirs.reversed().forEach {
            val index = it.index
            if (index != null) {
                val breadcrumbHref = relatePathToCurrentFile(trgFile, trgPath(index))
                lines.add("<a href=\"$breadcrumbHref\">${it.breadcrumbName}</a>")
            } else {
                lines.add("${it.breadcrumbName}")
            }
            lines.add(" > ")
        }
        lines.add("</div>\n")
    }
}

private fun translateNextPrev(trgFile: File, lines: MutableList<String>, page: Page) {
    val navigation = page.dir.navigation
    if (navigation != null) {
        var curPageIndex: Int? = null
        for (i in 0 until navigation.linksTo.size) {
            if (navigation.linksTo[i].page == page) {
                curPageIndex = i
                break
            }
        }
        if (curPageIndex == null) return
        val prevPage = navigation.linksTo.getOrNull(curPageIndex - 1)?.page
        val nextPage = navigation.linksTo.getOrNull(curPageIndex + 1)?.page
        lines.add("<div class=\"next_prev\">")
        if (prevPage != null) {
            val href = relatePathToCurrentFile(trgFile, trgPath(prevPage))
            lines.add("<a href=\"$href\" class=\"prev\"><- Назад</a>")
        }
        if (nextPage != null) {
            val href = relatePathToCurrentFile(trgFile, trgPath(nextPage))
            lines.add("<a href=\"$href\" class=\"next\">Далее -></a>")
        }
        lines.add("</div>")
    }
}

private fun translateFooter(trgFile: File, lines: MutableList<String>, page: Page) {
    val footer = page.dir.footer
    if (footer != null) {
        lines.add("<div class=\"footer\">\n")
        footer.blocks.forEach { translateBlock(trgFile, lines, it) }
        lines.add("</div>\n")
    }
}

private fun translateBlock(trgFile: File, lines: MutableList<String>, block: Block) {
    when (block) {
        is TableBlock -> {
            lines.add("<div class=\"table\">\n")
            lines.add("<table>\n")
            translateTableRow(trgFile, lines, block.header, true)
            block.rows.forEach { translateTableRow(trgFile, lines, it, false) }
            lines.add("</table>\n")
            lines.add("</div>\n")
        }
        is QuoteBlock -> {
            lines.add("<div class=\"quote_block\">\n")
            block.subBlocks.forEach { translateBlock(trgFile, lines, it) }
            lines.add("\n</div>\n")
        }
        is HeaderBlock -> {
            lines.add("<div class=\"header_block\">\n")
            lines.add("<h${block.headerLvl}>")
            block.textElements.forEach { translateTextElement(trgFile, lines, it) }
            lines.add("</h${block.headerLvl}>\n")
            lines.add("</div>\n")
        }
        is ListBlock -> {
            val tag = if (block.ordered) "ol" else "ul"
            lines.add("<$tag>\n")
            for (item in block.items) {
                lines.add("<li>\n")
                item.subBlocks.forEach { translateBlock(trgFile, lines, it) }
                lines.add("</li>\n")
            }
            lines.add("</$tag>\n")
        }
        is PlainBlock -> {
            lines.add("<div class=\"plain_block\">\n")
            block.textElements.forEach { translateTextElement(trgFile, lines, it) }
            lines.add("\n</div>\n")
        }
    }
}

private fun translateTableRow(trgFile: File, lines: MutableList<String>, tableHeader: TableRow, isHeader: Boolean = false) {
    val tag = if (isHeader) "th" else "td"
    lines.add("<tr>\n")
    for (cell in tableHeader.cells) {
        lines.add("<$tag>")
        cell.textElements.forEach { translateTextElement(trgFile, lines, it) }
        lines.add("</$tag>\n")
    }
    lines.add("</tr>\n")
}

private fun translateTextElement(trgFile: File, lines: MutableList<String>, textElement: TextElement) {
    when (textElement) {
        BOLD_START -> lines.add("<b>")
        BOLD_FINISH -> lines.add("</b>")
        ITALIC_START -> lines.add("<i>")
        ITALIC_FINISH -> lines.add("</i>")
        STRIKE_START -> lines.add("<s>")
        STRIKE_FINISH -> lines.add("</s>")
        CODE_START -> lines.add("<span class=\"code\">")
        CODE_FINISH -> lines.add("</span>")
        is PageLink -> {
            if (textElement.page == null) return
            val relPath = relatePathToCurrentFile(trgFile, trgPath(textElement.page!!))
            if (textElement.image) {
                lines.add("<a href=\"${relPath}\" target=_blank class=\"image\"> <img src=\"${relPath}\" alt=\"${textElement.text}\"/> </a>")
            } else {
                lines.add("<a href=\"$relPath\">${textElement.text}</a>")
            }
        }
        is WebLink -> lines.add("<a href=\"${textElement.href}\">${textElement.text}</a>")
        is PlainText -> lines.add(textElement.content)
    }
}

fun trgPath(dir: Dir): Path {
    val relativeSrcPath = dir.context.srcAbsolutePath.relativize(dir.srcPath)
    return dir.context.trgAbsolutePath.resolve(relativeSrcPath)
}

fun trgPath(page: Page): Path {
    val relativeSrcPath = page.context.srcAbsolutePath.relativize(page.srcPath)
    return if (page.isMd) {
        val pathToMd = page.context.trgAbsolutePath.resolve(relativeSrcPath)
        val nameWithHtml = page.srcPath.fileName.toString().replaceAfterLast(".", "html")
        val pathToHtml = pathToMd.parent.resolve(nameWithHtml)
        pathToHtml
    } else {
        page.context.trgAbsolutePath.resolve(relativeSrcPath)
    }
}

fun relatePathToCurrentFile(currentFile: File, path: Path): Path {
    return currentFile.toPath().parent.relativize(path)
}