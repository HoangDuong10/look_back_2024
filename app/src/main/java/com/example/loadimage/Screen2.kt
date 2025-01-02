package com.example.loadimage

import android.graphics.Bitmap
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import androidx.navigation.NavController
import dev.shreyaspatil.capturable.Capturable
import dev.shreyaspatil.capturable.controller.rememberCaptureController
import kotlinx.coroutines.delay
import kotlin.random.Random

@Composable
fun GReminderScreen2(
    isShowFull: Boolean,
    steps: Int,
    currentStep: Int,
    nextScreen: (Int) -> Unit,
    previousScreen: (Int) -> Unit,
) {
//    Screen2(
//        modifier = Modifier,
//        steps = steps,
//        currentStep = currentStep,
//        navigation,
//        nextScreen = nextScreen,
//        previousScreen = previousScreen
//    )
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun Screen2(
    modifier: Modifier = Modifier,
    nextScreen: () -> Unit,
    previousScreen: () -> Unit,
    data: ReminderDataNavigation
) {
    val context = LocalContext.current
    var currentStepState by remember { mutableStateOf(data.currentStep) }
    var isVisibleText1 by remember { mutableStateOf(false) }
    var isVisibleText2 by remember { mutableStateOf(false) }
    var isVisibleText3 by remember { mutableStateOf(false) }
    var isVisibleText4 by remember { mutableStateOf(false) }
    var isVideoPlaying by remember { mutableStateOf(true) }

    val lifecycleOwner = LocalLifecycleOwner.current
    val videoUris =
        getVideoDuration(context, "android.resource://${context.packageName}/${R.raw.man2}")
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
        data.currentStep += 1
        nextScreen.invoke()
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
    DisposableEffect(Unit) {
        onDispose {
            exoPlayer.release()
        }
    }
    LaunchedEffect(Unit) {
        exoPlayer.setMediaItem(MediaItem.fromUri("android.resource://${context.packageName}/${R.raw.man2}"))
        exoPlayer.prepare()
        exoPlayer.playWhenReady = true
    }

    val goToPreviousScreen = {
        config = config.reset()
        data.currentStep -= 1
        previousScreen.invoke()
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
//                    isPause1 = true
                }

                else -> Unit
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
    LaunchedEffect(isVideoPlaying) {
        if (exoPlayer.currentPosition > 0L && isVideoPlaying) {
            delay(2000 - exoPlayer.currentPosition)
            isVisibleText1 = true
        } else if (exoPlayer.currentPosition == 0L) {
            delay(2000)
            isVisibleText1 = true
        }
    }
    val captureController = rememberCaptureController()
    Box() {
        Capturable(
            onCaptured = { imageBitmap, _ ->
                val bitmap = imageBitmap?.asAndroidBitmap()
                if (bitmap != null) {
                    val uri = saveImageToCache(context, bitmap)
                    if (uri != null) {
                        shareImage(context, bitmap)
                    } else {
                        // Xử lý khi không lưu được ảnh
                    }
                }
            },

//            modifier = Modifier.fillMaxSize(),
            controller = captureController
        ) {
            ConstraintLayout(
                modifier = modifier
                    .fillMaxSize()
                    .background(Color.Green)
                    .pointerInput(Unit) {
                        val maxWidth = this.size.width
                        detectTapGestures(
                            onPress = {
                                isVideoPlaying = false
//                        isVideoPlaying = false
                                val pressStartTime = System.currentTimeMillis()
                                this.tryAwaitRelease()
                                isVideoPlaying = true
//                        isVideoPlaying = true
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
                val (box, progress, text1, text2, text4, text5, ivShare) = createRefs()
                val letterTopGuideline = createGuidelineFromTop(0.5f)
                Box(Modifier.fillMaxSize()) {
                    AndroidView(
                        factory = {
                            PlayerView(it).apply {
                                player = exoPlayer
                                useController = false
                                resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FILL
                            }
                        },
                        update = {
                            if (isVideoPlaying) {
                                config = config.resume()
                                exoPlayer.playWhenReady = true
                                it.onResume()
                            } else {
                                it.player = exoPlayer
                                config = config.pause()
                                it.onPause()
                                it.player?.pause()
                                it.player?.playWhenReady = false
                            }
                        }
                    )
                }
                Box(
                    modifier = Modifier
                        .constrainAs(text1) {
                            top.linkTo(letterTopGuideline)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                        }
                ) {
                    AnimatedVisibility(
                        visible = isVisibleText1,
                        enter = scaleIn(
                            initialScale = 0.2f,
                            animationSpec = tween(durationMillis = ReminderConstants.TIME_SCREEN_1)
                        ),
                        exit = scaleOut(
                            targetScale = 1f,
                            animationSpec = tween(durationMillis = ReminderConstants.TIME_SCREEN_1)
                        )
                    ) {
                        TypewriterTextEffectView(
                            modifier = Modifier,
                            "Nhìn lại 2024 bạn đã có 1 \n hành trình thật ấn tượng",
                            textHighLight = listOf(),
                            configTextHighLight = ConfigTextWriter(
                                Color.Black,
                                18.sp,
                                FontWeight.Medium
                            ),
                            configTextNormal = ConfigTextWriter(
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
                    modifier = Modifier.constrainAs(text2) {
                        top.linkTo(text1.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
                ) {
                    AnimatedVisibility(
                        visible = isVisibleText2,
                        enter = scaleIn(
                            initialScale = 0.2f,
                            animationSpec = tween(durationMillis = ReminderConstants.TIME_SCREEN_1)
                        ),
                        exit = scaleOut(
                            targetScale = 1f,
                            animationSpec = tween(durationMillis = ReminderConstants.TIME_SCREEN_1)
                        )
                    ) {
                        if (this.transition.currentState == this.transition.targetState) {
                            isVisibleText3 = true
                        }
                        Text(
                            text = buildAnnotatedString {
                                withStyle(
                                    style = ParagraphStyle(lineHeight = 21.sp)
                                ) {
                                    pushStyle(
                                        SpanStyle(
                                            fontWeight = FontWeight.Medium,
                                            fontSize = 55.sp,
                                            color = Color.Green,
                                        )
                                    )
                                    append(data.data?.order)
                                    pop()
                                    append("\n")
                                    pushStyle(
                                        SpanStyle(
                                            fontWeight = FontWeight.Medium,
                                            fontSize = 24.sp,
                                            color = Color.Green,
                                        )
                                    )
                                    append("đơn hàng")
                                    pop()
                                }
                            },
                            textAlign = TextAlign.Center
                        )

                    }
                }

                Box(
                    modifier = Modifier
                        .constrainAs(text4) {
                            top.linkTo(text2.bottom, margin = 0.dp)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                        }
                        .offset(y = (-10).dp)
                ) {
                    AnimatedVisibility(
                        visible = isVisibleText3,
                        enter = scaleIn(
                            initialScale = 0.2f,
                            animationSpec = tween(durationMillis = ReminderConstants.TIME_SCREEN_1)
                        ),
                        exit = scaleOut(
                            targetScale = 1f,
                            animationSpec = tween(durationMillis = ReminderConstants.TIME_SCREEN_1)
                        )
                    ) {
                        TypewriterTextEffectView(
                            modifier = Modifier,
                            "được chốt thành công",
                            textHighLight = listOf(),
                            configTextHighLight = ConfigTextWriter(
                                Color.Black,
                                18.sp,
                                FontWeight.Medium,
                            ),
                            configTextNormal = ConfigTextWriter(
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
                Box(
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .constrainAs(text5) {
                            top.linkTo(text4.bottom)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                        }
                ) {
                    AnimatedVisibility(
                        visible = isVisibleText4,
                        enter = scaleIn(
                            initialScale = 0.2f,
                            animationSpec = tween(durationMillis = ReminderConstants.TIME_SCREEN_1)
                        ),
                        exit = scaleOut(
                            targetScale = 1f,
                            animationSpec = tween(durationMillis = ReminderConstants.TIME_SCREEN_1)
                        )
                    ) {
                        TypewriterTextEffectView(
                            modifier = Modifier,
                            "Bạn lọt ${data.data?.topNhaBan} nhà bán \n chốt được nhiều đơn hàng nhất!",
                            textHighLight = listOf("Top 100"),
                            configTextHighLight = ConfigTextWriter(
                                Color.Green,
                                18.sp,
                                FontWeight.Medium
                            ),
                            configTextNormal = ConfigTextWriter(
                                Color.Black,
                                18.sp,
                                FontWeight.Medium
                            ),
                            isShowFull = false,
                            isVideoPlaying = isVideoPlaying
                        ) {
                        }

                    }
                }
//        }
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
                            animationSpec = tween(durationMillis = ReminderConstants.TIME_SCREEN_1)
                        ),
                        exit = scaleOut(
                            targetScale = 1f,
                            animationSpec = tween(durationMillis = ReminderConstants.TIME_SCREEN_1)
                        )
                    ) {
                        Image(
                            painter = painterResource(R.drawable.share),
                            contentDescription = null,
                            contentScale = ContentScale.Fit,
                            modifier = Modifier
                                .width(132.dp)
                                .height(32.dp)
                                .clickable {
                                    captureController.capture()
                                }
                        )
                    }
                }
                GSlicedProgressBar(
                    modifier = Modifier
                        .height(40.dp)
                        .padding(18.dp, 0.dp)
                        .fillMaxWidth()
                        .constrainAs(progress) {
                            top.linkTo(parent.top)
                        },
                    data.steps,
                    data.currentStep,
                    config,
                    videoUris.toInt() + 4000,
                    goToNextScreen
                )
            }
        }
    }
}