package dev.lvhung14.delta_v.network.model

import kotlinx.serialization.SerialName

data class NetworkUpcomingLaunches(
    val id: String,
    val url: String,
    val name: String,
    val status: StatusResponse,
    @SerialName("last_update")
    val lastUpdated: String? = null,
    val image: ImageResponse? = null,
    val infographic: String?,
)

data class StatusResponse(
    val id: String,
    val name: String,
    val abbrev: String,
    val description: String
)

data class ImageResponse(
    val id: String,
    val name: String,
    @SerialName("image_url")
    val imageUrl: String,
    @SerialName("thumbnail_url")
    val thumbnailUrl: String,
)