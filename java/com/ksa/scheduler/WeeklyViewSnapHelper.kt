package com.ksa.scheduler

import android.util.DisplayMetrics
import android.view.View
import android.view.animation.Interpolator
import android.widget.Scroller
import androidx.recyclerview.widget.*
import java.lang.IllegalStateException
import kotlin.math.abs

class WeeklyViewSnapHelper (private val maxFlingBlocks: Int) : SnapHelper() {

    private var recyclerView: RecyclerView? = null
    // Total number of items in a block of view in the RecyclerView
    private var blockSize: Int = 0
    // Maximum number of positions to move on a fling.
    private var maxPositionsToMove: Int = 0
    // Width of a RecyclerView item if orientation is horizontal; height of the item if vertical
    private var itemDimension: Int = 0
    // Callback interface when blocks are snapped.
    private var snapBlockCallback: SnapBlockCallback? = null
    // When snapping, used to determine direction of snap.
    private var priorFirstPosition = RecyclerView.NO_POSITION
    // Our private scroller
    private var scroller: Scroller? = null
    // Horizontal/vertical layout helper
    private var orientationHelper: OrientationHelper? = null

    @Throws(IllegalStateException::class)
    override fun attachToRecyclerView(recyclerView: RecyclerView?) {
        if (recyclerView != null) {
            this.recyclerView = recyclerView
            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
            orientationHelper = when {
                layoutManager.canScrollHorizontally() -> OrientationHelper.createHorizontalHelper(layoutManager)
                else -> throw IllegalStateException("RecyclerView must be scrollable")
            }
            scroller = Scroller(this.recyclerView!!.context, sInterpolator)
            initItemDimensionIfNeeded(layoutManager)
        }
        super.attachToRecyclerView(recyclerView)
    }

    // Called when the target view is available and we need to know how much more
    // to scroll to get it lined up with the side of the RecyclerView.
    override fun calculateDistanceToFinalSnap(layoutManager: RecyclerView.LayoutManager, targetView: View): IntArray {
        val out = IntArray(2)
        if (layoutManager.canScrollHorizontally())
            out[0] = getScrollToAlignView(targetView)
        if (snapBlockCallback != null)
            if (out[0] != 0)
                snapBlockCallback!!.onBlockSnap(layoutManager.getPosition(targetView))
        return out
    }

    // We are flinging and need to know where we are heading.
    override fun findTargetSnapPosition(layoutManager: RecyclerView.LayoutManager, velocityX: Int, velocityY: Int): Int {
        val lm = layoutManager as LinearLayoutManager
        initItemDimensionIfNeeded(layoutManager)
        scroller!!.fling(0, 0, velocityX, velocityY, Integer.MIN_VALUE, Integer.MAX_VALUE, Integer.MIN_VALUE, Integer.MAX_VALUE)
        return when {
            velocityX != 0 -> getPositionsToMove(lm, scroller!!.finalX, itemDimension)
            else -> RecyclerView.NO_POSITION
        }
    }

    // We have scrolled to the neighborhood where we will snap. Determine the snap position.
    override fun findSnapView(layoutManager: RecyclerView.LayoutManager): View? {
        // Snap to a view that is either 1) toward the bottom of the data and therefore on screen,
        // or, 2) toward the top of the data and may be off-screen.
        val snapPos = calcTargetPosition(layoutManager as LinearLayoutManager)
        return if (snapPos == RecyclerView.NO_POSITION)
            null
        else
            layoutManager.findViewByPosition(snapPos)
    }

    // Does the heavy lifting for findSnapView.
    private fun calcTargetPosition(layoutManager: LinearLayoutManager): Int {
        val snapPos: Int
        val firstVisiblePos = layoutManager.findFirstVisibleItemPosition()
        if (firstVisiblePos == RecyclerView.NO_POSITION) {
            return RecyclerView.NO_POSITION
        }
        initItemDimensionIfNeeded(layoutManager)
        if (firstVisiblePos >= priorFirstPosition) {
            // Scrolling toward bottom of data
            val firstCompletePosition = layoutManager.findFirstCompletelyVisibleItemPosition()
            snapPos = if (firstCompletePosition != RecyclerView.NO_POSITION && firstCompletePosition % blockSize == 0)
                firstCompletePosition
            else
                roundDownToBlockSize(firstVisiblePos + blockSize)
        } else {
            // Scrolling toward top of data
            snapPos = roundDownToBlockSize(firstVisiblePos)
            // Check to see if target view exists. If it doesn't, force a smooth scroll.
            // SnapHelper only snaps to existing views and will not scroll to a non-existent one.
            // If limiting fling to single block, then the following is not needed since the
            // views are likely to be in the RecyclerView pool.
            if (layoutManager.findViewByPosition(snapPos) == null) {
                val toScroll = calculateDistanceToScroll(layoutManager, snapPos)
                recyclerView!!.smoothScrollBy(toScroll[0], toScroll[1], sInterpolator)
            }
        }
        priorFirstPosition = firstVisiblePos
        return snapPos
    }

