package com.aquarids.knocker.ui

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Point
import android.graphics.PointF
import android.support.annotation.IntDef
import android.support.v4.math.MathUtils
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.VelocityTracker
import android.view.ViewConfiguration
import android.view.animation.AnimationUtils
import android.widget.FrameLayout
import android.widget.Scroller
import com.aquarids.knocker.R

class SliderLayout @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : FrameLayout(context, attrs, defStyleAttr) {

    companion object {
        const val NONE = 0x00
        const val LEFT_TO_RIGHT = 0x01
        const val RIGHT_TO_LEFT = 0x10
        const val TOP_TO_BOTTOM = 0x100
        const val BOTTOM_TO_TOP = 0x1000
        const val HORIZONTAL = LEFT_TO_RIGHT or RIGHT_TO_LEFT
        const val VERTICAL = TOP_TO_BOTTOM or BOTTOM_TO_TOP
        const val ALL = HORIZONTAL or VERTICAL
    }

    @IntDef(NONE, LEFT_TO_RIGHT, RIGHT_TO_LEFT, TOP_TO_BOTTOM, BOTTOM_TO_TOP, HORIZONTAL, VERTICAL, ALL)
    @Retention(AnnotationRetention.SOURCE)
    annotation class SliderDirection

    private val mScroller: Scroller
    private var mSlideBoundary: Float

    @SliderDirection
    private var mDirection: Int
    @SliderDirection
    private var mDragDirection: Int = NONE

    private val mViewConfig = ViewConfiguration.get(context)
    private var mTracker: VelocityTracker? = null
    private val mViewInitialPoint = Point()
    private val mFingerCapturePoint = PointF()
    private var mHasScrolled = false
    private var mSlideFinished = true

    var listener: SliderListener? = null

    init {
        val array = context.obtainStyledAttributes(attrs, R.styleable.SliderLayout)
        mSlideBoundary = array.getFraction(R.styleable.SliderLayout_boundary, 1, 1, 0.3f)
        val interpolator = array.getResourceId(R.styleable.SliderLayout_interpolator, android.R.interpolator.decelerate_cubic)
        mScroller = Scroller(context, AnimationUtils.loadInterpolator(context, interpolator))
        mDirection = array.getInt(R.styleable.SliderLayout_direction, LEFT_TO_RIGHT)
        array.recycle()
    }

