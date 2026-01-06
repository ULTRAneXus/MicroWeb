package framework

import mw.framework.Widget


class ContentPage {

    var stylesheet: String? = null
    var icon: String? = null
    var pageTitle: String? = null
    val header: MutableList<Widget> = mutableListOf()
    var content: MutableList<Widget> = mutableListOf()
    val footer: MutableList<Widget> = mutableListOf()

    constructor(
        stylesheet: String? = null,
        icon: String? = null,
        pageTitle: String? = null,
        header: List<Widget> = listOf(),
        content: List<Widget> = listOf(),
        footer: List<Widget> = listOf()
    ) {
        this.stylesheet = stylesheet
        this.icon = icon
        this.pageTitle = pageTitle
        this.header.addAll(header)
        this.content.addAll(content)
        this.footer.addAll(footer)
    }

    fun build(): List<String> {
        val res = mutableListOf<String>()

        res += "<!DOCTYPE html>"
        res += "<html lang=\"en\">"
        res += "<head>"
        res += "<meta content=\"text/html\" charset=\"UTF-8\">"

        stylesheet?.let { res += "<link href=\"$it\" rel=\"stylesheet\">" }
        icon?.let { res += "<link rel=\"icon\" href=\"$it\">" }
        pageTitle?.let { res += "<title>$it</title>" }

        res += "</head>"
        res += "<body>"
        res += "<main>"

        res.addAll(header.flatMap { it.getHTML() })
        res.addAll(content.flatMap { it.getHTML() })
        res.addAll(footer.flatMap { it.getHTML() })

        res += "</main>"
        res += "</body>"
        res += "</html>"

        return res
    }
}