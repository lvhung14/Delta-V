package dev.lvhung14.delta_v.network.retrofit

import dev.lvhung14.delta_v.network.DeltaVNetworkDataSource
import dev.lvhung14.delta_v.network.model.NetworkLaunch
import dev.lvhung14.delta_v.network.model.NetworkLaunchResponse
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Query
import javax.inject.Inject
import javax.inject.Singleton

private interface LaunchLibraryApi {
    @GET("launch/upcoming/")
    suspend fun getUpcomingLaunches(
        @Query("limit") limit: Int,
        @Query("mode") mode: String = "detailed"
    ): NetworkLaunchResponse
}


@Singleton
internal class DeltaVRetrofitNetwork @Inject constructor(
    retrofit: Retrofit
) : DeltaVNetworkDataSource {
    private val networkApi = retrofit.create(LaunchLibraryApi::class.java)

    override suspend fun getUpcomingLaunches(limit: Int): List<NetworkLaunch> {
        return networkApi.getUpcomingLaunches(limit = limit).results
    }
}
