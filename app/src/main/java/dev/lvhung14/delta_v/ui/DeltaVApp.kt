package dev.lvhung14.delta_v.ui

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.HiltAndroidApp
import dev.lvhung14.delta_v.network.retrofit.DeltaVRetrofitNetwork
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltAndroidApp
class DeltaVApp : Application() {
    private val TAG = javaClass.name
    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "onCreate()")
        GlobalScope.launch {
        }
    }
}