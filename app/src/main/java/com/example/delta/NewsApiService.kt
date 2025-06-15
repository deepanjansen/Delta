package com.example.delta

import retrofit2.http.GET
import retrofit2.http.Query

data class NewsResponse(val articles: List<Article>)
data class Article(
    val title: String,
    val source: String,
    val url: String
)
data class Source(val name: String)

interface NewsApiService {
    @GET("top-headlines")
    suspend fun getTopHeadlines(
        @Query("country") country: String = "in",
        @Query("apiKey") apiKey: String
    ): NewsResponse
}