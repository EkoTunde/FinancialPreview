package com.ekosoftware.financialpreview.presentation.ui.pending

import android.content.Context
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.ekosoftware.financialpreview.domain.local.HomeRepository
import dagger.hilt.android.qualifiers.ApplicationContext

class PendingViewModel @ViewModelInject constructor(
    @ApplicationContext private val appContext: Context,
    //private val homeRepository: HomeRepository,
    @Assisted private val savedStateHandle: SavedStateHandle
) : ViewModel() {



}