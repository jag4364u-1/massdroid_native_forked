package net.asksakis.massdroidv2.tv.ui

import android.net.Uri
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
 * Top-level TV destination switch:
 *  - Connected            -> navigable home / now-playing graph
 *  - saved server, not yet -> "connecting" (auto-retry, no forced re-login)
 *  - no saved server       -> onboarding/login
 */
@Composable
fun TvRoot(viewModel: TvRootViewModel = hiltViewModel()) {
    val connection by viewModel.connectionState.collectAsStateWithLifecycle()
    val hasSavedServer by viewModel.hasSavedServer.collectAsStateWithLifecycle()
    val changeServer by viewModel.changeServerRequested.collectAsStateWithLifecycle()
    val initialized by viewModel.initialized.collectAsStateWithLifecycle()

    when {
        connection is ConnectionState.Connected -> {
            val nav = rememberNavController()
            NavHost(navController = nav, startDestination = "home") {
                composable("home") {
                    TvHomeScreen(
                        onOpenPlayer = { playerId -> nav.navigate("nowplaying/$playerId") },
                        onOpenSettings = { nav.navigate("settings") },
                        onOpenArtist = { itemId, provider ->
                            nav.navigate("artist/${Uri.encode(itemId)}/${Uri.encode(provider)}")
                        },
                        onOpenBrowse = { nav.navigate("browse") }
                    )
                }
                composable("browse") {
                    TvBrowseScreen(
                        onOpenArtist = { itemId, provider ->
                            nav.navigate("artist/${Uri.encode(itemId)}/${Uri.encode(provider)}")
                        },
                        onOpenFolders = { nav.navigate("serverbrowse") }
                    )
                }
                composable("serverbrowse") { TvServerBrowseScreen() }
                composable(
                    route = "nowplaying/{playerId}",
                    arguments = listOf(navArgument("playerId") { type = NavType.StringType })
                ) {
                    TvNowPlayingScreen(onOpenQueue = { nav.navigate("queue") })
                }
                composable("queue") { TvQueueScreen() }
                composable(
                    route = "artist/{itemId}/{provider}",
                    arguments = listOf(
                        navArgument("itemId") { type = NavType.StringType },
                        navArgument("provider") { type = NavType.StringType }
                    )
                ) {
                    TvArtistScreen()
                }
                composable("settings") { TvSettingsScreen() }
            }
        }
        (!initialized || hasSavedServer) && !changeServer -> {
            TvConnectingScreen(server = viewModel.serverLabel(), onChangeServer = { viewModel.changeServer() })
        }
        else -> TvOnboardingScreen()
    }
}
