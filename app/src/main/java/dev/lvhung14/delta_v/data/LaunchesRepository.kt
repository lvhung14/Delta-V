package dev.lvhung14.delta_v.data

import androidx.room.withTransaction
import dev.lvhung14.delta_v.database.DeltaVDatabase
import dev.lvhung14.delta_v.database.dao.LaunchDao
import dev.lvhung14.delta_v.database.model.LaunchEntity
import dev.lvhung14.delta_v.database.model.asExternalModel
import dev.lvhung14.delta_v.database.model.toEntity
import dev.lvhung14.delta_v.di.IoDispatcher
import dev.lvhung14.delta_v.model.Launch
import dev.lvhung14.delta_v.network.DeltaVNetworkDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

interface LaunchesRepository {
    val upcomingLaunches: Flow<List<Launch>>
    suspend fun refreshUpcomingLaunches(): Result<Unit>
}

/**
 * Offline-first implementation that mirrors the approach used by the Now in Android team:
 * whenever we fetch fresh content we replace the local cache inside a DB transaction,
 * while every UI consumer observes the database and never talks to Retrofit directly.
 */
@Singleton
class OfflineFirstLaunchesRepository @Inject constructor(
    private val launchDao: LaunchDao,
    private val networkDataSource: DeltaVNetworkDataSource,
    private val database: DeltaVDatabase,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : LaunchesRepository {

    override val upcomingLaunches: Flow<List<Launch>> =
        launchDao.observeLaunches().map { launches ->
            launches.map(LaunchEntity::asExternalModel)
        }

    override suspend fun refreshUpcomingLaunches(): Result<Unit> = withContext(ioDispatcher) {
        runCatching {
            val networkLaunches = networkDataSource.getUpcomingLaunches()
            val entities = networkLaunches.map { it.toEntity() }

            // Keeping the delete + insert inside a transaction ensures readers never see a partial update.
            database.withTransaction {
                launchDao.deleteAll()
                launchDao.upsertLaunches(entities)
            }
        }
    }
}
