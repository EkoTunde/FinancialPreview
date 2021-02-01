package com.ekosoftware.financialpreview.presentation

import androidx.lifecycle.*
import com.ekosoftware.financialpreview.core.Resource
import com.ekosoftware.financialpreview.data.model.movement.MovementUI
import com.ekosoftware.financialpreview.domain.local.DetailsRepository
import dagger.assisted.Assisted
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

@HiltViewModel
class DetailsViewModel @Inject constructor(
    private val detailsRepository: DetailsRepository,
     private val savedStateHandle: SavedStateHandle
): ViewModel() {

    companion object {
        private const val MOVEMENT_ID_KEY = "movementIdKey"
        private const val BUDGET_ID_KEY = "budgetIdKey"
        private const val SETTLE_GROUP_ID_KEY = "settleGroupIdKey"
        private const val RECORD_ID_KEY = "recordIdKey"
    }

    val movementId: MutableLiveData<String> = savedStateHandle.getLiveData(MOVEMENT_ID_KEY)
    val budgetId: MutableLiveData<String> = savedStateHandle.getLiveData(BUDGET_ID_KEY)
    val settleGroupId: MutableLiveData<String> = savedStateHandle.getLiveData(SETTLE_GROUP_ID_KEY)
    val recordId: MutableLiveData<String> = savedStateHandle.getLiveData(RECORD_ID_KEY)

    fun setMovementId(id: String) {
        movementId.value = id
    }

    fun setBudgetId(id: String) {
        budgetId.value = id
    }

    fun setSettleGroupId(id: String) {
        settleGroupId.value = id
    }

    fun setRecordId(id: String) {
        recordId.value = id
    }

    val movementUI = movementId.distinctUntilChanged().switchMap { id ->
        liveData<Resource<MovementUI>>(viewModelScope.coroutineContext + Dispatchers.IO){
            emit(Resource.Loading())
            try {
                emitSource(detailsRepository.getMovement(id).map { Resource.Success(it) })
            } catch (e: Exception) {
                emit(Resource.Failure(e))
            }
        }
    }

    val budgetUI = movementId.distinctUntilChanged().switchMap { id ->
        liveData<Resource<MovementUI>>(viewModelScope.coroutineContext + Dispatchers.IO){
            emit(Resource.Loading())
            try {
                emitSource(detailsRepository.getMovement(id).map { Resource.Success(it) })
            } catch (e: Exception) {
                emit(Resource.Failure(e))
            }
        }
    }

    val settleGroupWithMovements = movementId.distinctUntilChanged().switchMap { id ->
        liveData<Resource<MovementUI>>(viewModelScope.coroutineContext + Dispatchers.IO){
            emit(Resource.Loading())
            try {
                emitSource(detailsRepository.getMovement(id).map { Resource.Success(it) })
            } catch (e: Exception) {
                emit(Resource.Failure(e))
            }
        }
    }

    val recordUI = movementId.distinctUntilChanged().switchMap { id ->
        liveData<Resource<MovementUI>>(viewModelScope.coroutineContext + Dispatchers.IO){
            emit(Resource.Loading())
            try {
                emitSource(detailsRepository.getMovement(id).map { Resource.Success(it) })
            } catch (e: Exception) {
                emit(Resource.Failure(e))
            }
        }
    }

}