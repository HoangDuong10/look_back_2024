package com.example.loadimage

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import androidx.navigation.NavController
import com.google.gson.Gson
import kotlinx.coroutines.delay
import kotlin.random.Random



@OptIn(ExperimentalAnimationApi::class)
@Composable
fun Screen7(
    modifier: Modifier = Modifier,
    nextScreen: () -> Unit,
    previousScreen: () -> Unit,
    data: ReminderDataNavigation
) {
    val context = LocalContext.current
    var isVisibleText1 by remember { mutableStateOf(false) }
    var isVisibleText2 by remember { mutableStateOf(false) }
    var isVisibleText3 by remember { mutableStateOf(false) }
    var isVisibleText4 by remember { mutableStateOf(false) }
    var isPlayVideo by remember { mutableStateOf(true) }
    val lifecycleOwner = LocalLifecycleOwner.current
    val videoUris =
        getVideoDuration(context, "android.resource://${context.packageName}/${R.raw.man7}")
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
        data.currentStep+=1
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
        exoPlayer.setMediaItem(MediaItem.fromUri("android.resource://${context.packageName}/${R.raw.man7}"))
        exoPlayer.prepare()
        exoPlayer.playWhenReady = true
    }
    val goToPreviousScreen = {
            config = config.reset()
            data.currentStep-=1
            previousScreen.invoke()
    }
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> {
                    isPlayVideo= true
                }

                Lifecycle.Event.ON_PAUSE -> {
                    isPlayVideo = false
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

    ConstraintLayout(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Green)
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
        val (box, progress, text1, text2, text3, text4, text5) = createRefs()
        val boxTopGuideline = createGuidelineFromTop(0.26f)
        val boxBottomGuideline = createGuidelineFromBottom(0.26f)
        val boxLeftGuideline = createGuidelineFromStart(0.12f)
        val boxRightGuideline = createGuidelineFromEnd(0.12f)
        val letterTopGuideline = createGuidelineFromTop(0.43f)
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
                    it.onResume()
                } else {
                    config = config.pause()
                    it.onPause()
                    it.player?.pause()
                    it.player?.playWhenReady = false
                }
            }
        )
//        ConstraintLayout(
//            modifier = Modifier
//                .background(Color.Transparent)
//                .constrainAs(box) {
//                    top.linkTo(boxTopGuideline)
//                    bottom.linkTo(boxBottomGuideline)
//                    start.linkTo(boxLeftGuideline)
//                    end.linkTo(boxRightGuideline)
//                    width = Dimension.fillToConstraints
//                    height = Dimension.fillToConstraints
//                }
//        ) {
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
                    if (this.transition.currentState == this.transition.targetState) {
                        isVisibleText2 = true
                    }
                    TypewriterTextEffectView(
                        modifier = Modifier,
                        data.data?.topYeuThich?:"",
                        textHighLight = listOf(data.data?.topYeuThich?:""),
                        configTextHighLight = ConfigTextWriter(
                            Color.Green,
                            40.sp,
                            FontWeight.Medium
                        ),
                        configTextNormal = ConfigTextWriter(
                            Color.Black,
                            18.sp,
                            FontWeight.Medium
                        ),
                        isShowFull = true,
                        isVideoPlaying = isPlayVideo
                    ) {
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
                    TypewriterTextEffectView(
                        modifier = Modifier,
                        "nhà bán hàng được yêu thích nhất\nkhông thể thiếu bạn!Trong đó",
                        textHighLight = listOf(),
                        configTextHighLight = ConfigTextWriter(
                            Color.Green,
                            34.sp,
                            FontWeight.Medium
                        ),
                        configTextNormal = ConfigTextWriter(
                            Color.Black,
                            18.sp,
                            FontWeight.Medium
                        ),
                        isShowFull = false,
                        isVideoPlaying = isPlayVideo
                    ) {
                        isVisibleText3 = true
                    }
                }
            }

