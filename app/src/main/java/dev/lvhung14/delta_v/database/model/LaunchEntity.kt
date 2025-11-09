package dev.lvhung14.delta_v.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import dev.lvhung14.delta_v.model.Launch
import dev.lvhung14.delta_v.model.LaunchStatus
import dev.lvhung14.delta_v.network.model.NetworkLaunch
import java.time.Instant

/**
 * Room entity representing the launches table.
 *
 * We keep the DTOs, entities, and UI models separate so that a change in one layer (for example the
 * REST API adding a field) does not cascade through the rest of the app.
 */
@Entity(tableName = "launches")
data class LaunchEntity(
    @PrimaryKey val id: String, // Launch Library identifier (same as network ID)
    val displayName: String, // Title shown to users (provider + mission)
    val missionName: String?, // Short mission name if provided
    val statusName: String?, // Verbose status ("Go", "In Flight", etc.)
    val statusAbbrev: String?, // Status abbreviation ("GO", "SUC")
    val providerName: String?, // Agency/operator name
    val rocketName: String?, // Rocket configuration name
    val padName: String?, // Pad label (e.g., "LC-39A")
    val locationName: String?, // Location or spaceport name
    val countryCode: String?, // ISO country code of the pad
    val netEpochMillis: Long?, // NET ("No Earlier Than") instant stored as epoch millis
    val imageUrl: String?, // Cached hero image URL
    val detailUrl: String?, // Deep link to API or article
    val missionDescription: String?, // Cached mission synopsis
    val lastUpdatedEpochMillis: Long // When this row was last refreshed
)

/*
Example cached row:
LaunchEntity(
    id = "6232fdd6-872e",
    displayName = "Falcon 9 | Starlink 9",
    missionName = "Starlink Group 9",
    statusName = "Go",
    statusAbbrev = "GO",
    providerName = "SpaceX",
    rocketName = "Falcon 9 Block 5",
    padName = "LC-39A",
    locationName = "Kennedy Space Center",
    countryCode = "USA",
    netEpochMillis = 1762639260000,
    imageUrl = "https://images.spacedevs.com/f9.jpg",
    detailUrl = "https://ll.thespacedevs.com/launch/6232fdd6/",
    missionDescription = "Another batch of Starlink satellites.",
    lastUpdatedEpochMillis = 1762638300000
)
*/

fun LaunchEntity.asExternalModel(): Launch = Launch(
    id = id,
    displayName = displayName,
    missionName = missionName,
    providerName = providerName,
    rocketName = rocketName,
    padName = padName,
    locationName = locationName,
    countryCode = countryCode,
    net = netEpochMillis?.let(Instant::ofEpochMilli),
    imageUrl = imageUrl,
    detailUrl = detailUrl,
    missionDescription = missionDescription,
    status = LaunchStatus(
        name = statusName,
        abbreviation = statusAbbrev
    ),
    lastUpdated = lastUpdatedEpochMillis.let(Instant::ofEpochMilli)
)

fun NetworkLaunch.toEntity(currentTimeMillis: Long = System.currentTimeMillis()): LaunchEntity {
    // API delivers NET as ISO-8601 text; convert to epoch millis so Room can sort/filter quickly.
    val parsedNet = net?.let { raw ->
        runCatching { Instant.parse(raw).toEpochMilli() }.getOrNull()
    }

    val rocketName = rocket?.configuration?.fullName
        ?: rocket?.configuration?.name
        ?: rocket?.configuration?.variant

    val locationName = pad?.location?.name

    return LaunchEntity(
        id = id,
        displayName = name.orEmpty(),
        missionName = mission?.name,
        statusName = status?.name,
        statusAbbrev = status?.abbrev,
        providerName = launchServiceProvider?.name,
        rocketName = rocketName,
        padName = pad?.name,
        locationName = locationName,
        countryCode = pad?.location?.countryCode,
        netEpochMillis = parsedNet,
        imageUrl = image,
        detailUrl = url,
        missionDescription = mission?.description,
        lastUpdatedEpochMillis = currentTimeMillis
    )
}
