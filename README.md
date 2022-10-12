<h2>Basic Library used as a tool kit for Kotlin Web Scraping.
</h2>
<h3>Dependencies Included:</h3>
- Jsoup
- Gson
- OkHttp

<h3> Basic Usage </h3>

```kotlin

// The WebCrawlerTools Object provides the tools you will use.
// This method will retrieve the webpage (using random user-agents) + parse it into
// a Jsoup Document
val parsedWebPage: Document? = WebCrawlerTools.parseUrl("https://www.google.com")


```