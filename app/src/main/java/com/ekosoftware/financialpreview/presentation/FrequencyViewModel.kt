package com.ekosoftware.financialpreview.presentation

import android.util.Log
import androidx.lifecycle.*
import com.ekosoftware.financialpreview.app.Strings
import com.ekosoftware.financialpreview.data.model.movement.Frequency
import com.ekosoftware.financialpreview.util.plusMonths
import com.ekosoftware.financialpreview.R
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FrequencyViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    enum class FrequencyType { REPEAT_FREQUENCY, NON_REPEAT_FREQUENCY, INSTALLMENTS_BASED_FREQUENCY }

    companion object {
        const val REPEAT_FREQUENCY_TYPE = 1
        const val NON_REPEAT_FREQUENCY_TYPE = 2
        const val INSTALLMENTS_BASED_FREQUENCY_TYPE = 3

        private const val SELECTED_FREQUENCY_TYPE_KEY = "Selected Frequency Type Key"

        private const val TAG = "FrequencyViewModel"
    }

    private val inputFrequency: MutableLiveData<Frequency?> = savedStateHandle.getLiveData<Frequency?>(SELECTED_FREQUENCY_TYPE_KEY, null)

    fun setInput(type: FrequencyType, _when: String, from: String, to: String, _installments: String, months: String? = null) {
        val installments = if (_installments.isNotEmpty()) _installments.toInt() else null
        Log.d(TAG, "setInput2: $to")
        val frequency = when (type) {
            FrequencyType.NON_REPEAT_FREQUENCY -> Frequency(_when.currentTextToInt(), _when.currentTextToInt(), null)
            FrequencyType.REPEAT_FREQUENCY -> Frequency(
                from.currentTextToInt(),
                if (to.isEmpty() || to == Strings.get(R.string.to)) 999999 else to.currentTextToInt(),
                null,
                months!!
            )
            FrequencyType.INSTALLMENTS_BASED_FREQUENCY -> Frequency(
                from.currentTextToInt(),
                from.currentTextToInt()?.plusMonths((installments ?: 2) - 1) ?: 99999,
                installments ?: 2
            )
        }
        savedStateHandle[SELECTED_FREQUENCY_TYPE_KEY] = frequency
    }


    private var _frequency: LiveData<Frequency?>? = null

    fun getFrequency(): LiveData<Frequency?> = _frequency ?: liveData { emitSource(inputFrequency) }.also { _frequency = it }

    private fun String.currentTextToInt(): Int? {
        return if ("/" in this) replace("/", "").toInt() else null
    }

}

/*
fun Frequency?.forDisplay(): String {
    this?.let {
        Log.d(TAG, "forDisplay: from>${it.from} to>${it.to} installments>${it.installments}")
        return when {
            it.from == it.to -> "${it.from!!.monthNameKey()}-${it.from!!.getYear()}"
            to == 999999 -> Strings.get(
                R.string.repeats_placeholder_from,
                it.from!!.monthNameKey(),
                it.from!!.getYear(),
                if (it.monthsChecked == Strings.get(R.string.all_months_checked_frequency)) {
                    Strings.get(R.string.every_month)
                } else Strings.get(R.string.some_months)
            )
            it.installments != null || it.installments!! > 0 -> Strings.get(R.string.installments_frequency_placeholder, it.installments!!)
            else -> Strings.get(
                R.string.repeats_placeholder_to,
                it.from!!.monthNameKey(),
                it.from!!.getYear(),
                it.to!!.monthNameKey(),
                it.to!!.getYear(),
                if (this.monthsChecked == Strings.get(R.string.all_months_checked_frequency)) {
                    Strings.get(R.string.every_month)
                } else Strings.get(R.string.some_months)
            )
        }
    }
    return ""
}*/
