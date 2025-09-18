@file:Suppress("KDocUnresolvedReference")

package br.com.arch.toolkit.storage.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import br.com.arch.toolkit.storage.core.KeyValue
import br.com.arch.toolkit.storage.core.KeyValue.Companion.map
import br.com.arch.toolkit.storage.core.KeyValue.Companion.required
import br.com.arch.toolkit.storage.core.StorageProvider
import kotlin.enums.EnumEntries

/**
 * [StorageProvider] implementation backed by AndroidX [DataStore] with [Preferences].
 *
 * [DataStoreProvider] exposes a type-safe API to access preference keys as reactive [KeyValue] entries.
 * It supports primitives, enums, and serializable models, while keeping the same contract as
 * other providers (e.g., Memory).
 *
 * ---
 *
 * ### Behavior
 * - Each primitive type is mapped to its corresponding `Preferences.Key`.
 * - Values are observed reactively through [KeyValue.get] as a [kotlinx.coroutines.flow.Flow].
 * - Writes are persisted asynchronously via [DataStore.edit].
 * - Enums are stored as strings (`name`) and mapped back to their enum entry.
 * - Models are stored as JSON strings using provided `fromJson` / `toJson` lambdas.
 *
 * ---
 *
 * ### Example: Primitives
 * ```kotlin
 * val store: DataStore<Preferences> = ...
 * val provider = DataStoreProvider(store)
 *
 * val isLoggedIn = provider.boolean("is_logged_in")
 * val userName = provider.string("user_name")
 *
 * isLoggedIn.set(true)
 * println("User: ${userName.instant()}")
 * ```
 *
 * ### Example: Enums
 * ```kotlin
 * enum class Theme { Light, Dark }
 *
 * val theme = provider.enum("theme", Theme.entries, Theme.Light)
 *
 * theme.set(Theme.Dark)
 * println("Theme is ${theme.instant()}")
 * ```
 *
 * ### Example: Models
 * ```kotlin
 * @Serializable
 * data class User(val id: String, val name: String)
 *
 * val user = provider.model(
 *     key = "user",
 *     fromJson = { Json.decodeFromString<User>(it) },
 *     toJson = { Json.encodeToString(it) }
 * )
 *
 * user.set(User("42", "Alice"))
 * println("User: ${user.instant()}")
 * ```
 *
 * ---
 *
 * ### Supported types
 * - [boolean]
 * - [byteArray]
 * - [double]
 * - [float]
 * - [int]
 * - [long]
 * - [string]
 * - [enum]
 * - [model]
 *
 * ---
 *
 * @param store The [DataStore] instance backed by [Preferences].
 *
 * @see StorageProvider For the contract implemented by this class.
 * @see DataStoreKeyValue For the internal [KeyValue] implementations used.
 */
class DataStoreProvider(private val store: DataStore<Preferences>) : StorageProvider() {

    override fun boolean(key: String): KeyValue<Boolean?> =
        DataStoreKeyValue.BooleanKV(key = key, store = store)

    override fun byteArray(key: String): KeyValue<ByteArray?> =
        DataStoreKeyValue.ByteArrayKV(key = key, store = store)

    override fun double(key: String): KeyValue<Double?> =
        DataStoreKeyValue.DoubleKV(key = key, store = store)

    override fun float(key: String): KeyValue<Float?> =
        DataStoreKeyValue.FloatKV(key = key, store = store)

    override fun int(key: String): KeyValue<Int?> =
        DataStoreKeyValue.IntKV(key = key, store = store)

    override fun long(key: String): KeyValue<Long?> =
        DataStoreKeyValue.LongKV(key = key, store = store)

    override fun string(key: String): KeyValue<String?> =
        DataStoreKeyValue.StringKV(key = key, store = store)

    override fun <T : Enum<T>> enum(
        key: String,
        entries: EnumEntries<T>,
        default: T
    ): KeyValue<T> = DataStoreKeyValue.StringKV(key = key, store = store).map(
        mapTo = { value -> entries.find { it.name.equals(value, true) } },
        mapBack = { it?.name }
    ).required { default }

    override fun <T : Any> model(
        key: String,
        fromJson: (String) -> T,
        toJson: (T) -> String
    ): KeyValue<T?> = DataStoreKeyValue.StringKV(key = key, store = store).map(
        mapTo = { value -> value?.let(fromJson) },
        mapBack = { value -> value?.let(toJson) }
    )
}
