# 📦 Localizzatore Prodotti

App Android (Kotlin + Jetpack Compose + Room + Hilt + Gemini AI) per ricordare dove hai messo gli oggetti in casa, tramite input vocale o testuale.

## Setup

1. Apri il progetto in **Android Studio** (Hedgehog o più recente).
2. Copia `local.properties.example` in `local.properties`.
3. Ottieni una API key gratuita su https://aistudio.google.com/app/apikey e inseriscila:
   ```
   GEMINI_API_KEY=la_tua_chiave_qui
   ```
4. Fai "Sync Gradle" e premi Run su un device/emulatore con **API 26+**.

## Funzionalità

- 🎤 Input vocale (Android Speech Recognizer) o testuale
- 🧠 Estrazione automatica di prodotto / posizione / categoria / stanza tramite **Gemini 1.5 Flash** (gratuito)
- 👁️ Schermata di conferma/modifica prima del salvataggio
- 💾 Database locale **Room (SQLite)**
- 📋 Vista a **tabella** con swipe-to-delete
- 🏠 Vista a **mappa semplificata** della casa, a stanze, con conteggio oggetti
- 🔍 Ricerca rapida: "dove ho messo il passaporto?"
- 🌙 Dark mode automatica (Material 3 dynamic color)

## Architettura

MVVM + Repository Pattern, dependency injection con **Hilt**, UI 100% **Jetpack Compose**, persistenza con **Room**, chiamate asincrone con **Coroutines/Flow**.

## Note

- Nessuna mappa GPS/Google Maps: la "mappa" è una griglia di stanze predefinite (CustomView/Compose), non geografica.
- Se la API key non è configurata, l'app mostra un errore chiaro invece di crashare.
