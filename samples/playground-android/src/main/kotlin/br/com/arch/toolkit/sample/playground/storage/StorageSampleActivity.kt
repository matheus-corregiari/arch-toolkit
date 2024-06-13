@file:Suppress("MagicNumber")

package br.com.arch.toolkit.sample.playground.storage

import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import br.com.arch.toolkit.delegate.viewProvider
import br.com.arch.toolkit.sample.playground.R
import br.com.arch.toolkit.sample.playground.statemachine.BaseActivity
import br.com.arch.toolkit.storage.ComplexDataParser
import br.com.arch.toolkit.storage.Storage
import com.google.gson.Gson
import kotlinx.coroutines.launch
import java.util.UUID
import kotlin.random.Random
import kotlin.reflect.KClass
import kotlin.time.Duration.Companion.minutes

class StorageSampleActivity : BaseActivity(R.layout.activity_storage_sample) {

    // Primitive
    private val intData = SampleData("int", 0, Int::class)
    private val longData = SampleData("long", 0L, Long::class)
    private val doubleData = SampleData("double", 0.0, Double::class)
    private val floatData = SampleData("float", 0.0f, Float::class)
    private val stringData = SampleData("string", "string", String::class)
    private val booleanData = SampleData("boolean", false, Boolean::class)

    // Complex
    private val enumData = SampleData("TestEnum", TestEnum.ONE, TestEnum::class)
    private val classData = SampleData("TestClass", TestClass("test"), TestClass::class)

    //region Views
    private val intDataView: StorageValidationView by viewProvider(R.id.int_data)
    private val longDataView: StorageValidationView by viewProvider(R.id.long_data)
    private val doubleDataView: StorageValidationView by viewProvider(R.id.double_data)
    private val floatDataView: StorageValidationView by viewProvider(R.id.float_data)
    private val stringDataView: StorageValidationView by viewProvider(R.id.string_data)
    private val booleanDataView: StorageValidationView by viewProvider(R.id.boolean_data)
    private val enumDataView: StorageValidationView by viewProvider(R.id.enum_data)
    private val classDataView: StorageValidationView by viewProvider(R.id.class_data)
    //endregion

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Storage.Settings.setDefaultStorage(Storage.KeyValue.encrypted)
        Storage.Settings.setDefaultThreshold(2.minutes)
        Storage.Settings.setComplexDataParser(object : ComplexDataParser {
            val gson = Gson()

            override fun <T : Any> fromJson(json: String, classToParse: KClass<T>) =
                gson.fromJson(json, classToParse.java)

            override fun <T : Any> toJson(data: T): String = gson.toJson(data)
        })

        lifecycleScope.launch {
            // Primitive
            intDataView.setData(intData, ::generateInt)
            longDataView.setData(longData, ::generateLong)
            doubleDataView.setData(doubleData, ::generateDouble)
            floatDataView.setData(floatData, ::generateFloat)
            stringDataView.setData(stringData, ::generateString)
            booleanDataView.setData(booleanData, ::generateBoolean)

            // Complex
            enumDataView.setData(enumData, ::generateEnum)
            classDataView.setData(classData, ::generateClass)
        }
    }

    private fun generateInt(): SampleData.NewData<Int> {
        fun randomData() = Random.nextInt(5000)
        return generate(::randomData)
    }

    private fun generateLong(): SampleData.NewData<Long> {
        fun randomData() = Random.nextLong(5000)
        return generate(::randomData)
    }

    private fun generateDouble(): SampleData.NewData<Double> {
        fun randomData() = Random.nextDouble(5000.0)
        return generate(::randomData)
    }

    private fun generateFloat(): SampleData.NewData<Float> {
        fun randomData() = Random.nextFloat()
        return generate(::randomData)
    }

    private fun generateString(): SampleData.NewData<String> {
        fun randomData() = UUID.randomUUID().toString().slice(0..3)
        return generate(::randomData)
    }

    private fun generateBoolean(): SampleData.NewData<Boolean> {
        fun randomData() = Random.nextBoolean()
        return generate(::randomData)
    }

    private fun generateEnum(): SampleData.NewData<TestEnum> {
        fun randomData() = TestEnum.entries.random()
        return generate(::randomData)
    }

    private fun generateClass(): SampleData.NewData<TestClass> {
        fun randomData() = TestClass(generateString().value)
        return generate(::randomData)
    }

    private fun <T : Any> generate(
        randomData: () -> T
    ): SampleData.NewData<T> {
        fun randomList() = (0..1).map { randomData() }
        return SampleData.NewData(
            nullableValue = Random.nextBoolean().ifTrue { randomData() },
            value = randomData(),
            nullableListOfValue = Random.nextBoolean().ifTrue { randomList() },
            listOfValue = randomList(),
            nullableMapOfValue = Random.nextBoolean().ifTrue { mapOf("key" to randomData()) },
            mapOfValue = mapOf("key" to randomData()),
        )
    }

    enum class TestEnum { ONE }
    data class TestClass(val value: String)

    private fun <T> Boolean.ifTrue(block: () -> T) = if (this) {
        block()
    } else {
        null
    }
}
