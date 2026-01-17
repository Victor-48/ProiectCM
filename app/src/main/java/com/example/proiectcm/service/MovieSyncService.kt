package com.example.proiectcm.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log

class MovieSyncService : Service() {

    private val TAG = "MovieSyncService"

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "Service started")
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "Service destroyed")
    }
}