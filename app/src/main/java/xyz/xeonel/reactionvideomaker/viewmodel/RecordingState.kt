package xyz.xeonel.reactionvideomaker.viewmodel

data class RecordingState(var isRecording: Boolean = false,
                          var watchVideoURL: String? = null,
                          var playBackFinished: Boolean = false)

