package com.localizzatore.prodotti.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.localizzatore.prodotti.domain.model.Categoria
import com.localizzatore.prodotti.domain.model.ProdottoEstratto
import com.localizzatore.prodotti.domain.model.Stanza
import com.localizzatore.prodotti.viewmodel.InputViewModel

/**
 * Schermata di conferma: mostra ciò che l'AI ha capito e permette di correggerlo
 * prima di salvarlo definitivamente nel database.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PreviewScreen(
    viewModel: InputViewModel,
    onSalvato: () -> Unit,
    onAnnulla: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val risultato = uiState.risultato

    // Se per qualche motivo non c'è più un risultato (es. processo morto e ricreato),
    // torniamo indietro invece di mostrare una schermata vuota.
    LaunchedEffect(risultato) {
        if (risultato == null) onAnnulla()
    }

    if (risultato == null) return

    var nome by remember(risultato) { mutableStateOf(risultato.prodotto) }
    var posizione by remember(risultato) { mutableStateOf(risultato.posizione) }
    var categoriaSelezionata by remember(risultato) { mutableStateOf(Categoria.fromString(risultato.categoria)) }
    var stanzaSelezionata by remember(risultato) { mutableStateOf(Stanza.fromString(risultato.stanza)) }
    var note by remember(risultato) { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Conferma i dati") })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "Controlla che l'AI abbia capito bene, poi correggi se serve.",
                style = MaterialTheme.typography.bodyMedium
            )

            Spaziatura()

            OutlinedTextField(
                value = nome,
                onValueChange = { nome = it },
                label = { Text("Prodotto") },
                modifier = Modifier.fillMaxWidth()
            )

            Spaziatura()

            OutlinedTextField(
                value = posizione,
                onValueChange = { posizione = it },
                label = { Text("Posizione") },
                modifier = Modifier.fillMaxWidth()
            )

            Spaziatura()

            var categoriaEspansa by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = categoriaEspansa,
                onExpandedChange = { categoriaEspansa = it }
            ) {
                OutlinedTextField(
                    value = "${categoriaSelezionata.emoji} ${categoriaSelezionata.label}",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Categoria") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoriaEspansa) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )
                androidx.compose.material3.ExposedDropdownMenu(
                    expanded = categoriaEspansa,
                    onDismissRequest = { categoriaEspansa = false }
                ) {
                    Categoria.entries.forEach { opzione ->
                        DropdownMenuItem(
                            text = { Text("${opzione.emoji} ${opzione.label}") },
                            onClick = {
                                categoriaSelezionata = opzione
                                categoriaEspansa = false
                            }
                        )
                    }
                }
            }

            Spaziatura()

            var stanzaEspansa by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = stanzaEspansa,
                onExpandedChange = { stanzaEspansa = it }
            ) {
                OutlinedTextField(
                    value = stanzaSelezionata.label,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Stanza") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = stanzaEspansa) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )
                androidx.compose.material3.ExposedDropdownMenu(
                    expanded = stanzaEspansa,
                    onDismissRequest = { stanzaEspansa = false }
                ) {
                    Stanza.entries.forEach { opzione ->
                        DropdownMenuItem(
                            text = { Text(opzione.label) },
                            onClick = {
                                stanzaSelezionata = opzione
                                stanzaEspansa = false
                            }
                        )
                    }
                }
            }

            Spaziatura()

            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                label = { Text("Note (opzionale)") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2,
                maxLines = 3
            )

            Spaziatura()

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onAnnulla,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Annulla")
                }
                Button(
                    onClick = {
                        viewModel.salvaProdotto(
                            modificato = ProdottoEstratto(
                                prodotto = nome,
                                posizione = posizione,
                                categoria = categoriaSelezionata.label,
                                stanza = stanzaSelezionata.label
                            ),
                            note = note,
                            onSalvato = onSalvato
                        )
                    },
                    enabled = nome.isNotBlank() && posizione.isNotBlank(),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Salva")
                }
            }
        }
    }
}

@Composable
private fun Spaziatura() {
    androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(16.dp))
}
