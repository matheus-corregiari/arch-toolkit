package br.com.arch.toolkit.sample.github.shared.structure.core.model

enum class AppLanguage(val tag: String) {
    ENGLISH("en"), PORTUGUESE_BRAZIL("pt-BR");

    companion object {
        fun fromTag(tag: String): AppLanguage = if (tag.lowercase().startsWith("pt")) PORTUGUESE_BRAZIL else ENGLISH
    }
}
