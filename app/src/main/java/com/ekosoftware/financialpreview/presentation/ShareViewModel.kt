package com.ekosoftware.financialpreview.presentation

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.ekosoftware.financialpreview.R
import com.ekosoftware.financialpreview.app.Strings
import com.ekosoftware.financialpreview.data.model.account.Account
import com.ekosoftware.financialpreview.data.model.settle.SettleGroupWithMovements

class ShareViewModel @ViewModelInject constructor(
    @Assisted private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    companion object {
        private const val KEY_SELECTED_MOVEMENT_ID = "selectedMovementId"
        private const val KEY_SELECTED_BUDGET_ID = "selectedBudgetId"
        private const val KEY_SELECTED_SETTLE_GROUP_ID = "selectedSettleGroupId"
        private const val KEY_SELECTED_CALCULATOR_AMOUNT = "selectedCalculatorAmount"
        private const val KEY_SELECTED_ACCOUNT = "selectedAccount"
    }

    val selectedMovementId = savedStateHandle.getLiveData<Int>(KEY_SELECTED_MOVEMENT_ID, 0)

    fun selectMovementId(id: Int) {
        selectedMovementId.value = id
    }

    val selectedBudgetId = savedStateHandle.getLiveData<Int>(KEY_SELECTED_BUDGET_ID, 0)

    fun selectBudgetId(id: Int) {
        selectedBudgetId.value = id
    }

    val selectedSettleGroup =
        savedStateHandle.getLiveData<SettleGroupWithMovements>(KEY_SELECTED_SETTLE_GROUP_ID)

    fun selectSettleGroup(settleGroupWithMovements: SettleGroupWithMovements) {
        selectedSettleGroup.value = settleGroupWithMovements
    }

    val calculatorAmount = savedStateHandle.getLiveData<Double>(KEY_SELECTED_CALCULATOR_AMOUNT, .0)

    fun selectCalculatorAmount(amount: Double) {
        calculatorAmount.value = amount
    }

    val selectedAccount = savedStateHandle.getLiveData<Account>(KEY_SELECTED_ACCOUNT)

    fun selectAccount(account: Account) {
        selectedAccount.value = account
    }

    val selectedAccountItem = MutableLiveData<SimpleQueryData>()

    fun selectAccountItem(id: String, name: String) {
        selectedAccountItem.value = SimpleQueryData(id, name, null, null)
    }

}

data class SimpleQueryData(
    var id: String,
    var name: String,
    var currencyCode: String? = null,
    var amount: Double? = null,
    var typeId: Int? = null,
    var description: String? = null,
    var color: Int? = null,
    var iconResId: Int? = null
)

data class SimpleDisplayedData(
    var id: String,
    var name: String,
    var description: String? = null,
    var color: Int? = null,
    var iconResId: Int? = null
)

fun SimpleQueryData.displayable(): SimpleDisplayedData {
    return SimpleDisplayedData(
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