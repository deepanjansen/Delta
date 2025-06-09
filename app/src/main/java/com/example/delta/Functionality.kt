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
import android.app.PendingIntent
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.ContentResolver
import android.content.IntentFilter
import android.database.Cursor
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.wifi.WifiManager
import android.os.BatteryManager
import android.provider.ContactsContract
import android.provider.MediaStore

import androidx.core.app.ActivityCompat.startActivityForResult
import android.provider.Settings
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.util.Calendar


@Suppress("DEPRECATION")
class Functionality(private val context: Context) {
    var flashLightStatus: Boolean = false
    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    private var mediaPlayer: MediaPlayer? = null
    private var currentSongIndex = 0
    private var songs: MutableList<String> = mutableListOf()
    var isShuffling = false
    private var originalPlaylist: List<String>? = null

    // Mapping app names to their correct package names
    private val appPackages = mapOf(
        "youtube" to "com.google.android.youtube",
        "instagram" to "com.instagram.android",
        "whatsapp" to "com.whatsapp",
        "facebook" to "com.facebook.katana",
        "twitter" to "com.twitter.android",
        "snapchat" to "com.snapchat.android",
        "spotify" to "com.spotify.music",
        "chrome" to "com.android.chrome",
        "gmail" to "com.google.android.gm",
        "playstore" to "com.android.vending",
        "google" to "com.google.android.googlequicksearchbox"
    )

    //  // Mapping app names to their correct app urls
    private val appUrls = mapOf(
        "youtube" to "https://www.youtube.com",
        "instagram" to "https://www.instagram.com",
        "whatsapp" to "https://web.whatsapp.com",
        "facebook" to "https://www.facebook.com",
        "twitter" to "https://twitter.com",
        "snapchat" to "https://www.snapchat.com",
        "spotify" to "https://www.spotify.com",
        "chrome" to "https://www.google.com",
        "gmail" to "https://mail.google.com",
        "playstore" to "https://play.google.com/store",
        "google" to "https://www.google.com"
    )


    fun open_app(appName: String) {
        val lowerName = appName.lowercase()
        val packageName = appPackages[lowerName]
        val url = appUrls[lowerName]

        when (lowerName) {
            "setting" -> {
                val intent = Intent(Settings.ACTION_SETTINGS)
                context.startActivity(intent)
                return
            }
            "messages", "sms" -> {
                val intent = Intent(Intent.ACTION_MAIN).apply {
                    addCategory(Intent.CATEGORY_APP_MESSAGING)
                }
                context.startActivity(intent)
                return
            }
            else -> {
                if (packageName != null) {
                    val intent = context.packageManager.getLaunchIntentForPackage(packageName)
                    if (intent != null) {
                        context.startActivity(intent)
                        return
                    }
                }

                if (url != null) {
                    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    context.startActivity(browserIntent)
                } else {
                    Toast.makeText(context, "App not found", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.M)
    fun toggleFlashlight(turnOn: Boolean): Int {
        if (flashLightStatus == turnOn) {
            return -1
        } else {
            val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
            val cameraId = cameraManager.cameraIdList[0]
            cameraManager.setTorchMode(cameraId, turnOn)
            flashLightStatus = turnOn
            return 0
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
        val contentResolver: ContentResolver = context.contentResolver
        val cursor: Cursor? = contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            arrayOf(ContactsContract.CommonDataKinds.Phone.NUMBER),
            "${ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME} = ?",
            arrayOf(contactName),
            null
        )

        var phoneNumber: String? = null
        try {
            if (cursor != null && cursor.moveToFirst()) {
                val numberIndex =
                    cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                if (numberIndex != -1) {
                    phoneNumber = cursor.getString(numberIndex)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            cursor?.close() // Ensure the cursor is closed after use
        }

        return phoneNumber
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

    // to turn on/off Bluetooth
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

        // 🔐 Request BLUETOOTH_CONNECT permission if required (Android 12+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S &&
            ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.BLUETOOTH_CONNECT
            ) != PackageManager.PERMISSION_GRANTED
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
                    bluetoothAdapter.enable()
                    "Bluetooth turned ON"
                } else {
                    "Bluetooth is already ON"
                }
            }

            0 -> {
                if (bluetoothAdapter.isEnabled) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        // Android 13+ can't disable Bluetooth programmatically
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

    fun handleAlarmCommand(message: String) {
        val timeRegex = Regex("""\b(\d{1,2})(?::(\d{2}))?\s?(am|pm)\b""", RegexOption.IGNORE_CASE)
        val match = timeRegex.find(message)

        if (match != null) {
            val hour = match.groupValues[1].toInt()
            val minute = if (match.groupValues[2].isNotEmpty()) match.groupValues[2].toInt() else 0
            val amPm = match.groupValues[3].lowercase()

            // Convert to 24-hour format
            var alarmHour = hour % 12
            if (amPm == "pm") alarmHour += 12

            val calendar = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, alarmHour)
                set(Calendar.MINUTE, minute)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)

                // If the time is before now, set for the next day
                if (before(Calendar.getInstance())) {
                    add(Calendar.DATE, 1)
                }
            }

            val triggerTime = calendar.timeInMillis
            setAlarm(triggerTime)

            Toast.makeText(context, "Alarm set for ${alarmHour}:${"%02d".format(minute)}", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Couldn't find a valid time in your message.", Toast.LENGTH_SHORT).show()
        }
    }


    @SuppressLint("ScheduleExactAlarm")
    private fun setAlarm(triggerTime: Long) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Use setExactAndAllowWhileIdle for better compatibility with Doze mode
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
        } else {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
        }
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







