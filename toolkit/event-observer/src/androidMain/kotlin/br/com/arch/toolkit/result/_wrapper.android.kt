@file:Suppress(
    "KotlinNullnessAnnotation",
    "TooManyFunctions",
    "CyclomaticComplexMethod"
)

package br.com.arch.toolkit.result

import androidx.annotation.NonNull
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import br.com.arch.toolkit.livedata.ResponseLiveData

/**
 * Observes until all observers on Wrapper get removed
 *
 * @param owner The desired Owner to observe
 *
 * @return The ResponseLiveData<T> attached to the Wrapper
 */
@NonNull
internal fun <T> ObserveWrapper<T>.attachTo(
    @NonNull liveData: ResponseLiveData<T>,
    @NonNull owner: LifecycleOwner
): ResponseLiveData<T> {
    val observer = object : Observer<DataResult<T>?> {
        override fun onChanged(value: DataResult<T>?) {
            scope.launchWithErrorTreatment {
                handleResult(value) { owner.lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED) }
                if (this@attachTo.eventList.isEmpty()) {
                    liveData.removeObserver(this)
                }
            }
        }
    }
    liveData.observe(owner, observer)
    return liveData
}
