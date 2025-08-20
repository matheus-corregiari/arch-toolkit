package br.com.arch.toolkit.sample.github.shared.structure.core.extension

inline fun <reified T> Collection<*>.firstInstanceOrNull() = firstOrNull { it is T } as? T
inline fun <reified T> Collection<*>.firstInstance() = requireNotNull(firstInstanceOrNull<T>()) {
    error("No items found for type ${T::class.simpleName}")
}
