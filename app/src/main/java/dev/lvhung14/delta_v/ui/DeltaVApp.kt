package dev.lvhung14.delta_v.ui

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import androidx.lifecycle.lifecycleScope
import dev.lvhung14.delta_v.network.DeltaVApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DeltaVApp : Application() {
    private val TAG = javaClass.name
    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "onCreate()")
        GlobalScope.launch {
            val results = DeltaVApi.retrofitService.getAllLaunches()
            Log.d(TAG, results.count.toString())
        }
    }
}