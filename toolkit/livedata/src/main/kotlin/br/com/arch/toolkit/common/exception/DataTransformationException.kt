package br.com.arch.toolkit.common.exception

data class DataTransformationException(override val message: String, val error: Throwable) :
    Exception(message, error)