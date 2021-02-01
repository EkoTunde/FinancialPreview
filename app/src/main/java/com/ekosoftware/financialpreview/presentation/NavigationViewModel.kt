package com.ekosoftware.financialpreview.presentation

import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.assisted.Assisted
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class NavigationViewModel @Inject constructor( private val savedStateHandle: SavedStateHandle) : ViewModel() {

    companion object {
        private const val OPTION_KEY = "optionKey"
        const val OPTION_ADD_MOVEMENT = 0
        const val OPTION_ADD_BUDGET = 1
        const val OPTION_ADD_GROUP = 2
        const val OPTION_ADD_RECORD = 3
        const val OPTION_ADD_ACCOUNT = 4
    }

    val startingTransitionView = MutableLiveData<View>()

    fun setStartingTransitionView(v: View) {
        startingTransitionView.value = v
    }

    val currentOptionForCompose = savedStateHandle.getLiveData(OPTION_KEY, 0)

    fun setOptionForCompose(option: Int) {
        currentOptionForCompose.value = option
    }

}