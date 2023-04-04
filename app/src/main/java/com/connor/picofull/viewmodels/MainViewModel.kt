package com.connor.picofull.viewmodels

import android.text.TextUtils
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.connor.picofull.BuildConfig
import com.connor.picofull.R
import com.connor.picofull.constant.ISSUES_PULSE_XXXX
import com.connor.picofull.constant.UPLOAD_1064
import com.connor.picofull.constant.UPLOAD_532
import com.connor.picofull.constant.videoPath
import com.connor.picofull.datastores.DataStoreManager
import com.connor.picofull.models.BackstageData
import com.connor.picofull.models.HomeData
import com.connor.picofull.models.SettingsData
import com.connor.picofull.models.VideoInfo
import com.connor.picofull.models.type.BtnType
import com.connor.picofull.utils.*
import com.connor.picofull.zc.Device
import com.connor.picofull.zc.SerialPortFinder
import com.vi.vioserial.NormalSerial
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val dataStoreManager: DataStoreManager) :
    ViewModel() {

    val homeData = HomeData()
    val settingsData = SettingsData()
    val backstageData = BackstageData()
    val videoList = ArrayList<VideoInfo>()
    val items = ArrayList<String>()

    var serialPort = ""

    private val sendHexList = ArrayList<String>()

    private val _receiveEvent = MutableSharedFlow<String>()
    val receiveEvent = _receiveEvent.asSharedFlow()

    private val _btnEvent = MutableSharedFlow<BtnType>()
    val btnEvent = _btnEvent.asSharedFlow()

    val waveState = dataStoreManager.waveFLow.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        false
    )

    val serialFlow = dataStoreManager.serialFlow.shareIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(3000),
    )

    private var remainingTime = -1L

    init {
        SerialPortFinder().allDevices.forEach {
            val ser = it.replace("\\s\\(.*?\\)".toRegex(), "")
            items.add(ser)
        }
        NormalSerial.instance().addDataListener { data ->
            data.logCat()
            sendHexList.clear()
            if (data.contains(ISSUES_PULSE_XXXX)) {
                val value = data.substring(data.length - 8).toLong(16)
                homeData.pulse = value
            }
            viewModelScope.launch { _receiveEvent.emit(data) }
        }

        dataStoreManager.languageFlow.filterNotNull().onEach { id ->
            settingsData.language = id
            when (id) {
                R.id.btn_chinese -> {
                    LocaleListCompat.forLanguageTags("zh").also {
                        AppCompatDelegate.setApplicationLocales(it)
                    }
                }
                R.id.btn_english -> {
                    LocaleListCompat.forLanguageTags("en").also {
                        AppCompatDelegate.setApplicationLocales(it)
                    }
                }
                R.id.btn_french -> {
                    LocaleListCompat.forLanguageTags("fr").also {
                        AppCompatDelegate.setApplicationLocales(it)
                    }
                }
                R.id.btn_spanish -> {
                    LocaleListCompat.forLanguageTags("es").also {
                        AppCompatDelegate.setApplicationLocales(it)
                    }
                }
            }
        }.launchIn(viewModelScope)

//        if (!BuildConfig.DEBUG) {
//            viewModelScope.launch(Dispatchers.Default) {
//                while (true) {
//                    delay(1000)
//                    remainingTime--
//                    remainingTime.logCat()
//                    if (remainingTime == 0L) {
//                        if (sendHexList.isNotEmpty()) {
//                            sendHexList.forEach { hex ->
//                                remainingTime = 10L
//                                NormalSerial.instance().sendHex(hex)
//                            }
//                        }
//                    }
//                    if (remainingTime <= -5184000L) remainingTime = -1L
//                }
//            }
//        }
    }

    fun sendWave() {
        viewModelScope.launch {
            delay(280)
            if (!homeData.waveId) {
                sendHex(UPLOAD_1064)
                "1064".logCat()
            } else {
                sendHex(UPLOAD_532)
                "532".logCat()
            }
        }
    }


    fun sendHex(hex: String) {
        sendHexList.add(hex)
        remainingTime = 10L
        NormalSerial.instance().sendHex(hex)
    }

    fun senBtnEvent(btnType: BtnType) {
        viewModelScope.launch {
            _btnEvent.emit(btnType)
        }
    }

    fun storeLanguage(value: Int) {
        viewModelScope.launch {
            dataStoreManager.storeLanguage(value)
        }
    }

    fun storeWave(value: Boolean) {
        viewModelScope.launch {
            dataStoreManager.storeWave(value)
        }
    }

    fun storeSerial(value: String) {
        viewModelScope.launch {
            dataStoreManager.storeSerial(value)
        }
    }

    override fun onCleared() {
        super.onCleared()
        "onCleared".logCat()
        NormalSerial.instance().close()
    }
}