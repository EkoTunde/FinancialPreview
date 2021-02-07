package com.ekosoftware.financialpreview.presentation

import androidx.lifecycle.*
import com.ekosoftware.financialpreview.core.BaseEntryViewModel
import com.ekosoftware.financialpreview.data.model.movement.Frequency
import com.ekosoftware.financialpreview.data.model.movement.Movement
import com.ekosoftware.financialpreview.domain.local.EntryRepository
import com.ekosoftware.financialpreview.util.forDatabaseAmount
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EntryMovementViewModel @Inject constructor(
    private val entryRepository: EntryRepository,
    private val savedStateHandle: SavedStateHandle
) : BaseEntryViewModel(entryRepository) {

    companion object {
        private const val ENTRY_ACCOUNT_ID_KEY = "accountIdForMovementKey"
        private const val ENTRY_BUDGET_ID_KEY = "budgetIdForMovementKey"
        private const val ENTRY_CATEGORY_ID_KEY = "categoryIdForMovementKey"
        private const val ENTRY_CURRENCY_CODE_KEY = "currencyCodeForMovementKey"
        private const val ENTRY_FREQUENCY_KEY = "frequencyForMovementKey"
        private const val ENTRY_MOVEMENT_ID_KEY = "movementIdKey"
        private const val ENTRY_LEFT_AMOUNT_KEY = "leftAmountKey"

        private const val WAS_MOVEMENT_RETRIEVED_KEY = "was_movement_retrieved_ok"
    }

    fun movementRetrievedOk() {
        savedStateHandle[WAS_MOVEMENT_RETRIEVED_KEY] = true
    }

    private val wasMovementRetrieved = savedStateHandle.getLiveData(WAS_MOVEMENT_RETRIEVED_KEY, false)

    private var _movement: LiveData<Movement?>? = null

    fun movement(id: String): LiveData<Movement?> = _movement ?: wasMovementRetrieved.switchMap { wasRetrieved ->
        liveData<Movement?>(viewModelScope.coroutineContext + Dispatchers.IO) {
            if (!wasRetrieved) emit(entryRepository.getMovement(id))
        }.also {
            _movement = it
        }
    }

    private val accountId: MutableLiveData<String?> = savedStateHandle.getLiveData(ENTRY_ACCOUNT_ID_KEY, null)

    fun setAccountId(id: String?) {
        accountId.value = id
    }

    private var _accountName: LiveData<String>? = null

    fun accountName(): LiveData<String> = _accountName ?: accountId.distinctUntilChanged().switchMap { id ->
        liveData(viewModelScope.coroutineContext + Dispatchers.IO) {
            if (id != null) {
                emitSource(entryRepository.getAccountName(id))
            } else emit("")
        }
    }.also {
        _accountName = it
    }

    private val budgetId: MutableLiveData<String?> = savedStateHandle.getLiveData(ENTRY_BUDGET_ID_KEY, null)

    fun setBudgetId(id: String?) {
        budgetId.value = id
    }

    private var _budgetName: LiveData<String>? = null

    fun budgetName(): LiveData<String> = _budgetName ?: budgetId.distinctUntilChanged().switchMap { id ->
        liveData(viewModelScope.coroutineContext + Dispatchers.IO) {
            if (id != null) emitSource(entryRepository.getBudgetName(id)) else emit("")
        }
    }.also {
        _budgetName = it
    }

    private val categoryId: MutableLiveData<String?> = savedStateHandle.getLiveData(ENTRY_CATEGORY_ID_KEY, null)

    fun setCategoryId(id: String?) {
        categoryId.value = id
    }

    private var _categoryName: LiveData<String>? = null

    fun categoryName(): LiveData<String> = _categoryName ?: categoryId.distinctUntilChanged().switchMap { id ->
        liveData(viewModelScope.coroutineContext + Dispatchers.IO) {
            if (id != null) emitSource(entryRepository.getCategoryName(id)) else emit("")
        }
    }.also { _categoryName = it }

    fun setCurrencyCode(code: String?) {
        savedStateHandle[ENTRY_CURRENCY_CODE_KEY] = code
    }

    private var _currencyCode: LiveData<String>? = null

    fun currencyCode(): LiveData<String> = _currencyCode ?: /*currencyId*/accountId.distinctUntilChanged().switchMap { id ->
        liveData(viewModelScope.coroutineContext + Dispatchers.IO) {
            if (id != null) {
                emitSource(entryRepository.getAccountCurrencyCode(id))
            } else {
                emitSource(savedStateHandle.getLiveData(ENTRY_CURRENCY_CODE_KEY, ""))
            }
        }
    }.also { _currencyCode = it }

    private var _frequency: LiveData<Frequency?>? = null

    fun setFrequency(frequency: Frequency?) {
        savedStateHandle[ENTRY_FREQUENCY_KEY] = frequency
    }

    fun getSingleFrequency(): Frequency? {
        return savedStateHandle[ENTRY_FREQUENCY_KEY]
    }

    fun frequency(): LiveData<Frequency?> = _frequency ?: liveData {
        emitSource(savedStateHandle.getLiveData<Frequency>(ENTRY_FREQUENCY_KEY, null))
    }.also {
        _frequency = it
    }

    private var _leftAmount: LiveData<Double?>? = null

    fun setLeftAmount(amount: Double?) {
        savedStateHandle[ENTRY_LEFT_AMOUNT_KEY] = amount
    }

    fun getLeftAmount(): LiveData<Double?> = _leftAmount ?: liveData<Double?> {
        emitSource(savedStateHandle.getLiveData<Double?>(ENTRY_LEFT_AMOUNT_KEY, null))
    }.also {
        _leftAmount = it
    }

    fun save(id: String, leftAmount: Double, startingAmount: Double, currencyCode: String, name: String, description: String) {
        addData(
            Movement(
                id,
                leftAmount = leftAmount.forDatabaseAmount(),
                startingAmount = startingAmount.forDatabaseAmount(),
                currencyCode = currencyCode,
                name = name,
                description = if (description.isEmpty()) null else description,
                frequency = savedStateHandle[ENTRY_FREQUENCY_KEY],
                accountId = accountId.value,
                categoryId = categoryId.value,
                budgetId = budgetId.value
            ),
            id
        )
    }

    fun delete(id: String) {
        viewModelScope.launch { entryRepository.deleteMovementWithId(id) }
    }

    fun clearAllSelectedData() {
        savedStateHandle[ENTRY_ACCOUNT_ID_KEY] = null
        savedStateHandle[ENTRY_BUDGET_ID_KEY] = null
        savedStateHandle[ENTRY_CATEGORY_ID_KEY] = null
        savedStateHandle[ENTRY_CURRENCY_CODE_KEY] = null
        savedStateHandle[ENTRY_FREQUENCY_KEY] = null
        savedStateHandle[ENTRY_MOVEMENT_ID_KEY] = null
        savedStateHandle[WAS_MOVEMENT_RETRIEVED_KEY] = false
        _movement = null
    }
}