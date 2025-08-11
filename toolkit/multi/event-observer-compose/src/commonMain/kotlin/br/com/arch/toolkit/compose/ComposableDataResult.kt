@file:Suppress(
    "ComposableNaming",
    "MagicNumber",
    "FunctionNaming",
    "TooManyFunctions",
)
@file:OptIn(ExperimentalAtomicApi::class)

package br.com.arch.toolkit.compose

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.com.arch.toolkit.compose.observable.ComposeObservable
import br.com.arch.toolkit.compose.observable.DataObservable
import br.com.arch.toolkit.compose.observable.EmptyObservable
import br.com.arch.toolkit.compose.observable.ErrorObservable
import br.com.arch.toolkit.compose.observable.ErrorWithThrowableObservable
import br.com.arch.toolkit.compose.observable.HideLoadingObservable
import br.com.arch.toolkit.compose.observable.ManyObservable
import br.com.arch.toolkit.compose.observable.NotEmptyObservable
import br.com.arch.toolkit.compose.observable.ShowLoadingObservable
import br.com.arch.toolkit.compose.observable.SingleObservable
import br.com.arch.toolkit.compose.observable.SuccessObservable
import br.com.arch.toolkit.flow.ResponseFlow
import br.com.arch.toolkit.result.DataResult
import br.com.arch.toolkit.result.DataResultStatus
import br.com.arch.toolkit.result.EventDataStatus
import br.com.arch.toolkit.result.EventDataStatus.DoesNotMatter
import br.com.arch.toolkit.result.ObserveWrapper
import br.com.arch.toolkit.result.unwrap
import kotlinx.coroutines.flow.Flow
import kotlin.concurrent.atomics.ExperimentalAtomicApi
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.DurationUnit

/**
 * Entry point for observing a [ResponseFlow] of [DataResult] in Arch Toolkit.
 *
 * Provides a fluent, declarative API to react to loading, error, data, and list states
 * via composable callbacks. This class also supports animations for the appearance and
 * disappearance of these states through the [AnimationConfig] class.
 *
 * @param T The type of data held by the [DataResult].
 * @property resultFlow The source [ResponseFlow] emitting [DataResult] values.
 * @see ResponseFlow
 * @see DataResult
 * @see AnimationConfig
 */
