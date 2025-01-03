package com.example.loadimage

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
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
import com.example.loadimage.ui.theme.LoadImageTheme
import com.google.gson.Gson
import kotlinx.coroutines.delay
import kotlin.random.Random


@OptIn(ExperimentalAnimationApi::class)
@Composable
fun Screen12(
    modifier: Modifier = Modifier,
    previousScreen: () -> Unit,
    data: ReminderDataNavigation
) {
    val context = LocalContext.current
    var isVisibleText1 by remember { mutableStateOf(false) }
    var isVisibleImage1 by remember { mutableStateOf(false) }
    var isVisibleImage7 by remember { mutableStateOf(false) }
    var isVisibleImage2 by remember { mutableStateOf(false) }
    var isVisibleImage3 by remember { mutableStateOf(false) }
    var isVisibleImage4 by remember { mutableStateOf(false) }
    var isVisibleImage5 by remember { mutableStateOf(false) }
    var isVisibleImage6 by remember { mutableStateOf(false) }
    var isPlayVideo by remember { mutableStateOf(true) }
    val lifecycleOwner = LocalLifecycleOwner.current
    val videoUris =
        getVideoDuration(context, "android.resource://${context.packageName}/${R.raw.man12}")
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
        exoPlayer.setMediaItem(MediaItem.fromUri("android.resource://${context.packageName}/${R.raw.man12}"))
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
                    isPlayVideo = true
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
    LaunchedEffect(isPlayVideo) {
        if (exoPlayer.currentPosition > 0L && isPlayVideo) {
            delay(1700 - exoPlayer.currentPosition)
            isVisibleText1 = true

        } else if (exoPlayer.currentPosition == 0L) {
            delay(1700)
            isVisibleText1 = true
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
        val (progress, text1, text2, textHighlight,btnShare) = createRefs()
        val letter1TopGuideline = createGuidelineFromTop(0.23f)
        val letter2TopGuideline = createGuidelineFromTop(0.3f)
        val letterStartGuideline = createGuidelineFromStart(0.2f)
        val buttonEndGuideline = createGuidelineFromEnd(0.14f)
        val buttonStartGuideline = createGuidelineFromStart(0.14f)
        val buttonBottomGuideline = createGuidelineFromBottom(0.12f)
        val textHighlightTopGuideline = createGuidelineFromTop(0.38f)
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
        Box(
            modifier = Modifier
                .constrainAs(text1) {
                    top.linkTo(letter1TopGuideline)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
        ) {
            AnimatedVisibility(
                visible = isVisibleText1,
                enter = scaleIn(
                    initialScale = 0.6f,
                ),
                exit = scaleOut(
                    targetScale = 1f,
                )
            ) {
                TypewriterTextEffectView(
                    modifier = Modifier,
                    "Người Kiến Tạo Doanh Số",
                    textHighLight = listOf("Người Kiến Tạo Doanh Số"),
                    configTextHighLight = ConfigTextWriter(
                        color = colorResource(R.color.main_color
                        ),
                        24.sp,
                        FontWeight.Bold
                    ),
                    configTextNormal = ConfigTextWriter(
                        Color.Black,
                        24.sp,
                        FontWeight.Medium
                    ),
                    isShowFull = true,
                    isVideoPlaying = isPlayVideo
                ) {
                }
            }
        }

        Box(
            modifier = Modifier
                .constrainAs(text2) {
                    top.linkTo(letter2TopGuideline)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
        ) {
            AnimatedVisibility(
                visible = isVisibleText1,
                enter = scaleIn(
                    initialScale = 0.6f,
//                    animationSpec = tween(durationMillis = ReminderConstants.TIME_SCREEN_1)
                ),
                exit = scaleOut(
                    targetScale = 1f,
//                    animationSpec = tween(durationMillis = ReminderConstants.TIME_SCREEN_1)
                )
            ) {
                if (this.transition.currentState == this.transition.targetState) {
                    isVisibleImage1 = true
                }
                TypewriterTextEffectView(
                    modifier = Modifier,
                    "Shop Test",
                    textHighLight = listOf(),
                    configTextHighLight = ConfigTextWriter(
                        Color.Green,
                        30.sp,
                        FontWeight.Medium
                    ),
                    configTextNormal = ConfigTextWriter(
                        Color.Black,
                        24.sp,
                        FontWeight.Medium
                    ),
                    isShowFull = true,
                    isVideoPlaying = isPlayVideo
                ) {
                }
            }
        }
        Column(modifier = Modifier.constrainAs(textHighlight) {
            top.linkTo(textHighlightTopGuideline)
            start.linkTo(letterStartGuideline)
            end.linkTo(parent.end)
        }) {
            HighlightItem(
                isPlayVideo = isPlayVideo,
                textNormal = "${data.data?.order} đơn hàng",
                textHighLight = "${data.data?.order}",
                isVisibleImage = isVisibleImage1,
                displayText = { isVisibleImage2 = true }
            )
            HighlightItem(
                isPlayVideo = isPlayVideo,
                textNormal = "${data.data?.doanhthu} VNĐ tổng doanh thu",
                textHighLight = "${data.data?.doanhthu} VNĐ",
                isVisibleImage = isVisibleImage2,
                displayText = { isVisibleImage3 = true }
            )
            HighlightItem(
                isPlayVideo = isPlayVideo,
                textNormal = "Tháng ${data.data?.thang} là tháng xuất sắc nhất",
                textHighLight = "Tháng ${data.data?.thang}",
                isVisibleImage = isVisibleImage3,
                displayText = { isVisibleImage4 = true }
            )
            HighlightItem(
                isPlayVideo = isPlayVideo,
                textNormal = "${data.data?.slKhachHang} khách hàng",
                textHighLight = "${data.data?.slKhachHang}",
                isVisibleImage = isVisibleImage4,
                displayText = { isVisibleImage5 = true }
            )
            HighlightItem(
                isPlayVideo = isPlayVideo,
                textNormal = "${data.data?.slKhachHang} khách hàng",
                textHighLight = "${data.data?.slKhachHang}",
                isVisibleImage = isVisibleImage5,
                displayText = { isVisibleImage6 = true }
            )
            HighlightItem(
                isPlayVideo = isPlayVideo,
                textNormal = "${data.data?.slKhachHang} khách hàng thân thiết",
                textHighLight = "${data.data?.slKhachHang}",
                isVisibleImage = isVisibleImage6,
                displayText = { isVisibleImage7 = true }
            )
            HighlightItem(
                isPlayVideo = isPlayVideo,
                textNormal = "${data.data?.slKhachHang} khách hàng thân thiết",
                textHighLight = "${data.data?.slKhachHang}",
                isVisibleImage = isVisibleImage7,
                displayText = {}
            )
        }
        Box(
            modifier = Modifier.fillMaxWidth().constrainAs(btnShare){
                bottom.linkTo(buttonBottomGuideline)
                start.linkTo(buttonStartGuideline)
                end.linkTo(buttonEndGuideline)
                width = Dimension.fillToConstraints
            }
        ) {
            AnimatedVisibility(
                visible = isVisibleText1,
                enter = scaleIn(
                    initialScale = 0.6f,
//                    animationSpec = tween(durationMillis = ReminderConstants.TIME_SCREEN_1)
                ),
                exit = scaleOut(
                    targetScale = 1f,
//                    animationSpec = tween(durationMillis = ReminderConstants.TIME_SCREEN_1)
                )
            ) {
                Button(
                    onClick = {},
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorResource(id = R.color.main_color),
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(4.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Chia sẻ",
                        fontSize = 20.sp,
                        color = Color.White
                    )
                }
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
            ReminderConstants.TOTAL_STEPS,
            data.currentStep,
            config,
            videoUris.toInt() + 2000,
            goToNextScreen
        )

    }
}

@Composable
fun HighlightItem(
    isVisibleImage: Boolean = false,
    isPlayVideo: Boolean,
    textNormal: String,
    textHighLight: String,
    displayText: () -> Unit
) {
    Box(
        modifier = Modifier.padding(8.dp)
    ) {
        AnimatedVisibility(
            visible = isVisibleImage,
            enter = scaleIn(
                initialScale = 1f,
            ),
        ) {
            Row {
                Icon(
                    painter = painterResource(R.drawable.ic_check),
                    contentDescription = null,
                    tint = Color.Unspecified,
                    modifier = Modifier.size(22.dp)
                )
                TypewriterTextEffectView(
                    modifier = Modifier.padding(start = 4.dp),
                    textNormal,
                    textHighLight = listOf(textHighLight),
                    configTextHighLight = ConfigTextWriter(
                        Color.Black,
                        20.sp,
                        FontWeight.Bold
                    ),
                    configTextNormal = ConfigTextWriter(
                        Color.Black,
                        20.sp,
                        FontWeight.Normal
                    ),
                    textAlign = TextAlign.Start,
                    isShowFull = false,
                    isVideoPlaying = isPlayVideo
                ) {
                    displayText.invoke()
                }
            }
        }
    }


}

@Preview(showBackground = true)
@Composable
fun ItemPreview() {
    LoadImageTheme {
        HighlightItem(
            isPlayVideo = true,
            textNormal = "2000 đơn hàng",
            textHighLight = "2000",
            isVisibleImage = true,
            displayText = {}
        )
    }
}