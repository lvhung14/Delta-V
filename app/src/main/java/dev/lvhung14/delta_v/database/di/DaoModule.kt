package dev.lvhung14.delta_v.database.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.lvhung14.delta_v.database.UpcomingLaunchDatabase
import dev.lvhung14.delta_v.database.dao.LaunchDao

@Module
@InstallIn(SingletonComponent::class)
internal object DaoModule {
    @Provides
    fun providesLaunchesDao(database: UpcomingLaunchDatabase): LaunchDao =
        database.upcomingLaunchDao()
}