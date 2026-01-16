package dev.lvhung14.delta_v.feature.launches.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.lvhung14.delta_v.R
import dev.lvhung14.delta_v.feature.launches.LaunchCardUiState
import dev.lvhung14.delta_v.feature.launches.LaunchesUiState
import dev.lvhung14.delta_v.ui.theme.DeltaVTheme
import kotlinx.coroutines.delay

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun LaunchesScreen(
    uiState: LaunchesUiState,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colorScheme = MaterialTheme.colorScheme
    val backgroundBrush =
        remember(colorScheme) {
            Brush.linearGradient(
                colors =
                    listOf(
                        colorScheme.background,
                        colorScheme.primary.copy(alpha = 0.08f),
                        colorScheme.tertiary.copy(alpha = 0.06f),
                        colorScheme.background,
                    ),
            )
        }

    Box(
        modifier =
            modifier
                .fillMaxSize()
                .background(backgroundBrush),
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = { LaunchesTopBar(onRefresh = onRefresh) },
            containerColor = Color.Transparent,
            contentColor = colorScheme.onBackground,
            contentWindowInsets = WindowInsets.safeDrawing,
        ) { padding ->
            AnimatedContent(
                targetState = uiState,
                transitionSpec = {
                    fadeIn(animationSpec = tween(220)) togetherWith
                        fadeOut(animationSpec = tween(180))
                },
                label = "launches-content",
            ) { state ->
                when (state) {
                    LaunchesUiState.Loading -> {
                        LoadingState(modifier = Modifier.padding(padding))
                    }

                    is LaunchesUiState.Error -> {
                        ErrorState(
                            message = state.message,
                            onRefresh = onRefresh,
                            modifier = Modifier.padding(padding),
                        )
                    }

                    is LaunchesUiState.Success -> {
                        LaunchList(
                            launches = state.launches,
                            isOffline = state.isOffline,
                            lastUpdatedStatus = state.lastUpdatedStatus,
                            onRefresh = onRefresh,
                            modifier = Modifier.padding(padding),
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LaunchesTopBar(
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
) {
    TopAppBar(
        modifier = modifier,
        title = {
            Column {
                Text(
                    text = stringResource(R.string.app_name),
                    style = MaterialTheme.typography.titleLarge,
                )
                Text(
                    text = stringResource(R.string.upcoming_launches),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                )
            }
        },
        actions = {
            FilledTonalIconButton(onClick = onRefresh) {
                Icon(
                    imageVector = Icons.Rounded.Refresh,
                    contentDescription = stringResource(R.string.refresh_launches_content_description),
                )
            }
        },
        colors =
            TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Transparent,
                scrolledContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
            ),
    )
}

@Composable
private fun LaunchList(
    launches: List<LaunchCardUiState>,
    isOffline: Boolean,
    lastUpdatedStatus: String,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val animatedLaunchIds = remember { mutableStateMapOf<String, Boolean>() }

    Column(
        modifier =
            modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
    ) {
        AnimatedVisibility(
            visible = isOffline,
            enter = fadeIn(animationSpec = tween(220)) + expandVertically(),
            exit = fadeOut(animationSpec = tween(150)) + shrinkVertically(),
        ) {
            OfflineBanner(onRefresh = onRefresh)
        }

        Spacer(modifier = Modifier.height(12.dp))
        LastUpdatedChip(text = lastUpdatedStatus)
        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 32.dp, top = 4.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            itemsIndexed(launches, key = { _, launch -> launch.id }) { index, launch ->
                val hasAnimated = animatedLaunchIds[launch.id] == true
                LaunchedEffect(launch.id, hasAnimated) {
                    if (!hasAnimated) {
                        delay(index * 60L)
                        animatedLaunchIds[launch.id] = true
                    }
                }
                AnimatedVisibility(
                    visible = hasAnimated,
                    enter =
                        fadeIn(animationSpec = tween(240)) +
                            slideInVertically(
                                animationSpec = tween(240),
                                initialOffsetY = { it / 6 },
                            ),
                    exit = fadeOut(animationSpec = tween(120)),
                ) {
                    LaunchCard(
                        launch = launch,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
        }
    }
}

@Composable
private fun OfflineBanner(
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(top = 12.dp),
        color = MaterialTheme.colorScheme.tertiaryContainer,
        tonalElevation = 2.dp,
        shadowElevation = 2.dp,
        shape = RoundedCornerShape(16.dp),
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
        ) {
            Text(
                text = stringResource(R.string.offline_mode),
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onTertiaryContainer,
            )
            Text(
                text = stringResource(R.string.offline_message),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onTertiaryContainer,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = onRefresh) {
                Text(stringResource(R.string.try_again))
            }
        }
    }
}

@Composable
private fun LastUpdatedChip(
    text: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
    ) {
        Surface(
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f),
            shape = RoundedCornerShape(999.dp),
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
private fun LoadingState(modifier: Modifier = Modifier) {
    Box(
        modifier =
            modifier
                .fillMaxSize()
                .padding(32.dp),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun ErrorState(
    message: String,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .fillMaxSize()
                .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = onRefresh) {
            Text(stringResource(R.string.retry))
        }
    }
}

@Preview
@Composable
private fun LaunchScreenPreview() {
    DeltaVTheme {
        LaunchesScreen(
            uiState =
                LaunchesUiState.Success(
                    launches = previewLaunches(),
                    isOffline = false,
                    lastUpdatedStatus = "Updated just now",
                ),
            onRefresh = {},
        )
    }
}

@Preview(name = "Loading")
@Composable
private fun LaunchesScreenLoadingPreview() {
    DeltaVTheme {
        LaunchesScreen(
            uiState = LaunchesUiState.Loading,
            onRefresh = {},
        )
    }
}

@Preview(name = "Error")
@Composable
private fun LaunchesScreenErrorPreview() {
    DeltaVTheme {
        LaunchesScreen(
            uiState = LaunchesUiState.Error("Unable to load launches"),
            onRefresh = {},
        )
    }
}

@Preview(name = "Offline")
@Composable
private fun LaunchesScreenOfflinePreview() {
    DeltaVTheme {
        LaunchesScreen(
            uiState =
                LaunchesUiState.Success(
                    launches = previewLaunches(),
                    isOffline = true,
                    lastUpdatedStatus = "Updated 2 hours ago",
                ),
            onRefresh = {},
        )
    }
}

private fun previewLaunches(): List<LaunchCardUiState> =
    listOf(
        LaunchCardUiState(
            id = "1",
            title = "Falcon 9 | Starlink 9",
            provider = "SpaceX",
            location = "LC-39A - KSC - USA",
            launchWindow = "Sat, Nov 9 - 09:00 UTC",
            countdown = "T-2d 4h",
            status = "Go",
            missionSummary = "Deploying another batch of Starlink satellites.",
            imageUrl = null,
            detailUrl = null,
        ),
        LaunchCardUiState(
            id = "2",
            title = "Electron | Owl Night Long",
            provider = "Rocket Lab",
            location = "LC-1A - Mahia - New Zealand",
            launchWindow = "Tue, Nov 12 - 01:00 UTC",
            countdown = "T-5d 20h",
            status = "TBD",
            missionSummary = "Dedicated smallsat rideshare to low Earth orbit.",
            imageUrl = null,
            detailUrl = "https://example.com/mission",
        ),
    )
