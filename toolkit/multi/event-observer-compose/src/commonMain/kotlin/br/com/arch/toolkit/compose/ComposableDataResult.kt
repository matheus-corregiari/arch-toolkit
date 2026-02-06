@file:Suppress(
    "ComposableNaming",
    "MagicNumber",
    "FunctionNaming",
    "TooManyFunctions",
)

package br.com.arch.toolkit.compose

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.com.arch.toolkit.compose.ComposableDataResult.AnimationConfig.Defaults.defaultEnterDuration
import br.com.arch.toolkit.compose.ComposableDataResult.AnimationConfig.Defaults.defaultExitDuration
import br.com.arch.toolkit.compose.ComposableDataResult.AnimationConfig.Defaults.enabledByDefault
import br.com.arch.toolkit.compose.observable.ComposeObservable
import br.com.arch.toolkit.compose.observable.DataObservable
import br.com.arch.toolkit.compose.observable.EmptyObservable
import br.com.arch.toolkit.compose.observable.ErrorObservable
import br.com.arch.toolkit.compose.observable.ErrorWithThrowableObservable
import br.com.arch.toolkit.compose.observable.HideLoadingObservable
import br.com.arch.toolkit.compose.observable.ManyObservable
import br.com.arch.toolkit.compose.observable.NotEmptyObservable
import br.com.arch.toolkit.compose.observable.ResultObservable
import br.com.arch.toolkit.compose.observable.ShowLoadingObservable
import br.com.arch.toolkit.compose.observable.SingleObservable
import br.com.arch.toolkit.compose.observable.StatusObservable
import br.com.arch.toolkit.compose.observable.SuccessObservable
import br.com.arch.toolkit.flow.ResponseFlow
import br.com.arch.toolkit.result.DataResult
import br.com.arch.toolkit.result.DataResultStatus
import br.com.arch.toolkit.result.EventDataStatus
import br.com.arch.toolkit.result.EventDataStatus.DoesNotMatter
import br.com.arch.toolkit.result.ObserveWrapper
import br.com.arch.toolkit.util.unwrap
import br.com.arch.toolkit.util.valueOrNull
import kotlinx.coroutines.flow.Flow
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.DurationUnit

/**
 * Declarative Compose wrapper for observing a [ResponseFlow] of [DataResult].
 *
 * [ComposableDataResult] provides a **fluent DSL** to react to common states:
 * - **Loading** → [OnShowLoading], [OnHideLoading]
 * - **Error** → [OnError]
 * - **Success/Data** → [OnSuccess], [OnData]
 * - **Collections** → [OnEmpty], [OnNotEmpty], [OnSingle], [OnMany]
 *
 * The final rendering is triggered by [Unwrap], which collects the underlying flow
 * and dispatches the configured callbacks.
 *
 * ---
 *
 * ### Behavior
 * - Works with [Flow]s of [DataResult] (commonly [ResponseFlow]).
 * - Each state observer adds a [ComposeObservable] to the pipeline.
 * - Supports optional **non-Compose side effects** via [outsideComposable].
 * - Provides **animations** for showing/hiding state blocks via [AnimationConfig].
 * - Uses [collectAsStateWithLifecycle] if a [LifecycleOwner] is available,
 *   falling back to [collectAsState].
 *
 * ---
 *
 * ### Example: Typical Usage
 * ```kotlin
 * val comp = myFlow.composable
 *
 * comp
 *   .OnShowLoading { CircularProgressIndicator() }
 *   .OnData { user -> Text("Hello ${user.name}") }
 *   .OnError { e -> Text("Error: ${e.message}") }
 *   .Unwrap()
 * ```
 *
 * ### Example: Nested DSL
 * ```kotlin
 * myFlow.composable.Unwrap {
 *   OnShowLoading { CircularProgressIndicator() }
 *   OnData { Text("Done!") }
 *   OnError { Text("Oops!") }
 * }
 * ```
 *
 * ### Example: Animations
 * ```kotlin
 * comp.animation {
 *   enabled = true
 *   defaultEnterDuration = 300.milliseconds
 *   defaultExitDuration = 200.milliseconds
 * }
 * ```
 *
 * ---
 *
 * @param T The type of data wrapped in [DataResult].
 * @property result The [Flow] emitting [DataResult] values.
 *
 * @see ResponseFlow
 * @see DataResult
 * @see Unwrap
 * @see AnimationConfig
 */
