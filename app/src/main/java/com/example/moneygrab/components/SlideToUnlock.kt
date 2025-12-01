
package com.example.moneygrab.components
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Arrangement.Absolute.spacedBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton

import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import com.example.moneygrab.R
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import androidx.wear.compose.material.ExperimentalWearMaterialApi
import androidx.wear.compose.material.FractionalThreshold
import androidx.wear.compose.material.SwipeProgress
import androidx.wear.compose.material.SwipeableDefaults
import androidx.wear.compose.material.SwipeableState
import androidx.wear.compose.material.rememberSwipeableState
import androidx.wear.compose.material.swipeable

import kotlin.math.roundToInt


@OptIn(ExperimentalWearMaterialApi::class)
@Composable
fun SlideToUnlock(
    isLoading: Boolean,
    onUnlockRequested: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val hapticFeedback = LocalHapticFeedback.current
    val swipeState = rememberSwipeableState(
        initialValue = if (isLoading) Anchor.End else Anchor.Start,
        confirmStateChange = { anchor ->
            if (anchor == Anchor.End) {
                hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                onUnlockRequested()
            }
            true
        }
    )

    var swipeFraction by remember { mutableStateOf(0f) }

    LaunchedEffect(isLoading) {
        swipeState.animateTo(if (isLoading) Anchor.End else Anchor.Start)
    }

    Track(
        swipeState = swipeState,
        enabled = !isLoading,
        modifier = modifier,
        onSwipeFractionChanged = { fraction -> swipeFraction = fraction },
    ) {
        Hint(
            text = "Pay now",
            swipeFraction = swipeFraction,
            modifier = Modifier
                .align(Alignment.Center)
                .padding(PaddingValues(horizontal = Thumb.Size + 8.dp)),
        )

        Thumb(
            isLoading = isLoading,
            modifier = Modifier.offset {
                IntOffset(swipeState.offset.value.roundToInt(), 0)
            },
        )
    }
}


enum class Anchor { Start, End }

@OptIn(ExperimentalWearMaterialApi::class)
@Composable
fun Track(
    swipeState: SwipeableState<Anchor>,
    enabled: Boolean,
    modifier: Modifier = Modifier,
    onSwipeFractionChanged: (Float) -> Unit,
    content: @Composable (BoxScope.() -> Unit),
) {
    val density = LocalDensity.current
    var fullWidth by remember { mutableIntStateOf(0) }

    val horizontalPadding = 10.dp

    val startOfTrackPx = 0f
    val endOfTrackPx = remember(fullWidth) {
        with(density) { fullWidth - (2 * horizontalPadding + Thumb.Size).toPx() }
    }

    val snapThreshold = 0.8f
    val thresholds = { from: Anchor, _: Anchor ->
        if (from == Anchor.Start) {
            FractionalThreshold(snapThreshold)
        } else {
            FractionalThreshold(1f - snapThreshold)
        }
    }

    // compute fraction from offset
    val localSwipeFraction = remember(swipeState.offset.value, endOfTrackPx) {
        if (endOfTrackPx <= 0f) 0f
        else (swipeState.offset.value / endOfTrackPx).coerceIn(0f, 1f)
    }

    // send fraction back up to SlideToUnlock
    LaunchedEffect(localSwipeFraction) {
        onSwipeFractionChanged(localSwipeFraction)
    }

    val backgroundColor by remember(localSwipeFraction) {
        derivedStateOf { calculateTrackColor(localSwipeFraction) }
    }

    Box(
        modifier = modifier
            .onSizeChanged { fullWidth = it.width }
            .height(56.dp)
            .fillMaxWidth()
            .swipeable(
                enabled = enabled,
                state = swipeState,
                orientation = Orientation.Horizontal,
                anchors = mapOf(
                    startOfTrackPx to Anchor.Start,
                    endOfTrackPx to Anchor.End,
                ),
                thresholds = thresholds,
                velocityThreshold = Track.VelocityThreshold,
            )
            .background(
                color = backgroundColor,
                shape = RoundedCornerShape(percent = 50),
            )
            .padding(
                PaddingValues(
                    horizontal = horizontalPadding,
                    vertical = 8.dp,
                )
            ),
        content = content,
    )
}

