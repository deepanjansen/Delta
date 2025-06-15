package com.example.delta

import android.app.Activity
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDate
import kotlin.math.min
import kotlin.random.Random

class ResponseHandler(private val context: Context) {
    val functionality = Functionality(context)
    val ask_gemini = GeminiHelper()
    private val firestore = Firebase.firestore
    private val auth = FirebaseAuth.getInstance()

    private var firstName: String = ""
    var fullName: String = ""


    init {
        fetchFirstName()
        fetchFullName()
    }

    private fun fetchFullName() {
        val uid = auth.currentUser?.uid ?: return
        firestore.collection("users").document(uid).get()
            .addOnSuccessListener { snapshot ->
                val first = snapshot.getString("firstName")?.trim()?.replaceFirstChar { it.uppercase() } ?: ""
                val last = snapshot.getString("lastName")?.trim()?.replaceFirstChar { it.uppercase() } ?: ""
                fullName = "$first $last".trim().ifEmpty { "there" }
                Log.d("ResponseHandler", "Fetched full name: $fullName")
            }
            .addOnFailureListener {
                Log.e("ResponseHandler", "Failed to fetch full name", it)
            }
    }


    private fun fetchFirstName() {
        val uid = auth.currentUser?.uid ?: return
        firestore.collection("users").document(uid).get()
            .addOnSuccessListener { snapshot ->
                firstName = snapshot.getString("firstName")?.capitalize() ?: "there"
                Log.d("ResponseHandler", "Fetched first name: $firstName")
            }
            .addOnFailureListener {
                Log.e("ResponseHandler", "Failed to fetch first name", it)
            }
    }
    fun greetResponses(): String {
        val name = if (firstName.isNotEmpty()) firstName else "there"
        val greetResponses = listOf(
            "Hey $name! 😊 How can I help you today?",
            "Hi $name! 👋 What can I do for you?",
            "Hello $name! I'm Delta, your assistant. Ask me anything!",
            "Hey $name! Ready when you are 😄",
            "Hi $name! Need help with something?",
            "Hello $name! Always happy to chat!",
            "Yo $name! 😎 What’s on your mind?",
            "Hi $name! Let’s get things done 💪",
            "Hey $name! Got any questions?",
            "Hiya $name! I'm here to assist you anytime!"
        )
        return greetResponses.random()
    }

