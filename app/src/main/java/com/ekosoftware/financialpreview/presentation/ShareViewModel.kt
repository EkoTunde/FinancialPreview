package com.ekosoftware.financialpreview.presentation

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.ekosoftware.financialpreview.data.model.settle.SettleGroupWithMovements

class ShareViewModel @ViewModelInject constructor(
    @Assisted private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    companion object {
        private const val KEY_SELECTED_MOVEMENT_ID = "selectedMovementId"
        private const val KEY_SELECTED_BUDGET_ID = "selectedBudgetId"
        private const val KEY_SELECTED_SETTLE_GROUP_ID = "selectedSettleGroupId"
        private const val KEY_SELECTED_CALCULATOR_AMOUNT = "selectedCalculatorAmount"
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

}