val AlmostBlack = Color(0xFF00341C)
val Green= Color(0xFF809685)
fun calculateTrackColor(swipeFraction: Float): Color {
    val endOfColorChangeFraction = 0.75f
    val fraction = (swipeFraction / endOfColorChangeFraction).coerceIn(0f..1f)
    return lerp(AlmostBlack, Green, fraction)
}

@Composable
fun Thumb(
    isLoading: Boolean,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .size(Thumb.Size)
            .background(color = Color.White, shape = CircleShape)
            .padding(8.dp),
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.padding(2.dp),
                color = Color.Black,
                strokeWidth = 2.dp
            )
        } else {
            Image(
                painter = painterResource(R.drawable.arrow_right),
                contentDescription = null
            )
        }
    }
}

@Composable
fun Hint(
    text: String,
    swipeFraction: Float,
    modifier: Modifier = Modifier,
) {
    val hintTextColor by remember(swipeFraction) {
        derivedStateOf { calculateHintTextColor(swipeFraction) }
    }

    Text(
        text = text,
        color = hintTextColor,
        style = MaterialTheme.typography.titleSmall,
        modifier = modifier
    )
}

fun calculateHintTextColor(swipeFraction: Float): Color {
    val endOfFadeFraction = 0.35f
    val fraction = (swipeFraction / endOfFadeFraction).coerceIn(0f..1f)
    return lerp(Color.White, Color.White.copy(alpha = 0f), fraction)
}


private object Thumb {
    val Size = 40.dp
}

private object Track {
    @OptIn(ExperimentalWearMaterialApi::class)
    val VelocityThreshold = SwipeableDefaults.VelocityThreshold * 10
}

@OptIn(ExperimentalWearMaterialApi::class)
@Preview
@Composable
private fun Preview() {
    val previewBackgroundColor = Color(0xFFEDEDED)
    var isLoading by remember { mutableStateOf(false) }
    val spacing = 88.dp
    Column(
        verticalArrangement = spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .background(previewBackgroundColor)
            .padding(horizontal = 24.dp),
    ) {
        Spacer(modifier = Modifier.height(spacing))

        Column(modifier = Modifier.width(IntrinsicSize.Max)) {
            Row(
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(text = "Normal")
                Spacer(modifier = Modifier.weight(1f))
                Thumb(isLoading = false)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(text = "Loading")
                Spacer(modifier = Modifier.widthIn(min = 16.dp))
                Thumb(isLoading = true)
            }
        }

        Spacer(modifier = Modifier.height(spacing))

        val inactiveState = rememberSwipeableState(Anchor.Start)
        val activeState = rememberSwipeableState(Anchor.End)

        Text(text = "Inactive")
        Track(
            swipeState = inactiveState,
            enabled = true,
            modifier = Modifier.fillMaxWidth(),
            onSwipeFractionChanged = {},
            content = {},
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(text = "Active")
        Track(
            swipeState = activeState,
            enabled = true,
            modifier = Modifier.fillMaxWidth(),
            onSwipeFractionChanged = {},
            content = {},
        )

        Spacer(modifier = Modifier.height(spacing))

        SlideToUnlock(
            isLoading = isLoading,
            onUnlockRequested = { isLoading = true },
        )

        Spacer(modifier = Modifier.weight(1f))

        OutlinedButton(
            colors = ButtonDefaults.outlinedButtonColors(),
            shape = RoundedCornerShape(percent = 50),
            onClick = { isLoading = false }
        ) {
            Text(text = "Cancel loading", style = MaterialTheme.typography.labelMedium)
        }

        Spacer(modifier = Modifier.height(16.dp))

    }
}
