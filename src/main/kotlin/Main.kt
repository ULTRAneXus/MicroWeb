package mw

import java.io.File

const val TEXT_DIR = "src/main/resources/pages/"
const val IMG_DIR = "src/main/resources/images/"
const val STYLE_DIR = "src/main/resources/style/"
const val OUTPUT_DIR = "out/"

fun main() {

    if (!File(OUTPUT_DIR).exists() && !File(OUTPUT_DIR).mkdir()) throw Exception("Could not create directory")

    val textFiles = File(TEXT_DIR).walk().filter { it.name.endsWith(".txt") }.toList()

    val index = Page("index")
    index.style = "index"

    val all = Page("all")
    all.style = "all"

    val weeks: MutableList<Page> = mutableListOf()

    for (text in textFiles) {
        val newPage = Page(text.name.removeSuffix(".txt"))
        newPage.content += LinkWidget("index.html", "Home")
        newPage.content += TextWidget(newPage.title, text.readLines())
        val animal: String? = if (text.name == "Week 1.txt") null else {
            text.name.removeSuffix(".txt").split("-").last().split(',').lastOrNull()?.trim()
        }
        if (animal != null) newPage.content += ImageWidget("$animal.png", animal)
        newPage.style = "weeks"
        weeks.add(newPage)
    }

    val pageIndex: MutableList<Pair<String, String>> = mutableListOf()
    weeks.forEach { pageIndex += Pair(it.title, "${it.title}.html") }
    index.content += IndexWidget(pageIndex)

    render(weeks + index + all)

    File(IMG_DIR).walk().filter { it.name.endsWith(".png") }
        .forEach { it.copyTo(File(OUTPUT_DIR + it.name), overwrite = true) }
    File(STYLE_DIR).walk().filter { it.name.endsWith(".css") }.forEach {
            it.copyTo(
                File(OUTPUT_DIR + it.name), overwrite = true
            )
        }
}

data class Page(val title: String) {
    val content: MutableList<Widget> = mutableListOf()
    var style: String? = null
}

fun buildPage(page: Page): List<String> {
    val result = mutableListOf<String>()
    result += "<!DOCTYPE html>" + "<html lang=\"en\">" + "<head>" + "<meta content=\"text/html\" charset=\"UTF-8\">" + "${page.style?.let { "<link href=\"${page.style}.css\" rel=\"stylesheet\">" }}" + "<title>${page.title}</title>" + "</head>" + "<body>" + "<main>"

    page.content.forEach { result += it.getHTML() }

    result += "</main>" + "</body>" + "</html>"
    return result
}

fun render(pages: List<Page>) {
    for (page in pages) {
        val out = File(OUTPUT_DIR + page.title + ".html")
        out.writeText(buildPage(page).joinToString("\n"))
    }
}