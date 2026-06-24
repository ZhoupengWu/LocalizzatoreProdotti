package com.localizzatore.prodotti.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.localizzatore.prodotti.ai.GeminiResult
import com.localizzatore.prodotti.ai.GeminiService
import com.localizzatore.prodotti.ai.SpeechRecognitionHelper
import com.localizzatore.prodotti.data.local.Prodotto
import com.localizzatore.prodotti.data.repository.ProdottoRepository
import com.localizzatore.prodotti.domain.model.ProdottoEstratto
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class InputUiState(
    val testo: String = "",
    val inAscolto: Boolean = false,
    val inElaborazione: Boolean = false,
    val errore: String? = null,
    val risultato: ProdottoEstratto? = null
)

/**
 * Gestisce sia la schermata di Input (testo/voce + chiamata a Gemini)
 * sia il salvataggio finale dopo la conferma in PreviewScreen.
 * Viene condiviso tra le due schermate tramite lo stesso NavBackStackEntry.
 */
@HiltViewModel
class InputViewModel @Inject constructor(
    private val geminiService: GeminiService,
    private val speechHelper: SpeechRecognitionHelper,
    private val repository: ProdottoRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(InputUiState())
    val uiState: StateFlow<InputUiState> = _uiState.asStateFlow()

    fun aggiornaTesto(testo: String) {
        _uiState.update { it.copy(testo = testo, errore = null) }
    }

    fun avviaAscolto() {
        _uiState.update { it.copy(errore = null) }
        speechHelper.avviaAscolto(
            onInizio = { _uiState.update { it.copy(inAscolto = true) } },
            onFine = { _uiState.update { it.copy(inAscolto = false) } },
            onRisultato = { testoRiconosciuto ->
                _uiState.update {
                    it.copy(testo = (it.testo + " " + testoRiconosciuto).trim())
                }
            },
            onErrore = { messaggio ->
                _uiState.update { it.copy(errore = messaggio, inAscolto = false) }
            }
        )
    }

    fun fermaAscolto() {
        speechHelper.fermaAscolto()
    }

    fun elaboraConGemini() {
        val testo = _uiState.value.testo.trim()
        if (testo.isBlank()) {
            _uiState.update {
                it.copy(errore = "Scrivi o detta prima una frase, es: \"Ho messo il passaporto nel cassetto della camera\".")
            }
            return
        }
        _uiState.update { it.copy(inElaborazione = true, errore = null) }
        viewModelScope.launch {
            when (val risultato = geminiService.estraiDati(testo)) {
                is GeminiResult.Successo -> _uiState.update {
                    it.copy(inElaborazione = false, risultato = risultato.dati)
                }
                is GeminiResult.Errore -> _uiState.update {
                    it.copy(inElaborazione = false, errore = risultato.messaggio)
                }
            }
        }
    }

    fun salvaProdotto(modificato: ProdottoEstratto, note: String, onSalvato: () -> Unit) {
        viewModelScope.launch {
            repository.salva(
                Prodotto(
                    nome = modificato.prodotto.trim(),
                    posizione = modificato.posizione.trim(),
                    categoria = modificato.categoria.trim().lowercase(),
                    stanza = modificato.stanza.trim().lowercase(),
                    note = note.trim()
                )
            )
            resetRisultato()
            onSalvato()
        }
    }

    fun resetRisultato() {
        _uiState.update { it.copy(risultato = null, testo = "") }
    }

    fun pulisciErrore() {
        _uiState.update { it.copy(errore = null) }
    }

    override fun onCleared() {
        super.onCleared()
        speechHelper.rilascia()
    }
}
