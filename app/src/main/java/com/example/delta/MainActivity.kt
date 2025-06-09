package com.example.delta


import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.speech.RecognizerIntent
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.offset
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.example.delta.ui.theme.botBubbleColor
import com.example.delta.ui.theme.botTextColor
import com.example.delta.ui.theme.color1
import com.example.delta.ui.theme.color2
import com.example.delta.ui.theme.color3
import com.example.delta.ui.theme.color4
import com.example.delta.ui.theme.sapAssistantBg
import com.example.delta.ui.theme.sapPrimary
import com.google.firebase.auth.FirebaseAuth
import com.yourapp.delta.WeatherData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale



class MainActivity : ComponentActivity() {
    sealed class Screen(val route: String) {
        object Splash : Screen("splash")
        object Login : Screen("login")
        object Register : Screen("register")
        object Chat : Screen("chat")
    }

    private val geminiHelper = GeminiHelper()
    private lateinit var response: ResponseHandler // Declare as a property
    private lateinit var weatherFetcher: WeatherData
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        response = ResponseHandler(this)
        weatherFetcher = WeatherData(this)
        enableEdgeToEdge()

        val isUserLoggedIn = FirebaseAuth.getInstance().currentUser != null

        setContent {
            val musicPlayerViewModel: MusicPlayerViewModel = viewModel()
            val navController = rememberNavController()
            NavHost(
                navController = navController,
                startDestination = Screen.Splash.route
            ) {

                composable(Screen.Splash.route) {
                    AnimatedSplashScreen(
                        onFinished = {
                            val isUserLoggedIn = FirebaseAuth.getInstance().currentUser != null
                            if (isUserLoggedIn) {
                                navController.navigate(Screen.Chat.route) {
                                    popUpTo(Screen.Splash.route) { inclusive = true }
                                }
                            } else {
                                navController.navigate(Screen.Login.route) {
                                    popUpTo(Screen.Splash.route) { inclusive = true }
                                }
                            }
                        }
                    )
                }

                composable(Screen.Login.route) {
                    LoginPage(
                        onLoginSuccess = {
                            navController.navigate(Screen.Chat.route) {
                                popUpTo(Screen.Login.route) { inclusive = true }
                            }
                        },
                        onRegisterClick = {
                            navController.navigate(Screen.Register.route)
                        }
                    )
                }

                composable(Screen.Register.route) {
                    RegisterPage(
                        onRegisterSuccess = {
                            navController.navigate(Screen.Chat.route) {
                                popUpTo(Screen.Register.route) { inclusive = true }
                            }
                        },
                        onBackClick = {
                            navController.popBackStack()
                        }
                    )
                }
                composable("about") {
                    AboutScreen(onBack = { navController.popBackStack() })
                }
                composable(Screen.Chat.route) {
                    ChatScreen(geminiHelper, response, weatherFetcher, musicPlayerViewModel,navController)
                }
            }
        }

    }
}


@Composable
fun AnimatedSplashScreen(onFinished: () -> Unit) {
    var isVisible by remember { mutableStateOf(true) }

    // Trigger splash screen visibility transition after a delay
    LaunchedEffect(Unit) {
        delay(2500) // Splash screen duration
        isVisible = false
        onFinished() // Notify that splash has finished
    }

    // AnimatedVisibility for fade in/out effect
    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(animationSpec = tween(durationMillis = 1500)),
        exit = fadeOut(animationSpec = tween(durationMillis = 800))
    ) {
        SplashContent()
    }
}

@Composable
fun SplashContent() {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF1F1F1)),
        contentAlignment = Alignment.Center
    ) {
        FadingInTextEffect(
            text = "Delta",
            fontSize = 42.sp,
            textColor = Color(0xFF2196F3)
        )
    }
}

@Composable
fun FadingInTextEffect(text: String, fontSize: TextUnit, textColor: Color) {
    // State to control opacity
    var opacity by remember { mutableStateOf(0f) }

    // Set up animation for fading effect
    val animatedOpacity by animateFloatAsState(
        targetValue = opacity,
        animationSpec = tween(durationMillis = 1500, easing = FastOutSlowInEasing)
    )

    // Set up LaunchedEffect to only fade in
    LaunchedEffect(Unit) {
        opacity = 1f // Fade in immediately after splash starts
        delay(2000) // Wait for the fade-in to complete
    }

    // Display the text with fading effect
    Text(
        text = text,
        fontSize = fontSize,
        fontWeight = FontWeight.Bold,
        color = textColor.copy(alpha = animatedOpacity), // Apply fading effect through alpha
        letterSpacing = 0.5.sp,
        lineHeight = fontSize * 1.2,
        modifier = Modifier.padding(16.dp)
    )
}
@Composable
fun Base64Image(base64String: String, modifier: Modifier = Modifier) {
    val decodedBytes = Base64.decode(base64String, Base64.DEFAULT)
    val bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
    Image(
        bitmap = bitmap.asImageBitmap(),
        contentDescription = "Profile Photo",
        contentScale = ContentScale.Crop,
        modifier = modifier
    )
}
//about section
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val uriHandler = LocalUriHandler.current

    // You can get the app version programmatically if you want:
    val appVersion = try {
        val pInfo = context.packageManager.getPackageInfo(context.packageName, 0)
        pInfo.versionName ?: "1.0.0"
    } catch (e: Exception) {
        "1.0.0"
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("About Delta") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        content = { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Delta Virtual Assistant",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Version: $appVersion",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )

                Text(
                    text = "Delta is your personal AI assistant designed to help you manage tasks, answer questions, and automate your daily workflow with smart responses and multilingual support.",
                    style = MaterialTheme.typography.bodyMedium
                )

                Divider()

                Text(
                    text = "Developer:",
                    fontWeight = FontWeight.SemiBold,
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = "Anshu Jaiswal",
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Clickable Privacy Policy
                Text(
                    text = "Privacy Policy",
                    color = Color(0xFF1E88E5),
                    modifier = Modifier.clickable {
                        uriHandler.openUri("https://delta-privacy.netlify.app")
                    }
                )

                // Clickable Contact Support
                Text(
                    text = "Contact Support",
                    color = Color(0xFF1E88E5),
                    modifier = Modifier.clickable {
                        val intent = Intent(Intent.ACTION_SENDTO).apply {
                            data = Uri.parse("mailto:anshujaiswal342@gmail.com")
                            putExtra(Intent.EXTRA_SUBJECT, "Support Request - Delta App")
                        }
                        context.startActivity(intent)
                    }
                )
            }
        }
    )
}

