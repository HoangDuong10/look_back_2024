package com.example.loadimage

import android.util.Log
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
import kotlinx.coroutines.delay
import kotlin.random.Random

@Composable
fun Screen4(
    nextScreen : () -> Unit,
    previousScreen : () -> Unit,
    data: ReminderDataNavigation
){


    val context = LocalContext.current
    var currentStepState by remember { mutableStateOf(data.currentStep) }
    var isVisibleText by remember { mutableStateOf(false) }
    var animation by remember { mutableStateOf(false) }
    var isPlayVideo by remember { mutableStateOf(true) }
    val videoUris =
        getVideoDuration(context, "android.resource://${context.packageName}/${R.raw.man4}")
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
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> {
                    isPlayVideo = true
                }
                Lifecycle.Event.ON_PAUSE -> {
//                    if (exoPlayer.playbackState == Player.STATE_ENDED) {
//                        exoPlayer.seekToDefaultPosition()
//                        exoPlayer.pause()
//                    } else {
//                        exoPlayer.playWhenReady = false // Tạm dừng video
//                    }
                    isPlayVideo = false
                }
                Lifecycle.Event.ON_STOP -> {
//                    if (exoPlayer.playbackState == Player.STATE_ENDED) {
//                        exoPlayer.seekToDefaultPosition()
//                        exoPlayer.pause()
//                    } else {
//                        exoPlayer.playWhenReady = false
//                    }
//                    isPlayVideo = true
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
            }
        }
    })
    val goToNextScreen = {
        config = config.reset()
        data.currentStep+=1
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
//        if((endVideo-startVideo)>0L && isPlayVideo){
//            delay((time*150+500)-(endVideo-startVideo))
//            isVisibleText = true
//        }else{
//            delay(time*150+500)
////            isVisibleText = true
//        }
        if (exoPlayer.currentPosition > 0L && isPlayVideo) {
            delay(data.data?.thang!!.toInt()*150+500 - exoPlayer.currentPosition)
            isVisibleText = true

        } else if (exoPlayer.currentPosition == 0L) {
            delay(data.data?.thang!!.toLong()*150+500)
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
                        isPlayVideo= false
                        val pressStartTime = System.currentTimeMillis()
                        this.tryAwaitRelease()
                        isPlayVideo= true
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
        val (imageMonth,textTitle,progress) = createRefs()
        val boxGuideline = createGuidelineFromTop(animatedGuidelineFraction )
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
                if(isPlayVideo){
                    config = config.resume()
                    exoPlayer.playWhenReady = true
                    Log.d("lifecycle11","222222")
                    it.onResume()
                }else{
                    config = config.pause()
                    it.onPause()
                    it.player?.pause()
                    it.player?.playWhenReady = false
                }
            }

        )
        ConstraintLayout(
            modifier = Modifier.background(Color.Transparent).constrainAs(imageMonth){
                top.linkTo(boxGuideline)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }

        ) {
            val(ivMonth,tvMonth) = createRefs()
            val monthGuideline = createGuidelineFromTop(0.6f)
            Image(
                painter = painterResource(R.drawable.month),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier.size(201.dp).constrainAs(ivMonth){
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    top.linkTo(parent.top)
                }
            )
            AnimatedContent(
                modifier = Modifier.constrainAs(tvMonth){
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
                    animationSpec = tween(durationMillis = ReminderConstants.TIME_SCREEN_1)
                ),
                exit = scaleOut(
                    targetScale = 1f,
                    animationSpec = tween(durationMillis = ReminderConstants.TIME_SCREEN_1)
                )
            ) {
                TypewriterTextEffectView(
                    modifier = Modifier,
                    "là tháng ghi nhận kết quả kinh doanh\nsuất sắc nhất của bạn",
                    textHighLight = listOf(),
                    configTextHighLight = ConfigTextWriter(
                        Color.Black,
                        18.sp,
                        FontWeight.Medium
                    ),
                    configTextNormal = ConfigTextWriter(
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
            data.currentStep,
            config,
            videoUris.toInt(),
            goToNextScreen
        )
    }
}
