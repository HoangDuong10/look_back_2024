package com.example.loadimage

import android.util.Log
import android.widget.Toast
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Popup
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import com.example.loadimage.ui.theme.LoadImageTheme
import dev.shreyaspatil.capturable.Capturable
import dev.shreyaspatil.capturable.controller.rememberCaptureController
import kotlinx.coroutines.delay
import kotlin.random.Random

@Composable
fun Screen4(
    nextScreen: () -> Unit,
    previousScreen: () -> Unit,
    data: LookBackDataNavigation
) {


    val context = LocalContext.current
    var isVisibleText by remember { mutableStateOf(false) }
    var animation by remember { mutableStateOf(false) }
    var isPlayVideo by remember { mutableStateOf(true) }
    var isCature by remember { mutableStateOf(false) }
    var stateReady by remember { mutableStateOf(false) }

    val videoUris =
        getVideoDuration(context, "android.resource://${context.packageName}/${R.raw.man4}")
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
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> {
                    isPlayVideo = true
                }

                Lifecycle.Event.ON_PAUSE -> {
                    isPlayVideo = false
                }

                Lifecycle.Event.ON_STOP -> {
                }

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
                Player.STATE_READY -> {
                    stateReady = false
                }
            }
        }
    })
    val goToNextScreen = {
        config = config.reset()
        data.currentStep += 1
        nextScreen.invoke()
    }

    val goToPreviousScreen = {
        config = config.reset()
        data.currentStep -= 1
        previousScreen.invoke()
    }


    DisposableEffect(Unit) {
        onDispose {
            exoPlayer.release()
        }
    }
    var count by remember { mutableStateOf(1) }
    var startVideo by remember { mutableStateOf(0L) }
    var endVideo by remember { mutableStateOf(0L) }
    LaunchedEffect(isPlayVideo) {
        if (isPlayVideo) {
            var isFirstTime = true
            while (count < data.data?.thang!!.toInt()) {
                if (isFirstTime && count == 1) {
                    delay(700)
                    isFirstTime = false
                } else {
                    delay(150)
                }
                count++
            }
            endVideo = System.currentTimeMillis()
            animation = true
        }
        if (exoPlayer.currentPosition > 0L && isPlayVideo) {
            delay(data.data?.thang!!.toInt() * 150 + 500 - exoPlayer.currentPosition)
            isVisibleText = true

        }
    }
    LaunchedEffect(Unit) {
        exoPlayer.setMediaItem(MediaItem.fromUri("android.resource://${context.packageName}/${R.raw.man4}"))
        exoPlayer.prepare()
        exoPlayer.playWhenReady = true
        startVideo = System.currentTimeMillis()
    }
    val animatedGuidelineFraction by animateFloatAsState(
        targetValue = if (animation) 0.15f else 0.4f,
        animationSpec = tween(durationMillis = 500)
    )
    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .pointerInput(Unit) {
                val maxWidth = this.size.width
                detectTapGestures(
                    onPress = {
                        isPlayVideo = false
                        val pressStartTime = System.currentTimeMillis()
                        this.tryAwaitRelease()
                        isPlayVideo = true
                        val pressEndTime = System.currentTimeMillis()
                        val totalPressTime = pressEndTime - pressStartTime
                        if (totalPressTime < 200) {
                            val isTapOnRightThreeQuarters = (it.x > (maxWidth / 4))
                            if (isTapOnRightThreeQuarters) {
                                goToNextScreen()
                            } else {
                                goToPreviousScreen()
                            }
                        }
                    }
                )
            }
    ) {
        val (imageMonth, textTitle, progress, ivShare) = createRefs()
        val boxGuideline = createGuidelineFromTop(animatedGuidelineFraction)
        AndroidView(
            factory = {
                PlayerView(it).apply {
                    player = exoPlayer
                    useController = false
                    resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FILL
                }
            },
            update = {
                it.player = exoPlayer
                if (isPlayVideo) {
                    config = config.resume()
                    exoPlayer.playWhenReady = true
                    Log.d("lifecycle11", "222222")
                    it.onResume()
                } else {
                    config = config.pause()
                    it.onPause()
                    it.player?.pause()
                    it.player?.playWhenReady = false
                }
            }

        )
        ConstraintLayout(
            modifier = Modifier
                .background(Color.Transparent)
                .constrainAs(imageMonth) {
                    top.linkTo(boxGuideline)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }

        ) {
            val (ivMonth, tvMonth) = createRefs()
            val monthGuideline = createGuidelineFromTop(0.6f)
            Image(
                painter = painterResource(R.drawable.month),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .size(201.dp)
                    .constrainAs(ivMonth) {
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        top.linkTo(parent.top)
                    }
            )
            AnimatedContent(
                modifier = Modifier.constrainAs(tvMonth) {
                    start.linkTo(ivMonth.start)
                    end.linkTo(ivMonth.end)
                    top.linkTo(monthGuideline)
                },
                targetState = count,
                label = "animation Content",
                transitionSpec = {
                    slideInVertically(
                        initialOffsetY = { it },
                        animationSpec = tween(150)
                    ) togetherWith slideOutVertically(
                        targetOffsetY = { -it },
                        animationSpec = tween(150)
                    )
                }
            ) { targetCount ->
                Text(
                    text = "Tháng ${targetCount}",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily(Font(R.font.forma_djr)),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),

                    )
            }
        }
        Box(
            modifier = Modifier
                .constrainAs(textTitle) {
                    top.linkTo(imageMonth.bottom, margin = 22.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
        ) {
            AnimatedVisibility(
                visible = isVisibleText,
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
                    "là tháng ghi nhận kết quả kinh doanh\nsuất sắc nhất của bạn",
                    textHighLight = listOf(),
                    configTextHighLight = ConfigText(
                        Color.Black,
                        18.sp,
                        FontWeight.Medium
                    ),
                    configTextNormal = ConfigText(
                        Color.White,
                        18.sp,
                        FontWeight.Medium
                    ),
                    isShowFull = false,
                    isVideoPlaying = isPlayVideo
                ) {

                }

            }
        }
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
            data.currentStep,
            config,
            videoUris.toInt(),
            goToNextScreen
        )
        Box(
            modifier = Modifier.constrainAs(ivShare) {
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                bottom.linkTo(parent.bottom, margin = 22.dp)
            }
        ) {
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
                ShareButton(
                    colorText = colorResource(R.color.main_color),
                    colorBg = Color.White,
                    onClick = {
                        isCature = true
                        isPlayVideo = false
                    }
                )
            }
        }
    }
    if (isCature) {
        CaptureScreenshotScreen4(
            data = data,
            showPopupScreen = { isCature = false },
        )
    }
}

