package mw

import mw.framework.PageParser
import mw.framework.TextPage
import mw.oldWidgets.*
import java.io.File

//paths based on repository root
const val TEXT_DIR = "src/main/resources/pages_old/"
const val IMG_DIR = "src/main/resources/images/"
const val STYLE_DIR = "src/main/resources/style/"
const val OUTPUT_DIR = "out/"

fun main() {
//    whatDidICook()
    val parser = PageParser()
    parser.registerCustomAttributeAction(customAttributeAction)
    val rawPage = parser.parsePage(File("src/main/resources/pages/16.txt"))
    println(rawPage)
    val page = TextPage("weeks.css", "icon.webp", "Week 16", rawPage)

    if (!File(OUTPUT_DIR).exists() && !File(OUTPUT_DIR).mkdir()) throw Exception("Could not create output directory")

    val out = File(OUTPUT_DIR + "Week 16.html")
    out.writeText(page.build().joinToString("\n"))
}

val customAttributeAction: (List<String>, MutableMap<String, String>) -> Unit =
    { l: List<String>, m: MutableMap<String, String> ->
        l.forEach { tag ->
            if (!tag.contains(":")) {
                if (tag.trim() != "") println("unrecognized tag: ${tag.trim()}")
                return@forEach
            }
            val parsedTag = tag.split(":", limit = 2)
            when (parsedTag.first().trim()) {
                "week" -> m["week"] = parsedTag.last().trim()
                "language" -> m["language"] = parsedTag.last().trim()
                "delay" -> m["delay"] = parsedTag.last().trim()
                "animals" -> m["animals"] = parsedTag.last().trim()
                "images" -> m["images"] = parsedTag.last().trim()
                else -> println("unrecognized tag: ${parsedTag.first().trim()}")
            }
        }
    }

fun whatDidICook() {

    if (!File(OUTPUT_DIR).exists() && !File(OUTPUT_DIR).mkdir()) throw Exception("Could not create output directory")

    //landing page
    val index = Page("index").apply {
        style = "index"
        content += TextWidget(
            title = "Hello this is Max's weekly friendship newsletter", content = listOf(
                "v2.3",
                "Contact me in case something is broken/I messed up/you want to give feedback: <a href=\"mailto:simon@greve.email\">simon@greve.email</a>"
            ), escapeSpecialCharacters = false
        )
        title = "maxWFN"
    }

    //pages of individual weeks
    val textFiles = File(TEXT_DIR).walk().filter { it.name.endsWith(".txt") }.toList()
    val weeks: MutableList<Page> = mutableListOf()
    for (text in textFiles) {
        val newPage = Page(text.name.removeSuffix(".txt")).apply {
            content += LinkWidget("index.html", "Home")
            content += LineBreakWidget()
            content += TextWidget(name, text.readLines())
            val animal: String? = if (text.name == "Week 1.txt") null else {
                text.name.removeSuffix(".txt").split("-", limit = 2).last().split(',').lastOrNull()?.trim()
            }
            if (animal != null && File("$IMG_DIR$animal.webp").exists()) {
                content += LineBreakWidget()
                content += ImageWidget("$animal.webp", animal)
            }
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
    weeks.reversed().forEach { week ->
        index.content += week.content.dropWhile { it is LinkWidget || it is LineBreakWidget }
    }

    //generate html to output dir
    renderTo(weeks + index, OUTPUT_DIR)

    //copy images to output dir
    File(IMG_DIR).walk().filter { it.name.endsWith(".webp") || it.name == "icon.png" }
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
    result += "<!DOCTYPE html>\n" + "<html lang=\"en\">\n" + "<head>\n" + "<meta content=\"text/html\" charset=\"UTF-8\">\n" + "${
        page.style?.let {
            "<link href=\"${page.style}.css\" rel=\"stylesheet\">"
        }
    }\n" + "<title>${page.title ?: page.name}</title>\n" + "<link rel=\"icon\" href=\"icon.png\">\n" + "</head>\n" + "<body>\n" + "<main>"

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