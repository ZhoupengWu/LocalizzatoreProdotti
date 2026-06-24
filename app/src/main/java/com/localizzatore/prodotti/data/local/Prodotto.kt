package com.localizzatore.prodotti.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity Room che rappresenta un prodotto salvato in casa.
 */
@Entity(tableName = "prodotti")
data class Prodotto(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nome: String,
    val posizione: String,
    val categoria: String,
    val stanza: String,
    val timestamp: Long = System.currentTimeMillis(),
    val note: String = ""
)
