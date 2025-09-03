package com.example.calculator

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.EaseInSine
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Icon
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.style.TextDecoration
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun CalculatorScreen(
    viewModel: CalculatorViewModel = viewModel()
) {
    val state = viewModel.state
    val buttonSpacing = 8.dp

    if (state.showAboutDialog) {
        AboutDialog(
            onDismiss = { viewModel.onAction(CalculatorAction.HideAboutDialog) }
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp, start = 4.dp)
                .align(Alignment.TopCenter)

        ) {
            Icon(
                imageVector = Icons.Outlined.Info,
                contentDescription = "About App",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .clip(RoundedCornerShape(24))
                    .clickable { viewModel.onAction(CalculatorAction.ShowAboutDialog) }
                    .background(color = MaterialTheme.colorScheme.surfaceVariant)
                    .padding(8.dp)

            )
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter),
            verticalArrangement = Arrangement.spacedBy(buttonSpacing)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 32.dp),
                horizontalAlignment = Alignment.End
            ) {
                AnimatedContent(
                    targetState = state.history,
                    transitionSpec = {
                        fadeIn(animationSpec = tween(200)) togetherWith
                                fadeOut(animationSpec = tween(200))
                    },
                    label = "historyAnimation"
                ) { targetHistory ->
                    val scrollState = rememberScrollState()

                    LaunchedEffect(targetHistory) {
                        scrollState.animateScrollTo(scrollState.maxValue)
                    }

                    Text(
                        text = targetHistory,
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                        textAlign = TextAlign.End,
                        maxLines = 1,
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(scrollState)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                AnimatedContent(
                    targetState = state.display,
                    transitionSpec = {
                        (slideInHorizontally(
                            initialOffsetX = { it / 30 },
                            animationSpec = tween(durationMillis = 150, easing = FastOutLinearInEasing)
                        ) + fadeIn(animationSpec = tween(100, easing = EaseInSine)))
                            .togetherWith(
                                slideOutHorizontally(
                                    targetOffsetX = { -it / 30 },
                                    animationSpec = tween(durationMillis = 150, easing = FastOutLinearInEasing)
                                ) + fadeOut(animationSpec = tween(100))
                            )
                    },
                    label = "mainDisplayAnimation"
                ) { targetDisplay ->
                    val scrollState = rememberScrollState()

                    LaunchedEffect(targetDisplay) {
                        scrollState.animateScrollTo(scrollState.maxValue)
                    }

                    ResponsiveText(
                        text = targetDisplay,
                        style = MaterialTheme.typography.displayLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(scrollState)
                    )
                }

            }
            // Buttons
            val buttonModifier = Modifier
                .aspectRatio(1f)
                .weight(1f)

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(buttonSpacing)) {
                CalculatorButton(symbol = "AC", modifier = buttonModifier, color = MaterialTheme.colorScheme.tertiaryContainer, onClick = { viewModel.onAction(CalculatorAction.Clear) })
                CalculatorButton(
                    symbol = "%",
                    modifier = buttonModifier,
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    onClick = { viewModel.onAction(CalculatorAction.Percentage) }
                )
                CalculatorButton(symbol = "Del", modifier = buttonModifier, color = MaterialTheme.colorScheme.tertiaryContainer, onClick = { viewModel.onAction(CalculatorAction.Delete) })
                CalculatorButton(symbol = "÷", modifier = buttonModifier, color = MaterialTheme.colorScheme.secondaryContainer, onClick = { viewModel.onAction(CalculatorAction.Operation("÷")) })
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(buttonSpacing)) {
                CalculatorButton(symbol = "7", modifier = buttonModifier, onClick = { viewModel.onAction(CalculatorAction.Number("7")) })
                CalculatorButton(symbol = "8", modifier = buttonModifier, onClick = { viewModel.onAction(CalculatorAction.Number("8")) })
                CalculatorButton(symbol = "9", modifier = buttonModifier, onClick = { viewModel.onAction(CalculatorAction.Number("9")) })
                CalculatorButton(symbol = "×", modifier = buttonModifier, color = MaterialTheme.colorScheme.secondaryContainer, onClick = { viewModel.onAction(CalculatorAction.Operation("×")) })
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(buttonSpacing)) {
                CalculatorButton(symbol = "4", modifier = buttonModifier, onClick = { viewModel.onAction(CalculatorAction.Number("4")) })
                CalculatorButton(symbol = "5", modifier = buttonModifier, onClick = { viewModel.onAction(CalculatorAction.Number("5")) })
                CalculatorButton(symbol = "6", modifier = buttonModifier, onClick = { viewModel.onAction(CalculatorAction.Number("6")) })
                CalculatorButton(symbol = "-", modifier = buttonModifier, color = MaterialTheme.colorScheme.secondaryContainer, onClick = { viewModel.onAction(CalculatorAction.Operation("-")) })
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(buttonSpacing)) {
                CalculatorButton(symbol = "1", modifier = buttonModifier, onClick = { viewModel.onAction(CalculatorAction.Number("1")) })
                CalculatorButton(symbol = "2", modifier = buttonModifier, onClick = { viewModel.onAction(CalculatorAction.Number("2")) })
                CalculatorButton(symbol = "3", modifier = buttonModifier, onClick = { viewModel.onAction(CalculatorAction.Number("3")) })
                CalculatorButton(symbol = "+", modifier = buttonModifier, color = MaterialTheme.colorScheme.secondaryContainer, onClick = { viewModel.onAction(CalculatorAction.Operation("+")) })
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(buttonSpacing)) {
                CalculatorButton(symbol = "0", modifier = Modifier
                    .aspectRatio(2f)
                    .weight(2f), onClick = { viewModel.onAction(CalculatorAction.Number("0")) })
                CalculatorButton(symbol = ".", modifier = buttonModifier, onClick = { viewModel.onAction(CalculatorAction.Decimal) })
                CalculatorButton(symbol = "=", modifier = buttonModifier, color = MaterialTheme.colorScheme.primaryContainer, onClick = { viewModel.onAction(CalculatorAction.Calculate) })
            }
        }
    }
}

