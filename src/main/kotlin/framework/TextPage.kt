package mw.framework

var stylesheet: String? = null
var icon: String? = null
var pageTitle: String? = null
var title: TitleWidget? = null
var content: TextWidget? = null
var image: ImageWidget? = null

class TextPage {

    //TODO: add support for headers and footers

    constructor(
        stylesheet: String? = null,
        icon: String? = null,
        pageTitle: String? = null,
        title: TitleWidget? = null,
        content: TextWidget? = null,
        image: ImageWidget? = null
    ) {
        mw.framework.stylesheet = stylesheet
        mw.framework.icon = icon
        mw.framework.pageTitle = pageTitle
        mw.framework.title = title
        mw.framework.content = content
        mw.framework.image = image
    }

    constructor(
        stylesheet: String? = null, icon: String? = null, pageTitle: String? = null, rawPage: PageParser.RawPage
    ) {
        mw.framework.stylesheet = stylesheet
        mw.framework.icon = icon
        mw.framework.pageTitle = pageTitle ?: rawPage.title
        title = TitleWidget(rawPage.title, 1)
        content = TextWidget(rawPage.content)
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

        title?.let { res.addAll(it.getHTML()) }
        content?.let { res.addAll(it.getHTML()) }
        image?.let { res.addAll(it.getHTML()) }

        res += "</main>"
        res += "</body>"
        res += "</html>"

        return res
    }
}