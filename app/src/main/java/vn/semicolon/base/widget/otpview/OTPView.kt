package vn.semicolon.base.widget.otpview

import android.animation.ValueAnimator
import android.content.Context
import android.content.res.ColorStateList
import android.content.res.Resources
import android.content.res.Resources.Theme
import android.graphics.*
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.text.InputFilter
import android.text.InputFilter.LengthFilter
import android.text.TextPaint
import android.text.TextUtils
import android.text.method.MovementMethod
import android.util.AttributeSet
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.annotation.Nullable
import androidx.annotation.Px
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.ViewCompat
import vn.semicolon.base.widget.R
import kotlin.math.abs
import kotlin.math.roundToInt


class OTPView : EditText {

    private val DBG = false
    private val BLINK = 500
    private val DEFAULT_COUNT = 4
    private val NO_FILTERS = arrayOfNulls<InputFilter>(0)
    private val SELECTED_STATE = intArrayOf(
        android.R.attr.state_selected
    )
    private val FILLED_STATE = intArrayOf(
        R.attr.otp_state_filled
    )
    private val VIEW_TYPE_RECTANGLE = 0
    private val VIEW_TYPE_LINE = 1
    private val VIEW_TYPE_NONE = 2
    private var viewType = 0
    private var otpViewItemCount = 0
    private var otpViewItemWidth = 0
    private var otpViewItemHeight = 0
    private var otpViewItemRadius = 0
    private var otpViewItemSpacing = 0
    private var paint: Paint? = null
    private val animatorTextPaint: TextPaint? = TextPaint()
    private var lineColor: ColorStateList? = null
    private var cursorLineColor: Int = Color.BLACK
    private var lineWidth = 0
    private val textRect: Rect = Rect()
    private val itemBorderRect = RectF()
    private val itemLineRect = RectF()
    private val path: Path = Path()
    private val itemCenterPoint = PointF()
    private var defaultAddAnimator: ValueAnimator? = null
    private var isAnimationEnable = false
    private var blink: Blink? = null
    private var isCursorVisible = false
    private var drawCursor = false
    private var cursorHeight = 0f
    private var cursorWidth = 0
    private var cursorColor = 0
    private var itemBackgroundResource = 0
    private var itemBackground: Drawable? = null
    private var hideLineWhenFilled = false
    private var rtlTextDirection = false
    private var maskingChar: String? = null
    private var onOtpCompletionListener: OnOtpCompletionListener? = null

    constructor(context: Context?):super(context) {
        this.init(context, null, R.attr.otpViewStyle)
    }

    constructor(context: Context?, @Nullable attrs: AttributeSet?):super(context,attrs) {
        this.init(context, attrs, R.attr.otpViewStyle)
    }

    constructor(
        context: Context, @Nullable attrs: AttributeSet?,
        defStyleAttr: Int
    ): super(context,attrs,defStyleAttr) {
        this.init(context,attrs,defStyleAttr)
    }

