package dev.lvhung14.delta_v.network.model

import com.google.gson.annotations.SerializedName

/**
 * Network DTOs that mirror the Launch Library 2 schema. We only keep the fields that are
 * surfaced in the UI to avoid parsing an extremely wide payload.
 */
data class NetworkLaunchResponse(
    val count: Int? = null, // Total items available on the server
    val next: String? = null, // Pagination link for the next page
    val previous: String? = null, // Pagination link for the previous page
    val results: List<NetworkLaunch> = emptyList() // Actual launch payload
)

data class NetworkLaunch(
    val id: String, // Stable UUID like "6232fdd6-872e-4146-aae9-4f3cca740512"
    val url: String?, // Canonical API URL for this launch
    val slug: String?, // URL-friendly identifier
    val name: String?, // Full display name, e.g., "Falcon 9 | Starlink 9"
    val status: NetworkLaunchStatus?, // Current lifecycle status
    @SerializedName("last_updated")
    val lastUpdated: String?, // ISO timestamp when Launch Library last edited the launch
    val net: String?, // "No Earlier Than" timestamp (earliest possible T-0)
    val image: String?, // Hero image URL
    val infographic: String?, // Infographic/poster URL if provided
    @SerializedName("launch_service_provider")
    val launchServiceProvider: NetworkLaunchServiceProvider?, // Agency/company info
    val mission: NetworkMission?, // Mission metadata
    val pad: NetworkPad?, // Launch site data
    val rocket: NetworkRocket? // Vehicle info
)

data class NetworkLaunchStatus(
    val id: Int?, // Numeric status ID
    val name: String?, // "Go", "Success", "Hold", etc.
    val abbrev: String?, // Short form such as "GO"
    val description: String? // Detailed explanation
)

data class NetworkLaunchServiceProvider(
    val id: Int?, // Provider ID
    val url: String?, // API URL for the provider
    val name: String?, // "SpaceX", "Rocket Lab", etc.
    val type: String? // Government, Commercial, etc.
)

data class NetworkMission(
    val id: Int?, // Mission ID
    val name: String?, // Mission name
    val description: String?, // Full description
    val type: String? // Mission category (Earth Science, Communications…)
)

data class NetworkRocket(
    val id: Int?, // Rocket ID
    val configuration: NetworkRocketConfiguration? // Human-readable rocket configuration
)

data class NetworkRocketConfiguration(
    val id: Int?, // Config ID
    val name: String?, // "Falcon 9"
    val family: String?, // Vehicle family
    @SerializedName("full_name")
    val fullName: String?, // "Falcon 9 Block 5"
    val variant: String? // Specific variant name
)

data class NetworkPad(
    val id: Int?, // Pad ID
    val name: String?, // "LC-39A"
    val location: NetworkLocation?, // Parent location/spaceport
    @SerializedName("map_url")
    val mapUrl: String?, // Google Maps link
    val latitude: String?, // Latitude as string
    val longitude: String? // Longitude as string
)

data class NetworkLocation(
    val id: Int?, // Location ID
    val name: String?, // "Kennedy Space Center, FL, USA"
    @SerializedName("country_code")
    val countryCode: String?, // ISO country code
    val description: String? // Free-form description from the API
)

/*
Example payload (trimmed):
{
  "id": "6232fdd6-872e-4146-aae9-4f3cca740512",
  "slug": "falcon-9-block-5-starlink-9",
  "name": "Falcon 9 Block 5 | Starlink 9",
  "net": "2025-11-08T21:01:00Z",
  "status": { "name": "Go", "abbrev": "GO" },
  "launch_service_provider": { "name": "SpaceX", "type": "Commercial" },
  "rocket": { "configuration": { "full_name": "Falcon 9 Block 5" } },
  "pad": {
    "name": "LC-39A",
    "location": { "name": "Kennedy Space Center, FL, USA", "country_code": "USA" }
  },
  "mission": {
    "name": "Starlink Group 9",
    "description": "Another batch of Starlink satellites."
  },
  "image": "https://images.spacedevs.com/f9.jpg",
  "url": "https://ll.thespacedevs.com/launch/6232fdd6/"
}
*/
