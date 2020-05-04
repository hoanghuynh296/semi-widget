package vn.semicolon.base.widget.customview

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.RippleDrawable
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatAutoCompleteTextView
import vn.semicolon.base.widget.R


open class SmartAutoCompleteTextView : AppCompatAutoCompleteTextView {

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
                R.styleable.SmartAutoCompleteTextView, defStyleAttr, 0
            )
            SmartViewHelper.setView(
                this,
                borderColor = typedArray.getColor(
                    R.styleable.SmartAutoCompleteTextView_smactv_borderColor,
                    Color.parseColor("#adadad")
                ),
                borderWidth = typedArray.getDimensionPixelOffset(
                    R.styleable.SmartAutoCompleteTextView_smactv_borderWidth,
                    0
                ),
                fillColor = typedArray.getColor(
                    R.styleable.SmartAutoCompleteTextView_smactv_fillColor,
                    Color.parseColor("#00000000")
                ),
                radius = typedArray.getDimension(R.styleable.SmartAutoCompleteTextView_smactv_radius, 0f),
                dashWidth = typedArray.getDimension(
                    R.styleable.SmartAutoCompleteTextView_smactv_dashWidth,
                    0f
                ),
                dashGap = typedArray.getDimension(R.styleable.SmartAutoCompleteTextView_smactv_dashGap, 0f),
                shape = typedArray.getInt(
                    R.styleable.SmartAutoCompleteTextView_smactv_shape,
                    GradientDrawable.RECTANGLE
                ),
                rippleColor = typedArray.getColor(
                    R.styleable.SmartAutoCompleteTextView_smactv_rippleColor,
                    Color.parseColor("#dfdfdf")
                )
            )
            typedArray.recycle()
        }
    }

    override fun setBackgroundColor(color: Int) {
        val drawableBg = background
        if (drawableBg is RippleDrawable) {
            val shape = drawableBg.getDrawable(0)
            if (shape is GradientDrawable) {
                shape.setColor(color)
            }
        }
    }
}