package com.localizzatore.prodotti.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.dp

/** Pulsante microfono con animazione "a pulsazione" mentre è in ascolto. */
@Composable
fun MicrofoneButton(
    inAscolto: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val transizioneInfinita = rememberInfiniteTransition(label = "pulsazione")
    val scala by transizioneInfinita.animateFloat(
        initialValue = 1f,
        targetValue = if (inAscolto) 1.15f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scala"
    )

    IconButton(
        onClick = onClick,
        modifier = modifier
            .size(72.dp)
            .scale(if (inAscolto) scala else 1f)
            .background(
                color = if (inAscolto) MaterialTheme.colorScheme.errorContainer
                else MaterialTheme.colorScheme.primaryContainer,
                shape = CircleShape
            )
    ) {
        Icon(
            imageVector = if (inAscolto) Icons.Filled.MicOff else Icons.Filled.Mic,
            contentDescription = if (inAscolto) "Ferma registrazione" else "Avvia registrazione vocale",
            tint = if (inAscolto) MaterialTheme.colorScheme.onErrorContainer
            else MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = Modifier.size(36.dp)
        )
    }
}
