package com.das.webcrawlertools

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.util.logging.Logger
import kotlin.coroutines.coroutineContext


object WebCrawlerTools{

    private var client: OkHttpClient? = null


    /**
     * Returns the Singleton Instance of OkHttpclient
     */
    fun getWebClient(): OkHttpClient {
        if (client == null) {
            client = buildClient()
        }

        return client!!
    }


    /**
     * Attempts to retrieve a webpage using a GET request on the url. Uses a Random User-Agent each time.
     */
    fun getWebpage(url: String): String? {

        val call = getWebClient().newCall(
            Request.Builder()
                .url(url)
                .header("user-agent", getRandomUserAgent())
                .get()
                .build()
        )

        val resp = call.execute()

        if (!resp.isSuccessful) {
            Log.w(
                "NETWORK", "getWebpage call failed for url: $url." +
                        " Response Code: ${resp.code}"
            )
            return null
        }

        return resp.body?.string()
    }


    /**
     * Attempts to retrieve webpage using async GET request on url. Won't waste system resources while waiting for
     * web page.
     */
    suspend fun getWebpageAsync(url: String): String? {

        val call = getWebClient().newCall(
            Request.Builder()
                .url(url)
                .header("user-agent", getRandomUserAgent())
                .get()
                .build()
        )

        // Launch async call
        val deferredResp = CoroutineScope(coroutineContext).async(Dispatchers.Unconfined) {
            call.execute()
        }

        // Await
        val resp = deferredResp.await()

        if (!resp.isSuccessful) {
            Log.w(
                "NETWORK", "getWebpageAsync call failed for url: $url." +
                        " Response Code: ${resp.code}"
            )
            return null
        }

        return resp.body?.string()
    }


    /**
     * Randomly Selects a User-Agent from one of six.
     */
    fun getRandomUserAgent(): String {
        val userAgents = listOf(
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko)" +
                    " Chrome/58.0.3029.110 Safari/537.36",
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:53.0) Gecko/20100101 Firefox/53.0",

            "Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.0; Trident/5.0; Trident/5.0)",

            "Mozilla/5.0 (compatible; MSIE 10.0; Windows NT 6.2; Trident/6.0; MDDCJS)",

            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko)" +
                    " Chrome/51.0.2704.79 Safari/537.36 Edge/14.14393",

            "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1)"
        )

        return userAgents[(0..5).random()]
    }


    /**
     * Retrieves the webpage with given url using random User-Agent, parses it into a Jsoup Document if possible.
     */
    fun parseUrl(url: String): Document?{
        val webPage = getWebpage(url) ?: return null

        return Jsoup.parse(webPage)
    }


    /**
     * Retrieves the webpage with given url using random User-Agent, parses it into a Jsoup Document if possible.
     * Does it Asynchronously, so CPU cycles are not wasted.
     */
    suspend fun parseUrlAsync(url: String): Document?{
        val webPage = getWebpageAsync(url) ?: return null

        return Jsoup.parse(webPage)
    }


    /**
     * Used to initially build the OkHttpClient. Only called once during program life.
     */
    private fun buildClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .retryOnConnectionFailure(true)
            .followRedirects(true)      // VERY IMPORTANT FOR ANDROID
            .followSslRedirects(true)       // ALSO MAYBE
            .build()
    }
}
