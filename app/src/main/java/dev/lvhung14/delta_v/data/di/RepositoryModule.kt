package dev.lvhung14.delta_v.data.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.lvhung14.delta_v.data.LaunchesRepository
import dev.lvhung14.delta_v.data.OfflineFirstLaunchesRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindsLaunchesRepository(
        repository: OfflineFirstLaunchesRepository
    ): LaunchesRepository
}
