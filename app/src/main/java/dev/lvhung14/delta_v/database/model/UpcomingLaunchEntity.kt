package dev.lvhung14.delta_v.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "launches")
data class UpcomingLaunchEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val name: String,
    val provider: String,
    val location: String,
    val launchComplexName: String,
    val launchTime: String,
    val launchResult: Boolean
)