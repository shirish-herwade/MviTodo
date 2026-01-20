package com.mvi.todo.model.repository

import com.mvi.todo.model.local.Todo
import kotlinx.coroutines.flow.Flow

interface TodoRepository {
    suspend fun insert(todo: Todo)

    suspend fun delete(todo: Todo)

    suspend fun update(todo: Todo)

     fun getAllTodoListFlow(): Flow<List<Todo>>

//     fun getAllTodo(): List<Todo>
}