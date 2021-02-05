package com.ekosoftware.financialpreview.presentation

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CalculatorViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    companion object {
        private const val TAG = "CalculatorViewModel"
        private const val SAVED_CALCULATOR_AMOUNT_KEY = "Saved Number Key"
    }

    fun saveAmount(amount: Double?) {
        savedStateHandle[SAVED_CALCULATOR_AMOUNT_KEY] = amount
    }

    private var _amount: LiveData<Double?>? = null

    fun getAmount(): LiveData<Double?> = _amount ?: liveData {
        emitSource(savedStateHandle.getLiveData<Double?>(SAVED_CALCULATOR_AMOUNT_KEY, null))
    }.also {
        _amount = it
    }
}