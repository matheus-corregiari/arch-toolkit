package br.com.arch.toolkit.lumber

/** Calls `console.log(message)`. */
@Suppress("unused", "UNUSED_PARAMETER")
internal fun jsLog(message: String): Unit = js(
    code = """{
        console.log(message);
    }"""
)

/** Calls `console.info(message)`. */
@Suppress("unused", "UNUSED_PARAMETER")
internal fun jsLogInfo(message: String): Unit = js(
    code = """{
        console.info(message);
    }"""
)

/** Calls `console.warn(message)`. */
@Suppress("unused", "UNUSED_PARAMETER")
internal fun jsLogWarn(message: String): Unit = js(
    code = """{
        console.warn(message);
    }"""
)

/** Calls `console.error(message)`. */
@Suppress("unused", "UNUSED_PARAMETER")
internal fun jsLogError(message: String): Unit = js(
    code = """{
        console.error(message);
    }"""
)
