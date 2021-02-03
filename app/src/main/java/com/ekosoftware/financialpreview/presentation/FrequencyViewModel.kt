package com.ekosoftware.financialpreview.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.ekosoftware.financialpreview.data.model.movement.Frequency
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FrequencyViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {



    private var _frequency: LiveData<Frequency>? = null

    fun setFrequency(frequency: Frequency) {

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
