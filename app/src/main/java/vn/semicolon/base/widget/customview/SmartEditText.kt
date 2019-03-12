package vn.semicolon.base.widget.customview

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.RippleDrawable
import android.text.InputType
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatTextView
import vn.semicolon.base.widget.R


open class SmartEditText : AppCompatEditText {

    private var mBorderColor: Int = Color.parseColor("#adadad")
    private var mBorderWidth: Int = 0
    private var mFillColor: Int = Color.parseColor("#00000000")
    private var mRadius: Float = 0f
    private var mShape = GradientDrawable.RECTANGLE
    private var mDashWidth = 0f
    private var mDashGap = 0f
    private var mRippleColor: Int = Color.parseColor("#dfdfdf")

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
        val helper = SmartTextViewHelper(this)
        helper.loadFromAttributes(attrs, defStyleAttr)
    }
}