package com.ekosoftware.financialpreview.util

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.ekosoftware.financialpreview.R
import com.ekosoftware.financialpreview.app.Strings
import com.ekosoftware.financialpreview.data.model.movement.Frequency
import com.ekosoftware.financialpreview.data.model.movement.MonthSummary
import com.ekosoftware.financialpreview.data.model.movement.Movement
import com.ekosoftware.financialpreview.data.model.movement.MovementUI
import com.ekosoftware.financialpreview.data.model.settle.SettleGroupWithMovements
import com.ekosoftware.financialpreview.presentation.SelectionViewModel
import com.ekosoftware.financialpreview.presentation.SimpleDisplayableData
import com.ekosoftware.financialpreview.presentation.SimpleQueryData
import org.joda.time.Days
import org.joda.time.LocalDateTime
import java.text.SimpleDateFormat
import java.util.*

private const val TAG = "DataExtensions"

/**
 * Returns the year, e.g.: 2019 from [Int] representing a year and a month, e.g.: 201907.
 */
fun Int.getYear() = this.toString().substring(0, 4).toInt()

/**
 * Returns the month, e.g.: 7 from [Int] representing a year and a month, e.g.: 201907.
 */
fun Int.getMonth(): Int {
    return this.toString().substring(4).toInt()
}

/**
 * Returns the month as String, e.g.: 07 from [Int] representing a year and a month, e.g.: 201907.
 */
fun Int.getMonthAsString(): String = this.toString().substring(4)

/**
 * Returns the result of adding the [this] month and the given months.
 *
 * @param months how many months to add.
 *
 * @throws IllegalArgumentException if params are 0.
 */
fun Int.plusMonths(months: Int): Int {
    //println("getyear() = ${getYear()} + months / 12 = ${months / 12}")

    if (months == 0) return this
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
): Long =
    this.movements.filter { movement ->
        movement.frequency!!.from!! >= fromTo
                && movement.frequency!!.to!! <= fromTo
                && movement.currencyCode == currencyCode
                && filter(movement)
    }.sumOf { it.leftAmount.times(this.settleGroup.percentage / 100).toLong() }

/**
 * Transforms a [List]<[SettleGroupWithMovements]> into a MonthSummary computing only
 * movements' taxes for a specified month and year ([fromTo]) and a particular [currency].
 *
 *  @param fromTo an [Int] representing a year and month (i.e.: 202010) to filter movements list.
 *  @param currency [String] formatted currency code to filter movements list.
 *
 * @return [MonthSummary]
 *
 * @see [SettleGroupWithMovements.taxes]
 */
fun List<SettleGroupWithMovements>.summary(fromTo: Int, currency: String): MonthSummary {
    val incomesTotal = this.takeUnless { it.isNullOrEmpty() }?.sumOf {
        it.taxes(fromTo, currency) { movement -> movement.leftAmount >= 0 }
    } ?: 0.0
    val expensesTotal = this.takeUnless { it.isNullOrEmpty() }?.sumOf {
        it.taxes(fromTo, currency) { movement -> movement.leftAmount < 0 }
    } ?: 0.0
    return MonthSummary(currency, fromTo, incomesTotal.toLong(), expensesTotal.toLong())
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

fun <X, Y, Z, R> LiveData<X>.combineWith(
    liveData1: LiveData<Y>,
    liveData2: LiveData<Z>,
    block: (X?, Y?, Z?) -> R
): LiveData<R> {
    val result = MediatorLiveData<R>()
    result.addSource(this) {
        result.value = block(this.value, liveData1.value, liveData2.value)
    }
    result.addSource(liveData1) {
        result.value = block(this.value, liveData1.value, liveData2.value)
    }
    result.addSource(liveData2) {
        result.value = block(this.value, liveData1.value, liveData2.value)
    }
    return result
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
        11 -> Strings.get(R.string.month_nov_key)
        else -> Strings.get(R.string.month_dec_key)
    }
}

fun Long.forCommunicationAmount(): Double = this / 10_000.0
fun Long.forDisplayAmount(currencyCode: String): String {
    val amount = (this / 10_000.0).toString().split(".")
    val preDecimal = amount[0]
    val postDecimal = "." + amount[1]
    println("currency is $currencyCode")
    return when (val maxDigits = Currency.getInstance(currencyCode).defaultFractionDigits) {
        0 -> preDecimal
        else -> preDecimal + postDecimal.take(maxDigits + 1).padEnd(maxDigits + 1, '0')
    }
}

