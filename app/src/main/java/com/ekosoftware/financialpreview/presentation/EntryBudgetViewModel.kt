package com.ekosoftware.financialpreview.presentation

import androidx.lifecycle.*
import com.ekosoftware.financialpreview.core.BaseEntryViewModel
import com.ekosoftware.financialpreview.data.model.budget.Budget
import com.ekosoftware.financialpreview.data.model.movement.Frequency
import com.ekosoftware.financialpreview.domain.local.EntryRepository
import com.ekosoftware.financialpreview.util.forDatabaseAmount
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EntryBudgetViewModel @Inject constructor(
    private val entryRepository: EntryRepository,
    private val savedStateHandle: SavedStateHandle
) : BaseEntryViewModel(entryRepository) {

    companion object {
        private const val ENTRY_CURRENCY_CODE_KEY = "currencyCodeForBudgetKey"
        private const val ENTRY_FREQUENCY_KEY = "frequencyForBudgetKey"
        private const val ENTRY_LEFT_AMOUNT_KEY = "leftAmountBudgetKey"

        private const val WAS_BUDGET_RETRIEVED_KEY = "was_budget_retrieved_ok"
    }

    fun budgetRetrievedOk() {
        savedStateHandle[WAS_BUDGET_RETRIEVED_KEY] = true
    }

    private val wasBudgetRetrieved = savedStateHandle.getLiveData(WAS_BUDGET_RETRIEVED_KEY, false)

    private var _budget: LiveData<Budget?>? = null

    fun get(id: String): LiveData<Budget?> = _budget ?: wasBudgetRetrieved.switchMap { wasRetrieved ->
        liveData<Budget?>(viewModelScope.coroutineContext + Dispatchers.IO) {
            if (!wasRetrieved) emit(entryRepository.getBudget(id))
        }.also {
            _budget = it
        }
    }

    fun setCurrencyCode(code: String?) {
        savedStateHandle[ENTRY_CURRENCY_CODE_KEY] = code
    }

    private var _currencyCode: LiveData<String>? = null

    fun currencyCode(): LiveData<String> = _currencyCode ?: liveData(viewModelScope.coroutineContext + Dispatchers.IO) {
        emitSource(savedStateHandle.getLiveData(ENTRY_CURRENCY_CODE_KEY, ""))
    }.also { _currencyCode = it }

    private var _frequency: LiveData<Frequency?>? = null

    fun setFrequency(frequency: Frequency?) {
        savedStateHandle[ENTRY_FREQUENCY_KEY] = frequency
    }

    fun getSingleFrequency(): Frequency? {
        return savedStateHandle[ENTRY_FREQUENCY_KEY]
    }

    fun frequency(): LiveData<Frequency?> = _frequency ?: liveData<Frequency?> {
        emitSource(savedStateHandle.getLiveData<Frequency>(ENTRY_FREQUENCY_KEY, null))
    }.also {
        _frequency = it
    }

    private var _leftAmount: LiveData<Double?>? = null

    fun setLeftAmount(amount: Double?) {
        savedStateHandle[ENTRY_LEFT_AMOUNT_KEY] = amount
    }

    fun leftAmount(): LiveData<Double?> = _leftAmount ?: liveData<Double?> {
        emitSource(savedStateHandle.getLiveData<Double?>(ENTRY_LEFT_AMOUNT_KEY, null))
    }.also {
        _leftAmount = it
    }

    fun save(id: String, leftAmount: Double, startingAmount: Double, currencyCode: String, name: String, description: String) {
        addData(
            Budget(
                id,
                leftAmount = leftAmount.forDatabaseAmount(),
                startingAmount = startingAmount.forDatabaseAmount(),
                currencyCode = currencyCode,
                name = name,
                description = if (description.isEmpty()) null else description,
                frequency = savedStateHandle[ENTRY_FREQUENCY_KEY]
            ),
            id
        )
    }

    fun delete(id: String) {
        viewModelScope.launch { entryRepository.deleteBudgetWithId(id) }
    }

    fun clearAllSelectedData() {
        savedStateHandle[ENTRY_CURRENCY_CODE_KEY] = null
        savedStateHandle[ENTRY_FREQUENCY_KEY] = null
        savedStateHandle[ENTRY_LEFT_AMOUNT_KEY] = null
        savedStateHandle[WAS_BUDGET_RETRIEVED_KEY] = false
        _budget = null
    }
}