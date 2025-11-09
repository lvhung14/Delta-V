package dev.lvhung14.delta_v.database.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.lvhung14.delta_v.database.DeltaVDatabase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object DatabaseModule {

    @Provides
    @Singleton
    fun providesDatabase(
        @ApplicationContext context: Context
    ): DeltaVDatabase = Room.databaseBuilder(
        context,
        DeltaVDatabase::class.java,
        "delta-v.db"
    )
        .fallbackToDestructiveMigration()
        .build()
}
