//package com.example.delta
//
//import android.app.Activity
//import android.content.Context
//import android.content.Intent
//import android.speech.RecognizerIntent
//import android.util.Log
//import androidx.activity.result.ActivityResultLauncher
//import org.json.JSONObject
//import org.vosk.Model
//import org.vosk.Recognizer
//import org.vosk.android.RecognitionListener
//import org.vosk.android.SpeechService
//import java.io.IOException
//import java.util.Locale
//
//class SpeechToTextHelper(private val context: Context) {
//
//    private var speechService: SpeechService? = null
//    private var model: Model? = null
//    private var recognizer: Recognizer? = null
//
//    // Call this once during initialization
//    fun initializeModel(onInitialized: () -> Unit = {}) {
//        Thread {
//            try {
//                if (model == null) {
//                    val assets = Assets(context)
//                    val modelPath = assets.syncAssets()
//                    model = Model(modelPath)
//                    recognizer = Recognizer(model, 16000.0f)
//                    Log.d("SpeechHelper", "Model loaded")
//                    onInitialized()
//                }
//            } catch (e: IOException) {
//                Log.e("SpeechHelper", "Model loading failed: ${e.message}")
//            }
//        }.start()
//    }
//    fun startSpeechRecognition(activity: Activity, launcher: ActivityResultLauncher<Intent>) {
//        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
//            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
//            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
//            putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak now...")
//        }
//        launcher.launch(intent)
//    }
//
//    fun startListening(onResult: (String) -> Unit) {
//        recognizer?.let { recognizer ->
//            speechService = SpeechService(recognizer, 16000.0f)
//            speechService?.startListening(object : RecognitionListener {
//                override fun onResult(hypothesis: String?) {
//                    hypothesis?.let {
//                        val plainText = extractPlainText(it)
//                        onResult(plainText)
//                    }
//                }
//
//                override fun onFinalResult(hypothesis: String?) {
//                    hypothesis?.let {
//                        val text = extractPlainText(it)
//                        if (text.isNotEmpty()) {
//                            onResult(text)
//                        }
//                    }
//                }
//
//
//                override fun onPartialResult(hypothesis: String?) {}
//                override fun onError(e: Exception?) {
//                    onResult("Error: ${e?.message}")
//                }
//
//                override fun onTimeout() {
//                    onResult("Listening timed out.")
//                }
//            })
//        }
//    }
//
//    fun stopListening() {
//        speechService?.stop()
//        speechService = null
//    }
//
//    fun shutdown() {
//        stopListening()
//        recognizer?.close()
//        model?.close()
//        recognizer = null
//        model = null
//    }
//
//    private fun extractPlainText(jsonResult: String): String {
//        return try {
//            val jsonObject = JSONObject(jsonResult)
//            jsonObject.optString("text", "").trim()
//        } catch (e: Exception) {
//            ""
//        }
//    }
//}
package com.example.delta
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.RecognitionListener
import android.util.Log
import android.view.inputmethod.InputMethodManager
import androidx.activity.result.ActivityResultLauncher
import java.util.Locale

class SpeechToTextHelper(private val context: Context) {

    private lateinit var speechRecognizer: SpeechRecognizer
    private lateinit var speechRecognitionListener: RecognitionListener

    init {
        // Initialize SpeechRecognizer and RecognitionListener
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context)
        speechRecognitionListener = object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {
                // Called when the speech recognizer is ready to listen
                Log.d("SpeechRecognition", "Ready for speech")
            }

            override fun onBeginningOfSpeech() {
                // Called when the user starts speaking
                Log.d("SpeechRecognition", "Speech has started")
            }

            override fun onRmsChanged(rmsdB: Float) {
                // This can be used for real-time speech volume level changes
            }

            override fun onBufferReceived(buffer: ByteArray?) {
                // Not typically used in most cases
            }

            override fun onEndOfSpeech() {
                // Called when the user finishes speaking
                Log.d("SpeechRecognition", "Speech has ended")
            }

            override fun onError(error: Int) {
                // Handle errors here (e.g., speech recognition failed)
                Log.e("SpeechRecognition", "Error: $error")
            }

            @SuppressLint("ServiceCast")
            override fun onResults(results: Bundle?) {
                val recognitionResults = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                recognitionResults?.let {
                    if (it.isNotEmpty()) {
                        val recognizedText = it[0]
                        Log.d("SpeechRecognition", "Recognized Text: $recognizedText")

                    }
                }
            }

            override fun onPartialResults(partialResults: Bundle?) {
                // Can be used to get partial results in real time
            }

            override fun onEvent(eventType: Int, params: Bundle?) {
                // Handle any other events (optional)
            }
        }

        speechRecognizer.setRecognitionListener(speechRecognitionListener)
    }

    // Function to start speech recognition
    fun startSpeechRecognition(activity: Activity, launcher: ActivityResultLauncher<Intent>) {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak now...")
        }
        launcher.launch(intent)
    }

    // You can add cleanup function to release resources
    fun cleanup() {
        speechRecognizer.destroy()
    }
}

