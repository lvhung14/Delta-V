package dev.lvhung14.delta_v.feature.launches.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.lvhung14.delta_v.feature.launches.LaunchesUiState
import dev.lvhung14.delta_v.feature.launches.LaunchesViewModel

/**
 * Route for the Upcoming Launch Calendar screen.
 * Transforms the existing LaunchesUiState into the data needed for the calendar view.
 */
@Composable
fun UpcomingLaunchCalendarRoute(
    viewModel: LaunchesViewModel = hiltViewModel(),
    onNavigateToVehicles: () -> Unit = {},
    onNavigateToNews: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var selectedNavItem by remember { mutableIntStateOf(0) }

    when (val state = uiState) {
        LaunchesUiState.Loading -> {
            // Show loading state in calendar format
            UpcomingLaunchCalendarScreen(
                featuredLaunch = FeaturedLaunch(
                    id = "loading",
                    missionName = "Loading...",
                    rocketName = "---",
                    launchSite = "---",
                    imageUrl = null,
                    targetTimeMillis = System.currentTimeMillis() + 86400000,
                    streamUrl = null
                ),
                missionManifest = emptyList(),
                selectedNavItem = selectedNavItem,
                onNavItemClick = { index ->
                    selectedNavItem = index
                    when (index) {
                        1 -> onNavigateToVehicles()
                        2 -> onNavigateToNews()
                        3 -> onNavigateToSettings()
                    }
                }
            )
        }

        is LaunchesUiState.Error -> {
            // Show error state
            UpcomingLaunchCalendarScreen(
                featuredLaunch = FeaturedLaunch(
                    id = "error",
                    missionName = "Unable to Load",
                    rocketName = "Tap to retry",
                    launchSite = state.message,
                    imageUrl = null,
                    targetTimeMillis = System.currentTimeMillis(),
                    streamUrl = null
                ),
                missionManifest = emptyList(),
                onWatchLiveClick = { viewModel.refreshUpcomingLaunches() },
                selectedNavItem = selectedNavItem,
                onNavItemClick = { index ->
                    selectedNavItem = index
                    when (index) {
                        1 -> onNavigateToVehicles()
                        2 -> onNavigateToNews()
                        3 -> onNavigateToSettings()
                    }
                }
            )
        }

        is LaunchesUiState.Success -> {
            val launches = state.launches

            // First launch becomes the featured launch
            val featuredLaunch = if (launches.isNotEmpty()) {
                val first = launches.first()
                FeaturedLaunch(
                    id = first.id,
                    missionName = first.title.substringAfter("|").trim()
                        .ifEmpty { first.title },
                    rocketName = first.title.substringBefore("|").trim(),
                    launchSite = first.location,
                    imageUrl = first.imageUrl,
                    targetTimeMillis = parseCountdownToMillis(first.countdown),
                    streamUrl = first.detailUrl
                )
            } else {
                FeaturedLaunch(
                    id = "empty",
                    missionName = "No Launches",
                    rocketName = "---",
                    launchSite = "Check back later",
                    imageUrl = null,
                    targetTimeMillis = System.currentTimeMillis(),
                    streamUrl = null
                )
            }

            // Remaining launches become the mission manifest
            val missionManifest = if (launches.size > 1) {
                launches.drop(1).mapIndexed { index, launch ->
                    MissionItem(
                        id = launch.id,
                        missionName = launch.title.substringAfter("|").trim()
                            .ifEmpty { launch.title },
                        rocketName = launch.title.substringBefore("|").trim(),
                        launchSite = launch.location.substringBefore("-").trim(),
                        imageUrl = launch.imageUrl,
                        date = extractDate(launch.launchWindow),
                        time = extractTime(launch.launchWindow),
                        isComplete = launch.status.lowercase() == "success",
                        hasNotificationEnabled = index % 2 == 1 // Demo toggle state
                    )
                }
            } else {
                emptyList()
            }

            UpcomingLaunchCalendarScreen(
                featuredLaunch = featuredLaunch,
                missionManifest = missionManifest,
                onMenuClick = { /* TODO: Open drawer */ },
                onFilterClick = { /* TODO: Open filter */ },
                onWatchLiveClick = { /* TODO: Open stream URL */ },
                onViewAllClick = { /* TODO: Navigate to full list */ },
                onMissionClick = { /* TODO: Navigate to detail */ },
                onNotificationToggle = { /* TODO: Toggle notification */ },
                selectedNavItem = selectedNavItem,
                onNavItemClick = { index ->
                    selectedNavItem = index
                    when (index) {
                        0 -> { /* Already on launches */ }
                        1 -> onNavigateToVehicles()
                        2 -> onNavigateToNews()
                        3 -> onNavigateToSettings()
                    }
                }
            )
        }
    }
}

/**
 * Parse countdown string like "T-2d 4h" to milliseconds from now
 */
private fun parseCountdownToMillis(countdown: String): Long {
    val now = System.currentTimeMillis()

    // Try to extract days and hours from format like "T-2d 4h" or similar
    val daysMatch = Regex("""(\d+)d""").find(countdown)
    val hoursMatch = Regex("""(\d+)h""").find(countdown)
    val minsMatch = Regex("""(\d+)m""").find(countdown)

    val days = daysMatch?.groupValues?.get(1)?.toLongOrNull() ?: 0
    val hours = hoursMatch?.groupValues?.get(1)?.toLongOrNull() ?: 0
    val mins = minsMatch?.groupValues?.get(1)?.toLongOrNull() ?: 0

    val totalMillis = (days * 24 * 60 * 60 * 1000) +
            (hours * 60 * 60 * 1000) +
            (mins * 60 * 1000)

    return now + totalMillis
}

/**
 * Extract date from launch window like "Sat, Nov 9 - 09:00 UTC"
 */
private fun extractDate(launchWindow: String): String {
    // Try to extract date part before the dash
    val datePart = launchWindow.substringBefore("-").trim()
    // Remove day of week if present
    return if (datePart.contains(",")) {
        datePart.substringAfter(",").trim()
    } else {
        datePart
    }
}

/**
 * Extract time from launch window like "Sat, Nov 9 - 09:00 UTC"
 */
private fun extractTime(launchWindow: String): String {
    return if (launchWindow.contains("-")) {
        launchWindow.substringAfter("-").trim()
    } else {
        "TBD"
    }
}