    override fun computeScroll() {
        super.computeScroll()
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.currX, mScroller.currY)
            postInvalidateOnAnimation()
        } else {
            if (!mSlideFinished) {
                listener?.onSlide(this, mDragDirection, mHasScrolled)

                mDragDirection = NONE
                mHasScrolled = false
                mSlideFinished = true
            }
        }
    }

    override fun onInterceptTouchEvent(motionEvent: MotionEvent?): Boolean {
        val action = motionEvent?.action ?: return super.onInterceptTouchEvent(motionEvent)
        when (action) {
            MotionEvent.ACTION_DOWN -> {
                mViewInitialPoint.set(scrollX, scrollY)
                mFingerCapturePoint.set(motionEvent.rawX, motionEvent.rawY)
            }
            MotionEvent.ACTION_MOVE -> {
                val deltaX = motionEvent.rawX - mFingerCapturePoint.x
                val deltaY = motionEvent.rawY - mFingerCapturePoint.y
                if (Math.hypot(deltaX.toDouble(), deltaY.toDouble()) > mViewConfig.scaledTouchSlop) {
                    val dirX = when {
                        (mDirection and HORIZONTAL) == HORIZONTAL -> if (deltaX > 0) LEFT_TO_RIGHT else RIGHT_TO_LEFT
                        (mDirection and HORIZONTAL) == LEFT_TO_RIGHT && deltaX > 0 -> LEFT_TO_RIGHT
                        (mDirection and HORIZONTAL) == RIGHT_TO_LEFT && deltaX < 0 -> RIGHT_TO_LEFT
                        else -> NONE
                    }
                    val dirY = when {
                        (mDirection and VERTICAL) == VERTICAL -> if (deltaY > 0) TOP_TO_BOTTOM else BOTTOM_TO_TOP
                        (mDirection and VERTICAL) == TOP_TO_BOTTOM && deltaY > 0 -> TOP_TO_BOTTOM
                        (mDirection and VERTICAL) == BOTTOM_TO_TOP && deltaY < 0 -> BOTTOM_TO_TOP
                        else -> NONE
                    }
                    mDragDirection = when {
                        dirX == NONE -> dirY
                        dirY == NONE -> dirX
                        Math.abs(deltaX) > Math.abs(deltaY) -> dirX
                        Math.abs(deltaX) < Math.abs(deltaY) -> dirY
                        else -> NONE
                    }
                }
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                mDragDirection = NONE
            }
        }
        return mDragDirection != NONE
    }


    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        val action = event?.action ?: return super.onTouchEvent(event)
        if (null == mTracker) {
            mTracker = VelocityTracker.obtain()
        }
        mTracker?.addMovement(event)
        when (action) {
            MotionEvent.ACTION_DOWN -> {
                mViewInitialPoint.set(scrollX, scrollY)
                mFingerCapturePoint.set(event.rawX, event.rawY)
            }
            MotionEvent.ACTION_MOVE -> {
                val deltaX = event.rawX - mFingerCapturePoint.x
                val deltaY = event.rawY - mFingerCapturePoint.y
                if (Math.hypot(deltaX.toDouble(), deltaY.toDouble()) > mViewConfig.scaledTouchSlop) {
                    val dirX = when {
                        (mDirection and HORIZONTAL) == HORIZONTAL -> if (deltaX > 0) LEFT_TO_RIGHT else RIGHT_TO_LEFT
                        (mDirection and HORIZONTAL) == LEFT_TO_RIGHT && deltaX > 0 -> LEFT_TO_RIGHT
                        (mDirection and HORIZONTAL) == RIGHT_TO_LEFT && deltaX < 0 -> RIGHT_TO_LEFT
                        else -> NONE
                    }
                    val dirY = when {
                        (mDirection and VERTICAL) == VERTICAL -> if (deltaY > 0) TOP_TO_BOTTOM else BOTTOM_TO_TOP
                        (mDirection and VERTICAL) == TOP_TO_BOTTOM && deltaY > 0 -> TOP_TO_BOTTOM
                        (mDirection and VERTICAL) == BOTTOM_TO_TOP && deltaY < 0 -> BOTTOM_TO_TOP
                        else -> NONE
                    }
                    mDragDirection = when {
                        dirX == NONE -> dirY
                        dirY == NONE -> dirX
                        Math.abs(deltaX) > Math.abs(deltaY) -> dirX
                        Math.abs(deltaX) < Math.abs(deltaY) -> dirY
                        else -> NONE
                    }
                }
                if (mDragDirection != NONE) {
                    var toX = 0f
                    var toY = 0f
                    when (mDragDirection) {
                        LEFT_TO_RIGHT -> toX = MathUtils.clamp(event.rawX - mFingerCapturePoint.x, 0f, width.toFloat())
                        RIGHT_TO_LEFT -> toX = MathUtils.clamp(event.rawX - mFingerCapturePoint.x, -width.toFloat(), 0f)
                        TOP_TO_BOTTOM -> toY = MathUtils.clamp(event.rawY - mFingerCapturePoint.y, 0f, height.toFloat())
                        BOTTOM_TO_TOP -> toY = MathUtils.clamp(event.rawY - mFingerCapturePoint.y, -height.toFloat(), 0f)
                    }
                    scrollTo((mViewInitialPoint.x - toX).toInt(), (mViewInitialPoint.y - toY).toInt())
                }
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                if (mDragDirection != NONE) {
                    mTracker?.let { tracker ->
                        tracker.computeCurrentVelocity(60 * 1000)
                        var toX = 0
                        var toY = 0
                        mHasScrolled = false
                        when (mDragDirection) {
                            LEFT_TO_RIGHT -> {
                                if ((Math.abs(tracker.xVelocity) >= mViewConfig.scaledMinimumFlingVelocity && Math.signum(tracker.xVelocity) > 0)
                                        || (Math.abs(tracker.xVelocity) < mViewConfig.scaledMinimumFlingVelocity && -scrollX > width * mSlideBoundary)) {
                                    mHasScrolled = true
                                    toX = -width
                                }
                            }
                            RIGHT_TO_LEFT -> {
                                if ((Math.abs(tracker.xVelocity) >= mViewConfig.scaledMinimumFlingVelocity && Math.signum(tracker.xVelocity) < 0)
                                        || (Math.abs(tracker.xVelocity) < mViewConfig.scaledMinimumFlingVelocity && -scrollX < -width * mSlideBoundary)) {
                                    mHasScrolled = true
                                    toX = width
                                }
                            }
                            TOP_TO_BOTTOM -> {
                                if ((Math.abs(tracker.yVelocity) >= mViewConfig.scaledMinimumFlingVelocity && Math.signum(tracker.yVelocity) > 0)
                                        || (Math.abs(tracker.yVelocity) < mViewConfig.scaledMinimumFlingVelocity && -scrollY > height * mSlideBoundary)) {
                                    mHasScrolled = true
                                    toY = -height
                                }
                            }
                            BOTTOM_TO_TOP -> {
                                if ((Math.abs(tracker.yVelocity) >= mViewConfig.scaledMinimumFlingVelocity && Math.signum(tracker.yVelocity) < 0)
                                        || (Math.abs(tracker.yVelocity) < mViewConfig.scaledMinimumFlingVelocity && -scrollY < -height * mSlideBoundary)) {
                                    mHasScrolled = true
                                    toY = height
                                }
                            }
                        }

                        mSlideFinished = false
                        mScroller.startScroll(scrollX, scrollY, toX - scrollX, toY - scrollY)
                        invalidate()
                    }
                }
                mTracker?.recycle()
                mTracker = null
            }
        }
        return true
    }

    interface SliderListener {
        fun onSlide(view: SliderLayout, @SliderDirection direction: Int, hasScrolled: Boolean)
    }
}