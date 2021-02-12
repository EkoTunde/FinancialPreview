package com.ekosoftware.financialpreview.core

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ekosoftware.financialpreview.app.Constants
import com.ekosoftware.financialpreview.data.model.settle.SettleGroupMovementsCrossRef
import com.ekosoftware.financialpreview.repository.EntryRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

abstract class BaseEntryViewModel(
    private val entryRepository: EntryRepository
) : ViewModel() {

    fun <T : Any> addData(obj: T, id: String) = if (id == Constants.nan) insert(obj) else update(obj)

    fun addSettleGroupMovementRef(groupId: String, movementId: String) = CoroutineScope(viewModelScope.coroutineContext + Dispatchers.IO).launch {
        val ref = SettleGroupMovementsCrossRef(groupId, movementId)
        entryRepository.insertSettleGroupMovementRef(ref)
    }

    open fun <T : Any> insert(obj: T) = CoroutineScope(viewModelScope.coroutineContext + Dispatchers.IO).launch { entryRepository.insert(obj) }
    open fun <T : Any> update(obj: T) = CoroutineScope(viewModelScope.coroutineContext + Dispatchers.IO).launch { entryRepository.update(obj) }
    open fun <T : Any> delete(obj: T) = CoroutineScope(viewModelScope.coroutineContext + Dispatchers.IO).launch { entryRepository.delete(obj) }
}