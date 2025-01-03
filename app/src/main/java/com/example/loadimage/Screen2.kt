package com.example.loadimage

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver
import android.widget.FrameLayout
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
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
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.ComposeView
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.Popup
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
import androidx.navigation.NavController
import com.example.loadimage.ui.theme.LoadImageTheme
import dev.shreyaspatil.capturable.Capturable
import dev.shreyaspatil.capturable.controller.CaptureController
import dev.shreyaspatil.capturable.controller.rememberCaptureController
import kotlinx.coroutines.delay
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
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
    data: ReminderDataNavigation,
) {

    val context = LocalContext.current
    var currentStepState by remember { mutableStateOf(data.currentStep) }
    var isVisibleText1 by remember { mutableStateOf(false) }
    var isVisibleText2 by remember { mutableStateOf(false) }
    var isVisibleText3 by remember { mutableStateOf(false) }
    var isVisibleText4 by remember { mutableStateOf(false) }
    var isVideoPlaying by remember { mutableStateOf(true) }
    var isCature by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    LoadingDialog(isLoading = isLoading)


    BackHandler {
        (context as? Activity)?.finish()
    }
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
//        config = config.reset()
//        data.currentStep += 1
//        nextScreen.invoke()
    }
    val mainColor = colorResource(id = R.color.main_color)
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
        delay((videoUris.toInt() + 4000).toLong())
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
//    if(!isCature){
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
                    val letterTopGuideline = createGuidelineFromTop(0.45f)
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
                            enter = slideInVertically(
                                initialOffsetY = { it },
                                animationSpec = tween(ReminderConstants.TIME_SCREEN_1)
                            ) + fadeIn(animationSpec = tween(durationMillis = ReminderConstants.TIME_SCREEN_1)),
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
                                    withStyle(style = ParagraphStyle(lineHeight = 21.sp)) {
                                        withStyle(
                                            style = SpanStyle(
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 55.sp,
                                                color = mainColor
                                            )
                                        ) {
                                            append("${data.data?.order}\n")
                                        }

                                        withStyle(
                                            style = SpanStyle(
                                                fontWeight = FontWeight.Medium,
                                                fontSize = 24.sp,
                                                color = mainColor,
                                            )
                                        ) {
                                            append("đơn hàng")
                                        }
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
                        videoUris.toInt() + 5000,
                        goToNextScreen
                    )
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
                                    mainColor,
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
                            ShareButton {
                                isCature = true
                                isLoading = true
                                isVideoPlaying = false
//                              captureController.capture()
//                        captureScreenshot(context,)
                            }
                        }
                    }
                }
            }
    }
    if(isCature){
        CatureScreen(data = data,
                showPopup = {isCature = false},
            hide = {isLoading = false}
               )
    }
//        }else{
//            CatureScreen(data = data,
//                showPopup = {}
//               )
//    }
}
//}

