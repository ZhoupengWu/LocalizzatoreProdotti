package com.localizzatore.prodotti.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.TableRows
import androidx.compose.material.icons.outlined.GridView
import androidx.compose.material.icons.outlined.Mic
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.TableRows
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.localizzatore.prodotti.ui.screens.InputScreen
import com.localizzatore.prodotti.ui.screens.MappaScreen
import com.localizzatore.prodotti.ui.screens.PreviewScreen
import com.localizzatore.prodotti.ui.screens.RicercaScreen
import com.localizzatore.prodotti.ui.screens.TabellaScreen
import com.localizzatore.prodotti.viewmodel.InputViewModel

/** Definisce tutte le rotte di navigazione dell'app, con titolo e icone per la barra inferiore. */
sealed class Schermata(
    val route: String,
    val titolo: String,
    val iconaPiena: ImageVector? = null,
    val iconaContorno: ImageVector? = null
) {
    data object Input : Schermata(
        "input", "Aggiungi",
        Icons.Filled.Mic, Icons.Outlined.Mic
    )
    data object Preview : Schermata("preview", "Conferma")
    data object Tabella : Schermata(
        "tabella", "Tabella",
        Icons.Filled.TableRows, Icons.Outlined.TableRows
    )
    data object Mappa : Schermata(
        "mappa", "Mappa",
        Icons.Filled.GridView, Icons.Outlined.GridView
    )
    data object Ricerca : Schermata(
        "ricerca", "Cerca",
        Icons.Filled.Search, Icons.Outlined.Search
    )
}

/** Le schermate che hanno una voce visibile nella barra di navigazione inferiore. */
private val schermateBarra = listOf(Schermata.Input, Schermata.Tabella, Schermata.Mappa, Schermata.Ricerca)

/** Naviga a una delle schermate principali della barra, evitando di accumulare schermate duplicate nello stack. */
private fun NavHostController.navigaSingolo(route: String) {
    navigate(route) {
        popUpTo(graph.findStartDestination().id) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
}

@Composable
fun LocalizzatoreApp() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val routeAttuale = backStackEntry?.destination

    Scaffold(
        bottomBar = {
            // La barra inferiore è nascosta durante la schermata di conferma/preview,
            // per concentrare l'attenzione sull'azione di salvataggio.
            val mostraBarra = routeAttuale?.route != Schermata.Preview.route
            if (mostraBarra) {
                NavigationBar {
                    schermateBarra.forEach { schermata ->
                        val selezionata = routeAttuale?.route == schermata.route
                        NavigationBarItem(
                            selected = selezionata,
                            onClick = { navController.navigaSingolo(schermata.route) },
                            icon = {
                                Icon(
                                    imageVector = if (selezionata) schermata.iconaPiena!! else schermata.iconaContorno!!,
                                    contentDescription = schermata.titolo
                                )
                            },
                            label = { Text(schermata.titolo) }
                        )
                    }
                }
            }
        }
    ) { paddingInterno ->
        NavHost(
            navController = navController,
            startDestination = Schermata.Input.route,
            modifier = Modifier.padding(paddingInterno)
        ) {
            composable(Schermata.Input.route) { entry ->
                val inputViewModel: InputViewModel = hiltViewModel(entry)
                InputScreen(
                    viewModel = inputViewModel,
                    onContinua = { navController.navigate(Schermata.Preview.route) }
                )
            }
            composable(Schermata.Preview.route) {
                // Recupera lo stesso InputViewModel della schermata Input (stesso back stack entry),
                // così il risultato di Gemini resta disponibile senza dover essere passato manualmente.
                val inputEntry = navController.getBackStackEntry(Schermata.Input.route)
                val inputViewModel: InputViewModel = hiltViewModel(inputEntry)
                PreviewScreen(
                    viewModel = inputViewModel,
                    onSalvato = {
                        navController.popBackStack(Schermata.Input.route, inclusive = false)
                    },
                    onAnnulla = {
                        navController.popBackStack()
                    }
                )
            }
            composable(Schermata.Tabella.route) {
                TabellaScreen(viewModel = hiltViewModel())
            }
            composable(Schermata.Mappa.route) {
                MappaScreen(viewModel = hiltViewModel())
            }
            composable(Schermata.Ricerca.route) {
                RicercaScreen(viewModel = hiltViewModel())
            }
        }
    }
}