//            Box(
//                modifier = Modifier.constrainAs(text3) {
//                    top.linkTo(text2.bottom)
//                    start.linkTo(parent.start)
//                    end.linkTo(parent.end)
//                }
//            ) {
//                AnimatedVisibility(
//                    visible = isVisibleText2,
//                    enter = scaleIn(
//                        initialScale = 0.2f,
//                        animationSpec = tween(durationMillis = ReminderConstants.TIME_SCREEN_1)
//                    ),
//                    exit = scaleOut(
//                        targetScale = 1f,
//                        animationSpec = tween(durationMillis = ReminderConstants.TIME_SCREEN_1)
//                    )
//                ) {
//                    TypewriterTextEffectView(
//                        modifier = Modifier,
//                        "VNĐ",
//                        textHighLight = listOf("VNĐ"),
//                        configTextHighLight = ConfigTextWriter(
//                            Color.Black,
//                            22.sp,
//                            FontWeight.Medium
//                        ),
//                        configTextNormal = ConfigTextWriter(
//                            Color.Black,
//                            22.sp,
//                            FontWeight.Medium
//                        ),
//                        isShowFull = true,
//                        isVideoPlaying = !isPause1
//                    ) {
//                    }
//
//                }
//            }

            Box(
                modifier = Modifier.constrainAs(text3) {
                    top.linkTo(text2.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
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
                    if (this.transition.currentState == this.transition.targetState) {
                        isVisibleText4 = true
                    }
                    Text(
                        text = buildAnnotatedString {
                            withStyle(
                                style = ParagraphStyle(lineHeight = 24.sp)
                            ) {
                                pushStyle(SpanStyle(fontWeight = FontWeight.Medium, fontSize = 55.sp ,color = Color.Green,))
                                append(data.data?.khachHang)
                                pop()
                                append("\n")
                                pushStyle(SpanStyle(fontWeight = FontWeight.Medium, fontSize = 24.sp ,color = Color.Green,))
                                append("khách hàng")
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
                    top.linkTo(text3.bottom)
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
                    "Tiếp tục quay lại trải nghiệm\nsản phẩm nhiều lần",
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
                    isVideoPlaying = isPlayVideo
                ) {
                    isVisibleText2 = true
                }
            }
        }
//            Box(
//                modifier = Modifier.constrainAs(text5) {
//                    top.linkTo(text4.bottom)
//                    start.linkTo(parent.start)
//                    end.linkTo(parent.end)
//                }
//            ) {
//                AnimatedVisibility(
//                    visible = isVisibleText4,
//                    enter = scaleIn(
//                        initialScale = 0.2f,
//                        animationSpec = tween(durationMillis = ReminderConstants.TIME_SCREEN_1)
//                    ),
//                    exit = scaleOut(
//                        targetScale = 1f,
//                        animationSpec = tween(durationMillis = ReminderConstants.TIME_SCREEN_1)
//                    )
//                ) {
//                    TypewriterTextEffectView(
//                        modifier = Modifier,
//                        "Bạn lọt Top 100 nhà bán \n chốt được nhiều đơn hàng nhất!",
//                        textHighLight = listOf("Top 100"),
//                        configTextHighLight = ConfigTextWriter(
//                            Color.Black,
//                            22.sp,
//                            FontWeight.Medium
//                        ),
//                        configTextNormal = ConfigTextWriter(
//                            Color.Black,
//                            22.sp,
//                            FontWeight.Medium
//                        ),
//                        isShowFull = false,
//                        isVideoPlaying = !isPause1
//                    ) {
//                    }
//
//                }
//            }
//        }
        GSlicedProgressBar(
            modifier = Modifier
                .height(40.dp)
                .padding(18.dp, 0.dp)
                .fillMaxWidth()
                .constrainAs(progress) {
                    top.linkTo(parent.top)
                },
            ReminderConstants.TOTAL_STEPS,
            data.currentStep,
            config,
            videoUris.toInt() + 4000,
            goToNextScreen
        )
        LaunchedEffect(isPlayVideo) {
            if (exoPlayer.currentPosition > 0L && isPlayVideo) {
                delay(2000 - exoPlayer.currentPosition)
                isVisibleText1 = true

            } else if (exoPlayer.currentPosition == 0L) {
                delay(2000)
                isVisibleText1 = true
            }
        }

    }
}