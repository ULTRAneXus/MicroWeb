package mw.framework

import java.io.File

class PageParser {

    private var customAttributeAction: (List<String>, MutableMap<String, String>) -> Unit =
        { l: List<String>, m: MutableMap<String, String> ->
            l.forEach { tag ->
                if (!tag.contains(":")) {
                    if (tag.trim() != "") println("unrecognized tag: ${tag.trim()}")
                    return@forEach
                }
                val parsedTag = tag.split(":", limit = 2)
                m[parsedTag.first().trim()] = parsedTag.last().trim()
            }
        }

    /**
     * custom attribute action will receive:
     *     - a list of all line containing custom attributes List<String>
     *     - the MutableMap of the RawPage containing custom attributes MutableMap<String, String>
     */
    fun registerCustomAttributeAction(customAttributeAction: (List<String>, MutableMap<String, String>) -> Unit) {
        this.customAttributeAction = customAttributeAction
    }

    fun parsePage(file: File): RawPage {
        val page = RawPage()
        val rawContent = file.readLines()
        val firstMarker = rawContent.withIndex().firstOrNull { it.value.trim() == "===" }
        if (firstMarker == null) {
            page.content.addAll(rawContent)
            return page
        }

        rawContent.dropLast(rawContent.size - firstMarker.index).forEach { tag ->
            if (!tag.contains(":")) {
                if (tag.trim() != "") println("unrecognized tag: ${tag.trim()} in ${file.path}")
                return@forEach
            }
            val parsedTag = tag.split(":", limit = 2)

            when (parsedTag.first().trim()) {
                "title" -> page.title = parsedTag.last().trim()
                "stylesheet" -> page.stylesheet = parsedTag.last().trim()
                else -> println("unrecognized tag: ${parsedTag.first().trim()} in ${file.path}")
            }
        }

        val secondMarker = rawContent.withIndex().last { it.value.trim() == "===" }
        page.content.addAll(rawContent.drop(secondMarker.index + 1))

        if (firstMarker.index != secondMarker.index) {
            val customAttributes = rawContent.drop(firstMarker.index + 1).dropLast(rawContent.size - secondMarker.index)
            customAttributeAction(customAttributes, page.customAttributes)
        }

        return page
    }

    data class RawPage(
        var title: String = "",
        var stylesheet: String = "",
        val content: MutableList<String> = mutableListOf(),
        val customAttributes: MutableMap<String, String> = mutableMapOf()
    )
}