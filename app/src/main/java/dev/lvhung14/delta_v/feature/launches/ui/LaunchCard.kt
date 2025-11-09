package dev.lvhung14.delta_v.feature.launches.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import dev.lvhung14.delta_v.R
import dev.lvhung14.delta_v.feature.launches.LaunchCardUiState

@Composable
fun LaunchCard(
    launch: LaunchCardUiState,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val uriHandler = LocalUriHandler.current

    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        if (launch.imageUrl != null) {
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(launch.imageUrl)
                    .crossfade(true)
                    .build(),
                placeholder = painterResource(R.drawable.test_image),
                error = painterResource(R.drawable.test_image),
                contentDescription = launch.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                contentScale = ContentScale.Crop
            )
        }
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = launch.title,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = launch.provider,
                style = MaterialTheme.typography.labelMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = launch.launchWindow,
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = launch.countdown,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = launch.location,
                style = MaterialTheme.typography.bodySmall
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Status: ${launch.status}",
                style = MaterialTheme.typography.bodySmall
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = launch.missionSummary,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 4,
                overflow = TextOverflow.Ellipsis
            )
            if (launch.detailUrl != null) {
                Spacer(modifier = Modifier.height(12.dp))
                TextButton(onClick = { uriHandler.openUri(launch.detailUrl) }) {
                    Text("Mission details")
                }
            }
        }
    }
}
