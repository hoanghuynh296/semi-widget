package vn.semicolon.base.widget.spinner

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.widget.PopupWindow
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import vn.semicolon.base.widget.R

class SemiPopupWindow : PopupWindow {

    internal var mRecyclerView: RecyclerView? = null

    constructor(context: Context) : super(context) {
        setBackgroundDrawable(null)
        elevation = 3f
        isOutsideTouchable = true
        isFocusable = true
        val view = LayoutInflater.from(context)
            .inflate(R.layout.popup_window_view, null, false)
        mRecyclerView = view.findViewById(R.id.popUpWindow_recyclerView)
        mRecyclerView?.layoutManager = LinearLayoutManager(context)
        contentView = view
    }

    fun <VH : RecyclerView.ViewHolder> setAdapter(adapter: RecyclerView.Adapter<VH>) {
        mRecyclerView?.adapter = adapter
    }

    fun setCardBackgroundDrawable(drawable: Drawable) {
        mRecyclerView?.background = drawable
    }

    fun setBackgroundColor(color: Int){
        mRecyclerView?.setBackgroundColor(color)
    }

    fun measureDimension(widthSpec: Int, heightSpec: Int, maxHeight: Int): Int {
        mRecyclerView?.apply {
            mRecyclerView?.measure(widthSpec, heightSpec)
            this@SemiPopupWindow.width = measuredWidth
            this@SemiPopupWindow.height = if (measuredHeight > maxHeight) maxHeight else measuredHeight
            return measuredHeight
        }
        return -1
    }
}