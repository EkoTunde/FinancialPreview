package com.ekosoftware.financialpreview.presentation

import android.util.Log
import androidx.lifecycle.*
import com.ekosoftware.financialpreview.app.Constants
import com.ekosoftware.financialpreview.data.model.movement.Frequency
import com.ekosoftware.financialpreview.data.model.movement.Movement
import com.ekosoftware.financialpreview.domain.local.EntryRepository
import com.ekosoftware.financialpreview.util.forDatabaseAmount
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

@HiltViewModel
class EntryMovementViewModel @Inject constructor(
    private val entryRepository: EntryRepository,
    private val savedStateHandle: SavedStateHandle
) : EntryViewModel(entryRepository) {

    companion object {
        private const val MOVEMENT_ID_KEY = "movementId"
        private const val ACCOUNT_ID_KEY = "accountIdForMovement"
        private const val CATEGORY_ID_KEY = "categoryIdForMovement"
        private const val CURRENCY_KEY = "currencyIdForMovement"
        private const val BUDGET_ID_KEY = "budgetIdForMovement"
        private const val FREQUENCY_ID_KEY = "frequencyForMovement"
    }

    private var movementWasRetrieved = false

    fun movementRetrievedOk() {
        movementWasRetrieved = true
    }

    val movementId: MutableLiveData<String> = savedStateHandle.getLiveData(MOVEMENT_ID_KEY, null)

    fun setMovementId(id: String) {
        movementId.value = id
    }

    val movement: LiveData<Movement?> = movementId.distinctUntilChanged().switchMap { id ->
        liveData<Movement?>(viewModelScope.coroutineContext + Dispatchers.IO) {
            if (movementWasRetrieved) emit(null) else entryRepository.getMovement(id)
        }
    }

    val currencyCode = savedStateHandle.getLiveData<String>(CURRENCY_KEY)

    private val accountId: MutableLiveData<String?> = savedStateHandle.getLiveData(ACCOUNT_ID_KEY, null)

    fun setAccountId(id: String?) {
        accountId.value = id
    }

    val accountName: LiveData<String> = accountId.distinctUntilChanged().switchMap { id ->
        liveData(viewModelScope.coroutineContext + Dispatchers.IO) {
            id?.let { emitSource(entryRepository.getAccountName(it)) }
        }
    }

    private val categoryId: MutableLiveData<String?> = savedStateHandle.getLiveData(CATEGORY_ID_KEY, null)

    fun setCategoryId(id: String?) {
        categoryId.value = id
    }

    val categoryName: LiveData<String> = categoryId.distinctUntilChanged().switchMap { id ->
        liveData(viewModelScope.coroutineContext + Dispatchers.IO) {
            id?.let { emitSource(entryRepository.getCategoryName(id)) }
        }
    }

    private val budgetId: MutableLiveData<String?> = savedStateHandle.getLiveData(BUDGET_ID_KEY, null)

    fun setBudgetId(id: String?) {
        budgetId.value = id
    }

    val budgetName: LiveData<String> = budgetId.distinctUntilChanged().switchMap { id ->
        liveData(viewModelScope.coroutineContext + Dispatchers.IO) {
            id?.let {
                emitSource(entryRepository.getBudgetName(id))
            }
        }
    }

    val frequency = savedStateHandle.getLiveData<Frequency>(FREQUENCY_ID_KEY)

    fun setFrequency(freq: Frequency) {
        this.frequency.value = freq
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
                frequency = frequency.value,
                accountId = accountId.value,
                categoryId = categoryId.value,
                budgetId = budgetId.value
            ),
            id
        )
    }
}