package builder.model

import java.nio.file.Path

class PageLink(context: Context, curPage: Page, text: String, image: Boolean, pagePath : Path) : Link(text, image) {
    var page: Page? = null

    init {
        curPage.linksTo.add(this)
        context.waitForPage(pagePath) {
            this.page = it
        }
    }
}