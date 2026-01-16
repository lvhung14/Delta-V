package dev.lvhung14.delta_v.feature.launches

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.lvhung14.delta_v.R
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
    private val launchesRepository: LaunchesRepository,
    @ApplicationContext private val context: Context
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
                        it.message ?: context.getString(R.string.unable_to_load_launches)
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
                                context.getString(R.string.no_cached_launches)
                            )
                        } else {
                            LaunchesUiState.Loading
                        }
                    } else {
                        val sortedLaunches = launches.sortedBy { it.net ?: Instant.MAX }
                        val cards = sortedLaunches.map { it.toCardUiState(clock, context) }
                        val lastUpdatedInstant = launches.mapNotNull { it.lastUpdated }.maxOrNull()
                        val lastUpdatedStatus = lastUpdatedInstant?.let {
                            context.getString(R.string.updated_prefix, formatRelativeTime(it, clock, context))
                        } ?: context.getString(R.string.updated_just_now)

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

private fun Launch.toCardUiState(clock: Clock, context: Context): LaunchCardUiState {
    val launchWindow = net?.let { launchWindowFormatter.format(it) } ?: context.getString(R.string.launch_window_tbd)
    val countdown = formatCountdown(net, clock, context)
    val location = buildList {
        padName?.let(::add)
        locationName?.let(::add)
        countryCode?.let { add(it.uppercase(Locale.getDefault())) }
    }.distinct().joinToString(" • ").ifBlank { context.getString(R.string.location_tbd) }

    val missionSummary = missionDescription?.takeIf { it.isNotBlank() }
        ?: context.getString(R.string.mission_summary_default)

    val statusText = status.abbreviation ?: status.name ?: context.getString(R.string.status_tbd)

    return LaunchCardUiState(
        id = id,
        title = displayName.ifBlank { missionName ?: context.getString(R.string.upcoming_mission) },
        provider = providerName ?: context.getString(R.string.launch_provider_tba),
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

private fun formatCountdown(net: Instant?, clock: Clock, context: Context): String {
    net ?: return context.getString(R.string.launch_time_tbd)
    val now = clock.instant()
    val duration = Duration.between(now, net)
    if (duration.isNegative) return context.getString(R.string.launched)

    val days = duration.toDays()
    val hours = duration.minusDays(days).toHours()
    val minutes = duration.minusDays(days).minusHours(hours).toMinutes()

    val builder = StringBuilder("T-")
    if (days > 0) builder.append("${days}d ")
    if (hours > 0 || days > 0) builder.append("${hours}h ")
    builder.append("${minutes}m")

    return builder.toString().trim()
}

private fun formatRelativeTime(instant: Instant, clock: Clock, context: Context): String {
    val duration = Duration.between(instant, clock.instant())
    val minutes = duration.toMinutes()
    return when {
        minutes < 1 -> context.getString(R.string.just_now)
        minutes < 60 -> context.getString(R.string.minutes_ago, minutes)
        minutes < 60 * 24 -> context.getString(R.string.hours_ago, duration.toHours())
        else -> context.getString(R.string.days_ago, duration.toDays())
    }
}