    val aboutBotResponses = listOf(
        "I'm Delta, your personal AI assistant 🤖. I'm here to help you with anything you need!",
        "Hey! I'm Delta, your virtual buddy built to make your life easier 💡",
        "I'm Delta, your Android AI assistant! Ask me anything — I'm listening 👂",
        "Delta at your service! I can open apps, set alarms, answer questions, and more 📱",
        "Hi, I’m Delta — your smart assistant. I'm always here to help you out 💬",
        "I'm an AI created to assist you with your daily tasks. Just say the word! 🧠",
        "Call me Delta! I'm an Android-based assistant designed to simplify your life 📲",
        "Nice to meet you! I’m Delta, your AI-powered assistant and digital helper 🤝",
        "I’m Delta, your personal assistant designed to respond, react, and assist 🚀",
        "Delta here 👋 I’m trained to handle commands and give you quick results!"
    )
    val developedByResponses = listOf(
        "I was developed by Anshu Jaiswal 🧠💻",
        "Proudly created by Anshu Jaiswal 🙌",
        "Anshu Jaiswal is the mind behind my existence 🤖✨",
        "Powered and developed by the one and only Anshu Jaiswal 🚀",
        "Brought to life by Anshu Jaiswal, with code and care ❤️",
        "Crafted with dedication by Anshu Jaiswal 🔧🧠",
        "Made possible thanks to the hard work of Anshu Jaiswal 💪",
        "Anshu Jaiswal developed me to assist you better every day 👨‍💻"
    )


//    fun detectIntent(userInput: String): String {
//        val commands = mapOf(
////            "youtube" to listOf("open youtube", "launch youtube", "start youtube", "run youtube"),
////            "instagram" to listOf(
////                "open instagram",
////                "launch instagram",
////                "start instagram",
////                "run instagram"
////            ),
////            "whatsapp" to listOf(
////                "open whatsapp",
////                "launch whatsapp",
////                "start whatsapp",
////                "run whatsapp"
////            ),
////            "facebook" to listOf(
////                "open facebook",
////                "launch facebook",
////                "start facebook",
////                "run facebook"
////            ),
////            "twitter" to listOf("open twitter", "launch twitter", "start twitter", "run twitter"),
////            "snapchat" to listOf(
////                "open snapchat",
////                "launch snapchat",
////                "start snapchat",
////                "run snapchat"
////            ),
////            "google" to listOf("open google", "launch google", "start google", "run google"),
////            "playstore" to listOf("open playstore", "launch playstore", "start playstore", "run playstore"),
////            "spotify" to listOf("open spotify", "launch spotify", "start spotify", "run spotify"),
////            "chrome" to listOf("open chrome", "launch chrome", "start chrome", "run chrome"),
////            "gmail" to listOf("open gmail", "launch gmail", "start gmail", "run gmail"),
//
//            "open_apps" to listOf(
//                "open",
//                "run",
//                "launch",
//                "start",
//                "search",
//            ),
//            "whatsapp" to listOf(
//                "send whatsapp message",
//                "whatsapp"
//            ),
//            "message" to listOf(
//                "send sms",
//                "send message",
//                "message"
//            ),
//            "flashlight_on" to listOf(
//                "turn on flashlight",
//                "enable flashlight",
//                "switch on the torch",
//                "torch on",
//                "can you turn on the flashlight",
//                "please turn on the flashlight",
//                "light on",
//                "start flashlight",
//                "power on flashlight",
//                "activate flashlight"
//            ),
//            "flashlight_off" to listOf(
//                "turn off flashlight",
//                "disable flashlight",
//                "switch off the torch",
//                "torch off",
//                "please turn off the flashlight",
//                "can you switch off the flashlight",
//                "light off",
//                "stop flashlight",
//                "power down flashlight",
//                "deactivate flashlight"
//            ),
//            "greet" to listOf("hi", "hello", "hey", "wassup", "hyy", "hy", "hii", "hlo"),
//            "set_alarm" to listOf("set an alarm", "create an alarm", "schedule an alarm"),
//            "stop_alarm" to listOf("stop alarm", "turn off alarm", "disable alarm"),
//            "about_bot" to listOf("who are you", "what is your name", "introduce yourself","your name"),
//            "developed_by" to listOf(
//                "developed you", "created you", "about developer", "made this assistant",
//                "behind this ai", "built this assistant?", "who designed you", "who programmed you", "made you"
//            ),
//            "about_bot" to listOf(
//                "who are you", "your name", "about yourself", "introduce yourself",
//                "who you are?", "what do people call you?", "who am i talking to?", "your identity"
//            ),
//            "music_control" to listOf(
//                "play music", "pause music", "resume music", "next song", "previous song"
//            ),
//            "weather" to listOf(
//                "weather", "today's weather"
//            ),
//            "time" to listOf(
//                "time", "current time"
//            ),
//            "increase_volume" to listOf(
//                "increase volume", "turn up the volume", "raise the volume", "volume up"
//            ),
//            "decrease_volume" to listOf(
//                "decrease volume", "turn down the volume", "lower the volume", "volume down"
//            ),
//            "mute_phone" to listOf(
//                "mute the phone", "mute", "silence the phone", "turn off the sound"
//            ),
//            "unmute_phone" to listOf(
//                "unmute the phone", "unmute", "turn on the sound", "turn up the sound"
//            ),
//            "turn_on_dnd" to listOf(
//                "turn on do not disturb", "enable dnd", "activate do not disturb", "switch on dnd mode"
//            ),
//            "calls" to listOf(
//                "call", "call Dad","phone call","make a call"
//            ),
//            "wifi_on" to listOf("turn on wifi", "switch on wifi", "enable wifi", "wifi on","wi-fi"),
//            "wifi_off" to listOf("turn off wifi", "switch off wifi", "disable wifi", "wifi off"),
//            "bluetooth_on" to listOf("turn on bluetooth", "switch on bluetooth" , "bluetooth on", "enable bluetooth"),
//            "bluetooth_off" to listOf("turn off bluetooth", "switch off bluetooth", "bluetooth off", "disable bluetooth"),
//            "device_status" to listOf(
//                "how much battery is left?",
//                "what's my battery percentage?",
//                "is my phone charging?",
//                "battery status?",
//                "tell me my battery level",
//                "do I need to charge my phone?",
//                "how much charge do I have?",
//                "battery percentage now?",
//                "is my battery low?",
//                "what’s my current battery status?",
//                "battery percentage"
//            ),
//            "increase_brightness" to listOf(
//                "increase brightness",
//                "turn up the brightness",
//                "raise the brightness",
//                "brightness up",
//                "brightness increase"
//            ),
//            "decrease_brightness" to listOf(
//                "decrease brightness",
//                "turn down the brightness",
//                "lower the brightness",
//                "brightness decrease"
//            ),
//            "play_song" to listOf(
//                "play a music",
//                "song",
//                "play a song",
//                "play music",
//                "music"
//            ),
//            "resume_song" to listOf(
//                "resume song",
//                "resume music",
//                "resume",
//                "continue",
//                "play it again",
//                "play again"
//            ),
//            "pause_song" to listOf(
//                "pause song",
//                "pause music",
//                "pause",
//                "stop",
//                "play song"
//            ),
//            "cancel_alarm" to listOf(
//                "cancel alarm",
//                "delete alarm",
//                "turn off alarm",
//                "stop alarm",
//                "disable alarm",
//                "remove alarm",
//                "clear alarm",
//                "cancel the alarm",
//                "please cancel the alarm",
//                "can you turn off my alarm",
//                "cancel my alarm",
//                "stop my alarm",
//                "cancel set alarm",
//                "stop set alarm"
//            ),
//            "dt" to listOf(
//                "date"
//            ),
//            "time" to listOf(
//                "time"
//            ),
//            "myself" to listOf(
//                "my name",
//                "i am",
//                "i'm",
//                "call me",
//                "this is me",
//                "know me",
//                "about me",
//                "who am i",
//                "remember my name",
//                "set my name",
//                "my profile",
//                "personal info",
//                "what's my name",
//                "change my name"
//            ),
//            "lock_phone" to listOf(
//                "lock my phone",
//                "lock phone",
//                "lock my device",
//                "lock device",
//                "lock the screen",
//                "lock screen",
//                "lock my scree",
//                "phone lock",
//                "screen lock"
//            ),
//            "mobile_data" to listOf(
//                "turn on mobile data",
//                "turn off mobile data",
//                "enable mobile data",
//                "disable mobile data",
//                "mobile data on",
//                "mobile data off",
//                "turn on cellular network",
//                "turn off cellular network",
//                "enable cellular network",
//                "disable cellular network",
//                "cellular network on",
//                "cellular network off"
//            ),
//            "flight_mode" to listOf(
//                "turn on airplane mode",
//                "turn off airplane mode",
//                "enable airplane mode",
//                "disable airplane mode",
//                "turn on flight mode",
//                "turn off flight mode",
//                "enable flight mode",
//                "disable flight mode",
//                "flight mode on",
//                "flight mode off",
//                "airplane mode on",
//                "airplane mode off",
//            )
//
//
//
//        )
//        val lowerInput = userInput.lowercase()
//        for ((intent, phrases) in commands) {
//            if (phrases.any { phrase -> "\\b${Regex.escape(phrase)}\\b".toRegex().containsMatchIn(lowerInput) }) {
//                return intent
//            }
//        }
//        return "unknown_intent"
//
//    }

