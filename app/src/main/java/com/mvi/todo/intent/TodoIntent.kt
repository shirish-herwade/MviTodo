package com.mvi.todo.intent

import com.mvi.todo.model.local.Todo

sealed class TodoIntent {
    data class Insert(val todo: Todo) : TodoIntent()

    data class Update(val todo: Todo) : TodoIntent()

    data class Delete(val todo: Todo) : TodoIntent()

    object SubmitTodo : TodoIntent()

    data class ChangeDraftTitle(val title: String) : TodoIntent()

    data class ShowDeleteDialog(
        val todo: Todo? = null,
        val selection: DeleteSelection? = null
    ) : TodoIntent()

    object DismissDeleteDialog : TodoIntent()

    object ConfirmDelete : TodoIntent()

    object DeleteSelected : TodoIntent()

    enum class DeleteSelection {
        Single,
        Selected
    }

//    object NavigateToMap : TodoIntent()
}