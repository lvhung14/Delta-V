package dev.lvhung14.delta_v.database.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.lvhung14.delta_v.database.DeltaVDatabase
import dev.lvhung14.delta_v.database.dao.LaunchDao
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object DaoModule {
    @Provides
    @Singleton
    fun providesLaunchesDao(database: DeltaVDatabase): LaunchDao =
        database.launchDao()
}
