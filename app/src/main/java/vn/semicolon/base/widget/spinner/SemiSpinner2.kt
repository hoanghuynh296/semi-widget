package vn.semicolon.base.widget.spinner

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import vn.semicolon.base.widget.R
import vn.semicolon.base.widget.adapter.BaseAdapter
import vn.semicolon.base.widget.customview.StateTextView
import java.util.*

class SemiSpinner2 : StateTextView {

    companion object {
        private const val MAX_HEIGHT = 500
        private val DEFAULT_TEXT_COLOR = Color.parseColor("#333333")
        private const val DEFAULT_ELEVATION = 3f
        private const val INSTANCE_STATE = "instance_state"
        private const val SELECTED_INDEX = "selected_index"
        private const val IS_POPUP_SHOWING = "is_popup_showing"
        private const val IS_ARROW_HIDDEN = "is_arrow_hidden"
        private const val ARROW_DRAWABLE_RES_ID = "arrow_drawable_res_id"
        private const val ARROW_DISABLE_DRAWABLE_RES_ID = "arrow_disable_drawable_res_id"
        const val VERTICAL_OFFSET = 1
    }

    //Define Item Text
    private var mItemTextColor: Int = Color.BLACK
    private var mItemTextSize: Int = 18
    private var mItemTextSelectedColor: Int = Color.BLACK
    private var selectedPosition = -1
    private var mArrowDrawable: Drawable? = null
    private var isArrowHidden: Boolean = false
    private var mItemBgSelectedColor: Int = Color.TRANSPARENT
    private var mDisplayHeight: Int = 0
    private var mPopupMaxHeight = MAX_HEIGHT
    private var mElevation = DEFAULT_ELEVATION
    private var mArrowDrawableResId: Int = 0
    private var mArrowDisableDrawableResId: Int = 0
    private var parentVerticalOffset: Int = 0
    //
    private var mPopupWindow: SemiPopupWindow? = null

    var adapter: SemiSpinnerAdapter<*> =
        SimpleAdapter()
        set(value) {
            field = value
            field.addOnItemSelectedListener(object : SemiSpinnerAdapter.OnItemSelectedListenter {
                override fun onItemSelected(s: String, pos: Int, v: View) {
                    text = s
                    selectedPosition = pos
                    select(pos)
                    mListener?.onItemClick(pos)
                    dismissDropDown()
                }
            })
            mPopupWindow?.mRecyclerView?.adapter = field
        }

    private var mListener: OnDropDownClickListener? = null
    private var mBackgroundPopupColor: Int = Color.WHITE


    constructor(context: Context) : super(context) {
        init(context, null, 0)
    }

    constructor(context: Context, attributeSet: AttributeSet?) : super(context, attributeSet) {
        init(context, attributeSet, 0)
    }

    constructor(context: Context, attributeSet: AttributeSet?, defStyleAttr: Int) :
            super(context, attributeSet, defStyleAttr) {
        init(context, attributeSet, defStyleAttr)
    }

    private fun init(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int
    ) {
        measureDisplayHeight()
        attrs?.let {
            val typedArray = context.obtainStyledAttributes(
                it,
                R.styleable.SemiSpinner, defStyleAttr, 0
            )
            mItemTextColor =
                typedArray.getColor(
                    R.styleable.SemiSpinner_smsp_itemTextColor,
                    DEFAULT_TEXT_COLOR
                )
            mItemTextSize = typedArray.getDimensionPixelSize(
                R.styleable.SemiSpinner_smsp_itemTextSize,
                18
            )
            mItemTextSelectedColor =
                typedArray.getColor(
                    R.styleable.SemiSpinner_smsp_itemTextSelectedColor,
                    Color.BLACK
                )
            mItemBgSelectedColor =
                typedArray.getColor(
                    R.styleable.SemiSpinner_smsp_itemBgSelectedColor,
                    Color.TRANSPARENT
                )
            isArrowHidden = typedArray.getBoolean(R.styleable.SemiSpinner_smsp_hideArrow, false)
            mArrowDrawableResId = typedArray.getResourceId(
                R.styleable.SemiSpinner_smsp_arrowEnableDrawable,
                R.drawable.ic_arrow_down_black
            )
            mArrowDisableDrawableResId = typedArray.getResourceId(
                R.styleable.SemiSpinner_smsp_arrowDisableDrawable,
                R.drawable.ic_arrow_down_grey
            )
            mPopupMaxHeight = typedArray.getDimensionPixelSize(
                R.styleable.SemiSpinner_smsp_popupMaxHeight,
                getDefaultHeight()
            )
            mElevation =
                typedArray.getDimension(
                    R.styleable.SemiSpinner_smsp_popupElevation,
                    DEFAULT_ELEVATION
                )
            mBackgroundPopupColor = typedArray.getColor(
                R.styleable.SemiSpinner_smsp_bgPopupColor,
                Color.WHITE
            )
            typedArray.recycle()
        }
        initPopup(context)
        setOnClickListener(onClick)
    }

