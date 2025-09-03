package com.example.unitconverter


import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import com.example.unitconverter.data.Category
import com.example.unitconverter.data.UnitsRepo
import com.example.unitconverter.ui.components.*
import com.example.unitconverter.ui.theme.AppTheme
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.round

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.statusBarColor = android.graphics.Color.TRANSPARENT
        setContent { App() }
    }
}

private enum class ActiveField { FROM, TO }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App() {
    AppTheme {
        // APP STATE
        var category by remember { mutableStateOf(Category.LENGTH) }
        var fromUnit by remember { mutableStateOf(UnitsRepo.units(category).first()) }
        var toUnit by remember { mutableStateOf(UnitsRepo.units(category)[1]) }

        var fromText by remember { mutableStateOf("2.5") }
        var toText by remember { mutableStateOf("") }
        var active by remember { mutableStateOf(ActiveField.FROM) }

        // Re-map units when category changes
        LaunchedEffect(category) {
            val us = UnitsRepo.units(category)
            fromUnit = us.first()
            toUnit = us.getOrElse(1) { us.first() }
            fromText = ""
            toText = ""
        }

        // Conversion
        fun computeAndUpdate() {
            val src = if (active == ActiveField.FROM) fromText else toText
            val parsed = src.toDoubleOrNull()
            val value = parsed ?: 0.0
            val result = if (active == ActiveField.FROM)
                UnitsRepo.convert(value, fromUnit, toUnit)
            else
                UnitsRepo.convert(value, toUnit, fromUnit)

            fun pretty(d: Double): String {
                if (d == 0.0) return "0"
                val mag = kotlin.math.max(0.0, kotlin.math.floor(kotlin.math.log10(abs(d)))).toInt()
                val decimals = (4 - mag).coerceIn(0, 6)
                val m = 10.0.pow(decimals)
                return (round(d * m) / m).toString()
            }

            if (active == ActiveField.FROM) toText = pretty(result) else fromText = pretty(result)
        }

        // Recompute when inputs/units change
        LaunchedEffect(fromText, toText, fromUnit, toUnit, active) { computeAndUpdate() }

        // Bottom sheet
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        var showSheet by remember { mutableStateOf(false) }
        var pickingFor by remember { mutableStateOf(ActiveField.FROM) }
        val scope = rememberCoroutineScope()
        val haptic = LocalHapticFeedback.current

        Box(Modifier.fillMaxSize()) {
            // Background
            AnimatedCategoryGradient(category, Modifier.fillMaxSize())

            // Foreground
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp, vertical = 34.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Unit Converter",
                    modifier = Modifier.padding(top = 16.dp),
                    style = TextStyle(
                        fontFamily = FontFamily.Cursive,
                        fontWeight = FontWeight.Bold,
                        fontSize = 40.sp
                    ))
                // Category chips
                CategoryChips(
                    categories = Category.entries,
                    selected = category,
                    onSelect = { category = it }
                )

                Spacer(Modifier.height(18.dp))

                // Big glass card: FROM
                BigValueCard(
                    value = fromText,
                    unitName = "${fromUnit.name} (${fromUnit.symbol})",
                    editable = active == ActiveField.FROM,
                    onValueChange = {
                        active = ActiveField.FROM
                        fromText = it
                    },
                    onUnitClick = {
                        pickingFor = ActiveField.FROM
                        showSheet = true
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                )

                // Swap
                SwapBubble(
                    onClick = {
                        val tmp = fromUnit
                        fromUnit = toUnit
                        toUnit = tmp
                        active = if (active == ActiveField.FROM) ActiveField.FROM else ActiveField.TO
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    },
                    modifier = Modifier.padding(vertical = 12.dp)
                )

                // Big glass card: TO
                BigValueCard(
                    value = toText,
                    unitName = "${toUnit.name} (${toUnit.symbol})",
                    editable = active == ActiveField.TO,
                    onValueChange = {
                        active = ActiveField.TO
                        toText = it
                    },
                    onUnitClick = {
                        pickingFor = ActiveField.TO
                        showSheet = true
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                )

                Spacer(Modifier.height(18.dp))

                // Convert (mostly for haptic/UX; conversion is live)
                Button(
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        computeAndUpdate()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                ) { Text("Convert") }
            }

            Column(modifier = Modifier.fillMaxSize().padding(bottom = 24.dp),
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CreditText()
            }

            if (showSheet) {
                val currentUnits = UnitsRepo.units(category)
                ModalBottomSheet(
                    onDismissRequest = { showSheet = false },
                    sheetState = sheetState
                ) {
                    UnitPickerSheet(
                        title = if (pickingFor == ActiveField.FROM) "From unit" else "To unit",
                        units = currentUnits,
                        onPick = { picked ->
                            if (pickingFor == ActiveField.FROM) fromUnit = picked else toUnit = picked
                            scope.launch { sheetState.hide() }.invokeOnCompletion { showSheet = false }
                        }
                    )
                }
            }
        }
    }
}


@Composable
fun CreditText() {
    val context = LocalContext.current

    val annotatedText = buildAnnotatedString {
        append("Made by ")

        // Add the clickable "Me"
        pushStringAnnotation(tag = "LINK", annotation = "https://www.linkedin.com/in/bhavin-viramgama/")
        withStyle(
            style = SpanStyle(
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                textDecoration = TextDecoration.Underline
            )
        ) {
            append("Bhavin")
        }
        pop()

        append(" with ❤️")
    }

    ClickableText(
        text = annotatedText,
        style = TextStyle(
            fontSize = 16.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Light
        ),
        onClick = { offset ->
            annotatedText.getStringAnnotations(tag = "LINK", start = offset, end = offset)
                .firstOrNull()?.let { annotation ->
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(annotation.item))
                    context.startActivity(intent)
                }
        }
    )
}

