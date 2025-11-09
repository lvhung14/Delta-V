package dev.lvhung14.delta_v.feature.launches

data class LaunchCardUiState(
    val id: String,
    val title: String,
    val provider: String,
    val location: String,
    val launchWindow: String,
    val countdown: String,
    val status: String,
    val missionSummary: String,
    val imageUrl: String?,
    val detailUrl: String?
)

sealed interface LaunchesUiState {
    data object Loading : LaunchesUiState
    data class Error(val message: String) : LaunchesUiState
    data class Success(
        val launches: List<LaunchCardUiState>,
        val isOffline: Boolean,
        val lastUpdatedStatus: String
    ) : LaunchesUiState
}
