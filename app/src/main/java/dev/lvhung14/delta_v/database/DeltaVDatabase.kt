package dev.lvhung14.delta_v.database

import androidx.room.Database
import androidx.room.RoomDatabase
import dev.lvhung14.delta_v.database.dao.LaunchDao
import dev.lvhung14.delta_v.database.model.LaunchEntity

@Database(
    entities = [LaunchEntity::class],
    version = 1,
    exportSchema = false
)
abstract class DeltaVDatabase : RoomDatabase() {
    abstract fun launchDao(): LaunchDao
}
