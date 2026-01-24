package com.mvi.todo.model.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Todo::class], version = 2)
abstract class TodoDatabase : RoomDatabase() {
    companion object {
        fun getInstance(context: Context): TodoDatabase = Room.databaseBuilder(
                context,
                TodoDatabase::class.java,
                "todo.db"
            ).fallbackToDestructiveMigration(false)
            .build()
    }

    abstract fun todoDao(): TodoDao
}