    private fun initItemDimensionIfNeeded(layoutManager: RecyclerView.LayoutManager) {
        if (itemDimension != 0)
            return
        val child = layoutManager.getChildAt(0) ?: return
        if (layoutManager.canScrollHorizontally()) {
            itemDimension = child.width
            blockSize = getSpanCount(layoutManager) * (recyclerView!!.width / itemDimension)
        }
        maxPositionsToMove = blockSize * maxFlingBlocks
    }

    private fun getSpanCount(layoutManager: RecyclerView.LayoutManager): Int = (layoutManager as? GridLayoutManager)?.spanCount ?: 1

    private fun roundDownToBlockSize(trialPosition: Int): Int = trialPosition - trialPosition % blockSize

    private fun roundUpToBlockSize(trialPosition: Int): Int = roundDownToBlockSize(trialPosition + blockSize - 1)

    override fun createScroller(layoutManager: RecyclerView.LayoutManager): LinearSmoothScroller? {
        return if (layoutManager !is RecyclerView.SmoothScroller.ScrollVectorProvider)
            null
        else object : LinearSmoothScroller(recyclerView!!.context) {
            override fun onTargetFound(targetView: View, state: RecyclerView.State, action: Action) {
                val snapDistances = calculateDistanceToFinalSnap(recyclerView!!.layoutManager!!, targetView)
                val dx = snapDistances[0]
                val dy = snapDistances[1]
                val time = calculateTimeForDeceleration(abs(dx).coerceAtLeast(abs(dy)))
                if (time > 0) {
                    action.update(dx, dy, time, sInterpolator)
                }
            }

            override fun calculateSpeedPerPixel(displayMetrics: DisplayMetrics): Float = MILLISECONDS_PER_INCH / displayMetrics.densityDpi
        }
    }

    fun setSnapBlockCallback(callback: SnapBlockCallback?) {
        snapBlockCallback = callback
    }

    private fun getScrollToAlignView(targetView: View): Int {
        return orientationHelper!!.getDecoratedStart(targetView)
    }

    /**
     * Calculate the distance to final snap position when the view corresponding to the snap
     * position is not currently available.
     *
     * @param layoutManager LinearLayoutManager or descendant class
     * @param targetPos     - Adapter position to snap to
     * @return int[2] {x-distance in pixels, y-distance in pixels}
     */
    private fun calculateDistanceToScroll(layoutManager: LinearLayoutManager, targetPos: Int): IntArray {
        val out = IntArray(2)
        val firstVisiblePos = layoutManager.findFirstVisibleItemPosition()
        if (layoutManager.canScrollHorizontally()) {
            if (targetPos <= firstVisiblePos) {  // scrolling toward top of data
                val firstView = layoutManager.findViewByPosition(firstVisiblePos)
                out[0] = orientationHelper!!.getDecoratedStart(firstView) - (firstVisiblePos - targetPos) * itemDimension
            }
        }
        if (layoutManager.canScrollVertically() && targetPos <= firstVisiblePos) { // scrolling toward top of data
            val firstView = layoutManager.findViewByPosition(firstVisiblePos)
            out[1] = firstView!!.top - (firstVisiblePos - targetPos) * itemDimension
        }
        return out
    }

    /*
        Calculate the number of positions to move in the RecyclerView given a scroll amount
        and the size of the items to be scrolled. Return integral multiple of mBlockSize not
        equal to zero.
     */
    private fun getPositionsToMove(llm: LinearLayoutManager, scroll: Int, itemSize: Int): Int {
        var positionsToMove: Int
        positionsToMove = roundUpToBlockSize(abs(scroll) / itemSize)
        if (positionsToMove < blockSize)
        // Must move at least one block
            positionsToMove = blockSize
        else if (positionsToMove > maxPositionsToMove)
        // Clamp number of positions to move so we don't get wild flinging.
            positionsToMove = maxPositionsToMove
        if (scroll < 0)
            positionsToMove *= -1
        return if (isDirectionToBottom(scroll < 0)) {
            // Scrolling toward the bottom of data.
            roundDownToBlockSize(llm.findFirstVisibleItemPosition()) + positionsToMove
        } else roundDownToBlockSize(llm.findLastVisibleItemPosition()) + positionsToMove
        // Scrolling toward the top of the data.
    }

    private fun isDirectionToBottom(velocityNegative: Boolean): Boolean {
        return !velocityNegative
    }

    interface SnapBlockCallback {
        fun onBlockSnap(snapPosition: Int)
    }

    companion object {
        // Borrowed from ViewPager.java
        private val sInterpolator = Interpolator { input ->
            var t = input
            // _o(t) = t * t * ((tension + 1) * t + tension)
            // o(t) = _o(t - 1) + 1
            t -= 1.0f
            t * t * t + 1.0f
        }

        private const val MILLISECONDS_PER_INCH = 100f
    }
}
