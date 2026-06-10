package net.asksakis.massdroidv2.tv.ui

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeDown
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.Speaker
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.tv.material3.Border
import androidx.tv.material3.Card
import androidx.tv.material3.CardDefaults
import androidx.tv.material3.Icon
import androidx.tv.material3.IconButton
import androidx.tv.material3.LocalContentColor
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Surface
import androidx.tv.material3.Text
import coil.compose.AsyncImage
import kotlinx.coroutines.delay
import net.asksakis.massdroidv2.domain.model.PlaybackState
import net.asksakis.massdroidv2.domain.model.Player

/** Idle time before an expanded mini player folds back into the corner pill. */
private const val AUTO_COLLAPSE_MS = 8_000L

private const val EXPAND_ANIM_MS = 500
private const val FOCUS_ATTACH_TRIES = 10
private const val FOCUS_ATTACH_RETRY_MS = 50L

private const val HINT_PREFS = "tv_hints"
private const val HINT_KEY_MINI_PLAYER = "mini_player_back_hint_shown"
private const val HINT_KEY_OPEN_PLAYER = "mini_player_open_hint_shown"
private const val HINT_VISIBLE_MS = 12_000L

private val PILL_ART = 44.dp
private val EXPANDED_ART = 64.dp

/**
 * Floating mini player, bottom-right: collapsed it is a small artwork pill (with a
 * playing glyph); OK expands it into track info + transport + the player-switch
 * dialog entry point; it folds back on its own after [AUTO_COLLAPSE_MS] of no
 * D-pad activity, returning focus to the pill so navigation never gets lost.
 */
