package com.example.unitconverter.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.unit.dp
import com.example.unitconverter.data.Category

@Composable
fun AnimatedCategoryGradient(
    category: Category,
    modifier: Modifier = Modifier
) {
    val colors = when (category) {
        Category.LENGTH->
            listOf(
                Color(0xFF8528E2),
                Color(0xFF7C23E1),
                Color(0xFF721EE1),
                Color(0xFF6919E1),
                Color(0xFF6013E0),
                Color(0xFF560DE0),
                Color(0xFF4A00E0)
            )

        Category.WEIGHT->
            listOf(
            Color(0xFF0CA199),
            Color(0xFF0A9991),
            Color(0xFF099189),
            Color(0xFF078981),
            Color(0xFF078981)
        )

        Category.TEMPERATURE->
            listOf(
            Color(0xFFF64729),
            Color(0xFFF33A33),
            Color(0xFFF22F3C),
            Color(0xFFF02544),
            Color(0xFFEE1C4C)
        )

    }


    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(
                    colors = colors,
                    start = Offset.Zero,
                    end = Offset.Infinite,
                    tileMode = TileMode.Mirror
                )
            )
    )
}

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    corner: Int = 28,
    content: @Composable () -> Unit = {}
) {
    val shape = RoundedCornerShape(corner.dp)
    val overlay = Brush.verticalGradient(
        listOf(
            Color.White.copy(alpha = 0.12f),
            Color.White.copy(alpha = 0.04f)
        )
    )

    Box(
        modifier = modifier.clip(shape)
    ) {
        // Blurred background layer
        Box(
            modifier = Modifier
                .matchParentSize()
                .blur(14.dp) // blur only this layer
                .background(Color.White.copy(alpha = 0.04f), shape)
                .border(1.dp, Color.White.copy(alpha = 0.10f), shape)
                .background(overlay, shape)
        )

        // Foreground content (kept sharp)
        Box(modifier = Modifier.clip(shape)) {
            content()
        }
    }
}

