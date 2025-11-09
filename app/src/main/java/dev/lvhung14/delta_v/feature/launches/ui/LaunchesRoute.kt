package dev.lvhung14.delta_v.feature.launches.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.lvhung14.delta_v.feature.launches.LaunchesUiState
import dev.lvhung14.delta_v.feature.launches.LaunchesViewModel

@Composable
fun LaunchesRoute(
    viewModel: LaunchesViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchesScreen(
        uiState = uiState,
        onRefresh = viewModel::refreshUpcomingLaunches
    )
}