@SuppressLint("RememberReturnType")
@Composable
fun ChatSidebarModern(
    navController: NavController,
    viewModel: ProfileViewModel = viewModel(),
    chatHistory: List<ChatSummary> = sampleChatHistory,
    onChatSelected: (ChatSummary) -> Unit = {},
    onNewChatClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
    onAboutClick: () -> Unit = {},
) {
    val context = LocalContext.current
    val userProfile by viewModel.userProfile.collectAsState()

    var showFullImageDialog by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }
    val authViewModel: AuthViewModel = viewModel()
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            viewModel.uploadProfilePhoto(context, it)
        }
    }

    Surface(
        modifier = Modifier
            .fillMaxHeight()
            .width(290.dp),
        color = Color(0xFF26A8DF),
        shape = RoundedCornerShape(topEnd = 24.dp, bottomEnd = 24.dp),
        shadowElevation = 8.dp
    ) {
        Column {
            // Profile section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF26A8DF))
                    .padding(vertical = 28.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier.size(80.dp),
                    contentAlignment = Alignment.Center
                ) {
                    val photoBase64 = userProfile?.photoBase64
                    if (!photoBase64.isNullOrEmpty()) {
                        Base64Image(
                            base64String = photoBase64,
                            modifier = Modifier
                                .size(80.dp)
                                .clip(CircleShape)
                                .clickable { showFullImageDialog = true }
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "Default User Icon",
                            tint = Color.White,
                            modifier = Modifier
                                .size(80.dp)
                                .clickable { showFullImageDialog = true }
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .align(Alignment.BottomEnd)
                            .offset(x = 6.dp, y = 6.dp)
                            .background(Color.Black.copy(alpha = 0.75f), CircleShape)
                            .border(2.dp, Color.White, CircleShape)
                            .clickable { launcher.launch("image/*") },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit Profile",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                AnimatedContent(targetState = userProfile?.name) { name ->
                    Text(
                        text = name ?: "Loading...",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = Color.White
                    )
                }
            }

            // White background for chat history + footer
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Text(
                    text = "Chat History",
                    color = Color.Black,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp
                )

                Divider(
                    color = Color.Black.copy(alpha = 0.1f),
                    modifier = Modifier.padding(vertical = 6.dp)
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNewChatClick() }
                        .padding(vertical = 10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "New Chat",
                        tint = Color.Black,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("New Chat", color = Color.Black, fontSize = 15.sp)
                }

                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .padding(top = 8.dp)
                ) {
                    items(chatHistory) { chat ->
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFFF5F5F5)
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable { onChatSelected(chat) },
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = chat.title,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color.Black,
                                    fontSize = 15.sp
                                )
                                Text(
                                    text = chat.lastMessage.take(40).let {
                                        if (chat.lastMessage.length > 40) "$it..." else it
                                    },
                                    fontSize = 13.sp,
                                    color = Color.Black.copy(alpha = 0.7f),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = formatTimestamp(chat.timestamp),
                                    fontSize = 11.sp,
                                    color = Color.Black.copy(alpha = 0.5f)
                                )
                            }
                        }
                    }
                }

                Divider(color = Color.Black.copy(alpha = 0.1f))
                Spacer(modifier = Modifier.height(8.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(vertical = 8.dp)
                ) {
                    SidebarBottomItem(
                        label = "Settings",
                        icon = Icons.Default.Settings,
                        onClick = {
                            Toast.makeText(context, "Feature not available", Toast.LENGTH_SHORT).show()
                        },
                        tintColor = Color.Black
                    )
                    SidebarBottomItem("About Us", Icons.Default.Info, onClick = {
                        navController.navigate("about")
                    }, tintColor = Color.Black)
                    SidebarBottomItem(
                        label = "Logout",
                        icon = Icons.Default.ExitToApp,
                        onClick = { showLogoutDialog = true },
                        tintColor = Color.Red.copy(alpha = 0.9f)
                    )
                }
            }
        }
    }

    // Full image dialog
    if (showFullImageDialog && !userProfile?.photoBase64.isNullOrEmpty()) {
        Dialog(onDismissRequest = { showFullImageDialog = false }) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Base64Image(
                    base64String = userProfile!!.photoBase64!!,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                        .padding(16.dp)
                )
            }
        }
    }

    // Logout confirmation dialog
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Confirm Logout") },
            text = { Text("Are you sure you want to logout?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showLogoutDialog = false
                        authViewModel.logoutUser(context) {
                            navController.navigate("login") {
                                popUpTo("main") { inclusive = true }
                            }
                        }
                    }
                ) {
                    Text("Logout", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun SidebarBottomItem(label: String, icon: ImageVector, onClick: () -> Unit, tintColor: Color) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 10.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = tintColor,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(text = label, fontSize = 15.sp, color = tintColor)
    }
}


