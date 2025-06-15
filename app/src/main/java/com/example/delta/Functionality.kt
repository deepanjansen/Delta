package com.example.delta

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraManager
import android.net.Uri
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.admin.DevicePolicyManager
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.ComponentName
import android.content.ContentResolver
import android.content.IntentFilter
import android.database.Cursor
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.wifi.WifiManager
import android.os.BatteryManager
import android.provider.AlarmClock
import android.provider.ContactsContract
import android.provider.MediaStore

import androidx.core.app.ActivityCompat.startActivityForResult
import android.provider.Settings
import com.yourapp.delta.WeatherData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import java.io.File
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


@Suppress("DEPRECATION")
class Functionality(private val context: Context) {
    var flashLightStatus: Boolean = false
    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    private var mediaPlayer: MediaPlayer? = null
    private var currentSongIndex = 0
    private var songs: MutableList<String> = mutableListOf()
    var isShuffling = false
    private var originalPlaylist: List<String>? = null



    fun openAppOrWebsite(normalizedInput: String) {
        val pm = context.packageManager
        val cleanedInput = normalizedInput.lowercase().trim().replace(" ", "")


        // Known app name aliases → package name
        val knownPackages = mapOf(
            "youtube" to "com.google.android.youtube",
            "yt" to "com.google.android.youtube",
            "instagram" to "com.instagram.android",
            "whatsapp" to "com.whatsapp",
            "facebook" to "com.facebook.katana",
            "twitter" to "com.twitter.android",
            "x" to "com.twitter.android",
            "snapchat" to "com.snapchat.android",
            "spotify" to "com.spotify.music",
            "chrome" to "com.android.chrome",
            "gmail" to "com.google.android.gm",
            "playstore" to "com.android.vending",
            "google" to "com.google.android.googlequicksearchbox",
            "camera" to "com.android.camera",
            "calculator" to "com.android.calculator2",
            "settings" to Settings.ACTION_SETTINGS,
            "sms" to Intent.ACTION_MAIN,  // Special handled below
            "messages" to Intent.ACTION_MAIN,
            "cricbuzz" to "com.cricbuzz.android"

        )

        // Known aliases → URLs
        val knownUrls = mapOf(
            "wikipedia" to "https://www.wikipedia.org",
            "reddit" to "https://www.reddit.com",
            "github" to "https://github.com",
            "stackoverflow" to "https://stackoverflow.com",
            "youtube" to "https://www.youtube.com",
            "google" to "https://www.google.com",
            "yt" to "https://www.youtube.com",
            "x" to "https://www.x.com",
            "instagram" to "https://www.instagram.com",
            "whatsapp" to "https://web.whatsapp.com",
            "facebook" to "https://www.facebook.com",
            "twitter" to "https://twitter.com",
            "snapchat" to "https://www.snapchat.com",
            "linkedin" to "https://www.linkedin.com",
            "google" to "https://www.google.com",
            "gmail" to "https://mail.google.com",
            "maps" to "https://www.google.com/maps",
            "drive" to "https://drive.google.com",
            "translate" to "https://translate.google.com",
            "photos" to "https://photos.google.com",
            "chatgpt" to "https://chat.openai.com",
            "amazon" to "https://www.amazon.in",
            "flipkart" to "https://www.flipkart.com",
            "myntra" to "https://www.myntra.com",
            "netflix" to "https://www.netflix.com",
            "primevideo" to "https://www.primevideo.com",
            "hotstar" to "https://www.hotstar.com",
            "spotify" to "https://www.spotify.com",
            "weather" to "https://www.weather.com",
            "speedtest" to "https://www.speedtest.net",
            "calculator" to "https://www.desmos.com/scientific",
            "calendar" to "https://calendar.google.com",
            "playstore" to "https://play.google.com/store",
            "quora" to "https://www.quora.com",
            "cricbuzz" to "https://www.cricbuzz.com"
        )

        // 1. Handle special cases
        if (cleanedInput == "settings" || cleanedInput == "setting") {
            val intent = Intent(Settings.ACTION_SETTINGS).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
            return
        }

        if (cleanedInput == "messages" || cleanedInput == "sms") {
            val intent = Intent(Intent.ACTION_MAIN).apply {
                addCategory(Intent.CATEGORY_APP_MESSAGING)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
            return
        }

        // 2. Try launching by known package
        knownPackages[cleanedInput]?.let { pkg ->
            if (pkg.startsWith("com.")) {
                val launchIntent = pm.getLaunchIntentForPackage(pkg)
                if (launchIntent != null) {
                    launchIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    context.startActivity(launchIntent)
                    return
                }
            }
        }

        // 3. Try fuzzy label match
        val installedApps = pm.getInstalledApplications(PackageManager.GET_META_DATA)
        val matched = installedApps.minByOrNull {
            val label = pm.getApplicationLabel(it).toString().lowercase()
            levenshteinDistance(label, cleanedInput)
        }

        matched?.let {
            val label = pm.getApplicationLabel(it).toString().lowercase()
            val distance = levenshteinDistance(label, cleanedInput)
            if (label.contains(cleanedInput) || distance in 1..2) { // strict threshold
                val intent = pm.getLaunchIntentForPackage(it.packageName)
                if (intent != null) {
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    context.startActivity(intent)
                    return
                }
            }
        }


        // 4. Try known URL
        knownUrls[cleanedInput]?.let { url ->
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(browserIntent)
            return
        }

        // 5. Final fallback → Google search
        val fallbackUrl = "https://www.google.com/search?q=" + Uri.encode(cleanedInput)
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(fallbackUrl)).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(browserIntent)
    }


