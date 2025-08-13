package br.com.arch.toolkit.lumber

@Suppress("unused")
internal fun jsLog(message: String): Unit = js(
    code = """{
        console.log(message);
    }"""
)

@Suppress("unused")
internal fun jsLogInfo(message: String): Unit = js(
    code = """{
        console.info(message);
    }"""
)

@Suppress("unused")
internal fun jsLogWarn(message: String): Unit = js(
    code = """{
        console.warn(message);
    }"""
)

@Suppress("unused")
internal fun jsLogError(message: String): Unit = js(
    code = """{
        console.error(message);
    }"""
)
