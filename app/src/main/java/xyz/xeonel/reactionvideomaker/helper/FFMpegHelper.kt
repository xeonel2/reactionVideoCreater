package xyz.xeonel.reactionvideomaker.helper

import android.content.Context
import android.net.Uri
import android.util.Log
import com.arthenica.mobileffmpeg.FFmpeg
import com.arthenica.mobileffmpeg.Config
import java.io.*

class FFMpegHelper {

    var concatableVideos : MutableList<File>? = mutableListOf()
    var concatOutput: File? = null

    fun createConcatableVideo(primaryFile: File) {
        concatOutput = primaryFile
        concatableVideos = mutableListOf()
    }

    fun addVideoToConcat(file: File) {
        concatableVideos?.add(file)
    }

    fun getCompleteRecording(context: Context) : File? {
        val concatFile = getTempListFile(context)
        val command = "-f concat -i ".
            plus(concatFile).
            plus(" -c copy ").
            plus(concatOutput?.absolutePath)
        var result = FFmpeg.execute(command)
        if (result == Config.RETURN_CODE_SUCCESS) {
            return concatOutput!!
        } else {
            return null
        }
    }

    private fun getTempListFile(context: Context) : String {
        val list: File = File.createTempFile("ffmpeg-list", ".txt", context.getExternalFilesDir(null))
        val writer : Writer = BufferedWriter(OutputStreamWriter(FileOutputStream(list)))

        try {
            for (input in concatableVideos!!) {
                writer.write("file '${input.absolutePath}'\n")
                Log.d("VideoConcat", "Writing to list file: file '${input.absolutePath}'")
            }
        } catch (e: IOException) {
            e.printStackTrace()
            return "/"
        } finally {
            try {
                writer.close()
            } catch (ex: IOException) {
                ex.printStackTrace()
            }
        }

        Log.d("VideoConcat", "Wrote list file to " + list.getAbsolutePath())
        return list.getAbsolutePath()
    }


    // Making this helper a singleton
    companion object {
        @Volatile
        private var instance: FFMpegHelper? = null

        fun getInstance() = instance ?: synchronized(this) {
            instance ?: FFMpegHelper().also { instance = it }
        }
    }
}