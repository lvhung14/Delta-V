package dev.lvhung14.delta_v.feature.launches.ui

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.rounded.FilterList
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material.icons.rounded.Newspaper
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.NotificationsNone
import androidx.compose.material.icons.rounded.PlayCircle
import androidx.compose.material.icons.rounded.Rocket
import androidx.compose.material.icons.rounded.RocketLaunch
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Today
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import dev.lvhung14.delta_v.ui.theme.AccentNeon
import dev.lvhung14.delta_v.ui.theme.CardDark
import dev.lvhung14.delta_v.ui.theme.DeepSpaceBlack
import dev.lvhung14.delta_v.ui.theme.DeltaVTheme
import dev.lvhung14.delta_v.ui.theme.MarsOrange
import dev.lvhung14.delta_v.ui.theme.SlateDark
import dev.lvhung14.delta_v.ui.theme.SlateLight
import dev.lvhung14.delta_v.ui.theme.SlateMedium
import kotlinx.coroutines.delay

// Data classes for the screen
data class FeaturedLaunch(
    val id: String,
    val missionName: String,
    val rocketName: String,
    val launchSite: String,
    val imageUrl: String?,
    val targetTimeMillis: Long,
    val streamUrl: String?
)

data class MissionItem(
    val id: String,
    val missionName: String,
    val rocketName: String,
    val launchSite: String,
    val imageUrl: String?,
    val date: String,
    val time: String,
    val isComplete: Boolean = false,
    val hasNotificationEnabled: Boolean = false
)

data class CountdownTime(
    val days: Long,
    val hours: Long,
    val minutes: Long,
    val seconds: Long
)

@Composable
fun UpcomingLaunchCalendarScreen(
    featuredLaunch: FeaturedLaunch,
    missionManifest: List<MissionItem>,
    onMenuClick: () -> Unit = {},
    onFilterClick: () -> Unit = {},
    onWatchLiveClick: (FeaturedLaunch) -> Unit = {},
    onViewAllClick: () -> Unit = {},
    onMissionClick: (MissionItem) -> Unit = {},
    onNotificationToggle: (MissionItem) -> Unit = {},
    selectedNavItem: Int = 0,
    onNavItemClick: (Int) -> Unit = {},
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DeepSpaceBlack)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 100.dp) // Space for bottom nav
        ) {
            // Hero Section with Featured Launch
            FeaturedLaunchSection(
                launch = featuredLaunch,
                onMenuClick = onMenuClick,
                onFilterClick = onFilterClick,
                onWatchLiveClick = onWatchLiveClick
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Mission Manifest Section
            MissionManifestSection(
                missions = missionManifest,
                onViewAllClick = onViewAllClick,
                onMissionClick = onMissionClick,
                onNotificationToggle = onNotificationToggle
            )
        }

        // Bottom Navigation Bar
        BottomNavBar(
            selectedIndex = selectedNavItem,
            onItemClick = onNavItemClick,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
private fun FeaturedLaunchSection(
    launch: FeaturedLaunch,
    onMenuClick: () -> Unit,
    onFilterClick: () -> Unit,
    onWatchLiveClick: (FeaturedLaunch) -> Unit
) {
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .padding(top = 8.dp)
    ) {
        // Main Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(4f / 5f)
                .clip(RoundedCornerShape(24.dp))
                .border(
                    width = 1.dp,
                    color = Color.White.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(24.dp)
                )
                .shadow(
                    elevation = 16.dp,
                    spotColor = MarsOrange.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(24.dp)
                )
        ) {
            // Background Image
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(launch.imageUrl)
                    .crossfade(true)
                    .build(),
                placeholder = ColorPainter(CardDark),
                error = ColorPainter(CardDark),
                contentDescription = launch.missionName,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            // Gradient Overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Black.copy(alpha = 0.3f),
                                Color.Black.copy(alpha = 0.4f),
                                DeepSpaceBlack
                            ),
                            startY = 0f,
                            endY = Float.POSITIVE_INFINITY
                        )
                    )
            )

            // Header with navigation
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .align(Alignment.TopCenter)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Black.copy(alpha = 0.8f),
                                Color.Transparent
                            )
                        )
                    )
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onMenuClick) {
                    Icon(
                        imageVector = Icons.Rounded.Menu,
                        contentDescription = "Menu",
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Text(
                    text = "LAUNCH TRACKER",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp
                    ),
                    color = Color.White.copy(alpha = 0.9f)
                )

                IconButton(onClick = onFilterClick) {
                    Icon(
                        imageVector = Icons.Rounded.FilterList,
                        contentDescription = "Filter",
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            // T-Minus Badge
            TMinusBadge(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 72.dp, end = 16.dp)
            )

            // Content at bottom
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(24.dp)
            ) {
                // Mission Label
                Text(
                    text = "NEXT MISSION",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 3.sp
                    ),
                    color = AccentNeon
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Mission Name
                Text(
                    text = launch.missionName,
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Rocket & Location
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Rounded.RocketLaunch,
                        contentDescription = null,
                        tint = SlateLight,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${launch.rocketName} • ${launch.launchSite}",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Medium
                        ),
                        color = SlateLight
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Countdown Timer
                CountdownTimer(targetTimeMillis = launch.targetTimeMillis)

                Spacer(modifier = Modifier.height(20.dp))

                // Watch Live Button
                Button(
                    onClick = { onWatchLiveClick(launch) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .shadow(
                            elevation = 12.dp,
                            spotColor = MarsOrange.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(12.dp)
                        ),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MarsOrange,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.PlayCircle,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Watch Live Stream",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun TMinusBadge(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "ping")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.75f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "ping-alpha"
    )

    Row(
        modifier = modifier
            .background(
                color = Color.Black.copy(alpha = 0.4f),
                shape = RoundedCornerShape(999.dp)
            )
            .border(
                width = 1.dp,
                color = Color.White.copy(alpha = 0.1f),
                shape = RoundedCornerShape(999.dp)
            )
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Pulsing dot
        Box(contentAlignment = Alignment.Center) {
            // Ping effect
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .alpha(alpha)
                    .background(MarsOrange, CircleShape)
            )
            // Solid dot
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .background(MarsOrange, CircleShape)
            )
        }

        Text(
            text = "T-MINUS",
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            ),
            color = Color.White
        )
    }
}

