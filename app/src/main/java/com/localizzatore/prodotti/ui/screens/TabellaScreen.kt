package com.localizzatore.prodotti.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.localizzatore.prodotti.ui.components.ProdottoItem
import com.localizzatore.prodotti.viewmodel.TabellaViewModel

/** Schermata con la lista completa di tutti i prodotti salvati, eliminabili con swipe. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TabellaScreen(viewModel: TabellaViewModel) {
    val prodotti by viewModel.prodotti.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (prodotti.isEmpty()) "I tuoi prodotti"
                        else "I tuoi prodotti (${prodotti.size})"
                    )
                }
            )
        }
    ) { padding ->
        if (prodotti.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "📦", style = MaterialTheme.typography.displayMedium)
                    Text(
                        text = "Non hai ancora salvato nessun prodotto.\nVai su \"Aggiungi\" per iniziare.",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        modifier = Modifier.padding(top = 12.dp, start = 32.dp, end = 32.dp)
                    )
                }
            }
        } else {
            LazyColumn(modifier = Modifier.padding(padding)) {
                items(prodotti, key = { it.id }) { prodotto ->
                    ProdottoItem(
                        prodotto = prodotto,
                        onClick = {},
                        onElimina = { viewModel.elimina(prodotto) }
                    )
                    HorizontalDivider()
                }
            }
        }
    }
}
