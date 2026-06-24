package com.localizzatore.prodotti.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.collectAsState
import com.localizzatore.prodotti.ui.components.MicrofoneButton
import com.localizzatore.prodotti.viewmodel.InputViewModel

/**
 * Schermata principale: l'utente scrive o detta dove ha messo un oggetto,
 * poi chiede all'AI di estrarre i dati strutturati prima di passare alla conferma.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InputScreen(
    viewModel: InputViewModel,
    onContinua: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    val richiediPermessoAudio = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { concesso ->
        if (concesso) viewModel.avviaAscolto()
    }

    // Quando Gemini restituisce un risultato valido, passiamo automaticamente alla conferma.
    LaunchedEffect(uiState.risultato) {
        if (uiState.risultato != null) {
            onContinua()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Dove l'ho messo?") })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Racconta cosa hai riposto e dove, a voce o per iscritto.",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )

            Spacer()

            OutlinedTextField(
                value = uiState.testo,
                onValueChange = viewModel::aggiornaTesto,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Es: Ho messo il passaporto nel cassetto della camera") },
                minLines = 3,
                maxLines = 5
            )

            Spacer()

            MicrofoneButton(
                inAscolto = uiState.inAscolto,
                onClick = {
                    if (uiState.inAscolto) {
                        viewModel.fermaAscolto()
                    } else {
                        val permessoGia = context.checkSelfPermission(Manifest.permission.RECORD_AUDIO) ==
                            PackageManager.PERMISSION_GRANTED
                        if (permessoGia) {
                            viewModel.avviaAscolto()
                        } else {
                            richiediPermessoAudio.launch(Manifest.permission.RECORD_AUDIO)
                        }
                    }
                }
            )

            Spacer()

            if (uiState.errore != null) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = uiState.errore ?: "",
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.padding(16.dp)
                    )
                }
                Spacer()
            }

            Button(
                onClick = viewModel::elaboraConGemini,
                enabled = !uiState.inElaborazione && uiState.testo.isNotBlank(),
                modifier = Modifier.fillMaxWidth()
            ) {
                if (uiState.inElaborazione) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                } else {
                    Icon(Icons.Filled.AutoAwesome, contentDescription = null)
                    Text(" Estrai con AI", modifier = Modifier.padding(start = 4.dp))
                }
            }
        }
    }
}

@Composable
private fun Spacer() {
    androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(20.dp))
}
