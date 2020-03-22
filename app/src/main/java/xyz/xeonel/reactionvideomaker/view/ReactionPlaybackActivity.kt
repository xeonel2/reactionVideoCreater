package xyz.xeonel.reactionvideomaker.view

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.exoplayer2.ExoPlayer
import kotlinx.android.synthetic.main.activity_reaction_playback.*
import xyz.xeonel.reactionvideomaker.R
import xyz.xeonel.reactionvideomaker.helper.ExoplayerHelper
import xyz.xeonel.reactionvideomaker.helper.FFMpegHelper

class ReactionPlaybackActivity : AppCompatActivity() {
    lateinit var player : ExoPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reaction_playback)
        val primaryURI = intent.extras!!["VideoURI"] as Uri
        val secondaryURI = intent.extras!!["RecordedFileURI"] as Uri
        val superImposed = FFMpegHelper.getInstance().getVideoWithReactionOverlay(this, primaryURI, secondaryURI)

        player = ExoplayerHelper.getInstance().getNewPlayer(this, Uri.fromFile(superImposed))
        reactionExoplayerView.player = player
        player.playWhenReady = true

    }
}
