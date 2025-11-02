package dev.lvhung14.delta_v.database

import androidx.room.Database
import androidx.room.RoomDatabase
import dev.lvhung14.delta_v.database.dao.LaunchDao
import dev.lvhung14.delta_v.database.model.UpcomingLaunchEntity

@Database(entities = [UpcomingLaunchEntity::class], version = 1, exportSchema = false)
internal abstract class UpcomingLaunchDatabase(): RoomDatabase() {
    abstract fun upcomingLaunchDao(): LaunchDao
}