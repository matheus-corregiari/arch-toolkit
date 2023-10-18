package br.com.arch.toolkit.sample.livedata

import br.com.arch.toolkit.result.DataResult
import br.com.arch.toolkit.result.DataResultStatus
import br.com.arch.toolkit.flow.MutableResponseFlow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

fun main() = runBlocking {
    val flow = MutableResponseFlow<String>()

    flow.collect {
        scope(this@runBlocking)
        transformDispatcher(Dispatchers.Default)

        data {
            println("Called Data: $it")
        }

        // Normal
        success {
            println("Normal Success")
        }
        loading {
            println("Normal loading: $it")
        }
        showLoading {
            println("Normal showLoading")
        }
        hideLoading {
            println("Normal hideLoading")
        }
        error { ->
            println("Normal error")
        }
        error { error ->
            println("Normal error: $error")
        }

        // WithData
        success(withData = true) {
            println("WithData Success")
        }
        loading(withData = true) {
            println("WithData loading: $it")
        }
        showLoading(withData = true) {
            println("WithData showLoading")
        }
        hideLoading(withData = true) {
            println("WithData hideLoading")
        }
        error(withData = true) { ->
            println("WithData error")
        }
        error(withData = true) { error ->
            println("WithData error: $error")
        }

        // WithoutData
        success(withData = false) {
            println("WithoutData Success")
        }
        loading(withData = false) {
            println("WithoutData loading: $it")
        }
        showLoading(withData = false) {
            println("WithoutData showLoading")
        }
        hideLoading(withData = false) {
            println("WithoutData hideLoading")
        }
        error(withData = false) { ->
            println("WithoutData error")
        }
        error(withData = false) { error ->
            println("WithoutData error: $error")
        }
    }

    delay(1000L)
    flow.value = DataResult(null, null, DataResultStatus.SUCCESS)
    println("--------")
    delay(1000L)
    flow.value = DataResult("data", null, DataResultStatus.SUCCESS)
    println("--------")
    delay(1000L)
    flow.value = DataResult(null, null, DataResultStatus.LOADING)
    println("--------")
    delay(1000L)
    flow.value = DataResult(null, null, DataResultStatus.ERROR)
    println("--------")
    delay(1000L)
    flow.value = DataResult(null, IllegalStateException("maldade"), DataResultStatus.ERROR)
    println("--------")

    delay(1000L)
    flow.value = DataResult("data", null, DataResultStatus.SUCCESS)
    println("--------")
    delay(1000L)
    flow.value = DataResult("cached_data", null, DataResultStatus.LOADING)
    println("--------")
    delay(1000L)
    flow.value = DataResult("cached_data", null, DataResultStatus.ERROR)
    println("--------")
    delay(1000L)
    flow.value = DataResult("cached_data", IllegalStateException("maldade"), DataResultStatus.ERROR)
    println("--------")
}