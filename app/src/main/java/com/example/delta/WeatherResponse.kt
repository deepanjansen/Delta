package com.example.delta

import java.util.UUID

data class WeatherResponse(
    val name: String,
    val weather: List<Weather>,
    val main: Main,
    val wind: Wind
)

data class Weather(
    val main: String,
    val description: String,
    val icon: String
)

data class Main(
    val temp: Float,
    val feels_like: Float,
    val humidity: Int,
    val pressure: Int
)

enum class MessageType {
    TEXT,
    WEATHER,
    MUSIC,
    NEWS
}

data class ChatMessage(
    val id: String = UUID.randomUUID().toString(),
    val text: String = "",
    val isUser: Boolean = true,
    val messageType: MessageType = MessageType.TEXT,
    val weatherData: WeatherData? = null,
    val newsArticles: List<Article>? = null
)
data class WeatherData(
    val city: String,
    val temperature: Double,
    val weatherDescription: String,
    val humidity: Int,
    val windSpeed: Double,
    val feelsLike: Double,
    val iconCode: String
)


data class Wind(
    val speed: Float
)


