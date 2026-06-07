package net.asksakis.massdroidv2.tv.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import dagger.hilt.android.AndroidEntryPoint
import net.asksakis.massdroidv2.tv.TvPlaybackService
import net.asksakis.massdroidv2.tv.ui.theme.MassDroidTvTheme

@AndroidEntryPoint
class TvMainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Bring up the foreground Sendspin player service so the Shield registers
        // with MA as a synced speaker (the activity is foreground here, so the FGS
        // start is allowed).
        TvPlaybackService.start(this)
        setContent {
            MassDroidTvTheme {
                TvRoot()
            }
        }
    }
}
