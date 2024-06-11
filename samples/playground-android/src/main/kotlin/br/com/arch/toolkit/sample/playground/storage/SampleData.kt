package br.com.arch.toolkit.sample.playground.storage

import br.com.arch.toolkit.storage.StorageCreator
import br.com.arch.toolkit.storage.delegate.keyValueStorage
import kotlin.reflect.KClass

class SampleData<T : Any>(private val name: String, default: T, kClass: KClass<T>) {
    private var nullableValue: T? by keyValueStorage(
        name = "nullable-$name",
        classToParse = kClass
    )
    private var value: T by keyValueStorage(
        name = name,
        default = default,
        classToParse = kClass
    )
    private var nullableListOfValue: List<T>? by keyValueStorage(
        name = "nullable-list-$name",
    )
    private var listOfValue: List<T> by keyValueStorage(
        name = "list-$name",
        default = emptyList()
    )
    private var nullableMapOfValue: Map<String, T>? by keyValueStorage(
        name = "nullable-map-$name",
    )
    private var mapOfValue: Map<String, T> by keyValueStorage(
        name = "map-$name",
        default = emptyMap()
    )

    fun asNewData() = NewData(
        nullableValue,
        value,
        nullableListOfValue,
        listOfValue,
        nullableMapOfValue,
        mapOfValue
    )

    fun applyNewData(new: NewData<T>) {
        nullableValue = new.nullableValue
        value = new.value
        nullableListOfValue = new.nullableListOfValue
        listOfValue = new.listOfValue
        nullableMapOfValue = new.nullableMapOfValue
        mapOfValue = new.mapOfValue
    }

    fun delete() {
        StorageCreator.defaultStorage().apply {
            remove(this@SampleData.name)
            remove("nullable-${this@SampleData.name}")
            remove("list-${this@SampleData.name}")
            remove("nullable-list-${this@SampleData.name}")
            remove("map-${this@SampleData.name}")
        }
    }

    override fun toString() = """
            |Actual Value:
            |
            |Nullable = $nullableValue
            |Value = $value
            |Null List = $nullableListOfValue
            |List = $listOfValue
            |Null Map = $nullableMapOfValue
            |Map = $mapOfValue
    """.trimMargin()

    class NewData<T : Any>(
        var nullableValue: T?,
        var value: T,
        var nullableListOfValue: List<T>?,
        var listOfValue: List<T>,
        var nullableMapOfValue: Map<String, T>?,
        var mapOfValue: Map<String, T>,
    ) {
        override fun toString() = """
            |Next Value:
            |
            |Nullable = $nullableValue
            |Value = $value
            |Null List = $nullableListOfValue
            |List = $listOfValue
            |Null Map = $nullableMapOfValue
            |Map = $mapOfValue
        """.trimMargin()
    }
}
