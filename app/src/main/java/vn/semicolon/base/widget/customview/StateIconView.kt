package vn.semicolon.base.widget.customview

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.widget.ImageView
import androidx.core.widget.ImageViewCompat
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
            isSelected = typedArray.getBoolean(
                R.styleable.StateIconView_smi_isSelected, false
            )
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
            mDrawableSelectedTint = typedArray.getColor(
                R.styleable.StateIconView_smi_drawableSelectedTint, mDrawableSelectedTint
            )
            mDrawableUnSelectedTint = typedArray.getColor(
                R.styleable.StateIconView_smi_drawableUnSelectedTint, mDrawableUnSelectedTint
            )
            mDrawableEnableTint = typedArray.getColor(
                R.styleable.StateIconView_smi_drawableEnableTint, mDrawableEnableTint
            )
            mDrawableDisableTint = typedArray.getColor(
                R.styleable.StateIconView_smi_drawableDisableTint, mDrawableDisableTint
            )
            typedArray.recycle()
        }
        onEnableChanged()
        onSelectedChanged()

    }

    override fun setSelected(selected: Boolean) {
        super.setSelected(selected)
        onSelectedChanged()
    }

    private fun onSelectedChanged() {
        if (mDrawableSelected != 0 && mDrawableUnSelected != 0)
            setImageResource(if (isSelected) mDrawableSelected else mDrawableUnSelected)
        val tint = if (isSelected) mDrawableSelectedTint else mDrawableUnSelectedTint
        if (tint != -1)
            ImageViewCompat.setImageTintList(this, ColorStateList.valueOf(tint))
        else clearColorFilter()
        createBackgroundShape()?.let {
            background = it
        }
    }

    private fun onEnableChanged() {
        if (mDrawableEnable != 0 && mDrawableDisable != 0)
            setImageResource(if (isEnabled) mDrawableEnable else mDrawableDisable)
        val tint = if (isEnabled) mDrawableEnableTint else mDrawableDisableTint
        if (tint != -1)
            ImageViewCompat.setImageTintList(this, ColorStateList.valueOf(tint))
        else clearColorFilter()
    }

    override fun setEnabled(enabled: Boolean) {
        super.setEnabled(enabled)
        onEnableChanged()
    }

    private fun createBackgroundShape(): Drawable? {
        if (mBackgroundShape == 0) return background
        val drawable = GradientDrawable()
        drawable.shape = mBackgroundShape
        drawable.cornerRadius = mBackgroundRadius
        drawable.setColor(if (isSelected) mBackgroundSelectedColor else mBackgroundUnSelectedColor)
        return drawable
    }
}