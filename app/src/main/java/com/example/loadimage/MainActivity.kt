package com.example.loadimage

import android.app.Activity
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
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.res.painterResource
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
import androidx.navigation.NavBackStackEntry
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
//            val navController = rememberAnimatedNavController()
            val navController = rememberNavController()
            val fakeData = FakeData(
                order = "12345",
                topNhaBan = "Top 100",
                doanhthu = 156066000000L,
                thang = "6",
                name = "John Doe",
                slKhachHang = "150",
                topYeuThich = "Top 100",
                khachHang = "500",
                danhGiaKH = "22",
                danhGiaCuaBan = "12",
                soLanSD = "20"
            )
            var navigationData = LookBackDataNavigation(
                LookBackConstants.TOTAL_STEPS,
                LookBackConstants.CURRENT_STEP_DEFAULT,
                data = fakeData
            )
            NavHost(navController = navController, startDestination = "screen1") {
                composable(
                    LookBackNavigation.Screen1.route,
                    enterTransition = ::slideInToRight,
                    exitTransition = ::slideOutToLeft
                ) {
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
                    arguments = listOf(navArgument("data") { type = NavType.StringType }),
//                    enterTransition = ::slideInToLeft,
//                    exitTransition = ::slideOutToLeft,
//                    popEnterTransition = ::slideInToRight,
//                    popExitTransition = ::slideOutToRight
                ) { backStackEntry ->
                    val json = backStackEntry.arguments?.getString("data") ?: ""
                    val reminderData = Gson().fromJson(json, LookBackDataNavigation::class.java)
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
                    arguments = listOf(navArgument("data") { type = NavType.StringType }),
//                    enterTransition = ::slideInToLeft,
//                    exitTransition = ::slideOutToLeft,
//                    popEnterTransition = ::slideInToRight,
//                    popExitTransition = ::slideOutToRight
                ) { backStackEntry ->
                    val json = backStackEntry.arguments?.getString("data") ?: ""
                    val reminderData = Gson().fromJson(json, LookBackDataNavigation::class.java)
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
                    arguments = listOf(navArgument("data") { type = NavType.StringType }),
//                    enterTransition = ::slideInToLeft,
//                    exitTransition = ::slideOutToLeft,
//                    popEnterTransition = ::slideInToRight,
//                    popExitTransition = ::slideOutToRight
                ) { backStackEntry ->
                    val json = backStackEntry.arguments?.getString("data") ?: ""
                    val reminderData = Gson().fromJson(json, LookBackDataNavigation::class.java)
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
                    arguments = listOf(navArgument("data") { type = NavType.StringType }),
//                    enterTransition = ::slideInToLeft,
//                    exitTransition = ::slideOutToLeft,
//                    popEnterTransition = ::slideInToRight,
//                    popExitTransition = ::slideOutToRight
                ) { backStackEntry ->
                    val json = backStackEntry.arguments?.getString("data") ?: ""
                    val reminderData = Gson().fromJson(json, LookBackDataNavigation::class.java)
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
                    arguments = listOf(navArgument("data") { type = NavType.StringType }),
//                    enterTransition = ::slideInToLeft,
//                    exitTransition = ::slideOutToLeft,
//                    popEnterTransition = ::slideInToRight,
//                    popExitTransition = ::slideOutToRight
                ) { backStackEntry ->
                    val json = backStackEntry.arguments?.getString("data") ?: ""
                    val reminderData = Gson().fromJson(json, LookBackDataNavigation::class.java)
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
                    arguments = listOf(navArgument("data") { type = NavType.StringType }),
//                    enterTransition = ::slideInToLeft,
//                    exitTransition = ::slideOutToLeft,
//                    popEnterTransition = ::slideInToRight,
//                    popExitTransition = ::slideOutToRight
                ) { backStackEntry ->
                    val json = backStackEntry.arguments?.getString("data") ?: ""
                    val reminderData = Gson().fromJson(json, LookBackDataNavigation::class.java)
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
                    arguments = listOf(navArgument("data") { type = NavType.StringType }),
//                    enterTransition = ::slideInToLeft,
//                    exitTransition = ::slideOutToLeft,
//                    popEnterTransition = ::slideInToRight,
//                    popExitTransition = ::slideOutToRight
                ) { backStackEntry ->
                    val json = backStackEntry.arguments?.getString("data") ?: ""
                    val reminderData = Gson().fromJson(json, LookBackDataNavigation::class.java)
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
                    arguments = listOf(navArgument("data") { type = NavType.StringType }),
//                    enterTransition = ::slideInToLeft,
//                    exitTransition = ::slideOutToLeft,
//                    popEnterTransition = ::slideInToRight,
//                    popExitTransition = ::slideOutToRight
                ) { backStackEntry ->
                    val json = backStackEntry.arguments?.getString("data") ?: ""
                    val reminderData = Gson().fromJson(json, LookBackDataNavigation::class.java)
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
                    arguments = listOf(navArgument("data") { type = NavType.StringType }),
//                    enterTransition = ::slideInToLeft,
//                    exitTransition = ::slideOutToLeft,
//                    popEnterTransition = ::slideInToRight,
//                    popExitTransition = ::slideOutToRight
                ) { backStackEntry ->
                    val json = backStackEntry.arguments?.getString("data") ?: ""
                    val reminderData = Gson().fromJson(json, LookBackDataNavigation::class.java)
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
                    arguments = listOf(navArgument("data") { type = NavType.StringType }),
//                    enterTransition = ::slideInToLeft,
//                    exitTransition = ::slideOutToLeft,
//                    popEnterTransition = ::slideInToRight,
//                    popExitTransition = ::slideOutToRight
                ) { backStackEntry ->
                    val json = backStackEntry.arguments?.getString("data") ?: ""
                    val reminderData = Gson().fromJson(json, LookBackDataNavigation::class.java)
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
                    arguments = listOf(navArgument("data") { type = NavType.StringType }),
//                    enterTransition = ::slideInToLeft,
//                    exitTransition = ::slideOutToLeft,
//                    popEnterTransition = ::slideInToRight,
//                    popExitTransition = ::slideOutToRight
                ) { backStackEntry ->
                    val json = backStackEntry.arguments?.getString("data") ?: ""
                    val reminderData = Gson().fromJson(json, LookBackDataNavigation::class.java)
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

fun slideInToLeft(scope: AnimatedContentTransitionScope<NavBackStackEntry>): EnterTransition {
    return scope.slideIntoContainer(
        AnimatedContentTransitionScope.SlideDirection.Left,
        animationSpec = tween(300)
    )
}

fun slideInToRight(scope: AnimatedContentTransitionScope<NavBackStackEntry>): EnterTransition {
    return scope.slideIntoContainer(
        AnimatedContentTransitionScope.SlideDirection.Right,
        animationSpec = tween(300)
    )
}

fun slideOutToLeft(scope: AnimatedContentTransitionScope<NavBackStackEntry>): ExitTransition {
    return scope.slideOutOfContainer(
        AnimatedContentTransitionScope.SlideDirection.Left,
        animationSpec = tween(300)
    )
}

fun slideOutToRight(scope: AnimatedContentTransitionScope<NavBackStackEntry>): ExitTransition {
    return scope.slideOutOfContainer(
        AnimatedContentTransitionScope.SlideDirection.Right,
        animationSpec = tween(300)
    )
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
    dataNavigation: LookBackDataNavigation
) {

    val context = LocalContext.current
    var isPlayVideo by remember { mutableStateOf(true) }
    val videoUris =
        getVideoDuration(context, "android.resource://${context.packageName}/${R.raw.man1}")
    var config by remember {
        mutableStateOf(
            ProgressConfig(
                action = LookBackConstants.RESET,
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
    var count by remember { mutableStateOf(0L) }
    LaunchedEffect(Unit) {
        count = System.currentTimeMillis()
        Log.d("timetest1","${count}")
    }
    var lastClickTime by remember { mutableStateOf(0L) }
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
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    val (x, y) = dragAmount

                    when {
                        y > 0 -> {
                            if (y > 20 && x > -10 && x < 10) {
                                (context as? Activity)?.finish()
                            }
                        }

                        y < 0 -> {}
                    }
                }
            }
            .pointerInput(Unit) {
                val maxWidth = this.size.width
//                var lastClickTime = 0L // Thời gian của lần click trước đó

                detectTapGestures(
                    onPress = {
                        isPlayVideo = false // Tạm dừng video khi nhấn
                        val pressStartTime = System.currentTimeMillis() // Thời gian bắt đầu nhấn
                        Log.d("test33", "Pause video at: $pressStartTime")

                        this.tryAwaitRelease() // Chờ người dùng nhả tay

                        isPlayVideo = true // Tiếp tục phát video sau khi nhả
                        val pressEndTime = System.currentTimeMillis() // Thời gian kết thúc nhấn
                        val totalPressTime = pressEndTime - pressStartTime // Thời gian nhấn giữ
                        val timeSinceLastClick = pressStartTime

                        Log.d("test33", "TotalPressTime: $totalPressTime, TimeSinceLastClick: $timeSinceLastClick")

                        // Kiểm tra điều kiện thời gian nhấn giữ và khoảng cách giữa 2 lần nhấn
                        Log.d("timetest1","${count} + ${pressStartTime} + ${pressStartTime-count}")
                        if (totalPressTime < 200&& pressStartTime-count>1000) {
                            lastClickTime = pressStartTime // Cập nhật thời gian nhấn cuối cùng
                            val isTapOnRightThreeQuarters = (it.x > (maxWidth / 4)) // Kiểm tra vị trí nhấn
                            if (isTapOnRightThreeQuarters) {
                                Log.d("test33", "Go to next screen")
                                goToNextScreen()
                            }
                        } else {
                            Log.d(
                                "test33",
                                "Ignored click: TotalPressTime=$totalPressTime, TimeSinceLastClick=$timeSinceLastClick"
                            )
                        }
                    }
                )
            }


    ) {
        val (progress,imgClose) = createRefs()
        AnimatedVisibility(
            visible = true,
            enter = scaleIn(
                initialScale = 0.2f,
                animationSpec = tween(durationMillis = LookBackConstants.TIME_SCREEN_1)
            ),
            exit = scaleOut(
                targetScale = 1f,
                animationSpec = tween(durationMillis = LookBackConstants.TIME_SCREEN_1)
            )
        ) {
            TextEffectView(
                modifier = Modifier,
                "Nhìn lại 2024 bạn đã có 1 \n hành trình thật ấn tượng Nhìn lại 2024 bạn đã có 1 \n" +
                        " hành trình thật ấn tượng Nhìn lại 2024 bạn đã có 1 \n" +
                        " hành trình thật ấn tượng Nhìn lại 2024 bạn đã có 1 \n" +
                        " hành trình thật ấn tượng hành trình thật ấn tượng Nhìn lại 2024 bạn đã có 1hành trình thật ấn tượng Nhìn lại 2024 bạn đã có 1hành trình thật ấn tượng Nhìn lại 2024 bạn đã có 1hành trình thật ấn tượng Nhìn lại 2024 bạn đã có 1hành trình thật ấn tượng Nhìn lại 2024 bạn đã có 1hành trình thật ấn tượng Nhìn lại 2024 bạn đã có 1hành trình thật ấn tượng Nhìn lại 2024 bạn đã có 1",
                textHighLight = listOf(),
                configTextHighLight = ConfigText(
                    Color.Black,
                    18.sp,
                    FontWeight.Medium
                ),
                configTextNormal = ConfigText(
                    Color.Black,
                    18.sp,
                    FontWeight.Medium
                ),
                isShowFull = false,
                isVideoPlaying = true
            ) {

            }
        }
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

        SlicedProgressBar(
            modifier = Modifier
                .height(40.dp)
                .padding(18.dp, 0.dp)
                .fillMaxWidth()
                .constrainAs(progress) {
                    top.linkTo(parent.top)
                    height = Dimension.wrapContent
                },
            LookBackConstants.TOTAL_STEPS,
            1,
            config,
            videoUris.toInt(),
            goToNextScreen
        )
        IconButton(
            onClick = {
                (context as? Activity)?.finish()
            },
            modifier = Modifier
                .constrainAs(imgClose) {
                    top.linkTo(parent.top, 32.dp)
                    end.linkTo(parent.end, 15.dp)
                }
                .size(22.dp),
            content = {
                Icon(
                    modifier = Modifier.fillMaxSize(),
                    painter = painterResource(id = R.drawable.ic_close),
                    contentDescription = "",
                    tint = Color.White
                )
            }
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





