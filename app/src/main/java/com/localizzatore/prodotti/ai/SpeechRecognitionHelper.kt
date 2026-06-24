package com.localizzatore.prodotti.ai

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.Locale
import javax.inject.Inject

/**
 * Wrapper sopra Android SpeechRecognizer per dettare a voce il testo
 * da inviare poi a Gemini.
 */
class SpeechRecognitionHelper @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private var recognizer: SpeechRecognizer? = null

    fun isDisponibile(): Boolean = SpeechRecognizer.isRecognitionAvailable(context)

    fun avviaAscolto(
        onRisultato: (String) -> Unit,
        onErrore: (String) -> Unit,
        onInizio: () -> Unit = {},
        onFine: () -> Unit = {}
    ) {
        if (!isDisponibile()) {
            onErrore("Riconoscimento vocale non disponibile su questo dispositivo.")
            return
        }

        recognizer?.destroy()
        recognizer = SpeechRecognizer.createSpeechRecognizer(context)

        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.ITALY.toString())
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, false)
        }

        recognizer?.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) { onInizio() }
            override fun onBeginningOfSpeech() {}
            override fun onRmsChanged(rmsdB: Float) {}
            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onEndOfSpeech() { onFine() }

            override fun onError(error: Int) {
                onFine()
                val messaggio = when (error) {
                    SpeechRecognizer.ERROR_NO_MATCH -> "Non ho capito, riprova."
                    SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "Nessun audio rilevato."
                    SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Permesso microfono negato."
                    else -> "Errore riconoscimento vocale ($error)."
                }
                onErrore(messaggio)
            }

            override fun onResults(results: Bundle?) {
                onFine()
                val testo = results
                    ?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    ?.firstOrNull()
                if (testo != null) onRisultato(testo) else onErrore("Nessun risultato riconosciuto.")
            }

            override fun onPartialResults(partialResults: Bundle?) {}
            override fun onEvent(eventType: Int, params: Bundle?) {}
        })

        recognizer?.startListening(intent)
    }

    fun fermaAscolto() {
        recognizer?.stopListening()
    }

    fun rilascia() {
        recognizer?.destroy()
        recognizer = null
    }
}
