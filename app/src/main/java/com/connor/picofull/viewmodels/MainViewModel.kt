package com.connor.picofull.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.connor.picofull.constant.UPLOAD_532
import com.connor.picofull.datastores.DataStoreManager
import com.connor.picofull.models.HomeData
import com.connor.picofull.models.SettingsData
import com.connor.picofull.utils.logCat
import com.vi.vioserial.NormalSerial
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val dataStoreManager: DataStoreManager) : ViewModel() {

    val homeData = HomeData()
    val settingsData = SettingsData()

    private val _receiveEvent = MutableSharedFlow<String>()
    val receiveEvent = _receiveEvent.asSharedFlow()

    init {
        NormalSerial.instance().open("/dev/ttyS0", 9600)
        NormalSerial.instance().addDataListener { data ->
            data.logCat()
            viewModelScope.launch {
                _receiveEvent.emit(data)
            }
        }
        dataStoreManager.languageFlow.onEach {
            settingsData.language = it
        }.launchIn(viewModelScope)
    }

    fun sendHex(hex: String) {
        NormalSerial.instance().sendHex(hex)
    }

    fun storeLanguage(value: Int) {
        viewModelScope.launch {
            dataStoreManager.storeLanguage(value)
        }
    }
}