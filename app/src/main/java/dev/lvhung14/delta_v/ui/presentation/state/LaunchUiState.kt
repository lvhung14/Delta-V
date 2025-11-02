package dev.lvhung14.delta_v.ui.presentation.state

data class LaunchUiState (
    val name: String = "",
    val provider: String = "",
    val location: String = "",
    val launchComplexName: String = "",
    val launchTime: String = "",
    val launchResult: Boolean = false
)