fun formatTimestamp(timestamp: Long): String {
    val sdf = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault())
    return sdf.format(Date(timestamp))
}


data class ChatSummary(
    val id: String,
    val title: String,
    val lastMessage: String,
    val timestamp: Long
)

val sampleChatHistory = listOf(
    ChatSummary("1", "Chat with Delta", "Hey, how can I help you today?", 1686135600000),
    ChatSummary("2", "Weather Query", "What's the weather like?", 1686059200000),
    ChatSummary("3", "Music Player", "Play my favorite songs", 1685982800000),
    ChatSummary("4", "General Chat", "Tell me a joke", 1685906400000),
)



@Composable
fun ChatScreen(geminiHelper: GeminiHelper, response: ResponseHandler,weatherFetcher: WeatherData, viewModel: MusicPlayerViewModel,navController: NavController) {
    var messages by remember { mutableStateOf(listOf<Pair<ChatMessage, String>>()) }
    var currentMessage by remember { mutableStateOf("") }
    var isTyping by remember { mutableStateOf(false) }
    var isFirstMessage by remember { mutableStateOf(true) }
    val keyboardController = LocalSoftwareKeyboardController.current
    var showButtons by remember { mutableStateOf(true) }
    val listState = rememberLazyListState()
    var isMusicPlayerShown by remember { mutableStateOf(false) }
    var musicPlayerMessageIndex by remember { mutableStateOf<Int?>(null) }
    var showVoiceScreen by remember { mutableStateOf(false) }
    val context = LocalContext.current
    var isListening by remember { mutableStateOf(false) }
    var recognizedText by remember { mutableStateOf("") }
    val activity = context as Activity
    val speechHelper = SpeechToTextHelper(context)
    var isTalkMode by remember { mutableStateOf(false) }
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()


    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }
    if (isTalkMode) {
        TalkModeScreen(
            onExit = { isTalkMode = false }
        )
        return
    }



    fun sendMessage(message: String) {
        if (message.isNotEmpty()) {
            val timestamp = getCurrentTime()
            messages = messages + (ChatMessage(message, true) to timestamp)
            currentMessage = ""
            isTyping = true
            isFirstMessage = false
            keyboardController?.hide()
            showButtons = false

            CoroutineScope(Dispatchers.Main).launch {
                if (message.contains("weather", ignoreCase = true)) {
                    weatherFetcher.fetchCurrentWeather { response ->
                        if (response != null) {
                            val city = response.name
                            val temp = response.main.temp
                            val desc = response.weather.firstOrNull()?.description ?: "N/A"
                            val humidity = response.main.humidity
                            val windSpeed = response.wind.speed
                            val feelsLike = response.main.feels_like
                            val iconCode = response.weather.firstOrNull()?.icon ?: "01d"

                            val weatherMessage = ChatMessage(
                                isUser = false,
                                messageType = MessageType.WEATHER,
                                weatherData = WeatherData(
                                    city = city,
                                    temperature = (Math.round(temp * 10f)) / 10f.toDouble(),
                                    weatherDescription = desc,
                                    humidity = humidity,
                                    windSpeed = (Math.round(windSpeed * 10f)) / 10f.toDouble(),
                                    feelsLike = (Math.round(feelsLike * 10f)) / 10f.toDouble(),
                                    iconCode = iconCode
                                )
                            )

                            CoroutineScope(Dispatchers.Main).launch {
                                delay(2000)
                                messages = messages + (weatherMessage to getCurrentTime())
                                isTyping = false
                            }
                        } else {
                            Log.e("Weather", "Failed to fetch weather.")
                            val botReply =
                                "Sorry, I couldn't fetch the weather information at the moment."
                            messages = messages + (ChatMessage(botReply, false) to getCurrentTime())
                            isTyping = false
                        }
                    }

                } else if (message.contains("music", ignoreCase = true)) {
                    if (!isMusicPlayerShown) {
                        // If the music player is not shown, play the music
                        viewModel.play() // Play music only once
                        val newMessage =
                            ChatMessage(messageType = MessageType.MUSIC, isUser = false)
                        delay(500)
                        messages = messages + (newMessage to getCurrentTime())
                        musicPlayerMessageIndex = messages.lastIndex
                        isMusicPlayerShown = true
                        isTyping = false
                    } else {
                        // If music player is already shown, either resume or pause
                        if (!viewModel.isPlaying.value) {
                            // Resume the music if it's pause
                            viewModel.resume()
                            val botReply = "Music resumed."
                            delay(500)
                            messages = messages + (ChatMessage(botReply, false) to getCurrentTime())
                        }
                        isTyping = false
                    }
                } else {
                    val historyForContext = messages
                        .filter { it.first.messageType == MessageType.TEXT } // Only text messages
                        .map { (msg, _) ->
                            mapOf(
                                "role" to if (msg.isUser) "user" else "assistant",
                                "content" to msg.text
                            )
                        }
                        .takeLast(10)
                    response.getResponse(message, historyForContext) { botReply ->
                        val botTimestamp = getCurrentTime()
                        CoroutineScope(Dispatchers.Main).launch {
                            delay(2000)
                            messages = (messages + (ChatMessage(
                                botReply,
                                false
                            ) to botTimestamp)).takeLast(10)
                            isTyping = false
                        }
                    }
                }
            }
        }
    }

    val speechLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            val resultText =
                data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.firstOrNull()
            if (!resultText.isNullOrEmpty()) {
                sendMessage(resultText)
            }
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ChatSidebarModern(
                navController = navController,
                onNewChatClick = { /* handle new chat */ },
                onChatSelected = { /* handle chat */ },
                onSettingsClick = { /* handle settings */ },
                onAboutClick = { /* handle about */ }
            )
        }
    )
    {
    Box(
            modifier = Modifier
                .fillMaxSize()
                .background(sapAssistantBg)
                .imePadding()
                .navigationBarsPadding()
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                if (isFirstMessage) {
                    LargeHeader(onLogoClick = { scope.launch { drawerState.open() } })
                } else {
                    SmallHeader(
                        isTalkMode = isTalkMode,
                        onModeToggle = { isTalkMode = it },
                        onLogoClick = { scope.launch { drawerState.open() } }
                    )
                }

                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 90.dp),
                    reverseLayout = false
                ) {
                    if (showButtons) {
                        item {
                            Column(
                                modifier = Modifier.padding(vertical = 12.dp).fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(10.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Button(
                                        onClick = { sendMessage("Hi Delta") },
                                        modifier = Modifier.weight(1f).height(48.dp),
                                        shape = RoundedCornerShape(14.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = Color.White)
                                    ) {
                                        Text("👋 Hi Delta", color = sapPrimary)
                                    }

                                    Button(
                                        onClick = { sendMessage("Today's Weather?") },
                                        modifier = Modifier.weight(1f).height(48.dp),
                                        shape = RoundedCornerShape(14.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = Color.White)
                                    ) {
                                        Text("☀️ Weather", color = sapPrimary)
                                    }
                                }
                            }
                        }
                    }

                    items(messages) { (message, timestamp) ->
                        ChatMessageItem(
                            message = message,
                            timestamp = timestamp,
                            viewModel = viewModel
                        )
                    }
                    if (isTyping) {
                        item { SAPTypingIndicator() }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))
            }

            Surface(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(8.dp),
                shadowElevation = 4.dp,
                shape = RoundedCornerShape(24.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = currentMessage,
                        onValueChange = { currentMessage = it },
                        modifier = Modifier.weight(1f).heightIn(min = 48.dp),
                        placeholder = { Text("Ask me anything...") },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        trailingIcon = {
                            Row {
                                IconButton(
                                    onClick = {
                                        speechHelper.startSpeechRecognition(
                                            activity,
                                            speechLauncher
                                        )
                                    },
                                    modifier = Modifier.size(40.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Mic,
                                        contentDescription = "Mic",
                                        tint = if (isListening) Color.Cyan else Color.Gray,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                                IconButton(
                                    onClick = { sendMessage(currentMessage) },
                                    modifier = Modifier.size(40.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Send,
                                        contentDescription = "Send",
                                        tint = sapPrimary,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }
                        },
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                        keyboardActions = KeyboardActions(onSend = { sendMessage(currentMessage) })
                    )
                }
            }
        }
    }
}