fun Double.forDisplayAmount(currencyCode: String): String {
    val amount = this.toString().split(".")
    val preDecimal = amount[0]
    val postDecimal = "." + amount[1]

    return when (val maxDigits = Currency.getInstance(currencyCode).defaultFractionDigits) {
        0 -> preDecimal
        else -> preDecimal + postDecimal.take(maxDigits + 1).padEnd(maxDigits + 1, '0')
    }
}

fun Double.forDatabaseAmount(): Long = (this * 10_000).toLong()
private fun Date.asString(): String {
    val date = LocalDateTime(this)
    val current = LocalDateTime()
    return when (val diff = Days.daysBetween(date, current).days) {
        0 -> Strings.get(R.string.today)
        1 -> Strings.get(R.string.yesterday)
        in 2 until 7 -> Strings.get(R.string.days_ago, diff)
        7 -> Strings.get(R.string.a_week_ago)
        else -> SimpleDateFormat("yyyy-MMM-dd ", Locale.getDefault()).format(this)
    }
}

fun MovementUI.installmentsCalc(): String {
    if (this.totalInstallments == null) return ""
    var current = this.fromYearMonth
    var counter = 0
    while (current < this.toYearMonth) {
        current = current.plusMonths(1)
        counter++
    }
    return if (counter == 0) return ""
    else " " + Strings.get(R.string.installments_placeholder, counter, this.totalInstallments!!)
}

fun String.monthsCheckedSummary(): String {
    val builder = StringBuilder()
    builder.append(Strings.get(R.string.is_repeated_on))
    this.split("-").filter { it != "000" }.map {
        when (it) {
            Strings.get(R.string.month_jan_key) -> Strings.get(R.string.month_jan_extended)
            Strings.get(R.string.month_feb_key) -> Strings.get(R.string.month_feb_extended)
            Strings.get(R.string.month_mar_key) -> Strings.get(R.string.month_mar_extended)
            Strings.get(R.string.month_apr_key) -> Strings.get(R.string.month_apr_extended)
            Strings.get(R.string.month_may_key) -> Strings.get(R.string.month_may_extended)
            Strings.get(R.string.month_jun_key) -> Strings.get(R.string.month_jun_extended)
            Strings.get(R.string.month_jul_key) -> Strings.get(R.string.month_jul_extended)
            Strings.get(R.string.month_aug_key) -> Strings.get(R.string.month_aug_extended)
            Strings.get(R.string.month_sep_key) -> Strings.get(R.string.month_sep_extended)
            Strings.get(R.string.month_oct_key) -> Strings.get(R.string.month_oct_extended)
            Strings.get(R.string.month_nov_key) -> Strings.get(R.string.month_nov_extended)
            Strings.get(R.string.month_dec_key) -> Strings.get(R.string.month_dec_extended)
            else -> ""
        }
    }.forEachIndexed { i, s ->
        if (i != 0) builder.append(", ")
        builder.append(s)
    }
    return builder.toString()
}

fun Frequency?.forDisplay(): String {
    this?.let {
        Log.d(TAG, "forDisplay: from>${it.from} to>${it.to} installments>${it.installments}")
        return when {
            it.from == it.to -> "${it.from!!.monthNameKey()}-${it.from!!.getYear()}"
            to == 999999 -> Strings.get(
                R.string.repeats_placeholder_from,
                it.from!!.monthNameKey(),
                it.from!!.getYear(),
                if (it.monthsChecked == Strings.get(R.string.all_months_checked_frequency)) {
                    Strings.get(R.string.every_month)
                } else Strings.get(R.string.some_months)
            )
            it.installments != null || it.installments!! > 0 -> Strings.get(R.string.installments_frequency_placeholder, it.installments!!)
            else -> Strings.get(
                R.string.repeats_placeholder_to,
                it.from!!.monthNameKey(),
                it.from!!.getYear(),
                it.to!!.monthNameKey(),
                it.to!!.getYear(),
                if (this.monthsChecked == Strings.get(R.string.all_months_checked_frequency)) {
                    Strings.get(R.string.every_month)
                } else Strings.get(R.string.some_months)
            )
        }
    }
    return ""
}

fun SimpleQueryData.forDisplay(type: Int): SimpleDisplayableData {
    return SimpleDisplayableData(
        id,
        name,
        when (type) {
            SelectionViewModel.ACCOUNTS -> Strings.get(typeId!!)
            SelectionViewModel.CURRENCIES -> Currency.getInstance(id).displayName.capitalize(Locale.ROOT)
            SelectionViewModel.MOVEMENTS -> Strings.get(
                R.string.amount_holder,
                currencyCode!!,
                amount!!.forDisplayAmount(currencyCode!!)
            )
            else -> description
        },
        color,
        iconResId
    )
}