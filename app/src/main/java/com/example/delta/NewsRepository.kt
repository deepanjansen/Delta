package com.example.delta


class NewsRepository {
    suspend fun getTopHeadlines(apiKey: String): NewsResponse {
        return NewsApiClient.api.getTopHeadlines(apiKey = apiKey)
    }
}