@Composable
fun TalkModeScreen(onExit: () -> Unit) {
    val context = LocalContext.current
    val activity = context as? Activity

    // Initialize the SpeechToTextHelper

    val speechHelper = remember { SpeechToTextHelper(activity!!) }
    val ttsHelper = remember { TextToSpeechHelper(context) }
    val responseHandler = remember { ResponseHandler(context) }

    // UI state
    var userSpeech by remember { mutableStateOf("") }
    var isListening by remember { mutableStateOf(false) }
    var botReply by remember { mutableStateOf("") }

    // Speech recognition launcher
    val speechLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = { result ->
            isListening = false
            if (result.resultCode == Activity.RESULT_OK) {
                val spokenText = result.data
                    ?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                    ?.firstOrNull()
                    .orEmpty()

                if (spokenText.isNotEmpty()) {
                    userSpeech = spokenText
                    val historyForContext = listOf(
                        mapOf("role" to "user", "content" to userSpeech)
                    ).takeLast(10) // Limit to last 10 messages

                    // Call the response handler to get bot reply
                    responseHandler.getResponse(userSpeech, historyForContext) { botReplyText ->
                        botReply = botReplyText

                        // After receiving bot's response, speak it out
                        ttsHelper.speak(botReply)
                    }
                }
            }
        }
    )

    // Start speech recognition when needed
    LaunchedEffect(isListening) {
        if (isListening) {
            activity?.let {
                speechHelper.startSpeechRecognition(it, speechLauncher)
            }
        }
    }

    // Stop listening and release the resources when done
    DisposableEffect(Unit) {
        onDispose {
            speechHelper.cleanup()
        }
    }

    // UI...
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("🎙️ Talk Mode", color = Color.White, fontSize = 22.sp)
            Spacer(Modifier.height(8.dp))

            // Display listening or waiting status
            Text(
                text = if (isListening) "Listening…" else "Waiting…",
                color = Color.Cyan
            )
            Spacer(Modifier.height(8.dp))

            // Display user speech
            Text("🧑 You: $userSpeech", color = Color.LightGray)
            Spacer(Modifier.height(24.dp))

            // Display bot reply after speech
            if (botReply.isNotEmpty()) {
                Text("🤖 Bot: $botReply", color = Color.White)
                Spacer(Modifier.height(24.dp))
            }

            // Buttons to start listening or exit
            Button(onClick = { isListening = true }) {
                Text("Start Listening")
            }
            Spacer(Modifier.height(8.dp))
            Button(onClick = onExit) {
                Text("Back to Chat")
            }
        }
    }
}



