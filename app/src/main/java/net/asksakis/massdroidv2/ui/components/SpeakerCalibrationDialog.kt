package net.asksakis.massdroidv2.ui.components

import android.Manifest
import android.content.Context
import android.media.AudioManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Speaker
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import net.asksakis.massdroidv2.data.sendspin.AcousticCalibrationCoordinator
import net.asksakis.massdroidv2.data.sendspin.CalibrationOutcome
import net.asksakis.massdroidv2.ui.permissions.AppPermissions

/**
 * Built-in SPEAKER acoustic self-calibration dialog. Single short pass: chirp
 * out the phone speaker, hear it back on the mic, and store the under-reported
 * output-latency shortfall (see [AcousticCalibrationCoordinator.runSpeakerCalibration]).
 *
 * Needed because some HALs (e.g. Xiaomi/MIUI) under-report
 * `AudioManager.getOutputLatency` for the built-in speaker, leaving grouped
 * playback tens of ms late with no software way to detect it. The chirp
 * measures the true acoustic delay.
 *
 * Media volume is forced to ~80% for the chirp and restored afterwards so the
 * tone is reliably picked up by the mic without depending on the user's level.
 */
@Composable
fun SpeakerCalibrationDialog(
    coordinator: AcousticCalibrationCoordinator,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    // No manual "pause first" gate: runSpeakerCalibration freezes the local
    // Sendspin output for the chirp and unfreezes after (click-free), so the
    // music is silenced automatically during the measurement and resumes itself.
    var phase by remember { mutableStateOf(SpeakerCalPhase.INSTRUCTIONS) }

    var toneIndex by remember { mutableIntStateOf(0) }
    var totalTones by remember { mutableIntStateOf(6) }

    var resultText by remember { mutableStateOf("") }
    var resultQuality by remember { mutableStateOf("") }
    var failureReason by remember { mutableStateOf("") }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        phase = if (granted) SpeakerCalPhase.MEASURING else SpeakerCalPhase.PERMISSION_DENIED
    }

    fun startMeasurement() {
        val missing = AppPermissions.missing(context, AppPermissions.acousticCalibrationRequired())
        if (missing.isNotEmpty()) {
            phase = SpeakerCalPhase.REQUESTING_PERMISSION
            permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
            return
        }
        phase = SpeakerCalPhase.MEASURING
    }

    if (phase == SpeakerCalPhase.MEASURING) {
        LaunchedEffect(Unit) {
            // Force a usable media level for the chirp, restore after so we do
            // not leave the user's volume changed.
            val am = context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager
            val savedVolume = am?.getStreamVolume(AudioManager.STREAM_MUSIC)
            am?.let {
                // ~75%: a weak chirp crosses the onset-detection threshold late,
                // over-measuring the round trip (and dropping SNR -> marginal
                // quality). A louder tone is detected promptly and cleanly.
                val maxVol = it.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
                val target = (maxVol * 4 / 5).coerceAtLeast(1)
                runCatching { it.setStreamVolume(AudioManager.STREAM_MUSIC, target, 0) }
            }
            coordinator.calibrator.onProgress = { idx, total -> toneIndex = idx; totalTones = total }
            val outcome = coordinator.runSpeakerCalibration { idx, total ->
                toneIndex = idx
                totalTones = total
            }
            coordinator.calibrator.onProgress = null
            if (savedVolume != null) {
                runCatching { am.setStreamVolume(AudioManager.STREAM_MUSIC, savedVolume, 0) }
            }
            when (outcome) {
                is CalibrationOutcome.Success -> {
                    resultText = "Speaker delay: ${outcome.compensationUs / 1000}ms"
                    resultQuality = outcome.quality
                    phase = SpeakerCalPhase.RESULT
                }
                is CalibrationOutcome.Failure -> {
                    failureReason = outcome.reason
                    phase = SpeakerCalPhase.ERROR
                }
            }
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(Icons.Default.Speaker, contentDescription = null, modifier = Modifier.size(24.dp))
                Text("Speaker Calibration")
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                when (phase) {
                    SpeakerCalPhase.INSTRUCTIONS -> {
                        Text("Measure this phone's speaker output delay for tighter group sync.")
                        Text(
                            "Keep the room quiet for a moment. Volume is set automatically; a short tone plays from the speaker.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    SpeakerCalPhase.REQUESTING_PERMISSION, SpeakerCalPhase.PERMISSION_DENIED -> {
                        Text(
                            if (phase == SpeakerCalPhase.PERMISSION_DENIED)
                                "Microphone permission is required for calibration."
                            else "Requesting microphone access..."
                        )
                        Text(
                            "The microphone is used only to measure speaker delay. Audio is processed locally and not stored.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    SpeakerCalPhase.MEASURING -> {
                        Text("Measuring speaker delay")
                        Spacer(Modifier.height(4.dp))
                        LinearProgressIndicator(
                            progress = { toneIndex.toFloat() / totalTones.coerceAtLeast(1) },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Text(
                            "Playing tone $toneIndex/$totalTones",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    SpeakerCalPhase.RESULT -> {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.Check, null, tint = MaterialTheme.colorScheme.primary)
                            Text(resultText)
                        }
                        Text(
                            "Quality: $resultQuality",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    SpeakerCalPhase.ERROR -> {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.Error, null, tint = MaterialTheme.colorScheme.error)
                            Text(failureReason)
                        }
                        Text(
                            "Keep the room quiet and try again.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        },
        confirmButton = {
            when (phase) {
                SpeakerCalPhase.INSTRUCTIONS -> {
                    MdTextButton(onClick = { startMeasurement() }) { Text("Start") }
                }
                SpeakerCalPhase.RESULT -> {
                    MdTextButton(onClick = onDismiss) { Text("Done") }
                }
                SpeakerCalPhase.ERROR -> {
                    MdTextButton(onClick = { phase = SpeakerCalPhase.INSTRUCTIONS }) { Text("Retry") }
                }
                SpeakerCalPhase.PERMISSION_DENIED -> {
                    MdTextButton(onClick = {
                        permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                    }) { Text("Grant Permission") }
                }
                else -> {}
            }
        },
        dismissButton = {
            if (phase != SpeakerCalPhase.MEASURING) {
                MdTextButton(onClick = onDismiss) { Text("Cancel") }
            }
        }
    )
}

private enum class SpeakerCalPhase {
    INSTRUCTIONS,
    REQUESTING_PERMISSION,
    PERMISSION_DENIED,
    MEASURING,
    RESULT,
    ERROR
}