    private fun initPopup(context: Context) {
        mPopupWindow = SemiPopupWindow(context)
        mPopupWindow?.elevation = mElevation
        mPopupWindow?.setBackgroundColor(mBackgroundPopupColor)
        mPopupWindow?.setOnDismissListener {

        }
        mPopupWindow?.mRecyclerView?.adapter = adapter
    }

    fun setBackgroundPopupDrawable(drawable: Drawable) {
        mPopupWindow?.setCardBackgroundDrawable(drawable)
    }

    fun setBackgroundPopupColor(color: Int) {
        mPopupWindow?.setBackgroundColor(color)
    }

    override fun onSaveInstanceState(): Parcelable? {
        val bundle = Bundle()
        bundle.putParcelable(INSTANCE_STATE, super.onSaveInstanceState())
        bundle.putInt(SELECTED_INDEX, selectedPosition)
        bundle.putBoolean(IS_ARROW_HIDDEN, isArrowHidden)
        bundle.putInt(ARROW_DRAWABLE_RES_ID, mArrowDrawableResId)
        bundle.putInt(ARROW_DISABLE_DRAWABLE_RES_ID, mArrowDisableDrawableResId)
        mPopupWindow?.run {
            bundle.putBoolean(IS_POPUP_SHOWING, isShowing)
        }
        return bundle
    }

    override fun onRestoreInstanceState(savedState: Parcelable?) {
        var instanceSave: Parcelable? = null
        if (savedState is Bundle) {
            selectedPosition = savedState.getInt(SELECTED_INDEX)
            if (savedState.getBoolean(IS_POPUP_SHOWING)) {
                mPopupWindow?.run {
                    post { showDropDown() }
                }
            }
            isArrowHidden = savedState.getBoolean(IS_ARROW_HIDDEN, false)
            mArrowDrawableResId = savedState.getInt(ARROW_DRAWABLE_RES_ID)
            mArrowDisableDrawableResId = savedState.getInt(ARROW_DISABLE_DRAWABLE_RES_ID)
            instanceSave = savedState.getParcelable(INSTANCE_STATE)
        }
        super.onRestoreInstanceState(instanceSave)
    }

    override fun onVisibilityChanged(changedView: View, visibility: Int) {
        super.onVisibilityChanged(changedView, visibility)
        mArrowDrawable = getDrawableFromDrawable()
        setArrowDrawableOrHide(mArrowDrawable)
    }

    override fun setEnabled(enabled: Boolean) {
        super.setEnabled(enabled)
        if (mArrowDrawableResId > 0 && mArrowDisableDrawableResId > 0) {
            mArrowDrawable = getDrawableFromDrawable()
            setArrowDrawableOrHide(mArrowDrawable)
        }
    }

    private fun getDrawableFromDrawable(): Drawable? {
        return ContextCompat.getDrawable(
            context, if (isEnabled)
                mArrowDrawableResId else
                mArrowDisableDrawableResId
        )
    }

