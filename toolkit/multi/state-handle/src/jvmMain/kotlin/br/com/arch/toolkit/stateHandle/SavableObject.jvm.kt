@file:Suppress(
    "EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING",
    "SerialVersionUIDInSerializableClass"
)

package br.com.arch.toolkit.stateHandle

import java.io.Serializable

actual open class SavableObject actual constructor() : Serializable
