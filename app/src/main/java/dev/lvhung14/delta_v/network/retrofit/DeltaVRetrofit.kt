package dev.lvhung14.delta_v.network.retrofit

import dev.lvhung14.delta_v.network.DeltaVNetworkDataSource
import dev.lvhung14.delta_v.network.model.NetworkUpcomingLaunches
import kotlinx.serialization.Serializable
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import javax.inject.Inject
import javax.inject.Singleton

private const val BASE_URL = "https://ll.thespacedevs.com/2.3.0/"

@Serializable
private data class NetworkResponse<T> (
    val data: T
)

private interface DeltaVApiService {
    @GET("launches")
    suspend fun getUpcomingLaunches(): NetworkResponse<List<NetworkUpcomingLaunches>>
}


@Singleton
internal class DeltaVRetrofitNetwork @Inject constructor(
    gsonConverterFactory: GsonConverterFactory
): DeltaVNetworkDataSource {
    private val networkApi = Retrofit.Builder()
        .addConverterFactory(gsonConverterFactory)
        .baseUrl(BASE_URL)
        .build()
        .create(DeltaVApiService::class.java)

    override suspend fun getUpcomingLaunches(): List<NetworkUpcomingLaunches> {
        return networkApi.getUpcomingLaunches().data
    }
}