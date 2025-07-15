@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package br.com.arch.toolkit.splinter

import androidx.lifecycle.LiveData
import br.com.arch.toolkit.livedata.ResponseLiveData

actual interface TargetResultHolder<T> : ResultHolder<T> {
    val liveData: ResponseLiveData<T>
}

actual interface TargetRegularHolder<T> : RegularHolder<T> {
    val liveData: LiveData<T?>
}
