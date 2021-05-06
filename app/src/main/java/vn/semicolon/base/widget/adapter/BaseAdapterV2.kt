package vn.semicolon.base.widget.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import vn.semicolon.baseui.adapter.OnItemLongClickListener


interface IAmAdapterV2<O> : OnItemClickListener<O> {

    fun isExist(item: O): Boolean

    fun replaceAt(newItem: O, index: Int): Boolean
    fun replaceAtAndNotify(newItem: O, index: Int): Boolean
    fun addAll(items: Collection<O>)
    fun addAllAndNotify(items: Collection<O>)

    fun addAll(index: Int, items: Collection<O>)
    fun addAllAndNotify(index: Int, items: Collection<O>)

    fun add(item: O)
    fun addAndNotify(item: O)

    fun addToFirst(item: O)
    fun addToFirstAndNotify(item: O)
    fun addToLast(item: O)
    fun addToLastAndNotify(item: O)

    fun getLastItem(): O?

    fun getFirstItem(): O?
    fun getItemAt(position: Int): O?

    fun add(index: Int, item: O)
    fun addAndNotify(index: Int, item: O)
    fun remove(item: O): Boolean
    fun removeAndNotify(item: O): Boolean

    fun removeAt(index: Int): O?
    fun removeAtAndNotify(index: Int): O?

    fun clear()
    fun clearAndNotify()

    fun createView(resource: Int, parent: ViewGroup, attachToRoot: Boolean = false): View {
        return LayoutInflater.from(parent.context).inflate(resource, parent, attachToRoot)
    }
}


abstract class BaseCoreAdapter<O> :
    androidx.recyclerview.widget.RecyclerView.Adapter<BaseCoreAdapter.BaseCoreViewHolder<O>>(),
    OnItemClickListener<O>,
    IAmAdapterV2<O> {
    open var preventDoubleClick = true
    protected open var mItemClickListener: OnItemClickListener<O>? = null
    protected open var mItemLongClickListener: OnItemLongClickListener<O>? = null

    open fun setOnItemClickListener(listener: OnItemClickListener<O>?) {
        mItemClickListener = listener
    }

    open fun setOnItemLongClickListener(listener: OnItemLongClickListener<O>) {
        mItemLongClickListener = listener
    }


    override fun onItemClick(item: O?, pos: Int, view: View) {
        mItemClickListener?.onItemClick(getItemAt(pos), pos, view)
    }

    override fun onBindViewHolder(holder: BaseCoreViewHolder<O>, position: Int) {
        getItemAt(position)?.let { item ->
            holder.bindData(item)
            holder.itemView.setOnClickListener {
                if (preventDoubleClick)
                    it.preventDoubleTap()
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
    }

    abstract class BaseCoreViewHolder<in O>(itemView: View) :
        androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
        abstract fun bindData(data: O)
    }

}

abstract class BaseListAdapter<O> : BaseCoreAdapter<O>() {
    open var data: MutableList<O> = ArrayList()

    override fun isExist(item: O): Boolean {
        return data.contains(item)
    }

    override fun addAll(items: Collection<O>) {
        data.addAll(items)
    }

    override fun addAll(index: Int, items: Collection<O>) {
        data.addAll(index, items)
    }

    override fun addAllAndNotify(index: Int, items: Collection<O>) {
        addAll(index, items)
        notifyItemRangeInserted(index, index + items.size)
    }

    override fun add(item: O) {
        data.add(item)
    }

    override fun addToFirst(item: O) {
        data.add(0, item)
    }

    override fun addToLast(item: O) {
        data.add(itemCount, item)
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
    }

    override fun remove(item: O): Boolean {
        return doRemove(item)
    }

    override fun removeAt(index: Int): O? {
        return doRemoveAt(index)
    }

    protected open fun doRemove(
        item: O,
        onDeleteComplete: ((index: Int) -> Unit)? = null
    ): Boolean {
        val index = data.indexOf(item)
        if (index == -1) return false
        val result = doRemoveAt(index, onDeleteComplete)
        return result != null
    }

    protected open fun doRemoveAt(
        index: Int,
        onDeleteComplete: ((index: Int) -> Unit)? = null
    ): O? {
        if (index < data.size && index >= 0) {
            val item = data.removeAt(index)
            onDeleteComplete?.invoke(index)
            return item
        }
        return null
    }

    override fun replaceAt(newItem: O, index: Int): Boolean {
        return doReplaceAt(newItem, index)
    }

    protected open fun doReplaceAt(
        newItem: O,
        index: Int,
        onReplaceSuccess: ((index: Int) -> Unit)? = null
    ): Boolean {
        if (index < 0 || index >= itemCount) return false
        data[index] = newItem
        onReplaceSuccess?.invoke(index)
        return true
    }

    override fun replaceAtAndNotify(newItem: O, index: Int): Boolean {
        return doReplaceAt(newItem, index) {
            notifyItemChanged(index)
        }
    }

    override fun clear() {
        data.clear()
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun addAllAndNotify(items: Collection<O>) {
        addAll(items)
        notifyDataSetChanged()
    }

    override fun addAndNotify(item: O) {
        add(item)
        notifyItemInserted(data.size - 1)
    }

    override fun addToFirstAndNotify(item: O) {
        addToFirst(item)
        notifyItemInserted(0)
    }

    override fun addToLastAndNotify(item: O) {
        addToLast(item)
        notifyItemInserted(itemCount)
    }

    override fun addAndNotify(index: Int, item: O) {
        add(index, item)
        notifyItemInserted(index)
    }

    override fun removeAndNotify(item: O): Boolean {
        return doRemove(item) {
            notifyItemRemoved(it)
        }
    }

    override fun removeAtAndNotify(index: Int): O? {
        return doRemoveAt(index) {
            notifyItemRemoved(it)
        }
    }

    override fun clearAndNotify() {
        clear()
        notifyDataSetChanged()
    }

}

fun View.preventDoubleTap(preventTime: Long = 300) {
    isClickable = false
    isEnabled = false
    postDelayed({
        isClickable = true
        isEnabled = true
    }, preventTime)
}