    fun detectIntent(userInput: String): String {
        val normalized = normalizeText(userInput)
        val stopwords = setOf("is", "the", "a", "an", "what", "of", "to", "in", "on", "and", "at", "for", "with", "how", "you", "are", "do", "i", "me", "my")
        val inputTokens = normalized.split(" ").filterNot { it in stopwords }

        val intents = mapOf(
            "open_apps" to listOf(
                "open",
                "run",
                "launch",
                "start"
            ),
            "web_search" to listOf(
                "search",
                "find",
                "look up",
                "google",
                "look up",
                "find",
                "show me"
            ),
            "greet" to listOf("hi", "hello", "hey", "wassup", "hlo","hy","what's up", "good morning", "how are you", "good evening"),
            "weather" to listOf("weather", "forecast","temperature forecast","is it raining", "will it rain today", "temperature","rain chances","precipitation chance","precipitation chances"),
            "calls" to listOf("call", "make a call", "phone call", "dial", "call dad", "call mom", "call friend"),
            "whatsapp" to listOf("send whatsapp message", "open whatsapp", "whatsapp", "message on whatsapp", "text on whatsapp"),
            "message" to listOf("send message", "send sms", "text", "send a text", "message"),
            "set_alarm" to listOf("set alarm", "create alarm","alarm at", "schedule alarm"),
            "cancel_alarm" to listOf("cancel alarm", "stop alarm", "delete alarm", "disable alarm", "turn off alarm"),
            "flashlight_on" to listOf("turn on flashlight", "enable flashlight", "torch on", "switch on torch", "light on","on torch","flashlight on","enable torch","on light","enable light"),
            "flashlight_off" to listOf("turn off flashlight", "disable flashlight", "torch off", "switch off torch", "light off","off torch","flashlight off","disable torch","off light","disable light"),
            "time" to listOf("what time is it", "current time", "tell me time", "time now","time"),
            "dt" to listOf("what's the date", "today's date", "date today", "current date","date"),
            "wifi_on" to listOf("turn on wifi", "enable wifi", "switch on wifi", "wifi on","turn on wi-fi"),
            "wifi_off" to listOf("turn off wifi", "disable wifi", "switch off wifi", "wifi off","turn off wi-fi","disable wi-fi"),
            "bluetooth_on" to listOf("turn on bluetooth", "enable bluetooth", "bluetooth on", "switch on bluetooth","on bluetooth"),
            "bluetooth_off" to listOf("turn off bluetooth", "disable bluetooth", "bluetooth off", "switch off bluetooth","off bluetooth"),
            "increase_volume" to listOf("increase volume", "volume up", "raise volume", "turn up the volume"),
            "decrease_volume" to listOf("decrease volume", "volume down", "lower volume", "turn down the volume"),
            "mute_phone" to listOf("mute the phone", "mute", "silence phone", "turn off sound","silent phone","silent"),
            "unmute_phone" to listOf("unmute", "unmute the phone", "turn on sound"),
            "turn_on_dnd" to listOf("turn on do not disturb", "enable dnd", "activate dnd", "switch on dnd mode","turn on dnd","dnd on","on dnd"),
            "turn_off_dnd" to listOf( "turn off do not disturb", "disable dnd", "deactivate dnd", "switch off dnd mode","turn off dnd","dnd off","off dnd"),
            "device_status" to listOf("battery status", "battery percentage", "charging", "battery level", "phone charge","battery","charge left"),
            "increase_brightness" to listOf("increase brightness", "brightness up", "turn up brightness"),
            "decrease_brightness" to listOf("decrease brightness", "brightness down", "turn down brightness"),
            "about_bot" to listOf("who are you", "what is your name", "introduce yourself", "your identity","your name"),
            "developed_by" to listOf("created you", "developed you", "about developer", "made you", "your creator","develops you","created you","you are created"),
            "myself" to listOf("my name", "who am i", "i'm", "call me", "set my name", "about me", "remember my name","myself"),
            "lock_phone" to listOf("lock my phone", "lock screen", "phone lock", "lock device"),
            "mobile_data" to listOf("turn on mobile data", "enable data", "data on", "turn off mobile data", "disable data", "data off","mobile data","turn on data","turn off data","turn on cellular network","turn off cellular network"),
            "flight_mode" to listOf("enable flight mode", "airplane mode on", "turn on airplane mode", "disable airplane mode", "flight mode off","flight mode","aeroplane mode"),
            "play_youtube_video" to listOf(
                "play on youtube", "watch on youtube", "play video", "play song", "play music on youtube", "play video on youtube"
            ),
            "take_photo" to listOf(
                "take a photo", "click a picture", "capture image", "take a selfie",
                "take photo after", "click picture in", "capture selfie", "take photo now",
                "take selfie in", "click a rear photo", "click rear camera","click a selfie","click a photo","capture a photo","capture selfie"
            )



        )

        var bestIntent: String? = null
        var bestScore = 0

        for ((intent, phrases) in intents) {
            for (phrase in phrases) {
                val normPhrase = normalizeText(phrase)

                // Exact match shortcut
                if (normalized == normPhrase) return intent

                // Word-boundary regex
                val regex = Regex("\\b" + Regex.escape(normPhrase) + "\\b")
                if (regex.containsMatchIn(normalized)) return intent

                // Token match with stopwords filtered
                val phraseTokens = normPhrase.split(" ").filterNot { it in stopwords }
                val overlap = phraseTokens.count { it in inputTokens }

                if (overlap > bestScore || (overlap == bestScore && bestIntent == null)) {
                    bestScore = overlap
                    bestIntent = intent
                }
            }
        }

        // Only return if decent match
        if (bestScore >= 2) return bestIntent!!

        return "unknown_intent"
    }

