@file:Suppress("Filename", "unused")

package androidx.lifecycle

/**
 * Yes!, I'm here stealing your protected stuff! =P
 *
 * In memory of the legendary class Watson from ActionBarSherlock
 */
val <T> LiveData<T>.watson: Int get() = version

/**
 * Yes!, I'm here stealing your protected stuff! =P
 *
 * In memory of the legendary class Watson from ActionBarSherlock
 */
abstract class WatsonLiveData<T> : LiveData<T>() {
    public override fun getVersion(): Int = super.getVersion()
}
