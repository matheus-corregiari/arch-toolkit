package br.com.arch.toolkit.recyclerAdapter.stickyheader

import android.view.View

/**
 * Adds sticky headers capabilities to the [androidx.recyclerview.widget.RecyclerView.Adapter]. Should return `true` for all
 * positions that represent sticky headers.
 */
interface StickyHeaders {

    fun isStickyHeader(position: Int): Boolean

    interface ViewSetup {
        /**
         * Adjusts any necessary properties of the `holder` that is being used as a sticky header.
         *
         * [.teardownStickyHeaderView] will be called sometime after this method
         * and before any other calls to this method go through.
         */
        fun setupStickyHeaderView(stickyHeader: View)

        /**
         * Reverts any properties changed in [.setupStickyHeaderView].
         *
         * Called after [.setupStickyHeaderView].
         */
        fun teardownStickyHeaderView(stickyHeader: View)
    }
}
