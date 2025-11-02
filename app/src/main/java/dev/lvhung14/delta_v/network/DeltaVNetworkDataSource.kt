package dev.lvhung14.delta_v.network

import dev.lvhung14.delta_v.network.model.NetworkUpcomingLaunches

interface DeltaVNetworkDataSource {
    suspend fun getUpcomingLaunches(): List<NetworkUpcomingLaunches>
}