package com.connor.picofull

import android.app.Application
import com.vi.vioserial.NormalSerial
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class App : Application() {

    override fun onCreate() {
        super.onCreate()
        NormalSerial.instance().open("/dev/ttyS0", 9600)
    }
}