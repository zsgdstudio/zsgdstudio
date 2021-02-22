package builder.model

import builder.util.Flow
import builder.util.absoluteNormal
import java.io.File
import java.nio.file.Path

class Page(val context: Context, val dir: Dir, srcFile: File) {
    val srcPath: Path = srcFile.toPath().absoluteNormal()
    val isMd = srcFile.name.endsWith(".md")
    val blocks = ArrayList<Block>()
    val linksTo = ArrayList<PageLink>()
    var reached = false
        private set

    init {
        check(!srcFile.isDirectory) {"${srcFile.name} is not directory"}

        if (isMd) {
            val lineFlow = Flow(srcFile.readLines().toMutableList())
            blocks.addAll(blocks(context, this, lineFlow))
        }

        context.addPage(this)
    }

    fun reach() {
        if (this.reached) return
        this.reached = true
        this.linksTo.forEach { it.page?.reach() }
        this.dir.reach()
    }
}