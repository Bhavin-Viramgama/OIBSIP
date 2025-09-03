package com.example.calculator


import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign


@Composable
fun ResponsiveText(
    modifier: Modifier = Modifier,
    text: String,
    style: TextStyle,
    color: Color = Color.Unspecified,
    textAlign: TextAlign = TextAlign.End
) {
    var responsiveTextStyle by remember { mutableStateOf(style) }

    LaunchedEffect(text) {
        responsiveTextStyle = style
    }

    Text(
        text = text,
        modifier = modifier,
        style = responsiveTextStyle,
        color = color,
        textAlign = textAlign,
        maxLines = 1,
        softWrap = false,
        onTextLayout = { result ->
            if (result.didOverflowWidth) {
                responsiveTextStyle = responsiveTextStyle.copy(
                    fontSize = responsiveTextStyle.fontSize * 0.95f
                )
            }
        }
    )
}
