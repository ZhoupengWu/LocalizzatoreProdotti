package com.localizzatore.prodotti.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/**
 * Conteggio dei prodotti raggruppati per stanza, usato dalla MappaScreen.
 */
data class ConteggioStanza(
    val stanza: String,
    val count: Int
)

@Dao
interface ProdottoDao {

    @Query("SELECT * FROM prodotti ORDER BY timestamp DESC")
    fun getTutti(): Flow<List<Prodotto>>

    @Query("SELECT * FROM prodotti WHERE stanza = :stanza ORDER BY timestamp DESC")
    fun getPerStanza(stanza: String): Flow<List<Prodotto>>

    @Query(
        """
        SELECT * FROM prodotti
        WHERE nome LIKE '%' || :query || '%'
           OR posizione LIKE '%' || :query || '%'
        ORDER BY timestamp DESC
        """
    )
    suspend fun cerca(query: String): List<Prodotto>

    @Query("SELECT stanza, COUNT(*) as count FROM prodotti GROUP BY stanza")
    fun getConteggioPerStanza(): Flow<List<ConteggioStanza>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun inserisci(prodotto: Prodotto): Long

    @Update
    suspend fun aggiorna(prodotto: Prodotto)

    @Delete
    suspend fun elimina(prodotto: Prodotto)

    @Query("SELECT * FROM prodotti WHERE id = :id")
    suspend fun getById(id: Int): Prodotto?
}
