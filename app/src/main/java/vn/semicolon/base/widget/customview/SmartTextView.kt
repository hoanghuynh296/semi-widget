package vn.semicolon.base.widget.customview

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.RippleDrawable
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import vn.semicolon.base.widget.R


class SmartTextView : AppCompatTextView {

    private var mBorderColor: Int = Color.parseColor("#adadad")
    private var mBorderWidth: Int = 0
    private var mFillColor: Int = Color.parseColor("#00000000")
    private var mRadius: Float = 0f
    private var mShape = GradientDrawable.RECTANGLE
    private var mDashWidth = 0f
    private var mDashGap = 0f
    private var mRippleColor: Int = Color.parseColor("#dfdfdf")

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
            super(context, attrs, defStyleAttr) {
        this.init(context, attrs, defStyleAttr)
    }

    private fun init(context: Context, attrs: AttributeSet?, defStyleAttr: Int) {
        attrs?.let {
            val typedArray = context.obtainStyledAttributes(attrs, R.styleable.SmartTextView, defStyleAttr, 0)
            mBorderColor = typedArray.getColor(
                R.styleable.SmartTextView_smtv_borderColor,
                Color.parseColor("#adadad")
            )
            mBorderWidth = typedArray.getDimensionPixelOffset(R.styleable.SmartTextView_smtv_borderWidth, 0)
            mFillColor = typedArray.getColor(
                R.styleable.SmartTextView_smtv_fillColor,
                Color.parseColor("#00000000")
            )
            mRadius = typedArray.getDimension(R.styleable.SmartTextView_smtv_radius, 0f)
            mDashWidth = typedArray.getDimension(R.styleable.SmartTextView_smtv_dashWidth, 0f)
            mDashGap = typedArray.getDimension(R.styleable.SmartTextView_smtv_dashGap, 0f)
            mShape = typedArray.getInt(R.styleable.SmartTextView_smtv_shape, GradientDrawable.RECTANGLE)
            mRippleColor = typedArray.getColor(
                R.styleable.SmartTextView_smtv_rippleColor,
                Color.parseColor("#dfdfdf")
            )
            typedArray.recycle()
        }
        val shape = GradientDrawable()
        shape.shape = mShape
        shape.cornerRadius = mRadius
        shape.setStroke(mBorderWidth, mBorderColor, mDashWidth, mDashGap)
        shape.setColor(mFillColor)

        val rippleBackground = RippleDrawable(
            getPressedColorSelector(
                mRippleColor,
                Color.parseColor("#00000000")
            ),
            shape, shape
        )
        this.background = rippleBackground
    }

    fun getPressedColorSelector(normalColor: Int, pressedColor: Int): ColorStateList {
        return ColorStateList(
            arrayOf(
                intArrayOf(android.R.attr.state_pressed),
                intArrayOf(android.R.attr.state_focused),
                intArrayOf(android.R.attr.state_activated),
                intArrayOf()
            ),
            intArrayOf(pressedColor, pressedColor, pressedColor, normalColor)
        )
    }

}