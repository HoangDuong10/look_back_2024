package com.example.loadimage

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.media.MediaMetadataRetriever
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.core.content.FileProvider
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.gson.Gson
import kotlinx.coroutines.delay
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.random.Random

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
        setContent {
//            LoadImageTheme {
//                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
////                    Greeting(
////                        name = "Android",
////                        modifier = Modifier.padding(innerPadding)
////                    )
//                    var navigationData = ReminderDataNavigation(
//                        ReminderConstants.TOTAL_STEPS,
//                        ReminderConstants.CURRENT_STEP_DEFAULT,
//                        isShowFull = false
//                    )
//                    MultiLinearDeterminateIndicator(
//                        modifier = Modifier.padding(innerPadding),
//                        navigationData.steps,
//                        navigationData.currentStep,
//                        false,
//                        true
//                    )
//                }
//            }
//            AnimatedTextExample()
//            TestTypeText()

//            ScrollingMonthBox(selectedMonth = "June")
            val navController = rememberNavController()
            val fakeData = FakeData(
                order = "12345",
                topNhaBan = "Top 100",
                doanhthu = "100,000,000",
                thang = "6",
                name = "John Doe",
                slKhachHang = "150",
                topYeuThich = "Top 100",
                khachHang = "500",
                danhGiaKH = "22",
                danhGiaCuaBan = "12",
                soLanSD = "20"
            )
            var navigationData = ReminderDataNavigation(
                ReminderConstants.TOTAL_STEPS,
                ReminderConstants.CURRENT_STEP_DEFAULT,
                data = fakeData
            )
            NavHost(navController = navController, startDestination = "screen1") {
                composable(LookBackNavigation.Screen1.route) {
                    Screen1(
                        modifier = Modifier,
                        nextScreen = {
                            val jsonData = Gson().toJson(navigationData)
                            navController.navigate("${LookBackNavigation.Screen2.route}/$jsonData")
                        },
                        dataNavigation =  navigationData
                    )
                }
                composable(
                    "${LookBackNavigation.Screen2.route}/{data}",
                    arguments = listOf(navArgument("data") { type = NavType.StringType })
                ) { backStackEntry ->
                    val json = backStackEntry.arguments?.getString("data") ?: ""
                    val reminderData = Gson().fromJson(json, ReminderDataNavigation::class.java)
                    Screen2 (
                        nextScreen = {
                            val jsonData = Gson().toJson(reminderData)
                            navController.navigate("${LookBackNavigation.Screen3.route}/$jsonData")
                        },
                        previousScreen = {
                            val jsonData = Gson().toJson(reminderData)
                            navController.navigate("${LookBackNavigation.Screen1.route}")
                        },
                        data = reminderData
                    )

                }

                composable(
                    "${LookBackNavigation.Screen3.route}/{data}",
                    arguments = listOf(navArgument("data") { type = NavType.StringType })
                ) { backStackEntry ->
                    val json = backStackEntry.arguments?.getString("data") ?: ""
                    val reminderData = Gson().fromJson(json, ReminderDataNavigation::class.java)
                    Screen3(
                        nextScreen = {
                            val jsonData = Gson().toJson(reminderData)
                            navController.navigate("${LookBackNavigation.Screen4.route}/$jsonData")
                        },
                        previousScreen = {
                            val jsonData = Gson().toJson(reminderData)
                            navController.navigate("${LookBackNavigation.Screen2.route}/$jsonData")
                        },
                        data = reminderData
                    )

                }
                composable(
                    "${LookBackNavigation.Screen4.route}/{data}",
                    arguments = listOf(navArgument("data") { type = NavType.StringType })
                ) { backStackEntry ->
                    val json = backStackEntry.arguments?.getString("data") ?: ""
                    val reminderData = Gson().fromJson(json, ReminderDataNavigation::class.java)
                    Screen4(
                        nextScreen = {
                            val jsonData = Gson().toJson(reminderData)
                            navController.navigate("${LookBackNavigation.Screen5.route}/$jsonData")
                        },
                        previousScreen = {
                            val jsonData = Gson().toJson(reminderData)
                            navController.navigate("${LookBackNavigation.Screen3.route}/$jsonData")
                        },
                        data = reminderData
                    )

                }
                composable(
                    "${LookBackNavigation.Screen5.route}/{data}",
                    arguments = listOf(navArgument("data") { type = NavType.StringType })
                ) { backStackEntry ->
                    val json = backStackEntry.arguments?.getString("data") ?: ""
                    val reminderData = Gson().fromJson(json, ReminderDataNavigation::class.java)
                    Screen5(
                        nextScreen = {
                            val jsonData = Gson().toJson(reminderData)
                            navController.navigate("${LookBackNavigation.Screen6.route}/$jsonData")
                        },
                        previousScreen = {
                            val jsonData = Gson().toJson(reminderData)
                            navController.navigate("${LookBackNavigation.Screen4.route}/$jsonData")
                        },
                        data = reminderData
                    )

                }
                composable(
                    "${LookBackNavigation.Screen6.route}/{data}",
                    arguments = listOf(navArgument("data") { type = NavType.StringType })
                ) { backStackEntry ->
                    val json = backStackEntry.arguments?.getString("data") ?: ""
                    val reminderData = Gson().fromJson(json, ReminderDataNavigation::class.java)
                    Screen6(
                        nextScreen = {
                            val jsonData = Gson().toJson(reminderData)
                            navController.navigate("${LookBackNavigation.Screen7.route}/$jsonData")
                        },
                        previousScreen = {
                            val jsonData = Gson().toJson(reminderData)
                            navController.navigate("${LookBackNavigation.Screen5.route}/$jsonData")
                        },
                        data = reminderData
                    )

                }
                composable(
                    "${LookBackNavigation.Screen7.route}/{data}",
                    arguments = listOf(navArgument("data") { type = NavType.StringType })
                ) { backStackEntry ->
                    val json = backStackEntry.arguments?.getString("data") ?: ""
                    val reminderData = Gson().fromJson(json, ReminderDataNavigation::class.java)
                    Screen7(
                        nextScreen = {
                            val jsonData = Gson().toJson(reminderData)
                            navController.navigate("${LookBackNavigation.Screen8.route}/$jsonData")
                        },
                        previousScreen = {
                            val jsonData = Gson().toJson(reminderData)
                            navController.navigate("${LookBackNavigation.Screen6.route}/$jsonData")
                        },
                        data = reminderData
                    )

                }

                composable(
                    "${LookBackNavigation.Screen8.route}/{data}",
                    arguments = listOf(navArgument("data") { type = NavType.StringType })
                ) { backStackEntry ->
                    val json = backStackEntry.arguments?.getString("data") ?: ""
                    val reminderData = Gson().fromJson(json, ReminderDataNavigation::class.java)
                    Screen8(
                        nextScreen = {
                            val jsonData = Gson().toJson(reminderData)
                            navController.navigate("${LookBackNavigation.Screen9.route}/$jsonData")
                        },
                        previousScreen = {
                            val jsonData = Gson().toJson(reminderData)
                            navController.navigate("${LookBackNavigation.Screen7.route}/$jsonData")
                        },
                        data = reminderData
                    )
                }

                composable(
                    "${LookBackNavigation.Screen9.route}/{data}",
                    arguments = listOf(navArgument("data") { type = NavType.StringType })
                ) { backStackEntry ->
                    val json = backStackEntry.arguments?.getString("data") ?: ""
                    val reminderData = Gson().fromJson(json, ReminderDataNavigation::class.java)
                    Screen9(
                        nextScreen = {
                            val jsonData = Gson().toJson(reminderData)
                            navController.navigate("${LookBackNavigation.Screen10.route}/$jsonData")
                        },
                        previousScreen = {
                            val jsonData = Gson().toJson(reminderData)
                            navController.navigate("${LookBackNavigation.Screen8.route}/$jsonData")
                        },
                        data = reminderData
                    )

                }
                composable(
                    "${LookBackNavigation.Screen10.route}/{data}",
                    arguments = listOf(navArgument("data") { type = NavType.StringType })
                ) { backStackEntry ->
                    val json = backStackEntry.arguments?.getString("data") ?: ""
                    val reminderData = Gson().fromJson(json, ReminderDataNavigation::class.java)
                    Screen10(
                        nextScreen = {
                            val jsonData = Gson().toJson(reminderData)
                            navController.navigate("${LookBackNavigation.Screen11.route}/$jsonData")
                        },
                        previousScreen = {
                            val jsonData = Gson().toJson(reminderData)
                            navController.navigate("${LookBackNavigation.Screen9.route}/$jsonData")
                        },
                        data = reminderData
                    )

                }

                composable(
                    "${LookBackNavigation.Screen11.route}/{data}",
                    arguments = listOf(navArgument("data") { type = NavType.StringType })
                ) { backStackEntry ->
                    val json = backStackEntry.arguments?.getString("data") ?: ""
                    val reminderData = Gson().fromJson(json, ReminderDataNavigation::class.java)
                    Screen11(
                        nextScreen = {
                            val jsonData = Gson().toJson(reminderData)
                            navController.navigate("${LookBackNavigation.Screen12.route}/$jsonData")
                        },
                        previousScreen = {
                            val jsonData = Gson().toJson(reminderData)
                            navController.navigate("${LookBackNavigation.Screen10.route}/$jsonData")
                        },
                        data = reminderData
                    )

                }

                composable(
                    "${LookBackNavigation.Screen12.route}/{data}",
                    arguments = listOf(navArgument("data") { type = NavType.StringType })
                ) { backStackEntry ->
                    val json = backStackEntry.arguments?.getString("data") ?: ""
                    val reminderData = Gson().fromJson(json, ReminderDataNavigation::class.java)
                    Screen12(
                        previousScreen = {
                            val jsonData = Gson().toJson(reminderData)
                            navController.navigate("${LookBackNavigation.Screen11.route}/$jsonData")
                        },
                        data = reminderData
                    )

                }

            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

fun captureScreenshot(context: Context, view: View) {
    val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    view.draw(canvas)
    val now = SimpleDateFormat("yyyy-MM-dd_HH:mm:ss", Locale.getDefault()).format(Date())
    val path = File(context.getExternalFilesDir(null), "$now.jpg")
    FileOutputStream(path).use { outputStream ->
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
    }

    val uri = FileProvider.getUriForFile(context, "com.e.caputerScreenshost.android.provider222", path)
    val intent = Intent(Intent.ACTION_SEND).apply {
        putExtra(Intent.EXTRA_STREAM, uri)
        type = "image/jpeg"
    }
    context.startActivity(Intent.createChooser(intent, "Chia sẻ ảnh qua"))
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun Screen1(
    modifier: Modifier = Modifier,
    nextScreen: (Int) -> Unit,
    dataNavigation: ReminderDataNavigation
) {

    val context = LocalContext.current
    var isPlayVideo by remember { mutableStateOf(true) }
    val videoUris =
        getVideoDuration(context, "android.resource://${context.packageName}/${R.raw.man1}")
    var config by remember {
        mutableStateOf(
            ProgressBarConfig(
                action = ReminderConstants.RESET,
                configValue = Random.nextInt()
            )
        )
    }
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build()
    }
    val goToNextScreen = {
            config = config.reset()
            dataNavigation.currentStep += 1
            nextScreen.invoke(dataNavigation.currentStep)

    }
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> {isPlayVideo = true}
                Lifecycle.Event.ON_PAUSE -> {isPlayVideo = false}
                Lifecycle.Event.ON_STOP -> {}
                else -> Unit
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
    exoPlayer.addListener(object : Player.Listener {
        override fun onPlaybackStateChanged(playbackState: Int) {
            when (playbackState) {
                Player.STATE_ENDED -> {
                    exoPlayer.seekTo(exoPlayer.duration)
                }
            }
        }
    })
//    LaunchedEffect(Unit) {
//        dataNavigation.currentStep = 1
//    }
    LaunchedEffect(Unit) {
        dataNavigation.currentStep = 1
    }
    DisposableEffect(Unit) {
        exoPlayer.setMediaItem(MediaItem.fromUri("android.resource://${context.packageName}/${R.raw.man1}"))
        exoPlayer.prepare()
        exoPlayer.playWhenReady = true
        onDispose {
            exoPlayer.release()
        }
    }
    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Green)
        .pointerInput(Unit) {
            val maxWidth = this.size.width
            detectTapGestures(
                onPress = {
                    isPlayVideo = false
                    val pressStartTime = System.currentTimeMillis()
                    Log.d("test","pause")
                    this.tryAwaitRelease()
                    isPlayVideo =true
                    val pressEndTime = System.currentTimeMillis()
                    val totalPressTime = pressEndTime - pressStartTime
                    if (totalPressTime < 200) {
                        val isTapOnRightThreeQuarters = (it.x > (maxWidth / 4))
                        if (isTapOnRightThreeQuarters) {
                            goToNextScreen()
                        }
                    }
                }
            )
        }
    ) {
        val (progress) = createRefs()
        AndroidView(
            factory = {
                PlayerView(it).apply {
                    player = exoPlayer
                    useController = false
                    resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FILL
                }
            },
            update = {
                if(isPlayVideo){
                    config = config.resume()
                    exoPlayer.playWhenReady = true
                    it.onResume()
                }else{
                    it.player = exoPlayer
                    config = config.pause()
                    it.onPause()
                    it.player?.pause()
                    it.player?.playWhenReady = false
                }
            }

        )

        GSlicedProgressBar(
            modifier = Modifier
                .height(40.dp)
                .padding(18.dp, 0.dp)
                .fillMaxWidth()
                .constrainAs(progress) {
                    top.linkTo(parent.top)
                    height = Dimension.wrapContent
                },
            ReminderConstants.TOTAL_STEPS,
            1,
            config,
            videoUris.toInt(),
            goToNextScreen
        )
    }
}

