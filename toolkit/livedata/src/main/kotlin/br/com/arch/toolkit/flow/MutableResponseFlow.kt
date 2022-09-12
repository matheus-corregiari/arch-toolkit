package br.com.arch.toolkit.flow

import br.com.arch.toolkit.common.DataResult
import br.com.arch.toolkit.common.DataResultStatus

class MutableResponseFlow<T> : ResponseFlow<T> {

    /**
     * Empty constructor when initializing with a value is not needed
     *
     * @return An empty ResponseFlow<T> instance
     */
    constructor() : this(DataResult(null, null, DataResultStatus.LOADING))

    /**
     * Constructor for initializing with a value
     *
     * @param value The initial value for this MutableResponseLiveData
     *
     * @return An instance of ResponseFlow<T> with a default value set
     */
    constructor(value: DataResult<T>) : super(value)

    override var value: DataResult<T>
        get() = super.value
        public set(value) {
            super.value = value
        }
}