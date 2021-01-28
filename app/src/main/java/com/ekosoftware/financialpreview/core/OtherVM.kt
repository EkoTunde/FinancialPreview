package com.ekosoftware.financialpreview.core

import android.content.Context
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import com.ekosoftware.financialpreview.domain.local.MainRepository
import dagger.hilt.android.qualifiers.ApplicationContext

class OtherVM @ViewModelInject constructor(
    @ApplicationContext private val appContext: Context,
    private val mainRepository: MainRepository,
    @Assisted private val savedStateHandle: SavedStateHandle
) : BaseViewModel() {

    val pirates = MutableLiveData<String>()

    val ships = pirates.switchToIOLiveData {
        mainRepository.getMovementsSummaries(it, it, 22)
    }


}