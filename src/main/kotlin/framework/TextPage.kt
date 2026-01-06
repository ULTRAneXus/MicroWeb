package mw.framework

class TextPage {

    var stylesheet: String? = null
    var icon: String? = null
    var pageTitle: String? = null
    val header: MutableList<Widget> = mutableListOf()
    var title: TitleWidget? = null
    var content: TextWidget? = null
    var image: ImageWidget? = null
    val footer: MutableList<Widget> = mutableListOf()

    constructor(
        stylesheet: String? = null,
        icon: String? = null,
        pageTitle: String? = null,
        header: List<Widget> = listOf(),
        title: TitleWidget? = null,
        content: TextWidget? = null,
        image: ImageWidget? = null,
        footer: List<Widget> = listOf()
    ) {
        this.stylesheet = stylesheet
        this.icon = icon
        this.pageTitle = pageTitle
        this.header.addAll(header)
        this.title = title
        this.content = content
        this.image = image
        this.footer.addAll(footer)
    }

    constructor(
        stylesheet: String? = null,
        icon: String? = null,
        pageTitle: String? = null,
        header: List<Widget> = listOf(),
        rawPage: PageParser.RawPage,
        footer: List<Widget> = listOf()
    ) {
        this.stylesheet = stylesheet
        this.icon = icon
        this.pageTitle = pageTitle ?: rawPage.title
        this.header.addAll(header)
        rawPage.title?.let { title = TitleWidget(it, 1) }
        rawPage.image?.let { image = ImageWidget(it, it) }
        content = TextWidget(rawPage.content)
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
        title?.let { res.addAll(it.getHTML()) }
        content?.let { res.addAll(it.getHTML()) }
        image?.let { res.addAll(it.getHTML()) }
        res.addAll(footer.flatMap { it.getHTML() })

        res += "</main>"
        res += "</body>"
        res += "</html>"

        return res
    }
}