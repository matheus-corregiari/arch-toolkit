@file:Suppress(
    "EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING",
    "PARCELABLE_PRIMARY_CONSTRUCTOR_IS_EMPTY"
)

package br.com.arch.toolkit.stateHandle

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
actual open class SavableObject actual constructor() : Parcelable