@Composable
private fun CountdownTimer(
    targetTimeMillis: Long,
    modifier: Modifier = Modifier
) {
    var countdown by remember { mutableLongStateOf(0L) }

    LaunchedEffect(targetTimeMillis) {
        while (true) {
            val now = System.currentTimeMillis()
            countdown = maxOf(0, targetTimeMillis - now)
            delay(1000)
        }
    }

    val time = remember(countdown) {
        val totalSeconds = countdown / 1000
        CountdownTime(
            days = totalSeconds / 86400,
            hours = (totalSeconds % 86400) / 3600,
            minutes = (totalSeconds % 3600) / 60,
            seconds = totalSeconds % 60
        )
    }

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        CountdownUnit(value = time.days, label = "Days", modifier = Modifier.weight(1f))
        CountdownUnit(value = time.hours, label = "Hrs", modifier = Modifier.weight(1f))
        CountdownUnit(value = time.minutes, label = "Mins", modifier = Modifier.weight(1f))
        CountdownUnit(
            value = time.seconds,
            label = "Secs",
            isHighlighted = true,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun CountdownUnit(
    value: Long,
    label: String,
    isHighlighted: Boolean = false,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .background(
                color = Color.White.copy(alpha = 0.1f),
                shape = RoundedCornerShape(12.dp)
            )
            .border(
                width = 1.dp,
                color = Color.White.copy(alpha = 0.05f),
                shape = RoundedCornerShape(12.dp)
            )
            .then(
                if (isHighlighted) {
                    Modifier.background(
                        color = MarsOrange.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(12.dp)
                    )
                } else Modifier
            )
            .padding(vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = String.format("%02d", value),
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = (-1).sp
            ),
            color = if (isHighlighted) AccentNeon else Color.White
        )
        Text(
            text = label.uppercase(),
            style = MaterialTheme.typography.labelSmall.copy(
                letterSpacing = 1.sp
            ),
            color = SlateMedium
        )
    }
}

@Composable
private fun MissionManifestSection(
    missions: List<MissionItem>,
    onViewAllClick: () -> Unit,
    onMissionClick: (MissionItem) -> Unit,
    onNotificationToggle: (MissionItem) -> Unit
) {
    Column(
        modifier = Modifier.padding(horizontal = 16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Mission Manifest",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = Color.White
            )
            Text(
                text = "VIEW ALL",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                ),
                color = MarsOrange,
                modifier = Modifier.clickable { onViewAllClick() }
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Mission List
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            missions.forEach { mission ->
                MissionCard(
                    mission = mission,
                    onClick = { onMissionClick(mission) },
                    onNotificationToggle = { onNotificationToggle(mission) }
                )
            }
        }
    }
}

