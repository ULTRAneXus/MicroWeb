package mw

import java.io.File

const val TEXT_DIR = "src/main/resources/pages/"
const val IMG_DIR = "src/main/resources/images/"
const val STYLE_DIR = "src/main/resources/style/"
const val OUTPUT_DIR = "out/"

fun main() {

    if (!File(OUTPUT_DIR).exists() && !File(OUTPUT_DIR).mkdir()) throw Exception("Could not create output directory")

    //landing page
    val index = Page("index").apply {
        style = "index"
        content += TextWidget("helo this is maxs weekly friendship newsletter", listOf("v2.0"))
        title = "maxWFN"
    }

    //pages of individual weeks
    val textFiles = File(TEXT_DIR).walk().filter { it.name.endsWith(".txt") }.toList()
    val weeks: MutableList<Page> = mutableListOf()
    for (text in textFiles) {
        val newPage = Page(text.name.removeSuffix(".txt")).apply {
            content += LinkWidget("index.html", "Home")
            content += TextWidget(name, text.readLines())
            val animal: String? = if (text.name == "Week 1.txt") null else {
                text.name.removeSuffix(".txt").split("-").last().split(',').lastOrNull()?.trim()
            }
            if (animal != null && File("$IMG_DIR$animal.png").exists()) content += ImageWidget("$animal.png", animal)
            style = "weeks"
            title = text.name.split('-').firstOrNull()?.trim()
        }
        weeks.add(newPage)
    }

    //sort pages by number in title, dangerous but works :)
    weeks.sortBy { page -> page.name.filter { it.isDigit() }.toIntOrNull() }

    //index of all weeks
    val pageIndex: MutableList<Pair<String, String>> = mutableListOf()
    weeks.forEach { pageIndex += Pair(it.name, "${it.name}.html") }
    index.content += IndexWidget(pageIndex)

    //add content of all weeks to landing page, without home button
    weeks.reversed().forEach { index.content += it.content.filter { widget -> widget !is LinkWidget } }

    //generate html to output dir
    renderTo(weeks + index, OUTPUT_DIR)

    //copy images to output dir
    File(IMG_DIR).walk().filter { it.name.endsWith(".png") }
        .forEach { it.copyTo(File(OUTPUT_DIR + it.name), overwrite = true) }

    //copy css to output dir
    File(STYLE_DIR).walk().filter { it.name.endsWith(".css") }.forEach {
        it.copyTo(
            File(OUTPUT_DIR + it.name), overwrite = true
        )
    }
}

data class Page(val name: String) {
    val content: MutableList<Widget> = mutableListOf()
    var style: String? = null
    var title: String? = null
}

fun buildPage(page: Page, includeFooter: Boolean = true): List<String> {
    val result = mutableListOf<String>()
    result += "<!DOCTYPE html>\n" + "<html lang=\"en\">\n" + "<head>\n" + "<meta content=\"text/html\" charset=\"UTF-8\">\n" + "${page.style?.let { "<link href=\"${page.style}.css\" rel=\"stylesheet\">" }}\n" + "<title>${page.title ?: page.name}</title>\n" + "</head>\n" + "<body>\n" + "<main>"

    page.content.forEach { result += it.getHTML() }

    if (includeFooter) result += "<p>Built with <a href=\"https://github.com/ULTRAneXus/MicroWeb\">MicroWeb</a></p>"
    result += "</main>\n" + "</body>\n" + "</html>\n"
    return result
}

fun renderTo(pages: List<Page>, path: String) {
    for (page in pages) {
        val out = File(path + page.name + ".html")
        out.writeText(buildPage(page).joinToString("\n"))
    }
}