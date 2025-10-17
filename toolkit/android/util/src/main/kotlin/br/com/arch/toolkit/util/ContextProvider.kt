package br.com.arch.toolkit.util

import android.app.Activity
import android.app.Application
import android.app.Application.ActivityLifecycleCallbacks
import android.content.Context
import android.os.Bundle
import java.lang.ref.WeakReference

/**
 * This code defines an object called ContextProvider which is a common way in Kotlin to create a Singleton.
 * This object aims to provide access to a Context throughout an Android application. Let's break down its functionality:
 * 1. lastContext:
 *      - It's a nullable property of type WeakReference<Context>.
 *      - WeakReference is used to hold a reference to a Context (like an Activity or Application)
 *      without preventing the garbage collector from reclaiming it if it's no longer needed elsewhere. This helps avoid memory leaks.
 * 2. current:
 *  - This is a computed property that returns the Context held by lastContext
 *  if it's still available, or null if the Context has been garbage collected.
 * 3. register(application: Application):
 *      - This function is meant to be called early in your app's lifecycle, ideally within your Application class's onCreate() method.
 *      - It initializes lastContext with a WeakReference to the provided Application context.
 *      - It registers an ActivityLifecycleCallbacks listener to the Application.
 *      This listener updates lastContext whenever an Activity is created or resumed, ensuring you have access to the most current Activity context.
 * ## Purpose:
 * The ContextProvider object provides a convenient way to access a valid Context from anywhere
 * in your application. This is often needed for tasks like:
 *      - Inflating layouts
 *      - Accessing resources (strings, drawables, etc.)
 *      - Starting services or other components
 * ## Caveats:
 * - Be mindful of potential memory leaks if you hold a strong reference to the Context returned by current for an extended period.
 * - In some cases, using the Application context might be sufficient and safer than relying on an
 * Activity context, especially if you need the context to persist beyond the lifecycle of a single Activity.
 * ## Example Usage:
 * ```kotlin
 * // In your Application class's onCreate()
 * ContextProvider.register(this)
 *
 * // Elsewhere in your app
 * val context = ContextProvider.current
 * if (context != null) {
 *     // Use the context, e.g., to inflate a layout
 * }
 * ```
 */
object ContextProvider {

    private var lastContext: WeakReference<Context>? = null
    private val callback = object : ActivityLifecycleCallbacks {
        override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
            lastContext = WeakReference(activity)
        }

        override fun onActivityResumed(activity: Activity) {
            lastContext = WeakReference(activity)
        }

        override fun onActivityStarted(activity: Activity) = Unit
        override fun onActivityPaused(activity: Activity) = Unit
        override fun onActivityStopped(activity: Activity) = Unit
        override fun onActivitySaveInstanceState(
            activity: Activity,
            outState: Bundle
        ) = Unit

        override fun onActivityDestroyed(activity: Activity) = Unit
    }

    val current: Context? get() = lastContext?.get()

    fun init(context: Context) {
        lastContext = WeakReference(context)
        (context as? Application)?.let(::register)
            ?: (context.applicationContext as? Application)?.let(::register)
    }

    fun register(application: Application) {
        if (current == null) lastContext = WeakReference(application)
        application.registerActivityLifecycleCallbacks(callback)
    }
}
