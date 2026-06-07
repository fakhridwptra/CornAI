package com.cornai.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.cornai.R
import com.cornai.ui.theme.GoldPrimary
import com.cornai.ui.theme.GreenPrimary

@Composable
fun CornIcon(
    modifier: Modifier = Modifier,
    size: Dp = 120.dp,
    primaryColor: Color = GreenPrimary,
    accentColor: Color = GoldPrimary
) {
    Image(
        painter = painterResource(id = R.drawable.logo_cornai),
        contentDescription = "Corn AI Logo",
        modifier = modifier
            .size(size)
            .clip(CircleShape),
        contentScale = ContentScale.Crop
    )
}