@Composable
fun CatureScreen(
    data: ReminderDataNavigation,
    modifier: Modifier = Modifier,
    showPopup : (Boolean) -> Unit,
    hide : (Boolean) -> Unit,
) {
    val captureController = rememberCaptureController()
    val context = LocalContext.current
    var isLayoutReady by remember { mutableStateOf(false) }
    val shareLauncher = rememberShareLauncher(context) { success ->
        showPopup(success)
    }
    if (isLayoutReady) {
        LaunchedEffect(Unit) {
            captureController.capture()
        }
    }
//    val shareLauncher = rememberLauncherForActivityResult(
//        contract = ActivityResultContracts.StartActivityForResult()
//    ) { result ->
//        if (result.resultCode == Activity.RESULT_CANCELED) {
//            // Người dùng nhấn ra ngoài hoặc không chọn ứng dụng chia sẻ
//            Toast.makeText(context, "Bạn đã hủy chia sẻ.", Toast.LENGTH_SHORT).show()
//            showPopup.invoke(false)
//        }
//    }
    Popup (
        onDismissRequest = {  }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()

        ) {
            Capturable(
                onCaptured = { imageBitmap, _ ->
                    val bitmap = imageBitmap?.asAndroidBitmap()
                    if (bitmap != null) {
                        val imageUri = saveImageToCache(context, bitmap)
                        if (imageUri != null) {
                            // Tạo intent chia sẻ
//                            val shareIntent = Intent(Intent.ACTION_SEND).apply {
//                                type = "image/png"
//                                putExtra(Intent.EXTRA_STREAM, imageUri)
//                                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
//                            }
//                            // Khởi chạy intent chia sẻ qua launcher
//                            shareLauncher.launch(Intent.createChooser(shareIntent, "Chia sẻ qua"))
                            shareImage(context, shareLauncher, imageUri)
                            hide.invoke(false)
                        } else {
                            // Xử lý khi không lưu được ảnh
                            Toast.makeText(context, "Không thể lưu ảnh!", Toast.LENGTH_SHORT).show()
                        }
                    }
                },
                controller = captureController
            ) {

                ConstraintLayout(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Green).onGloballyPositioned {
                            isLayoutReady = true
                        }
                ) {
                    val (text1, text2, text4, text5, progress) = createRefs()
                    val letterTopGuideline = createGuidelineFromTop(0.45f)
                    Box(Modifier.fillMaxSize()) {
                        Image(
                            painter = painterResource(R.drawable.bg2),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.FillBounds,
                            alignment = Alignment.Center
                        )
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
                        ProgressBarConfig(
                            action = ReminderConstants.RESET,
                            configValue = Random.nextInt()
                        ),
                        0,
                        {}
                    )
                    Box(
                        modifier = Modifier
                            .constrainAs(text1) {
                                top.linkTo(letterTopGuideline)
                                start.linkTo(parent.start)
                                end.linkTo(parent.end)
                            }
                    ) {

                        Text(
                            text = "Nhìn lại 2024 bạn đã có 1 \n hành trình thật ấn tượng",
                            color = Color.Black,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Box(
                        modifier = Modifier.constrainAs(text2) {
                            top.linkTo(text1.bottom)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                        }
                    ) {
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

                    Box(
                        modifier = Modifier
                            .constrainAs(text4) {
                                top.linkTo(text2.bottom, margin = 0.dp)
                                start.linkTo(parent.start)
                                end.linkTo(parent.end)
                            }
                            .offset(y = (-10).dp)
                    ) {
                        Text(
                            text = "Đợc chốt thành công",
                            color = Color.Black,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium
                        )
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

                        Text(
                            text = buildAnnotatedString {
                                append("Baạn lọt ")
                                withStyle(
                                    style = SpanStyle(
                                        color = Color.Green,
                                        fontWeight = FontWeight.Medium
                                    )
                                ) {
                                    append(data.data?.topNhaBan)
                                }
                                append(
                                    "nhà bán \n" +
                                            " chốt được nhiều đơn hàng nhất!"
                                )

                            },
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.Black
                        )
                    }
                }
            }
        }
    }

}

@Preview(showBackground = true)
@Composable
fun dialogPreview(){
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
    LoadImageTheme {
        CatureScreen(data = navigationData, showPopup = {}, hide = {})
    }
}
fun captureScreenshot1(context: Context, bitmap: Bitmap) {
    val now = SimpleDateFormat("yyyy-MM-dd_HH:mm:ss", Locale.getDefault()).format(Date())
    val path = File(context.getExternalFilesDir(null), "$now.jpg")
    FileOutputStream(path).use { outputStream ->
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
    }

    val uri = FileProvider.getUriForFile(context, "com.example.testcapture.fileprovider222", path)
    val intent = Intent(Intent.ACTION_SEND).apply {
        putExtra(Intent.EXTRA_STREAM, uri)
        type = "image/jpeg"
    }
    context.startActivity(Intent.createChooser(intent, "Chia sẻ ảnh qua"))
}


fun captureComposeScreenshot(context: Context, composableContent: @Composable () -> Unit, onBitmapCaptured: (Bitmap) -> Unit) {
    val frameLayout = FrameLayout(context)
    val composeView = ComposeView(context).apply {
        setContent {
            composableContent()
        }
    }

    // Thêm ComposeView vào FrameLayout
    frameLayout.addView(composeView)

    // Đợi cây View được vẽ hoàn toàn trước khi đo
    composeView.viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
        override fun onPreDraw(): Boolean {
            // Đảm bảo ComposeView đã được đo và vẽ
            composeView.viewTreeObserver.removeOnPreDrawListener(this)

            // Đo và vẽ ComposeView
            composeView.measure(
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            )
            composeView.layout(0, 0, composeView.measuredWidth, composeView.measuredHeight)

            // Tạo Bitmap từ ComposeView
            val bitmap = Bitmap.createBitmap(composeView.measuredWidth, composeView.measuredHeight, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            composeView.draw(canvas)

            // Gọi callback với Bitmap
            onBitmapCaptured(bitmap)

            return true // Tiếp tục vẽ
        }
    })
}

fun saveBitmapAndShare(context: Context, bitmap: Bitmap) {
    // Lưu bitmap vào file
    val now = SimpleDateFormat("yyyy-MM-dd_HH:mm:ss", Locale.getDefault()).format(Date())
    val path = File(context.getExternalFilesDir(null), "$now.jpg")

    FileOutputStream(path).use { outputStream ->
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
    }

    // Lấy URI cho file và tạo Intent
    val uri = FileProvider.getUriForFile(context, "com.example.testcapture.fileprovider222", path)
    val intent = Intent(Intent.ACTION_SEND).apply {
        putExtra(Intent.EXTRA_STREAM, uri)
        type = "image/jpeg"
    }
    context.startActivity(Intent.createChooser(intent, "Chia sẻ ảnh qua"))
}

