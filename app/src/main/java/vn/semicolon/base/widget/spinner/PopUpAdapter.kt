package vn.semicolon.base.widget.spinner

import android.graphics.Color
import android.graphics.Typeface
import android.view.View
import android.view.ViewGroup
import androidx.annotation.ColorInt
import kotlinx.android.synthetic.main.adapter_popup_base_view.view.*
import vn.semicolon.base.widget.R
import vn.semicolon.base.widget.adapter.BaseAdapter

class PopUpAdapter : BaseAdapter<PopUpModel>() {
    private var mColorSelected: Int = Color.TRANSPARENT
    private var mTitleColor: Int = Color.parseColor("#333333")
    private var mSelectedIndex: Int = -1
    private var mTypeface: Typeface? = null

    fun setTypeface(typeface: Typeface?){
        mTypeface = typeface
    }

    fun setTitleColor(@ColorInt color: Int){
        mTitleColor = color
    }

    fun setSelectedColor(@ColorInt color: Int){
        mColorSelected = color
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<PopUpModel> {
        val view = createView(R.layout.adapter_popup_base_view, parent, false)
        return ViewHolder(view)
    }

    inner class ViewHolder(itemView: View) : BaseAdapter.BaseViewHolder<PopUpModel>(itemView) {
        override fun bindData(data: PopUpModel) {
            itemView.popupAdapter_textView.text = data.getDisplay()
            if (mSelectedIndex == adapterPosition) {
                itemView.setBackgroundColor(mColorSelected)
            } else {
                itemView.setBackgroundColor(Color.TRANSPARENT)
            }
            mTypeface?.let {
                itemView.popupAdapter_textView.typeface = it
            }
        }
    }

    fun setSelected(position: Int) {
        val current = mSelectedIndex
        mSelectedIndex = position
        notifyItemChanged(current)
        notifyItemChanged(mSelectedIndex)
    }
}