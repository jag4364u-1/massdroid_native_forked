package net.asksakis.massdroidv2.tv.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import net.asksakis.massdroidv2.data.websocket.ConnectionState

/**
 * Top-level TV destination switch: onboarding until the shared WS client is
 * Connected, then the navigable home / now-playing graph.
 */
@Composable
fun TvRoot(viewModel: TvRootViewModel = hiltViewModel()) {
    val connection by viewModel.connectionState.collectAsStateWithLifecycle()
    if (connection is ConnectionState.Connected) {
        val nav = rememberNavController()
        NavHost(navController = nav, startDestination = "home") {
            composable("home") {
                TvHomeScreen(onOpenPlayer = { playerId -> nav.navigate("nowplaying/$playerId") })
            }
            composable(
                route = "nowplaying/{playerId}",
                arguments = listOf(navArgument("playerId") { type = NavType.StringType })
            ) {
                TvNowPlayingScreen()
            }
        }
    } else {
        TvOnboardingScreen()
    }
}
