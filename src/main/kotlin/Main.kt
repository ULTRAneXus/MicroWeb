package mw

import mw.framework.*
import java.io.File

//paths based on repository root
const val TEXT_DIR = "src/main/resources/pages/"
const val IMG_DIR = "src/main/resources/images/"
const val STYLE_DIR = "src/main/resources/style/"
const val OUTPUT_DIR = "out/"

fun main() {
//    whatDidICook()
    val parser = PageParser()
    parser.registerCustomAttributeAction(customAttributeAction)
    parser.registerCustomTextParseAction(customTextParseAction)

    val rawPages = File(TEXT_DIR).walk().filter { it.name.endsWith(".txt") }.toList().map { parser.parsePage(it) }
    val textPages = rawPages.map {
        Pair(
            it.customAttributes["week"]?.toInt() ?: 0, TextPage(
                "weeks.css",
                "icon.webp",
                "Week ${it.customAttributes["week"]}",
                listOf(LinkWidget("index.html", "Home")),
                it
            )
        )
    }.toMutableList()
    val sortedPages = textPages.sortedBy { it.first }.map { it.second }

    val index = sortedPages.withIndex().map { LinkWidget("${it.index}.html", "${it.value.pageTitle}") }

    val homePage = ContentPage(
        "index.css", "icon.webp", "maxWFN", content = listOf(
            TitleWidget("Hello this is Max's weekly friendship newsletter", 1), TextWidget(
                listOf(
                    "v2.4",
                    "Contact me in case something is broken/I messed up/you want to give feedback: <a href=\"mailto:simon@greve.email\">simon@greve.email</a>"
                )
            ), IndexWidget(index)
        )
    )

    sortedPages.reversed().forEach { page ->
        run {
            page.title?.let { homePage.content.add(page.title!!) }
            page.content?.let { homePage.content.add(page.content!!) }
            page.image?.let { homePage.content.add(page.image!!) }
        }
    }

    if (!File(OUTPUT_DIR).exists() && !File(OUTPUT_DIR).mkdir()) throw Exception("Could not create output directory")

    File(OUTPUT_DIR + "index.html").writeText(homePage.build().joinToString("\n"))
    sortedPages.withIndex()
        .forEach { File(OUTPUT_DIR + "${it.index}.html").writeText(it.value.build().joinToString("\n")) }
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

val customTextParseAction: (List<String>) -> List<String> = { l: List<String> ->
    l.map {
        it.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;")
            .replace("'", "&apos;")
    }
}

//fun whatDidICook() {
//
//    if (!File(OUTPUT_DIR).exists() && !File(OUTPUT_DIR).mkdir()) throw Exception("Could not create output directory")
//
//    //landing page
//    val index = Page("index").apply {
//        style = "index"
//        content += TextWidget(
//            title = "Hello this is Max's weekly friendship newsletter", content = listOf(
//                "v2.3",
//                "Contact me in case something is broken/I messed up/you want to give feedback: <a href=\"mailto:simon@greve.email\">simon@greve.email</a>"
//            ), escapeSpecialCharacters = false
//        )
//        title = "maxWFN"
//    }
//
//    //pages of individual weeks
//    val textFiles = File(TEXT_DIR).walk().filter { it.name.endsWith(".txt") }.toList()
//    val weeks: MutableList<Page> = mutableListOf()
//    for (text in textFiles) {
//        val newPage = Page(text.name.removeSuffix(".txt")).apply {
//            content += mw.oldWidgets.LinkWidget("index.html", "Home")
//            content += LineBreakWidget()
//            content += TextWidget(name, text.readLines())
//            val animal: String? = if (text.name == "Week 1.txt") null else {
//                text.name.removeSuffix(".txt").split("-", limit = 2).last().split(',').lastOrNull()?.trim()
//            }
//            if (animal != null && File("$IMG_DIR$animal.webp").exists()) {
//                content += LineBreakWidget()
//                content += ImageWidget("$animal.webp", animal)
//            }
//            style = "weeks"
//            title = text.name.split('-').firstOrNull()?.trim()
//        }
//        weeks.add(newPage)
//    }
//
//    //sort pages by number in title, dangerous but works :)
//    weeks.sortBy { page -> page.name.filter { it.isDigit() }.toIntOrNull() }
//
//    //index of all weeks
//    val pageIndex: MutableList<Pair<String, String>> = mutableListOf()
//    weeks.forEach { pageIndex += Pair(it.name, "${it.name}.html") }
//    index.content += IndexWidget(pageIndex)
//
//    //add content of all weeks to landing page, without home button
//    weeks.reversed().forEach { week ->
//        index.content += week.content.dropWhile { it is LinkWidget || it is LineBreakWidget }
//    }
//
//    //generate html to output dir
//    renderTo(weeks + index, OUTPUT_DIR)
//
//    //copy images to output dir
//    File(IMG_DIR).walk().filter { it.name.endsWith(".webp") || it.name == "icon.png" }
//        .forEach { it.copyTo(File(OUTPUT_DIR + it.name), overwrite = true) }
//
//    //copy css to output dir
//    File(STYLE_DIR).walk().filter { it.name.endsWith(".css") }.forEach {
//        it.copyTo(
//            File(OUTPUT_DIR + it.name), overwrite = true
//        )
//    }
//}
//
//data class Page(val name: String) {
//    val content: MutableList<Widget> = mutableListOf()
//    var style: String? = null
//    var title: String? = null
//}
//
//fun buildPage(page: Page, includeFooter: Boolean = true): List<String> {
//    val result = mutableListOf<String>()
//    result += "<!DOCTYPE html>\n" + "<html lang=\"en\">\n" + "<head>\n" + "<meta content=\"text/html\" charset=\"UTF-8\">\n" + "${
//        page.style?.let {
//            "<link href=\"${page.style}.css\" rel=\"stylesheet\">"
//        }
//    }\n" + "<title>${page.title ?: page.name}</title>\n" + "<link rel=\"icon\" href=\"icon.png\">\n" + "</head>\n" + "<body>\n" + "<main>"
//
//    page.content.forEach { result += it.getHTML() }
//
//    if (includeFooter) result += "<p>Built with <a href=\"https://github.com/ULTRAneXus/MicroWeb\">MicroWeb</a></p>"
//    result += "</main>\n" + "</body>\n" + "</html>\n"
//    return result
//}
//
//fun renderTo(pages: List<Page>, path: String) {
//    for (page in pages) {
//        val out = File(path + page.name + ".html")
//        out.writeText(buildPage(page).joinToString("\n"))
//    }
//}