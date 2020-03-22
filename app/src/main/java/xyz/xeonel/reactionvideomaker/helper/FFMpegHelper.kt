package xyz.xeonel.reactionvideomaker.helper

import android.content.Context
import android.net.Uri
import android.util.Log
import com.arthenica.mobileffmpeg.Config
import com.arthenica.mobileffmpeg.FFmpeg
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
        var command = "-f concat -safe 0 -i ".
            plus(concatFile).
            plus(" -c copy ").
            plus(concatOutput?.absolutePath)
        Log.d("Video", "Exocuting command :$command")

        var result = FFmpeg.execute(command)
        if (result == Config.RETURN_CODE_SUCCESS) {
            return concatOutput!!
        } else {
            return null
        }
    }

    fun getVideoWithReactionOverlay(context: Context, primaryUri: Uri, secondaryUri: Uri) {

        val primaryFile = getFileFromURI(context, primaryUri)
        val secondaryFile = File(secondaryUri.path)

        //Scaling both the videos
//        ffmpeg -i input.jpg -vf scale=w=320:h=240:force_original_aspect_ratio=decrease output_320.png

        val primaryScaled = createTempFile("tempscaled", ".mp4", context.getExternalFilesDir(null))
        val secondaryScaled = createTempFile("tempscaled", ".mp4", context.getExternalFilesDir(null))
        val superImposed = createTempFile("Overlaid", ".mp4", context.getExternalFilesDir(null))

//        var command = "-y -i ".
//            plus(primaryFile.absolutePath).
//            plus(" -vf scale=w1280:h=920 ").
//            plus(primaryScaled.absolutePath)
//        Log.d("Video", "Exocuting command :$command")
//        var result = FFmpeg.execute(command)
//
//
//
//        command = "-y -i ".
//            plus(secondaryFile.absolutePath).
//            plus(" -vf scale=w320:h=240 ").
//            plus(secondaryScaled.absolutePath)
//        Log.d("Video", "Exocuting command :$command")
//        result = FFmpeg.execute(command)
//
//        command = "-y -i ".
//            plus(primaryScaled.absolutePath).
//            plus(" -vf \"movie=").
//            plus(secondaryScaled.absolutePath).
//            plus(";[in][inner] overlay=70:70 [out]\" ").
//            plus(superImposed.absolutePath)
//        Log.d("Video", "Exocuting command :$command")
//        result = FFmpeg.execute(command)

        var command = "-y -i ".
            plus(primaryFile.absolutePath).
            plus(" -vf \"movie=").
            plus(secondaryFile.absolutePath).
            plus(", scale=200:200 [inner]; [in][inner] overlay=2:70 [out]\" ").
                plus(superImposed.absolutePath)
        Log.d("Video", "Exocuting command :$command")
        var result = FFmpeg.execute(command)

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

        Log.d("VideoConcat", "Wrote list file to " + list.absolutePath)
        return list.absolutePath
    }

    fun getFileFromURI(context: Context, uri: Uri) : File {
        val f = createTempFile("tempuriToFile", ".mp4", context.getExternalFilesDir(null))
        val inputStream: InputStream? = context.contentResolver.openInputStream(uri)

            f.setWritable(true, false)
            val outputStream: OutputStream = FileOutputStream(f)
            val buffer = ByteArray(1024)
            var length = 0
            while (inputStream?.read(buffer).also({ length = it!! })!! > 0) {
                outputStream.write(buffer, 0, length)
            }
            outputStream.close()
            inputStream?.close()
            return f
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