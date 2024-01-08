package com.yashkumartech.groupgab.presentation.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource

@Composable
fun AppLogoLarge(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
    ) {
        val gradientColors = listOf(
            MaterialTheme.colorScheme.primary,
            MaterialTheme.colorScheme.secondary,
            MaterialTheme.colorScheme.tertiary
        )
        val currentFontSizePx = with(LocalDensity.current) { MaterialTheme.typography.displayLarge.fontSize.toPx() }
        val currentFontSizeDoublePx = currentFontSizePx * 2
        val infiniteTransition = rememberInfiniteTransition(label = "")
        val offset by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = currentFontSizeDoublePx,
            animationSpec = infiniteRepeatable(tween(1000, easing = LinearEasing)),
            label = ""
        )

        val brush = Brush.linearGradient(
            colors = gradientColors,
            start = Offset(offset, offset),
            end = Offset(offset + currentFontSizePx, offset + currentFontSizePx),
            tileMode = TileMode.Mirror
        )

        Text(
            text = "Group Gab",
            style = MaterialTheme.typography.displayLarge.copy(
                brush = brush
            )
        )
    }
}