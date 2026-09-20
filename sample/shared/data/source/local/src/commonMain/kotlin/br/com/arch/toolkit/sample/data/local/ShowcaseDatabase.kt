package br.com.arch.toolkit.sample.data.local

import androidx.room.ConstructedBy
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Query
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.room.Transaction
import androidx.room.Upsert
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow

@Entity(primaryKeys = ["owner", "name"])
data class ViewedRepositoryEntity(val owner: String, val name: String, val viewedAtMillis: Long)

@Dao
interface ViewedRepositoryDao {
    @Query("SELECT * FROM ViewedRepositoryEntity ORDER BY viewedAtMillis DESC, owner, name LIMIT 20")
    fun observe(): Flow<List<ViewedRepositoryEntity>>

    @Upsert
    suspend fun upsert(item: ViewedRepositoryEntity)

    @Query("DELETE FROM ViewedRepositoryEntity WHERE owner || '/' || name NOT IN (SELECT owner || '/' || name FROM ViewedRepositoryEntity ORDER BY viewedAtMillis DESC, owner, name LIMIT 20)")
    suspend fun trim()

    @Transaction
    suspend fun remember(item: ViewedRepositoryEntity) {
        upsert(item)
        trim()
    }
}

@Database(entities = [ViewedRepositoryEntity::class], version = 1)
@ConstructedBy(ShowcaseDatabaseConstructor::class)
abstract class ShowcaseDatabase : RoomDatabase() {
    abstract fun viewedRepositories(): ViewedRepositoryDao
}

@Suppress("KotlinNoActualForExpect")
expect object ShowcaseDatabaseConstructor : RoomDatabaseConstructor<ShowcaseDatabase> {
    override fun initialize(): ShowcaseDatabase
}

expect fun databaseBuilder(): RoomDatabase.Builder<ShowcaseDatabase>

fun createShowcaseDatabase(): ShowcaseDatabase = databaseBuilder()
    .setDriver(BundledSQLiteDriver())
    .setQueryCoroutineContext(Dispatchers.IO)
    .build()