    private fun init(context: Context?, @Nullable attrs: AttributeSet?,
                     defStyleAttr: Int){
        if(context == null){
            return
        }
        val res: Resources = resources
        paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint?.style = Paint.Style.STROKE
        animatorTextPaint!!.set(getPaint())
        val theme: Theme = context.theme
        val typedArray =
            theme.obtainStyledAttributes(attrs, R.styleable.OtpView, defStyleAttr, 0)
        viewType = typedArray.getInt(R.styleable.OtpView_otp_viewType, VIEW_TYPE_NONE)
        otpViewItemCount = typedArray.getInt(R.styleable.OtpView_otp_itemCount, DEFAULT_COUNT)
        otpViewItemHeight = typedArray.getDimension(
            R.styleable.OtpView_otp_itemHeight,
            res.getDimensionPixelSize(R.dimen.otp_view_item_size).toFloat()
        ).toInt()
        otpViewItemWidth = typedArray.getDimension(
            R.styleable.OtpView_otp_itemWidth,
            res.getDimensionPixelSize(R.dimen.otp_view_item_size).toFloat()
        ).toInt()
        otpViewItemSpacing = typedArray.getDimensionPixelSize(
            R.styleable.OtpView_otp_itemSpacing,
            res.getDimensionPixelSize(R.dimen.otp_view_item_spacing)
        )
        otpViewItemRadius = typedArray.getDimension(R.styleable.OtpView_otp_itemRadius, 0f).toInt()
        lineWidth = typedArray.getDimension(
            R.styleable.OtpView_otp_lineWidth,
            res.getDimensionPixelSize(R.dimen.otp_view_item_line_width).toFloat()
        ).toInt()
        lineColor = typedArray.getColorStateList(R.styleable.OtpView_otp_lineColor)
        isCursorVisible = typedArray.getBoolean(R.styleable.OtpView_android_cursorVisible, true)
        cursorColor = typedArray.getColor(R.styleable.OtpView_otp_cursorColor, currentTextColor)
        cursorWidth = typedArray.getDimensionPixelSize(
            R.styleable.OtpView_otp_cursorWidth,
            res.getDimensionPixelSize(R.dimen.otp_view_cursor_width)
        )
        itemBackground = typedArray.getDrawable(R.styleable.OtpView_android_itemBackground)
        hideLineWhenFilled = typedArray.getBoolean(R.styleable.OtpView_otp_hideLineWhenFilled, false)
        rtlTextDirection = typedArray.getBoolean(R.styleable.OtpView_otp_rtlTextDirection, false)
        maskingChar = typedArray.getString(R.styleable.OtpView_otp_maskingChar)
        typedArray.recycle()
        if (lineColor != null) {
            cursorLineColor = lineColor!!.defaultColor
        }
        updateCursorHeight()
        checkItemRadius()
        setMaxLength(otpViewItemCount)
        paint?.strokeWidth = lineWidth.toFloat()
        setupAnimator()
        super.setCursorVisible(false)
        setTextIsSelectable(false)
    }

    override fun setTypeface(tf: Typeface?, style: Int) {
        super.setTypeface(tf, style)
    }

    override fun setTypeface(tf: Typeface?) {
        super.setTypeface(tf)
        animatorTextPaint?.set(getPaint())
    }

    private fun setMaxLength(maxLength: Int) {
        filters = if (maxLength >= 0) arrayOf<InputFilter>(LengthFilter(maxLength)) else NO_FILTERS
    }

    private fun setupAnimator() {
        defaultAddAnimator = ValueAnimator.ofFloat(0.5f, 1f)
        defaultAddAnimator?.duration = 150
        defaultAddAnimator?.interpolator = DecelerateInterpolator()
        defaultAddAnimator?.addUpdateListener { animation ->
            val scale = animation.animatedValue as Float
            val alpha = (255 * scale).toInt()
            animatorTextPaint!!.textSize = textSize * scale
            animatorTextPaint.alpha = alpha
            postInvalidate()
        }
    }

