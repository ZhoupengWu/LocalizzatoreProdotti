package com.localizzatore.prodotti.domain.model

/**
 * Risultato grezzo estratto da Gemini a partire dal testo dell'utente.
 * I campi corrispondono 1:1 alle chiavi del JSON richiesto al modello.
 */
data class ProdottoEstratto(
    val prodotto: String,
    val posizione: String,
    val categoria: String,
    val stanza: String
)

enum class Categoria(val label: String, val emoji: String) {
    DOCUMENTI("Documenti", "📄"),
    ELETTRONICA("Elettronica", "🔌"),
    ABBIGLIAMENTO("Abbigliamento", "👕"),
    CUCINA("Cucina", "🍳"),
    IGIENE("Igiene", "🧴"),
    GIOCATTOLI("Giocattoli", "🧸"),
    ATTREZZI("Attrezzi", "🔧"),
    ALTRO("Altro", "📦");

    companion object {
        fun fromString(value: String): Categoria =
            entries.find {
                it.name.equals(value, ignoreCase = true) || it.label.equals(value, ignoreCase = true)
            } ?: ALTRO
    }
}

enum class Stanza(val label: String) {
    CUCINA("Cucina"),
    CAMERA("Camera"),
    BAGNO("Bagno"),
    SALOTTO("Salotto"),
    GARAGE("Garage"),
    RIPOSTIGLIO("Ripostiglio"),
    CORRIDOIO("Corridoio"),
    STUDIO("Studio"),
    CANTINA("Cantina"),
    ALTRO("Altro");

    companion object {
        fun fromString(value: String): Stanza =
            entries.find {
                it.name.equals(value, ignoreCase = true) || it.label.equals(value, ignoreCase = true)
            } ?: ALTRO
    }
}
