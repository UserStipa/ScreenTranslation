package com.userstipa.screentranslation.data.api

sealed class NetworkResponse<T> {
    data class Success<T>(var data: T) : NetworkResponse<T>()
    data class Error<T>(val code: Int, val message: String?) : NetworkResponse<T>()
    data class Exception<T>(val e: Throwable) : NetworkResponse<T>()
}