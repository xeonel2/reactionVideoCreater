package xyz.xeonel.reactionvideomaker.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.exoplayer2.ExoPlayer
import com.otaliastudios.cameraview.CameraListener
import com.otaliastudios.cameraview.CameraView
import com.otaliastudios.cameraview.VideoResult
import kotlinx.android.synthetic.main.activity_record.*
import xyz.xeonel.reactionvideomaker.R
import xyz.xeonel.reactionvideomaker.databinding.ActivityRecordBinding
import xyz.xeonel.reactionvideomaker.handlers.ExoplayerCallbackHandler
import xyz.xeonel.reactionvideomaker.helper.ExoplayerHelper
import xyz.xeonel.reactionvideomaker.helper.FFMpegHelper
import xyz.xeonel.reactionvideomaker.viewmodel.RecordViewModel
import java.io.File
import java.io.FileOutputStream
import java.sql.Timestamp


class RecordActivity : AppCompatActivity() {

    lateinit var uri : Uri
    private var camera: CameraView? = null
    lateinit var player : ExoPlayer
//    lateinit var outputStream : FileOutputStream
    var file : File? = null
    lateinit var callbackHandler : ExoplayerCallbackHandler
//    var tempFile =


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

        camera = cameraView
        camera?.setLifecycleOwner(this@RecordActivity)



        uri = intent.extras!!["VideoURI"] as Uri
        file = File(getExternalFilesDir(null), "" + Timestamp(System.currentTimeMillis()).time + ".mp4")
        FFMpegHelper.getInstance().createConcatableVideo(file!!)
//        outputStream = FileOutputStream(file)

        //Getting Exoplayer ready for playback
        callbackHandler =  ExoplayerCallbackHandler(recordViewModel)
        player =  ExoplayerHelper.getInstance().getNewPlayer(this, uri)
        simpleExoPlayerView.player = player
        player.playWhenReady = false
        player.seekToDefaultPosition(0)
        player.addListener(callbackHandler)

        camera?.addCameraListener(object : CameraListener() {
            override fun onVideoTaken(result: VideoResult) {
                Log.v("VideoMaker", "Adding file to concat")
                FFMpegHelper.getInstance().addVideoToConcat(result.file)
                if (recordViewModel.completedRecording.value!!) {
                    file = FFMpegHelper.getInstance().getCompleteRecording(this@RecordActivity)
                    player.release()
                    // Next screen
                    Log.v("VideoMaker", "Finished Recording Recording URI: $file")
                    val reactionPlaybackActivity =
                        Intent(this@RecordActivity, ReactionPlaybackActivity::class.java)
                    reactionPlaybackActivity.putExtra("VideoURI", uri)
                    reactionPlaybackActivity.putExtra("RecordedFileURI", Uri.parse(file.toString()))
                    startActivity(reactionPlaybackActivity)
                }
            }
        })


        recordViewModel.facing.observe(this, Observer {
            Log.v("VideoMaker", "Observer triggered")
            camera?.toggleFacing()
        })

        recordViewModel.completedRecording.observe(this, Observer {
            if (it) {
            Log.v("VideoMaker", "Finished Recording")

            camera?.stopVideo()
//            outputStream.flush()
//            file = FFMpegHelper.getInstance().getCompleteRecording()
//            player.release()
//            // Next screen
//            Log.v("VideoMaker", "Finished Recording Recording URI: $file")
//            val reactionPlaybackActivity = Intent(this, ReactionPlaybackActivity::class.java)
//            reactionPlaybackActivity.putExtra("VideoURI", uri)
//            reactionPlaybackActivity.putExtra("RecordedFileURI", Uri.parse(file.toString()))
//            startActivity(reactionPlaybackActivity)
            }
        })

        recordViewModel.isRecording.observe(this, Observer {

            if (it) {
                player.playWhenReady = true
                camera?.takeVideo(createTempFile("tempvideo", ".mp4", getExternalFilesDir(null)))
                Log.v("VideoMaker", "Started Recording")
            } else {
                player.playWhenReady = false
                camera?.stopVideo()
                Log.v("VideoMaker", "Storpped Recording")
            }
        })


    }
}
