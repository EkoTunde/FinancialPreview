package com.ekosoftware.financialpreview.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ekosoftware.financialpreview.data.model.settle.SettleGroupMovementsCrossRef
import com.ekosoftware.financialpreview.domain.local.EntryRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

abstract class EntryViewModel(
    private val entryRepository: EntryRepository
) : ViewModel() {

    open fun <T : Any> addData(obj: T, id: String) = if (id == "NaN") update(obj) else insert(obj)

    fun addSettleGroupMovementRef(groupId: String, movementId: String) = CoroutineScope(viewModelScope.coroutineContext + Dispatchers.IO).launch {
        val ref = SettleGroupMovementsCrossRef(groupId, movementId)
        entryRepository.insertSettleGroupMovementRef(ref)
    }

    open fun <T : Any> insert(obj: T) = CoroutineScope(viewModelScope.coroutineContext + Dispatchers.IO).launch { entryRepository.insert(obj) }
    open fun <T : Any> update(obj: T) = CoroutineScope(viewModelScope.coroutineContext + Dispatchers.IO).launch { entryRepository.update(obj) }
    open fun <T : Any> delete(obj: T) = CoroutineScope(viewModelScope.coroutineContext + Dispatchers.IO).launch { entryRepository.delete(obj) }
}