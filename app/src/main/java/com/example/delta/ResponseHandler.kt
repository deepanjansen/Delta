package com.example.delta

import android.app.Activity
import android.content.Context
import android.util.Log
import android.widget.Toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

class ResponseHandler(private val context: Context) {
    val functionality = Functionality(context)
    val ask_gemini = GeminiHelper()
    val greetResponses = listOf(
        "Hey there! 😊 How can I help you today?",
        "Hi! 👋 What can I do for you?",
        "Hello! I'm Delta, your assistant. Ask me anything!",
        "Hey! Ready when you are 😄",
        "Hi! Need help with something?",
        "Hello! Always happy to chat!",
        "Yo! 😎 What’s on your mind?",
        "Hi! Let’s get things done 💪",
        "Hey there! Got any questions?",
        "Hiya! I'm here to assist you anytime!"
    )
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


    fun detectIntent(userInput: String): String {
        val commands = mapOf(
            "youtube" to listOf("open youtube", "launch youtube", "start youtube", "run youtube"),
            "instagram" to listOf(
                "open instagram",
                "launch instagram",
                "start instagram",
                "run instagram"
            ),
            "whatsapp" to listOf(
                "open whatsapp",
                "launch whatsapp",
                "start whatsapp",
                "run whatsapp"
            ),
            "facebook" to listOf(
                "open facebook",
                "launch facebook",
                "start facebook",
                "run facebook"
            ),
            "twitter" to listOf("open twitter", "launch twitter", "start twitter", "run twitter"),
            "snapchat" to listOf(
                "open snapchat",
                "launch snapchat",
                "start snapchat",
                "run snapchat"
            ),
            "google" to listOf("open google", "launch google", "start google", "run google"),
            "playstore" to listOf("open playstore", "launch playstore", "start playstore", "run playstore"),
            "spotify" to listOf("open spotify", "launch spotify", "start spotify", "run spotify"),
            "chrome" to listOf("open chrome", "launch chrome", "start chrome", "run chrome"),
            "gmail" to listOf("open gmail", "launch gmail", "start gmail", "run gmail"),
            "flashlight_on" to listOf(
                "turn on flashlight",
                "enable flashlight",
                "switch on the torch",
                "torch on",
                "can you turn on the flashlight",
                "please turn on the flashlight",
                "light on",
                "start flashlight",
                "power on flashlight",
                "activate flashlight"
            ),
            "flashlight_off" to listOf(
                "turn off flashlight",
                "disable flashlight",
                "switch off the torch",
                "torch off",
                "please turn off the flashlight",
                "can you switch off the flashlight",
                "light off",
                "stop flashlight",
                "power down flashlight",
                "deactivate flashlight"
            ),
            "greet" to listOf("hi", "hello", "hey", "wassup", "hyy", "hy", "hii", "hlo"),
            "set_alarm" to listOf("set an alarm", "create an alarm", "schedule an alarm"),
            "stop_alarm" to listOf("stop alarm", "turn off alarm", "disable alarm"),
            "about_bot" to listOf("who are you", "what is your name", "introduce yourself"),
            "developed_by" to listOf(
                "developed you", "created you", "about developer", "made this assistant",
                "behind this ai", "built this assistant?", "who designed you", "who programmed you", "made you"
            ),
            "about_bot" to listOf(
                "who are you", "your name", "about yourself", "introduce yourself",
                "who you are?", "what do people call you?", "who am i talking to?", "your identity"
            ),
            "navigation" to listOf(
                "open maps", "launch maps", "start google maps", "navigate to home"
            ),
            "music_control" to listOf(
                "play music", "pause music", "resume music", "next song", "previous song"
            ),
            "weather" to listOf(
                "weather", "today's weather"
            ),
            "time" to listOf(
                "time", "current time"
            ),
            "increase_volume" to listOf(
                "increase volume", "turn up the volume", "raise the volume", "volume up"
            ),
            "decrease_volume" to listOf(
                "decrease volume", "turn down the volume", "lower the volume", "volume down"
            ),
            "mute_phone" to listOf(
                "mute the phone", "mute", "silence the phone", "turn off the sound"
            ),
            "turn_on_dnd" to listOf(
                "turn on do not disturb", "enable dnd", "activate do not disturb", "switch on dnd mode"
            ),
            "calls" to listOf(
                "call", "call Dad"
            ),
            "wifi_on" to listOf("turn on wifi", "switch on wifi", "enable wifi", "wifi on","wi-fi"),
            "wifi_off" to listOf("turn off wifi", "switch off wifi", "disable wifi", "wifi off"),
            "bluetooth_on" to listOf("turn on bluetooth", "switch on bluetooth" , "bluetooth on", "enable bluetooth"),
            "bluetooth_off" to listOf("turn off bluetooth", "switch off bluetooth", "bluetooth off", "disable bluetooth"),
            "device_status" to listOf(
                "how much battery is left?",
                "what's my battery percentage?",
                "is my phone charging?",
                "battery status?",
                "tell me my battery level",
                "do I need to charge my phone?",
                "how much charge do I have?",
                "battery percentage now?",
                "is my battery low?",
                "what’s my current battery status?",
                "battery percentage"
            ),
            "increase_brightness" to listOf(
                "increase brightness",
                "turn up the brightness",
                "raise the brightness",
                "brightness up",
                "brightness increase"
            ),
            "decrease_brightness" to listOf(
                "decrease brightness",
                "turn down the brightness",
                "lower the brightness",
                "brightness decrease"
            ),
            "play_song" to listOf(
                "play a music",
                "song",
                "play a song",
                "play music",
                "music"
            ),
            "resume_song" to listOf(
                "resume song",
                "resume music",
                "resume",
                "continue",
                "play it again",
                "play again"
            ),
            "pause_song" to listOf(
                "pause song",
                "pause music",
                "pause",
                "stop"
            ),
            "setting" to listOf(
                "open setting",
                "settings",
                "open tools"
            ),
            "messages" to listOf(
                "open message",
                "open message app",
                "message",
                "messages",
                "sms",
                "open sms",
            ),



        )
        val lowerInput = userInput.lowercase()
        for ((intent, phrases) in commands) {
            if (phrases.any { phrase -> "\\b${Regex.escape(phrase)}\\b".toRegex().containsMatchIn(lowerInput) }) {
                return intent
            }
        }
        return "unknown_intent"

    }

