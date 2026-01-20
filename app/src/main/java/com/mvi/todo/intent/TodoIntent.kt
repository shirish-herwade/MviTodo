package com.mvi.todo.intent

import com.mvi.todo.model.local.Todo

sealed class TodoIntent {
    data class Insert(val todo: Todo) : TodoIntent()
    data class Update(val todo: Todo) : TodoIntent()
    data class Delete(val todo: Todo) : TodoIntent()
    data class SaveTodo(val title: String) : TodoIntent()
}