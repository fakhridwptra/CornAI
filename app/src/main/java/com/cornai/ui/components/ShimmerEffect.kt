package com.cornai.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import com.cornai.ui.theme.*

@Composable
fun shimmerEffect(): Brush {
    val shimmerColors = listOf(
        Surface.copy(alpha = 0.6f),
        Surface.copy(alpha = 0.2f),
        Surface.copy(alpha = 0.6f)
    )
    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim = transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_translate"
    )
    return Brush.linearGradient(
        colors = shimmerColors,
        start = Offset.Zero,
        end = Offset(x = translateAnim.value, y = translateAnim.value)
    )
}

@Composable
fun ShimmerBox(
    modifier: Modifier = Modifier,
    width: Int? = null,
    height: Int = 16,
    cornerRadius: Int = 8
) {
    Box(
        modifier = modifier
            .then(if (width != null) Modifier.width(width.dp) else Modifier)
            .height(height.dp)
            .clip(RoundedCornerShape(cornerRadius.dp))
            .background(shimmerEffect())
    )
}

@Composable
fun ShimmerCard(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(100.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(shimmerEffect())
    )
}

@Composable
fun ShimmerListItem() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        ShimmerBox(modifier = Modifier, width = 56, height = 56, cornerRadius = 16)
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            ShimmerBox(modifier = Modifier.fillMaxWidth(), height = 16)
            Spacer(modifier = Modifier.height(8.dp))
            ShimmerBox(modifier = Modifier.fillMaxWidth(0.6f), height = 12)
        }
        Spacer(modifier = Modifier.width(12.dp))
        ShimmerBox(modifier = Modifier, width = 60, height = 24, cornerRadius = 12)
    }
}

@Composable
fun ShimmerProfile() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
    ) {
        ShimmerBox(modifier = Modifier, width = 100, height = 100, cornerRadius = 50)
        Spacer(modifier = Modifier.height(16.dp))
        ShimmerBox(modifier = Modifier.fillMaxWidth(0.4f), height = 22)
        Spacer(modifier = Modifier.height(8.dp))
        ShimmerBox(modifier = Modifier.fillMaxWidth(0.3f), height = 14)
    }
}

@Composable
fun ShimmerStats() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        repeat(3) {
            Column(horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally) {
                ShimmerBox(modifier = Modifier, width = 40, height = 32)
                Spacer(modifier = Modifier.height(4.dp))
                ShimmerBox(modifier = Modifier, width = 50, height = 12)
            }
        }
    }
}