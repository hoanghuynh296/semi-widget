package vn.semicolon.base.widget.adapter

import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import vn.semicolon.baseui.adapter.OnItemLongClickListener
import java.util.*


abstract class BaseAdapter<O> : RecyclerView.Adapter<BaseAdapter.BaseViewHolder<O>>(),
    OnItemClickListener<O>,
    IAmAdapter<O> {
    override val data: MutableList<O> = ArrayList()
    private var mItemClickListener: OnItemClickListener<O>? = null
    private var mItemLongClickListener: OnItemLongClickListener<O>? = null

    fun setOnItemClickListener(listener: OnItemClickListener<O>) {
        mItemClickListener = listener
    }

    /**
     * @author HuynhMH
     * @since 27/11/2019
     */
    fun setOnItemClickListener(onClick: (item: O?, pos: Int, view: View) -> Unit) {
        mItemClickListener = object : OnItemClickListener<O> {
            override fun onItemClick(item: O?, pos: Int, view: View) {
                onClick.invoke(item, pos, view)
            }
        }
    }

    /**
     * @author HuynhMH
     * @since 27/11/2019
     */
    fun setOnItemLongClickListener(onLongClick: (item: O?, pos: Int, view: View) -> Unit) {
        mItemLongClickListener = object : OnItemLongClickListener<O> {
            override fun onItemLongClick(item: O?, pos: Int, view: View) {
                onLongClick.invoke(item, pos, view)
            }
        }
    }

    fun setOnItemLongClickListener(listener: OnItemLongClickListener<O>) {
        mItemLongClickListener = listener
    }

    override fun isExist(item: O): Boolean {
        return data.contains(item)
    }

    override fun addAll(items: Collection<O>) {
        val size = data.size
        data.addAll(items)
        notifyItemRangeInserted(size, items.size)
    }

    override fun add(item: O) {
        data.add(item)
        notifyItemInserted(data.size - 1)
    }

    override fun addToFirst(item: O) {
        data.add(0, item)
        notifyItemInserted(0)
    }

    override fun addToLast(item: O) {
        data.add(itemCount, item)
        notifyItemInserted(itemCount)
    }

    override fun getLastItem(): O? {
        return if (data.size > 0) data[data.size - 1] else null
    }

    override fun getFirstItem(): O? {
        return if (data.size > 0) data[0] else null
    }

    override fun getItemAt(position: Int): O? {
        return if (position < data.size && position >= 0) data[position] else null
    }

    override fun add(index: Int, item: O) {
        if (index < 0) return
        data.add(index, item)
        notifyItemInserted(index)
    }

    override fun remove(item: O): O {
        val index = data.indexOf(item)
        if (index == -1)
            throw Exception("Can't remove item ${item.toString()}, item not found")
        data.remove(item)
        notifyItemRemoved(index)
        return item
    }

    override fun removeAt(index: Int): O {
        if (index < data.size && index >= 0) {
            val item = data[index]
            data.removeAt(index)
            notifyItemRemoved(index)
            return item
        }
        throw Exception("Can't remove item at index $index, item not found")
    }

    override fun onItemClick(item: O?, pos: Int, view: View) {
        mItemClickListener?.onItemClick(getItemAt(pos), pos, view)
    }

    override fun clear() {
        data.clear()
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: BaseViewHolder<O>, position: Int) {
        val item = getItemAt(holder.adapterPosition)
        holder.bindData(item!!)
        holder.itemView.setOnClickListener {
            onItemClick(item, holder.adapterPosition, holder.itemView)
        }
        holder.itemView.setOnLongClickListener {
            mItemLongClickListener?.onItemLongClick(
                getItemAt(holder.adapterPosition),
                holder.adapterPosition,
                holder.itemView
            )
            true
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    abstract class BaseViewHolder<in O>(itemView: View) : RecyclerView.ViewHolder(itemView) {
        abstract fun bindData(data: O)
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