    fun normalizeText(input: String): String {
        return input.lowercase()
            .replace(Regex("[^a-z0-9 ]"), " ")
            .replace(Regex("\\s+"), " ")
            .trim()
    }

    fun levenshtein(lhs: String, rhs: String): Int {
        val dp = Array(lhs.length + 1) { IntArray(rhs.length + 1) }
        for (i in 0..lhs.length) dp[i][0] = i
        for (j in 0..rhs.length) dp[0][j] = j

        for (i in 1..lhs.length) {
            for (j in 1..rhs.length) {
                val cost = if (lhs[i - 1] == rhs[j - 1]) 0 else 1
                dp[i][j] = minOf(
                    dp[i - 1][j] + 1,
                    dp[i][j - 1] + 1,
                    dp[i - 1][j - 1] + cost
                )
            }
        }
        return dp[lhs.length][rhs.length]
    }


    fun extractAppName(command: String): String {
        val triggers = listOf("open", "launch", "start", "run", "please", "app", "the", "application","search")

        return command
            .lowercase()
            .split(" ")
            .filterNot { it in triggers }
            .joinToString(" ")
            .trim()
    }

    fun getResponse(query: String, history: List<Map<String, String>>, callback: (String) -> Unit) {
        val currentIntent = detectIntent(query)
        Log.d("intent", currentIntent)
        var response=""
        if (currentIntent.isNotEmpty()) {
            when (currentIntent) {
                "about_bot" -> callback(aboutBotResponses.random())
                "developed_by" -> callback(developedByResponses.random())
                "greet" -> callback(greetResponses())

//                "youtube", "instagram", "whatsapp", "facebook", "twitter", "snapchat",
//                "spotify", "chrome", "gmail", "google", "playstore","setting","messages","sms"
                "open_apps" -> {
                    val appName = extractAppName(query)
                    CoroutineScope(Dispatchers.Main).launch {
                        callback("Opening ${appName.replaceFirstChar { it.uppercaseChar() }}...")
                        delay(1500)
                        functionality.openAppOrWebsite(appName)
                    }
                }
                "message","whatsapp" -> {
                    callback("Opening ${currentIntent}...")

                    CoroutineScope(Dispatchers.Main).launch {
                        delay(1500)
                        functionality.openAppOrWebsite(currentIntent)
                    }
                }

                "myself" ->{
                    callback("You are ${fullName}")
                }
                "lock_phone" ->{
                    callback("Locking the phone")
                    CoroutineScope(Dispatchers.Main).launch {
                        delay(1500)
                        functionality.lockPhone(context)
                    }
                }
                "flashlight_on" -> {
                    callback("Turning on flashlight")
                    CoroutineScope(Dispatchers.Main).launch {
                        delay(1500) // wait 1.5 seconds
                        functionality.toggleFlashlight(true)
                    }

                }

                "flashlight_off" -> {
                    callback("Turning off flashlight")
                    CoroutineScope(Dispatchers.Main).launch {
                        delay(1500) // wait 1.5 seconds
                        functionality.toggleFlashlight(false)
                    }
                }

                "weather" -> {
                    CoroutineScope(Dispatchers.Main).launch {
                        delay(1500)
                        response=functionality.getWeatherPrompt()
                        callback(response)
                    }

                }

                "device_status" -> {
                    CoroutineScope(Dispatchers.Main).launch {
                        delay(1500);
                        val batteryPercentage = functionality.getBatteryPercentage(context)
                        if (batteryPercentage != -1) {
                            callback("You have $batteryPercentage% battery left")
                        } else {
                            callback("Sorry, I couldn't fetch battery status")
                        }

                    }
                }

                "calls" -> {
                    CoroutineScope(Dispatchers.Main).launch {
                        response=functionality.callContactByName(query)
                        callback(response)
                        delay(2500)
                        Toast.makeText( context, response, Toast.LENGTH_SHORT).show()
                    }
                }
                "bluetooth_on" -> {
                    callback("Turning on bluetooth")
                    CoroutineScope(Dispatchers.Main).launch {
                        delay(2500) // wait 1.5 seconds
                        response=functionality.switchBluetooth(context, 1)
                        Toast.makeText( context, response, Toast.LENGTH_SHORT).show()
                    }

                }
                "bluetooth_off" -> {
                    callback("Turning off bluetooth")
                    CoroutineScope(Dispatchers.Main).launch {
                        delay(2500) // wait 1.5 seconds
                        response=functionality.switchBluetooth(context, 0)
                        Toast.makeText( context, response, Toast.LENGTH_SHORT).show()
                    }
                }
                "wifi_on" -> {
                    callback("Turning on wifi")
                    CoroutineScope(Dispatchers.Main).launch {
                        delay(2500) // wait 2.5 seconds
                        response = functionality.switchnWifi(context, 1)
                        Toast.makeText( context, response, Toast.LENGTH_SHORT).show()
                    }
                }
                "wifi_off" -> {
                    callback("Turning off wifi")
                    CoroutineScope(Dispatchers.Main).launch {
                        delay(2500) // wait 2.5 seconds
                        response=functionality.switchnWifi(context, 0)
                        Toast.makeText( context, response, Toast.LENGTH_SHORT).show()
                    }
                }
                "increase_volume" -> {
                    callback("Increasing volume")
                    CoroutineScope(Dispatchers.Main).launch {
                        delay(1500)
                        functionality.adjustVolume(1)
                        Toast.makeText( context, "Volume Increased", Toast.LENGTH_SHORT).show()
                    }
                }
                "decrease_volume" -> {
                    callback("Decreasing volume")
                    CoroutineScope(Dispatchers.Main).launch {
                        delay(1500)
                        functionality.adjustVolume(-1)
                        Toast.makeText( context, "Volume Decreased", Toast.LENGTH_SHORT).show()
                    }
                }
                "mute_phone" -> {
                    callback("Muting the phone")
                    CoroutineScope(Dispatchers.Main).launch {
                        delay(1500)
                        functionality.adjustVolume(0)
                        Toast.makeText( context, "Phone Muted", Toast.LENGTH_SHORT).show()
                    }
                }
                "unmute_phone" -> {
                    callback("Unmuting the phone")
                    CoroutineScope(Dispatchers.Main).launch {
                        delay(1500)
                        functionality.adjustVolume(10)
                        Toast.makeText( context, "Phone Unmuted", Toast.LENGTH_SHORT).show()
                    }
                }

                "increase_brightness" -> {
                    callback("Increasing brightness")
                    CoroutineScope(Dispatchers.Main).launch {
                        delay(1500)
                        functionality.changeBrightness(context as Activity, true,0.5f)
                        Toast.makeText(context, "Brightness Increased", Toast.LENGTH_SHORT).show()
                    }
                }
                "decrease_brightness" -> {
                    callback("Decreasing brightness")
                    CoroutineScope(Dispatchers.Main).launch {
                        delay(1500)
                        functionality.changeBrightness(context as Activity, false,0.7f)
                        Toast.makeText(context, "Brightness Decreased", Toast.LENGTH_SHORT).show()
                    }

                }
                "set_alarm" -> {
                    callback("Setting an alarm")
                    CoroutineScope(Dispatchers.Main).launch {
                        delay(1500)
                        functionality.handleAlarmCommand(query)

                    }
                }
                "cancel_alarm"->{
                    callback("cancelling an alarm")
                    CoroutineScope(Dispatchers.Main).launch {
                        delay(1500)
                        functionality.cancelAlarm()
                    }
                }
                "mobile_data" -> {
                    callback("⚠️ Mobile data control is restricted.\nRedirecting to mobile data settings...")
                    CoroutineScope(Dispatchers.Main).launch {
                        delay(1500)
                        functionality.openMobileDataSettings(context)
                    }
                }
                "flight_mode"-> {
                    callback("⚠️ Flight mode control is restricted.\nRedirecting to flight mode settings...")
                    CoroutineScope(Dispatchers.Main).launch {
                        delay(1500)
                        functionality.openAirplaneModeSettings(context)
                    }
                }
                "turn_on_dnd" ->{
                    callback("Turning on do not disturb")
                    CoroutineScope(Dispatchers.Main).launch {
                        delay(1500)
                        functionality.setDoNotDisturb(1,context)
                    }
                }
                "turn_off_dnd" ->{
                    callback("Turning off do not disturb")
                    CoroutineScope(Dispatchers.Main).launch {
                        delay(1500)
                        functionality.setDoNotDisturb(0,context)
                    }
                }
                "play_youtube_video" ->{
                    callback("Playing YouTube video")
                    CoroutineScope(Dispatchers.Main).launch {
                        delay(1500)
                        functionality.handleYouTubePrompt(query)
                    }
                }
                "web_search" ->{
                    callback("Searching the web")
                    CoroutineScope(Dispatchers.Main).launch {
                        delay(1500)
                        functionality.searchOnBrowser(query)
                    }
                }
                "take_photo" -> {
                    callback("Getting ready to take your photo...")
                    CoroutineScope(Dispatchers.Main).launch {
                        delay(1500)
                        functionality.handleCameraPrompt(query)
                    }
                }


//                "play_song" -> {
//                    callback("Playing Song")
//                    CoroutineScope(Dispatchers.Main).launch {
//                        delay(1500)
//                        functionality.playRandomSong(context)
//                    }
//                }
//                "resume_song" -> {
//                    callback("Resuming Song")
//                    CoroutineScope(Dispatchers.Main).launch {
//                        delay(1500)
//                        functionality.resume(context)
//                    }
//                }
//                "pause_song" -> {
//                    callback("Pausing Song")
//                    CoroutineScope(Dispatchers.Main).launch {
//                        delay(1500)
//                        functionality.pause(context)
//                    }
//                }

                "music_control" -> callback("Controlling music playback")
                "dt" -> callback("Today is ${functionality.getCurrentDateFormatted()}")
                "time" -> callback("The time is ${functionality.getCurrentTimeFormatted()}")






                else -> ask_gemini.getGeminiResponse(query, history) { reply ->
                    callback(reply)
                }
            }
        } else {
            callback("Invalid Response")
        }

    }
}
//fun main(){
//    val get=ResponseHandler()
//    println(get.detectIntent("turn on flashlight"))
//}