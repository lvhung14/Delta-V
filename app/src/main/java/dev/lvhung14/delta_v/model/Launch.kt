package dev.lvhung14.delta_v.model

import java.time.Instant

/**
 * Domain model consumed by the UI layer. Keeping this model decoupled from both the Room entity
 * and the Retrofit DTO makes migrations safer – a trick borrowed straight from Now in Android.
 */
data class Launch(
    val id: String, // Stable ID from Launch Library (e.g., "a1b2c3")
    val displayName: String, // User-facing title such as "Falcon 9 | Starlink 9"
    val missionName: String?, // Mission nickname, often shorter than displayName
    val providerName: String?, // Launch provider like "SpaceX" or "Rocket Lab"
    val rocketName: String?, // Full vehicle name, e.g., "Falcon 9 Block 5"
    val padName: String?, // Launch pad label such as "LC-39A"
    val locationName: String?, // Spaceport or region, e.g., "Kennedy Space Center"
    val countryCode: String?, // ISO country code of the pad, e.g., "USA"
    val net: Instant?, // "No Earlier Than" timestamp—earliest possible launch time
    val imageUrl: String?, // Hero image URL supplied by the API
    val detailUrl: String?, // API or article link with more mission info
    val missionDescription: String?, // Long-form description from the API
    val status: LaunchStatus, // Current status (Go, Hold, Success, etc.)
    val lastUpdated: Instant? // When we last synced this record into the DB
)

/**
 * Example:
 * Launch(
 *   id = "6232fdd6-872e",
 *   displayName = "Falcon 9 | Starlink 9",
 *   missionName = "Starlink Group 9",
 *   providerName = "SpaceX",
 *   rocketName = "Falcon 9 Block 5",
 *   padName = "LC-39A",
 *   locationName = "Kennedy Space Center",
 *   countryCode = "USA",
 *   net = Instant.parse("2025-11-08T21:01:00Z"),
 *   imageUrl = "https://images.spacedevs.com/f9.jpg",
 *   detailUrl = "https://ll.thespacedevs.com/launch/6232fdd6/",
 *   missionDescription = "Another batch of Starlink satellites.",
 *   status = LaunchStatus(name = "Go", abbreviation = "GO"),
 *   lastUpdated = Instant.parse("2025-11-08T20:45:00Z")
 * )
 */

data class LaunchStatus(
    val name: String?,
    val abbreviation: String?
)
