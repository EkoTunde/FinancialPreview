package com.ekosoftware.financialpreview.core

import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlin.coroutines.CoroutineContext


abstract class BaseViewModel : ViewModel() {

    /**
     * Returns a [LiveData] instance for the code block passed as a parameter,
     * specifying [viewModelScope]'s [CoroutineContext] and [Dispatchers.IO] as [CoroutineContext] for it.
     *
     * @param block lambda without parameters that returns [LiveData]<[T]>.
     */
    fun <T : Any> getIOLiveData(block: () -> LiveData<T>): LiveData<T> {
        return liveData(viewModelScope.coroutineContext + Dispatchers.IO) {
            emitSource(block())
        }
    }

    /**
     * Returns a [LiveData] lambda code block passed as a parameter,
     * mapped from the input `this` `LiveData` by applying [switchMap] to it .
     *
     * @param block labmda with [LiveData]'s [T] as a parameter, that returns new [LiveData] wrapping [R].
     *
     */
    fun <T : Any, R : Any> LiveData<T>.switchToIOLiveData(block: (T) -> LiveData<R>): LiveData<R> {
        return getIOLiveData { this.switchMap { block(it) } }
    }

    /**
     * Returns a [LiveData] result of mapping the result of applying [getIOLiveData] to lambda code block
     * passed as a parameter, from [LiveData]<[T]> to [LiveData]<[Resource]<[T]>>
     */
    fun <T : Any> resourceIOLiveData(block: () -> LiveData<T>): LiveData<Resource<T>> {
        return liveData<Resource<T>>(viewModelScope.coroutineContext + Dispatchers.IO) {
            emit(Resource.Loading())
            try {
                emitSource(getIOLiveData(block).map {
                    Resource.Success(it)
                })
            } catch (e: java.lang.Exception) {
                emit(Resource.Failure(e))
            }
        }
    }

    /**
     * TODO("Finish doc")
     */
    fun <T : Any, R : Any> LiveData<T>.switchToResourceIOLiveData(block: (T) -> LiveData<R>): LiveData<Resource<R>> {
        return this@switchToResourceIOLiveData.switchMap { resourceIOLiveData { block(it) } }
    }

    /**
     * TODO("Finish doc")
     */
    fun <T> LiveData<T>.toResourceLiveData(): LiveData<Resource<T>> {
        return this.switchMap { data ->
            liveData<Resource<T>>(viewModelScope.coroutineContext + Dispatchers.Default) {
                emit(Resource.Loading())
                try {
                    emit(Resource.Success(data))
                } catch (e: Exception) {
                    emit(Resource.Failure(e))
                }
            }
        }
    }


    /**
     * TODO("Finish doc")
     */
    fun <T, R> LiveData<T>.mapToLiveData(block: (T) -> R): LiveData<R> {
        return this.switchMap {
            liveData<R>(viewModelScope.coroutineContext + Dispatchers.Default) {
                emit(block(it))
            }
        }
    }


    fun <X, Y> LiveData<X>.pruebaFinal(block: (X) -> Y) =
        this.switchMap { x ->
            liveData(viewModelScope.coroutineContext + Dispatchers.IO) {
                emitSource((block(x) as LiveData<*>).map {
                    Resource.Success(it)
                })
            }
        }

    fun <T> LiveData<T>.resourceLiveData() =
        liveData<Resource<T>>(viewModelScope.coroutineContext + Dispatchers.Default) {
            emit(Resource.Loading())
            try {
                emitSource(this@resourceLiveData.map { Resource.Success(it) })
            } catch (e: Exception) {
                emit(Resource.Failure(e))
            }
        }

    fun <T, R> LiveData<T>.resourcefy(block: (T) -> LiveData<R>) =
        this.switchMap {
            liveData<Resource<R>>(viewModelScope.coroutineContext + Dispatchers.Default) {
                emit(Resource.Loading())
                try {
                    emitSource(block(it).map { Resource.Success(it) })
                } catch (e: Exception) {
                    emit(Resource.Failure(e))
                }
            }
        }

    // OPCIONES
    /*

    a) switch + liveData
    b) switch + liveData + Resource
    c) liveData + Resource
    d) liveData



     */

    fun <T, R> LiveData<T>.switchMasLiveData(block: (T) -> LiveData<R>): LiveData<R> {
        return this.switchMap {
            liveData {
                emitSource(block(it))
            }
        }
    }

    fun <T, R> LiveData<T>.switchLiveDataResource(block: (T) -> LiveData<R>): LiveData<Resource<R>> {
        return this.switchMap { t ->
            liveData<Resource<R>> {
                emit(Resource.Loading())
                try {
                    emitSource(block(t).map { r ->
                        Resource.Success(r)
                    })
                } catch (e: Exception) {
                    emit(Resource.Failure(e))
                }
            }
        }
    }

    fun <T> ioResourceLiveDataFromLiveData(block: () -> LiveData<T>): LiveData<Resource<T>> {
        return liveData<Resource<T>>(contextIO) {
            emit(Resource.Loading())
            try {
                emitSource(block().map { Resource.Success(it) })
            } catch (e: Exception) {
                emit(Resource.Failure(e))
            }
        }
    }

    fun <T> ioResourceLiveData(block: () -> T): LiveData<Resource<T>> {
        return liveData<Resource<T>>(contextIO) {
            emit(Resource.Loading())
            try {
                emit(Resource.Success(block()))
            } catch (e: Exception) {
                emit(Resource.Failure(e))
            }
        }
    }

    fun <T> T.copyWithResource() : LiveData<Resource<T>> {
        return liveData<Resource<T>>(contextDefault) {
            emit(Resource.Loading())
            try {
                emit(Resource.Success(this@copyWithResource))
            } catch (e: Exception) {
                emit(Resource.Failure(e))
            }
        }
    }

    fun <T> LiveData<T>.copyWithResource(): LiveData<Resource<T>> {
        return liveData<Resource<T>>(contextDefault) {
            emit(Resource.Loading())
            try {
                emitSource(this@copyWithResource.map { Resource.Success(it) })
            } catch (e: Exception) {
                emit(Resource.Failure(e))
            }
        }
    }

    val contextDefault by lazy { viewModelScope.coroutineContext + Dispatchers.Default }

    val contextIO by lazy { viewModelScope.coroutineContext + Dispatchers.IO }

    fun <T> liveDataIO(block: () -> T): LiveData<T> {
        return liveData(contextIO) {
            emit(block())
        }
    }
}