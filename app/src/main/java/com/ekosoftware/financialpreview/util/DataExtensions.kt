package com.ekosoftware.financialpreview.util

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.ekosoftware.financialpreview.R
import com.ekosoftware.financialpreview.app.Strings
import com.ekosoftware.financialpreview.data.model.SimpleDisplayableData
import com.ekosoftware.financialpreview.data.model.SimpleQueryData
import com.ekosoftware.financialpreview.data.model.budget.Budget
import com.ekosoftware.financialpreview.data.model.movement.Frequency
import com.ekosoftware.financialpreview.data.model.movement.MonthSummary
import com.ekosoftware.financialpreview.data.model.movement.Movement
import com.ekosoftware.financialpreview.data.model.movement.MovementUI
import com.ekosoftware.financialpreview.data.model.record.Record
import com.ekosoftware.financialpreview.data.model.settle.SettleGroupWithMovements
import com.ekosoftware.financialpreview.presentation.SelectionViewModel
import org.joda.time.Days
import org.joda.time.LocalDateTime
import java.math.BigDecimal
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
fun Int.getMonthAsString(): String = this.getMonth().toString().padStart(2, '0')


/**
 * Returns the result of given input month represented as an [Int] (e.g.: 1 for January)
 * into a [String] representing the abbreviated format of the month (e.g.: Jan for January).
 */
fun Int.parseToAbbreviatedString(): String = Strings.get(
    when (this) {
        1 -> R.string.month_jan_key
        2 -> R.string.month_feb_key
        3 -> R.string.month_mar_key
        4 -> R.string.month_apr_key
        5 -> R.string.month_may_key
        6 -> R.string.month_jun_key
        7 -> R.string.month_jul_key
        8 -> R.string.month_aug_key
        9 -> R.string.month_sep_key
        10 -> R.string.month_oct_key
        11 -> R.string.month_nov_key
        12 -> R.string.month_dec_key
        else -> throw IllegalArgumentException("Given parameter $this isn't a value month to be parsed to abbreviated string.")
    }
)


/**
 * Returns the result of adding the [this] month and the given months.
 *
 * @param months how many months to add.
 *
 * @throws IllegalArgumentException if params are 0.
 */
fun Int.plusMonths(months: Int): Int {

    //Returns same month, there is nothing to add
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
): Long /*=*/ {

    Log.d(
        "HOLA6", "taxes: ${
            this.movements.filter { movement ->
                Log.d("HOLA5", "taxes: $movement")
                movement.frequency!!.from!! <= fromTo
                        && fromTo <= movement.frequency!!.to!!
                        && movement.currencyCode == currencyCode
                        && filter(movement)
            }
        }"
    )

    return this.movements.filter { movement ->
        movement.frequency!!.from!! <= fromTo
                && fromTo <= movement.frequency!!.to!!
                && movement.currencyCode == currencyCode
                && filter(movement)
    }.sumOf {
        val left = BigDecimal(it.leftAmount)
        val percent = BigDecimal(this.settleGroup.percentage / 100) /*/ BigDecimal("100.0")*/
        val result = left.times(percent)
        result.toLong()
    }
}


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


/**
 * Combines three [LiveData] objects in one.
 * **See Also:**[StackOverflow](https://stackoverflow.com/a/57079290/10860780).
 *
 * @param liveData1 [LiveData] to combine to.
 * @param liveData2 [LiveData] to combine to.
 * @param block lambda code block.
 */
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


/**
 * TODO()
 */
fun currentYearMonth(): Int {
    val c = Calendar.getInstance()
    val year = c.get(Calendar.YEAR)
    val month = c.get(Calendar.MONTH) + 1
    return "$year${month.toString().padStart(2,'0')}".toInt()
}


/**
 * Takes an [Int] representing a month and year, and returns
 * it's corresponding month key in [String] format, e.g.: 202101 to "Jan".
 */
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
        12 -> Strings.get(R.string.month_dec_key)
        else -> throw IllegalArgumentException("Given $this isn't a month")
    }
}