//@Composable
//fun VoiceInputScreen(
//    onResult: (String) -> Unit,
//    onClose: () -> Unit
//) {
//    val context = LocalContext.current
//    var recognizedText by remember { mutableStateOf("") }
//    var isListening by remember { mutableStateOf(false) }
//    var micState by remember { mutableStateOf("Click to Speak") }
//
//    val speechToTextHelper = remember {
//        SpeechToTextHelper(context) { spokenText ->
//            recognizedText = spokenText // Update recognized text as the user speaks
//            onResult(spokenText) // Send the result back to the parent
//        }
//    }
//
//    val coroutineScope = rememberCoroutineScope()
//
//    // Handle Mic Click
//    val onMicClick: () -> Unit = {
//        if (isListening) {
//            // Stop listening immediately if already listening
//            micState = "Stopped listening"
//            speechToTextHelper.stopListening()
//            isListening = false
//        } else {
//            // Start listening if not already listening
//            micState = "Listening..."
//            speechToTextHelper.startListening()
//            isListening = true
//        }
//    }
//
//    LaunchedEffect(isListening) {
//        // Ensure to start listening when the screen appears or isListening changes
//        if (isListening) speechToTextHelper.startListening()
//    }
//
//    DisposableEffect(Unit) {
//        onDispose {
//            speechToTextHelper.stopListening()
//            speechToTextHelper.destroy()
//        }
//    }
//
//    val infiniteTransition = rememberInfiniteTransition()
//    val pulse by infiniteTransition.animateFloat(
//        initialValue = 1f,
//        targetValue = 1.5f,
//        animationSpec = infiniteRepeatable(
//            animation = tween(1000, easing = FastOutSlowInEasing),
//            repeatMode = RepeatMode.Reverse
//        )
//    )
//
//    Box(
//        modifier = Modifier
//            .fillMaxSize()
//            .background(Color(0xFF121212)) // Dark background for better contrast
//            .padding(16.dp)
//    ) {
//        // ❌ Close Button
//        IconButton(
//            onClick = { onClose() },
//            modifier = Modifier
//                .align(Alignment.TopEnd)
//        ) {
//            Icon(
//                imageVector = Icons.Default.Close,
//                contentDescription = "Close",
//                tint = Color(0xFF2196F3),
//                modifier = Modifier.size(28.dp)
//            )
//        }
//
//        // 🎤 Mic Pulse & Text
//        Column(
//            modifier = Modifier.align(Alignment.Center),
//            horizontalAlignment = Alignment.CenterHorizontally
//        ) {
//            // Speech Text Display above the mic
//            Text(
//                text = recognizedText.ifEmpty { "Say something..." },
//                color = Color.White,
//                fontSize = 24.sp,
//                fontWeight = FontWeight.Bold,
//                letterSpacing = 1.2.sp,
//                modifier = Modifier
//                    .padding(bottom = 16.dp) // Space between text and mic icon
//                    .fillMaxWidth()
//                    .wrapContentWidth(Alignment.CenterHorizontally)
//            )
//
//            // Mic Icon with Pulse Effect
//            Box(
//                modifier = Modifier
//                    .size(160.dp)
//                    .graphicsLayer {
//                        scaleX = pulse
//                        scaleY = pulse
//                        alpha = 0.5f
//                    }
//                    .background(
//                        brush = Brush.radialGradient(
//                            colors = listOf(Color.Cyan, Color.Transparent),
//                            center = Offset.Zero,
//                            radius = 200f
//                        ),
//                        shape = CircleShape
//                    ),
//                contentAlignment = Alignment.Center
//            ) {
//                IconButton(
//                    onClick = { onMicClick() }
//                ) {
//                    Icon(
//                        imageVector = Icons.Default.Mic,
//                        contentDescription = micState,
//                        tint = if (isListening) Color.Cyan else Color.Gray,
//                        modifier = Modifier
//                            .size(64.dp)
//                            .shadow(10.dp, CircleShape)
//                    )
//                }
//            }
//
//            Spacer(modifier = Modifier.height(16.dp))
//
//            // Dynamic Text based on listening state
//            Text(
//                text = micState,
//                color = Color.White,
//                fontSize = 20.sp,
//                fontWeight = FontWeight.Medium,
//                letterSpacing = 1.2.sp
//            )
//
//            Text(
//                text = "Speak your command",
//                color = Color(0xFFAAAAAA),
//                fontSize = 14.sp,
//                fontStyle = FontStyle.Italic,
//                modifier = Modifier.padding(top = 4.dp)
//            )
//        }
//    }
//}



@Composable
fun SmoothGradientBackground(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition()

    // Animate offset to move gradient left to right and back
    val shift by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 800f, // shift across screen width
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 10000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    val gradientColors = listOf(
        color1, // Dominant Blue
        color2, // Extra Blue
        color3, // Teal Accent
        color4  // Pink/Purple at far end
    )

    Box(
        modifier = modifier

            .background(
                Brush.linearGradient(
                    colors = gradientColors,
                    start = Offset(0f + shift, 0f),
                    end = Offset(1000f + shift, 1000f)
                )
            )
    ) {
        content()
    }
}


@Composable
fun ChatMessageView(message: ChatMessage, timestamp: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Text(
            text = message.text,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (message.isUser) FontWeight.Bold else FontWeight.Normal,
            color = if (message.isUser) Color.Black else Color.Gray,
            modifier = Modifier.padding(bottom = 4.dp)
        )

        // Optionally, you can display a timestamp
        Text(
            text = timestamp,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray,
            modifier = Modifier.align(Alignment.End)
        )
    }
}

