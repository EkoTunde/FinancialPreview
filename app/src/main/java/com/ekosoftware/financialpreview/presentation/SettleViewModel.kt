package com.ekosoftware.financialpreview.presentation

import android.text.Editable
import androidx.lifecycle.*
import com.ekosoftware.financialpreview.app.Constants
import com.ekosoftware.financialpreview.data.model.account.Account
import com.ekosoftware.financialpreview.data.model.movement.Movement
import com.ekosoftware.financialpreview.data.model.movement.MovementUI
import com.ekosoftware.financialpreview.data.model.record.Record
import com.ekosoftware.financialpreview.repository.EntryRepository
import com.ekosoftware.financialpreview.repository.SettleRepository
import com.ekosoftware.financialpreview.util.currentYearMonth
import com.ekosoftware.financialpreview.util.forDatabaseAmount
import com.ekosoftware.financialpreview.util.newId
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class SettleViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val settleRepository: SettleRepository,
    private val entryRepository: EntryRepository
) : ViewModel() {

    companion object {
        private const val SETTLE_MOVEMENT_ID_KEY = "settle movement id key"
        private const val SETTLE_ACCOUNT_ID_KEY = "settle account id key"
        private const val SETTLE_CURRENCY_CODE_KEY = "settle currency code key"
        private const val SETTLE_SETTLE_AMOUNT_KEY = "settle left amount key"
    }

    private var movement: LiveData<MovementUI>? = null

    fun getMovementUI(movementId: String): LiveData<MovementUI> {
        savedStateHandle[SETTLE_MOVEMENT_ID_KEY] = movementId
        return movement ?: liveData(viewModelScope.coroutineContext + Dispatchers.IO) {
            emitSource(settleRepository.getMovementUI(movementId))
        }.also {
            movement = it
        }
    }

    private fun setMovementVars(movement: Movement) {
        savedStateHandle[SETTLE_ACCOUNT_ID_KEY] = movement.accountId
    }

    private val accountId: MutableLiveData<String?> = savedStateHandle.getLiveData<String?>(SETTLE_ACCOUNT_ID_KEY, null)

    fun getAccountId(): String? = savedStateHandle[SETTLE_ACCOUNT_ID_KEY]

    fun setAccountId(accountId: String) {
        savedStateHandle[SETTLE_ACCOUNT_ID_KEY] = accountId
    }

    private var account: LiveData<Account?>? = null

    fun getAccount(): LiveData<Account?> = account ?: accountId.switchMap { id ->
        liveData<Account?>(viewModelScope.coroutineContext + Dispatchers.IO) {
            if (id != null) {
                emitSource(settleRepository.getAccount(id).map { it })
            } else {
                emit(null)
            }
        }
    }.also { account = it }

    fun setCurrency(currencyCode: String) {
        savedStateHandle[SETTLE_CURRENCY_CODE_KEY] = currencyCode
    }

    fun getCurrencyCode(): String? {
        return savedStateHandle[SETTLE_CURRENCY_CODE_KEY]
    }

    private var currency: LiveData<String?>? = null

    fun getCurrency(): LiveData<String?> = currency ?: liveData<String?>(viewModelScope.coroutineContext + Dispatchers.Default) {
        emitSource(savedStateHandle.getLiveData(SETTLE_CURRENCY_CODE_KEY, null))
    }.also { currency = it }

    private var settleAmount: LiveData<Double?>? = null

    fun getSettleAmount(): LiveData<Double?> = settleAmount ?: liveData<Double?>(viewModelScope.coroutineContext + Dispatchers.Default) {
        emitSource(savedStateHandle.getLiveData(SETTLE_SETTLE_AMOUNT_KEY, null))
    }.also {
        settleAmount = it
    }

    fun setSettleAmount(settleAmount: Double) {
        savedStateHandle[SETTLE_SETTLE_AMOUNT_KEY] = settleAmount
    }

    fun settleMovement(name: CharSequence, amount: Editable) = viewModelScope.launch(Dispatchers.IO) {
        val movement = settleRepository.getMovement(savedStateHandle[SETTLE_MOVEMENT_ID_KEY]!!)
        val record = Record(
            newId(),
            Date(),
            name.toString(),
            movement,
            amount.toString().toDouble().forDatabaseAmount(),
            savedStateHandle[SETTLE_CURRENCY_CODE_KEY]!!,
            savedStateHandle[SETTLE_ACCOUNT_ID_KEY]!!,
            null,
            null,
            movement.categoryId,
            null,
            null,
            movement.description,
            null
        )
        settleRepository.settleMovement(record, currentYearMonth())
    }

    fun clearData() {
        savedStateHandle[SETTLE_MOVEMENT_ID_KEY] = null
        savedStateHandle[SETTLE_ACCOUNT_ID_KEY] = null
        savedStateHandle[SETTLE_CURRENCY_CODE_KEY] = null
        savedStateHandle[SETTLE_SETTLE_AMOUNT_KEY] = null
        movement = null
        account = null
        currency = null
        settleAmount = null
    }
}