@ConsistentCopyVisibility
data class ComposableDataResult<T> internal constructor(
    val result: Flow<DataResult<T>>,
) {
    private val animationConfig = AnimationConfig()
    private var notComposableBlock: (ObserveWrapper<T>.() -> Unit)? = null
    private val observableList = mutableListOf<ComposeObservable<T, *>>()

    /**
     * Configures animation parameters for all subsequent composable callbacks that are
     * managed by this [ComposableDataResult] instance.
     *
     * Example:
     *
     * ```kotlin
     * comp.animation {
     *   enabled = true
     *   defaultEnterDuration = 300.milliseconds
     * }
     * ```
     *
     * By default, animations are enabled with predefined fade-in and fade-out transitions.
     * You can disable animations or customize the enter/exit transitions and their durations.
     *
     * @param config A DSL block to customize the [AnimationConfig] for this instance.
     * @return This [ComposableDataResult] instance for chaining further configurations.
     * @see AnimationConfig
     */
    fun animation(config: AnimationConfig.() -> Unit) = apply { animationConfig.config() }

    /**
     * Attaches non-@Composable observation logic that runs outside of the Compose scope.
     *
     * Use this to add side-effects or loggers via an [ObserveWrapper].
     *
     * Example:
     * ```kotlin
     * comp.outsideComposable {
     *   error { throwable -> logError(throwable) }
     * }
     * ```
     *
     * @param config receiver lambda on an [ObserveWrapper]<T> for traditional callbacks
     * @return this [ComposableDataResult] for chaining
     * @see ObserveWrapper
     */
    fun outsideComposable(config: ObserveWrapper<T>.() -> Unit) =
        apply { notComposableBlock = config }

    // region Success

    /**
     * Renders the given composable block when the [DataResultStatus] is `SUCCESS`.
     *
     * ---
     *
     * ### Behavior
     * - Triggered when the upstream [DataResult] has a success status.
     * - Optional [EventDataStatus] filter allows reacting only if data is present or not.
     *
     * ---
     *
     * ### Example
     * ```kotlin
     * comp.OnSuccess(EventDataStatus.WithData) {
     *   Text("Success with data!")
     * }.Unwrap()
     * ```
     *
     * @param dataStatus Filter when to render based on [EventDataStatus]. Default = [EventDataStatus.DoesNotMatter].
     * @param func The composable content to display on success.
     * @return This [ComposableDataResult] for fluent chaining.
     *
     * @see DataResultStatus.SUCCESS
     * @see EventDataStatus
     */
    @Composable
    fun OnSuccess(
        dataStatus: EventDataStatus = DoesNotMatter,
        func: @Composable () -> Unit,
    ) = apply { observableList.add(SuccessObservable(dataStatus, func)) }

    // endregion

    // region Loading

    /**
     * Renders the given composable block while the [DataResultStatus] is `LOADING`.
     *
     * ---
     *
     * ### Example
     * ```kotlin
     * comp.OnShowLoading {
     *   CircularProgressIndicator()
     * }.Unwrap()
     * ```
     *
     * @param dataStatus Filter based on [EventDataStatus]. Default = [EventDataStatus.DoesNotMatter].
     * @param func The composable content to show during loading.
     */
    @Composable
    fun OnShowLoading(
        dataStatus: EventDataStatus = DoesNotMatter,
        func: @Composable () -> Unit,
    ) = apply { observableList.add(ShowLoadingObservable(dataStatus, func)) }

    /**
     * Renders the given composable block when loading finishes
     * (i.e., status changes from `LOADING` to something else).
     *
     * ---
     *
     * ### Example
     * ```kotlin
     * comp.OnHideLoading {
     *   Text("Loaded!")
     * }.Unwrap()
     * ```
     *
     * @param dataStatus Filter based on [EventDataStatus]. Default = [EventDataStatus.DoesNotMatter].
     * @param func The composable content to display after loading ends.
     */
    @Composable
    fun OnHideLoading(
        dataStatus: EventDataStatus = DoesNotMatter,
        func: @Composable () -> Unit,
    ) = apply { observableList.add(HideLoadingObservable(dataStatus, func)) }

    // endregion

    // region Error

    /**
     * Renders the given composable block when an error occurs,
     * without exposing the [Throwable].
     *
     * ---
     *
     * ### Example
     * ```kotlin
     * comp.OnError {
     *   Text("Something went wrong")
     * }.Unwrap()
     * ```
     */
    @Composable
    fun OnError(
        dataStatus: EventDataStatus = DoesNotMatter,
        func: @Composable () -> Unit,
    ) = apply { observableList.add(ErrorObservable(dataStatus, func)) }

    /**
     * Renders the given composable block when an error occurs,
     * exposing the thrown [Throwable].
     *
     * ---
     *
     * ### Example
     * ```kotlin
     * comp.OnError { t ->
     *   Text("Error: ${t.message}")
     * }.Unwrap()
     * ```
     */
    @Composable
    fun OnError(
        dataStatus: EventDataStatus = DoesNotMatter,
        func: @Composable (Throwable) -> Unit,
    ) = apply { observableList.add(ErrorWithThrowableObservable(dataStatus, func)) }

    // endregion

    // region Data

    /**
     * Renders the given composable block when non-null data is available.
     *
     * ---
     *
     * ### Example
     * ```kotlin
     * comp.OnData { user ->
     *   Text("Hello, ${user.name}")
     * }.Unwrap()
     * ```
     */
    @Composable
    fun OnData(func: @Composable (T) -> Unit) = apply {
        observableList.add(DataObservable { data, _, _ -> func(data) })
    }

    /**
     * Renders the given composable block when data is available,
     * providing the [DataResultStatus] alongside the data.
     *
     * ---
     *
     * ### Example
     * ```kotlin
     * comp.OnData { user, status ->
     *   Text("User ${user.name} (status=$status)")
     * }
     * ```
     */
    @Composable
    fun OnData(func: @Composable (T, DataResultStatus) -> Unit) = apply {
        observableList.add(DataObservable { data, status, _ -> func(data, status) })
    }

    /**
     * Renders the given composable block when data is available,
     * providing data, its [DataResultStatus], and a possible [Throwable].
     *
     * ---
     *
     * ### Example
     * ```kotlin
     * comp.OnData { user, status, error ->
     *   if (error != null) Text("Recovered from ${error.message}")
     *   else Text("User ${user.name}")
     * }
     * ```
     */
    @Composable
    fun OnData(func: @Composable (T, DataResultStatus, Throwable?) -> Unit) = apply {
        observableList.add(DataObservable(func))
    }

    // endregion

    // region Result
    @Composable
    fun OnResult(func: @Composable (T?) -> Unit) =
        apply { observableList.add(ResultObservable { data, _, _ -> func(data) }) }

    @Composable
    fun OnResult(func: @Composable (T?, DataResultStatus) -> Unit) =
        apply { observableList.add(ResultObservable { data, status, _ -> func(data, status) }) }

    @Composable
    fun OnResult(func: @Composable (T?, DataResultStatus, Throwable?) -> Unit) =
        apply { observableList.add(ResultObservable(func)) }
    // endregion

    // region Status
    @Composable
    fun OnStatus(
        dataStatus: EventDataStatus = DoesNotMatter,
        func: @Composable (DataResultStatus) -> Unit
    ) = apply { observableList.add(StatusObservable(dataStatus, func)) }
    // endregion

    // region List Type

    /**
     * Renders when the underlying list is empty.
     *
     * ---
     *
     * ### Example
     * ```kotlin
     * comp.OnEmpty {
     *   Text("No items found")
     * }
     * ```
     */
    @Composable
    fun OnEmpty(func: @Composable () -> Unit) =
        apply { observableList.add(EmptyObservable { _, _ -> func() }) }

    @Composable
    fun OnEmpty(func: @Composable (DataResultStatus) -> Unit) =
        apply { observableList.add(EmptyObservable { status, _ -> func(status) }) }

    @Composable
    fun OnEmpty(func: @Composable (DataResultStatus, Throwable?) -> Unit) =
        apply { observableList.add(EmptyObservable(func)) }

    /**
     * Renders when the underlying list is not empty.
     */
    @Composable
    fun OnNotEmpty(func: @Composable (T) -> Unit) =
        apply { observableList.add(NotEmptyObservable { data, _, _ -> func(data) }) }

    @Composable
    fun OnNotEmpty(func: @Composable (T, DataResultStatus) -> Unit) =
        apply { observableList.add(NotEmptyObservable { data, status, _ -> func(data, status) }) }

    @Composable
    fun OnNotEmpty(func: @Composable (T, DataResultStatus, Throwable?) -> Unit) =
        apply { observableList.add(NotEmptyObservable(func)) }

    /**
     * Renders when the list contains exactly one item.
     */
    @Composable
    fun <R> OnSingle(func: @Composable (R) -> Unit) =
        apply { observableList.add(SingleObservable<T, R> { data, _, _ -> func(data) }) }

    @Composable
    fun <R> OnSingle(func: @Composable (R, DataResultStatus) -> Unit) = apply {
        val observable = SingleObservable<T, R> { data, status, _ -> func(data, status) }
        observableList.add(observable)
    }

    @Composable
    fun <R> OnSingle(func: @Composable (R, DataResultStatus, Throwable?) -> Unit) =
        apply { observableList.add(SingleObservable(func)) }

    /**
     * Renders when the list contains more than one item.
     */
    @Composable
    fun OnMany(func: @Composable (T) -> Unit) =
        apply { observableList.add(ManyObservable { data, _, _ -> func(data) }) }

    @Composable
    fun OnMany(func: @Composable (T, DataResultStatus) -> Unit) =
        apply { observableList.add(ManyObservable { data, status, _ -> func(data, status) }) }

    @Composable
    fun OnMany(func: @Composable (T, DataResultStatus, Throwable?) -> Unit) =
        apply { observableList.add(ManyObservable(func)) }

    // endregion

    // region Unwrap

    /**
     * DSL entrypoint to configure multiple observables before collection.
     *
     * ---
     *
     * ### Example
     * ```kotlin
     * comp.Unwrap {
     *   OnShowLoading { CircularProgressIndicator() }
     *   OnData { Text("Done!") }
     *   OnError { Text("Oops!") }
     * }
     * ```
     *
     * @param owner Optional [LifecycleOwner] for lifecycle-aware collection.
     * @param config DSL block on this [ComposableDataResult].
     */
    @Composable
    fun Unwrap(
        owner: LifecycleOwner? = LocalLifecycleOwner.current,
        config: @Composable ComposableDataResult<T>.() -> Unit
    ) {
        config()
        Unwrap(owner)
    }

    /**
     * Starts collecting the underlying [Flow] and dispatches
     * all configured observables.
     *
     * ---
     *
     * ### Example
     * ```kotlin
     * comp
     *   .OnShowLoading { … }
     *   .OnData { … }
     *   .OnError { … }
     *   .Unwrap()
     * ```
     *
     * @param owner Optional [LifecycleOwner] for lifecycle-aware collection.
     */
    @Composable
    fun Unwrap(owner: LifecycleOwner? = LocalLifecycleOwner.current) {
        LaunchedEffect(this) { result.unwrap { notComposableBlock?.invoke(this) } }
        val animationConfig = remember { animationConfig }
        val state: DataResult<T>? by if (owner != null) {
            result.collectAsStateWithLifecycle(result.valueOrNull(), owner)
        } else {
            result.collectAsState(result.valueOrNull())
        }
        val resultState = state?.takeIf { it.isNone.not() } ?: return
        observableList.forEachIndexed { index, observable ->
            if (animationConfig.enabled) {
                AnimatedVisibility(
                    label = "observable - ${index.toString().padStart(3, '0')}",
                    visible = observable.hasVisibleContent(resultState),
                    modifier = animationConfig.animationModifier,
                    enter = animationConfig.enterAnimation,
                    exit = animationConfig.exitAnimation,
                    content = { observable.observe(resultState) },
                )
            } else {
                if (observable.hasVisibleContent(resultState)) observable.observe(resultState)
            }
        }
    }
    //endregion

    /**
     * Holds animation configuration for the composable states managed by [ComposableDataResult].
     *
     * ---
     *
     * ### Behavior
     * - Controls whether animations are applied to [AnimatedVisibility] when rendering
     *   success, error, loading, or data states.
     * - Defines the default `Modifier`, enter, and exit transitions.
     * - Provides global defaults via [Defaults], which can be overridden before use.
     *
     * ---
     *
     * ### Example
     * ```kotlin
     * comp.animation {
     *   enabled = true
     *   enterAnimation = fadeIn(tween(300))
     *   exitAnimation = fadeOut(tween(200))
     * }
     * ```
     *
     * Or set global defaults once:
     * ```kotlin
     * ComposableDataResult.AnimationConfig.enabledByDefault = false
     * ComposableDataResult.AnimationConfig.defaultEnterDuration = 200.milliseconds
     * ComposableDataResult.AnimationConfig.defaultExitDuration = 200.milliseconds
     * ```
     *
     * ---
     *
     * @property enabled Whether animations are active for the composable blocks.
     *           Defaults to [Defaults.enabledByDefault].
     * @property animationModifier A [Modifier] applied to the `AnimatedVisibility` wrapper.
     * @property enterAnimation The [EnterTransition] for showing content.
     *           Defaults to a fade-in with a delay equal to the exit duration.
     * @property exitAnimation The [ExitTransition] for hiding content.
     *           Defaults to a simple fade-out.
     *
     * @see ComposableDataResult.animation
     * @see AnimatedVisibility
     */
    class AnimationConfig internal constructor() {
        var enabled: Boolean = enabledByDefault
        var animationModifier = Modifier
        var enterAnimation: EnterTransition = fadeIn(
            animationSpec = tween(
                durationMillis = defaultEnterDuration.toInt(DurationUnit.MILLISECONDS),
                // Delays enter to potentially run after a preceding exit animation completes
                delayMillis = defaultExitDuration.toInt(DurationUnit.MILLISECONDS),
            ),
        )
        var exitAnimation: ExitTransition =
            fadeOut(
                animationSpec = tween(
                    durationMillis = defaultExitDuration.toInt(DurationUnit.MILLISECONDS),
                ),
            )

        /**
         * Provides global defaults for [AnimationConfig].
         *
         * ---
         *
         * ### Behavior
         * - These values are applied whenever a new [AnimationConfig] is created.
         * - Can be changed globally to affect all instances.
         *
         * ---
         *
         * ### Example
         * ```kotlin
         * // Disable animations everywhere
         * ComposableDataResult.AnimationConfig.enabledByDefault = false
         *
         * // Faster transitions
         * ComposableDataResult.AnimationConfig.defaultEnterDuration = 150.milliseconds
         * ComposableDataResult.AnimationConfig.defaultExitDuration = 150.milliseconds
         * ```
         *
         * ---
         *
         * @property enabledByDefault Global flag to enable/disable animations.
         *           Default = `true`.
         * @property defaultEnterDuration Default duration for [enterAnimation].
         *           Default = `450.milliseconds`.
         * @property defaultExitDuration Default duration for [exitAnimation].
         *           Default = `450.milliseconds`.
         */
        companion object Defaults {
            var enabledByDefault = true
            var defaultEnterDuration: Duration = 450.milliseconds
            var defaultExitDuration: Duration = 450.milliseconds
        }
    }
}
