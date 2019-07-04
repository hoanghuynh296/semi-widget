package vn.semicolon.base.widget.customview

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.RippleDrawable
import android.util.AttributeSet
import androidx.cardview.widget.CardView
import vn.semicolon.base.widget.R

class SmartCardView : CardView {

    private var mBorderColor: Int = Color.parseColor("#ADADAD")
    private var mBorderWidth: Int = 0
    private var mShape = GradientDrawable.RECTANGLE
    private var mDashWidth = 0f
    private var mDashGap = 0f
    private var mRippleColor: Int = Color.parseColor("#DFDFDF")

    constructor(context: Context) : super(context) {
        init(null, 0)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(attrs, 0)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
            super(context, attrs, defStyleAttr) {
        init(attrs, defStyleAttr)
    }

    private fun init(attrs: AttributeSet?, defStyleAttr: Int) {
        attrs?.let {
            val typedArray = context.obtainStyledAttributes(
                attrs,
                R.styleable.SmartCardView, defStyleAttr, 0
            )
            mBorderColor = typedArray.getColor(
                R.styleable.SmartTextView_smtv_borderColor,
                Color.parseColor("#ADADAD")
            )
            mBorderWidth = typedArray.getDimensionPixelOffset(R.styleable.SmartCardView_smcv_borderWidth, 0)
            mDashWidth = typedArray.getDimension(R.styleable.SmartCardView_smcv_dashWidth, 0f)
            mDashGap = typedArray.getDimension(R.styleable.SmartCardView_smcv_dashGap, 0f)
            mShape = typedArray.getInt(R.styleable.SmartCardView_smcv_shape, GradientDrawable.RECTANGLE)
            mRippleColor = typedArray.getColor(
                R.styleable.SmartCardView_smcv_rippleColor,
                Color.parseColor("#DFDFDF")
            )
            typedArray.recycle()
        }
        //
        customRippleBackground()
    }

    private fun customRippleBackground() {
        val shape = GradientDrawable()
        shape.shape = mShape
        shape.cornerRadius = radius
        shape.setStroke(mBorderWidth, mBorderColor, mDashWidth, mDashGap)
        shape.color = cardBackgroundColor

        val rippleBackground = RippleDrawable(
            getPressedColorSelector(
                Color.parseColor("#00000000"),
                mRippleColor
            ),
            shape, shape
        )
        foreground = rippleBackground
    }

    private fun getPressedColorSelector(normalColor: Int, pressedColor: Int): ColorStateList {
        return ColorStateList(
            arrayOf(
                intArrayOf(),
                intArrayOf(android.R.attr.state_pressed)
            ),
            intArrayOf(pressedColor, pressedColor, pressedColor, normalColor)
        )
    }
}