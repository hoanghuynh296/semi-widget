package  vn.semicolon.baseui.adapter

import android.view.View

/**
 * Create by HuynhMH at 01/08/2018
 */
interface OnItemLongClickListener<O> {
    fun onItemLongClick(item: O?, pos: Int, view: View)
}