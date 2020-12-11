package com.ekosoftware.financialpreview.presentation

import android.content.Context
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.qualifiers.ApplicationContext

class SearchViewModel @ViewModelInject constructor(
    @ApplicationContext private val appContext: Context,
    @Assisted private val savedStateHandle: SavedStateHandle
) : ViewModel() {
}