@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package br.com.arch.toolkit.stateHandle

/**
 * Base class for values intended to be stored in a `StateHandle` across multiplatform targets.
 *
 * This type provides a single common contract, while each platform supplies its own
 * persistence mechanism:
 *
 * - Android: Parcelable
 * - JVM/Desktop: Serializable
 * - Apple / Web: plain Kotlin class (serialize manually if needed)
 *
 * Use this as a marker supertype for small, lightweight, UI-restorable state.
 */
expect open class SavableObject()
