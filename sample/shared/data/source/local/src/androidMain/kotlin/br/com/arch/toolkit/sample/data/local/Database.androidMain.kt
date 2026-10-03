package br.com.arch.toolkit.sample.data.local

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase

fun databaseBuilder(context: Context): RoomDatabase.Builder<ShowcaseDatabase> =
    Room.databaseBuilder<ShowcaseDatabase>(
        context = context,
        name = "showcase.db"
    )
