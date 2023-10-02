package br.com.arch.toolkit.sample.github.data.remote.moshi

import br.com.arch.toolkit.sample.github.data.remote.model.PageDTO
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import java.lang.reflect.Type

class PageAdapterFactory : JsonAdapter.Factory {
    override fun create(
        type: Type,
        annotations: MutableSet<out Annotation>,
        moshi: Moshi
    ): JsonAdapter<*>? {
        if (
            annotations.isNotEmpty() ||  // Annotations? This factory doesn't apply.
            type !== PageDTO::class.java // Not a QuestionResponse This factory doesn't apply.
        ) {
            return null
        }

        // Handle Type erasure at runtime, this class does not need adapter with single level of generic though
        val parameterizedType = Types.newParameterizedType(type, Any::class.java)
        val elementAdapter: JsonAdapter<Any> = moshi.adapter(parameterizedType)

        return object : JsonAdapter<Any>() {
            override fun fromJson(reader: JsonReader) = elementAdapter.fromJson(reader)
            override fun toJson(writer: JsonWriter, value: Any?) =
                elementAdapter.toJson(writer, value)
        }.nullSafe()
    }
}