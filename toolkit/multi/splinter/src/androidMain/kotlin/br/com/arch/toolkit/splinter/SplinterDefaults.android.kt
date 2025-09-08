@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package br.com.arch.toolkit.splinter

import br.com.arch.toolkit.splinter.Splinter.ExecutionPolicy
import br.com.arch.toolkit.splinter.Splinter.ExecutionPolicy.IgnoreWhenHasRunningOperations
import br.com.arch.toolkit.splinter.Splinter.StopPolicy
import br.com.arch.toolkit.splinter.Splinter.StopPolicy.AfterFirstExecution
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

actual object SplinterDefaults {

    internal actual var scope: CoroutineScope = CoroutineScope(Dispatchers.IO)
        private set
    internal actual var quiet: Boolean = false
        private set
    internal actual var policy: ExecutionPolicy = IgnoreWhenHasRunningOperations
        private set
    internal actual var stopPolicy: StopPolicy = AfterFirstExecution
        private set

    actual fun scope(scope: CoroutineScope) = apply { this.scope = scope }
    actual fun logging(enabled: Boolean) = apply { this.quiet = enabled.not() }
    actual fun policy(policy: ExecutionPolicy) = apply { this.policy = policy }
    actual fun stop(policy: StopPolicy) = apply { this.stopPolicy = policy }
}
