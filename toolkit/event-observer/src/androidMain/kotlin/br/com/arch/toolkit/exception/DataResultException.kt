package br.com.arch.toolkit.exception

data class DataResultException(override val message: String, val error: Throwable) :
    Exception(message, error)
