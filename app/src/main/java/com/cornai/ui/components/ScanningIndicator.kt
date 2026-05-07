package com.cornai.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.cornai.ui.theme.GreenPrimary

@Composable
fun ScanningIndicator(
    modifier: Modifier = Modifier,
    size: Dp = 200.dp,
    primaryColor: Color = GreenPrimary
) {
    val infiniteTransition = rememberInfiniteTransition(label = "scan")

    val animatedOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "offset"
    )

    val animatedAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    Canvas(modifier = modifier.size(size)) {
        val canvasWidth = size.toPx()
        val canvasHeight = size.toPx()
        val centerX = canvasWidth / 2
        val centerY = canvasHeight / 2

        // Outer scanning circle
        drawCircle(
            color = primaryColor.copy(alpha = animatedAlpha * 0.3f),
            radius = canvasWidth * 0.45f * animatedOffset + canvasWidth * 0.1f,
            center = Offset(centerX, centerY),
            style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2.dp.toPx())
        )

        // Inner static circle
        drawCircle(
            color = primaryColor.copy(alpha = 0.5f),
            radius = canvasWidth * 0.35f,
            center = Offset(centerX, centerY),
            style = androidx.compose.ui.graphics.drawscope.Stroke(width = 3.dp.toPx())
        )

        // Corner brackets
        val bracketSize = canvasWidth * 0.08f
        val bracketLength = canvasWidth * 0.15f
        val offset = canvasWidth * 0.35f - bracketLength

        // Top-left bracket
        drawLine(
            color = primaryColor,
            start = Offset(centerX - offset, centerY - offset),
            end = Offset(centerX - offset + bracketLength, centerY - offset),
            strokeWidth = 4.dp.toPx()
        )
        drawLine(
            color = primaryColor,
            start = Offset(centerX - offset, centerY - offset),
            end = Offset(centerX - offset, centerY - offset + bracketLength),
            strokeWidth = 4.dp.toPx()
        )

        // Top-right bracket
        drawLine(
            color = primaryColor,
            start = Offset(centerX + offset, centerY - offset),
            end = Offset(centerX + offset - bracketLength, centerY - offset),
            strokeWidth = 4.dp.toPx()
        )
        drawLine(
            color = primaryColor,
            start = Offset(centerX + offset, centerY - offset),
            end = Offset(centerX + offset, centerY - offset + bracketLength),
            strokeWidth = 4.dp.toPx()
        )

        // Bottom-left bracket
        drawLine(
            color = primaryColor,
            start = Offset(centerX - offset, centerY + offset),
            end = Offset(centerX - offset + bracketLength, centerY + offset),
            strokeWidth = 4.dp.toPx()
        )
        drawLine(
            color = primaryColor,
            start = Offset(centerX - offset, centerY + offset),
            end = Offset(centerX - offset, centerY + offset - bracketLength),
            strokeWidth = 4.dp.toPx()
        )

        // Bottom-right bracket
        drawLine(
            color = primaryColor,
            start = Offset(centerX + offset, centerY + offset),
            end = Offset(centerX + offset - bracketLength, centerY + offset),
            strokeWidth = 4.dp.toPx()
        )
        drawLine(
            color = primaryColor,
            start = Offset(centerX + offset, centerY + offset),
            end = Offset(centerX + offset, centerY + offset - bracketLength),
            strokeWidth = 4.dp.toPx()
        )
    }
}
