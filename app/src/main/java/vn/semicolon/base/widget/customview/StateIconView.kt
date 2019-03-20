package vn.semicolon.base.widget.customview

import android.content.Context
import android.util.AttributeSet
import android.widget.ImageView
import vn.semicolon.base.widget.R

class StateIconView : ImageView {

    private var mDrawableSelected: Int = 0
    private var mDrawableUnSelected: Int = 0
    private var mDrawableEnable: Int = 0
    private var mDrawableDisable: Int = 0

    constructor(context: Context) : super(context) {
        init(context, null, 0)
    }

    constructor(context: Context, attributeSet: AttributeSet?) : super(context, attributeSet) {
        init(context, attributeSet, 0)
    }

    constructor(context: Context, attributeSet: AttributeSet?, defStyle: Int) :
            super(context, attributeSet, defStyle) {
        init(context, attributeSet, defStyle)
    }

    private fun init(context: Context, attributeSet: AttributeSet?, defStyleAttr: Int) {
        attributeSet?.let {
            val typedArray = context.obtainStyledAttributes(
                it,
                R.styleable.StateIconView, defStyleAttr, 0
            )
            mDrawableSelected = typedArray.getResourceId(
                R.styleable.StateIconView_smi_drawableSelected, 0
            )
            mDrawableUnSelected = typedArray.getResourceId(
                R.styleable.StateIconView_smi_drawableUnSelected, 0
            )
            if (mDrawableSelected > 0 && mDrawableUnSelected > 0) {
                isSelected = typedArray.getBoolean(
                    R.styleable.StateIconView_smi_isSelected, false
                )
            }
            mDrawableEnable = typedArray.getResourceId(
                R.styleable.StateIconView_smi_drawableEnable, 0
            )
            mDrawableDisable = typedArray.getResourceId(
                R.styleable.StateIconView_smi_drawableDisable, 0
            )
            if (mDrawableEnable > 0
                && mDrawableDisable > 0
            ) {
                isEnabled = typedArray.getBoolean(
                    R.styleable.StateIconView_smi_isEnable, false
                )
            }
            typedArray.recycle()
        }
    }

    override fun setSelected(selected: Boolean) {
        super.setSelected(selected)
        setImageResource(if (selected) mDrawableSelected else mDrawableUnSelected)
    }

    override fun setEnabled(enabled: Boolean) {
        super.setEnabled(enabled)
        setImageResource(if (enabled) mDrawableEnable else mDrawableDisable)
    }
}