package dev.lvhung14.delta_v

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Single entry point for Hilt. Keeping this lightweight prevents Application start latency.
 */
@HiltAndroidApp
class DeltaVApplication : Application()
