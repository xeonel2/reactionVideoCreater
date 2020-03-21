package xyz.xeonel.reactionvideomaker.view

import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_record.*
import xyz.xeonel.reactionvideomaker.R


class VideoSelectActivity : AppCompatActivity() {
    val REQUEST_TAKE_GALLERY_VIDEO = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


    }

    fun selectButtonClickHandler(view : View) {
        val getVideoIntent = Intent(Intent.ACTION_GET_CONTENT, MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
        getVideoIntent.type = "video/*"


        if(getVideoIntent.resolveActivity(packageManager) != null) {
            //has an app to select video
            startActivityForResult(Intent.createChooser(getVideoIntent, "Select Video"), REQUEST_TAKE_GALLERY_VIDEO )

        }


    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_TAKE_GALLERY_VIDEO && resultCode == RESULT_OK) {
            val videoURI = data?.data
            Log.v("VideoSelect", " "+videoURI)
            // Use ViewModel to set URL to layout

            val recordActivityIntent = Intent(this, RecordActivity::class.java)
            recordActivityIntent.putExtra("VideoURI", videoURI)
            startActivity(recordActivityIntent)

        }


    }
}
