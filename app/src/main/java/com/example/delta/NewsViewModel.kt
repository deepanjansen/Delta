package com.example.delta

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import androidx.compose.runtime.mutableStateListOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject

class NewsViewModel : ViewModel() {
    private val repository = NewsRepository()
    var articles = mutableStateListOf<Article>()
    private set

    suspend fun fetchNews(apiKey: String): List<Article> = withContext(Dispatchers.IO) {
        try {
            val url = "https://newsapi.org/v2/top-headlines?category=general&apiKey=$apiKey"
//            val url = "https://newsapi.org/v2/top-headlines?country=us&category=general&apiKey=$apiKey"


            val client = OkHttpClient()
            val request = Request.Builder().url(url).build()
            val response = client.newCall(request).execute()

            if (!response.isSuccessful) {
                Log.e("NewsFetcher", "Failed: ${response.code}")
                return@withContext emptyList()
            }

            val rawJson = response.body?.string() ?: ""
            Log.d("NewsFetcher", "Raw JSON: $rawJson")

            val json = JSONObject(rawJson)
            val articlesJson = json.getJSONArray("articles")


            Log.d("NewsFetcher", "Fetched ${articlesJson.length()} articles")
            List(articlesJson.length()) { i ->
                val article = articlesJson.getJSONObject(i)
                val source = article.getJSONObject("source").optString("name", "Unknown")
                Article(
                    title = article.optString("title", "No Title"),
                    source = source,
                    url = article.optString("url", "")
                )
            }
        } catch (e: Exception) {
            Log.e("NewsFetcher", "Error: ${e.message}", e)
            emptyList()
        }
    }

}
