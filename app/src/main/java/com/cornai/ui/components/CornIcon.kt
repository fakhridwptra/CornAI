package com.cornai.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.cornai.ui.theme.GoldPrimary
import com.cornai.ui.theme.GreenPrimary

@Composable
fun CornIcon(
    modifier: Modifier = Modifier,
    size: Dp = 120.dp,
    primaryColor: Color = GreenPrimary,
    accentColor: Color = GoldPrimary
) {
    Canvas(modifier = modifier.size(size)) {
        val canvasWidth = size.toPx()
        val canvasHeight = size.toPx()

        // Corn body (yellow/gold)
        val cornBody = Path().apply {
            moveTo(canvasWidth * 0.35f, canvasHeight * 0.25f)
            lineTo(canvasWidth * 0.65f, canvasHeight * 0.25f)
            quadraticBezierTo(
                canvasWidth * 0.75f, canvasHeight * 0.4f,
                canvasWidth * 0.7f, canvasHeight * 0.55f
            )
            lineTo(canvasWidth * 0.65f, canvasHeight * 0.75f)
            quadraticBezierTo(
                canvasWidth * 0.55f, canvasHeight * 0.85f,
                canvasWidth * 0.35f, canvasHeight * 0.75f
            )
            lineTo(canvasWidth * 0.3f, canvasHeight * 0.55f)
            quadraticBezierTo(
                canvasWidth * 0.25f, canvasHeight * 0.4f,
                canvasWidth * 0.35f, canvasHeight * 0.25f
            )
            close()
        }
        drawPath(cornBody, accentColor, style = Fill)

        // Corn kernels (rows)
        val kernelColor = primaryColor
        val kernelSpacing = canvasWidth * 0.08f
        val startX = canvasWidth * 0.38f
        val startY = canvasHeight * 0.32f

        // Row 1
        drawRoundRect(
            color = kernelColor,
            topLeft = Offset(startX, startY),
            size = Size(kernelSpacing, kernelSpacing * 0.8f),
            cornerRadius = CornerRadius(4f, 4f)
        )
        drawRoundRect(
            color = kernelColor,
            topLeft = Offset(startX + kernelSpacing * 1.1f, startY),
            size = Size(kernelSpacing, kernelSpacing * 0.8f),
            cornerRadius = CornerRadius(4f, 4f)
        )
        drawRoundRect(
            color = kernelColor,
            topLeft = Offset(startX + kernelSpacing * 2.2f, startY),
            size = Size(kernelSpacing, kernelSpacing * 0.8f),
            cornerRadius = CornerRadius(4f, 4f)
        )

        // Row 2
        val row2Y = startY + kernelSpacing * 1.1f
        drawRoundRect(
            color = kernelColor,
            topLeft = Offset(startX + kernelSpacing * 0.1f, row2Y),
            size = Size(kernelSpacing, kernelSpacing * 0.8f),
            cornerRadius = CornerRadius(4f, 4f)
        )
        drawRoundRect(
            color = kernelColor,
            topLeft = Offset(startX + kernelSpacing * 1.2f, row2Y),
            size = Size(kernelSpacing, kernelSpacing * 0.8f),
            cornerRadius = CornerRadius(4f, 4f)
        )
        drawRoundRect(
            color = kernelColor,
            topLeft = Offset(startX + kernelSpacing * 2.3f, row2Y),
            size = Size(kernelSpacing, kernelSpacing * 0.8f),
            cornerRadius = CornerRadius(4f, 4f)
        )

        // Row 3
        val row3Y = row2Y + kernelSpacing * 1.1f
        drawRoundRect(
            color = kernelColor,
            topLeft = Offset(startX, row3Y),
            size = Size(kernelSpacing, kernelSpacing * 0.8f),
            cornerRadius = CornerRadius(4f, 4f)
        )
        drawRoundRect(
            color = kernelColor,
            topLeft = Offset(startX + kernelSpacing * 1.1f, row3Y),
            size = Size(kernelSpacing, kernelSpacing * 0.8f),
            cornerRadius = CornerRadius(4f, 4f)
        )
        drawRoundRect(
            color = kernelColor,
            topLeft = Offset(startX + kernelSpacing * 2.2f, row3Y),
            size = Size(kernelSpacing, kernelSpacing * 0.8f),
            cornerRadius = CornerRadius(4f, 4f)
        )

        // Row 4
        val row4Y = row3Y + kernelSpacing * 1.1f
        drawRoundRect(
            color = kernelColor,
            topLeft = Offset(startX + kernelSpacing * 0.1f, row4Y),
            size = Size(kernelSpacing, kernelSpacing * 0.8f),
            cornerRadius = CornerRadius(4f, 4f)
        )
        drawRoundRect(
            color = kernelColor,
            topLeft = Offset(startX + kernelSpacing * 1.2f, row4Y),
            size = Size(kernelSpacing, kernelSpacing * 0.8f),
            cornerRadius = CornerRadius(4f, 4f)
        )
        drawRoundRect(
            color = kernelColor,
            topLeft = Offset(startX + kernelSpacing * 2.3f, row4Y),
            size = Size(kernelSpacing, kernelSpacing * 0.8f),
            cornerRadius = CornerRadius(4f, 4f)
        )

        // Husk/leaves at top
        val huskColor = GreenPrimary
        val huskPath = Path().apply {
            moveTo(canvasWidth * 0.5f, canvasHeight * 0.05f)
            quadraticBezierTo(
                canvasWidth * 0.7f, canvasHeight * 0.15f,
                canvasWidth * 0.6f, canvasHeight * 0.28f
            )
            lineTo(canvasWidth * 0.4f, canvasHeight * 0.28f)
            quadraticBezierTo(
                canvasWidth * 0.3f, canvasHeight * 0.15f,
                canvasWidth * 0.5f, canvasHeight * 0.05f
            )
            close()
        }
        drawPath(huskPath, huskColor, style = Fill)
    }
}