@Composable
fun CaptureScreenshotScreen4(
    data: LookBackDataNavigation,
    showPopupScreen: (Boolean) -> Unit
) {
    val captureController = rememberCaptureController()
    val context = LocalContext.current
    var isLayoutReady by remember { mutableStateOf(false) }
    val mainColor = colorResource(R.color.main_color)
    val shareLauncher = rememberShareLauncher(context) { success ->
        showPopupScreen(success)
    }
    if (isLayoutReady) {
        LaunchedEffect(Unit) {
            captureController.capture()
        }
    }
    Popup {
        Capturable(
            onCaptured = { imageBitmap, _ ->
                val bitmap = imageBitmap?.asAndroidBitmap()
                if (bitmap != null) {
                    val imageUri = saveImageToCache(context, bitmap)
                    if (imageUri != null) {
                        shareImage(context, shareLauncher, imageUri)
                    } else {
                        Toast.makeText(context, "Không thể lưu ảnh!", Toast.LENGTH_SHORT).show()
                    }
                }
            },
            controller = captureController
        ) {
            ConstraintLayout(
                modifier = Modifier
                    .fillMaxSize()
                    .onGloballyPositioned {
                        isLayoutReady = true
                    }
            ) {
                val imageGuidelineFromTop = createGuidelineFromTop(0.15f)
                val (imageMonth, textTitle, progress) = createRefs()
                Image(
                    painter = painterResource(R.drawable.bg4),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.FillBounds,
                    alignment = Alignment.Center
                )
                ConstraintLayout(
                    modifier = Modifier
                        .background(Color.Transparent)
                        .constrainAs(imageMonth) {
                            top.linkTo(imageGuidelineFromTop)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                        }

                ) {
                    val (ivMonth, tvMonth) = createRefs()
                    val monthGuideline = createGuidelineFromTop(0.6f)
                    Image(
                        painter = painterResource(R.drawable.month),
                        contentDescription = null,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .size(201.dp)
                            .constrainAs(ivMonth) {
                                start.linkTo(parent.start)
                                end.linkTo(parent.end)
                                top.linkTo(parent.top)
                            }
                    )
                    Text(
                        text = "Tháng ${data.data?.thang}",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily(Font(R.font.forma_djr)),
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .constrainAs(tvMonth) {
                                start.linkTo(ivMonth.start)
                                end.linkTo(ivMonth.end)
                                top.linkTo(monthGuideline)
                            },
                    )
                }
                Box(
                    modifier = Modifier
                        .constrainAs(textTitle) {
                            top.linkTo(imageMonth.bottom, margin = 22.dp)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                        }
                ) {
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
                            "là tháng ghi nhận kết quả kinh doanh\nsuất sắc nhất của bạn",
                            textHighLight = listOf(),
                            configTextHighLight = ConfigText(
                                Color.Black,
                                18.sp,
                                FontWeight.Medium
                            ),
                            configTextNormal = ConfigText(
                                Color.White,
                                18.sp,
                                FontWeight.Medium
                            ),
                            isShowFull = true,
                            isVideoPlaying = true
                        ) {

                        }

                    }
                }
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
                    data.currentStep,
                    ProgressConfig(
                        action = LookBackConstants.RESET,
                        configValue = Random.nextInt()
                    ),
                    0,
                    {}
                )

            }
        }

    }

}

@Preview(showBackground = true)
@Composable
fun Dialog4Preview() {
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
    var navigationData = LookBackDataNavigation(
        LookBackConstants.TOTAL_STEPS,
        LookBackConstants.CURRENT_STEP_DEFAULT,
        data = fakeData
    )
    LoadImageTheme {
        CaptureScreenshotScreen4(data = navigationData, showPopupScreen = {})
    }
}
