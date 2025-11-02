package dev.lvhung14.delta_v.ui.presentation.vm

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.lvhung14.delta_v.ui.presentation.state.LaunchUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class LaunchViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(LaunchUiState())
    val launchUiState: StateFlow<LaunchUiState> = _uiState.asStateFlow()
}