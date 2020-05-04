package vn.semicolon.base.widget.customview

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.widget.ImageView
import vn.semicolon.base.widget.R

class StateIconView : androidx.appcompat.widget.AppCompatImageView {

    private var mDrawableSelected: Int = 0
    private var mDrawableUnSelected: Int = 0
    private var mDrawableEnable: Int = 0
    private var mDrawableDisable: Int = 0
    private var mBackgroundRadius = 0f
    private var mBackgroundSelectedColor = Color.TRANSPARENT
    private var mBackgroundUnSelectedColor = Color.TRANSPARENT
    private var mDrawableSelectedTint: Int = -1
    private var mDrawableUnSelectedTint: Int = -1
    private var mDrawableEnableTint: Int = -1
    private var mDrawableDisableTint: Int = -1

    /**
     * @link android.graphics.drawable.GradientDrawable
     */
    private var mBackgroundShape = 0

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
            //Background
            mBackgroundRadius = typedArray.getDimension(
                    R.styleable.StateIconView_smi_backgroundRadius, 0f
            )
            mBackgroundSelectedColor = typedArray.getColor(
                    R.styleable.StateIconView_smi_backgroundSelectedColor,
                    Color.TRANSPARENT
            )
            mBackgroundUnSelectedColor = typedArray.getColor(
                    R.styleable.StateIconView_smi_backgroundUnSelectedColor,
                    Color.TRANSPARENT
            )
            mBackgroundShape = typedArray.getColor(
                    R.styleable.StateIconView_smi_backgroundShape,
                    GradientDrawable.RECTANGLE
            )
            // tint
            mDrawableSelectedTint = typedArray.getResourceId(
                    R.styleable.StateIconView_smi_drawableSelectedTint, mDrawableSelectedTint
            )
            mDrawableUnSelectedTint = typedArray.getResourceId(
                    R.styleable.StateIconView_smi_drawableUnSelectedTint, mDrawableUnSelectedTint
            )
            mDrawableEnableTint = typedArray.getResourceId(
                    R.styleable.StateIconView_smi_drawableEnableTint, mDrawableEnableTint
            )
            mDrawableDisableTint = typedArray.getResourceId(
                    R.styleable.StateIconView_smi_drawableDisableTint, mDrawableDisableTint
            )
            typedArray.recycle()
        }
    }

    override fun setSelected(selected: Boolean) {
        super.setSelected(selected)
        setImageResource(if (selected) mDrawableSelected else mDrawableUnSelected)
        background = createBackgroundShape()
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        val tint = when {
            isEnabled -> mDrawableEnableTint
            !isEnabled -> mDrawableDisableTint
            isSelected -> mDrawableSelectedTint
            !isSelected -> mDrawableUnSelectedTint
            else -> -1
        }
        if (tint != -1)
            setColorFilter(tint)
        else clearColorFilter()
    }

    override fun setEnabled(enabled: Boolean) {
        super.setEnabled(enabled)
        setImageResource(if (enabled) mDrawableEnable else mDrawableDisable)
    }

    private fun createBackgroundShape(): Drawable {
        val drawable = GradientDrawable()
        drawable.shape = mBackgroundShape
        drawable.cornerRadius = mBackgroundRadius
        drawable.setColor(if (isSelected) mBackgroundSelectedColor else mBackgroundUnSelectedColor)
        return drawable
    }
}