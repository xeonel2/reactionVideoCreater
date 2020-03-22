package xyz.xeonel.reactionvideomaker.helper

import android.content.Context
import com.google.android.exoplayer2.DefaultLoadControl
import com.google.android.exoplayer2.DefaultRenderersFactory
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import android.net.Uri

class ExoplayerHelper {

    //Get a new Exoplayer
    fun getNewPlayer(context: Context, uri: Uri) : ExoPlayer {
        val renderersFactory = DefaultRenderersFactory(context)
        val trackSelector = DefaultTrackSelector()
        val loadControl = DefaultLoadControl()
        val player =  ExoPlayerFactory.newSimpleInstance(
            context,
            renderersFactory,
            trackSelector,
            loadControl
        )
        player.prepare(
            ProgressiveMediaSource.Factory(DefaultDataSourceFactory(context, "player")).createMediaSource(
                uri))
        return player
    }

    // Making this helper a singleton
    companion object {
        @Volatile
        private var instance: ExoplayerHelper? = null

        fun getInstance() = instance ?: synchronized(this) {
            instance ?: ExoplayerHelper().also { instance = it }
        }
    }
}