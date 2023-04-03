package com.connor.picofull

import android.app.Application
import com.connor.picofull.constant.configPath
import com.connor.picofull.datastores.DataStoreManager
import com.connor.picofull.utils.logCat
import com.connor.picofull.utils.readIniFile
import com.vi.vioserial.NormalSerial
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collect
import java.io.File
import javax.inject.Inject

@HiltAndroidApp
class App : Application() {

     companion object {
      lateinit var app: App private set
    }

    override fun onCreate() {
        super.onCreate()
        app = this
    }
}