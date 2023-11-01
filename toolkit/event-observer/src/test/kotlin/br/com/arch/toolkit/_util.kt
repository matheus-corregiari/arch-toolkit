package br.com.arch.toolkit

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.LiveData
import br.com.arch.toolkit.livedata.ResponseLiveData
import br.com.arch.toolkit.result.DataResult
import br.com.arch.toolkit.result.DataResultStatus
import br.com.arch.toolkit.util.dataResultError
import br.com.arch.toolkit.util.dataResultLoading
import org.mockito.kotlin.mock
import org.mockito.kotlin.verifyBlocking
import org.mockito.kotlin.verifyNoInteractions

internal val alwaysOnOwner = object : LifecycleOwner {
    private val registry = LifecycleRegistry(this)
    override val lifecycle: Lifecycle
        get() {
            registry.currentState = Lifecycle.State.RESUMED
            return registry
        }
}

internal fun <T> ResponseLiveData<T>.testSetValue(value: DataResult<T>?) {
    LiveData::class.java.declaredMethods.find { it.name == "setValue" }?.let {
        it.isAccessible = true
        it.invoke(this, value)
    }
}

internal fun <T> ResponseLiveData<T>.testPostValue(value: DataResult<T>?) {
    LiveData::class.java.declaredMethods.find { it.name == "postValue" }?.let {
        it.isAccessible = true
        it.invoke(this, value)
    }
}

class Mocks<T>(
    // Loading
    val showLoadingObserver: () -> Unit = mock(),
    val hideLoadingObserver: () -> Unit = mock(),
    val loadingObserver: (Boolean) -> Unit = mock(),

    // Data
    val dataObserver: (T) -> Unit = mock(),

    // Error
    val errorObserver: (Throwable) -> Unit = mock(),
    val emptyErrorObserver: () -> Unit = mock(),

    // Success
    val successObserver: () -> Unit = mock(),

    // Status
    val statusObserver: (DataResultStatus) -> Unit = mock(),

    // Result
    val resultObserver: (DataResult<T>) -> Unit = mock(),

    // Empty / NotEmpty
    val emptyObserver: () -> Unit = mock(),
    val notEmptyObserver: () -> Unit = mock(),

    // None
    val noneObserver: () -> Unit = mock()
) {
    fun apply(single: Boolean, liveData: ResponseLiveData<T>) {
        liveData.observe(alwaysOnOwner) {
            // Loading
            showLoading(single = single, observer = showLoadingObserver)
            hideLoading(single = single, observer = hideLoadingObserver)
            loading(single = single, observer = loadingObserver)

            // Data
            data(single = single, observer = dataObserver)

            // Error
            error(single = single, observer = emptyErrorObserver)
            error(single = single, observer = errorObserver)

            // Success
            success(single = single, observer = successObserver)

            // Status
            status(single = single, observer = statusObserver)

            // Result
            result(single = single, observer = resultObserver)

            // Empty / NotEmpty
            empty(single = single, observer = emptyObserver)
            notEmpty(single = single, observer = notEmptyObserver)

            // None
            none(single = single, observer = noneObserver)
        }
    }

    fun assertAllZeroInteractions() {
        verifyNoInteractions(showLoadingObserver)
        verifyNoInteractions(hideLoadingObserver)
        verifyNoInteractions(loadingObserver)
        verifyNoInteractions(dataObserver)
        verifyNoInteractions(errorObserver)
        verifyNoInteractions(emptyErrorObserver)
        verifyNoInteractions(successObserver)
        verifyNoInteractions(statusObserver)
        verifyNoInteractions(resultObserver)
        verifyNoInteractions(emptyObserver)
        verifyNoInteractions(notEmptyObserver)
        verifyNoInteractions(noneObserver)
    }

    fun assertLoadingWithoutData() {
        verifyBlocking(showLoadingObserver) { invoke() }
        verifyNoInteractions(hideLoadingObserver)
        verifyBlocking(loadingObserver) { invoke(true) }
        verifyNoInteractions(dataObserver)
        verifyNoInteractions(errorObserver)
        verifyNoInteractions(emptyErrorObserver)
        verifyNoInteractions(successObserver)
        verifyBlocking(statusObserver) { invoke(DataResultStatus.LOADING) }
        verifyBlocking(resultObserver) { invoke(dataResultLoading()) }
        verifyNoInteractions(emptyObserver)
        verifyNoInteractions(notEmptyObserver)
        verifyNoInteractions(noneObserver)
    }

    fun assertLoadingWithData(data: T) {
        verifyBlocking(showLoadingObserver) { invoke() }
        verifyNoInteractions(hideLoadingObserver)
        verifyBlocking(loadingObserver) { invoke(true) }
        verifyBlocking(dataObserver) { invoke(data) }
        verifyNoInteractions(dataObserver)
        verifyNoInteractions(errorObserver)
        verifyNoInteractions(emptyErrorObserver)
        verifyNoInteractions(successObserver)
        verifyBlocking(statusObserver) { invoke(DataResultStatus.LOADING) }
        verifyBlocking(resultObserver) { invoke(dataResultLoading(data)) }
        verifyNoInteractions(emptyObserver)
        verifyNoInteractions(notEmptyObserver)
        verifyNoInteractions(noneObserver)
    }

    fun assertErrorWithoutData(error: Throwable) {
        verifyNoInteractions(showLoadingObserver)
        verifyBlocking(hideLoadingObserver) { invoke() }
        verifyBlocking(loadingObserver) { invoke(false) }
        verifyNoInteractions(dataObserver)
        verifyBlocking(errorObserver) { invoke(error) }
        verifyBlocking(emptyErrorObserver) { invoke() }
        verifyNoInteractions(successObserver)
        verifyBlocking(statusObserver) { invoke(DataResultStatus.ERROR) }
        verifyBlocking(resultObserver) { invoke(dataResultError(error)) }
        verifyNoInteractions(emptyObserver)
        verifyNoInteractions(notEmptyObserver)
        verifyNoInteractions(noneObserver)
    }

    fun assertErrorWithData(error: Throwable, data: T) {
        verifyNoInteractions(showLoadingObserver)
        verifyBlocking(hideLoadingObserver) { invoke() }
        verifyBlocking(loadingObserver) { invoke(false) }
        verifyBlocking(dataObserver) { invoke(data) }
        verifyBlocking(errorObserver) { invoke(error) }
        verifyBlocking(emptyErrorObserver) { invoke() }
        verifyNoInteractions(successObserver)
        verifyBlocking(statusObserver) { invoke(DataResultStatus.ERROR) }
        verifyBlocking(resultObserver) { invoke(dataResultError(error, data)) }
        verifyNoInteractions(emptyObserver)
        verifyNoInteractions(notEmptyObserver)
        verifyNoInteractions(noneObserver)
    }
}