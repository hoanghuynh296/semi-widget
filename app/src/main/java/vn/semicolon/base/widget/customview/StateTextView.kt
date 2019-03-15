package vn.semicolon.base.widget.customview

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.RippleDrawable
import android.text.InputType
import android.util.AttributeSet
import vn.semicolon.base.widget.R


class StateTextView : SmartTextView {

    private var mBackgroundSelectedColor: Int = Color.parseColor("#0f0f0f")
    private var mBackgroundUnSelectedColor: Int = Color.parseColor("#00000000")
    private var mTextSelectedColor: Int = Color.parseColor("#0f0f0f")
    private var mTextUnSelectedColor: Int = Color.parseColor("#000000")

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
            mBackgroundSelectedColor = typedArray.getColor(
                R.styleable.StateTextView_smtv_backgroundSelectedColor,
                Color.parseColor("#0f0f0f")
            )
            mBackgroundUnSelectedColor = typedArray.getColor(
                R.styleable.StateTextView_smtv_backgroundUnSelectedColor,
                Color.parseColor("#00000000")
            )
            mTextSelectedColor = typedArray.getColor(
                R.styleable.StateTextView_smtv_textSelectedColor,
                Color.parseColor("#000000")
            )
            mTextUnSelectedColor = typedArray.getColor(
                R.styleable.StateTextView_smtv_textUnSelectedColor,
                Color.parseColor("#0f0f0f")
            )
            isSelected = typedArray.getBoolean(R.styleable.StateTextView_smtv_isSelected, false)
            typedArray.recycle()
        }
    }

    override fun setSelected(selected: Boolean) {
        super.setSelected(selected)
        val drawableBg = background
        if (drawableBg is RippleDrawable) {
            val shape = drawableBg.getDrawable(0)
            if (shape is GradientDrawable) {
                shape.setColor(if (selected) mBackgroundSelectedColor else mBackgroundUnSelectedColor)
            }
        }
        this.setTextColor(if (selected) mTextSelectedColor else mTextUnSelectedColor)
        requestLayout()
        invalidate()
    }

}