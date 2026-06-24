package com.localizzatore.prodotti.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.localizzatore.prodotti.data.local.Prodotto
import com.localizzatore.prodotti.domain.model.Categoria
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/** Riga prodotto nella tabella/lista, con swipe verso sinistra per eliminare. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProdottoItem(
    prodotto: Prodotto,
    onClick: () -> Unit,
    onElimina: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { valore ->
            if (valore == SwipeToDismissBoxValue.EndToStart) {
                onElimina()
                true
            } else {
                false
            }
        }
    )

    SwipeToDismissBox(
        state = dismissState,
        modifier = modifier,
        enableDismissFromStartToEnd = false,
        backgroundContent = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.errorContainer)
                    .padding(horizontal = 24.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = "Elimina",
                    tint = MaterialTheme.colorScheme.onErrorContainer
                )
            }
        }
    ) {
        ListItem(
            modifier = Modifier.clickable(onClick = onClick),
            headlineContent = { Text(prodotto.nome, fontWeight = FontWeight.SemiBold) },
            supportingContent = {
                Column {
                    Text("📍 ${prodotto.posizione}")
                    Text(
                        text = "${prodotto.stanza} · ${formattaData(prodotto.timestamp)}",
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.Gray
                    )
                }
            },
            leadingContent = {
                Text(
                    text = Categoria.fromString(prodotto.categoria).emoji,
                    style = MaterialTheme.typography.headlineSmall
                )
            }
        )
    }
}

private fun formattaData(timestamp: Long): String {
    val formato = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.ITALY)
    return formato.format(Date(timestamp))
}
