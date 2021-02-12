package com.ekosoftware.financialpreview.util

import android.graphics.LinearGradient
import android.graphics.Shader
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextPaint
import android.text.style.RelativeSizeSpan
import android.text.style.SuperscriptSpan
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import java.math.BigDecimal
import java.math.RoundingMode

fun TextView.applyMoneyFormat(currency: String, amount: Double) {

    val withCurrencyFormat = amount.forDisplayAmount(currency)

    text = if (!withCurrencyFormat.contains(".")) {
        withCurrencyFormat
    } else {
        val wholeNo = withCurrencyFormat.split(".")[0]
        val decimalNo = withCurrencyFormat.split(".")[1]
        // Initialize a new String variable
        val inputText = "$currency $wholeNo $decimalNo"
        SpannableStringBuilder(inputText).apply {
            setSpan(
                SuperscriptSpan(),  // Span to add
                inputText.lastIndexOf(decimalNo),  // Start of the span (inclusive)
                inputText.lastIndexOf(decimalNo) + decimalNo.length,  // End of the span (exclusive)
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE // Do not extend the span when text add later
            )
            setSpan(
                RelativeSizeSpan(.5f),
                inputText.lastIndexOf(decimalNo), // Start of the span (inclusive)
                inputText.lastIndexOf(decimalNo) + decimalNo.length, // End of the span (exclusive)
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE // Do not extend the span when text add later
            )
        }
    }
}

fun TextView.applyMoneyFormatInK(currency: String, amount: Double) {
    val newAmount = BigDecimal(amount).div(BigDecimal("1000.00")).setScale(1, RoundingMode.DOWN)
    val fixed = if (newAmount.hasUselessDecimals()) newAmount.toString().removeZeros() else newAmount.toString()
    val result = "$currency ${fixed}k"
    text = result
}

fun TextView.applyMoneyFormatAndSpecifiedStart(start: String, currency: String, amount: Double) {

    val withCurrencyFormat = amount.forDisplayAmount(currency)

    text = if (!withCurrencyFormat.contains(".")) {
        withCurrencyFormat
    } else {
        val wholeNo = withCurrencyFormat.split(".")[0]
        val decimalNo = withCurrencyFormat.split(".")[1]
        // Initialize a new String variable
        val inputText = "$start $currency $wholeNo $decimalNo"
        SpannableStringBuilder(inputText).apply {
            setSpan(
                SuperscriptSpan(),  // Span to add
                inputText.lastIndexOf(decimalNo),  // Start of the span (inclusive)
                inputText.lastIndexOf(decimalNo) + decimalNo.length,  // End of the span (exclusive)
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE // Do not extend the span when text add later
            )
            setSpan(
                RelativeSizeSpan(.45f),
                inputText.lastIndexOf(decimalNo), // Start of the span (inclusive)
                inputText.lastIndexOf(decimalNo) + decimalNo.length, // End of the span (exclusive)
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE // Do not extend the span when text add later
            )
        }
    }
}

fun TextView.applyShader(vararg colorResId: Int) = this.apply {
    val paint: TextPaint = paint
    val width = paint.measureText(text.toString())

    val textShader: Shader = LinearGradient(
        0f,
        0f,
        width,
        textSize,
        colorResId.map {
            ResourcesCompat.getColor(resources, it, null)
        }.toIntArray(),
        null,
        Shader.TileMode.CLAMP
    )
    paint.shader = textShader
    setTextColor(ResourcesCompat.getColor(resources, colorResId[0], null))
}

fun View.hide() = arrayOf(this).hide()

fun View.show() = arrayOf(this).show()

/*fun View.hide(dur: Long) = arrayOf(this).hide(dur)

fun View.show(dur: Long) = arrayOf(this).show(dur)*/

fun <T : View> Array<T>.show() = this.forEach { it.visibility = View.VISIBLE }

fun <T : View> Array<T>.hide() = this.forEach {
    it.visibility = View.GONE
}
/*

fun <T : View> Array<T>.hide(dur: Long) = this.forEach {
    it.animate()
        .alpha(0.0f)
        .setDuration(dur)
        .setListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                super.onAnimationEnd(animation)
                it.visibility = View.GONE
            }
        })
}

fun <T : View> Array<T>.show(dur: Long) = this.forEach {

    it.setVisibility()
    it.visibility = View.VISIBLE
    it.animate()
        .alpha(1.0f).duration = dur
}*/

fun View.visible(visible: Boolean) = if (visible) show() else hide()

fun ViewGroup.visible(visible: Boolean) = if (visible) show() else hide()