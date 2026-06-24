package com.localizzatore.prodotti.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.localizzatore.prodotti.data.local.ConteggioStanza
import com.localizzatore.prodotti.data.local.Prodotto
import com.localizzatore.prodotti.data.repository.ProdottoRepository
import com.localizzatore.prodotti.domain.model.Stanza
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class MappaViewModel @Inject constructor(
    repository: ProdottoRepository
) : ViewModel() {

    private val conteggiFlow: StateFlow<List<ConteggioStanza>> = repository.getConteggioPerStanza()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    /** Mappa stanza (lowercase) -> numero di prodotti contenuti. */
    val conteggioPerStanza: StateFlow<Map<String, Int>> = conteggiFlow
        .map { lista -> lista.associate { it.stanza to it.count } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    private val _stanzaSelezionata = MutableStateFlow<String?>(null)
    val stanzaSelezionata: StateFlow<String?> = _stanzaSelezionata.asStateFlow()

    val prodottiStanzaSelezionata: StateFlow<List<Prodotto>> = _stanzaSelezionata
        .flatMapLatest { stanza ->
            if (stanza == null) flowOf(emptyList()) else repository.getPerStanza(stanza)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    /** Le stanze predefinite mostrate nella griglia, con etichetta leggibile (es. "Camera"). */
    val stanzeDisponibili: List<String> = Stanza.entries.map { it.label }

    fun selezionaStanza(stanzaLowercase: String?) {
        _stanzaSelezionata.value = stanzaLowercase
    }
}
