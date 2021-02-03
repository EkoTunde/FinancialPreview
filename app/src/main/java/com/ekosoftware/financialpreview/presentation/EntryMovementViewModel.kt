package com.ekosoftware.financialpreview.presentation

import android.util.Log
import androidx.lifecycle.*
import com.ekosoftware.financialpreview.data.model.movement.Frequency
import com.ekosoftware.financialpreview.data.model.movement.Movement
import com.ekosoftware.financialpreview.domain.local.EntryRepository
import com.ekosoftware.financialpreview.util.forDatabaseAmount
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EntryMovementViewModel @Inject constructor(
    private val entryRepository: EntryRepository,
    private val savedStateHandle: SavedStateHandle
) : EntryViewModel(entryRepository) {

    companion object {
        private const val ENTRY_ACCOUNT_ID_KEY = "accountIdForMovement"
        private const val ENTRY_BUDGET_ID_KEY = "budgetIdForMovement"
        private const val ENTRY_CATEGORY_ID_KEY = "categoryIdForMovement"
        private const val ENTRY_CURRENCY_ID_KEY = "currencyCodeForMovement"
        private const val ENTRY_FREQUENCY_ID_KEY = "frequencyForMovement"
        private const val ENTRY_MOVEMENT_ID_KEY = "movementId"

        private const val TAG = "EntryMovementViewModel"
    }

    private var movementWasRetrieved = false

    fun movementRetrievedOk() {
        movementWasRetrieved = true
        wasMovementRetrieved.value = true
    }

    val movementId: MutableLiveData<String> = savedStateHandle.getLiveData(ENTRY_MOVEMENT_ID_KEY, null)

    fun setMovementId(id: String) {
        movementId.value = id
    }

    val wasMovementRetrieved = savedStateHandle.getLiveData("movement_retrieved", false)

    private var m: LiveData<Movement?>? = null

    fun get(id: String): LiveData<Movement?> {
        Log.d(TAG, "get: GET")
        return m ?: wasMovementRetrieved.distinctUntilChanged().switchMap { wasRetrieved ->
            liveData<Movement?>(viewModelScope.coroutineContext + Dispatchers.IO) {
                if (wasRetrieved) emit(null) else emit(entryRepository.getMovement(id))
            }.also {
                m = it
            }
        }
    }

    val movement: LiveData<Movement?> = movementId.distinctUntilChanged().switchMap { id ->
        liveData<Movement?>(viewModelScope.coroutineContext + Dispatchers.IO) {
            if (movementWasRetrieved) emit(null) else entryRepository.getMovement(id)
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
                val currencyCode = entryRepository.getAccountCurrencyCode(id)
                CoroutineScope(Dispatchers.Main).launch { setCurrencyId(currencyCode) }
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
            if (id != null) {
                Log.d(TAG, "categoryName: EMITTING AGAIN. id is = $id")
                emitSource(entryRepository.getCategoryName(id))
            } else emit("")
        }
    }.also {
        _categoryName = it
    }

    private val currencyId: MutableLiveData<String?> = savedStateHandle.getLiveData(ENTRY_CURRENCY_ID_KEY, null)

    fun setCurrencyId(id: String?) {
        currencyId.value = id
    }

    private var _currencyCode: LiveData<String>? = null

    fun currencyCode(): LiveData<String> = _currencyCode ?: currencyId.distinctUntilChanged().switchMap { code ->
        liveData(viewModelScope.coroutineContext) {
            if (code != null) emit(code) else emit("")
        }
    }.also {
        _currencyCode = it
    }

    val frequency = savedStateHandle.getLiveData<Frequency>(ENTRY_FREQUENCY_ID_KEY, null)

    fun setFrequency(frequency: Frequency) {
        savedStateHandle[ENTRY_FREQUENCY_ID_KEY] = frequency
    }

    fun save(id: String, leftAmount: Double, startingAmount: Double, currencyCode: String, name: String, description: String) {
        /*addData(*/
            val m = Movement(
                id,
                leftAmount = leftAmount.forDatabaseAmount(),
                startingAmount = startingAmount.forDatabaseAmount(),
                currencyCode = currencyCode,
                name = name,
                description = if (description.isEmpty()) null else description,
                frequency = frequency.value,
                accountId = accountId.value,
                categoryId = categoryId.value,
                budgetId = budgetId.value
            )/*,
            id
        )*/
        Log.d(TAG, "save: $m")
    }

    fun clearAllSelectedData() {
        accountId.value = null
        savedStateHandle[ENTRY_ACCOUNT_ID_KEY] = null
        savedStateHandle[ENTRY_BUDGET_ID_KEY] = null
        savedStateHandle[ENTRY_CATEGORY_ID_KEY] = null
        savedStateHandle[ENTRY_CURRENCY_ID_KEY] = null
        savedStateHandle[ENTRY_FREQUENCY_ID_KEY] = null
        savedStateHandle[ENTRY_MOVEMENT_ID_KEY] = null
    }
}