@Composable
fun LargeHeader(onLogoClick: () -> Unit) {
    SmoothGradientBackground(
        modifier = Modifier
            .fillMaxWidth()
            .height(600.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding())
        ) {
            // Top Row Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, top = 16.dp)
            ) {
                Image(
                    painter = painterResource(R.drawable.logo128x128),
                    contentDescription = "Logo",
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { onLogoClick() }  // <-- Make logo clickable here
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "Delta",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Beta",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 12.sp,
                    modifier = Modifier.padding(start = 8.dp, bottom = 4.dp)
                )
            }

            // Center Content (unchanged)
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                Spacer(modifier = Modifier.height(32.dp))

                Image(
                    painter = painterResource(R.drawable.logo),
                    contentDescription = "Delta Logo",
                    modifier = Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(32.dp))
                )

                Spacer(modifier = Modifier.height(16.dp))

                TypingText(
                    fullText = "Hey! How can I help you?",
                    typingSpeed = 60L,
                    loop = true,
                    textStyle = TextStyle(
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )
            }
        }
    }
}


@Composable
fun TypingText(
    fullText: String,
    typingSpeed: Long = 50L,
    cursorBlinkSpeed: Int = 500,
    loop: Boolean = false,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = TextStyle(fontSize = 20.sp, color = Color.White)
) {
    var displayedText by remember { mutableStateOf("") }

    // Typing effect
    LaunchedEffect(fullText, loop) {
        do {
            displayedText = ""
            for (char in fullText) {
                displayedText += char
                delay(typingSpeed)
            }
            if (loop) {
                delay(1500L) // pause before restarting
            }
        } while (loop)
    }

    Text(
        text = displayedText ,
        modifier = modifier,
        style = textStyle
    )
}



@Composable
fun SmallHeader(
    isTalkMode: Boolean,
    onModeToggle: (Boolean) -> Unit,
    onLogoClick: () -> Unit  // <-- add this param
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding())
    ) {
        SmoothGradientBackground(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Left: Logo and Title
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(R.drawable.logo128x128),
                        contentDescription = "Logo",
                        modifier = Modifier
                            .size(32.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { onLogoClick() }  // <-- clickable here
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = "Delta",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    Text(
                        text = "Beta",
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 11.sp,
                        modifier = Modifier.padding(start = 6.dp, bottom = 2.dp)
                    )
                }

                // Right: Mode Toggle
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = if (isTalkMode) "Talk" else "Chat",
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 12.sp,
                        modifier = Modifier.padding(end = 4.dp)
                    )

                    Switch(
                        checked = isTalkMode,
                        onCheckedChange = onModeToggle,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            uncheckedThumbColor = Color.Gray,
                            checkedTrackColor = Color.Cyan.copy(alpha = 0.5f),
                            uncheckedTrackColor = Color.White.copy(alpha = 0.2f)
                        ),
                        modifier = Modifier.scale(0.8f) // Make the switch smaller
                    )
                }
            }
        }
    }
}


@Composable
fun ChatMessageItem(message: ChatMessage, timestamp: String, viewModel: MusicPlayerViewModel) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalAlignment = if (message.isUser) Alignment.End else Alignment.Start
    ) {
        // Timestamp and avatar
        if (!message.isUser) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(start = 12.dp, bottom = 4.dp)
            ) {
                Image(
                    painter = painterResource(R.drawable.logo128x128),
                    contentDescription = "Bot Icon",
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "BOT • $timestamp",
                    color = Color.Gray,
                    fontSize = 12.sp
                )
            }
        } else {
            Text(
                text = "You • $timestamp",
                color = Color.Gray,
                fontSize = 12.sp,
                modifier = Modifier
                    .padding(end = 12.dp, bottom = 4.dp)
                    .align(Alignment.End)
            )
        }

        // Message bubble or custom content
        when (message.messageType) {
            MessageType.TEXT -> {
                if (message.isUser) {
                    SmoothGradientBackground(
                        modifier = Modifier
                            .widthIn(max = 325.dp)
                            .clip(RoundedCornerShape(14.dp))
                    ) {
                        Box(modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)) {
                            Text(
                                text = message.text,
                                color = Color.White,
                                fontSize = 16.sp,
                                lineHeight = 22.sp
                            )
                        }
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .widthIn(max = 325.dp)
                            .background(
                                color = botBubbleColor,
                                shape = RoundedCornerShape(14.dp)
                            )
                            .border(1.dp, Color(0xFFB6D0E2), RoundedCornerShape(14.dp))
                            .padding(horizontal = 14.dp, vertical = 10.dp)
                    ) {
                        Text(
                            text = message.text,
                            color = botTextColor,
                            fontSize = 16.sp,
                            lineHeight = 22.sp
                        )
                    }
                }
            }

            MessageType.WEATHER -> {
                message.weatherData?.let {
                    WeatherCard(
                        city = it.city,
                        temperature = it.temperature,
                        weatherDescription = it.weatherDescription,
                        humidity = it.humidity,
                        windSpeed = it.windSpeed,
                        feelsLike = it.feelsLike,
                        iconCode = it.iconCode
                    )
                }
            }

            MessageType.MUSIC -> {
                MusicPlayerUI(viewModel = viewModel)
            }
        }
    }
}





