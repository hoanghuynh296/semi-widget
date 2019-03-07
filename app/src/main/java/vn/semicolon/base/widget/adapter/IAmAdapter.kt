package vn.semicolon.base.widget.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

interface IAmAdapter<O> : OnItemClickListener<O> {
    val data: MutableList<O>

    fun isExist(item: O): Boolean

    fun addAll(items: Collection<O>)

    fun add(item: O)

    fun addToFirst(item: O)
    fun addToLast(item: O)

    fun getLastItem(): O?

    fun getFirstItem(): O?
    fun getItemAt(position: Int): O?

    fun add(index: Int, item: O)
    fun remove(item: O): Boolean

    fun removeAt(index: Int): Boolean

    fun clear()

    fun createView(resource: Int, parent: ViewGroup, attachToRoot: Boolean = false): View {
        return LayoutInflater.from(parent.context).inflate(resource, parent, attachToRoot)
    }
}
