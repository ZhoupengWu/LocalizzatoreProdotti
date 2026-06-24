package com.localizzatore.prodotti.ai

import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.generationConfig
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.localizzatore.prodotti.BuildConfig
import com.localizzatore.prodotti.domain.model.ProdottoEstratto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

sealed class GeminiResult {
    data class Successo(val dati: ProdottoEstratto) : GeminiResult()
    data class Errore(val messaggio: String) : GeminiResult()
}

/**
 * Wrapper attorno al Google Generative AI SDK (Gemini 1.5 Flash, gratuito)
 * che trasforma una frase libera in italiano in dati strutturati.
 */
@Singleton
class GeminiService @Inject constructor() {

    private val istruzioni = """
        Sei un assistente per organizzare oggetti in casa.
        L'utente ti dice dove ha messo un oggetto. Estrai SOLO un oggetto JSON valido,
        senza testo aggiuntivo, senza markdown, senza backtick, in questo formato esatto:
        {"prodotto": "...", "posizione": "...", "categoria": "...", "stanza": "..."}

        Regole:
        - categoria deve essere una tra: documenti, elettronica, abbigliamento, cucina, igiene, giocattoli, attrezzi, altro
        - stanza deve essere una tra: cucina, camera, bagno, salotto, garage, ripostiglio, corridoio, studio, cantina, altro
        - se un'informazione non e' esplicita, deducila nel modo piu' sensato possibile
        - rispondi ESCLUSIVAMENTE con il JSON, nient'altro

        Esempio:
        Input: "Ho messo il passaporto nel cassetto della camera da letto"
        Output: {"prodotto": "passaporto", "posizione": "cassetto della camera da letto", "categoria": "documenti", "stanza": "camera"}
    """.trimIndent()

    private val model by lazy {
        GenerativeModel(
            modelName = "gemini-1.5-flash",
            apiKey = BuildConfig.GEMINI_API_KEY,
            generationConfig = generationConfig {
                temperature = 0.2f
            }
        )
    }

    suspend fun estraiDati(testoUtente: String): GeminiResult = withContext(Dispatchers.IO) {
        if (BuildConfig.GEMINI_API_KEY.isBlank()) {
            return@withContext GeminiResult.Errore(
                "API key Gemini non configurata. Aggiungila in local.properties come GEMINI_API_KEY=la_tua_chiave"
            )
        }
        try {
            val prompt = "$istruzioni\n\nInput: \"$testoUtente\"\nOutput:"
            val response = model.generateContent(prompt)
            val testoRisposta = response.text?.trim()
                ?: return@withContext GeminiResult.Errore("Risposta vuota da Gemini.")

            val jsonPulito = pulisciJson(testoRisposta)
            val dati = Gson().fromJson(jsonPulito, ProdottoEstratto::class.java)

            if (dati == null || dati.prodotto.isBlank()) {
                GeminiResult.Errore("Non sono riuscito a capire l'oggetto descritto. Riprova con più dettagli.")
            } else {
                GeminiResult.Successo(dati)
            }
        } catch (e: JsonSyntaxException) {
            GeminiResult.Errore("Risposta AI non valida (JSON malformato), riprova.")
        } catch (e: Exception) {
            GeminiResult.Errore("Errore di rete o AI: ${e.localizedMessage ?: "sconosciuto"}")
        }
    }

    /** Rimuove eventuali fence ```json...``` e isola l'oggetto { ... } dalla risposta del modello. */
    private fun pulisciJson(testo: String): String {
        var t = testo.trim()
        if (t.startsWith("```")) {
            t = t.substringAfter("\n").substringBeforeLast("```").trim()
        }
        val start = t.indexOf('{')
        val end = t.lastIndexOf('}')
        return if (start >= 0 && end > start) t.substring(start, end + 1) else t
    }
}
