package vn.semicolon.base.widget.customview

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.RippleDrawable
import android.text.InputType
import android.util.AttributeSet
import vn.semicolon.base.widget.R


open class StateTextView : SmartTextView {
    private val DEFAULT_ACTIVE_COLOR = Color.parseColor("#0f0f0f")
    private val DEFAULT_INACTIVE_COLOR = Color.parseColor("#00000000")
    private val DEFAULT_TEXT_ACTIVE_COLOR = Color.parseColor("#000000")
    private val DEFAULT_TEXT_INACTIVE_COLOR = Color.parseColor("#0f0f0f")
    private var mBackgroundSelectedColor: Int = DEFAULT_ACTIVE_COLOR
    private var mBackgroundUnSelectedColor: Int = DEFAULT_INACTIVE_COLOR
    private var mTextSelectedColor: Int = DEFAULT_TEXT_ACTIVE_COLOR
    private var mTextUnSelectedColor: Int = DEFAULT_TEXT_INACTIVE_COLOR
    //Enable state
    private var mBackgroundEnableColor: Int = DEFAULT_ACTIVE_COLOR
    private var mBackgroundDisableColor: Int = DEFAULT_INACTIVE_COLOR
    private var mTextEnableColor: Int = DEFAULT_TEXT_ACTIVE_COLOR
    private var mTextDisableColor: Int = DEFAULT_TEXT_INACTIVE_COLOR

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
            //Selected State
            val typedArray = context.obtainStyledAttributes(attrs, R.styleable.StateTextView, defStyleAttr, 0)
            mBackgroundSelectedColor = typedArray.getColor(
                R.styleable.StateTextView_smtv_backgroundSelectedColor,
                DEFAULT_ACTIVE_COLOR
            )
            mBackgroundUnSelectedColor = typedArray.getColor(
                R.styleable.StateTextView_smtv_backgroundUnSelectedColor,
                DEFAULT_INACTIVE_COLOR
            )
            mTextSelectedColor = typedArray.getColor(
                R.styleable.StateTextView_smtv_textSelectedColor,
                DEFAULT_TEXT_ACTIVE_COLOR
            )
            mTextUnSelectedColor = typedArray.getColor(
                R.styleable.StateTextView_smtv_textUnSelectedColor,
                DEFAULT_TEXT_INACTIVE_COLOR
            )
            if ((mBackgroundSelectedColor != DEFAULT_ACTIVE_COLOR &&
                        mTextSelectedColor != DEFAULT_TEXT_ACTIVE_COLOR) ||
                (mBackgroundUnSelectedColor != DEFAULT_INACTIVE_COLOR &&
                        mTextUnSelectedColor != DEFAULT_TEXT_INACTIVE_COLOR)
            ) {
                isSelected = typedArray.getBoolean(R.styleable.StateTextView_smtv_isSelected, false)
            }
            //Enable State
            mBackgroundEnableColor = typedArray.getColor(
                R.styleable.StateTextView_smtv_backgroundEnableColor,
                DEFAULT_ACTIVE_COLOR
            )
            mBackgroundDisableColor = typedArray.getColor(
                R.styleable.StateTextView_smtv_backgroundDisableColor,
                DEFAULT_INACTIVE_COLOR
            )
            mTextEnableColor = typedArray.getColor(
                R.styleable.StateTextView_smtv_textEnableColor,
                DEFAULT_TEXT_ACTIVE_COLOR
            )
            mTextDisableColor = typedArray.getColor(
                R.styleable.StateTextView_smtv_textDisableColor,
                DEFAULT_TEXT_INACTIVE_COLOR
            )
            if ((mBackgroundEnableColor != DEFAULT_ACTIVE_COLOR &&
                        mTextEnableColor != DEFAULT_TEXT_ACTIVE_COLOR) ||
                (mBackgroundDisableColor != DEFAULT_INACTIVE_COLOR &&
                        mTextDisableColor != DEFAULT_TEXT_INACTIVE_COLOR)
            ) {
                updateEnableColor(isEnabled)
            }
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

    override fun setEnabled(enabled: Boolean) {
        super.setEnabled(enabled)
        updateEnableColor(enabled)
    }

    private fun updateEnableColor(enabled: Boolean) {
        val drawableBg = background
        if (drawableBg is RippleDrawable) {
            val shape = drawableBg.getDrawable(0)
            if (shape is GradientDrawable) {
                shape.setColor(if (enabled) mBackgroundEnableColor else mBackgroundDisableColor)
            }
        }
        this.setTextColor(if (enabled) mTextEnableColor else mTextDisableColor)
        requestLayout()
        invalidate()
    }
}