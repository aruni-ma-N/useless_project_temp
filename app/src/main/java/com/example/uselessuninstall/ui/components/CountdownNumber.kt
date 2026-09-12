package com.example.uselessuninstall.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uselessuninstall.ui.theme.UselessUninstallTheme

/**
 * Animated prominent countdown number that smoothly scales and fades as the counter ticks down.
 *
 * Designed as the visual anchor of the countdown screen.
 *
 * @param number Current second count to display.
 * @param modifier Modifier applied to the outer container.
 */
@Composable
fun CountdownNumber(
    number: Int,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(170.dp)
            .background(
                color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.4f),
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        AnimatedContent(
            targetState = number,
            transitionSpec = {
                (scaleIn(initialScale = 0.5f) + fadeIn()) togetherWith
                        (scaleOut(targetScale = 1.4f) + fadeOut())
            },
            label = "CountdownNumberAnimation"
        ) { targetNumber ->
            Text(
                text = "$targetNumber",
                fontSize = 84.sp,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CountdownNumberPreview5() {
    UselessUninstallTheme {
        CountdownNumber(number = 5)
    }
}

@Preview(showBackground = true)
@Composable
fun CountdownNumberPreview1() {
    UselessUninstallTheme {
        CountdownNumber(number = 1)
    }
}
