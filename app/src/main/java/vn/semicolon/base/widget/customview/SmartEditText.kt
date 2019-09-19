package vn.semicolon.base.widget.customview

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import vn.semicolon.base.widget.R


open class SmartEditText : AppCompatEditText {

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
                R.styleable.SmartEditText, defStyleAttr, 0
            )
            SmartViewHelper.setView(
                this,
                borderColor = typedArray.getColor(
                    R.styleable.SmartEditText_smet_borderColor,
                    Color.parseColor("#adadad")
                ),
                borderWidth = typedArray.getDimensionPixelOffset(
                    R.styleable.SmartEditText_smet_borderWidth,
                    0
                ),
                fillColor = typedArray.getColor(
                    R.styleable.SmartEditText_smet_fillColor,
                    Color.parseColor("#00000000")
                ),
                radius = typedArray.getDimension(R.styleable.SmartEditText_smet_radius, 0f),
                dashWidth = typedArray.getDimension(
                    R.styleable.SmartEditText_smet_dashWidth,
                    0f
                ),
                dashGap = typedArray.getDimension(R.styleable.SmartEditText_smet_dashGap, 0f),
                shape = typedArray.getInt(
                    R.styleable.SmartEditText_smet_shape,
                    GradientDrawable.RECTANGLE
                ),
                rippleColor = typedArray.getColor(
                    R.styleable.SmartEditText_smet_rippleColor,
                    Color.parseColor("#dfdfdf")
                )
            )
            typedArray.recycle()
        }

    }
}