    fun classifyCallIntent(input: String): Pair<String, String?> {
        val words = input.split(" ") // Split input into words
        return if (words.size > 1) {
            // Call with a name
            Pair("call_with_name", words.drop(1).joinToString(" "))
        } else {
            // Just "call"
            Pair("call_general", null)
        }
    }

    fun getResponse(query: String, history: List<Map<String, String>>, callback: (String) -> Unit) {
        val currentIntent = detectIntent(query)
        Log.d("intent", currentIntent)
        var response=""
        if (currentIntent.isNotEmpty()) {
            when (currentIntent) {
                "about_bot" -> callback(aboutBotResponses.random())
                "developed_by" -> callback(developedByResponses.random())
                "greet" -> callback(greetResponses.random())

                "youtube", "instagram", "whatsapp", "facebook", "twitter", "snapchat",
                "spotify", "chrome", "gmail", "google", "playstore","setting","messages","sms"
                    -> {
                    CoroutineScope(Dispatchers.Main).launch {
                        delay(1500) // wait 1.5 seconds
                        functionality.open_app(currentIntent)
                    }
                    callback("Opening ${currentIntent.replaceFirstChar { it.uppercase() }}...")
                }

                "flashlight_on" -> {
                    callback("Turning on the flashlight")
                    CoroutineScope(Dispatchers.Main).launch {
                        delay(1500) // wait 1.5 seconds
                        functionality.toggleFlashlight(true)
                    }
                }

                "flashlight_off" -> {
                    callback("Turning off the flashlight")
                    CoroutineScope(Dispatchers.Main).launch {
                        delay(1500) // wait 1.5 seconds
                        functionality.toggleFlashlight(false)
                    }
                }

                "weather" -> {
                    CoroutineScope(Dispatchers.Main).launch {
                        callback("Fetching weather data...")

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
                        functionality.changeBrightness(context as Activity, false,0.5f)
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

                "navigation" -> callback("Opening Google Maps")
                "music_control" -> callback("Controlling music playback")

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