@Composable
fun TvMiniPlayer(
    onOpenPlayer: (String) -> Unit,
    modifier: Modifier = Modifier,
    expandSignal: kotlinx.coroutines.flow.SharedFlow<Unit>? = null,
    entryFocus: FocusRequester? = null,
    onExitToContent: (() -> Unit)? = null,
    onActiveChange: ((Boolean) -> Unit)? = null,
    viewModel: TvMiniPlayerViewModel = hiltViewModel()
) {
    val players by viewModel.players.collectAsStateWithLifecycle()
    val selected by viewModel.selectedPlayer.collectAsStateWithLifecycle()
    val localPlayerId by viewModel.localPlayerId.collectAsStateWithLifecycle()
    if (players.isEmpty()) return

    var expanded by remember { mutableStateOf(false) }
    var showPicker by remember { mutableStateOf(false) }
    // Bumped on every focus move/press inside the component; restarts the idle timer.
    var interactionTick by remember { mutableIntStateOf(0) }
    var hasFocusInside by remember { mutableStateOf(false) }
    // Set by the long-press-BACK shortcut: grab focus on expand even from elsewhere.
    var forceFocusOnExpand by remember { mutableStateOf(false) }
    val internalPillFocus = remember { FocusRequester() }
    // The pill doubles as the "last row" focus target the root jumps to on DPAD-DOWN.
    val pillFocus = entryFocus ?: internalPillFocus
    val expandedFocus = remember { FocusRequester() }

    LaunchedEffect(expanded, hasFocusInside) { onActiveChange?.invoke(expanded || hasFocusInside) }
    DisposableEffect(Unit) { onDispose { onActiveChange?.invoke(false) } }

    // First-launch hint: once per install, a small balloon above the pill points at the
    // long-press-BACK shortcut; it goes away on its own or as soon as the player is used.
    val context = androidx.compose.ui.platform.LocalContext.current
    var showHint by remember { mutableStateOf(false) }
    // Follow-up hint, once per install: after the shortcut is first used, point at OK-to-open.
    var showOpenHint by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        val prefs = context.getSharedPreferences(HINT_PREFS, android.content.Context.MODE_PRIVATE)
        if (!prefs.getBoolean(HINT_KEY_MINI_PLAYER, false)) {
            prefs.edit().putBoolean(HINT_KEY_MINI_PLAYER, true).apply()
            showHint = true
            delay(HINT_VISIBLE_MS)
            showHint = false
        }
    }

    if (expandSignal != null) {
        LaunchedEffect(expandSignal) {
            expandSignal.collect {
                interactionTick++
                forceFocusOnExpand = true
                val prefs = context.getSharedPreferences(HINT_PREFS, android.content.Context.MODE_PRIVATE)
                if (!prefs.getBoolean(HINT_KEY_OPEN_PLAYER, false)) {
                    prefs.edit().putBoolean(HINT_KEY_OPEN_PLAYER, true).apply()
                    showOpenHint = true
                }
                if (expanded) expandedFocus.requestFocusRetrying() else expanded = true
            }
        }
    }
    LaunchedEffect(expanded, interactionTick, showPicker) {
        if (!expanded || showPicker) return@LaunchedEffect
        delay(AUTO_COLLAPSE_MS)
        expanded = false
    }
    // Hand focus over on every state flip so the D-pad never lands in a removed node. The
    // requesters attach a frame or two later inside AnimatedContent, hence the retry.
    LaunchedEffect(expanded) {
        if (expanded && (hasFocusInside || forceFocusOnExpand)) {
            expandedFocus.requestFocusRetrying()
        } else if (!expanded && hasFocusInside) {
            pillFocus.requestFocusRetrying()
        }
        forceFocusOnExpand = false
    }

    val focusManager = androidx.compose.ui.platform.LocalFocusManager.current
    // Leaving the island: restore the content's saved focus when the host provides it
    // (lands on the exact card the user came from), else fall back to a spatial move.
    val exitToContent: () -> Unit = onExitToContent ?: {
        if (!focusManager.moveFocus(FocusDirection.Up)) {
            focusManager.clearFocus(force = true)
            focusManager.moveFocus(FocusDirection.Up)
        }
    }
    // BACK while the cursor is on the mini player = leave the player (collapse + focus back to
    // the content) instead of bubbling to the screen's back handling (home would prompt to exit).
    androidx.activity.compose.BackHandler(enabled = expanded || hasFocusInside) {
        if (hasFocusInside) exitToContent()
        expanded = false
    }
    val touch = Modifier.onFocusChanged { if (it.isFocused) interactionTick++ }
    Column(
        horizontalAlignment = Alignment.End,
        modifier = modifier
            .onFocusChanged { hasFocusInside = it.hasFocus }
            // The floating island sits over the content, where the native 2D focus search can
            // fail to find a way out; force the exit so UP (and LEFT while collapsed) always
            // returns the cursor to the content.
            .onPreviewKeyEvent { event ->
                if (event.type != KeyEventType.KeyDown) return@onPreviewKeyEvent false
                val exits = event.key == Key.DirectionUp ||
                    (event.key == Key.DirectionLeft && !expanded)
                if (!exits) return@onPreviewKeyEvent false
                exitToContent()
                true
            }
    ) {
        if (showHint && !expanded) {
            HintBalloon("Hold BACK to open the mini player")
            Spacer(Modifier.height(8.dp))
        }
        if (showOpenHint && expanded) {
            HintBalloon("Press OK to open the full player")
            Spacer(Modifier.height(8.dp))
        }
        // Genuine expand/collapse: the container's size animates (one-shot tween, fine per
        // the project animation rules) and the content is revealed by the growing clip, no
        // crossfade. Bottom/end anchoring comes from the parent alignment, so it grows up-left.
        Box(
            modifier = Modifier.animateContentSize(
                animationSpec = tween(EXPAND_ANIM_MS, easing = FastOutSlowInEasing)
            )
        ) {
            val isExpanded = expanded
            if (isExpanded) {
                ExpandedMiniPlayer(
                    player = selected,
                    bodyFocus = expandedFocus,
                    touch = touch,
                    onOpenPlayer = {
                        showOpenHint = false
                        selected?.let { onOpenPlayer(it.playerId) }
                    },
                    onPlayPause = viewModel::playPause,
                    onNext = viewModel::next,
                    onVolumeDown = viewModel::volumeDown,
                    onVolumeUp = viewModel::volumeUp,
                    onSwitchPlayer = { showPicker = true }
                )
            } else {
                CollapsedPill(
                    player = selected,
                    modifier = touch.focusRequester(pillFocus),
                    onClick = { expanded = true }
                )
            }
        }
    }
    LaunchedEffect(expanded) { if (expanded) showHint = false }
    LaunchedEffect(showOpenHint) {
        if (showOpenHint) {
            delay(HINT_VISIBLE_MS)
            showOpenHint = false
        }
    }

    if (showPicker) {
        TvPlayerPickerDialog(
            players = players,
            selectedPlayerId = selected?.playerId,
            localPlayerId = localPlayerId,
            onSelect = { playerId ->
                viewModel.selectPlayer(playerId)
                showPicker = false
                interactionTick++
            },
            onDismiss = { showPicker = false }
        )
    }
}

