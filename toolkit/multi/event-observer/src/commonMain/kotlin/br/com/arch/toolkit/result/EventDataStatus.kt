package br.com.arch.toolkit.result

/**
 * Defines how data presence should be considered when evaluating an event.
 *
 * This allows fine-grained control over whether the event callback should be triggered
 * based on the presence or absence of data in the [DataResult].
 *
 * @see ObserveWrapper
 * @see DataResult
 */
enum class EventDataStatus {
    
    /**
     * Executes the event only when the DataResult contains a non-null data payload.
     */
    WithData,

    /**
     * Executes the event only when the DataResult has a null data payload.
     */
    WithoutData,

    /**
     * Ignores the presence of data and always executes the event if other conditions are met.
     */
    DoesNotMatter;

    /**
     * Evaluates whether this event should be considered based on the current data result.
     *
     * @param result The [DataResult] to be evaluated.
     * @return `true` if the event should be triggered according to the defined data status.
     */
    fun considerEvent(result: DataResult<*>) = when (this) {
        WithData -> result.hasData
        WithoutData -> !result.hasData
        DoesNotMatter -> true
    }
}
