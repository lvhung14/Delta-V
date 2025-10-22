package dev.lvhung14.delta_v.network

import dev.lvhung14.delta_v.network.model.LaunchesResponse
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET

private const val BASE_URL = "https://ll.thespacedevs.com/2.3.0/"

private val retrofit = Retrofit.Builder()
    .addConverterFactory(GsonConverterFactory.create())
    .baseUrl(BASE_URL)
    .build()

interface DeltaVApiService {
    @GET("launches")
    suspend fun getAllLaunches(): LaunchesResponse
}

object DeltaVApi {
    val retrofitService: DeltaVApiService by lazy {
        retrofit.create(DeltaVApiService::class.java)
    }
}