@Composable
private fun CollapsedPill(
    player: Player?,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = androidx.tv.material3.ClickableSurfaceDefaults.shape(RoundedCornerShape(14.dp)),
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(8.dp).widthIn(max = 300.dp)
        ) {
            MiniArtwork(player, Modifier.size(PILL_ART))
            Spacer(Modifier.width(10.dp))
            Column(modifier = Modifier.padding(end = 6.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (player?.state == PlaybackState.PLAYING) {
                        Icon(
                            Icons.Filled.GraphicEq,
                            contentDescription = "Playing",
                            modifier = Modifier.padding(end = 5.dp).size(13.dp)
                        )
                    }
                    Text(
                        player?.currentMedia?.title?.takeIf { it.isNotBlank() } ?: "Nothing playing",
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Filled.Speaker,
                        contentDescription = null,
                        tint = LocalContentColor.current.copy(alpha = 0.7f),
                        modifier = Modifier.padding(end = 4.dp).size(12.dp)
                    )
                    Text(
                        player?.displayName ?: "No player",
                        style = MaterialTheme.typography.labelSmall,
                        color = LocalContentColor.current.copy(alpha = 0.7f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@Composable
private fun ExpandedMiniPlayer(
    player: Player?,
    bodyFocus: FocusRequester,
    touch: Modifier,
    onOpenPlayer: () -> Unit,
    onPlayPause: () -> Unit,
    onNext: () -> Unit,
    onVolumeDown: () -> Unit,
    onVolumeUp: () -> Unit,
    onSwitchPlayer: () -> Unit
) {
    Surface(shape = RoundedCornerShape(16.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp).widthIn(max = 560.dp)
        ) {
            // Main body: artwork + track info; OK opens the full player view. Focus feedback is
            // color-only (scale 1.0): the default zoom would overflow the outer container.
            Surface(
                onClick = onOpenPlayer,
                shape = androidx.tv.material3.ClickableSurfaceDefaults.shape(RoundedCornerShape(12.dp)),
                scale = androidx.tv.material3.ClickableSurfaceDefaults.scale(focusedScale = 1f),
                modifier = touch.focusRequester(bodyFocus)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(6.dp)
                ) {
                    MiniArtwork(player, Modifier.size(EXPANDED_ART))
                    Spacer(Modifier.width(12.dp))
                    Column(modifier = Modifier.widthIn(max = 220.dp).padding(end = 6.dp)) {
                        Text(
                            player?.currentMedia?.title?.takeIf { it.isNotBlank() } ?: "Nothing playing",
                            style = MaterialTheme.typography.titleMedium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        val subtitle = listOfNotNull(
                            player?.currentMedia?.artist?.takeIf { it.isNotBlank() },
                            player?.displayName
                        ).joinToString(" · ")
                        if (subtitle.isNotBlank()) {
                            Text(
                                subtitle,
                                style = MaterialTheme.typography.bodySmall,
                                color = LocalContentColor.current.copy(alpha = 0.7f),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
            Spacer(Modifier.width(8.dp))
            IconButton(onClick = onPlayPause, modifier = touch) {
                Icon(
                    if (player?.state == PlaybackState.PLAYING) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                    contentDescription = "Play/Pause"
                )
            }
            Spacer(Modifier.width(6.dp))
            IconButton(onClick = onNext, modifier = touch) {
                Icon(Icons.Filled.SkipNext, contentDescription = "Next")
            }
            Spacer(Modifier.width(6.dp))
            IconButton(onClick = onVolumeDown, modifier = touch) {
                Icon(Icons.AutoMirrored.Filled.VolumeDown, contentDescription = "Volume down")
            }
            Spacer(Modifier.width(6.dp))
            IconButton(onClick = onVolumeUp, modifier = touch) {
                Icon(Icons.AutoMirrored.Filled.VolumeUp, contentDescription = "Volume up")
            }
            Spacer(Modifier.width(6.dp))
            IconButton(onClick = onSwitchPlayer, modifier = touch) {
                Icon(Icons.Filled.Speaker, contentDescription = "Switch player")
            }
        }
    }
}

@Composable
private fun HintBalloon(text: String) {
    Surface(shape = RoundedCornerShape(10.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Icon(
                Icons.Filled.Info,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(end = 8.dp).size(16.dp)
            )
            Text(text, style = MaterialTheme.typography.labelMedium)
        }
    }
}

@Composable
private fun MiniArtwork(player: Player?, modifier: Modifier) {
    val art = player?.currentMedia?.imageUrl
    if (art != null) {
        AsyncImage(
            model = art,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = modifier.clip(RoundedCornerShape(12.dp))
        )
    } else {
        Box(
            modifier = modifier
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Filled.MusicNote, contentDescription = null)
        }
    }
}

/** Player switcher: OK selects the player and closes; the current one is highlighted. */
@Composable
private fun TvPlayerPickerDialog(
    players: List<Player>,
    selectedPlayerId: String?,
    localPlayerId: String?,
    onSelect: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val initialFocus = remember { FocusRequester() }
    Dialog(onDismissRequest = onDismiss) {
        Surface(shape = RoundedCornerShape(16.dp)) {
            Column(modifier = Modifier.padding(20.dp).width(340.dp)) {
                Text("Players", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(12.dp))
                val sortedPlayers = remember(players) { players.sortedBy { it.displayName.lowercase() } }
                LazyColumn(
                    modifier = Modifier.heightIn(max = 400.dp),
                    verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp)
                ) {
                    items(sortedPlayers, key = { it.playerId }) { p ->
                        PlayerPickerCard(
                            player = p,
                            selected = p.playerId == selectedPlayerId,
                            local = p.playerId == localPlayerId,
                            modifier = if (p.playerId == (selectedPlayerId ?: sortedPlayers.first().playerId)) {
                                Modifier.focusRequester(initialFocus)
                            } else {
                                Modifier
                            },
                            onClick = { onSelect(p.playerId) }
                        )
                    }
                }
            }
        }
    }
    LaunchedEffect(Unit) { runCatching { initialFocus.requestFocus() } }
}

@Composable
private fun PlayerPickerCard(
    player: Player,
    selected: Boolean,
    local: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val subtitle = player.currentMedia?.title?.takeIf { it.isNotBlank() }
        ?: player.state.name.lowercase().replaceFirstChar { it.uppercase() }
    val selectedBorder = CardDefaults.border(
        border = Border(
            border = BorderStroke(3.dp, MaterialTheme.colorScheme.primary),
            shape = RoundedCornerShape(12.dp)
        )
    )
    Card(
        onClick = onClick,
        border = if (selected) selectedBorder else CardDefaults.border(),
        // Color/border focus feedback only: the default zoom overflows the dialog insets.
        scale = CardDefaults.scale(focusedScale = 1f),
        modifier = modifier.fillMaxWidth()
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 10.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (player.state == PlaybackState.PLAYING) {
                        Icon(
                            Icons.Filled.GraphicEq,
                            contentDescription = "Playing",
                            modifier = Modifier.padding(end = 6.dp).size(15.dp)
                        )
                    }
                    Text(
                        player.displayName,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Spacer(Modifier.height(2.dp))
                Text(
                    subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = LocalContentColor.current.copy(alpha = 0.7f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            if (local) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFFD32F2F))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        "Local Player",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White
                    )
                }
            }
        }
    }
}

/** [FocusRequester.requestFocus] that tolerates the node attaching a few frames later. */
private suspend fun FocusRequester.requestFocusRetrying() {
    repeat(FOCUS_ATTACH_TRIES) {
        if (runCatching { requestFocus() }.isSuccess) return
        delay(FOCUS_ATTACH_RETRY_MS)
    }
}
