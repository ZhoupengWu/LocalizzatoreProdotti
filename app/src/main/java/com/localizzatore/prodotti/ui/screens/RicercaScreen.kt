package com.localizzatore.prodotti.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.localizzatore.prodotti.ui.components.ProdottoItem
import com.localizzatore.prodotti.ui.components.SearchBar
import com.localizzatore.prodotti.viewmodel.RicercaViewModel

/** Schermata di ricerca rapida tra nome e posizione dei prodotti salvati. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RicercaScreen(viewModel: RicercaViewModel) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Cerca un prodotto") })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            SearchBar(
                query = uiState.query,
                onQueryChange = viewModel::aggiornaQuery,
                onCerca = viewModel::cerca
            )

            androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(16.dp))

            when {
                !uiState.cercato -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = "Scrivi il nome o la posizione di un prodotto e premi cerca.",
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center
                        )
                    }
                }
                uiState.risultati.isEmpty() -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "❌", style = MaterialTheme.typography.displayMedium)
                            Text(
                                text = "Nessun prodotto trovato per \"${uiState.query}\".",
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(top = 12.dp)
                            )
                        }
                    }
                }
                else -> {
                    Text(
                        text = "✅ Trovati ${uiState.risultati.size} risultati",
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    LazyColumn {
                        items(uiState.risultati, key = { it.id }) { prodotto ->
                            ProdottoItem(
                                prodotto = prodotto,
                                onClick = {},
                                onElimina = {}
                            )
                            HorizontalDivider()
                        }
                    }
                }
            }
        }
    }
}
