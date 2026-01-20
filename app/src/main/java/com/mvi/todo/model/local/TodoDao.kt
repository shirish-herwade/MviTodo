package com.mvi.todo.model.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TodoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(toDo: Todo)

    @Update
    fun update(toDo: Todo): Int

    @Delete
    fun delete(toDo: Todo): Int

    @Query("SELECT * FROM todo_table")
    fun getAlTodoList(): Flow<List<Todo>>

    @Query("SELECT * FROM todo_table")
    fun getAlTodo(): List<Todo>
}