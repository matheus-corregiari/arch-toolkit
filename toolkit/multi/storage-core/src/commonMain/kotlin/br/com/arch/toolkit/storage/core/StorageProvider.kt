package br.com.arch.toolkit.storage.core

import br.com.arch.toolkit.storage.core.KeyValue.Companion.map
import br.com.arch.toolkit.storage.core.KeyValue.Companion.required
import br.com.arch.toolkit.storage.core.StorageProvider.Defaults.defaultJson
import kotlinx.serialization.json.Json
import kotlin.enums.EnumEntries
import kotlin.enums.enumEntries

/**
 * Abstract factory for creating [KeyValue] entries across different types.
 *
 * A [StorageProvider] defines the contract for storage backends. Concrete
 * implementations (e.g., DataStore, in-memory, file-based) must implement this class
 * to provide type-safe access to persisted or cached values.
 *
 * ---
 *
 * ### Core responsibilities
 * - Expose [KeyValue] entries for primitive types (Boolean, Int, String, etc.).
 * - Provide helpers for working with [Enum] values and serializable models.
 * - Handle serialization/deserialization with configurable [Json].
 *
 * ---
 *
 * ### Example: Using primitive keys
 * ```kotlin
 * val provider: StorageProvider = DataStoreProvider(store)
 *
 * val isLoggedIn: KeyValue<Boolean?> = provider.boolean("is_logged_in")
 * val userName: KeyValue<String?> = provider.string("user_name")
 *
 * isLoggedIn.set(true)
 * println("User: ${userName.instant()}")
 * ```
 *
 * ### Example: Enum support
 * ```kotlin
 * enum class Theme { Light, Dark }
 *
 * val theme: KeyValue<Theme> = provider.enum("theme", Theme.entries, Theme.Light)
 *
 * theme.set(Theme.Dark)
 * println("Theme is now ${theme.instant()}")
 * ```
 *
 * ### Example: Model serialization
 * ```kotlin
 * @Serializable
 * data class User(val id: String, val name: String)
 *
 * val user: KeyValue<User?> = provider.model("user")
 *
 * user.set(User("42", "Alice"))
 *
 * lifecycleScope.launch {
 *     user.get().collect { println("Current user: $it") }
 * }
 * ```
 *
 * ---
 *
 * ### Default JSON configuration
 * By default, [model] serialization uses [defaultJson], which is preconfigured with:
 * - `ignoreUnknownKeys = true`
 * - `encodeDefaults = true`
 * - `prettyPrint = true`
 *
 * You can override this globally:
 * ```kotlin
 * StorageProvider.json(Json { ignoreUnknownKeys = false })
 * ```
 *
 * @see KeyValue For the reactive entry abstraction.
 * @see KeyValue.required To enforce non-null values.
 * @see KeyValue.map To transform between types.
 */
@StorageApi
@Suppress("TooManyFunctions")
abstract class StorageProvider {

    @StorageApi
    abstract fun boolean(key: String): KeyValue<Boolean?>

    @StorageApi
    abstract fun byteArray(key: String): KeyValue<ByteArray?>

    @StorageApi
    abstract fun double(key: String): KeyValue<Double?>

    @StorageApi
    abstract fun float(key: String): KeyValue<Float?>

    @StorageApi
    abstract fun int(key: String): KeyValue<Int?>

    @StorageApi
    abstract fun long(key: String): KeyValue<Long?>

    @StorageApi
    abstract fun string(key: String): KeyValue<String?>

    @StorageApi
    abstract fun <T : Enum<T>> enum(key: String, entries: EnumEntries<T>, default: T): KeyValue<T>

    @StorageApi
    abstract fun <T : Any> model(
        key: String,
        fromJson: (String) -> T,
        toJson: (T) -> String
    ): KeyValue<T?>

    @StorageApi
    inline fun <reified T : Any> model(
        key: String,
        json: Json = defaultJson
    ) = model<T>(key, json::decodeFromString, json::encodeToString)

    @StorageApi
    inline fun <reified T : Enum<T>> enum(
        key: String,
        default: T
    ) = enum(key, enumEntries<T>(), default)

    companion object Defaults {
        var defaultJson: Json
            private set

        init {
            defaultJson = Json {
                ignoreUnknownKeys = true
                encodeDefaults = true
                prettyPrint = true
            }
        }

        fun json(json: Json) = apply { defaultJson = json }
    }
}