@Composable
fun SAPTypingIndicator() {
    val dotCount = 3
    val dotSize = 8.dp
    val dotSpacing = 4.dp
    val delayPerDot = 300
    val infiniteTransition = rememberInfiniteTransition()
    val alphas = List(dotCount) { index ->
        infiniteTransition.animateFloat(
            initialValue = 0.3f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 600, delayMillis = delayPerDot * index),
                repeatMode = RepeatMode.Reverse
            )
        )
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(R.drawable.logo128x128),
            contentDescription = "Typing",
            modifier = Modifier.size(20.dp)
                .clip(RoundedCornerShape(10.dp))
        )

        Spacer(modifier = Modifier.width(8.dp))

        // Dots with animated alpha
        Row(
            horizontalArrangement = Arrangement.spacedBy(dotSpacing)
        ) {
            alphas.forEach { alpha ->
                Box(
                    modifier = Modifier
                        .size(dotSize)
                        .graphicsLayer { this.alpha = alpha.value }
                        .background(sapPrimary, shape = CircleShape)
                )
            }
        }
    }
}


fun getCurrentTime(): String {
    val dateFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
    return dateFormat.format(Date())
}

@Composable
fun WeatherCard(
    city: String,
    temperature: Double,
    weatherDescription: String,
    humidity: Int,
    windSpeed: Double,
    feelsLike: Double,
    iconCode: String
) {
    val currentDate = SimpleDateFormat("EEE, MMM d • h:mm a", Locale.getDefault()).format(Date())

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFDAF2FF)),
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = city,
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                color = color2
            )

            Text(
                text = currentDate,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.DarkGray
            )

            Spacer(modifier = Modifier.height(16.dp))

            AsyncImage(
                model = "https://openweathermap.org/img/wn/${iconCode}@4x.png",
                contentDescription = "Weather Icon",
                modifier = Modifier.size(100.dp)
            )

            Text(
                text = "$temperature°C",
                fontSize = 38.sp,
                fontWeight = FontWeight.Bold,
                color = color2
            )

            Text(
                text = weatherDescription.replaceFirstChar { it.uppercaseChar() },
                fontSize = 18.sp,
                color = Color.DarkGray,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                WeatherInfoItem("💧 Humidity", "$humidity%")
                WeatherInfoItem("🌬 Wind", "$windSpeed m/s")
                WeatherInfoItem("🌡 Feels like", "$feelsLike°C")
            }
        }
    }
}

@Composable
fun WeatherInfoItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, fontSize = 14.sp, color = Color.DarkGray)
        Text(text = value, fontSize = 16.sp, fontWeight = FontWeight.Medium, color = Color.Black)
    }
}

@Composable
fun formatTime(milliseconds: Long): String {
    val minutes = (milliseconds / 1000) / 60
    val seconds = (milliseconds / 1000) % 60
    return String.format("%02d:%02d", minutes, seconds)
}

@Composable
fun MusicPlayerUI(viewModel: MusicPlayerViewModel) {
    val isPlaying by viewModel.isPlaying
    val currentPosition by viewModel.currentPosition
    val totalDuration by viewModel.totalDuration
    val songName by viewModel.songName
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    LaunchedEffect(isPlaying) {
        while (isPlaying) {
            viewModel.updateProgress()
            delay(1000L)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xFFDAF2FF))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "🎵 Now Playing: $songName",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        Slider(
            value = currentPosition,
            onValueChange = {
                viewModel.seekTo(it)
            },
            valueRange = 0f..totalDuration,
            modifier = Modifier
                .fillMaxWidth()
                .height(32.dp),
            colors = SliderDefaults.colors(
                thumbColor = Color(0xFF2196F3),
                activeTrackColor = Color(0xFF2196F3),
                inactiveTrackColor = Color(0xFFBBDEFB)
            )
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = formatTime(currentPosition.toLong()),
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = formatTime(totalDuration.toLong()),
                style = MaterialTheme.typography.bodySmall
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            IconButton(
                onClick = {
                    viewModel.previousSong()
                },
                modifier = Modifier.size(60.dp)
            ) {
                Icon(Icons.Default.SkipPrevious, contentDescription = "Previous", modifier = Modifier.size(32.dp))
            }

            IconButton(
                onClick = {
                    if (isPlaying) viewModel.pause() else viewModel.resume()
                },
                modifier = Modifier.size(80.dp)
            ) {
                Icon(
                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = if (isPlaying) "Pause" else "Play",
                    modifier = Modifier.size(40.dp)
                )
            }

            IconButton(
                onClick = {
                    viewModel.nextSong()
                },
                modifier = Modifier.size(60.dp)
            ) {
                Icon(Icons.Default.SkipNext, contentDescription = "Next", modifier = Modifier.size(32.dp))
            }
            IconButton(
                onClick = {
                    viewModel.toggleShuffle()
                    if (viewModel.isShuffling.value) {
                        Toast.makeText(context, "Shuffle On", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Shuffle Off", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.size(60.dp)
            ) {
                val shuffleIconColor = if (viewModel.isShuffling.value) Color.Blue else Color.Gray
                Icon(
                    imageVector = Icons.Default.Shuffle,
                    contentDescription = "Shuffle",
                    modifier = Modifier.size(32.dp),
                    tint = shuffleIconColor // Change icon color based on shuffle state
                )
            }
        }
    }
}



//data class ChatMessage(val text: String, val isUser: Boolean)

@Composable
fun DeltaTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    isPassword: Boolean = false
) {
    val visualTransformation =
        if (isPassword) PasswordVisualTransformation() else VisualTransformation.None

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        visualTransformation = visualTransformation,
        singleLine = true,
        shape = RoundedCornerShape(12.dp),
        textStyle = TextStyle(color = Color.Black),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color(0xFF2196F3),
            unfocusedBorderColor = Color.LightGray,
            cursorColor = Color(0xFF2196F3),
            focusedTextColor = Color.Black,
            unfocusedTextColor = Color.Black,
            focusedLabelColor = Color(0xFF2196F3),
            unfocusedLabelColor = Color.Gray,
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            disabledContainerColor = Color.White,
            errorContainerColor = Color.White
        ),
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
    )
}




