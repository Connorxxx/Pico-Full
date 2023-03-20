package com.connor.picofull

import android.app.Application
import com.vi.vioserial.NormalSerial
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class App : Application() {

     companion object {
      lateinit var app: App private set
    }
    override fun onCreate() {
        super.onCreate()
        app = this
        NormalSerial.instance().open("/dev/ttyS0", 9600)
    }
}