package com.ekosoftware.financialpreview.data.model.movement

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue
import kotlin.math.max


@Parcelize
@Entity(tableName = "movements")
data class Movement(

    @PrimaryKey
    @ColumnInfo(name = "movementId", index = true)
    var id: String,

    @ColumnInfo(name = "movementLeftAmount")
    var leftAmount: Long,

    @ColumnInfo(name = "movementStartingAmount")
    var startingAmount: Long,

    @ColumnInfo(name = "movementCurrencyCode")
    var currencyCode: String,

    @ColumnInfo(name = "movementName")
    var name: String,

    @Embedded(prefix = "movement")
    var frequency: @RawValue Frequency?,

    @ColumnInfo(name = "movementAccountId")
    var accountId: String?,

    @ColumnInfo(name = "movementCategoryId")
    var categoryId: String?,

    @ColumnInfo(name = "movementBudgetId")
    var budgetId: String?

) : Parcelable {

    /**
     * Returns the year, e.g.: 2019 from [Int] representing a year and a month, e.g.: 201907.
     */
    private fun Int.getYear() = this.toString().substring(0, 4).toInt()

    /**
     * Returns the month, e.g.: 07 from [Int] representing a year and a month, e.g.: 201907.
     */
    private fun Int.getMonth() = this.toString().substring(4).toInt()

    /**
     * Returns the result of adding the current month and the given months.
     *
     * @param current the current month to start adding
     * @param months how many months to add.
     *
     * @throws IllegalArgumentException if params are 0.
     */
    fun plus(current: Int, months: Int): Int {
        if (current == 0 || months == 0) throw IllegalArgumentException("Parameters can't be null.")
        var currentYear = current.getYear() + (months / 12)
        var currentMonth = current.getMonth() + (months % 12)
        if (currentMonth > 12) { // Because months aren't bigger than 12
            currentYear += (currentMonth / 12)
            currentMonth = (currentMonth - 12)
        }
        return "${currentYear}${
            currentMonth.toString().padStart(2, '0')
        }".toInt() // Pad starts puts a zero before one digit months
    }

    /**
     * Returns next time the month will be the same as starting one.
     *
     * @param startingMonth represents the month and year
     * @param steps space between months
     */
    private fun leastRepeatedMonth(startingMonth: Int, steps: Int): Int {
        val nextCommon = lcm(steps)
        return plus(startingMonth, nextCommon)
    }

    /**
     * Returns the least common number between given steps and 12.
     *
     * @param steps number to get lcm with.
     */
    private fun lcm(steps: Int): Int {
        var lcm = max(steps, 12)
        //Running Loop to find out LCM
        while (true) {
            //check lcm value divisible by both the numbers
            if (lcm % steps == 0 && lcm % 12 == 0) {
                //break the loop if condition satisfies
                break
            }
            //increase lcm value by 1
            lcm++
        }
        return lcm
    }

    /**
     * Return installments from starting year and month till next time the month is same as starting.
     *
     * @param start starting [Int] representing
     * @param steps months between installment's months.
     */
    private fun create(start: Int, steps: Int): MutableList<Int> {
        val stop = leastRepeatedMonth(start, steps)
        var current = start
        val installments = mutableListOf<Int>()
        while (current < stop) {
            installments.add(current)
            current = plus(current, steps)
        }
        return installments
    }

    /**
     * @param current a given year and month, e.g.: 201809, to calculate installment no.
     * @param end the last year and month, e.g.: 201809.
     * @param total total installments from payment plan.
     * @param steps how many months between installments.
     * @return the installment number.
     */
    fun nthForCurrent(current: Int, end: Int, total: Int, steps: Int): Int =
        total - (create(current, steps).filter { it <= end }.size - 1)

    /**
     * Gets next installment month.
     *
     * @param currentYM [Int] representing the current year-month.
     * @return next month, counting from given current year-month [Int], taking steps between months into an account.
     */
    //fun nextMonthForStep(currentYM: Int): Int = create(currentYM, this.steps)[1]
}