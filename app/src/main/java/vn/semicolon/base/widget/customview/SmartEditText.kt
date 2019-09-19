package vn.semicolon.base.widget.customview

import android.content.Context
import android.text.Editable
import android.text.Selection
import android.text.TextUtils
import android.text.method.ArrowKeyMovementMethod
import android.text.method.MovementMethod
import android.util.AttributeSet
import android.view.accessibility.AccessibilityNodeInfo
import android.widget.TextView
import vn.semicolon.base.widget.R


open class SmartEditText : SmartTextView {
    constructor(context: Context) : super(context) {
        SmartEditText(context, null)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        SmartEditText(context, attrs, R.attr.editTextStyle)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        SmartEditText(context, attrs, defStyleAttr, 0)
    }

    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(
        context,
        attrs,
        defStyleAttr
    )
    
    override fun getFreezesText(): Boolean {
        return true
    }

    override fun getDefaultEditable(): Boolean {
        return true
    }

    override fun getDefaultMovementMethod(): MovementMethod {
        return ArrowKeyMovementMethod.getInstance()
    }

    override fun getText(): Editable? {
        val text = super.getText() ?: return null
        // This can only happen during construction.
        if (text is Editable) {
            return super.getText() as Editable
        }
        super.setText(text, TextView.BufferType.EDITABLE)
        return super.getText() as Editable
    }

    override fun setText(text: CharSequence, type: TextView.BufferType) {
        super.setText(text, TextView.BufferType.EDITABLE)
    }

    /**
     * Convenience for [Selection.setSelection].
     */
    fun setSelection(start: Int, stop: Int) {
        Selection.setSelection(text, start, stop)
    }

    /**
     * Convenience for [Selection.setSelection].
     */
    fun setSelection(index: Int) {
        Selection.setSelection(text, index)
    }

    /**
     * Convenience for [Selection.selectAll].
     */
    fun selectAll() {
        Selection.selectAll(text)
    }

    /**
     * Convenience for [Selection.extendSelection].
     */
    fun extendSelection(index: Int) {
        Selection.extendSelection(text, index)
    }

    /**
     * Causes words in the text that are longer than the view's width to be ellipsized instead of
     * broken in the middle. [ TextUtils.TruncateAt#MARQUEE][TextUtils.TruncateAt.MARQUEE] is not supported.
     *
     * @param ellipsis Type of ellipsis to be applied.
     * @throws IllegalArgumentException When the value of `ellipsis` parameter is
     * [TextUtils.TruncateAt.MARQUEE].
     * @see TextView.setEllipsize
     */
    override fun setEllipsize(ellipsis: TextUtils.TruncateAt) {
        require(ellipsis != TextUtils.TruncateAt.MARQUEE) { "EditText cannot use the ellipsize mode " + "TextUtils.TruncateAt.MARQUEE" }
        super.setEllipsize(ellipsis)
    }

    override fun getAccessibilityClassName(): CharSequence {
        return android.widget.EditText::class.java.name
    }

    /** @hide
     */
    protected fun supportsAutoSizeText(): Boolean {
        return false
    }

    override fun onInitializeAccessibilityNodeInfo(info: AccessibilityNodeInfo?) {
        super.onInitializeAccessibilityNodeInfo(info)
        if (isEnabled) {
            info?.addAction(AccessibilityNodeInfo.AccessibilityAction.ACTION_SET_TEXT)
        }
    }
}