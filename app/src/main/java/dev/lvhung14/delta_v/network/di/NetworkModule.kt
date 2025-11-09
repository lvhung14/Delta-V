package dev.lvhung14.delta_v.network.di

import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.lvhung14.delta_v.BuildConfig
import dev.lvhung14.delta_v.network.DeltaVNetworkDataSource
import dev.lvhung14.delta_v.network.retrofit.DeltaVRetrofitNetwork
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object NetworkModule {

    @Provides
    @Singleton
    fun providesGsonConverter(): GsonConverterFactory =
        GsonConverterFactory.create(
            GsonBuilder()
                .setLenient()
                .create()
        )

    @Provides
    @Singleton
    fun providesOkHttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.BASIC
            }
        }

        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()
    }

    @Provides
    @Singleton
    fun providesRetrofit(
        gsonConverterFactory: GsonConverterFactory,
        okHttpClient: OkHttpClient
    ): Retrofit = Retrofit.Builder()
        .baseUrl("https://ll.thespacedevs.com/2.2.0/")
        .addConverterFactory(gsonConverterFactory)
        .client(okHttpClient)
        .build()

    @Provides
    @Singleton
    fun providesNetworkDataSource(
        retrofitNetwork: DeltaVRetrofitNetwork
    ): DeltaVNetworkDataSource = retrofitNetwork
}
