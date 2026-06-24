package com.localizzatore.prodotti.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.localizzatore.prodotti.ui.components.StanzaCard
import com.localizzatore.prodotti.viewmodel.MappaViewModel

/**
 * Mappa semplificata della casa: una griglia di stanze con il numero di oggetti in ognuna.
 * Toccando una stanza si entra nel dettaglio con la lista dei prodotti che contiene.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MappaScreen(viewModel: MappaViewModel) {
    val conteggi by viewModel.conteggioPerStanza.collectAsState()
    val stanzaSelezionata by viewModel.stanzaSelezionata.collectAsState()
    val prodottiStanza by viewModel.prodottiStanzaSelezionata.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (stanzaSelezionata == null) "Mappa di casa"
                        else stanzaSelezionata!!.replaceFirstChar { it.uppercase() }
                    )
                },
                navigationIcon = {
                    if (stanzaSelezionata != null) {
                        IconButton(onClick = { viewModel.selezionaStanza(null) }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Torna alla mappa")
                        }
                    }
                }
            )
        }
    ) { padding ->
        if (stanzaSelezionata == null) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(16.dp),
                verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(16.dp)
            ) {
                items(viewModel.stanzeDisponibili) { nomeStanza ->
                    StanzaCard(
                        nomeStanza = nomeStanza,
                        numeroProdotti = conteggi[nomeStanza.lowercase()] ?: 0,
                        onClick = { viewModel.selezionaStanza(nomeStanza.lowercase()) }
                    )
                }
            }
        } else {
            if (prodottiStanza.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "🏠", style = MaterialTheme.typography.displayMedium)
                        Text(
                            text = "Nessun prodotto salvato in questa stanza.",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(top = 12.dp)
                        )
                    }
                }
            } else {
                LazyColumn(modifier = Modifier.padding(padding)) {
                    items(prodottiStanza, key = { it.id }) { prodotto ->
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
