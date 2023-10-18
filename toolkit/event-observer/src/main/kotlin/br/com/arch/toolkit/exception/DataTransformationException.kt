package br.com.arch.toolkit.exception

data class DataTransformationException(override val message: String, val error: Throwable) :
    Exception(message, error)