package vn.semicolon.base.widget.adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import vn.semicolon.baseui.adapter.OnItemLongClickListener

/**
 * Use for list has F = Footer, H = Header
 * Use Nothing to present mHeader or mFooter if not exist
 * Ex: BaseAdapterHasHF<Nothing,String,String>
 */
abstract class BaseAdapterHasHF<H, O, F> : RecyclerView.Adapter<RecyclerView.ViewHolder>(), IAmAdapterHasHF<O> {
    protected var mHeader: H? = null
    protected var mFooter: F? = null
    private var onFooterClickListener: OnItemClickListener<F>? = null
    private var onHeaderClickListener: OnItemClickListener<H>? = null
    private var onItemClickListener: OnItemClickListener<O>? = null
    private var onItemLongClickListener: OnItemClickListener<O>? = null
    override val data: MutableList<O> = ArrayList()
    protected val dataSize: Int
        get() {
            return data.size
        }
    open val headerLayoutResId: Int = -1
    open val footerLayoutResId: Int = -1
    abstract val bodyLayoutResId: Int


    companion object {
        const val HEADER = 0
        const val FOOTER = 1
        const val BODY = 2
    }

    protected var recyclerView: RecyclerView? = null
    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        this.recyclerView = recyclerView
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        this.recyclerView = null
        super.onDetachedFromRecyclerView(recyclerView)
    }

    fun setHeader(header: H?) {
        this.mHeader = header
        notifyHeaderChanged()
    }

    fun setFooter(footer: F?) {
        this.mFooter = footer
        notifyFooterChanged()
    }

    private val hasHeader: Boolean
        get() {
            return headerLayoutResId != -1
        }
    private val hasFooter: Boolean
        get() {
            return footerLayoutResId != -1
        }

    /**
     * Note that this function return data size include mHeader & mFooter
     */
    override fun getItemCount(): Int {
        if (mHeader == null && mFooter == null)
            return dataSize
        if (mHeader == null || mFooter == null)
            return dataSize + 1
        return dataSize + 2
    }

    open fun setOnHeaderClickListener(callback: OnItemClickListener<H>) {
        onHeaderClickListener = callback
    }

    override fun getItemViewType(position: Int): Int {
        if (position == 0 && hasHeader) return HEADER
        if (position == itemCount && hasFooter) return FOOTER
        return BODY
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is BaseViewHolder<*>) {
            when (getItemViewType(position)) {
                HEADER -> {
                    holder.itemView.setOnClickListener {
                        onHeaderClickListener?.onItemClick(mHeader, holder.adapterPosition, holder.itemView)
                    }
                    (holder as BaseViewHolder<H>).bindData(mHeader)
                }
                BODY -> {
                    holder.itemView.setOnClickListener {
                        onItemClickListener?.onItemClick(getItemAt(getDataPositionOfFromAdapterPosition(holder.adapterPosition)), holder.adapterPosition, holder.itemView)
                    }
                    (holder as BaseViewHolder<O>).bindData(getItemAt(position))
                }
                FOOTER -> {
                    onFooterClickListener?.onItemClick(mFooter, holder.adapterPosition, holder.itemView)
                    (holder as BaseViewHolder<F>).bindData(mFooter)
                }
            }
        } else {
            throw Exception("Your ViewHolder must extends BaseAdapterHasHF.BaseViewHolder")
        }
    }

    open fun onCreateHeaderHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<H>? {
        return null
    }

    open fun onCreateFooterHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<F>? {
        return null
    }

    abstract fun onCreateBodyHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when (viewType) {
            HEADER -> return onCreateHeaderHolder(parent, viewType)!!
            FOOTER -> return onCreateFooterHolder(parent, viewType)!!
        }
        return onCreateBodyHolder(parent, viewType)
    }

    open fun setOnFooterClickListener(callback: OnItemClickListener<F>) {
        onFooterClickListener = callback
    }


    fun setOnItemClickListener(listener: OnItemClickListener<O>) {
        onItemClickListener = listener
    }

    fun setOnItemLongClickListener(listener: OnItemLongClickListener<O>) {
    }

    override fun isExist(item: O): Boolean {
        return data.contains(item)
    }

    override fun addAll(items: Collection<O>) {
        val start = dataSize
        data.addAll(items)
        notifyHFItemDataRangeInsert(start, dataSize, true)
    }

    override fun add(item: O) {
        addToLast(item)
    }

    override fun addToFirst(item: O) {
        data.add(0, item)
        notifyHFItemDataInserted(0, true)
    }

    override fun addToLast(item: O) {
        data.add(dataSize, item)
        notifyHFItemDataInserted(dataSize, true)
    }

    override fun getLastItem(): O? {
        return if (dataSize > 0) data[dataSize - 1] else null
    }

    override fun getFirstItem(): O? {
        return if (dataSize > 0) data[0] else null
    }

    override fun getItemAt(position: Int): O? {
        return if (dataSize > position) data[position] else null
    }

    override fun add(index: Int, item: O) {
        data.add(index, item)
        notifyHFItemDataInserted(index, true)
    }

    override fun remove(item: O): Boolean {
        val index = data.indexOf(item)
        if (index == -1) return false
        data.remove(item)
        notifyHFItemDataRemoved(index, true)
        return true
    }

    override fun removeAt(index: Int): Boolean {
        if (index < data.size && index >= 0) {
            notifyHFItemDataRemoved(index, true)
            return true
        }
        return false
    }

    /**
     * Because we has mHeader or mFooter
     */
    private fun getDataPositionOfFromAdapterPosition(position: Int): Int {
        if (hasHeader)
            return position - 1
        return position
    }

    private fun getAdapterPositionOfFromDataPosition(position: Int): Int {
        if (hasHeader)
            return position + 1
        return position
    }

    override fun onItemClick(item: O?, pos: Int, view: View) {

    }

    override fun clear() {
        data.clear()
        notifyHFDataChanged()
    }

    fun clearIncludeHF() {
        mHeader = null
        mFooter = null
        clear()
    }


    override fun addByAdapterPosition(index: Int, data: O) {
        val i = getDataPositionOfFromAdapterPosition(index)
        this.data.add(i, data)
        notifyHFItemDataInserted(index)
    }

    override fun removeByAdapterPosition(index: Int) {
        val i = getDataPositionOfFromAdapterPosition(index)
        this.data.removeAt(i)
        notifyHFItemDataRemoved(index)
    }

    /**
     * Notify ============================================
     */

    override fun notifyHFDataChanged() {
        recyclerView?.post {
            notifyDataSetChanged()
        }
    }

    override fun notifyFooterChanged() {
        if (hasFooter)
            notifyHFItemDataChanged(itemCount)
    }

    override fun notifyHeaderChanged() {
        if (hasHeader)
            notifyHFItemDataChanged(0)
    }

    override fun notifyHFItemDataChanged(index: Int, dataPosition: Boolean) {
        recyclerView?.post {
            if (!dataPosition) {
                notifyItemChanged(index)
                return@post
            }
            notifyItemChanged(getAdapterPositionOfFromDataPosition(index))
        }
    }

    override fun notifyHFItemDataRemoved(index: Int, dataPosition: Boolean) {
        recyclerView?.post {
            if (!dataPosition) {
                notifyItemChanged(index)
                return@post
            }
            notifyItemRemoved(getAdapterPositionOfFromDataPosition(index))
        }
    }

    override fun notifyHFItemDataInserted(index: Int, dataPosition: Boolean) {
        recyclerView?.post {
            if (!dataPosition) {
                notifyItemInserted(index)
                return@post
            }
            notifyItemInserted(getAdapterPositionOfFromDataPosition(index))
        }
    }


    override fun notifyHFItemDataMoved(fromIndex: Int, toIndex: Int, dataPosition: Boolean) {
        recyclerView?.post {
            if (!dataPosition) {
                notifyItemMoved(fromIndex, toIndex)
                return@post
            }
            notifyItemMoved(getAdapterPositionOfFromDataPosition(fromIndex), getDataPositionOfFromAdapterPosition(toIndex))
        }
    }

    override fun notifyHFItemDataRangeChanged(startIndex: Int, endIndex: Int, dataPosition: Boolean) {
        recyclerView?.post {
            if (!dataPosition) {
                notifyItemRangeChanged(startIndex, endIndex)
                return@post
            }
            notifyItemRangeChanged(getAdapterPositionOfFromDataPosition(startIndex), getDataPositionOfFromAdapterPosition(endIndex))
        }
    }

    override fun notifyHFItemDataRangeInsert(startIndex: Int, endIndex: Int, dataPosition: Boolean) {
        recyclerView?.post {
            if (!dataPosition) {
                notifyItemRangeInserted(startIndex, endIndex)
                return@post
            }
            notifyItemRangeInserted(getAdapterPositionOfFromDataPosition(startIndex), getDataPositionOfFromAdapterPosition(endIndex))
        }
    }

    override fun notifyHFItemDataRangeRemoved(startIndex: Int, endIndex: Int, dataPosition: Boolean) {
        recyclerView?.post {
            if (!dataPosition) {
                notifyItemRangeRemoved(startIndex, endIndex)
                return@post
            }
            notifyItemRangeRemoved(getAdapterPositionOfFromDataPosition(startIndex), getDataPositionOfFromAdapterPosition(endIndex))
        }
    }

    abstract class BaseViewHolder<in O>(itemView: View) : RecyclerView.ViewHolder(itemView) {
        open fun bindData(data: O?) {}
    }

}


interface IAmAdapterHasHF<O> : IAmAdapter<O> {
    fun notifyFooterChanged()
    fun notifyHeaderChanged()
    fun notifyHFItemDataChanged(index: Int, dataPosition: Boolean = false)
    fun notifyHFItemDataRemoved(index: Int, dataPosition: Boolean = false)
    fun notifyHFItemDataInserted(index: Int, dataPosition: Boolean = false)
    fun notifyHFItemDataRangeChanged(startIndex: Int, endIndex: Int, dataPosition: Boolean = false)
    fun notifyHFItemDataRangeRemoved(startIndex: Int, endIndex: Int, dataPosition: Boolean = false)
    fun notifyHFItemDataRangeInsert(startIndex: Int, endIndex: Int, dataPosition: Boolean = false)
    fun notifyHFItemDataMoved(fromIndex: Int, toIndex: Int, dataPosition: Boolean = false)

    fun notifyHFDataChanged()

    fun addByAdapterPosition(index: Int, data: O)
    fun removeByAdapterPosition(index: Int)

}