    private fun checkItemRadius() {
        if (viewType == VIEW_TYPE_LINE) {
            val halfOfLineWidth = lineWidth.toFloat() / 2
            require(otpViewItemRadius <= halfOfLineWidth) { "The itemRadius can not be greater than lineWidth when viewType is line" }
        } else if (viewType == VIEW_TYPE_RECTANGLE) {
            val halfOfItemWidth = otpViewItemWidth.toFloat() / 2
            require(otpViewItemRadius <= halfOfItemWidth) { "The itemRadius can not be greater than itemWidth" }
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)
        var width: Int
        val height: Int
        val boxHeight = otpViewItemHeight
        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize
        } else {
            val boxesWidth =
                (otpViewItemCount - 1) * otpViewItemSpacing + otpViewItemCount * otpViewItemWidth
            width = boxesWidth + ViewCompat.getPaddingEnd(this) + ViewCompat.getPaddingStart(this)
            if (otpViewItemSpacing == 0) {
                width -= (otpViewItemCount - 1) * lineWidth
            }
        }
        height =
            if (heightMode == MeasureSpec.EXACTLY) heightSize else boxHeight + paddingTop + paddingBottom
        setMeasuredDimension(width, height)
    }

    override fun onTextChanged(
        text: CharSequence,
        start: Int,
        lengthBefore: Int,
        lengthAfter: Int
    ) {
        if (start != text.length) {
            moveSelectionToEnd()
        }
        if (text.length == otpViewItemCount && onOtpCompletionListener != null) {
            onOtpCompletionListener!!.onOtpCompleted(text.toString())
        }
        makeBlink()
        if (isAnimationEnable) {
            val isAdd = lengthAfter - lengthBefore > 0
            if (isAdd && defaultAddAnimator != null) {
                defaultAddAnimator!!.end()
                defaultAddAnimator!!.start()
            }
        }
    }

    override fun onFocusChanged(
        focused: Boolean,
        direction: Int,
        previouslyFocusedRect: Rect?
    ) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect)
        if (focused) {
            moveSelectionToEnd()
            makeBlink()
        }
    }

    override fun onSelectionChanged(selStart: Int, selEnd: Int) {
        super.onSelectionChanged(selStart, selEnd)
        if (text != null && selEnd != text.length) {
            moveSelectionToEnd()
        }
    }

    private fun moveSelectionToEnd() {
        if (text != null) {
            setSelection(text.length)
        }
    }

    override fun drawableStateChanged() {
        super.drawableStateChanged()
        if (lineColor == null || lineColor!!.isStateful) {
            updateColors()
        }
    }

    override fun onDraw(canvas: Canvas) {
        canvas.save()
        updatePaints()
        drawOtpView(canvas)
        canvas.restore()
    }

    private fun updatePaints() {
        paint?.color = cursorLineColor
        paint?.style = Paint.Style.STROKE
        paint?.strokeWidth = lineWidth.toFloat()
        getPaint().color = currentTextColor
    }

    private fun drawOtpView(canvas: Canvas) {
        val nextItemToFill: Int = if (rtlTextDirection) {
            otpViewItemCount - 1
        } else {
            if (text != null) {
                text.length
            } else {
                0
            }
        }
        for (i in 0 until otpViewItemCount) {
            val itemSelected = isFocused && nextItemToFill == i
            val itemFilled = i < nextItemToFill
            var itemState: IntArray? = null
            if (itemFilled) {
                itemState = FILLED_STATE
            } else if (itemSelected) {
                itemState = SELECTED_STATE
            }
            paint?.color = itemState?.let { getLineColorForState(it) } ?: cursorLineColor
            updateItemRectF(i)
            updateCenterPoint()
            canvas.save()
            if (viewType == VIEW_TYPE_RECTANGLE) {
                updateOtpViewBoxPath(i)
                canvas.clipPath(path)
            }
            drawItemBackground(canvas, itemState)
            canvas.restore()
            if (itemSelected) {
                drawCursor(canvas)
            }
            if (viewType == VIEW_TYPE_RECTANGLE) {
                drawOtpBox(canvas, i)
            } else if (viewType == VIEW_TYPE_LINE) {
                drawOtpLine(canvas, i)
            }
            if (DBG) {
                drawAnchorLine(canvas)
            }
            if (rtlTextDirection) {
                val reversedPosition = otpViewItemCount - i
                if (text.length >= reversedPosition) {
                    drawInput(canvas, i)
                } else if (!TextUtils.isEmpty(hint) && hint.length == otpViewItemCount) {
                    drawHint(canvas, i)
                }
            } else {
                if (text.length > i) {
                    drawInput(canvas, i)
                } else if (!TextUtils.isEmpty(hint) && hint.length == otpViewItemCount) {
                    drawHint(canvas, i)
                }
            }
        }
        if (isFocused
            && text != null && text.length != otpViewItemCount && viewType == VIEW_TYPE_RECTANGLE
        ) {
            val index = text.length
            updateItemRectF(index)
            updateCenterPoint()
            updateOtpViewBoxPath(index)
            paint?. color = getLineColorForState(SELECTED_STATE)
            drawOtpBox(canvas, index)
        }
    }

    private fun drawInput(
        canvas: Canvas,
        i: Int
    ) { //allows masking for all number keyboard
        if (maskingChar != null &&
            (isNumberInputType(inputType) ||
                    isPasswordInputType(inputType))
        ) {
            drawMaskingText(canvas, i, maskingChar!![0].toString())
        } else if (isPasswordInputType(inputType)) {
            drawCircle(canvas, i)
        } else {
            drawText(canvas, i)
        }
    }

    private fun getLineColorForState(states: IntArray): Int {
        return if (lineColor != null) lineColor!!.getColorForState(
            states,
            cursorLineColor
        ) else cursorLineColor
    }

    private fun drawItemBackground(canvas: Canvas, backgroundState: IntArray?) {
        if (itemBackground == null) {
            return
        }
        val delta = lineWidth.toFloat() / 2
        val left = (itemBorderRect.left - delta).roundToInt()
        val top = (itemBorderRect.top - delta).roundToInt()
        val right = (itemBorderRect.right + delta).roundToInt()
        val bottom = (itemBorderRect.bottom + delta).roundToInt()
        itemBackground!!.setBounds(left, top, right, bottom)
        if (viewType != VIEW_TYPE_NONE) {
            itemBackground!!.state = backgroundState ?: drawableState
        }
        itemBackground!!.draw(canvas)
    }

    private fun updateOtpViewBoxPath(i: Int) {
        var drawRightCorner = false
        var drawLeftCorner = false
        if (otpViewItemSpacing != 0) {
            drawRightCorner = true
            drawLeftCorner = drawRightCorner
        } else {
            if (i == 0 && i != otpViewItemCount - 1) {
                drawLeftCorner = true
            }
            if (i == otpViewItemCount - 1 && i != 0) {
                drawRightCorner = true
            }
        }
        updateRoundRectPath(
            itemBorderRect,
            otpViewItemRadius.toFloat(),
            otpViewItemRadius.toFloat(),
            drawLeftCorner,
            drawRightCorner
        )
    }

    private fun drawOtpBox(canvas: Canvas, i: Int) {
        if (text != null && hideLineWhenFilled && i < text.length) {
            return
        }
        canvas.drawPath(path, paint!!)
    }

    private fun drawOtpLine(canvas: Canvas, i: Int) {
        if (text != null && hideLineWhenFilled && i < text.length) {
            return
        }
        var drawLeft: Boolean
        var drawRight: Boolean
        drawRight = true
        drawLeft = drawRight
        if (otpViewItemSpacing == 0 && otpViewItemCount > 1) {
            when (i) {
                0 -> {
                    drawRight = false
                }
                otpViewItemCount - 1 -> {
                    drawLeft = false
                }
                else -> {
                    drawRight = false
                    drawLeft = drawRight
                }
            }
        }
        paint?.style = Paint.Style.FILL
        paint?.strokeWidth = lineWidth.toFloat() / 10
        val halfLineWidth = lineWidth.toFloat() / 2
        itemLineRect[itemBorderRect.left - halfLineWidth, itemBorderRect.bottom - halfLineWidth, itemBorderRect.right + halfLineWidth] =
            itemBorderRect.bottom + halfLineWidth
        updateRoundRectPath(
            itemLineRect,
            otpViewItemRadius.toFloat(),
            otpViewItemRadius.toFloat(),
            drawLeft,
            drawRight
        )
        canvas.drawPath(path, paint!!)
    }

    private fun drawCursor(canvas: Canvas) {
        if (drawCursor) {
            val cx = itemCenterPoint.x
            val cy = itemCenterPoint.y
            val y = cy - cursorHeight / 2
            val color: Int = paint!!.color
            val width: Float = paint!!.strokeWidth
            paint!!.color = cursorColor
            paint!!.strokeWidth = cursorWidth.toFloat()
            canvas.drawLine(cx, y, cx, y + cursorHeight, paint!!)
            paint!!.color = color
            paint!!.strokeWidth = width
        }
    }

    private fun updateRoundRectPath(
        rectF: RectF,
        rx: Float,
        ry: Float,
        l: Boolean,
        r: Boolean
    ) {
        updateRoundRectPath(rectF, rx, ry, l, r, r, l)
    }

    private fun updateRoundRectPath(
        rectF: RectF, rx: Float, ry: Float,
        tl: Boolean, tr: Boolean, br: Boolean, bl: Boolean
    ) {
        path.reset()
        val l = rectF.left
        val t = rectF.top
        val r = rectF.right
        val b = rectF.bottom
        val w = r - l
        val h = b - t
        val lw = w - 2 * rx
        val lh = h - 2 * ry
        path.moveTo(l, t + ry)
        if (tl) {
            path.rQuadTo(0F, -ry, rx, -ry)
        } else {
            path.rLineTo(0F, -ry)
            path.rLineTo(rx, 0F)
        }
        path.rLineTo(lw, 0F)
        if (tr) {
            path.rQuadTo(rx, 0F, rx, ry)
        } else {
            path.rLineTo(rx, 0F)
            path.rLineTo(0F, ry)
        }
        path.rLineTo(0F, lh)
        if (br) {
            path.rQuadTo(0F, ry, -rx, ry)
        } else {
            path.rLineTo(0F, ry)
            path.rLineTo(-rx, 0F)
        }
        path.rLineTo(-lw, 0F)
        if (bl) {
            path.rQuadTo(-rx, 0F, -rx, -ry)
        } else {
            path.rLineTo(-rx, 0F)
            path.rLineTo(0F, -ry)
        }
        path.rLineTo(0F, -lh)
        path.close()
    }

    private fun updateItemRectF(i: Int) {
        val halfLineWidth = lineWidth.toFloat() / 2
        var left = (scrollX
                + ViewCompat.getPaddingStart(this)
                + i * (otpViewItemSpacing + otpViewItemWidth) + halfLineWidth)
        if (otpViewItemSpacing == 0 && i > 0) {
            left -= lineWidth * i
        }
        val right = left + otpViewItemWidth - lineWidth
        val top = scrollY + paddingTop + halfLineWidth
        val bottom = top + otpViewItemHeight - lineWidth
        itemBorderRect[left, top, right] = bottom
    }

    private fun drawText(canvas: Canvas, i: Int) {
        val paint: Paint? = getPaintByIndex(i)
        paint?.color = currentTextColor
        if (rtlTextDirection) {
            val reversedPosition = otpViewItemCount - i
            val reversedCharPosition: Int
            reversedCharPosition = if (text == null) {
                reversedPosition
            } else {
                reversedPosition - text.length
            }
            if (reversedCharPosition <= 0 && text != null) {
                drawTextAtBox(canvas, paint, text, Math.abs(reversedCharPosition))
            }
        } else if (text != null) {
            drawTextAtBox(canvas, paint, text, i)
        }
    }

    private fun drawMaskingText(
        canvas: Canvas,
        i: Int,
        maskingChar: String
    ) {
        val paint: Paint? = getPaintByIndex(i)
        paint?.color = currentTextColor
        if (rtlTextDirection) {
            val reversedPosition = otpViewItemCount - i
            val reversedCharPosition: Int
            reversedCharPosition = if (text == null) {
                reversedPosition
            } else {
                reversedPosition - text.length
            }
            if (reversedCharPosition <= 0 && text != null) {
                drawTextAtBox(
                    canvas, paint, text.toString().replace(".".toRegex(), maskingChar),
                    abs(reversedCharPosition)
                )
            }
        } else if (text != null) {
            drawTextAtBox(
                canvas,
                paint,
                text.toString().replace(".".toRegex(), maskingChar),
                i
            )
        }
    }

    private fun drawHint(canvas: Canvas, i: Int) {
        val paint: Paint? = getPaintByIndex(i)
        paint?.color = currentHintTextColor
        if (rtlTextDirection) {
            val reversedPosition = otpViewItemCount - i
            val reversedCharPosition = reversedPosition - hint.length
            if (reversedCharPosition <= 0) {
                drawTextAtBox(canvas, paint, hint, Math.abs(reversedCharPosition))
            }
        } else {
            drawTextAtBox(canvas, paint, hint, i)
        }
    }

    private fun drawTextAtBox(
        canvas: Canvas,
        paint: Paint?,
        text: CharSequence,
        charAt: Int
    ) {
        paint?.getTextBounds(text.toString(), charAt, charAt + 1, textRect)
        val cx = itemCenterPoint.x
        val cy = itemCenterPoint.y
        val x: Float =
            cx - abs(textRect.width().toFloat()) / 2 - textRect.left
        val y: Float =
            cy + abs(textRect.height().toFloat()) / 2 - textRect.bottom
        canvas.drawText(text, charAt, charAt + 1, x, y, paint!!)
    }

    private fun drawCircle(canvas: Canvas, i: Int) {
        val paint: Paint? = getPaintByIndex(i)
        val cx = itemCenterPoint.x
        val cy = itemCenterPoint.y
        if (rtlTextDirection) {
            val reversedItemPosition = otpViewItemCount - i
            val reversedCharPosition = reversedItemPosition - hint.length
            if (reversedCharPosition <= 0) {
                canvas.drawCircle(cx, cy, paint!!.textSize / 2, paint)
            }
        } else {
            canvas.drawCircle(cx, cy, paint!!.textSize / 2, paint)
        }
    }

    private fun getPaintByIndex(i: Int): Paint? {
        return if (text != null && isAnimationEnable && i == text.length - 1) {
            animatorTextPaint!!.color = getPaint().color
            animatorTextPaint
        } else {
            getPaint()
        }
    }

    private fun drawAnchorLine(canvas: Canvas) {
        var cx = itemCenterPoint.x
        var cy = itemCenterPoint.y
        paint?.strokeWidth = 1F
        cx -= paint!!.strokeWidth / 2
        cy -= paint!!.strokeWidth / 2
        path.reset()
        path.moveTo(cx, itemBorderRect.top)
        path.lineTo(cx, itemBorderRect.top + abs(itemBorderRect.height()))
        canvas.drawPath(path, paint!!)
        path.reset()
        path.moveTo(itemBorderRect.left, cy)
        path.lineTo(itemBorderRect.left + abs(itemBorderRect.width()), cy)
        canvas.drawPath(path, paint!!)
        path.reset()
        paint!!.strokeWidth = lineWidth.toFloat()
    }

    private fun updateColors() {
        var shouldInvalidate = false
        val color = if (lineColor != null) lineColor!!.getColorForState(
            drawableState,
            0
        ) else currentTextColor
        if (color != cursorLineColor) {
            cursorLineColor = color
            shouldInvalidate = true
        }
        if (shouldInvalidate) {
            invalidate()
        }
    }

    private fun updateCenterPoint() {
        val cx = itemBorderRect.left + abs(itemBorderRect.width()) / 2
        val cy = itemBorderRect.top + abs(itemBorderRect.height()) / 2
        itemCenterPoint[cx] = cy
    }

    private fun isPasswordInputType(inputType: Int): Boolean {
        val variation =
            inputType and (EditorInfo.TYPE_MASK_CLASS or EditorInfo.TYPE_MASK_VARIATION)
        return (variation
                == EditorInfo.TYPE_CLASS_TEXT or EditorInfo.TYPE_TEXT_VARIATION_PASSWORD) || (variation
                == EditorInfo.TYPE_CLASS_TEXT or EditorInfo.TYPE_TEXT_VARIATION_WEB_PASSWORD) || (variation
                == EditorInfo.TYPE_CLASS_NUMBER or EditorInfo.TYPE_NUMBER_VARIATION_PASSWORD)
    }

    private fun isNumberInputType(inputType: Int): Boolean {
        return inputType == EditorInfo.TYPE_CLASS_NUMBER
    }

    override fun getDefaultMovementMethod(): MovementMethod? {
        return DefaultMovementMethod.getInstance()
    }

    /**
     * Sets the line color for all the states (normal, selected,
     * focused) to be this color.
     *
     * @param color A color value in the form 0xAARRGGBB.
     * Do not pass a resource ID. To get a color value from a resource ID, call
     * [getColor][androidx.core.content.ContextCompat.getColor].
     * @attr ref R.styleable#OtpView_lineColor
     * @see .setLineColor
     * @see .getLineColors
     */
    fun setLineColor(@ColorInt color: Int) {
        lineColor = ColorStateList.valueOf(color)
        updateColors()
    }

    /**
     * Sets the line color.
     *
     * @attr ref R.styleable#OtpView_lineColor
     * @see .setLineColor
     * @see .getLineColors
     */
    fun setLineColor(colors: ColorStateList?) {
        requireNotNull(colors) { "Color cannot be null" }
        lineColor = colors
        updateColors()
    }

    /**
     * Gets the line colors for the different states (normal, selected, focused) of the OtpView.
     *
     * @attr ref R.styleable#OtpView_lineColor
     * @see .setLineColor
     * @see .setLineColor
     */
    fun getLineColors(): ColorStateList? {
        return lineColor
    }

    /**
     *
     * Return the current color selected for normal line.
     *
     * @return Returns the current item's line color.
     */
    @ColorInt
    fun getCurrentLineColor(): Int {
        return cursorLineColor
    }

    /**
     * Sets the line width.
     *
     * @attr ref R.styleable#OtpView_lineWidth
     * @see .getLineWidth
     */
    fun setLineWidth(@Px borderWidth: Int) {
        lineWidth = borderWidth
        checkItemRadius()
        requestLayout()
    }

    /**
     * @return Returns the width of the item's line.
     * @see .setLineWidth
     */
    fun getLineWidth(): Int {
        return lineWidth
    }

    /**
     * Sets the count of items.
     *
     * @attr ref R.styleable#OtpView_itemCount
     * @see .getItemCount
     */
    fun setItemCount(count: Int) {
        otpViewItemCount = count
        setMaxLength(count)
        requestLayout()
    }

    /**
     * @return Returns the count of items.
     * @see .setItemCount
     */
    fun getItemCount(): Int {
        return otpViewItemCount
    }

    /**
     * Sets the radius of square.
     *
     * @attr ref R.styleable#OtpView_itemRadius
     * @see .getItemRadius
     */
    fun setItemRadius(@Px itemRadius: Int) {
        otpViewItemRadius = itemRadius
        checkItemRadius()
        requestLayout()
    }

    /**
     * @return Returns the radius of square.
     * @see .setItemRadius
     */
    fun getItemRadius(): Int {
        return otpViewItemRadius
    }

    /**
     * Specifies extra space between two items.
     *
     * @attr ref R.styleable#OtpView_itemSpacing
     * @see .getItemSpacing
     */
    fun setItemSpacing(@Px itemSpacing: Int) {
        otpViewItemSpacing = itemSpacing
        requestLayout()
    }

    /**
     * @return Returns the spacing between two items.
     * @see .setItemSpacing
     */
    @Px
    fun getItemSpacing(): Int {
        return otpViewItemSpacing
    }

    /**
     * Sets the height of item.
     *
     * @attr ref R.styleable#OtpView_itemHeight
     * @see .getItemHeight
     */
    fun setItemHeight(@Px itemHeight: Int) {
        otpViewItemHeight = itemHeight
        updateCursorHeight()
        requestLayout()
    }

    /**
     * @return Returns the height of item.
     * @see .setItemHeight
     */
    fun getItemHeight(): Int {
        return otpViewItemHeight
    }

    /**
     * Sets the width of item.
     *
     * @attr ref R.styleable#OtpView_itemWidth
     * @see .getItemWidth
     */
    fun setItemWidth(@Px itemWidth: Int) {
        otpViewItemWidth = itemWidth
        checkItemRadius()
        requestLayout()
    }

    /**
     * @return Returns the width of item.
     * @see .setItemWidth
     */
    fun getItemWidth(): Int {
        return otpViewItemWidth
    }

    /**
     * Specifies whether the text animation should be enabled or disabled.
     * By the default, the animation is disabled.
     *
     * @param enable True to start animation when adding text, false to transition immediately
     */
    fun setAnimationEnable(enable: Boolean) {
        isAnimationEnable = enable
    }

    /**
     * Specifies whether the line (border) should be hidden or visible when text entered.
     * By the default, this flag is false and the line is always drawn.
     *
     * @param hideLineWhenFilled true to hide line on a position where text entered,
     * false to always show line
     * @attr ref R.styleable#OtpView_hideLineWhenFilled
     */
    fun setHideLineWhenFilled(hideLineWhenFilled: Boolean) {
        this.hideLineWhenFilled = hideLineWhenFilled
    }

    override fun setTextSize(size: Float) {
        super.setTextSize(size)
        updateCursorHeight()
    }

    override fun setTextSize(unit: Int, size: Float) {
        super.setTextSize(unit, size)
        updateCursorHeight()
    }

    fun setOtpCompletionListener(otpCompletionListener: OnOtpCompletionListener?) {
        onOtpCompletionListener = otpCompletionListener
    }

    //region ItemBackground

    //region ItemBackground
    /**
     * Set the item background to a given resource. The resource should refer to
     * a Drawable object or 0 to remove the item background.
     *
     * @param resId The identifier of the resource.
     * @attr ref R.styleable#OtpView_android_itemBackground
     */
    fun setItemBackgroundResources(@DrawableRes resId: Int) {
        if (resId != 0 && itemBackgroundResource != resId) {
            return
        }
        itemBackground = ResourcesCompat.getDrawable(resources, resId, context.theme)
        setItemBackground(itemBackground)
        itemBackgroundResource = resId
    }

    /**
     * Sets the item background color for this view.
     *
     * @param color the color of the item background
     */
    fun setItemBackgroundColor(@ColorInt color: Int) {
        if (itemBackground is ColorDrawable) {
            ((itemBackground as ColorDrawable).mutate() as ColorDrawable).color = color
            itemBackgroundResource = 0
        } else {
            setItemBackground(ColorDrawable(color))
        }
    }

    /**
     * Set the item background to a given Drawable, or remove the background.
     *
     * @param background The Drawable to use as the item background, or null to remove the
     * item background
     */
    fun setItemBackground(background: Drawable?) {
        itemBackgroundResource = 0
        itemBackground = background
        invalidate()
    }
    //endregion

    //region Cursor

    //endregion
