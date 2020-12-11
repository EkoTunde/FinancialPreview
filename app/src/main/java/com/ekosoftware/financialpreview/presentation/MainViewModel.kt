package com.ekosoftware.financialpreview.presentation

import android.content.Context
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ekosoftware.financialpreview.R
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch

class MainViewModel @ViewModelInject constructor(
    @ApplicationContext private val appContext: Context,
    @Assisted private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    companion object {
        private const val ARG_FIRST_TIME_CONFIG = "is first time arg"
    }

    private fun sharedPref() = appContext.getSharedPreferences(
        appContext.getString(R.string.preference_file_key), Context.MODE_PRIVATE
    )

    fun launchFirstTimeConfig() = sharedPref().getBoolean(ARG_FIRST_TIME_CONFIG, true)

    fun setFirstTimeConfigDone() = with(sharedPref().edit()) {
        putBoolean(ARG_FIRST_TIME_CONFIG, false)
        apply()
    }
}