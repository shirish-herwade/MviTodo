package com.mvi.todo.state

import com.mvi.todo.intent.TodoIntent
import com.mvi.todo.model.local.Todo

data class TodoState(
    val items: List<Todo> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val draftTitle: String = "",
    val showDelete: Boolean = false,
    val deleteSelection: TodoIntent.DeleteSelection? = null,
    val selectedTodo: Todo? = null
)