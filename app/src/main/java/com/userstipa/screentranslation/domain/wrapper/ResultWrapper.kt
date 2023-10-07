package com.userstipa.screentranslation.domain.wrapper

sealed class ResultWrapper<T : Any> {
    data class Success<T : Any>(val data: T) : ResultWrapper<T>()
    data class Error<T : Any>(val message: String) : ResultWrapper<T>()
}