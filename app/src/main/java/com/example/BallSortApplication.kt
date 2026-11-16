package com.example

import android.app.Application
import android.util.Log
import java.io.File

class BallSortApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        try {
            // Pre-create WebView cache directories so Chromium file enumerator finds them
            File(cacheDir, "WebView/Default/HTTP Cache/Code Cache/js").mkdirs()
            File(cacheDir, "WebView/Default/HTTP Cache/Code Cache/wasm").mkdirs()
        } catch (e: Exception) {
            Log.d("BallSortApplication", "Cache dir pre-create: ${e.message}")
        }
    }
}
