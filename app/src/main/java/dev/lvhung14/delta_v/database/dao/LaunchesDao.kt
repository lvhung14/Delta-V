package dev.lvhung14.delta_v.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import dev.lvhung14.delta_v.database.model.LaunchEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LaunchDao {

    @Query("SELECT * FROM launches ORDER BY netEpochMillis ASC")
    fun observeLaunches(): Flow<List<LaunchEntity>>

    @Upsert
    suspend fun upsertLaunches(launches: List<LaunchEntity>)

    @Query("DELETE FROM launches")
    suspend fun deleteAll()
}