@ConsistentCopyVisibility
data class ComposableDataResult<T> internal constructor(
    private val result: Flow<DataResult<T>>,
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
     * Renders the given @Composable block when the [DataResultStatus] is SUCCESS.
     *
     * Example:
     * ```kotlin
     * comp.OnSuccess(EventDataStatus.WithData) {
     *   Text("Success")
     * }.Unwrap()
     * ```
     *
     * @param dataStatus when to run the block based on data presence (see [EventDataStatus])
     * @param func the @Composable lambda to execute on success
     * @return this [ComposableDataResult] for chaining
     */
    @Composable
    fun OnSuccess(
        dataStatus: EventDataStatus = DoesNotMatter,
        func: @Composable () -> Unit,
    ) = apply { observableList.add(SuccessObservable(dataStatus, func)) }

    // endregion

    // region Loading

    /**
     * Renders the given @Composable block when the [DataResultStatus] is LOADING.
     *
     * Example:
     * ```kotlin
     * comp.OnShowLoading(EventDataStatus.WithData) {
     *   CircularProgressIndicator()
     * }.Unwrap()
     * ```
     *
     * @param dataStatus when to run the block based on data presence (see [EventDataStatus])
     * @param func the @Composable lambda to execute on loading
     * @return this [ComposableDataResult] for chaining
     * @see EventDataStatus
     */
    @Composable
    fun OnShowLoading(
        dataStatus: EventDataStatus = DoesNotMatter,
        func: @Composable () -> Unit,
    ) = apply { observableList.add(ShowLoadingObservable(dataStatus, func)) }

    /**
     * Renders the given @Composable block when loading finishes (status becomes non-LOADING).
     *
     * Example:
     * ```kotlin
     * comp.OnHideLoading {
     *   // hide your loader
     * }.Unwrap()
     * ```
     *
     * @param dataStatus when to run the block based on data presence (see [EventDataStatus])
     * @param func the @Composable lambda to execute after loading ends
     * @return this [ComposableDataResult] for chaining
     * @see EventDataStatus
     */
    @Composable
    fun OnHideLoading(
        dataStatus: EventDataStatus = DoesNotMatter,
        func: @Composable () -> Unit,
    ) = apply { observableList.add(HideLoadingObservable(dataStatus, func)) }

    // endregion

    // region Error

    /**
     * Renders the given @Composable block when an error occurs (without the error object).
     *
     * Example:
     * ```kotlin
     * comp.OnError {
     *   Text("Unexpected error")
     * }.Unwrap()
     * ```
     *
     * @param dataStatus when to run the block based on data presence (see [EventDataStatus])
     * @param func the @Composable lambda to execute on error
     * @return this [ComposableDataResult] for chaining
     * @see EventDataStatus
     */
    @Composable
    fun OnError(
        dataStatus: EventDataStatus = DoesNotMatter,
        func: @Composable () -> Unit,
    ) = apply { observableList.add(ErrorObservable(dataStatus, func)) }

    /**
     * Renders the given @Composable block when an error occurs, providing the [Throwable].
     *
     * Example:
     * ```kotlin
     * comp.OnError { t ->
     *   Text("Error: ${t.message}")
     * }.Unwrap()
     * ```
     *
     * @param dataStatus when to run the block based on data presence (see [EventDataStatus])
     * @param func the @Composable lambda receiving the thrown [Throwable]
     * @return this [ComposableDataResult] for chaining
     * @see EventDataStatus
     */
    @Composable
    fun OnError(
        dataStatus: EventDataStatus = DoesNotMatter,
        func: @Composable (Throwable) -> Unit,
    ) = apply { observableList.add(ErrorWithThrowableObservable(dataStatus, func)) }

    // endregion

    // region Data

    /**
     * Renders the given @Composable block when non-null data is available.
     *
     * Example:
     * ```kotlin
     * comp.OnData { data ->
     *   Text(data.toString())
     * }.Unwrap()
     * ```
     *
     * @param func the @Composable lambda receiving the data of type [T]
     * @return this [ComposableDataResult] for chaining
     * @see DataResultStatus
     */
    @Composable
    fun OnData(func: @Composable (T) -> Unit) = apply {
        observableList.add(DataObservable { data, _, _ -> func(data) })
    }

    /**
     * Renders the given @Composable block when data is available, also providing the [DataResultStatus].
     *
     * @param func the @Composable lambda receiving the data of type [T] and its [DataResultStatus]
     * @return this [ComposableDataResult] for chaining
     * @see DataResultStatus
     */
    @Composable
    fun OnData(func: @Composable (T, DataResultStatus) -> Unit) = apply {
        observableList.add(DataObservable { data, status, _ -> func(data, status) })
    }

    /**
     * Renders the given @Composable block when data is available, providing data, status, and optional error.
     *
     * @param func the @Composable lambda receiving data, its [DataResultStatus], and a possible [Throwable]
     * @return this [ComposableDataResult] for chaining
     * @see DataResultStatus
     */
    @Composable
    fun OnData(func: @Composable (T, DataResultStatus, Throwable?) -> Unit) = apply {
        observableList.add(DataObservable(func))
    }

    // endregion

    // region List Type

    /**
     * Renders the given @Composable block when the data list is empty.
     *
     * Example:
     * ```kotlin
     * comp.OnEmpty { Text("Nothing here") }.Unwrap()
     * ```
     *
     * @param func the @Composable lambda to execute on empty list
     * @return this [ComposableDataResult] for chaining
     */
    @Composable
    fun OnEmpty(func: @Composable () -> Unit) = apply { observableList.add(EmptyObservable(func)) }

    /**
     * Renders the given @Composable block when the data list is not empty.
     *
     * @param func the @Composable lambda receiving the non-empty data list
     * @return this [ComposableDataResult] for chaining
     */
    @Composable
    fun OnNotEmpty(func: @Composable (T) -> Unit) =
        apply { observableList.add(NotEmptyObservable(func)) }

    /**
     * Renders the given @Composable block when the data list contains exactly one item.
     *
     * @param func the @Composable lambda receiving the single item of type [R]
     * @return this [ComposableDataResult] for chaining
     */
    @Composable
    fun <R> OnSingle(func: @Composable (R) -> Unit) =
        apply { observableList.add(SingleObservable(func)) }

    /**
     * Renders the given @Composable block when the data list contains more than one item.
     *
     * @param func the @Composable lambda receiving the entire data list
     * @return this [ComposableDataResult] for chaining
     */
    @Composable
    fun OnMany(func: @Composable (T) -> Unit) = apply { observableList.add(ManyObservable(func)) }

    // endregion

    // region Unwrap

    /**
     * Configures multiple observables in a nested DSL before collecting the flow.
     *
     * Example:
     * ```kotlin
     * comp.Unwrap {
     *   OnData { … }
     *   OnError { … }
     * }
     * ```
     *
     * @param config A @Composable receiver on this [ComposableDataResult] to set up callbacks.
     * @see Unwrap(LifecycleOwner)
     */
    @Composable
    fun Unwrap(config: @Composable ComposableDataResult<T>.() -> Unit) {
        config()
        Unwrap()
    }

    /**
     * Starts collecting the underlying [ResponseFlow] and dispatches all configured callbacks.
     *
     * Must be the last call in your chain if not using the DSL-style `Unwrap` overload.
     *
     * Example:
     *
     * ```kotlin
     * comp
     *   .OnShowLoading { … }
     *   .OnData { … }
     *   .OnError { … }
     *   .Unwrap()
     * ```
     *
     * @see ResponseFlow.collect
     * @see ObserveWrapper
     */
    @Composable
    fun Unwrap() {
        LaunchedEffect(this) { result.unwrap { notComposableBlock?.invoke(this) } }
        val animationConfig = remember { animationConfig }
        val state: DataResult<T>? by result.collectAsStateWithLifecycle(null)
        val resultState = state.takeIf { it?.isNone == false } ?: return
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
     * This class allows for enabling/disabling animations, customizing the [Modifier]
     * applied during animations, and defining specific [EnterTransition] and [ExitTransition]
     * effects.
     *
     * Default animations are fade-in and fade-out. The default enter animation includes a delay
     * matching the default exit animation's duration, which can help create a sequential
     * animation effect when one composable state replaces another within the Unwrap block.
     *
     * @property enabled Whether animations are active for the composable blocks.
     *           Defaults to [AnimationConfig.Defaults.enabledByDefault].
     * @property animationModifier A [Modifier] to be applied to the `AnimatedVisibility`
     *           wrapper if animations are enabled.
     * @property enterAnimation The [EnterTransition] to use when a composable block becomes visible.
     *           Defaults to a `fadeIn` animation.
     * @property exitAnimation The [ExitTransition] to use when a composable block becomes hidden.
     *           Defaults to a `fadeOut` animation.
     * @see ComposableDataResult.animation
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
         * Provides default values for [AnimationConfig] properties.
         * These can be overridden globally before any [ComposableDataResult] instance
         * creates its [AnimationConfig].
         */
        companion object Defaults {
            /**
             * Determines if animations are enabled by default for new [AnimationConfig] instances.
             * Default value is `true`.
             */
            var enabledByDefault = true

            /**
             * The default duration for enter animations if not specified otherwise in [enterAnimation].
             * Default value is 450 milliseconds.
             */
            var defaultEnterDuration: Duration = 450.milliseconds

            /**
             * The default duration for exit animations if not specified otherwise in [exitAnimation].
             * Default value is 450 milliseconds.
             */
            var defaultExitDuration: Duration = 450.milliseconds
        }
    }
}
