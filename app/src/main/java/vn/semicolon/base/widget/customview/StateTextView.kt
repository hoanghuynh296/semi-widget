package vn.semicolon.base.widget.customview

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.RippleDrawable
import android.text.InputType
import android.util.AttributeSet
import vn.semicolon.base.widget.R


class StateTextView : SmartTextView {

    private var mBackgroundActiveColor: Int = Color.parseColor("#0f0f0f")
    private var mBackgroundInInActiveColor: Int = Color.parseColor("#00000000")
    private var mTextActiveColor: Int = Color.parseColor("#0f0f0f")
    private var mTextInActiveColor: Int = Color.parseColor("#000000")

    constructor(context: Context) : super(context) {
        init(context, null, 0)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context, attrs, 0)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
            super(context, attrs, defStyleAttr) {
        init(context, attrs, defStyleAttr)
    }

    private fun init(context: Context, attrs: AttributeSet?, defStyleAttr: Int) {
        attrs?.let {
            val typedArray = context.obtainStyledAttributes(attrs, R.styleable.StateTextView, defStyleAttr, 0)
            mBackgroundActiveColor = typedArray.getColor(
                R.styleable.StateTextView_smtv_backgroundActiveColor,
                Color.parseColor("#0f0f0f")
            )
            mBackgroundInInActiveColor = typedArray.getColor(
                R.styleable.StateTextView_smtv_backgroundInActiveColor,
                Color.parseColor("#00000000")
            )
            mTextActiveColor = typedArray.getColor(
                R.styleable.StateTextView_smtv_textActiveColor,
                Color.parseColor("#000000")
            )
            mTextInActiveColor = typedArray.getColor(
                R.styleable.StateTextView_smtv_textInActiveColor,
                Color.parseColor("#0f0f0f")
            )
            typedArray.recycle()
        }
        isSelected = false
    }

    override fun setSelected(selected: Boolean) {
        super.setSelected(selected)
        val drawableBg = background
        if (drawableBg is RippleDrawable) {
            val shape = drawableBg.getDrawable(0)
            if (shape is GradientDrawable) {
                shape.setColor(if (selected) mBackgroundActiveColor else mBackgroundInInActiveColor)
            }
        }
        this.setTextColor(if (selected) mTextActiveColor else mTextInActiveColor)
        requestLayout()
        invalidate()
    }

}