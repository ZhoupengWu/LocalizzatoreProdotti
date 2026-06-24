package com.localizzatore.prodotti

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.localizzatore.prodotti.ui.navigation.LocalizzatoreApp
import com.localizzatore.prodotti.ui.theme.LocalizzatoreProdottiTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LocalizzatoreProdottiTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    LocalizzatoreApp()
                }
            }
        }
    }
}
