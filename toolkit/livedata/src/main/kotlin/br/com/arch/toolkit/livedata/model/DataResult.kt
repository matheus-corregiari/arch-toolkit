package br.com.arch.toolkit.livedata.model

import br.com.arch.toolkit.livedata.model.enum.DataResultStatus

data class DataResult<out T>(
    val data: T?,
    var error: Throwable?,
    val status: DataResultStatus
)