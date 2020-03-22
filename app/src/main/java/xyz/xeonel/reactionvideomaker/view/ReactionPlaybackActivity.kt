package xyz.xeonel.reactionvideomaker.view

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import xyz.xeonel.reactionvideomaker.R
import xyz.xeonel.reactionvideomaker.helper.FFMpegHelper
import java.io.File

class ReactionPlaybackActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reaction_playback)
        val primaryURI = intent.extras!!["VideoURI"] as Uri
        val secondaryURI = intent.extras!!["RecordedFileURI"] as Uri
        FFMpegHelper.getInstance().getVideoWithReactionOverlay(this, primaryURI, secondaryURI)
    }
}
