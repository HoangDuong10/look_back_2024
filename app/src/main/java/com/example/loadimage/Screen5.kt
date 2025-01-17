package com.example.loadimage

import android.util.Log
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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



@OptIn(ExperimentalAnimationApi::class)
@Composable
fun Screen5(
    modifier: Modifier = Modifier,
    nextScreen: () -> Unit,
    previousScreen: () -> Unit,
    data: LookBackDataNavigation
) {
    val context = LocalContext.current
    var isVisibleText1 by remember { mutableStateOf(false) }
    var isVisibleText2 by remember { mutableStateOf(false) }
    var isVisibleText3 by remember { mutableStateOf(false) }
    var isVisibleText4 by remember { mutableStateOf(false) }
    var isVideoPlaying by remember { mutableStateOf(true) }
    var isCature by remember { mutableStateOf(false) }
    val lifecycleOwner = LocalLifecycleOwner.current
    val videoUris =
        getVideoDuration(context, "android.resource://${context.packageName}/${R.raw.man5}")
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
            data.currentStep+=1
            nextScreen.invoke()
    }
    exoPlayer.addListener(object : Player.Listener {
        override fun onPlaybackStateChanged(playbackState: Int) {
            when (playbackState) {
                Player.STATE_ENDED -> {
                    exoPlayer.seekTo(exoPlayer.duration)
                    isVisibleText1 = true
                }

                Player.STATE_READY -> Log.d("PlayerState", "Player đang sẵn sàng")
            }
        }
    })
    DisposableEffect(Unit) {
        onDispose {
            exoPlayer.release()
        }
    }
    LaunchedEffect(Unit) {
        exoPlayer.setMediaItem(MediaItem.fromUri("android.resource://${context.packageName}/${R.raw.man5}"))
        exoPlayer.prepare()
        exoPlayer.playWhenReady = true
    }

    val goToPreviousScreen = {
            config = config.reset()
            data.currentStep-=1
            previousScreen.invoke()
    }

    LaunchedEffect(isVideoPlaying) {
        if (exoPlayer.currentPosition > 0L && isVideoPlaying) {
            delay(2000 - exoPlayer.currentPosition)
            isVisibleText1 = true

        } else if (exoPlayer.currentPosition == 0L ) {
            delay(2000)
            isVisibleText1 = true
        }
    }
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> {
                    isVideoPlaying = true
                }

                Lifecycle.Event.ON_PAUSE -> {
                    isVideoPlaying = false
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

    ConstraintLayout(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Green)
            .pointerInput(Unit) {
                val maxWidth = this.size.width
                detectTapGestures(
                    onPress = {
                        isVideoPlaying = false
                        val pressStartTime = System.currentTimeMillis()
                        this.tryAwaitRelease()
                        isVideoPlaying = true
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
        val ( progress, text1, text2, text4, ivShare) = createRefs()
        val letter1StartGuideline = createGuidelineFromStart(0.2f)
        val letter1EndGuideline = createGuidelineFromEnd(0.2f)
        val letterTopGuideline = createGuidelineFromTop(0.45f)
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
                if (isVideoPlaying) {
                    config = config.resume()
                    exoPlayer.playWhenReady = true
                    it.onResume()
                } else {
                    config = config.pause()
                    it.onPause()
                    it.player?.pause()
                    it.player?.playWhenReady = false
                }
            }
        )
        Box(
            modifier = Modifier
                .constrainAs(text1) {
                    top.linkTo(letterTopGuideline)
                }
        ) {
            AnimatedVisibility(
                visible = isVisibleText1,
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
                    "Trong danh mục sản phẩm\n yêu thích của khách hàng",
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
                    isVideoPlaying = isVideoPlaying
                ) {
                    isVisibleText2 = true
                }
            }
        }

        Box(
            modifier = Modifier.padding(top=6.dp)
                .background(Color.Red)
                .constrainAs(text2) {
                    top.linkTo(text1.bottom)
                    start.linkTo(letter1StartGuideline)
                    end.linkTo(letter1EndGuideline)
                    width = Dimension.fillToConstraints
                }
        ) {
            AnimatedVisibility(
                visible = isVisibleText2,
                enter = scaleIn(
                    initialScale = 0.2f,
                    animationSpec = tween(durationMillis = LookBackConstants.TIME_SCREEN_1)
                ),
                exit = scaleOut(
                    targetScale = 1f,
                    animationSpec = tween(durationMillis = LookBackConstants.TIME_SCREEN_1)
                )
            ) {
                if (this.transition.currentState == this.transition.targetState) {
                    isVisibleText3 = true
                }

                   Text(
                       text = data.data?.name?:"",
                       fontWeight = FontWeight.Medium,
                       fontSize = 40.sp,
                       color = colorResource(R.color.main_color),
                       textAlign = TextAlign.Center,
                       modifier = Modifier.fillMaxWidth()
                   )


            }
        }

        Box(
            modifier = Modifier
                .padding(top = 8.dp)
                .constrainAs(text4) {
                    top.linkTo(text2.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
        ) {
            AnimatedVisibility(
                visible = isVisibleText3,
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
                    "giữ vị trí dẫn đầu",
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
                    isVideoPlaying = isVideoPlaying
                ) {
                    isVisibleText4 = true
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
                },
            LookBackConstants.TOTAL_STEPS,
            data.currentStep,
            config,
            videoUris.toInt() + 4000,
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
                visible = isVisibleText1,
                enter = scaleIn(
                    initialScale = 0.2f,
                    animationSpec = tween(durationMillis = LookBackConstants.TIME_SCREEN_1)
                ),
                exit = scaleOut(
                    targetScale = 1f,
                    animationSpec = tween(durationMillis = LookBackConstants.TIME_SCREEN_1)
                )
            ) {
                ShareButton {
                    isCature = true
                    isVideoPlaying = false
                }
            }
        }
    }
    if(isCature){
        CaptureScreenshotScreen5(
            data = data,
            showPopupScreen = {isCature = false},
        )
    }
}

