package vn.semicolon.base.widget.utils

import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Base64
import android.util.Patterns
import androidx.annotation.ColorInt
import java.security.MessageDigest

/**
 * String to CharSequence
 *
 * @param textSpan: Input string you want to set span
 * @param textColor: Color for text span
 * @param index: Start with index of text span
 *
 * @author Simon Le
 */
fun String.spanTextColor(
    textSpan: String,
    @ColorInt textColor: Int,
    index: Int = 0
): CharSequence {
    if (textSpan.isEmpty()) {
        return this
    }
    val start = this.indexOf(textSpan) + index
    if (start < 0)
        return this
    val end = start + textSpan.length
    val fSpannableString = SpannableString(this)
    fSpannableString.setSpan(
        ForegroundColorSpan(textColor), start, end,
        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
    )
    return fSpannableString
}

/**
 * CharSequence to CharSequence
 *
 * @param textSpan: Input string you want to set span
 * @param textColor: Color for text span
 * @param index: Start with index of text span
 *
 * @author Simon Le
 */
fun CharSequence.spanTextColor(
    textSpan: String,
    @ColorInt textColor: Int,
    index: Int = 0
): CharSequence {
    if (textSpan.isEmpty()) {
        return this
    }
    val start = this.indexOf(textSpan) + index
    if (start < 0)
        return this
    val end = start + textSpan.length
    val fSpannableString = SpannableString(this)
    fSpannableString.setSpan(
        ForegroundColorSpan(textColor), start, end,
        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
    )
    return fSpannableString
}

/**
 * Regex phone number
 */
fun String?.isPhone(): Boolean {
    if (isNullOrEmpty())
        return false
    return this?.matches("^\\+(?:[0-9] ?){6,14}[0-9]$".toRegex()) ?: false
}

/**
 * Check String all digit
 */
fun String?.isDigitChar(): Boolean {
    if (isNullOrEmpty())
        return false
    return this?.matches("[0-9]".toRegex()) ?: false
}

/**
 * String to MD5 String
 */
fun String.toMD5(): String {
    val md = MessageDigest.getInstance("MD5")
    val digested = md.digest(toByteArray())
    return digested.joinToString("") {
        String.format("%02x", it)
    }
}

/**
 * To Base64
 */
fun String.toBase64(flags: Int = Base64.NO_WRAP): String {
    return Base64.encodeToString(this.toByteArray(), flags)
}

/**
 * From Base64
 */
fun String.fromBase64(flags: Int = Base64.NO_WRAP): String {
    return String(Base64.decode(this, flags))
}

/**
 * Valid email address
 */
fun String.validEmail(): Boolean {
    return Patterns.EMAIL_ADDRESS.matcher(this).matches()
}

//Function 1
inline infix fun <T> Boolean.then(param: () -> T): T? = if (this) param() else null

//Function 2
infix fun <T> Boolean.then(param: T): T? = if (this) param else null