/**
 * Takes a [Long] representing an amount which was parsed
 * for [androidx.room]'s database and parses it back as
 * a [Double]. For this process, amount must be divided
 * by 10_000.0, the inverse process that was performed
 * when parsing it for database.
 */
fun Long.forCommunicationAmount(): Double = this / 10_000.0


/**
 * Takes a [Long] representing and amount (times 10_000 because it was retrieved from database)
 * and returns a [String] version considering the amount of decimal places the currency permits.
 *
 * @param currencyCode [String] representing currency's code
 */
fun Long.forDisplayAmount(currencyCode: String): String {
    val amount = (this / 10_000.0).toString().split(".")
    // Part before the decimal indicator
    val preDecimal = amount[0]
    // Part after decimal indicator
    val postDecimal = "." + amount[1]
    return when (val maxDigits = Currency.getInstance(currencyCode).defaultFractionDigits) {
        // When no decimal places allowed, return first part
        0 -> preDecimal
        // When decimal places allowed, return first and second part, and pad to the end the
        // amount of zeros needed to reach the amount of decimal places permitted by currency
        else -> preDecimal + postDecimal.take(maxDigits + 1).padEnd(maxDigits + 1, '0')
    }
}


/**
 * Transforms a [Double] *this* (which represents and amount)
 * into a [String] ready for displaying in UI, taking into an account
 * the amount of fraction digits the provided *currency* allows.
 *
 * @param currencyCode [String] representing the currency of a [Movement]'s amount.
 */
fun Double.forDisplayAmount(currencyCode: String): String {
    val amount = this.toString().split(".")
    val preDecimal = amount[0]
    val postDecimal = "." + amount[1]
    // Takes as a param the amount of
    return when (val maxDigits = Currency.getInstance(currencyCode).defaultFractionDigits) {
        0 -> preDecimal
        else -> preDecimal + postDecimal.take(maxDigits + 1).padEnd(maxDigits + 1, '0')
    }
}


/**
 * Takes a [Double] representing an amount generated by
 * user input, and parses it into a [Long]. For this process,
 * amount must be multiplied by 10_000, to avoid floating points
 * error when making future SQL operations. For this purpose,
 * amounts ares multiplied and parsed to [Long] so operations
 * are done with Real Numbers instead of Rational Numbers.
 * The number 10_000 is needed because some currencies allow
 * up to four fraction digits after decimal point.
 *
 */
fun Double.forDatabaseAmount(): Long = (this * 10_000).toLong()


/**
 * TODO()
 */
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


/**
 * Takes a [MovementUI] *this* and returns a [String]
 * containing the number of quota from total installments,
 * as in *e.g.: " 8/12" for 8th quota from 12 in total*.
 */
fun MovementUI.installmentsCalc(): String {
    if (this.totalInstallments == null) return ""
    var current = this.fromYearMonth
    var counter = 0
    while (current < this.toYearMonth) {
        current = current.plusMonths(1)
        counter++
    }
    if (counter < 0) return ""
    return " " + Strings.get(R.string.installments_placeholder, this.totalInstallments!! - counter, this.totalInstallments!!)
}

/**
 * Takes a [Movement] *this* and returns a [String]
 * containing the number of quota from total installments,
 * as in *e.g.: " 8/12" for 8th quota from 12 in total*.
 */
fun Movement.installmentsCalc(): String {
    if (this.frequency == null || this.frequency?.installments == null) return ""
    var current = this.frequency!!.from!!
    var counter = 0
    while (current < this.frequency!!.to!!) {
        current = current.plusMonths(1)
        counter++
    }
    return if (counter < 0) return ""
    else " " + Strings.get(R.string.installments_placeholder, this.frequency!!.installments!! - counter, this.frequency!!.installments!!)
}


/**
 * TODO()
 */
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


/**
 * Returns a summary of parts of the [Frequency].
 *
 * Depending on the information provided by the [Frequency] itself,
 * the resulting [String] communicates different information. For instance,
 * when the [Frequency.from] equals [Frequency.to], it indicates the
 * [Frequency] is a one-time [Movement].
 *
 * @return [String] representing the summary result.
 *
 */