@Composable
private fun MissionCard(
    mission: MissionItem,
    onClick: () -> Unit,
    onNotificationToggle: () -> Unit
) {
    val context = LocalContext.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = CardDark,
                shape = RoundedCornerShape(16.dp)
            )
            .border(
                width = 1.dp,
                color = Color.White.copy(alpha = 0.05f),
                shape = RoundedCornerShape(16.dp)
            )
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Mission Image with optional checkmark
        Box {
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(mission.imageUrl)
                    .crossfade(true)
                    .build(),
                placeholder = ColorPainter(SlateDark),
                error = ColorPainter(SlateDark),
                contentDescription = mission.missionName,
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .border(
                        width = 1.dp,
                        color = Color.White.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(12.dp)
                    ),
                contentScale = ContentScale.Crop
            )

            if (mission.isComplete) {
                Box(
                    modifier = Modifier
                        .size(18.dp)
                        .align(Alignment.BottomEnd)
                        .offset(x = 4.dp, y = 4.dp)
                        .background(Color.Black, CircleShape)
                        .padding(2.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Completed",
                        tint = Color.White,
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color(0xFF22C55E), CircleShape)
                            .padding(2.dp)
                    )
                }
            }
        }

        // Mission Details
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = mission.missionName,
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = Color.White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "${mission.rocketName} • ${mission.launchSite}",
                style = MaterialTheme.typography.bodySmall,
                color = SlateMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        // Date/Time
        Column(
            horizontalAlignment = Alignment.End
        ) {
            Text(
                text = mission.date,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = Color.White
            )
            Text(
                text = mission.time,
                style = MaterialTheme.typography.bodySmall,
                color = SlateDark
            )
        }

        // Notification Toggle
        IconButton(
            onClick = onNotificationToggle,
            modifier = Modifier
                .size(32.dp)
                .then(
                    if (mission.hasNotificationEnabled) {
                        Modifier.background(
                            color = MarsOrange.copy(alpha = 0.1f),
                            shape = CircleShape
                        )
                    } else Modifier
                )
        ) {
            Icon(
                imageVector = if (mission.hasNotificationEnabled) {
                    Icons.Rounded.Notifications
                } else {
                    Icons.Rounded.NotificationsNone
                },
                contentDescription = if (mission.hasNotificationEnabled) {
                    "Disable notifications"
                } else {
                    "Enable notifications"
                },
                tint = if (mission.hasNotificationEnabled) MarsOrange else SlateDark,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun BottomNavBar(
    selectedIndex: Int,
    onItemClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val navItems = listOf(
        NavItem(Icons.Rounded.Today, "Launch"),
        NavItem(Icons.Rounded.Rocket, "Vehicles"),
        NavItem(Icons.Rounded.Newspaper, "News"),
        NavItem(Icons.Rounded.Settings, "Settings")
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 24.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .background(
                    color = CardDark.copy(alpha = 0.8f),
                    shape = RoundedCornerShape(16.dp)
                )
                .border(
                    width = 1.dp,
                    color = Color.White.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            navItems.forEachIndexed { index, item ->
                NavItemButton(
                    icon = item.icon,
                    label = item.label,
                    isSelected = index == selectedIndex,
                    onClick = { onItemClick(index) }
                )
            }
        }
    }
}

private data class NavItem(
    val icon: ImageVector,
    val label: String
)

@Composable
private fun NavItemButton(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .clickable { onClick() }
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (isSelected) MarsOrange else SlateDark,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
            ),
            color = if (isSelected) MarsOrange else SlateDark
        )
    }
}

// Preview
@Preview(showBackground = true, backgroundColor = 0xFF050A14)
@Composable
private fun UpcomingLaunchCalendarScreenPreview() {
    val featuredLaunch = FeaturedLaunch(
        id = "1",
        missionName = "Artemis II",
        rocketName = "SLS Block 1",
        launchSite = "Kennedy Space Center",
        imageUrl = "https://images.unsplash.com/photo-1516849841032-87cbac4d88f7",
        targetTimeMillis = System.currentTimeMillis() + (4L * 24 * 60 * 60 * 1000) +
                (12L * 60 * 60 * 1000) + (33L * 60 * 1000) + (45L * 1000),
        streamUrl = "https://youtube.com"
    )

    val missions = listOf(
        MissionItem(
            id = "2",
            missionName = "Starlink 7-1",
            rocketName = "Falcon 9",
            launchSite = "Cape Canaveral",
            imageUrl = null,
            date = "Oct 24",
            time = "10:00 AM",
            isComplete = true,
            hasNotificationEnabled = false
        ),
        MissionItem(
            id = "3",
            missionName = "ESA Euclid",
            rocketName = "Ariane 62",
            launchSite = "French Guiana",
            imageUrl = null,
            date = "Nov 02",
            time = "04:30 PM",
            isComplete = false,
            hasNotificationEnabled = true
        )
    )

    DeltaVTheme(dynamicColor = false) {
        UpcomingLaunchCalendarScreen(
            featuredLaunch = featuredLaunch,
            missionManifest = missions,
            selectedNavItem = 0
        )
    }
}