    private fun setArrowDrawableOrHide(drawable: Drawable?) {
        if (!isArrowHidden && drawable != null) {
            setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null)
        } else {
            setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
        }
    }

    fun getSelectedIndex(): Int {
        return selectedPosition
    }

    fun select(pos: Int) {
        doSelect(pos)
    }

    private fun doSelect(pos: Int) {
        val s = adapter.getDisplayAt(pos)
        doOnItemSelected(s, pos)
    }

    private val onClick = OnClickListener {
        if (isEnabled) {
            mPopupWindow?.run {
                if (!isShowing) {
                    showDropDown()
                } else {
                    dismissDropDown()
                }
            }
        }
    }

    fun dismissDropDown() {
        mPopupWindow?.dismiss()
    }

    private fun showDropDown() {
        measurePopUpDimension()
        mPopupWindow?.showAsDropDown(this)
    }

    private fun measurePopUpDimension() {
        val widthSpec = MeasureSpec.makeMeasureSpec(measuredWidth, MeasureSpec.EXACTLY)
        val heightSpec = MeasureSpec.makeMeasureSpec(getPopUpHeight(), MeasureSpec.AT_MOST)
        mPopupWindow?.measureDimension(widthSpec, heightSpec, mPopupMaxHeight)
    }

    private fun getPopUpHeight(): Int {
        return Math.max(verticalSpaceBelow(), verticalSpaceAbove())
    }

    private fun verticalSpaceAbove(): Int {
        return getParentVerticalOffset()
    }

    private fun getParentVerticalOffset(): Int {
        if (parentVerticalOffset > 0) {
            return parentVerticalOffset
        }
        val locationOnScreen = IntArray(2)
        getLocationOnScreen(locationOnScreen)
        parentVerticalOffset = locationOnScreen[VERTICAL_OFFSET]
        return parentVerticalOffset
    }

    private fun verticalSpaceBelow(): Int {
        return mDisplayHeight - getParentVerticalOffset() - measuredHeight
    }

    private fun measureDisplayHeight() {
        mDisplayHeight = context.resources.displayMetrics.heightPixels
    }

    private fun getDefaultHeight(): Int {
        return mDisplayHeight / 3
    }

    fun setOnDropdownItemClick(listener: OnDropDownClickListener?) {
        mListener = listener
    }

    fun setOnDropdownItemClick(listener: (position: Int) -> Unit) {
        mListener = object : OnDropDownClickListener {
            override fun onItemClick(position: Int) {
                listener.invoke(position)
            }
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        mListener = null
        mPopupWindow = null
        mArrowDrawable = null
    }

    private fun doOnItemSelected(s: String, pos: Int) {
        this.text = s
        selectedPosition = pos
        mListener?.onItemClick(pos)
        dismissDropDown()
    }
}


abstract class SemiSpinnerAdapter<O> : BaseAdapter<O>() {
    abstract fun getDisplayAt(pos: Int): String
    fun getSelectedItem() = selectedItem
    private var selectedItem: O? = null
    private val onItemSelectedListeners = ArrayList<OnItemSelectedListenter>()
    fun addOnItemSelectedListener(callback: OnItemSelectedListenter) {
        onItemSelectedListeners.add(callback)
    }

    fun addOnItemSelectedListener(callback: (s: String, pos: Int, v: View) -> Unit) {
        val newCallback = object : OnItemSelectedListenter {
            override fun onItemSelected(s: String, pos: Int, v: View) {
                callback.invoke(s, pos, v)
            }
        }
        onItemSelectedListeners.add(newCallback)
    }


    override fun onItemClick(item: O?, pos: Int, view: View) {
        super.onItemClick(item, pos, view)
        selectedItem = item
        onItemSelectedListeners.forEach {
            it.onItemSelected(getDisplayAt(pos), pos, view)
        }
    }

    interface OnItemSelectedListenter {
        fun onItemSelected(s: String, pos: Int, v: View)
    }
}

class SimpleAdapter : SemiSpinnerAdapter<String>() {
    override fun getDisplayAt(pos: Int): String {
        return getItemAt(pos)!!
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<String> {
        val tv = TextView(parent.context)
        tv.tag = "textView"
        tv.setPadding(20, 20, 20, 20)
        return SimpleViewHolder(tv)
    }

    class SimpleViewHolder(v: View) : BaseViewHolder<String>(v) {
        override fun bindData(data: String) {
            itemView.findViewWithTag<TextView>("textView").text = data
        }
    }
}