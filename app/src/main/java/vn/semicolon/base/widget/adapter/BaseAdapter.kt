package vn.semicolon.base.widget.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import vn.semicolon.baseui.adapter.OnItemLongClickListener
import java.util.*


abstract class BaseAdapter<O> : RecyclerView.Adapter<BaseAdapter.BaseViewHolder<O>>(), OnItemClickListener<O>,
    IAmAdapter<O> {
    override val data: MutableList<O> = ArrayList()
    private var mItemClickListener: OnItemClickListener<O>? = null
    private var mItemLongClickListener: OnItemLongClickListener<O>? = null

    fun setOnItemClickListener(listener: OnItemClickListener<O>) {
        mItemClickListener = listener
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

    override fun remove(item: O): Boolean {
        val index = data.indexOf(item)
        if (index == -1) return false
        data.remove(item)
        notifyItemRemoved(index)
        return true
    }

    override fun removeAt(index: Int): Boolean {
        if (index < data.size && index >= 0) {
            notifyItemRemoved(index)
            return true
        }
        return false
    }

    override fun onItemClick(item: O?, pos: Int, view: View) {
        mItemClickListener?.onItemClick(getItemAt(pos), pos, view)
    }

    override fun clear() {
        data.clear()
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: BaseViewHolder<O>, position: Int) {
        val item = getItemAt(position)
        holder.bindData(item!!)
        holder.itemView.setOnClickListener {
            onItemClick(item, position, holder.itemView)
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

