package xyz.xeonel.reactionvideomaker.viewmodel

import android.icu.text.AlphabeticIndex
import android.view.View
import androidx.databinding.Bindable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.camerakit.CameraKitView
import kotlinx.android.synthetic.main.activity_record.*
import xyz.xeonel.reactionvideomaker.view.RecordActivity

class RecordViewModel : ViewModel() {
    private val _recordingState = MutableLiveData<RecordingState>()
    val recordingState : LiveData<RecordingState>
        get() = _recordingState

    private val _isRecording = MutableLiveData<Boolean>()
    val isRecording : LiveData<Boolean>
        get() = _isRecording

    private val _completedRecording = MutableLiveData<Boolean>()
    val completedRecording : LiveData<Boolean>
        get() = _completedRecording

    private var _facing = MutableLiveData<String>()
    val facing : LiveData<String>
        get() = _facing

    init{
        _facing.value = "back"
        _completedRecording.value = false
        _isRecording.value = false
    }


    fun switchCamera() {
        if (_facing.value == "back") _facing.value = "front"
        else _facing.value = "back"
    }

    fun finishRecording() {
        if (isRecording.value!!) {
            _completedRecording.value = true
        }
    }

}