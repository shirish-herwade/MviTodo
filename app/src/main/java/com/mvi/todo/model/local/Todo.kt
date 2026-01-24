package com.mvi.todo.model.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "todo_table")
data class Todo(
    val title: String,
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val isSelected: Boolean = false,
) {
}