package net.asksakis.massdroidv2.tv.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Button
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Surface
import androidx.tv.material3.Text

/**
 * Shown while auto-connecting to the saved server. Keeps the user here (with a
 * retry running in the VM) on transient failures instead of forcing re-login;
 * a button lets them switch servers if needed.
 */
@Composable
fun TvConnectingScreen(server: String, onChangeServer: () -> Unit) {
    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize().padding(64.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Connecting to Music Assistant", style = MaterialTheme.typography.headlineMedium)
            if (server.isNotBlank()) {
                Spacer(Modifier.height(8.dp))
                Text(
                    server,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(Modifier.height(32.dp))
            Button(onClick = onChangeServer) { Text("Change server") }
        }
    }
}