    fun levenshteinDistance(lhs: String, rhs: String): Int {
        val lhsLength = lhs.length
        val rhsLength = rhs.length

        val dp = Array(lhsLength + 1) { IntArray(rhsLength + 1) }

        for (i in 0..lhsLength) dp[i][0] = i
        for (j in 0..rhsLength) dp[0][j] = j

        for (i in 1..lhsLength) {
            for (j in 1..rhsLength) {
                val cost = if (lhs[i - 1] == rhs[j - 1]) 0 else 1
                dp[i][j] = minOf(
                    dp[i - 1][j] + 1,    // deletion
                    dp[i][j - 1] + 1,    // insertion
                    dp[i - 1][j - 1] + cost  // substitution
                )
            }
        }

        return dp[lhsLength][rhsLength]
    }



    @RequiresApi(Build.VERSION_CODES.M)
    fun toggleFlashlight(turnOn: Boolean) {
        try {
            if (flashLightStatus == turnOn) {
                Toast.makeText(
                    context,
                    if (turnOn) "Flashlight is already on" else "Flashlight is already off",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
                val cameraId = cameraManager.cameraIdList.first()
                cameraManager.setTorchMode(cameraId, turnOn)
                flashLightStatus = turnOn
                Toast.makeText(
                    context,
                    if (turnOn) "Flashlight turned on" else "Flashlight turned off",
                    Toast.LENGTH_SHORT
                ).show()
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Failed to toggle flashlight: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    // extract name from text
    fun extractContactName(input: String): String? {
        val cleanedInput = input.lowercase().trim()
        val pattern = Regex("call(?: (?:my )?(?:friend )?)?(.+)", RegexOption.IGNORE_CASE)

        val match = pattern.find(cleanedInput)
        return match?.groups?.get(1)?.value?.trim()?.replaceFirstChar { it.uppercase() }
    }

    // to make a call by name
    fun callContactByName(str: String): String {
        val contactName = extractContactName(str)

        if (contactName.isNullOrBlank()) {
            return "Could not extract contact name from the command."
        }

        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.CALL_PHONE
            ) != PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_CONTACTS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                context as Activity,
                arrayOf(Manifest.permission.READ_CONTACTS, Manifest.permission.CALL_PHONE),
                1
            )
            return "Permission not granted"
        }

        val phoneNumber = getPhoneNumberFromContact(contactName)

        return if (phoneNumber != null) {
            // Delay and make the call in a coroutine
            CoroutineScope(Dispatchers.Main).launch {
                delay(3000)
                val intent = Intent(Intent.ACTION_CALL)
                intent.data = Uri.parse("tel:$phoneNumber")
                context.startActivity(intent)
            }
            "Calling $contactName..."
        } else {
            "Contact '$contactName' not found in your contacts."
        }
    }


    fun getPhoneNumberFromContact(contactName: String): String? {
        val contentResolver = context.contentResolver

        // First get matching contact IDs and their frequency
        val contactCursor = contentResolver.query(
            ContactsContract.Contacts.CONTENT_URI,
            arrayOf(
                ContactsContract.Contacts._ID,
                ContactsContract.Contacts.DISPLAY_NAME,
                ContactsContract.Contacts.TIMES_CONTACTED
            ),
            "${ContactsContract.Contacts.DISPLAY_NAME} LIKE ?",
            arrayOf("%$contactName%"),
            "${ContactsContract.Contacts.TIMES_CONTACTED} DESC"
        )

        var contactId: String? = null
        try {
            if (contactCursor != null && contactCursor.moveToFirst()) {
                // Get most contacted matching contact
                contactId = contactCursor.getString(contactCursor.getColumnIndexOrThrow(ContactsContract.Contacts._ID))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            contactCursor?.close()
        }

        if (contactId == null) return null

        // Now fetch the phone number for that contact ID
        val phoneCursor = contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            arrayOf(ContactsContract.CommonDataKinds.Phone.NUMBER),
            "${ContactsContract.CommonDataKinds.Phone.CONTACT_ID} = ?",
            arrayOf(contactId),
            null
        )

        var phoneNumber: String? = null
        try {
            if (phoneCursor != null && phoneCursor.moveToFirst()) {
                phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            phoneCursor?.close()
        }

        return phoneNumber
    }
    fun lockPhone(context: Context) {
        val devicePolicyManager = context.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
        val component = ComponentName(context, MyDeviceAdminReceiver::class.java)

        if (devicePolicyManager.isAdminActive(component)) {
            devicePolicyManager.lockNow()
        } else {
            // Prompt user to grant admin access
            val intent = Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN).apply {
                putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, component)
                putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "Please grant admin access to lock the phone")
            }
            context.startActivity(intent)
        }
    }

    fun setDoNotDisturb(enabled: Int, context: Context) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Check if permission is granted
        if (!notificationManager.isNotificationPolicyAccessGranted) {
            // Ask user to grant permission
            Toast.makeText(context, "Grant DND access permission", Toast.LENGTH_SHORT).show()
            val intent = Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
            return
        }

        // Set DND mode based on input
        val filter = if (enabled == 1) {
            NotificationManager.INTERRUPTION_FILTER_NONE // Enable DND
        } else {
            NotificationManager.INTERRUPTION_FILTER_ALL // Disable DND
        }

        notificationManager.setInterruptionFilter(filter)

        Toast.makeText(
            context,
            if (enabled == 1) "Do Not Disturb enabled" else "Do Not Disturb disabled",
            Toast.LENGTH_SHORT
        ).show()
    }

    fun handleCameraPrompt(prompt: String) {
        val isSelfie = Regex("\\b(selfie|front)\\b", RegexOption.IGNORE_CASE).containsMatchIn(prompt)
        val isRear = Regex("\\b(rear|back)\\b", RegexOption.IGNORE_CASE).containsMatchIn(prompt)
        val delayRegex = Regex("""(?:in|after)\s*(\d+)\s*(seconds?|sec)?""", RegexOption.IGNORE_CASE)
        val delayMatch = delayRegex.find(prompt)
        val delaySeconds = delayMatch?.groups?.get(1)?.value?.toIntOrNull() ?: 0

        val useFrontCamera = isSelfie || (!isRear && prompt.contains("selfie", ignoreCase = true))

        val intent = Intent(context, CameraCaptureActivity::class.java).apply {
            putExtra("useFrontCamera", useFrontCamera)
            putExtra("delay", delaySeconds)
        }

        context.startActivity(intent)
    }


    fun handleYouTubePrompt(prompt: String) {
        val cleaned = prompt.trim().lowercase()

        // Regex pattern to match variations like:
        // "play [title]", "watch [title] on youtube", "search [title] on youtube"
        val pattern = Regex("""(?:play|watch|search)\s+(?:the\s+video\s+)?(.+?)(?:\s+on\s+youtube)?$""")

        val match = pattern.find(cleaned)
        val videoTitle = match?.groups?.get(1)?.value?.trim()

        if (!videoTitle.isNullOrBlank()) {
            val query = Uri.encode(videoTitle)
            val youtubeIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/results?search_query=$query"))
            youtubeIntent.setPackage("com.google.android.youtube")

            if (youtubeIntent.resolveActivity(context.packageManager) != null) {
                context.startActivity(youtubeIntent)
            } else {
                // Fallback to browser if YouTube app not found
                val fallbackIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/results?search_query=$query"))
                context.startActivity(fallbackIntent)
            }
        } else {
            Toast.makeText(context, "Could not understand what to play on YouTube.", Toast.LENGTH_SHORT).show()
        }
    }

    fun searchOnBrowser(userInput: String) {
        val cleanedInput = userInput.lowercase().trim()
        val patterns = listOf(
            Regex("""(?:search for|search|google|look up|find me|find|show me|i want to search|can you google)\s+(.*)"""),
            Regex("""(?:what is|how to|who is|where is|when is)\s+(.*)""")
        )

        var query: String? = null

        for (pattern in patterns) {
            val match = pattern.find(cleanedInput)
            if (match != null) {
                query = match.groups[1]?.value?.trim()
                break
            }
        }

        if (query.isNullOrEmpty()) {
            // Fallback if it just starts with 'search' or 'google'
            if (cleanedInput.startsWith("search")) {
                query = cleanedInput.removePrefix("search").trim()
            } else if (cleanedInput.startsWith("google")) {
                query = cleanedInput.removePrefix("google").trim()
            }
        }

        if (!query.isNullOrEmpty()) {
            val encodedQuery = Uri.encode(query)
            val uri = Uri.parse("https://www.google.com/search?q=$encodedQuery")
            val intent = Intent(Intent.ACTION_VIEW, uri)
            context.startActivity(intent)
        } else {
            Toast.makeText(context, "Couldn't understand what to search.", Toast.LENGTH_SHORT).show()
        }
    }


    suspend fun getWeatherPrompt(): String = suspendCancellableCoroutine { cont ->
        val weatherFetcher = WeatherData(context)

        weatherFetcher.fetchCurrentWeather { response ->
            if (response != null) {
                val city = response.name
                val temp = response.main.temp.toInt()
                val feelsLike = response.main.feels_like.toInt()
                val humidity = response.main.humidity
                val windSpeed = String.format("%.1f", response.wind.speed)
                val description = response.weather.firstOrNull()?.description?.replaceFirstChar { it.uppercase() } ?: "Clear"

                val templates = listOf(
                    "Right now in $city, it's $temp°C with $description. Feels like $feelsLike°C. 🌡️",
                    "Hey! It's $description in $city. The temp is $temp°C but it feels more like $feelsLike°C.",
                    "Heads up! Weather in $city is $description — $temp°C, feels like $feelsLike°C, with $humidity% humidity.",
                    "Currently in $city: $description skies, $temp°C, and it feels like $feelsLike°C. Wind’s at $windSpeed m/s.",
                    "Looks like $city is experiencing $description with $temp°C. Humidity's at $humidity%. Stay comfy!"
                )

                cont.resume(templates.random(), null)
            } else {
                val fallbackTemplates = listOf(
                    "Oops! Couldn't fetch the weather right now.",
                    "I'm having trouble getting the weather details.",
                    "Sorry! The weather data isn't available at the moment."
                )
                cont.resume(fallbackTemplates.random(), null)
            }
        }
    }





    // to turn on/off wifi
    fun switchnWifi(context: Context, switch: Int): String {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
            val wifiManager =
                context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
            return if (switch == 1) {
                if (!wifiManager.isWifiEnabled) {
                    wifiManager.isWifiEnabled = true
                    "Wi-Fi turned ON"
                } else {
                    "Wi-Fi is already ON"
                }
            } else {
                if (wifiManager.isWifiEnabled) {
                    wifiManager.isWifiEnabled = false
                    "Wi-Fi turned OFF"
                } else {
                    "Wi-Fi is already OFF"
                }
            }
        } else {
            val intent = Intent(android.provider.Settings.ACTION_WIFI_SETTINGS)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
//            return "Cannot toggle Wi-Fi programmatically on Android 10+. Opening Wi-Fi settings..."
            return "Opening Wi-Fi settings. Please turn it manually."
        }
    }

    fun switchBluetooth(context: Context, switch: Int): String {
        val bluetoothAdapter: BluetoothAdapter? =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val bluetoothManager =
                    context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
                bluetoothManager.adapter
            } else {
                BluetoothAdapter.getDefaultAdapter()
            }

        if (bluetoothAdapter == null) {
            return "Bluetooth is not available on this device"
        }

        // Android 12+ requires runtime permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S &&
            ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT)
            != PackageManager.PERMISSION_GRANTED
        ) {
            if (context is Activity) {
                ActivityCompat.requestPermissions(
                    context,
                    arrayOf(Manifest.permission.BLUETOOTH_CONNECT),
                    1001
                )
                return "Requesting Bluetooth permission..."
            } else {
                return "Bluetooth permission not granted (not an Activity)"
            }
        }

        return when (switch) {
            1 -> {
                if (!bluetoothAdapter.isEnabled) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        // Android 13+ can't enable Bluetooth directly
                        val intent = Intent(Settings.ACTION_BLUETOOTH_SETTINGS)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        context.startActivity(intent)
                        "Bluetooth can't be turned ON automatically on Android 13+. Opening settings..."
                    } else {
                        bluetoothAdapter.enable()
                        "Bluetooth turned ON"
                    }
                } else {
                    "Bluetooth is already ON"
                }
            }

            0 -> {
                if (bluetoothAdapter.isEnabled) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        val intent = Intent(Settings.ACTION_BLUETOOTH_SETTINGS)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        context.startActivity(intent)
                        "Bluetooth can't be turned OFF automatically on Android 13+. Opening settings..."
                    } else {
                        bluetoothAdapter.disable()
                        "Bluetooth turned OFF"
                    }
                } else {
                    "Bluetooth is already OFF"
                }
            }

            else -> "Invalid command"
        }
    }


    // to get battery percentage
    fun getBatteryPercentage(context: Context): Int {
        // Create an intent filter to listen for battery status updates
        val ifilter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)

        // Get the battery status from the system
        val batteryStatus: Intent? = context.registerReceiver(null, ifilter)

        // Retrieve battery level and scale
        val level: Int = batteryStatus?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
        val scale: Int = batteryStatus?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: -1

        // Calculate battery percentage
        return if (level != -1 && scale != -1) {
            (level / scale.toFloat() * 100).toInt()
        } else {
            -1 // If the battery percentage cannot be fetched
        }
    }


    fun adjustVolume(action: Int) {
        when (action) {
            1 -> increaseVolume()
            -1 -> decreaseVolume()
            0 -> muteVolume()
            10 -> unmuteVolume()
        }
    }

    private fun increaseVolume() {
        audioManager.adjustStreamVolume(
            AudioManager.STREAM_MUSIC,
            AudioManager.ADJUST_RAISE,
            AudioManager.FLAG_SHOW_UI
        )
    }

    private fun decreaseVolume() {
        audioManager.adjustStreamVolume(
            AudioManager.STREAM_MUSIC,
            AudioManager.ADJUST_LOWER,
            AudioManager.FLAG_SHOW_UI
        )
    }

    private fun muteVolume() {
        audioManager.adjustStreamVolume(
            AudioManager.STREAM_MUSIC,
            AudioManager.ADJUST_MUTE,
            AudioManager.FLAG_SHOW_UI
        )
    }

    private fun unmuteVolume() {
        audioManager.adjustStreamVolume(
            AudioManager.STREAM_MUSIC,
            AudioManager.ADJUST_UNMUTE,
            AudioManager.FLAG_SHOW_UI
        )
    }

    // to change brightness
    fun changeBrightness(activity: Activity, increase: Boolean, step: Float = 0.1f) {
        val layoutParams = activity.window.attributes
        var current = layoutParams.screenBrightness

        // If brightness was never set before (defaults to -1), assume 0.5
        if (current < 0f) current = 0.5f

        val newBrightness = if (increase) {
            (current + step).coerceIn(0.1f, 1.0f)
        } else {
            (current - step).coerceIn(0.1f, 1.0f)
        }

        layoutParams.screenBrightness = newBrightness
        activity.window.attributes = layoutParams
    }

    // Load local songs (mp3, wav) from MediaStore
    fun loadSongs() {
        val collection = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(MediaStore.Audio.Media.DATA)
        val cursor = context.contentResolver.query(collection, projection, null, null, null)

        cursor?.use {
            val dataIndex = it.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
            while (it.moveToNext()) {
                val filePath = it.getString(dataIndex)
                if (filePath.endsWith(".mp3") || filePath.endsWith(".wav")) {
                    songs.add(filePath)
                }
            }
        }
    }

    // Play the current song or the first if not playing anything
    fun playSongs() {
        if (songs.isEmpty()) {
            loadSongs()
        }

        if (songs.isEmpty()) {
            Toast.makeText(context, "⚠️ No local songs found", Toast.LENGTH_SHORT).show()
            return
        }

        // Ensure index is valid
        if (currentSongIndex >= songs.size) {
            currentSongIndex = 0
        }

        mediaPlayer?.release()
        mediaPlayer = MediaPlayer().apply {
            setDataSource(songs[currentSongIndex])
            prepare()
            start()
        }

//        Toast.makeText(
//            context,
//            "🎵 Playing: ${File(songs[currentSongIndex]).name}",
//            Toast.LENGTH_SHORT
//        ).show()
    }

    fun pause() {
        if (mediaPlayer?.isPlaying == true) {
            mediaPlayer?.pause()
        } else {
//            Toast.makeText(context, "⏸️ No song is currently playing", Toast.LENGTH_SHORT).show()
        }
    }

    fun resume() {
        if (mediaPlayer != null && mediaPlayer?.isPlaying == false) {
            mediaPlayer?.start()
//            Toast.makeText(context, "▶️ Song resumed", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "▶️ Song is already playing or not initialized", Toast.LENGTH_SHORT).show()
        }
    }

    fun stop() {
        if (mediaPlayer != null) {
            mediaPlayer?.stop()
            mediaPlayer?.release()
            mediaPlayer = null
//            Toast.makeText(context, "⏹️ Music stopped", Toast.LENGTH_SHORT).show()
        }
    }

    fun next() {
        if (songs.isEmpty()) return

        currentSongIndex = (currentSongIndex + 1) % songs.size
        playSongs()
    }

    fun previous() {
        if (songs.isEmpty()) return

        currentSongIndex = if (currentSongIndex - 1 < 0) songs.size - 1 else currentSongIndex - 1
        playSongs()
    }
    fun getCurrentSongName(): String {
        return if (songs.isNotEmpty()) {
            File(songs[currentSongIndex]).name
        } else {
            "No song playing"
        }
    }
    fun getCurrentPosition(): Int {
        return mediaPlayer?.currentPosition ?: 0
    }

    fun getDuration(): Int {
        return mediaPlayer?.duration ?: 1
    }

    fun seekTo(position: Int) {
        mediaPlayer?.seekTo(position)
    }
    //adding shuffle
    fun toggleShuffle() {
        if (!isShuffling) {
            originalPlaylist = songs.toList()
            songs = songs.shuffled().toMutableList()
            isShuffling = true
        } else {
            originalPlaylist?.let {
                songs = it.toMutableList()
            }
            isShuffling = false
        }
    }

    fun isShufflingActive(): Boolean {
        return isShuffling
    }

    fun isPlaying(): Boolean = mediaPlayer?.isPlaying == true

    @SuppressLint("ScheduleExactAlarm")
    fun handleAlarmCommand(message: String) {
        val timeRegex = Regex("""\b(\d{1,2})(?::(\d{2}))?\s*(a\.?m\.?|p\.?m\.?)?\b""", RegexOption.IGNORE_CASE)
        val match = timeRegex.find(message)

        if (match != null) {
            val hourRaw = match.groupValues[1].toInt()
            val minute = if (match.groupValues[2].isNotEmpty()) match.groupValues[2].toInt() else 0
            val amPmRaw = match.groupValues[3].lowercase().replace(".", "")

            var hour = hourRaw % 12
            if (amPmRaw == "pm") hour += 12
            if (amPmRaw.isEmpty()) hour = hourRaw


            val calendar = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, hour)
                set(Calendar.MINUTE, minute)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
                if (before(Calendar.getInstance())) add(Calendar.DATE, 1)
            }

            val alarmTime = calendar.timeInMillis
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (!alarmManager.canScheduleExactAlarms()) {
                    val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    }
                    context.startActivity(intent)
                    Toast.makeText(context, "Please allow exact alarms permission", Toast.LENGTH_LONG).show()
                    return
                }
            }

            val intent = Intent(context, AlarmReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, alarmTime, pendingIntent)

            Toast.makeText(context, "Alarm set for ${"%02d".format(hour)}:${"%02d".format(minute)}", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Couldn't parse time from your message.", Toast.LENGTH_SHORT).show()
        }
    }


    fun cancelAlarm() {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.cancel(pendingIntent)
        Toast.makeText(context, "Alarm cancelled", Toast.LENGTH_SHORT).show()
    }
    fun getCurrentDateFormatted(): String {
        val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        return dateFormat.format(Date())
    }
    fun getCurrentTimeFormatted(): String {
        val dateFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
        return dateFormat.format(Date())
    }
    fun openMobileDataSettings(context: Context) {
        val intent = Intent(Settings.ACTION_DATA_ROAMING_SETTINGS).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
    }
    fun openAirplaneModeSettings(context: Context) {
        val intent = Intent(Settings.ACTION_AIRPLANE_MODE_SETTINGS).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
    }









