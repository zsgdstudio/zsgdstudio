package builder.model

import builder.util.absoluteNormal
import java.io.File
import java.nio.file.Path

class Context(srcPath: String, trgPath: String, entryPointPath: String) {
    val srcAbsolutePath: Path
    val trgAbsolutePath: Path
    val rootDir: Dir
    val warnings: MutableList<Warning> = ArrayList()

    private val pagesByPath: MutableMap<Path, Page> = HashMap()
    private val pageWaiters: MutableMap<Path, MutableList<(Page)->Unit>> = HashMap()

    init {
        val srcFile = File(srcPath)
        check(srcFile.exists() && srcFile.isDirectory) { "src file don't exist or is not directory $srcPath" }
        this.srcAbsolutePath = srcFile.toPath().absoluteNormal()

        val trgFile = File(trgPath)
        this.trgAbsolutePath = trgFile.toPath().absoluteNormal()

        val entryPoint = File(entryPointPath).takeIf { it.exists() }
                ?: File(srcPath + File.separator + entryPointPath)
        check(entryPoint.exists()) {"entry point file don't exist $entryPointPath"}
        val entryPointAbsolutePath = entryPoint.toPath().absoluteNormal()

        if (trgFile.exists()) {
            trgFile.deleteRecursively()
            trgFile.mkdir()
        }

        rootDir = Dir(this, null, srcFile)

        pageWaiters.forEach {
            if (it.value.isNotEmpty()) this.addWarning("link unresolved", "${it.key}")
        }

        val entryPage = pagesByPath[entryPointAbsolutePath]
        check(entryPage != null) {"entry point page not parsed"}
        entryPage.reach()
    }

    fun waitForPage(srcPath: Path, onReady: (Page)->Unit) {
        val page = pagesByPath[srcPath]
        if (page != null) onReady(page)
        else {
            pageWaiters.putIfAbsent(srcPath, ArrayList())
            pageWaiters[srcPath]!!.add(onReady)
        }
    }

    fun addPage(page: Page) {
        pagesByPath[page.srcPath] = page
        pageWaiters[page.srcPath]?.forEach { it(page) }
        pageWaiters.remove(page.srcPath)
    }

    fun addWarning(message: String, atLine: String) {
        this.warnings.add(Warning(message, atLine))
    }
}