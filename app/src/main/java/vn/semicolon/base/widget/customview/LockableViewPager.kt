package vn.semicolon.base.widget.customview

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import androidx.viewpager.widget.ViewPager
import vn.semicolon.base.widget.R
import android.view.MotionEvent


class LockableViewPager : ViewPager {
    private var isLockableSwipe = true

    constructor(context: Context) : super(context) {
        init(context, null)
    }

    constructor(context: Context, attributeSet: AttributeSet?) : super(context, attributeSet) {
        init(context, attributeSet)
    }

    private fun init(context: Context, attributeSet: AttributeSet?) {
        attributeSet?.let {
            val typedArray = context.obtainStyledAttributes(
                attributeSet,
                R.styleable.LockableViewPager, 0, 0
            )
            isLockableSwipe = typedArray.getBoolean(R.styleable.LockableViewPager_lvp_isLockSwipe, true)
            typedArray.recycle()
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        return !isLockableSwipe && super.onTouchEvent(event)
    }

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        return !isLockableSwipe && super.onInterceptTouchEvent(event)
    }

    override fun canScrollHorizontally(direction: Int): Boolean {
        return !isLockableSwipe && super.canScrollHorizontally(direction)
    }

    fun setLockableSwipe(isLockable: Boolean) {
        isLockableSwipe = isLockable
    }
}