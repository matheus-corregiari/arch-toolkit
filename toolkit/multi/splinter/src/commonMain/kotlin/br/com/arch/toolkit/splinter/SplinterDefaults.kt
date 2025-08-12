@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package br.com.arch.toolkit.splinter

import br.com.arch.toolkit.splinter.Splinter.ExecutionPolicy
import br.com.arch.toolkit.splinter.Splinter.StopPolicy
import kotlinx.coroutines.CoroutineScope

expect object SplinterDefaults {
    internal var scope: CoroutineScope
        private set
    internal var quiet: Boolean
        private set
    internal var policy: ExecutionPolicy
        private set
    internal var stopPolicy: StopPolicy
        private set

    fun scope(scope: CoroutineScope): SplinterDefaults
    fun logging(enabled: Boolean): SplinterDefaults
    fun policy(policy: ExecutionPolicy): SplinterDefaults
    fun stop(policy: StopPolicy): SplinterDefaults
}