//    fun playRandomSong(context: Context) {
//        val songs = getLocalSongs(context)
//        if (songs.isNotEmpty()) {
//            val randomPath = songs.random()
//            try {
//                mediaPlayer?.release()
//                mediaPlayer = MediaPlayer().apply {
//                    setDataSource(randomPath)
//                    prepare()
//                    isLooping = false
//                    this@Functionality.isPlaying = true
//                    start()
//                }
//                Toast.makeText(context,  "🎵Playing:${File(randomPath).name}", Toast.LENGTH_SHORT)
//                    .show()
//            } catch (e: Exception) {
//                e.printStackTrace()
//                Toast.makeText(context,  "❌Error playing file", Toast.LENGTH_SHORT).show()
//            }
//        } else {
//            Toast.makeText(context, "⚠️No local songs found", Toast.LENGTH_SHORT).show()
//        }
//    }
//    fun pauseSong(context: Context) {
//        if (isPlaying) {
//            mediaPlayer?.pause()
//            isPlaying = false
//            Toast.makeText(context, "⏸️ Song paused", Toast.LENGTH_SHORT).show()
//        } else {
//            Toast.makeText(context, "⏸️ No song is playing", Toast.LENGTH_SHORT).show()
//        }
//    }
//    fun resumeSong(context: Context) {
//        if (!isPlaying && mediaPlayer != null) {
//            mediaPlayer?.start()
//            isPlaying = true
//            Toast.makeText(context, "▶️ Song resumed", Toast.LENGTH_SHORT).show()
//        } else {
//            Toast.makeText(context, "▶️ Song is already playing", Toast.LENGTH_SHORT).show()
//        }
//    }



}







