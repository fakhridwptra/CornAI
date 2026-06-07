package com.cornai.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cornai.ui.theme.*
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun BarChart(
    data: List<ChartData>,
    modifier: Modifier = Modifier,
    barColor: Color = GreenPrimary,
    height: Dp = 200.dp
) {
    if (data.isEmpty()) return

    val maxValue = data.maxOfOrNull { it.value } ?: 1f

    Column(modifier = modifier) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(height)
        ) {
            val barWidth = size.width / (data.size * 2f)
            val maxBarHeight = size.height * 0.8f

            data.forEachIndexed { index, item ->
                val barHeight = (item.value / maxValue) * maxBarHeight
                val x = barWidth + (index * barWidth * 2)

                // Draw bar with gradient
                drawRoundRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(barColor, barColor.copy(alpha = 0.5f)),
                        startY = size.height - barHeight,
                        endY = size.height
                    ),
                    topLeft = Offset(x - barWidth / 2, size.height - barHeight),
                    size = Size(barWidth, barHeight),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(8f, 8f)
                )

                // Draw value on top
                drawCircle(
                    color = barColor,
                    radius = 4f,
                    center = Offset(x, size.height - barHeight - 10)
                )
            }

            // Draw baseline
            drawLine(
                color = Color.Gray.copy(alpha = 0.3f),
                start = Offset(0f, size.height),
                end = Offset(size.width, size.height),
                strokeWidth = 2f
            )
        }

        // Labels
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            data.forEach { item ->
                Text(
                    text = item.label,
                    fontSize = 10.sp,
                    color = TextSecondary,
                    modifier = Modifier.weight(1f),
                    maxLines = 1
                )
            }
        }
    }
}

data class ChartData(
    val label: String,
    val value: Float,
    val color: Color? = null
)

@Composable
fun PieChart(
    data: List<PieChartData>,
    modifier: Modifier = Modifier,
    size: Dp = 200.dp
) {
    if (data.isEmpty()) return

    val total = data.sumOf { it.value.toDouble() }.toFloat()

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            var startAngle = -90f

            data.forEach { item ->
                val sweepAngle = (item.value / total) * 360f

                drawArc(
                    color = item.color,
                    startAngle = startAngle,
                    sweepAngle = sweepAngle,
                    useCenter = true,
                    size = Size(size.toPx(), size.toPx())
                )

                startAngle += sweepAngle
            }

            // Center hole
            drawCircle(
                color = Color.White,
                radius = size.toPx() * 0.3f
            )
        }

        // Center text
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "${data.size}",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Text(
                text = "Total",
                fontSize = 10.sp,
                color = TextSecondary
            )
        }
    }
}

data class PieChartData(
    val label: String,
    val value: Float,
    val color: Color
)

@Composable
fun DonutChart(
    data: List<PieChartData>,
    modifier: Modifier = Modifier,
    size: Dp = 200.dp,
    strokeWidth: Dp = 24.dp
) {
    if (data.isEmpty()) return

    val total = data.sumOf { it.value.toDouble() }.toFloat()

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            var startAngle = -90f
            val radius = (size.toPx() - strokeWidth.toPx()) / 2
            val center = Offset(size.toPx() / 2, size.toPx() / 2)

            data.forEach { item ->
                val sweepAngle = (item.value / total) * 360f

                drawArc(
                    color = item.color,
                    startAngle = startAngle,
                    sweepAngle = sweepAngle,
                    useCenter = false,
                    topLeft = Offset(center.x - radius, center.y - radius),
                    size = Size(radius * 2, radius * 2),
                    style = Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Round)
                )

                startAngle += sweepAngle
            }
        }

        // Legend
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            data.firstOrNull()?.let {
                Text(
                    text = "${((it.value / total) * 100).toInt()}%",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = it.color
                )
                Text(
                    text = it.label,
                    fontSize = 10.sp,
                    color = TextSecondary
                )
            }
        }
    }
}

