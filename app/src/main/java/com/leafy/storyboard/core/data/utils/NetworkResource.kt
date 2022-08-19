package com.leafy.storyboard.core.data.utils

import com.leafy.storyboard.core.data.remote.network.ApiResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow

abstract class NetworkResource<RequestType, ResultType> {
    private val result: Flow<Status<ResultType>> = flow {
        emit(Status.Loading())

        when (val apiResponse = createCall().first()) {
            is ApiResponse.Success -> {
                val result = convertResponse(apiResponse.data)
                emit(Status.Success(result))
            }
            is ApiResponse.Empty -> emit(Status.Empty())
            is ApiResponse.Error -> emit(Status.Error(apiResponse.errorMessage))
        }
    }

    protected abstract fun convertResponse(data: RequestType): ResultType

    protected abstract suspend fun createCall(): Flow<ApiResponse<RequestType>>

    fun asFlow() = result
}