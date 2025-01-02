package com.example.loadimage

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import kotlinx.coroutines.delay

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun GReminderLetter1(
    modifier: Modifier,
    isVisibleLetter: Boolean,
    listText: List<String>,
    textHighLight: List<String>,
    isFinish: @Composable (isFinish: Boolean) -> Unit,
) {
    Box(modifier = modifier) {
        AnimatedVisibility(
            visible = isVisibleLetter,
            enter =
            fadeIn() +
                    slideInVertically(
                        animationSpec = tween(ReminderConstants.TIME_ENTER_LETTER),
                        initialOffsetY = { fullHeight -> fullHeight / 3 },
                    ),
            exit = scaleOut(),
        ) {
            var boxHeight =0
            var oneThirdPosition =0
            val text1 = listText[0]
            val text2 = listText[1]
            val text3 = listText[2]
            ConstraintLayout(
                modifier = Modifier.fillMaxSize(),
            ) {
                var isVisibleMsg by remember { mutableStateOf(false) }
                var isVisibleCountOrder by remember { mutableStateOf(false) }
                var isVisibleOrder by remember { mutableStateOf(false) }

                val letterTopGuideline = createGuidelineFromTop(0.2f)

                val (box, msg, countOrder, order) = createRefs()
                val boxTopGuideline = createGuidelineFromTop(0.26f)
                val boxBottomGuideline = createGuidelineFromBottom(0.26f)
                val boxLeftGuideline = createGuidelineFromStart(0.12f)
                val boxRightGuideline = createGuidelineFromEnd(0.12f)

                Box(
                    modifier = Modifier
                        .background(Color.Green)
                        .constrainAs(box) {
                            top.linkTo(boxTopGuideline )
                            bottom.linkTo(boxBottomGuideline)
                            start.linkTo(boxLeftGuideline)
                            end.linkTo(boxRightGuideline)
                            width = Dimension.fillToConstraints
                            height = Dimension.fillToConstraints
                        } .onGloballyPositioned { coordinates ->
                            boxHeight = coordinates.size.height
                            oneThirdPosition = (coordinates.positionInRoot().y + boxHeight / 3).toInt()
                        }
                ) {
                }

//                if (isVisibleMsg) {
//                    TypewriterTextEffectView(
//                        modifier =
//                        Modifier.constrainAs(msg) {
//                            top.linkTo(box.top, 33.dp)
//                            start.linkTo(box.start)
//                            end.linkTo(parent.end)
//                            width = Dimension.wrapContent
//                        },
//                        text1,
//                        textHighLight = textHighLight,
//                        configTextHighLight =
//                        ConfigTextWriter(
//                            Color.Green,
//                            20.sp,
//                            FontWeight.Medium,
//                        ),
//                        configTextNormal =
//                        ConfigTextWriter(
//                            Color.Black,
//                            20.sp,
//                            FontWeight.Medium,
//                        ),
//                        isShowFull = false,
//                    ) {
//                        isVisibleCountOrder = true
//                    }
//                }

                Box(
                    modifier =
                    Modifier
                        .constrainAs(countOrder) {
                            top.linkTo(msg.bottom)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                        },
                ) {
                    AnimatedVisibility(
                        visible = isVisibleCountOrder,
                        enter =
                        scaleIn(
                            initialScale = 0.2f,
                            animationSpec = tween(durationMillis = 500),
                        ),
                        exit =
                        scaleOut(
                            targetScale = 1f,
                            animationSpec = tween(durationMillis = 500),
                        ),
                    ) {
                        if (this.transition.currentState == this.transition.targetState) {
                            isVisibleOrder = true
                        }
                        Box {
                            Text(
                                style =
                                TextStyle(
                                    platformStyle =
                                    PlatformTextStyle(
                                        includeFontPadding = false,
                                    ),
                                ),
                                text = text2,
                                color = Color.Green,
                                fontSize = 52.sp,
                                fontWeight = FontWeight.Bold,
                            )
                        }
                    }
                }

                Box(
                    modifier =
                    Modifier
                        .constrainAs(order) {
                            top.linkTo(countOrder.bottom)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                        },
                ) {
                    AnimatedVisibility(
                        visible = isVisibleOrder,
                        enter =
                        scaleIn(
                            initialScale = 0.2f,
                            animationSpec = tween(durationMillis = 500),
                        ),
                        exit =
                        scaleOut(
                            targetScale = 1f,
                            animationSpec = tween(durationMillis = 500),
                        ),
                    ) {
                        if (this.transition.currentState == this.transition.targetState) {
                            isFinish.invoke(true)
                        }
                        Box {
                            Text(
                                style =
                                TextStyle(
                                    platformStyle =
                                    PlatformTextStyle(
                                        includeFontPadding = false,
                                    ),
                                ),
                                text = text3,
                                color = Color.Black,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                            )
                        }
                    }
                }

                LaunchedEffect(true) {
                    delay(1600)
                    isVisibleMsg = true
                }
            }
        }
    }
}
