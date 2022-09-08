package br.com.arch.toolkit.foldable.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.window.layout.FoldingFeature
import br.com.arch.toolkit.delegate.viewProvider
import br.com.arch.toolkit.foldable.R
import br.com.arch.toolkit.foldable.extension.handleFoldableStateChange

open class FoldableActivity : AppCompatActivity(R.layout.activity_foldable) {

    protected open val orientation: FoldingFeature.Orientation = FoldingFeature.Orientation.VERTICAL

    @LayoutRes
    protected open val startViewId: Int = -1

    @LayoutRes
    protected open val endViewId: Int = -1

    @LayoutRes
    protected open val topViewId: Int = -1

    @LayoutRes
    protected open val bottomViewId: Int = -1

    private val layoutRes: Int
        get() = if (orientation == FoldingFeature.Orientation.VERTICAL) {
            R.layout.include_vertical_foldable_activity
        } else {
            R.layout.include_horizontal_foldable_activity
        }

    private val root: ViewGroup by viewProvider(R.id.root)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        LayoutInflater.from(this).inflate(layoutRes, root, true)

        if (orientation == FoldingFeature.Orientation.VERTICAL) setUpVerticalViewStubs()
        else setUpHorizontalViewStubs()

        handleFoldableStateChange(root, R.id.fold_guide, orientation)
    }

    private fun setUpVerticalViewStubs() {
        val startLayout = findViewById<FrameLayout>(R.id.start_layout)
        val endLayout = findViewById<FrameLayout>(R.id.end_layout)

        if (startViewId != -1) View.inflate(this, startViewId, startLayout)
        if (endViewId != -1) View.inflate(this, endViewId, endLayout)
    }

    private fun setUpHorizontalViewStubs() {
        val topLayout = findViewById<FrameLayout>(R.id.top_layout)
        val bottomViewStub = findViewById<FrameLayout>(R.id.bottom_layout)

        if (topViewId != -1) View.inflate(this, topViewId, topLayout)
        if (bottomViewId != -1) View.inflate(this, bottomViewId, bottomViewStub)
    }
}