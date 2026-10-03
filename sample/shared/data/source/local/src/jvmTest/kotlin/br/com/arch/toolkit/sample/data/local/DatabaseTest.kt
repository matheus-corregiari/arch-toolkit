package br.com.arch.toolkit.sample.data.local

import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import java.nio.file.Files
import kotlin.test.Test
import kotlin.test.assertEquals

class DatabaseTest {
    @Test
    fun historyIsBoundedAndSurvivesReopening() = runTest {
        val directory = Files.createTempDirectory("showcase-room").toFile()
        fun open() = Room.databaseBuilder<ShowcaseDatabase>(
            name = directory.resolve("history.db").absolutePath
        ).setDriver(BundledSQLiteDriver()).setQueryCoroutineContext(Dispatchers.IO).build()
        try {
            val first = open()
            try {
                val db = first
                repeat(25) { index ->
                    db.viewedRepositories().remember(
                        ViewedRepositoryEntity("owner", "$index", index.toLong())
                    )
                }
                assertEquals(20, db.viewedRepositories().observe().first().size)
            } finally {
                first.close()
            }
            val second = open()
            try {
                val db = second
                val rows = db.viewedRepositories().observe().first()
                assertEquals("24", rows.first().name)
                assertEquals("5", rows.last().name)
                db.viewedRepositories().remember(ViewedRepositoryEntity("owner", "5", 30))
                assertEquals("5", db.viewedRepositories().observe().first().first().name)
                assertEquals(20, db.viewedRepositories().observe().first().size)
            } finally {
                second.close()
            }
        } finally {
            directory.deleteRecursively()
        }
    }
}
