package xyz.xeonel.reactionvideomaker.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.camerakit.CameraKitView
import com.google.android.exoplayer2.DefaultLoadControl
import com.google.android.exoplayer2.DefaultRenderersFactory
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import kotlinx.android.synthetic.main.activity_record.*
import xyz.xeonel.reactionvideomaker.R
import xyz.xeonel.reactionvideomaker.databinding.ActivityRecordBinding
import xyz.xeonel.reactionvideomaker.handlers.ExoplayerCallbackHandler
import xyz.xeonel.reactionvideomaker.viewmodel.RecordViewModel
import java.io.File
import java.io.FileOutputStream
import java.sql.Timestamp


class RecordActivity : AppCompatActivity() {
    val renderersFactory = DefaultRenderersFactory(this)
    val trackSelector = DefaultTrackSelector()
    val loadControl = DefaultLoadControl()
    lateinit var uri : Uri
    private var cameraKitView: CameraKitView? = null
    var isRecording : Boolean = false
    lateinit var player : ExoPlayer
    lateinit var outputStream : FileOutputStream
    lateinit var file : File
    lateinit var callbackHandler : ExoplayerCallbackHandler


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_record)
        val recordViewModel = ViewModelProviders.of(this)
            .get(RecordViewModel::class.java)

        DataBindingUtil.setContentView<ActivityRecordBinding>(
            this, R.layout.activity_record
        ).apply {
            this.lifecycleOwner = this@RecordActivity
            this.viewmodel = recordViewModel
        }

        cameraKitView = cameraView
        uri = intent.extras!!["VideoURI"] as Uri
        file = File(getExternalFilesDir(null), "" + Timestamp(System.currentTimeMillis()).time + ".mp4")
        outputStream = FileOutputStream(file)
        callbackHandler =  ExoplayerCallbackHandler(recordViewModel)
        player =  ExoPlayerFactory.newSimpleInstance(this,renderersFactory, trackSelector,loadControl)
        simpleExoPlayerView.player = player
        player.prepare(ProgressiveMediaSource.Factory(DefaultDataSourceFactory(this, "player")).createMediaSource(
            uri))
        player.playWhenReady = false
        player.seekToDefaultPosition(0)
        player.addListener(callbackHandler)


        recordViewModel.facing.observe(this, Observer {
            Log.v("VideoMaker", "Observer triggered")
            cameraKitView?.toggleFacing()
        })

        recordViewModel.completedRecording.observe(this, Observer {
            if (it) {
            Log.v("VideoMaker", "Finished Recording")
            outputStream.close()
            outputStream.flush()
            cameraKitView?.onStop()
            player.release()
            // Next screen
            val reactionPlaybackActivity = Intent(this, ReactionPlaybackActivity::class.java)
            reactionPlaybackActivity.putExtra("VideoURI", uri)
            reactionPlaybackActivity.putExtra("RecordedFile", file)
            startActivity(reactionPlaybackActivity) }
        })
    }

    override fun onStart() {
        super.onStart()
        cameraKitView!!.onStart()
    }

    override fun onResume() {
        super.onResume()
        cameraKitView!!.onResume()
    }

    override fun onPause() {
        cameraKitView!!.onPause()
        super.onPause()
    }

    override fun onStop() {
        cameraKitView!!.onStop()
        super.onStop()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions!!, grantResults!!)
        cameraKitView!!.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }


    fun toggleRecording() {
        if (!isRecording) {
            player.playWhenReady = true
            cameraKitView!!.onStart()
            player.addListener(callbackHandler)

            cameraKitView?.captureVideo(CameraKitView.VideoCallback { cameraKitView, capturedVideo ->

                val outputData = capturedVideo as ByteArray
                outputStream.write(outputData)
            })

        } else {
            player.playWhenReady = false
            cameraKitView!!.onPause()
        }
        isRecording = !isRecording
    }

}
