package vn.semicolon.base.widget.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter

class DefaultViewPagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {
    private val mItems = mutableListOf<Fragment>()

    fun addItems(items: MutableList<Fragment>) {
        mItems.clear()
        mItems.addAll(items)
    }

    fun addItem(item: Fragment) {
        mItems.add(item)
    }

    fun clear() {
        mItems.clear()
    }

    override fun getItem(position: Int): Fragment {
        return mItems[position]
    }

    override fun getCount(): Int {
        return mItems.size
    }
}