package com.localizzatore.prodotti.data.repository

import com.localizzatore.prodotti.data.local.ConteggioStanza
import com.localizzatore.prodotti.data.local.Prodotto
import com.localizzatore.prodotti.data.local.ProdottoDao
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Unico punto di accesso ai dati dei prodotti: nasconde Room al resto dell'app
 * (pattern Repository, richiesto dall'architettura MVVM).
 */
@Singleton
class ProdottoRepository @Inject constructor(
    private val dao: ProdottoDao
) {
    fun getTutti(): Flow<List<Prodotto>> = dao.getTutti()

    fun getPerStanza(stanza: String): Flow<List<Prodotto>> = dao.getPerStanza(stanza)

    fun getConteggioPerStanza(): Flow<List<ConteggioStanza>> = dao.getConteggioPerStanza()

    suspend fun cerca(query: String): List<Prodotto> = dao.cerca(query)

    suspend fun salva(prodotto: Prodotto): Long = dao.inserisci(prodotto)

    suspend fun aggiorna(prodotto: Prodotto) = dao.aggiorna(prodotto)

    suspend fun elimina(prodotto: Prodotto) = dao.elimina(prodotto)

    suspend fun getById(id: Int): Prodotto? = dao.getById(id)
}
