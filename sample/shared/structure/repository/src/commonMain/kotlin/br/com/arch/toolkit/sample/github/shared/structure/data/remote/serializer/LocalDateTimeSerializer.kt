package br.com.arch.toolkit.sample.github.shared.structure.data.remote.serializer

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
internal object LocalDateTimeSerializer : KSerializer<LocalDateTime> {

    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("kotlinx.datetime.LocalDateTime", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): LocalDateTime {
        val instant = Instant.parse(decoder.decodeString())
        return instant.toLocalDateTime(TimeZone.UTC)
    }

    override fun serialize(encoder: Encoder, value: LocalDateTime) {
        encoder.encodeString(value.toInstant(TimeZone.UTC).toString())
    }

}
