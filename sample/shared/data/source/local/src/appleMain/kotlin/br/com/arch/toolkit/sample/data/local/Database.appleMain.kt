package br.com.arch.toolkit.sample.data.local

import androidx.room.Room
import androidx.room.RoomDatabase
import br.com.arch.toolkit.sample.github.shared.structure.data.local.defaultKeyValuePath

fun databaseBuilder(): RoomDatabase.Builder<ShowcaseDatabase> =
    Room.databaseBuilder<ShowcaseDatabase>(
        name = defaultKeyValuePath().substringBeforeLast("/") + "/showcase.db"
    )
