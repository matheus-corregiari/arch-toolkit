package br.com.arch.toolkit.sample.shared.ui.home

/** Accept only the app scheme or internal absolute paths; registries resolve destinations. */
internal fun showcaseLinkPath(value: String): String? {
    val path = when {
        value.startsWith("archtoolkit://") -> "/" + value.removePrefix("archtoolkit://")
        value.startsWith("/") -> value
        else -> return null
    }
    return path.takeIf { it.length <= MAX_LINK_LENGTH && !it.contains("..") && !it.contains('#') }
}

private const val MAX_LINK_LENGTH = 512
