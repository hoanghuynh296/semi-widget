package  vn.semicolon.base.widget.adapter

import android.view.View

/**
 * *******************************************
 * * Created by Simon on 4/4/18.
 * * All rights reserved
 * *******************************************
 */
interface OnItemClickListener<O> {
    fun onItemClick(item: O?, pos: Int, view: View)
}