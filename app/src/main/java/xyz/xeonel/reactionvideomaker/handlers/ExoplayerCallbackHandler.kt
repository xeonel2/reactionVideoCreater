package xyz.xeonel.reactionvideomaker.handlers

import com.google.android.exoplayer2.Player
import xyz.xeonel.reactionvideomaker.viewmodel.RecordViewModel

class ExoplayerCallbackHandler(val recordViewModel: RecordViewModel) : Player.EventListener {
    override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
        if (playbackState == Player.STATE_ENDED && playWhenReady) {
            recordViewModel.finishRecording()
        }
    }
}