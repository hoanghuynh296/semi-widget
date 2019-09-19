package vn.semicolon.base.widget.customview

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.accessibility.AccessibilityNodeInfo
import androidx.appcompat.widget.AppCompatTextView
import vn.semicolon.base.widget.R


open class SmartTextView : AppCompatTextView {

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
                R.styleable.SmartTextView, defStyleAttr, 0
            )
            SmartViewHelper.setView(
                this,
                borderColor = typedArray.getColor(
                    R.styleable.SmartTextView_smtv_borderColor,
                    Color.parseColor("#adadad")
                ),
                borderWidth = typedArray.getDimensionPixelOffset(
                    R.styleable.SmartTextView_smtv_borderWidth,
                    0
                ),
                fillColor = typedArray.getColor(
                    R.styleable.SmartTextView_smtv_fillColor,
                    Color.parseColor("#00000000")
                ),
                radius = typedArray.getDimension(R.styleable.SmartTextView_smtv_radius, 0f),
                dashWidth = typedArray.getDimension(R.styleable.SmartTextView_smtv_dashWidth, 0f),
                dashGap = typedArray.getDimension(R.styleable.SmartTextView_smtv_dashGap, 0f),
                shape = typedArray.getInt(
                    R.styleable.SmartTextView_smtv_shape,
                    GradientDrawable.RECTANGLE
                ),
                rippleColor = typedArray.getColor(
                    R.styleable.SmartTextView_smtv_rippleColor,
                    Color.parseColor("#dfdfdf")
                )
            )
            typedArray.recycle()
        }

    }

}