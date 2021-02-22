package builder.model

import builder.util.absoluteNormal
import java.io.File
import java.nio.file.Path

class Dir(val context: Context, val parentDir: Dir?, srcFile: File) {
    val srcPath: Path = srcFile.toPath().absoluteNormal()
    val dirs = ArrayList<Dir>()
    val pages = ArrayList<Page>()
    var index: Page? = null
        private set
    var reached = false
        private set

    var style: Page? = null
        get() {
            return if (field == null) parentDir?.style else field
        }
        private set

    var header: Page? = null
        get() {
            return if (field == null) parentDir?.header else field
        }
        private set

    var navigation: Page? = null
        get() {
            return if (field == null) parentDir?.navigation else field
        }
        private set

    var breadcrumbName: String? = null
        private set

    var footer: Page? = null
        get() {
            return if (field == null) parentDir?.footer else field
        }
        private set

    init {
        check(srcFile.isDirectory) {"${srcFile.name} is not directory"}

        for (file in srcFile.listFiles().orEmpty()) {
            when {
                file.isDirectory -> dirs.add(Dir(context, this, file))
                file.name == "dir.md" -> breadcrumbName = file.readLines().elementAtOrNull(0) ?: ""
                file.name == "style.css" -> style = Page(context, this, file)
                file.name == "header.md" -> header = Page(context, this, file)
                file.name == "navigation.md" -> navigation = Page(context, this, file)
                file.name == "footer.md" -> footer = Page(context, this, file)
                else -> {
                    val page = Page(context, this, file)
                    pages.add(page)
                    if (file.name == "index.md" || file.name == "index.html") this.index = page
                }
            }
        }
    }

    fun reach() {
        if (this.reached) return
        this.reached = true
        this.style?.reach()
        this.header?.reach()
        this.navigation?.reach()
        this.footer?.reach()
        this.parentDir?.reach()
    }
}