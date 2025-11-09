package dev.lvhung14.delta_v.feature.launches.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.lvhung14.delta_v.R
import dev.lvhung14.delta_v.feature.launches.LaunchCardUiState
import dev.lvhung14.delta_v.feature.launches.LaunchesUiState
import dev.lvhung14.delta_v.ui.theme.DeltaVTheme

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun LaunchesScreen(
    uiState: LaunchesUiState,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            LaunchesTopBar(onRefresh = onRefresh)
        }
    ) { padding ->
        when (uiState) {
            LaunchesUiState.Loading -> LoadingState(modifier = Modifier.padding(padding))
            is LaunchesUiState.Error -> ErrorState(
                message = uiState.message,
                onRefresh = onRefresh,
                modifier = Modifier.padding(padding)
            )
            is LaunchesUiState.Success -> LaunchList(
                launches = uiState.launches,
                isOffline = uiState.isOffline,
                lastUpdatedStatus = uiState.lastUpdatedStatus,
                onRefresh = onRefresh,
                modifier = Modifier.padding(padding)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LaunchesTopBar(
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        modifier = modifier,
        title = { Text(text = stringResource(R.string.app_name)) },
        actions = {
            IconButton(onClick = onRefresh) {
                Icon(
                    imageVector = Icons.Rounded.Refresh,
                    contentDescription = "Refresh launches"
                )
            }
        }
    )
}

@Composable
private fun LaunchList(
    launches: List<LaunchCardUiState>,
    isOffline: Boolean,
    lastUpdatedStatus: String,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        if (isOffline) {
            OfflineBanner(onRefresh = onRefresh)
        }

        Text(
            text = lastUpdatedStatus,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier
                .padding(vertical = 8.dp)
                .fillMaxWidth(),
            textAlign = TextAlign.Center
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(launches, key = { it.id }) { launch ->
                LaunchCard(
                    launch = launch,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun OfflineBanner(
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 12.dp),
        color = MaterialTheme.colorScheme.tertiaryContainer,
        tonalElevation = 2.dp,
        shadowElevation = 2.dp
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Text(
                text = "Offline mode",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onTertiaryContainer
            )
            Text(
                text = "You're seeing cached data. Tap refresh when you're back online.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onTertiaryContainer
            )
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = onRefresh) {
                Text("Try again")
            }
        }
    }
}

@Composable
private fun LoadingState(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun ErrorState(
    message: String,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = onRefresh) {
            Text("Retry")
        }
    }
}

@Preview
@Composable
private fun LaunchScreenPreview() {
    DeltaVTheme {
        LaunchesScreen(
            uiState = LaunchesUiState.Success(
                launches = listOf(
                    LaunchCardUiState(
                        id = "1",
                        title = "Falcon 9 | Starlink 9",
                        provider = "SpaceX",
                        location = "LC-39A • KSC • USA",
                        launchWindow = "Sat, Nov 9 • 09:00 UTC",
                        countdown = "T-2d 4h",
                        status = "Go",
                        missionSummary = "Deploying another batch of Starlink satellites.",
                        imageUrl = null,
                        detailUrl = null
                    )
                ),
                isOffline = false,
                lastUpdatedStatus = "Updated just now"
            ),
            onRefresh = {}
        )
    }
}
