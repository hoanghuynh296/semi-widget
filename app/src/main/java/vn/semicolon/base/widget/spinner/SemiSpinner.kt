package vn.semicolon.base.widget.spinner

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Parcelable
import android.util.AttributeSet
import androidx.annotation.DrawableRes
import vn.semicolon.base.widget.customview.SmartTextView
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import android.view.MotionEvent
import androidx.core.graphics.BitmapCompat
import androidx.recyclerview.widget.RecyclerView
import vn.semicolon.base.widget.R
import vn.semicolon.base.widget.adapter.OnItemClickListener

class SemiSpinner : SmartTextView {

    companion object {
        private const val MAX_HEIGHT = 500
        private val DEFAULT_TEXT_COLOR = Color.parseColor("#333333")
        private const val DEFAULT_ELEVATION = 3f
        private const val INSTANCE_STATE = "instance_state"
        private const val SELECTED_INDEX = "selected_index"
        private const val IS_POPUP_SHOWING = "is_popup_showing"
        private const val IS_ARROW_HIDDEN = "is_arrow_hidden"
        private const val ARROW_DRAWABLE_RES_ID = "arrow_drawable_res_id"
        const val VERTICAL_OFFSET = 1
    }

    //Define Item Text
    private var mItemTextColor: Int = Color.BLACK
    private var mItemTextSize: Int = 18
    private var mItemTextSelectedColor: Int = Color.BLACK
    private var mSelectedIndex = 0
    private var mArrowDrawable: Drawable? = null
    private var isArrowHidden: Boolean = false
    private var mItemBgSelectedColor: Int = Color.TRANSPARENT
    private var mDisplayHeight: Int = 0
    private var mPopupMaxHeight = MAX_HEIGHT
    private var mElevation = DEFAULT_ELEVATION
    @DrawableRes
    private var mArrowDrawableResId: Int = 0
    private var parentVerticalOffset: Int = 0
    //
    private var mPopupWindow: SemiPopupWindow? = null
    private var mAdapter: PopUpAdapter? = null
    private var mListener: OnDropDownClickListener? = null
    private var mItems: MutableList<PopUpModel>? = null
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
                R.styleable.SemiSpinner_smsp_arrowDrawable,
                R.drawable.ic_arrow_down_black
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
    }

    private fun initAdapter() {
        mAdapter = PopUpAdapter()
        mItems?.let {
            mAdapter?.addAll(it)
        }
        mAdapter?.setTitleColor(mItemTextColor)
        mAdapter?.setSelectedColor(mItemBgSelectedColor)
        mAdapter?.setOnItemClickListener(object : OnItemClickListener<PopUpModel> {
            override fun onItemClick(item: PopUpModel?, pos: Int, view: View) {
                mSelectedIndex = pos
                setSelectedIndex(pos)
                mListener?.onItemClick(pos)
                dismissDropDown()
            }
        })
    }

    private fun initPopup(context: Context) {
        mPopupWindow = SemiPopupWindow(context)
        mPopupWindow?.elevation = mElevation
        mPopupWindow?.setBackgroundColor(mBackgroundPopupColor)
        mPopupWindow?.setOnDismissListener {

        }
    }

    fun setBackgroundPopupDrawable(drawable: Drawable){
        mPopupWindow?.setCardBackgroundDrawable(drawable)
    }

    fun setBackgroundPopupColor(color: Int){
        mPopupWindow?.setBackgroundColor(color)
    }

    fun setItems(items: MutableList<PopUpModel>) {
        mItems = items
    }

    override fun onSaveInstanceState(): Parcelable? {
        val bundle = Bundle()
        bundle.putParcelable(INSTANCE_STATE, super.onSaveInstanceState())
        bundle.putInt(SELECTED_INDEX, mSelectedIndex)
        bundle.putBoolean(IS_ARROW_HIDDEN, isArrowHidden)
        bundle.putInt(ARROW_DRAWABLE_RES_ID, mArrowDrawableResId)
        mPopupWindow?.run {
            bundle.putBoolean(IS_POPUP_SHOWING, isShowing)
        }
        return bundle
    }

    override fun onRestoreInstanceState(savedState: Parcelable?) {
        var instanceSave: Parcelable? = null
        if (savedState is Bundle) {
            mSelectedIndex = savedState.getInt(SELECTED_INDEX)
            if (savedState.getBoolean(IS_POPUP_SHOWING)) {
                mPopupWindow?.run {
                    post { showDropDown() }
                }
            }
            isArrowHidden = savedState.getBoolean(IS_ARROW_HIDDEN, false)
            mArrowDrawableResId = savedState.getInt(ARROW_DRAWABLE_RES_ID)
            instanceSave = savedState.getParcelable(INSTANCE_STATE)
        }
        super.onRestoreInstanceState(instanceSave)
    }

    override fun onVisibilityChanged(changedView: View, visibility: Int) {
        super.onVisibilityChanged(changedView, visibility)
        mArrowDrawable = getDrawableFromDrawable()
        setArrowDrawableOrHide(mArrowDrawable)
    }

    private fun getDrawableFromDrawable(): Drawable? {
        return ContextCompat.getDrawable(context, mArrowDrawableResId)
    }

    private fun setArrowDrawableOrHide(drawable: Drawable?) {
        if (!isArrowHidden && drawable != null) {
            setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null)
        } else {
            setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
        }
    }

    fun getSelectedIndex(): Int {
        return mSelectedIndex
    }

    fun setSelectedIndex(position: Int) {
        mAdapter?.setSelected(position)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (isEnabled && event.action == MotionEvent.ACTION_DOWN) {
            mPopupWindow?.run {
                if (!isShowing) {
                    showDropDown()
                } else {
                    dismissDropDown()
                }
                return true
            }
        }
        return super.onTouchEvent(event)
    }

    fun dismissDropDown() {
        mPopupWindow?.dismiss()
    }

    private fun showDropDown() {
        if (mPopupWindow?.mRecyclerView?.adapter == null) {
            if (mAdapter == null) {
                initAdapter()
                mPopupWindow?.mRecyclerView?.adapter = mAdapter
            }
        }
        measurePopUpDimension()
        mPopupWindow?.showAsDropDown(this)
    }

    private fun measurePopUpDimension() {
        val widthSpec = View.MeasureSpec.makeMeasureSpec(measuredWidth, View.MeasureSpec.EXACTLY)
        val heightSpec = View.MeasureSpec.makeMeasureSpec(getPopUpHeight(), View.MeasureSpec.AT_MOST)
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

    //TODO: Handle after
    fun <VH : RecyclerView.ViewHolder> setCustomAdapter(adapter: RecyclerView.Adapter<VH>) {
        mPopupWindow?.mRecyclerView?.adapter = adapter
    }

    fun setOnDropdownItemClick(listener: OnDropDownClickListener?) {
        mListener = listener
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        mListener = null
        mPopupWindow = null
        mArrowDrawable = null
    }

    fun setItemTextTypeface(typeface: Typeface?) {
        mAdapter?.setTypeface(typeface)
    }
}