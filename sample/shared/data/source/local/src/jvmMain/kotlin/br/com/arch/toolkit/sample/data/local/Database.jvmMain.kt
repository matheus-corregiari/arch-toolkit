package br.com.arch.toolkit.sample.data.local

import androidx.room.Room
import androidx.room.RoomDatabase
import br.com.arch.toolkit.sample.data.local.defaultKeyValuePath
import java.io.File

fun databaseBuilder(): RoomDatabase.Builder<ShowcaseDatabase> =
    Room.databaseBuilder<ShowcaseDatabase>(
        name = File(defaultKeyValuePath()).parentFile.resolve("showcase.db").absolutePath
    )
