package com.connor.picofull

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.connor.picofull.utils.logCat

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        intent.action?.logCat()
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            "action in".logCat()
            // 在这里启动您的应用程序
            val launchIntent = context.packageManager.getLaunchIntentForPackage("com.connor.picofull")
            if (launchIntent != null) {
                launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(launchIntent)
            }
        }
    }
}
