package dev.lvhung14.delta_v.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import dev.lvhung14.delta_v.database.model.UpcomingLaunchEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LaunchDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(upcomingLaunchEntity: UpcomingLaunchEntity)

    @Update
    suspend fun update(upcomingLaunchEntity: UpcomingLaunchEntity)

    @Delete
    suspend fun delete(upcomingLaunchEntity: UpcomingLaunchEntity)

    @Query("SELECT * FROM launches WHERE id = :id")
    fun getLaunch(id: Int): Flow<UpcomingLaunchEntity>

    @Query("SELECT * FROM launches ORDER BY launchTime DESC")
    fun getAllLaunches(): Flow<List<UpcomingLaunchEntity>>
}