@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package br.com.arch.toolkit.sample.github.shared.structure.core

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment

actual sealed class FeatureContent
class IntentContent(val create: (Context, Bundle) -> Intent) : FeatureContent()
class FragmentContent(val create: (Bundle) -> Fragment) : FeatureContent()