@Composable
fun CaptureScreenshotScreen5(
    data: LookBackDataNavigation,
    showPopupScreen: (Boolean) -> Unit
) {
    val captureController = rememberCaptureController()
    val context = LocalContext.current
    var isLayoutReady by remember { mutableStateOf(false) }
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
                val ( progress, text1, text2, text4,) = createRefs()
                val letterTopGuideline = createGuidelineFromTop(0.45f)
                val letter1StartGuideline = createGuidelineFromStart(0.2f)
                val letter1EndGuideline = createGuidelineFromEnd(0.2f)
                Image(
                    painter = painterResource(R.drawable.bg5),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.FillBounds,
                    alignment = Alignment.Center
                )
                Box(
                    modifier = Modifier
                        .constrainAs(text1) {
                            top.linkTo(letterTopGuideline)
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
                            "Trong danh mục sản phẩm\n yêu thích của khách hàng",
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
                            isShowFull = true,
                            isVideoPlaying = true
                        ) {
                        }
                    }
                }

                Box(
                    modifier = Modifier.padding(top=6.dp)
                        .constrainAs(text2) {
                            top.linkTo(text1.bottom)
                            start.linkTo(letter1StartGuideline)
                            end.linkTo(letter1EndGuideline)
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
                        Text(
                            text = data.data?.name?:"",
                            fontWeight = FontWeight.Medium,
                            fontSize = 40.sp,
                            color = colorResource(R.color.main_color),
                        )

                    }
                }

                Box(
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .constrainAs(text4) {
                            top.linkTo(text2.bottom)
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
                            "giữ vị trí dẫn đầu",
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
fun Dialog5Preview(){
    val fakeData = FakeData(
        order = "12345",
        topNhaBan = "Top 100",
        doanhthu = 32124,
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
        CaptureScreenshotScreen5(data = navigationData, showPopupScreen = {})
    }
}