//region Cursor
    /**
     * Sets the width (in pixels) of cursor.
     *
     * @attr ref R.styleable#OtpView_cursorWidth
     * @see .getCursorWidth
     */
    fun setCursorWidth(@Px width: Int) {
        cursorWidth = width
        if (isCursorVisible()) {
            invalidateCursor(true)
        }
    }

    /**
     * @return Returns the width (in pixels) of cursor.
     * @see .setCursorWidth
     */
    fun getCursorWidth(): Int {
        return cursorWidth
    }

    /**
     * Sets the cursor color.
     *
     * @param color A color value in the form 0xAARRGGBB.
     * Do not pass a resource ID. To get a color value from a resource ID, call
     * [getColor][androidx.core.content.ContextCompat.getColor].
     * @attr ref R.styleable#OtpView_cursorColor
     * @see .getCursorColor
     */
    fun setCursorColor(@ColorInt color: Int) {
        cursorColor = color
        if (isCursorVisible()) {
            invalidateCursor(true)
        }
    }

    /**
     * Gets the cursor color.
     *
     * @return Return current cursor color.
     * @see .setCursorColor
     */
    fun getCursorColor(): Int {
        return cursorColor
    }

    fun setMaskingChar(maskingChar: String?) {
        this.maskingChar = maskingChar
        requestLayout()
    }

    fun getMaskingChar(): String? {
        return maskingChar
    }

    override fun setCursorVisible(visible: Boolean) {
        if (isCursorVisible != visible) {
            isCursorVisible = visible
            invalidateCursor(isCursorVisible)
            makeBlink()
        }
    }

    override fun isCursorVisible(): Boolean {
        return isCursorVisible
    }

    override fun onScreenStateChanged(screenState: Int) {
        super.onScreenStateChanged(screenState)
        if (screenState == View.SCREEN_STATE_ON) {
            resumeBlink()
        } else if (screenState == View.SCREEN_STATE_OFF) {
            suspendBlink()
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        resumeBlink()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        suspendBlink()
    }

    private fun shouldBlink(): Boolean {
        return isCursorVisible() && isFocused
    }

    private fun makeBlink() {
        if (shouldBlink()) {
            if (blink == null) {
                blink = Blink()
            }
            removeCallbacks(blink)
            drawCursor = false
            postDelayed(blink, BLINK.toLong())
        } else {
            if (blink != null) {
                removeCallbacks(blink)
            }
        }
    }

    private fun suspendBlink() {
        if (blink != null) {
            blink!!.cancel()
            invalidateCursor(false)
        }
    }

    private fun resumeBlink() {
        if (blink != null) {
            blink!!.unCancel()
            makeBlink()
        }
    }

    private fun invalidateCursor(showCursor: Boolean) {
        if (drawCursor != showCursor) {
            drawCursor = showCursor
            invalidate()
        }
    }

    private fun updateCursorHeight() {
        val delta = 2 * dpToPx()
        cursorHeight =
            if (otpViewItemHeight - textSize > delta) textSize + delta else textSize
    }

    inner class Blink : Runnable {
        private var cancelled = false
        override fun run() {
            if (cancelled) {
                return
            }
            removeCallbacks(this)
            if (shouldBlink()) {
                invalidateCursor(!drawCursor)
                postDelayed(this, BLINK.toLong())
            }
        }

        fun cancel() {
            if (!cancelled) {
                removeCallbacks(this)
                cancelled = true
            }
        }

        fun unCancel() {
            cancelled = false
        }
    }
    //endregion

    //endregion
    private fun dpToPx(): Int {
        return (2.toFloat() * resources.displayMetrics.density + 0.5f).toInt()
    }
}