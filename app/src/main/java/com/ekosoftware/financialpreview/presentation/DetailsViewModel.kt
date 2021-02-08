package com.ekosoftware.financialpreview.presentation

import android.util.Log
import androidx.lifecycle.*
import com.ekosoftware.financialpreview.core.Resource
import com.ekosoftware.financialpreview.data.model.budget.Budget
import com.ekosoftware.financialpreview.data.model.movement.MovementUI
import com.ekosoftware.financialpreview.data.model.settle.SettleGroup
import com.ekosoftware.financialpreview.data.model.settle.SettleGroupWithMovements
import com.ekosoftware.financialpreview.domain.local.DetailsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailsViewModel @Inject constructor(
    private val detailsRepository: DetailsRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    companion object {
        private const val MOVEMENT_ID_KEY = "movementIdKey"
        private const val BUDGET_ID_KEY = "budgetIdKey"
        private const val SETTLE_GROUP_ID_KEY = "settleGroupIdKey"
        private const val RECORD_ID_KEY = "recordIdKey"
    }

    val movementId: MutableLiveData<String> = savedStateHandle.getLiveData(MOVEMENT_ID_KEY)

    fun setMovementId(id: String) {
        movementId.value = id
    }

    val movementUI = movementId.distinctUntilChanged().switchMap { id ->
        liveData<Resource<MovementUI>>(viewModelScope.coroutineContext + Dispatchers.IO) {
            emit(Resource.Loading())
            try {
                emitSource(detailsRepository.getMovement(id).map { Resource.Success(it) })
            } catch (e: Exception) {
                emit(Resource.Failure(e))
            }
        }
    }

    private var budget: LiveData<Resource<Budget>>? = null

    fun getBudget(id: String): LiveData<Resource<Budget>> = budget ?: liveData<Resource<Budget>>(viewModelScope.coroutineContext + Dispatchers.IO) {
        emit(Resource.Loading())
        try {
            emitSource(detailsRepository.getBudget(id).map { Resource.Success(it) })
        } catch (e: Exception) {
            emit(Resource.Failure(e))
        }
    }.also {
        budget = it
    }

    private var settleGroupWithMovements: LiveData<Resource<SettleGroup>>? = null

    fun getSettleGroup(id: String): LiveData<Resource<SettleGroup>> =
        settleGroupWithMovements ?: liveData<Resource<SettleGroup>> {
            emit(Resource.Loading())
            try {
                emitSource(detailsRepository.getSingleSettleGroup(id).map { Resource.Success(it) })
            } catch (e: Exception) {
                emit(Resource.Failure(e))
            }
        }.also {
            settleGroupWithMovements = it
        }

    private var movementsInSettleGroup: LiveData<Resource<List<SimpleDisplayableData>>>? = null

    fun getMovementsInSettleGroup(settleGroupId: String): LiveData<Resource<List<SimpleDisplayableData>>> =
        movementsInSettleGroup ?: liveData<Resource<List<SimpleDisplayableData>>> {
            emit(Resource.Loading())
            try {
                emitSource(
                    detailsRepository.getSimpleMovementsForSettleGroupId(settleGroupId).map {
                        Resource.Success(it.map { data ->
                            SimpleDisplayableData(
                                data.id,
                                data.name,
                                data.description,
                                data.color,
                                data.iconResId
                            )
                        })
                    })
            } catch (e: Exception) {
                emit(Resource.Failure(e))
            }
        }.also {
            movementsInSettleGroup = it
        }

    //val settleGroupId: MutableLiveData<String> = savedStateHandle.getLiveData(SETTLE_GROUP_ID_KEY)

    /*fun setSettleGroupId(id: String) {
        settleGroupId.value = id
    }*/

    /*val settleGroupWithMovements = movementId.distinctUntilChanged().switchMap { id ->
        liveData<Resource<MovementUI>>(viewModelScope.coroutineContext + Dispatchers.IO) {
            emit(Resource.Loading())
            try {
                emitSource(detailsRepository.getMovement(id).map { Resource.Success(it) })
            } catch (e: Exception) {
                emit(Resource.Failure(e))
            }
        }
    }*/

    fun setRecordId(id: String) {
        recordId.value = id
    }

    val recordId: MutableLiveData<String> = savedStateHandle.getLiveData(RECORD_ID_KEY)

    val recordUI = movementId.distinctUntilChanged().switchMap { id ->
        liveData<Resource<MovementUI>>(viewModelScope.coroutineContext + Dispatchers.IO) {
            emit(Resource.Loading())
            try {
                emitSource(detailsRepository.getMovement(id).map { Resource.Success(it) })
            } catch (e: Exception) {
                emit(Resource.Failure(e))
            }
        }
    }

    fun addSettleGroupAndMovementRef(settleGroupId: String, movementId: String) {
        viewModelScope.launch { detailsRepository.addSettleGroupMovementCrossRef(settleGroupId, movementId) }
    }

    fun clearData() {
        budget = null
        settleGroupWithMovements = null
        movementsInSettleGroup = null
    }

}