@Composable
fun AnimatedTextExample() {
    var isVisibleCountOrder by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        Button(onClick = { isVisibleCountOrder = !isVisibleCountOrder }) {
            Text(text = "Toggle Visibility")
        }

        AnimatedVisibility(
            visible = isVisibleCountOrder,
            enter = scaleIn(initialScale = 0.2f, animationSpec = tween(500)),
            exit = scaleOut(targetScale = 1f, animationSpec = tween(500))
        ) {
            Box {
                Text(
                    text = "Hello World!",
                    style = TextStyle(
                        platformStyle = PlatformTextStyle(includeFontPadding = false)
                    ),
                    color = Color.Green,
                    fontSize = 52.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

fun getVideoUrisWithDurations(context: Context): List<Pair<String, Long>> {
    val videoResources = listOf(
        R.raw.man1,
        R.raw.man2,
        R.raw.man3,
        R.raw.man4,
        R.raw.man5
    )

    val videoUrisWithDurations = videoResources.map { resId ->
        val uri = "android.resource://${context.packageName}/$resId"
        val duration = getVideoDuration(context, uri)
        Pair(uri, duration)
    }

    return videoUrisWithDurations
}

fun getVideoDuration(context: Context, uri: String): Long {
    val retriever = MediaMetadataRetriever()
    try {
        retriever.setDataSource(context, android.net.Uri.parse(uri))
        val duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
        return duration?.toLong() ?: 0L
    } catch (e: Exception) {
        e.printStackTrace()
        return 0L
    } finally {
        retriever.release()
    }
}

//@Composable
//fun TestTypeText() {
//    val configNormal = ConfigTextWriter(
//        color = Color.Black,
//        textSize = 16.sp,
//        font = FontWeight.Normal
//    )
//
//    val configHighlight = ConfigTextWriter(
//        color = Color.Red,
//        textSize = 18.sp,
//        font = FontWeight.Bold
//    )
//
//    TypewriterTextEffectView(
//        modifier = Modifier.fillMaxWidth(),
//        textData = "Welcome to Jetpack Compose!",
//        textHighLight = listOf("Jetpack", "ma"),
//        configTextNormal = configNormal,
//        configTextHighLight = configHighlight,
//        isShowFull = false,
//        isVideoPlaying = false
//    ) { isFinish ->
//        if (isFinish) {
//            // Hiệu ứng hoàn tất
//            println("Effect completed!")
//        }
//    }
//
//}

@Composable
fun ScrollingMonthBox(selectedMonth: String) {
    var count by remember { mutableStateOf(0) }
    LaunchedEffect(Unit) {
        while (count <= 5) {
            delay(2000)
            count++
        }
    }
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        AnimatedContent(
            targetState = count,
            label = "animation Content",
            transitionSpec = {
                slideInVertically(
                    initialOffsetY = { -it },
                    animationSpec = tween(1000)
                ) togetherWith slideOutVertically(
                    targetOffsetY = { it },
                    animationSpec = tween(1000)
                )
            }
        ) { targetCount ->
            Text(
                text = "Tháng ${targetCount}",
                fontSize = 22.sp,
                fontFamily = FontFamily.Serif,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
            )
        }
//        Spacer(modifier = Modifier.size(8.dp))
//        Button(onClick ={count++} ) {
//            Text(
//                text= "add number",
//                fontSize = 22.sp
//            )
//        }


    }
}

@Composable
fun AnimatedBoxWithEffect() {
    var isVisibleText by remember { mutableStateOf(false) }
    var animation by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(3000L)
        animation = true
    }

    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.LightGray)
    ) {
        val boxRef = createRef()
        val boxGuideline = createGuidelineFromTop(if (animation) 0.2f else 0.5f)
        Box(
            modifier = Modifier
                .size(100.dp)
                .constrainAs(boxRef) {
                    top.linkTo(boxGuideline)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
                .background(Color.Red)
        )
    }
}