fun Frequency?.forDisplay(): String {
    this?.let {
        Log.d(TAG, "forDisplay: from>${it.from} to>${it.to} installments>${it.installments} months>${it.monthsChecked}")
        return when {

            // When ´from´ equals ´to´, it indicates the Frequency is a one-time Movement,
            // so it returns the month's name (monthNameKey) and the year, as in "Jan-2021"
            it.from == it.to -> "${it.from!!.monthNameKey()}-${it.from!!.getYear()}"

            // When ´to´ equals 999999, it means the Movement is a never ending
            // repeated one. So result string indicates Movements repeats from
            // certain month and year and whether all months are included or not.
            to == 999999 -> Strings.get(
                R.string.repeats_placeholder_from,
                it.from!!.monthNameKey(),
                it.from!!.getYear(),
                if (it.monthsChecked == Strings.get(R.string.all_months_checked_frequency)) Strings.get(R.string.every_month) else Strings.get(R.string.some_months)
            )

            // If there are any installments at all, just return how much
            // of them and when it starts.
            it.installments != null && it.installments!! > 1 -> Strings.get(
                R.string.installments_frequency_placeholder,
                it.installments!!,
                (it.from?.getMonth() ?: 1).parseToAbbreviatedString(),
                it.from!!.getYear()
            )

            // If none of above, summary must indicate it repeats,
            // and from when till when, and if every month, or not.
            else -> Strings.get(
                R.string.repeats_placeholder_to,
                it.from!!.monthNameKey(),
                it.from!!.getYear(),
                it.to!!.monthNameKey(),
                it.to!!.getYear(),
                if (this.monthsChecked == Strings.get(R.string.all_months_checked_frequency)) Strings.get(R.string.every_month) else Strings.get(R.string.some_months)
            )
        }
    }
    return ""
}


/**
 * Parses [SimpleQueryData] *this* to [SimpleDisplayableData].
 * Called when data from database loaded with general purpose [SimpleQueryData] class,
 * needs to be transform into displayable data, because of it's general purpose data needs
 * to be adequate to be presented.
 *
 * @param type [Int] representing a constant value declared in [SelectionViewModel].
 */
fun SimpleQueryData.forDisplay(type: Int): SimpleDisplayableData {
    return SimpleDisplayableData(
        id,
        name,
        // Description property varies for some of the object types.
        when (type) {
            // When obj is an Account, description is the typeId, which stands for a @StringResId.
            SelectionViewModel.ACCOUNTS -> Strings.get(typeId!!)
            // When a Currency, it's display name.
            SelectionViewModel.CURRENCIES -> Currency.getInstance(id).displayName.capitalize(Locale.ROOT)
            // When a Movement, the currency and left amount.
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

/**
 * Checks whether given number is a factor o 100.
 * Called by [hasUselessDecimals].
 */
fun factor100(n: Number) = n.toDouble() % 100.0 == 0.0

/**
 * Returns true if given [BigDecimal]'s decimal point numbers are different from zeros.
 * Checks whether given number times 100 is a factor of 100.
 *
 * @see factor100
 */
fun BigDecimal.hasUselessDecimals(): Boolean = factor100(times(BigDecimal("100")))

fun String.removeZeros(): String {
    return if (this.contains(".") && (this.endsWith("0") || this.endsWith("."))) {
        this.substring(0, this.length - 1).removeZeros()
    } else {
        this
    }
}

fun getDaysAgo(days: Int): Date {
    val today = LocalDateTime()
    val aWeekAgo = today.minusDays(days)
    return aWeekAgo.toDate()
}

fun newId() = UUID.randomUUID().toString()

fun Movement.duplicate(): Movement {
    return this.apply {
        id = newId()
    }
}

fun Budget.duplicate(): Budget {
    return this.apply {
        id = newId()
    }
}

fun SimpleQueryData.displayable(): SimpleDisplayableData {
    return SimpleDisplayableData(
        this.id,
        this.name,
        this.description ?: Strings.get(
            if (typeId != null) typeId!! else
                R.string.amount_holder,
            currencyCode!!,
            amount!!
        ),
        this.color,
        this.iconResId
    )
}