package com.ekosoftware.financialpreview.util

import android.content.Context
import android.graphics.LinearGradient
import android.graphics.Shader
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextPaint
import android.text.style.RelativeSizeSpan
import android.text.style.SuperscriptSpan
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.ekosoftware.financialpreview.R
import com.ekosoftware.financialpreview.app.Strings
import com.ekosoftware.financialpreview.data.model.movement.Movement
import com.ekosoftware.financialpreview.data.model.settle.SettleGroupWithMovements
import com.ekosoftware.financialpreview.data.model.movement.MonthSummary
import com.google.android.material.snackbar.Snackbar
import java.util.*

/**
 * Returns the year, e.g.: 2019 from [Int] representing a year and a month, e.g.: 201907.
 */
fun Int.getYear() = this.toString().substring(0, 4).toInt()

/**
 * Returns the month, e.g.: 07 from [Int] representing a year and a month, e.g.: 201907.
 */
fun Int.getMonth() = this.toString().substring(4).toInt()

/**
 * Returns the result of adding the [this] month and the given months.
 *
 * @param months how many months to add.
 *
 * @throws IllegalArgumentException if params are 0.
 */
fun Int.plusMonths(months: Int): Int {
    println("getyear() = ${getYear()} + months / 12 = ${months / 12}")

    // Gets the given year and adds the years
    // given by the times a year fits in given months
    // to add
    var currentYear = getYear() + (months / 12)

    // Gets the given month and adds the spare months
    // after subtracting the 12 for each year that
    // fits in given months to add
    var currentMonth = getMonth() + (months % 12)

    // If the resulting currentMonth is bigger than 12
    if (currentMonth > 12) {
        // Add to currentYear the times a year
        // fits in given months
        currentYear += (currentMonth / 12)
        // Takes out the extra year
        currentMonth = (currentMonth - 12)
    }
    return "${currentYear}${
        currentMonth.toString().padStart(2, '0')
    }".toInt() // Pad starts puts a zero before one digit months
}

fun TextView.applyMoneyFormat(currency: String, amount: Double) {
    val wholeNo = amount.toString().split(".")[0]
    val decimalNo = amount.toString().split(".")[1]
    // Initialize a new String variable
    val inputText = "$currency $wholeNo $decimalNo"
    text = SpannableStringBuilder(inputText).apply {
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

fun View.hide() {
    visibility = View.GONE
}

fun View.show() {
    visibility = View.VISIBLE
}

fun Context.toast(message: String, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, message, duration).show()
}

fun Fragment.toast(message: String, duration: Int = Toast.LENGTH_SHORT) {
    requireContext().toast(message, duration)
}

fun View.snack(
    message: String,
    duration: Int = Snackbar.LENGTH_LONG,
) {
    Snackbar.make(this, message, duration).show()
}

fun Context.stringResource(resIntId: Int, vararg formatArgs: Any?): String {
    return this.getString(resIntId, *formatArgs)
}

fun Fragment.stringResource(resIntId: Int, vararg formatArgs: Any?): String {
    return requireContext().stringResource(resIntId, *formatArgs)
}


/**
 * Combines two [LiveData] objects in one.
 * **See Also:**[StackOverflow](https://stackoverflow.com/a/57079290/10860780).
 *
 * @param liveData [LiveData] to combine to.
 * @param block lambda code block.
 */
fun <T, K, R> LiveData<T>.combineWith(
    liveData: LiveData<K>,
    block: (T?, K?) -> R
): LiveData<R> {
    val result = MediatorLiveData<R>()
    result.addSource(this) {
        result.value = block(this.value, liveData.value)
    }
    result.addSource(liveData) {
        result.value = block(this.value, liveData.value)
    }
    return result
}


/**
 * Transforms a [List] of [Movement]s into a [Double] representing the sum of the products between
 * [Movement]'s leftAmounts and the [SettleGroupWithMovements.settleGroup]'s taxes percentage rate.
 *
 *  @param fromTo an [Int] representing a year and month (i.e.: 202010) to filter movements list.
 *  @param currencyCode [String] formatted currency code to filter movements list.
 *  @param filter lambda expression to filter movements.
 *
 */
fun SettleGroupWithMovements.taxes(
    fromTo: Int,
    currencyCode: String,
    filter: (Movement) -> Boolean
): Double =
    this.movements.filter { movement ->
        movement.frequency!!.from!! >= fromTo
                && movement.frequency!!.to!! <= fromTo
                && movement.currencyCode == currencyCode
                && filter(movement)
    }.sumOf {
        it.leftAmount * (this.settleGroup.percentage / 100)
    }


/**
 * Transforms a [List]<[SettleGroupWithMovements]> into a PendingSummary computing only
 * movements' taxes (@see [SettleGroupWithMovements.taxes()]) for a specified month and year ([fromTo]) and a particular [currency].
 *
 *  @param fromTo an [Int] representing a year and month (i.e.: 202010) to filter movements list.
 *  @param currency [String] formatted currency code to filter movements list.
 *
 * @return [MonthSummary]
 */
fun List<SettleGroupWithMovements>.summary(fromTo: Int, currency: String): MonthSummary {
    val incomesTotal = this.takeUnless { it.isNullOrEmpty() }?.sumOf {
        it.taxes(fromTo, currency) { movement -> movement.leftAmount >= 0 }
    } ?: 0.0
    val expensesTotal = this.takeUnless { it.isNullOrEmpty() }?.sumOf {
        it.taxes(fromTo, currency) { movement -> movement.leftAmount < 0 }
    } ?: 0.0
    return MonthSummary(currency, fromTo, incomesTotal, expensesTotal)
}

fun currentYearMonth(): Int {
    val c = Calendar.getInstance()
    val year = c.get(Calendar.YEAR)
    val month = c.get(Calendar.MONTH) + 1
    return "$year$month".toInt()
}

fun Int.monthNameKey(): String {
    return when (this.getMonth()) {
        1 -> Strings.get(R.string.month_jan_key)
        2 -> Strings.get(R.string.month_feb_key)
        3 -> Strings.get(R.string.month_mar_key)
        4 -> Strings.get(R.string.month_apr_key)
        5 -> Strings.get(R.string.month_may_key)
        6 -> Strings.get(R.string.month_jun_key)
        7 -> Strings.get(R.string.month_jul_key)
        8 -> Strings.get(R.string.month_aug_key)
        9 -> Strings.get(R.string.month_sep_key)
        10 -> Strings.get(R.string.month_oct_key)
        11 -> Strings.get(R.string.month_noc_key)
        else -> Strings.get(R.string.month_dec_key)
    }
}