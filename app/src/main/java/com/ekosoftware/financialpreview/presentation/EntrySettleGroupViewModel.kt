package com.ekosoftware.financialpreview.presentation

import android.util.Log
import androidx.lifecycle.*
import com.ekosoftware.financialpreview.R
import com.ekosoftware.financialpreview.app.Constants
import com.ekosoftware.financialpreview.core.BaseEntryViewModel
import com.ekosoftware.financialpreview.data.model.settle.SettleGroup
import com.ekosoftware.financialpreview.data.model.settle.SettleGroupWithMovements
import com.ekosoftware.financialpreview.repository.EntryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EntrySettleGroupViewModel @Inject constructor(
    private val entryRepository: EntryRepository,
    private val savedStateHandle: SavedStateHandle
) : BaseEntryViewModel(entryRepository) {

    companion object {
        private const val WAS_SETTLE_GROUP_RETRIEVED_KEY = "was_settle_group_retrieved_ok"
        private const val SAVED_SETTLE_GROUP_KEY = "saved settle group key"
        private const val SAVED_NAME_KEY = "Saved Name Key"
        private const val SAVED_DESCRIPTION_KEY = "Saved Description Key"
        private const val SAVED_PERCENTAGE_KEY = "Saved Percentage Key"
        private const val SAVED_COLOR_RES_ID_KEY = "color res id key"
    }

    fun budgetRetrievedOk() {
        savedStateHandle[WAS_SETTLE_GROUP_RETRIEVED_KEY] = true
    }

    private val wasBudgetRetrieved = savedStateHandle.getLiveData(WAS_SETTLE_GROUP_RETRIEVED_KEY, false)

    private var _settleGroup: LiveData<SettleGroupWithMovements>? = null

    fun get(id: String): LiveData<SettleGroupWithMovements> = _settleGroup ?: wasBudgetRetrieved.switchMap { wasRetrieved ->
        liveData<SettleGroupWithMovements>(viewModelScope.coroutineContext + Dispatchers.IO) {
            if (!wasRetrieved) emitSource(entryRepository.getSingleSettleGroupWithMovements(id))
        }.also {
            _settleGroup = it
        }
    }

    fun setName(newName: String) {
        savedStateHandle[SAVED_NAME_KEY] = newName
        /*val old: SettleGroup? = savedStateHandle[SAVED_SETTLE_GROUP_KEY]
        savedStateHandle[SAVED_SETTLE_GROUP_KEY] = old?.apply {
            this.name = newName
        } ?: SettleGroup()*/
    }

    fun setDescription(newDescription: String) {
        savedStateHandle[SAVED_DESCRIPTION_KEY] = newDescription
        /*val old: SettleGroup? = savedStateHandle[SAVED_SETTLE_GROUP_KEY]
            savedStateHandle[SAVED_SETTLE_GROUP_KEY] = old?.apply {
                this.description = newDescription
            }*/
    }

    fun setPercentage(newPercentage: Double) {
        savedStateHandle[SAVED_PERCENTAGE_KEY] = newPercentage
        /*val old: SettleGroup? = savedStateHandle[SAVED_SETTLE_GROUP_KEY]
            savedStateHandle[SAVED_SETTLE_GROUP_KEY] = old?.apply {
                this.percentage = newPercentage
            }*/
    }

    private var _colorResId: LiveData<Int?>? = null

    fun setColorResId(colorResId: Int) {
        Log.d("c}", "setColorResId: c}$colorResId")
        savedStateHandle[SAVED_COLOR_RES_ID_KEY] = colorResId
    }

    fun getColorResId(): LiveData<Int?> = _colorResId ?: liveData<Int?> {
        emitSource(savedStateHandle.getLiveData(SAVED_COLOR_RES_ID_KEY, null))
    }.also { _colorResId = it }

    fun setSettleGroupAttrs(settleGroup: SettleGroup) {
        setName(settleGroup.name)
        setDescription(settleGroup.description)
        setPercentage(settleGroup.percentage)
        setColorResId(settleGroup.colorResId)
    }

    fun isRequiredInfoFilled(): Boolean {
        val name: String? = savedStateHandle[SAVED_NAME_KEY]
        val description: String? = savedStateHandle[SAVED_DESCRIPTION_KEY]
        val percentage: Double? = savedStateHandle[SAVED_PERCENTAGE_KEY]
        val colorResId: Int? = savedStateHandle[SAVED_COLOR_RES_ID_KEY]
        return name != null && description != null && percentage != null && colorResId != null && name.isNotEmpty() && description.isNotEmpty()
    }

    fun save(id: String = Constants.nan) {
        addData(
            SettleGroup(
                id,
                savedStateHandle[SAVED_NAME_KEY] ?: "",
                savedStateHandle[SAVED_DESCRIPTION_KEY] ?: "",
                savedStateHandle[SAVED_PERCENTAGE_KEY] ?: 0.0,
                savedStateHandle[SAVED_COLOR_RES_ID_KEY] ?: R.color.colorPrimary
            ), id
        )
    }

    fun delete(id: String) {
        viewModelScope.launch { entryRepository.deleteSettleGroupWithId(id) }
    }

    fun deleteRef(settleGroupId: String, movementId: String) {
        viewModelScope.launch { entryRepository.deleteCrossRef(settleGroupId, movementId) }
    }

    fun clearAllSelectedData() {
        _settleGroup = null
        _colorResId = null
        savedStateHandle[SAVED_COLOR_RES_ID_KEY] = 0
    }
}