@Composable
fun LoginPage(
    authViewModel: AuthViewModel = viewModel(),
    onLoginSuccess: () -> Unit,
    onRegisterClick: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    // Error states
    var emailError by remember { mutableStateOf(false) }
    var passwordError by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF1F1F1))
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            "Welcome Back",
            fontSize = 30.sp,
            color = Color(0xFF2196F3),
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(24.dp))

        DeltaTextField(
            value = email,
            onValueChange = {
                email = it
                if (it.isNotBlank()) emailError = false // Clear error on input
            },
            label = "Email",
            modifier = Modifier.fillMaxWidth()
        )
        if (emailError) {
            Text(
                "This field is required",
                color = Color.Red,
                fontSize = 12.sp,
                modifier = Modifier.align(Alignment.Start).padding(start = 8.dp)
            )
        }

        DeltaTextField(
            value = password,
            onValueChange = {
                password = it
                if (it.isNotBlank()) passwordError = false
            },
            label = "Password",
            isPassword = true,
            modifier = Modifier.fillMaxWidth()
        )
        if (passwordError) {
            Text(
                "This field is required",
                color = Color.Red,
                fontSize = 12.sp,
                modifier = Modifier.align(Alignment.Start).padding(start = 8.dp)
            )
        }

        Spacer(Modifier.height(24.dp))

        Button(
            onClick = {
                // Validate fields on click
                emailError = email.isBlank()
                passwordError = password.isBlank()

                if (!emailError && !passwordError) {
                    authViewModel.login(email, password, onLoginSuccess)
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3)),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth().height(50.dp)
        ) {
            Text("Login", color = Color.White, fontWeight = FontWeight.SemiBold)
        }

        Spacer(Modifier.height(12.dp))

        TextButton(onClick = onRegisterClick) {
            Text("Don't have an account? Register", color = Color.Gray)
        }

        authViewModel.errorMessage?.let {
            Text(it, color = Color.Red, modifier = Modifier.padding(top = 16.dp))
        }
    }
}


@Composable
fun RegisterPage(
    authViewModel: AuthViewModel = viewModel(),
    onRegisterSuccess: () -> Unit,
    onBackClick: () -> Unit
) {
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    // Error states for each field
    var firstNameError by remember { mutableStateOf(false) }
    var lastNameError by remember { mutableStateOf(false) }
    var emailError by remember { mutableStateOf(false) }
    var passwordError by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF1F1F1))
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            "Create Account",
            fontSize = 30.sp,
            color = Color(0xFF2196F3),
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(24.dp))

        DeltaTextField(
            value = firstName,
            onValueChange = {
                firstName = it
                if (it.isNotBlank()) firstNameError = false
            },
            label = "First Name"
        )
        if (firstNameError) {
            Text(
                "This field is required",
                color = Color.Red,
                fontSize = 12.sp,
                modifier = Modifier.align(Alignment.Start).padding(start = 8.dp)
            )
        }

        DeltaTextField(
            value = lastName,
            onValueChange = {
                lastName = it
                if (it.isNotBlank()) lastNameError = false
            },
            label = "Last Name"
        )
        if (lastNameError) {
            Text(
                "This field is required",
                color = Color.Red,
                fontSize = 12.sp,
                modifier = Modifier.align(Alignment.Start).padding(start = 8.dp)
            )
        }

        DeltaTextField(
            value = email,
            onValueChange = {
                email = it
                if (it.isNotBlank()) emailError = false
            },
            label = "Email"
        )
        if (emailError) {
            Text(
                "This field is required",
                color = Color.Red,
                fontSize = 12.sp,
                modifier = Modifier.align(Alignment.Start).padding(start = 8.dp)
            )
        }

        DeltaTextField(
            value = password,
            onValueChange = {
                password = it
                if (it.isNotBlank()) passwordError = false
            },
            label = "Password",
            isPassword = true
        )
        if (passwordError) {
            Text(
                "This field is required",
                color = Color.Red,
                fontSize = 12.sp,
                modifier = Modifier.align(Alignment.Start).padding(start = 8.dp)
            )
        }

        Spacer(Modifier.height(24.dp))

        Button(
            onClick = {
                // Validate fields
                firstNameError = firstName.isBlank()
                lastNameError = lastName.isBlank()
                emailError = email.isBlank()
                passwordError = password.isBlank()

                if (!firstNameError && !lastNameError && !emailError && !passwordError) {
                    authViewModel.register(email, password, firstName, lastName, onRegisterSuccess)
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3)),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth().height(50.dp)
        ) {
            Text("Register", color = Color.White, fontWeight = FontWeight.SemiBold)
        }

        Spacer(Modifier.height(12.dp))

        TextButton(onClick = onBackClick) {
            Text("Already have an account? Login", color = Color.Gray)
        }

        authViewModel.errorMessage?.let {
            Text(it, color = Color.Red, modifier = Modifier.padding(top = 16.dp))
        }
    }
}




@Preview(showBackground = true)
@Composable
fun GreetingPreview1() {

//    val geminiHelper=GeminiHelper()
//    ChatMessageItem(ChatMessage("hello",true),"1233")
//
//    WeatherCard(
//        city = "New York",
//        temperature = 25.5,
//        weatherDescription = "Sunny",
//        humidity = 60,
//        windSpeed = 5.0,
//        feelsLike = 24.0,
//        iconCode = "01d"
//    )

}
