package dev.lvhung14.delta_v.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import dev.lvhung14.delta_v.feature.launches.ui.UpcomingLaunchCalendarRoute
import dev.lvhung14.delta_v.ui.theme.DeltaVTheme

@Composable
fun DeltaVApp() {
    DeltaVTheme(dynamicColor = false) { // Disable dynamic color to use Mars theme
        Surface(color = MaterialTheme.colorScheme.background) {
            UpcomingLaunchCalendarRoute()
        }
    }
}

