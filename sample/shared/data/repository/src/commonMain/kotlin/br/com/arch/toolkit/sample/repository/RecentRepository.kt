package br.com.arch.toolkit.sample.repository

import br.com.arch.toolkit.sample.data.local.ViewedRepositoryDao
import br.com.arch.toolkit.sample.data.local.ViewedRepositoryEntity
import kotlinx.coroutines.flow.map
import kotlin.time.Clock

data class RecentRepositoryRO(val owner: String, val name: String)

/** Local, bounded history of successfully opened repositories; no background sync. */
class RecentRepository(private val dao: ViewedRepositoryDao) {
    val items = dao.observe().map { rows -> rows.map { RecentRepositoryRO(it.owner, it.name) } }

    suspend fun remember(owner: String, name: String) {
        dao.remember(ViewedRepositoryEntity(owner, name, Clock.System.now().toEpochMilliseconds()))
    }
}
