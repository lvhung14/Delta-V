package dev.lvhung14.delta_v.feature.launches

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.lvhung14.delta_v.data.LaunchesRepository
import dev.lvhung14.delta_v.model.Launch
import java.time.Clock
import java.time.Duration
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

@HiltViewModel
class LaunchesViewModel @Inject constructor(
    private val launchesRepository: LaunchesRepository
) : ViewModel() {

    private val clock: Clock = Clock.systemUTC()

    // Remember if the latest sync failed – the UI uses this to display the offline banner.
    private val offlineMode = MutableStateFlow(false)
    private val _uiState: MutableStateFlow<LaunchesUiState> =
        MutableStateFlow(LaunchesUiState.Loading)
    val uiState: StateFlow<LaunchesUiState> = _uiState.asStateFlow()

    init {
        observeCache()
        refreshUpcomingLaunches()
    }

    fun refreshUpcomingLaunches() {
        viewModelScope.launch {
            val result = launchesRepository.refreshUpcomingLaunches()
            result.onFailure {
                offlineMode.value = true
                if (_uiState.value !is LaunchesUiState.Success) {
                    _uiState.value = LaunchesUiState.Error(
                        it.message ?: "Unable to load launches right now."
                    )
                }
            }
            result.onSuccess {
                offlineMode.value = false
            }
        }
    }

    /**
     * Mirrors the Now in Android approach: we react to DB updates and merge them with the latest
     * connectivity status so the UI can stay responsive even without network.
     */
    private fun observeCache() {
        viewModelScope.launch {
            launchesRepository.upcomingLaunches
                .combine(offlineMode) { launches, offline ->
                    if (launches.isEmpty()) {
                        if (offline) {
                            LaunchesUiState.Error(
                                "No cached launches yet. Reconnect and try again."
                            )
                        } else {
                            LaunchesUiState.Loading
                        }
                    } else {
                        val sortedLaunches = launches.sortedBy { it.net ?: Instant.MAX }
                        val cards = sortedLaunches.map { it.toCardUiState(clock) }
                        val lastUpdatedInstant = launches.mapNotNull { it.lastUpdated }.maxOrNull()
                        val lastUpdatedStatus = lastUpdatedInstant?.let {
                            "Updated ${formatRelativeTime(it, clock)}"
                        } ?: "Updated just now"

                        LaunchesUiState.Success(
                            launches = cards,
                            isOffline = offline,
                            lastUpdatedStatus = lastUpdatedStatus
                        )
                    }
                }
                .collect { state -> _uiState.value = state }
        }
    }
}

private fun Launch.toCardUiState(clock: Clock): LaunchCardUiState {
    val launchWindow = net?.let { launchWindowFormatter.format(it) } ?: "Launch window TBD"
    val countdown = formatCountdown(net, clock)
    val location = buildList {
        padName?.let(::add)
        locationName?.let(::add)
        countryCode?.let { add(it.uppercase(Locale.getDefault())) }
    }.distinct().joinToString(" • ").ifBlank { "Location TBD" }

    val missionSummary = missionDescription?.takeIf { it.isNotBlank() }
        ?: "Mission details will be announced closer to launch."

    val statusText = status.abbreviation ?: status.name ?: "TBD"

    return LaunchCardUiState(
        id = id,
        title = displayName.ifBlank { missionName ?: "Upcoming Mission" },
        provider = providerName ?: "Launch provider TBA",
        location = location,
        launchWindow = launchWindow,
        countdown = countdown,
        status = statusText,
        missionSummary = missionSummary,
        imageUrl = imageUrl,
        detailUrl = detailUrl
    )
}

private val launchWindowFormatter = DateTimeFormatter.ofPattern("EEE, MMM d • HH:mm z")
    .withLocale(Locale.getDefault())
    .withZone(ZoneId.systemDefault())

private fun formatCountdown(net: Instant?, clock: Clock): String {
    net ?: return "Launch time TBD"
    val now = clock.instant()
    val duration = Duration.between(now, net)
    if (duration.isNegative) return "Launched"

    val days = duration.toDays()
    val hours = duration.minusDays(days).toHours()
    val minutes = duration.minusDays(days).minusHours(hours).toMinutes()

    val builder = StringBuilder("T-")
    if (days > 0) builder.append("${days}d ")
    if (hours > 0 || days > 0) builder.append("${hours}h ")
    builder.append("${minutes}m")

    return builder.toString().trim()
}

private fun formatRelativeTime(instant: Instant, clock: Clock): String {
    val duration = Duration.between(instant, clock.instant())
    val minutes = duration.toMinutes()
    return when {
        minutes < 1 -> "just now"
        minutes < 60 -> "${minutes}m ago"
        minutes < 60 * 24 -> "${duration.toHours()}h ago"
        else -> "${duration.toDays()}d ago"
    }
}
