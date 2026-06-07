package net.asksakis.massdroidv2.tv.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.tv.material3.Button
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Surface
import androidx.tv.material3.Text

@Composable
fun TvSettingsScreen(viewModel: TvSettingsViewModel = hiltViewModel()) {
    val syncDelay by viewModel.syncDelayMs.collectAsStateWithLifecycle()

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize().padding(64.dp)) {
            Text("Settings", style = MaterialTheme.typography.headlineMedium)
            Spacer(Modifier.height(40.dp))

            Text("Audio sync delay", style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(8.dp))
            Text(
                "Adjust if this TV plays slightly after or before other speakers in a group. " +
                    "Negative makes it play earlier (compensates HDMI/AV receiver latency).",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(24.dp))

            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(onClick = { viewModel.nudge(-50) }) { Text("-50") }
                Button(onClick = { viewModel.nudge(-10) }) { Text("-10") }
                Spacer(Modifier.width(8.dp))
                Text(
                    "${if (syncDelay > 0) "+" else ""}$syncDelay ms",
                    style = MaterialTheme.typography.headlineSmall
                )
                Spacer(Modifier.width(8.dp))
                Button(onClick = { viewModel.nudge(10) }) { Text("+10") }
                Button(onClick = { viewModel.nudge(50) }) { Text("+50") }
                Spacer(Modifier.width(16.dp))
                Button(onClick = { viewModel.setSyncDelay(0) }) { Text("Reset") }
            }
        }
    }
}
