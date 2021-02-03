package com.ekosoftware.financialpreview.data.local.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.ekosoftware.financialpreview.data.model.Category
import com.ekosoftware.financialpreview.data.model.account.Account
import com.ekosoftware.financialpreview.data.model.budget.Budget
import com.ekosoftware.financialpreview.data.model.currency.Currency
import com.ekosoftware.financialpreview.data.model.movement.Movement
import com.ekosoftware.financialpreview.data.model.record.Record
import com.ekosoftware.financialpreview.data.model.settle.SettleGroup
import com.ekosoftware.financialpreview.data.model.settle.SettleGroupMovementsCrossRef

@Dao
interface SomeDao {

    @Insert
    fun insertCurrency(vararg currency: Currency)

    @Insert
    fun insertAccount(vararg account: Account)

    @Insert
    fun insertCategory(vararg category: Category)

    @Insert
    fun insertBudget(vararg budget: Budget)

    @Insert
    fun insertMovement(vararg movement: Movement)

    @Insert
    fun insertSettleGroup(vararg settleGroup: SettleGroup)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertSettleGroupCrossRef(vararg settleGroupMovementsCrossRef: SettleGroupMovementsCrossRef)

    @Insert
    fun insertRecord(vararg record: Record)

}