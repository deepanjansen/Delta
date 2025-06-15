package com.example.delta

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resumeWithException

class ChatViewModel : ViewModel() {
    private val db = Firebase.firestore

    private var chatSummariesListener: ListenerRegistration? = null
    private var messagesListener: ListenerRegistration? = null

    private val _chatSummaries = MutableStateFlow<List<ChatSummary>>(emptyList())
    val chatSummaries = _chatSummaries.asStateFlow()

    private val _messages = MutableStateFlow<List<Pair<ChatMessage, String>>>(emptyList())
    val messages = _messages.asStateFlow()


    private var currentChatId: String? = null

    fun loadChatSummaries() {
        val userId = Firebase.auth.currentUser?.uid ?: return
        chatSummariesListener?.remove()

        chatSummariesListener = db.collection("users")
            .document(userId)
            .collection("chats")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("Firestore", "Error fetching chat summaries", error)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val chats = snapshot.documents.map { doc ->
                        ChatSummary(
                            chatId = doc.id,
                            title = doc.getString("title") ?: "New Chat",
                            lastMessage = doc.getString("lastMessage") ?: "",
                            timestamp = doc.getLong("timestamp") ?: 0L
                        )
                    }
                    _chatSummaries.value = chats
                }
            }
    }

    fun loadMessages(chatId: String) {
        val userId = Firebase.auth.currentUser?.uid ?: return
        messagesListener?.remove()
        currentChatId = chatId

        messagesListener = db.collection("users")
            .document(userId)
            .collection("chats")
            .document(chatId)
            .collection("messages")
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("Firestore", "Error fetching messages", error)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val msgs = snapshot.documents.map { doc ->
                        val text = doc.getString("text") ?: ""
                        val isUser = doc.getBoolean("isUser") ?: false
                        val messageTypeStr = doc.getString("messageType") ?: "TEXT"
                        val timestamp = doc.getLong("timestamp") ?: 0L

                        val messageType = when (messageTypeStr) {
                            "WEATHER" -> MessageType.WEATHER
                            "MUSIC" -> MessageType.MUSIC
                            "NEWS" -> MessageType.NEWS
                            else -> MessageType.TEXT
                        }


                        val weatherData = if (messageType == MessageType.WEATHER) {
                            doc.get("weatherData")?.let { data ->
                                @Suppress("UNCHECKED_CAST")
                                val wd = data as Map<String, Any>
                                WeatherData(
                                    city = wd["city"] as? String ?: "",
                                    temperature = (wd["temperature"] as? Number)?.toDouble() ?: 0.0,
                                    weatherDescription = wd["weatherDescription"] as? String ?: "",
                                    humidity = (wd["humidity"] as? Number)?.toInt() ?: 0,
                                    windSpeed = (wd["windSpeed"] as? Number)?.toDouble() ?: 0.0,
                                    feelsLike = (wd["feelsLike"] as? Number)?.toDouble() ?: 0.0,
                                    iconCode = wd["iconCode"] as? String ?: ""
                                )
                            }
                        } else null

                        val newsArticles = if (messageType == MessageType.NEWS) {
                            (doc["newsArticles"] as? List<Map<String, Any>>)?.map { articleMap ->
                                Article(
                                    title = articleMap["title"] as? String ?: "",
                                    source = articleMap["source"] as? String ?: "",
                                    url = articleMap["url"] as? String ?: ""
                                )
                            }
                        } else null

                        ChatMessage(
                            text = text,
                            isUser = isUser,
                            messageType = messageType,
                            weatherData = weatherData,
                            newsArticles = newsArticles
                        ) to formatTimestamp(timestamp)

                    }
                    _messages.value = msgs
                }
            }
    }

    fun sendMessage(chatId: String, message: ChatMessage) {
        val userId = Firebase.auth.currentUser?.uid ?: return
        val timestamp = System.currentTimeMillis()

        val msgData = mutableMapOf<String, Any?>(
            "text" to message.text,
            "isUser" to message.isUser,
            "messageType" to message.messageType.name,
            "timestamp" to timestamp
        )

        message.weatherData?.let {
            msgData["weatherData"] = mapOf(
                "city" to it.city,
                "temperature" to it.temperature,
                "weatherDescription" to it.weatherDescription,
                "humidity" to it.humidity,
                "windSpeed" to it.windSpeed,
                "feelsLike" to it.feelsLike,
                "iconCode" to it.iconCode
            )
        }
        message.newsArticles?.let { articles ->
            msgData["newsArticles"] = articles.map {
                mapOf(
                    "title" to it.title,
                    "source" to it.source,
                    "url" to it.url
                )
            }
        }

        // Choose appropriate last message summary
        val summaryText = when (message.messageType) {
            MessageType.WEATHER -> "Sent a weather update"
            MessageType.MUSIC -> "Started music playback"
            MessageType.NEWS -> "Sent a news update"
            else -> message.text.take(40)
        }

        db.collection("users")
            .document(userId)
            .collection("chats")
            .document(chatId)
            .collection("messages")
            .add(msgData)
            .addOnSuccessListener {
                db.collection("users")
                    .document(userId)
                    .collection("chats")
                    .document(chatId)
                    .set(
                        mapOf(
                            "lastMessage" to summaryText,
                            "timestamp" to timestamp
                        ),
                        SetOptions.merge()
                    )
                    .addOnFailureListener { e ->
                        Log.e("Firestore", "Failed to update chat summary", e)
                    }

            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Failed to send message", e)
            }
    }



    suspend fun createNewChat(title: String = "New Chat"): String = suspendCancellableCoroutine { cont ->
        val userId = Firebase.auth.currentUser?.uid ?: run {
            cont.resumeWithException(Exception("User not logged in"))
            return@suspendCancellableCoroutine
        }
        val chatDoc = db.collection("users")
            .document(userId)
            .collection("chats")
            .document()

        val data = hashMapOf(
            "title" to title,
            "lastMessage" to "",
            "timestamp" to System.currentTimeMillis()
        )

        chatDoc.set(data)
            .addOnSuccessListener {
                cont.resume(chatDoc.id) {}
            }
            .addOnFailureListener { e ->
                cont.resumeWithException(e)
            }
    }



    fun renameChat(chatId: String, newTitle: String) {
        val userId = Firebase.auth.currentUser?.uid ?: return
        Firebase.firestore.collection("users")
            .document(userId)
            .collection("chats")
            .document(chatId)
            .update("title", newTitle)
    }

    fun deleteChat(chatId: String) {
        val userId = Firebase.auth.currentUser?.uid ?: return
        Firebase.firestore.collection("users")
            .document(userId)
            .collection("chats")
            .document(chatId)
            .delete()
    }



    override fun onCleared() {
        super.onCleared()
        chatSummariesListener?.remove()
        messagesListener?.remove()
    }

    private fun formatTimestamp(timestamp: Long): String {
        val sdf = java.text.SimpleDateFormat("hh:mm a", java.util.Locale.getDefault())
        return sdf.format(java.util.Date(timestamp))
    }

}
