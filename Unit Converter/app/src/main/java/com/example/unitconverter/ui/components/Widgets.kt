package com.example.unitconverter.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.unitconverter.data.Category

@Composable
fun CategoryChips(
    categories: List<Category>,
    selected: Category,
    onSelect: (Category) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(modifier = modifier.padding(top = 24.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        items(categories) { cat ->
            FilterChip(
                selected = selected == cat,
                onClick = { onSelect(cat) },
                label = { Text(cat.name.lowercase().replaceFirstChar { it.uppercase() }) },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.ArrowUpward, contentDescription = null)
                }
            )
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun BigValueCard(
    value: String,
    unitName: String,
    editable: Boolean,
    onValueChange: (String) -> Unit,
    onUnitClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    GlassCard(modifier = modifier) {
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (editable) {
                    OutlinedTextField(
                        value = value,
                        onValueChange = { txt ->
                            // allow empty or decimal-number
                            if (txt.isEmpty() || txt.matches(Regex("""^\d*\.?\d*$""")))
                                onValueChange(txt)
                        },
                        singleLine = true,
                        textStyle = MaterialTheme.typography.displayLarge.copy(textAlign = TextAlign.Center, color = Color.Black),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    AnimatedContent(
                        targetState = value.ifBlank { "0" },
                        transitionSpec = {
                            fadeIn(animationSpec = tween(180)).togetherWith(fadeOut(animationSpec = tween(180))) using SizeTransform(false)
                        },
                        label = "valueAnim"
                    ) { v ->
                        Text(
                            text = v,
                            style = MaterialTheme.typography.displayLarge,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                Spacer(Modifier.height(12.dp))

                AssistChip(
                    onClick = onUnitClick,
                    label = { Text(unitName) }
                )
            }
        }
    }
}

@Composable
fun SwapBubble(onClick: () -> Unit, modifier: Modifier = Modifier) {
    Surface(
        tonalElevation = 4.dp,
        shape = MaterialTheme.shapes.extraLarge
    ) {
        Box(modifier = Modifier.clickable(onClick = onClick)
                .size(52.dp)
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.SwapVert, contentDescription = "Swap")
        }
    }
}
