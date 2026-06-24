package com.localizzatore.prodotti.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.localizzatore.prodotti.data.local.Prodotto
import com.localizzatore.prodotti.data.repository.ProdottoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TabellaViewModel @Inject constructor(
    private val repository: ProdottoRepository
) : ViewModel() {

    val prodotti: StateFlow<List<Prodotto>> = repository.getTutti()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun elimina(prodotto: Prodotto) {
        viewModelScope.launch { repository.elimina(prodotto) }
    }

    fun aggiorna(prodotto: Prodotto) {
        viewModelScope.launch { repository.aggiorna(prodotto) }
    }
}
