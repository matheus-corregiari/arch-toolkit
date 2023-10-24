package br.com.arch.toolkit.exception

data class DataResultTransformationException(override val message: String, val error: Throwable) :
    Exception(message, error)