package vn.semicolon.base.widget.adapter

import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter


class TitleTabLayoutAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {

    private val mItems: MutableList<Fragment> = ArrayList()
    private val mTitles: MutableList<String> = ArrayList()
    private lateinit var mCurrent: Fragment

    fun addItem(fragment: Fragment, title: String) {
        mItems.add(fragment)
        mTitles.add(title)
    }

    override fun getPageTitle(position: Int): CharSequence? {
        if (position >= mTitles.size) return null
        return mTitles[position]
    }

    override fun getItem(position: Int): Fragment{
        return mItems[position]
    }

    override fun getCount(): Int {
        return mItems.size
    }

    override fun setPrimaryItem(container: ViewGroup, position: Int, obj: Any) {
        if (obj is Fragment) {
            if (!::mCurrent.isInitialized || mCurrent != obj) {
                mCurrent = obj
            }
        }
        super.setPrimaryItem(container, position, obj)
    }

    fun getCurrent(): Fragment {
        return mCurrent
    }
}