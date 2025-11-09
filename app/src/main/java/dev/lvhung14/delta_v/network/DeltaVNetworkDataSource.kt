package dev.lvhung14.delta_v.network

import dev.lvhung14.delta_v.network.model.NetworkLaunch

interface DeltaVNetworkDataSource {
    suspend fun getUpcomingLaunches(limit: Int = DEFAULT_PAGE_LIMIT): List<NetworkLaunch>

    companion object {
        const val DEFAULT_PAGE_LIMIT = 50
    }
}
