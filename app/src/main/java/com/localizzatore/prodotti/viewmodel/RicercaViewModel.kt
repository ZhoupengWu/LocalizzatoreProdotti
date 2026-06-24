package com.localizzatore.prodotti.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.localizzatore.prodotti.data.local.Prodotto
import com.localizzatore.prodotti.data.repository.ProdottoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RicercaUiState(
    val query: String = "",
    val risultati: List<Prodotto> = emptyList(),
    val cercato: Boolean = false
)

@HiltViewModel
class RicercaViewModel @Inject constructor(
    private val repository: ProdottoRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RicercaUiState())
    val uiState: StateFlow<RicercaUiState> = _uiState.asStateFlow()

    fun aggiornaQuery(query: String) {
        _uiState.update { it.copy(query = query) }
        if (query.isBlank()) {
            _uiState.update { it.copy(risultati = emptyList(), cercato = false) }
        }
    }

    fun cerca() {
        val query = _uiState.value.query.trim()
        if (query.isBlank()) return
        viewModelScope.launch {
            val risultati = repository.cerca(query)
            _uiState.update { it.copy(risultati = risultati, cercato = true) }
        }
    }
}
