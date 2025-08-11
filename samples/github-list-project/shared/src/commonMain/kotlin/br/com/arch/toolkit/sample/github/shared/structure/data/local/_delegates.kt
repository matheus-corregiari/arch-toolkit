package br.com.arch.toolkit.sample.github.shared.structure.data.local

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import kotlinx.coroutines.CoroutineScope
import org.koin.compose.koinInject
import org.koin.core.qualifier.named
import kotlin.enums.enumEntries

@Composable
fun dataStoreByteArray(
    key: String,
    default: ByteArray? = null,
    scope: CoroutineScope? = null,
) = ByteArrayKeyValue(
    key = key,
    default = default,
    store = koinInject(named(DEFAULT_DATA_STORE)),
).asMutableState(scope)

@Composable
fun dataStoreBoolean(
    key: String,
    default: Boolean? = null,
    scope: CoroutineScope? = null,
) = BooleanKeyValue(
    key = key,
    default = default,
    store = koinInject(named(DEFAULT_DATA_STORE)),
).asMutableState(scope)

@Composable
fun dataStoreDouble(
    key: String,
    default: Double? = null,
    scope: CoroutineScope? = null,
) = DoubleKeyValue(
    key = key,
    default = default,
    store = koinInject(named(DEFAULT_DATA_STORE)),
).asMutableState(scope)

@Composable
fun dataStoreFloat(
    key: String,
    default: Float? = null,
    scope: CoroutineScope? = null,
) = FloatKeyValue(
    key = key,
    default = default,
    store = koinInject(named(DEFAULT_DATA_STORE)),
).asMutableState(scope)

@Composable
fun dataStoreInt(
    key: String,
    default: Int? = null,
    scope: CoroutineScope? = null,
) = IntKeyValue(
    key = key,
    default = default,
    store = koinInject(named(DEFAULT_DATA_STORE)),
).asMutableState(scope)

@Composable
fun dataStoreLong(
    key: String,
    default: Long? = null,
    scope: CoroutineScope? = null,
) = LongKeyValue(
    key = key,
    default = default,
    store = koinInject(named(DEFAULT_DATA_STORE)),
).asMutableState(scope)

@Composable
fun dataStoreString(
    key: String,
    default: String? = null,
    scope: CoroutineScope? = null,
) = StringKeyValue(
    key = key,
    default = default,
    store = koinInject(named(DEFAULT_DATA_STORE)),
).asMutableState(scope)

@Composable
inline fun <reified T : Any> dataStore(
    key: String,
    default: T? = null,
    scope: CoroutineScope? = null,
) = ObjectKeyValue.Companion(
    key = key,
    default = default,
    store = koinInject(named(DEFAULT_DATA_STORE)),
    json = koinInject()
).asMutableState(scope)

@Composable
inline fun <reified T : Enum<T>> dataStoreEnum(
    key: String,
    default: T,
    scope: CoroutineScope? = null,
): MutableState<T> = EnumKeyValue(
    key = key,
    default = default,
    all = enumEntries(),
    store = koinInject(named(DEFAULT_DATA_STORE)),
).asMutableState(scope)
