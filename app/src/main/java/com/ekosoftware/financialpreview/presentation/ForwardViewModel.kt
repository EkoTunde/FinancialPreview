package com.ekosoftware.financialpreview.presentation

import android.content.Context
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.ekosoftware.financialpreview.core.Resource
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import java.util.*

class ForwardViewModel @ViewModelInject constructor(
    @ApplicationContext private val appContext: Context,
    @Assisted private val savedStateHandle: SavedStateHandle
) : ViewModel() {


}