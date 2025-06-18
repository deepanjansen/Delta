# 🤖 Delta - Your An Android Virtual Assistant

Delta is a smart, multilingual Android virtual assistant designed to simplify your everyday tasks using voice commands, natural language understanding, and contextual awareness. Built with Jetpack Compose, Firebase, and Gemini AI, Delta offers a conversational UI with personalized, intelligent assistance.

---

## ✨ Features

* 🎧 **Voice Interaction** – Talk to Delta using your voice via STT (Speech-to-Text).
* 💬 **Natural Language Chat UI** – Responsive, emoji-friendly chat interface.
* 🧠 **Contextual AI Responses** – Context-aware conversations using Gemini API with memory of last 10 exchanges.
* ☁️ **Weather Forecast** – Ask "What's the weather?" to get real-time data.
* 📰 **Latest News** – Fetches and displays the latest news in a clean card layout.
* 🎵 **Music Control** – Play, pause, and manage local music with voice.
* 📁 **File Sharing (Planned)** – NFC + Wi-Fi Direct for fast device-to-device sharing.
* 🌐 **Web Search** – Search the internet using voice/natural text.
* 👤 **User Profiles** – Firebase Auth, Firestore, and Storage integration to store user details and editable avatars.
* 🗌 **Sidebar Navigation** – Telegram-style drawer with chat history, profile, and settings.
* 📃 **Multiple Chat Threads** – Supports unique chat sessions with titles and timestamps.
* 🔊 **Talk Mode** – Continuous conversation for hands-free use.

---

## 🧱 Architecture

* **MVVM Pattern**: Clean separation using `ViewModel`, `Repository`, and Composable UIs.
* **Jetpack Compose**: Modern declarative UI toolkit for smooth user experience.
* **Firebase Integration**:

    * 🔐 Authentication
    * ☁️ Firestore for real-time message storage
    * 🗂️ Firebase Storage for profile images
* **AI Integration**:

    * ✨ Google Gemini API for intelligent responses
    * 🎨 Stability AI for image generation
* **Offline Capabilities (In Progress)**:

    * 🧠 MobileBERT fine-tuning for offline intent recognition
    * 🎯 Intent detection logic with natural phrase support

---

## 🛠️ Tech Stack

* **Kotlin** – Main language
* **Jetpack Compose** – UI Layer
* **Firebase** – Auth, Firestore, Storage
* **Gemini AI** – Context-aware assistant
* **Stability AI** – Image generation
* **MobileBERT (Planned)** – Offline NLP
* **ExoPlayer** – Music playback
* **Android Jetpack Libraries** – Navigation, Lifecycle, etc.

---

## 📂 Project Structure

```
📁 app/
┣ 📁 ui/
┃ ┗ 📄 ChatScreen.kt
┃ ┗ 📄 Sidebar.kt
┣ 📁 viewmodel/
┃ ┗ 📄 ChatViewModel.kt
┃ ┗ 📄 ProfileViewModel.kt
┣ 📁 model/
┃ ┗ 📄 ChatMessage.kt
┣ 📁 utils/
┃ ┗ 📄 IntentDetector.kt
┃ ┗ 📄 ResponseHandler.kt
┃ ┗ 📄 WeatherData.kt
┃ ┗ 📄 NewsArticle.kt
┣ 📁 ai/
┃ ┗ 📄 GeminiHelper.kt
┣ 📄 MainActivity.kt
```

---

## 🚀 Getting Started

1. **Clone the repo**

   ```bash
   git clone https://github.com/jaiswal00007/delta_test1.git
   ```
2. **Open in Android Studio**
3. **Set up Firebase**

    * Add your `google-services.json`
    * Configure Firestore & Storage
4. **Add your API keys** for Gemini and Stability AI
5. **Build & Run**

---

## 📸 Screenshots

### 📱 Chat UI
![Chat UI](assets/main_ui.jpg)

### 🌦️ Weather Card
![Weather Card](assets/weather_screenshot.jpg)

### 📰 Music Card
![News](assets/music_screenshot.jpg)

###  Chat History
![Chat History](assets/sidebar_screenshot.jpg)

---

## 🧠 Future Roadmap

* [ ] Offline NLP with fine-tuned MobileBERT
* [ ] IoT Device Control Integration
* [ ] Smart Reminders & Alarms
* [ ] App Integration (Calendar, Contacts, Calls)
* [ ] Multi-language Chat Translation
* [ ] Sentiment-aware Responses

---

## 🙌 Contributions

Pull requests are welcome! If you’d like to contribute new features, fix bugs, or improve performance/UI, feel free to fork the project.

---

## 📄 License

This project is open source and available under the [MIT License](LICENSE).

---

## 📬 Contact

Created with ❤️ by [Anshu Jaiswal](https://github.com/geekstreak2025)