@Composable
fun AboutDialog(
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.fillMaxWidth(),
        title = {

            Text(
                text = "Calculator App",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .padding(10.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = "A modern and minimal calculator app built with Jetpack Compose and Material 3. " +
                            "It includes unique features like repeated equals (=) operations, smooth Expressive animations, " +
                            "and a clean responsive UI.",
                    style = MaterialTheme.typography.bodyLarge,
                    lineHeight = 22.sp
                )
                Text(
                    text = "Features:",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                )
                Text("• Standard arithmetic operations")
                Text("• Unique repeated '=' functionality")
                Text("• Smooth animations for expressions")
                Text("• Light and Dark Modes (System Default)")
                Text("• Expressive animations for Buttons")
                Text("• Light & clean Material 3 design")

                Spacer(modifier = Modifier.height(24.dp))
                CreditText()
            }


        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("OK")
            }
        }
    )
}


@Composable
fun CreditText() {
    val annotatedText = buildAnnotatedString {
        append("Made by ")

        pushLink(
            LinkAnnotation.Url(
                url = "https://www.linkedin.com/in/bhavin-viramgama/",
                styles = TextLinkStyles(
                    style = SpanStyle(
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                )
            )
        )
        withStyle(
            style = SpanStyle(
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                color = MaterialTheme.colorScheme.onSurface,
                textDecoration = TextDecoration.Underline
            )
        ){
            append("Bhavin")
        }
        pop()
        append(" with ❤️")
    }

    Text(
        text = annotatedText,
        style = TextStyle(
            fontSize = 16.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Light,
            color = MaterialTheme.colorScheme.onSurface
        )
    )
}