@Composable
fun LineChart(
    data: List<ChartData>,
    modifier: Modifier = Modifier,
    lineColor: Color = GreenPrimary,
    height: Dp = 200.dp
) {
    if (data.isEmpty()) return

    val infiniteTransition = rememberInfiniteTransition(label = "lineChart")
    val animationProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "progress"
    )

    Column(modifier = modifier) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(height)
        ) {
            val maxValue = data.maxOfOrNull { it.value } ?: 1f
            val pointSpacing = size.width / (data.size - 1).coerceAtLeast(1)
            val maxHeight = size.height * 0.8f

            val points = data.mapIndexed { index, item ->
                Offset(
                    index * pointSpacing,
                    size.height - (item.value / maxValue) * maxHeight
                )
            }

            // Draw gradient area
            val areaPath = androidx.compose.ui.graphics.Path().apply {
                moveTo(0f, size.height)
                points.forEach { lineTo(it.x, it.y) }
                lineTo(size.width, size.height)
                close()
            }

            drawPath(
                path = areaPath,
                brush = Brush.verticalGradient(
                    colors = listOf(lineColor.copy(alpha = 0.3f), lineColor.copy(alpha = 0f))
                )
            )

            // Draw line
            val linePath = androidx.compose.ui.graphics.Path().apply {
                points.forEachIndexed { index, point ->
                    if (index == 0) moveTo(point.x, point.y)
                    else lineTo(point.x, point.y)
                }
            }

            drawPath(
                path = linePath,
                color = lineColor,
                style = Stroke(width = 3f, cap = StrokeCap.Round)
            )

            // Draw points
            points.forEach { point ->
                drawCircle(
                    color = lineColor,
                    radius = 6f,
                    center = point
                )
                drawCircle(
                    color = Color.White,
                    radius = 3f,
                    center = point
                )
            }
        }

        // Labels
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            data.forEach { item ->
                Text(
                    text = item.label,
                    fontSize = 10.sp,
                    color = TextSecondary
                )
            }
        }
    }
}

@Composable
fun ProgressCircle(
    progress: Float,
    modifier: Modifier = Modifier,
    size: Dp = 100.dp,
    strokeWidth: Dp = 10.dp,
    color: Color = GreenPrimary,
    backgroundColor: Color = Surface
) {
    Canvas(modifier = modifier.size(size)) {
        val radius = (size.toPx() - strokeWidth.toPx()) / 2
        val center = Offset(size.toPx() / 2, size.toPx() / 2)

        // Background circle
        drawCircle(
            color = backgroundColor,
            radius = radius,
            center = center,
            style = Stroke(width = strokeWidth.toPx())
        )

        // Progress arc
        drawArc(
            color = color,
            startAngle = -90f,
            sweepAngle = progress * 360f,
            useCenter = false,
            topLeft = Offset(center.x - radius, center.y - radius),
            size = Size(radius * 2, radius * 2),
            style = Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Round)
        )
    }
}

@Composable
fun AnimatedCounter(
    targetValue: Int,
    modifier: Modifier = Modifier,
    duration: Int = 1000,
    color: Color = GreenPrimary
) {
    var currentValue by remember { mutableStateOf(0) }

    LaunchedEffect(targetValue) {
        val startTime = System.currentTimeMillis()
        while (currentValue < targetValue) {
            val elapsed = System.currentTimeMillis() - startTime
            val progress = (elapsed.toFloat() / duration).coerceIn(0f, 1f)
            currentValue = (targetValue * progress).toInt()
            kotlinx.coroutines.delay(16)
        }
        currentValue = targetValue
    }

    Text(
        text = currentValue.toString(),
        fontSize = 32.sp,
        fontWeight = FontWeight.Bold,
        color = color,
        modifier = modifier
    )
}

@Composable
fun StatsCounter(
    label: String,
    value: Int,
    icon: @Composable () -> Unit,
    color: Color = GreenPrimary
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(16.dp)
    ) {
        Box(
            modifier = Modifier
                .size(60.dp)
                .background(color.copy(alpha = 0.1f), RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center
        ) {
            icon()
        }
        Spacer(modifier = Modifier.height(8.dp))
        AnimatedCounter(targetValue = value, color = color)
        Text(
            text = label,
            fontSize = 12.sp,
            color = TextSecondary
        )
    }
}

@Composable
fun HeatmapGrid(
    data: List<List<Float>>,
    modifier: Modifier = Modifier,
    cellSize: Dp = 24.dp
) {
    Column(modifier = modifier) {
        data.forEachIndexed { rowIndex, row ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                row.forEachIndexed { colIndex, value ->
                    val alpha = value.coerceIn(0f, 1f)
                    Box(
                        modifier = Modifier
                            .size(cellSize)
                            .background(
                                GreenPrimary.copy(alpha = alpha),
                                RoundedCornerShape(4.dp)
                            )
                    )
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}