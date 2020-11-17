package com.ma7moud3ly.jobit

import android.app.Application

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        resume_path = getExternalFilesDir(null)?.path ?: ""
    }

    companion object {
        lateinit var resume_path: String
    }
}