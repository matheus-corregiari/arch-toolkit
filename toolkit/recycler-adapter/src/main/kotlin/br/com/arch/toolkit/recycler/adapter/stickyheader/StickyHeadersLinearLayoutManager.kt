package br.com.arch.toolkit.recycler.adapter.stickyheader

import android.content.Context
import android.view.View
import android.view.ViewTreeObserver
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.max
import kotlin.math.min

/**
 * Adds sticky headers capabilities to your [RecyclerView.Adapter]. It must implement [StickyHeaders] to
 * indicate which items are headers.
 */
@Suppress(
    "ReturnCount",
    "UnusedPrivateMember",
    "SameParameterValue",
    "NestedBlockDepth",
    "ComplexMethod",
    "TooManyFunctions"
)
class StickyHeadersLinearLayoutManager<T> :
    LinearLayoutManager where T : RecyclerView.Adapter<*>, T : StickyHeaders {
    private var mAdapter: T? = null

    private var mTranslationX: Float = 0.toFloat()
    private var mTranslationY: Float = 0.toFloat()
    private var stickyHeaderEvaluation: (View) -> Boolean = { true }

    // Header positions for the currently displayed list and their observer.
    private val mHeaderPositions = ArrayList<Int>(0)
    private val mHeaderPositionsObserver = HeaderPositionsAdapterDataObserver()

    // Sticky header's ViewHolder and dirty state.
    private var mStickyHeader: View? = null
    private var mStickyHeaderPosition = RecyclerView.NO_POSITION

    private var mPendingScrollPosition = RecyclerView.NO_POSITION
    private var mPendingScrollOffset = 0

    constructor(context: Context) : super(context)

    constructor(context: Context, orientation: Int, reverseLayout: Boolean) : super(
        context,
        orientation,
        reverseLayout
    )

    /**
     * Offsets the vertical location of the sticky header relative to the its default position.
     */
    fun setStickyHeaderTranslationY(translationY: Float) {
        mTranslationY = translationY
        requestLayout()
    }

    /**
     * Offsets the horizontal location of the sticky header relative to the its default position.
     */
    fun setStickyHeaderTranslationX(translationX: Float) {
        mTranslationX = translationX
        requestLayout()
    }

    /**
     * Offsets the horizontal location of the sticky header relative to the its default position.
     */
    fun setStickyHeaderEvaluation(evaluation: (View) -> Boolean) {
        stickyHeaderEvaluation = evaluation
        requestLayout()
    }

    /**
     * Returns true if `view` is the current sticky header.
     */
    fun isStickyHeader(view: View): Boolean {
        return view === mStickyHeader
    }

    override fun onAttachedToWindow(view: RecyclerView?) {
        super.onAttachedToWindow(view)
        setAdapter(view?.adapter)
    }

    override fun onAdapterChanged(
        oldAdapter: RecyclerView.Adapter<*>?,
        newAdapter: RecyclerView.Adapter<*>?
    ) {
        super.onAdapterChanged(oldAdapter, newAdapter)
        setAdapter(newAdapter)
    }

    @Suppress("UNCHECKED_CAST")
    private fun setAdapter(adapter: RecyclerView.Adapter<*>?) {
        mAdapter?.unregisterAdapterDataObserver(mHeaderPositionsObserver)

        mAdapter = when (adapter) {
            is StickyHeaders -> adapter as T?
            is ConcatAdapter -> adapter.adapters.find { it is StickyHeaders } as? T?
            else -> null
        }

        if (mAdapter != null) {
            mAdapter?.registerAdapterDataObserver(mHeaderPositionsObserver)
            mHeaderPositionsObserver.onChanged()
        } else {
            mHeaderPositions.clear()
        }
    }

    override fun scrollVerticallyBy(
        dy: Int,
        recycler: RecyclerView.Recycler?,
        state: RecyclerView.State?
    ): Int {
        detachStickyHeader()
        val scrolled = super.scrollVerticallyBy(dy, recycler, state)
        attachStickyHeader()

        if (scrolled != 0) {
            updateStickyHeader(recycler, false)
        }

        return scrolled
    }

    override fun scrollHorizontallyBy(
        dx: Int,
        recycler: RecyclerView.Recycler?,
        state: RecyclerView.State?
    ): Int {
        detachStickyHeader()
        val scrolled = super.scrollHorizontallyBy(dx, recycler, state)
        attachStickyHeader()

        if (scrolled != 0) {
            updateStickyHeader(recycler, false)
        }

        return scrolled
    }

    override fun onLayoutChildren(recycler: RecyclerView.Recycler?, state: RecyclerView.State) {
        detachStickyHeader()
        super.onLayoutChildren(recycler, state)
        attachStickyHeader()

        if (!state.isPreLayout) {
            updateStickyHeader(recycler, true)
        }
    }

    override fun scrollToPosition(position: Int) {
        scrollToPositionWithOffset(position, INVALID_OFFSET)
    }

    override fun scrollToPositionWithOffset(position: Int, offset: Int) {
        scrollToPositionWithOffset(position, offset, true)
    }

    private fun scrollToPositionWithOffset(
        position: Int,
        offset: Int,
        adjustForStickyHeader: Boolean
    ) {
        // Reset pending scroll.
        setPendingScroll(RecyclerView.NO_POSITION, INVALID_OFFSET)

        // Adjusting is disabled.
        if (!adjustForStickyHeader) {
            super.scrollToPositionWithOffset(position, offset)
            return
        }

        // There is no header above or the position is a header.
        val headerIndex = findHeaderIndexOrBefore(position)
        if (headerIndex == -1 || findHeaderIndex(position) != -1) {
            super.scrollToPositionWithOffset(position, offset)
            return
        }

        // The position is right below a header, scroll to the header.
        if (findHeaderIndex(position - 1) != -1) {
            super.scrollToPositionWithOffset(position - 1, offset)
            return
        }

        // Current sticky header is the same as at the position. Adjust the scroll offset and reset pending scroll.
        if (mStickyHeader != null && headerIndex == findHeaderIndex(mStickyHeaderPosition)) {
            val adjustedOffset =
                (if (offset != INVALID_OFFSET) offset else 0) + mStickyHeader!!.height
            super.scrollToPositionWithOffset(position, adjustedOffset)
            return
        }

        // Remember this position and offset and scroll to it to trigger creating the sticky header.
        setPendingScroll(position, offset)
        super.scrollToPositionWithOffset(position, offset)
    }

    private fun detachStickyHeader() {
        mStickyHeader?.let {
            it.elevation = 0f
            detachView(it)
        }
    }

    private fun attachStickyHeader() {
        mStickyHeader?.let {
            attachView(it)
            it.elevation = 8f
        }
    }

    /**
     * Updates the sticky header state (creation, binding, display), to be called whenever there's a layout or scroll
     */
    private fun updateStickyHeader(recycler: RecyclerView.Recycler?, layout: Boolean) {
        val headerCount = mHeaderPositions.size
        val childCount = childCount

        if (headerCount > 0 && childCount > 0) {
            // Find first valid child.
            var anchorView: View? = null
            var anchorIndex = -1
            var anchorPos = -1

            for (i in 0 until childCount) {
                val child = getChildAt(i)
                val params = child?.layoutParams as? RecyclerView.LayoutParams

                if (isViewValidAnchor(child, params)) {
                    anchorView = child
                    anchorIndex = i
                    anchorPos = params?.absoluteAdapterPosition
                        ?: error("Unknown adapter position")
                    break
                }
            }

            if (anchorView != null && anchorPos != -1) {
                val headerIndex = findHeaderIndexOrBefore(anchorPos)
                val headerPos = if (headerIndex != -1) mHeaderPositions[headerIndex] else -1
                val nextHeaderPos =
                    if (headerCount > headerIndex + 1) mHeaderPositions[headerIndex + 1] else -1

                // Show sticky header if:
                // - There's one to show;
                // - It's on the edge or it's not the anchor view;
                // - Isn't followed by another sticky header;
                if (headerPos != -1 &&
                    (headerPos != anchorPos || isViewOnBoundary(anchorView)) &&
                    nextHeaderPos != headerPos + 1
                ) {
                    // Ensure existing sticky header, if any, is of correct type.
                    if (mStickyHeader != null &&
                        mAdapter != null &&
                        getItemViewType(mStickyHeader!!) != mAdapter!!.getItemViewType(headerPos)
                    ) {
                        // A sticky header was shown before but is not of the correct type. Scrap it.
                        scrapStickyHeader(recycler)
                    }

                    // Ensure sticky header is created, if absent, or bound, if being laid out or the position changed.
                    if (mStickyHeader == null) {
                        createStickyHeader(recycler, headerPos)
                    } else {
                        if (isStickyHeader(mStickyHeader!!).not()) {
                            updateStickyHeader(recycler, true)
                        }
                    }

                    val mStickyHeader = mStickyHeader ?: return
                    if (layout || getPosition(mStickyHeader) != headerPos) {
                        bindStickyHeader(recycler!!, headerPos)
                    }

                    // Draw the sticky header using translation values which depend on orientation, direction and
                    // position of the next header view.
                    var nextHeaderView: View? = null
                    if (nextHeaderPos != -1) {
                        nextHeaderView = getChildAt(anchorIndex + (nextHeaderPos - anchorPos))
                        // The header view itself is added to the RecyclerView. Discard it if it comes up.
                        if (nextHeaderView === mStickyHeader) {
                            nextHeaderView = null
                        }
                    }
                    mStickyHeader.translationX = getX(mStickyHeader, nextHeaderView)
                    mStickyHeader.translationY = getY(mStickyHeader, nextHeaderView)
                    return
                }
            }
        }

        mStickyHeader?.run { scrapStickyHeader(recycler) }
    }

    /**
     * Creates [RecyclerView.ViewHolder] for `position`, including measure / layout, and assigns it to
     * [.mStickyHeader].
     */
    private fun createStickyHeader(recycler: RecyclerView.Recycler?, position: Int) {
        if (recycler == null) throw KotlinNullPointerException("Null RecyclerView")
        val stickyHeader = recycler.getViewForPosition(position)

        // Setup sticky header if the adapter requires it.
        if (mAdapter is StickyHeaders.ViewSetup) {
            (mAdapter as StickyHeaders.ViewSetup).setupStickyHeaderView(stickyHeader)
        }

        // Add sticky header as a child view, to be detached / reattached whenever LinearLayoutManager#fill() is called,
        // which happens on layout and scroll (see overrides).
        if (mAdapter?.isStickyHeader(position) == true) {
            if (stickyHeaderEvaluation.invoke(stickyHeader)) {
                addView(stickyHeader)
                measureAndLayout(stickyHeader)

                // Ignore sticky header, as it's fully managed by this LayoutManager.
                ignoreView(stickyHeader)

                mStickyHeader = stickyHeader
                mStickyHeaderPosition = position
            } else {
                detachStickyHeader()
            }
        }
    }

    /**
     * Binds the [.mStickyHeader] for the given `position`.
     */
    private fun bindStickyHeader(recycler: RecyclerView.Recycler, position: Int) {
        // Bind the sticky header.
        recycler.bindViewToPosition(mStickyHeader!!, position)
        mStickyHeaderPosition = position
        measureAndLayout(mStickyHeader!!)

        // If we have a pending scroll wait until the end of layout and scroll again.
        if (mPendingScrollPosition != RecyclerView.NO_POSITION) {
            val vto = mStickyHeader!!.viewTreeObserver
            vto.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    vto.removeOnGlobalLayoutListener(this)

                    if (mPendingScrollPosition != RecyclerView.NO_POSITION) {
                        scrollToPositionWithOffset(mPendingScrollPosition, mPendingScrollOffset)
                        setPendingScroll(RecyclerView.NO_POSITION, INVALID_OFFSET)
                    }
                }
            })
        }
    }

    /**
     * Measures and lays out `stickyHeader`.
     */
    private fun measureAndLayout(stickyHeader: View) {
        measureChildWithMargins(stickyHeader, 0, 0)
        if (orientation == RecyclerView.VERTICAL) {
            stickyHeader.layout(paddingLeft, 0, width - paddingRight, stickyHeader.measuredHeight)
        } else {
            stickyHeader.layout(0, paddingTop, stickyHeader.measuredWidth, height - paddingBottom)
        }
    }

    /**
     * Returns [.mStickyHeader] to the [RecyclerView]'s [RecyclerView.RecycledViewPool], assigning it
     * to `null`.
     *
     * @param recycler If passed, the sticky header will be returned to the recycled view pool.
     */
    private fun scrapStickyHeader(recycler: RecyclerView.Recycler?) {
        val stickyHeader = mStickyHeader
        mStickyHeader = null
        mStickyHeaderPosition = RecyclerView.NO_POSITION

        // Revert translation values.
        stickyHeader?.let {
            stickyHeader.translationX = 0f
            stickyHeader.translationY = 0f

            // Teardown holder if the adapter requires it.
            if (mAdapter is StickyHeaders.ViewSetup) {
                (mAdapter as StickyHeaders.ViewSetup).teardownStickyHeaderView(stickyHeader)
            }

            // Stop ignoring sticky header so that it can be recycled.
            stopIgnoringView(stickyHeader)

            // Remove and recycle sticky header.
            removeView(stickyHeader)
            recycler?.recycleView(stickyHeader)
        }
    }

    /**
     * Returns true when `view` is a valid anchor, ie. the first view to be valid and visible.
     */
    private fun isViewValidAnchor(view: View?, params: RecyclerView.LayoutParams?): Boolean {
        return if (view != null && params != null && !params.isItemRemoved && !params.isViewInvalid) {
            if (orientation == RecyclerView.VERTICAL) {
                if (reverseLayout) {
                    view.top + view.translationY <= height + mTranslationY
                } else {
                    view.bottom - view.translationY >= mTranslationY
                }
            } else {
                if (reverseLayout) {
                    view.left + view.translationX <= width + mTranslationX
                } else {
                    view.right - view.translationX >= mTranslationX
                }
            }
        } else {
            false
        }
    }

    /**
     * Returns true when the `view` is at the edge of the parent [RecyclerView].
     */
    private fun isViewOnBoundary(view: View): Boolean {
        return if (orientation == RecyclerView.VERTICAL) {
            if (reverseLayout) {
                view.bottom - view.translationY > height + mTranslationY
            } else {
                view.top + view.translationY < mTranslationY
            }
        } else {
            if (reverseLayout) {
                view.right - view.translationX > width + mTranslationX
            } else {
                view.left + view.translationX < mTranslationX
            }
        }
    }

    /**
     * Returns the position in the Y axis to position the header appropriately, depending on orientation, direction and
     * [android.R.attr.clipToPadding].
     */
    private fun getY(headerView: View, nextHeaderView: View?): Float {
        return if (orientation == RecyclerView.VERTICAL) {
            var y = mTranslationY

            if (reverseLayout) y += height - headerView.height
            nextHeaderView?.let {
                y = if (reverseLayout) {
                    max(it.bottom.toFloat(), y)
                } else {
                    min((it.top - headerView.height).toFloat(), y)
                }
            }

            y
        } else {
            mTranslationY
        }
    }

    /**
     * Returns the position in the X axis to position the header appropriately, depending on orientation, direction and
     * [android.R.attr.clipToPadding].
     */
    private fun getX(headerView: View, nextHeaderView: View?): Float {
        return if (orientation != RecyclerView.VERTICAL) {
            var x = mTranslationX

            if (reverseLayout) x += width - headerView.width
            nextHeaderView?.let {
                x = if (reverseLayout) {
                    max(it.right.toFloat(), x)
                } else {
                    min((it.left - headerView.width).toFloat(), x)
                }
            }

            x
        } else {
            mTranslationX
        }
    }

    /**
     * Finds the header index of `position` in `mHeaderPositions`.
     */
    private fun findHeaderIndex(position: Int): Int {
        var low = 0
        var high = mHeaderPositions.size - 1

        while (low <= high) {
            val middle = (low + high) / 2

            when {
                mHeaderPositions[middle] > position -> high = middle - 1
                mHeaderPositions[middle] < position -> low = middle + 1
                else -> return middle
            }
        }
        return -1
    }

    /**
     * Finds the header index of `position` or the one before it in `mHeaderPositions`.
     */
    private fun findHeaderIndexOrBefore(position: Int): Int {
        var low = 0
        var high = mHeaderPositions.size - 1

        while (low <= high) {
            val middle = (low + high) / 2

            when {
                mHeaderPositions[middle] > position -> high = middle - 1
                middle < mHeaderPositions.size - 1 && mHeaderPositions[middle + 1] <= position ->
                    low = middle + 1

                else -> return middle
            }
        }
        return -1
    }

    /**
     * Finds the header index of `position` or the one next to it in `mHeaderPositions`.
     */
    private fun findHeaderIndexOrNext(position: Int): Int {
        var low = 0
        var high = mHeaderPositions.size - 1
        while (low <= high) {
            val middle = (low + high) / 2

            when {
                middle > 0 && mHeaderPositions[middle - 1] >= position -> high = middle - 1
                mHeaderPositions[middle] < position -> low = middle + 1
                else -> return middle
            }
        }
        return -1
    }

    private fun setPendingScroll(position: Int, offset: Int) {
        mPendingScrollPosition = position
        mPendingScrollOffset = offset
    }

    /**
     * Handles header positions while adapter changes occur.
     *
     * This is used in detriment of [RecyclerView.LayoutManager]'s callbacks to control when they're received.
     */
    private inner class HeaderPositionsAdapterDataObserver : RecyclerView.AdapterDataObserver() {
        override fun onChanged() {
            // There's no hint at what changed, so go through the adapter.
            mHeaderPositions.clear()
            val itemCount = mAdapter?.itemCount ?: 0
            for (i in 0 until itemCount) {
                if (mAdapter!!.isStickyHeader(i)) {
                    mHeaderPositions.add(i)
                }
            }

            // Remove sticky header immediately if the entry it represents has been removed. A layout will follow.
            if (mStickyHeader != null && !mHeaderPositions.contains(mStickyHeaderPosition)) {
                scrapStickyHeader(null)
            }
        }

        override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
            // Shift headers below down.
            val headerCount = mHeaderPositions.size
            if (headerCount > 0) {
                var i = findHeaderIndexOrNext(positionStart)

                while (i != -1 && i < headerCount) {
                    mHeaderPositions[i] = mHeaderPositions[i] + itemCount
                    i++
                }
            }

            // Add new headers.
            for (i in positionStart until positionStart + itemCount) {
                if ((mAdapter?.itemCount ?: 0) > i && mAdapter?.isStickyHeader(i) == true) {
                    val headerIndex = findHeaderIndexOrNext(i)
                    if (headerIndex != -1) {
                        mHeaderPositions.add(headerIndex, i)
                    } else {
                        mHeaderPositions.add(i)
                    }
                }
            }
        }

        override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
            var headerCount = mHeaderPositions.size
            if (headerCount > 0) {
                // Remove headers.
                for (i in positionStart + itemCount - 1 downTo positionStart) {
                    val index = findHeaderIndex(i)
                    if (index != -1) {
                        mHeaderPositions.removeAt(index)
                        headerCount--
                    }
                }

                // Remove sticky header immediately if the entry it represents has been removed. A layout will follow.
                if (mStickyHeader != null && !mHeaderPositions.contains(mStickyHeaderPosition)) {
                    scrapStickyHeader(null)
                }

                // Shift headers below up.
                var i = findHeaderIndexOrNext(positionStart + itemCount)
                while (i != -1 && i < headerCount) {
                    mHeaderPositions[i] = mHeaderPositions[i] - itemCount
                    i++
                }
            }
        }

        override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) {
            // Shift moved headers by toPosition - fromPosition.
            // Shift headers in-between by -itemCount (reverse if upwards).
            val headerCount = mHeaderPositions.size
            if (headerCount > 0) {
                if (fromPosition < toPosition) {
                    var i = findHeaderIndexOrNext(fromPosition)

                    while (i != -1 && i < headerCount) {
                        val headerPos = mHeaderPositions[i]

                        if (headerPos >= fromPosition && headerPos < fromPosition + itemCount) {
                            mHeaderPositions[i] = headerPos - (toPosition - fromPosition)
                            sortHeaderAtIndex(i)
                        } else if (headerPos >= fromPosition + itemCount && headerPos <= toPosition) {
                            mHeaderPositions[i] = headerPos - itemCount
                            sortHeaderAtIndex(i)
                        } else {
                            break
                        }

                        i++
                    }
                } else {
                    var i = findHeaderIndexOrNext(toPosition)
                    while (i != -1 && i < headerCount) {
                        val headerPos = mHeaderPositions[i]

                        if (headerPos >= fromPosition && headerPos < fromPosition + itemCount) {
                            mHeaderPositions[i] = headerPos + (toPosition - fromPosition)
                            sortHeaderAtIndex(i)
                        } else if (headerPos in toPosition..fromPosition) {
                            mHeaderPositions[i] = headerPos + itemCount
                            sortHeaderAtIndex(i)
                        } else {
                            break
                        }

                        i++
                    }
                }
            }
        }

        private fun sortHeaderAtIndex(index: Int) {
            val headerPos = mHeaderPositions.removeAt(index)
            val headerIndex = findHeaderIndexOrNext(headerPos)
            if (headerIndex != -1) {
                mHeaderPositions.add(headerIndex, headerPos)
            } else {
                mHeaderPositions.add(headerPos)
            }
        }
    }
}
