package com.mvi.todo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mvi.todo.di.IoDispatcher
import com.mvi.todo.intent.TodoIntent
import com.mvi.todo.model.local.Todo
import com.mvi.todo.model.repository.TodoRepository
import com.mvi.todo.state.TodoState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TodoViewModel @Inject constructor(
    private val repository: TodoRepository,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : ViewModel() {
    private val _state = MutableStateFlow(TodoState())
    val state = _state.asStateFlow()

    init {
        observeTodos()
    }

    private fun observeTodos() {
        viewModelScope.launch(ioDispatcher) {
            _state.update { it.copy(isLoading = true) }
            repository.getAllTodoListFlow().collect { items ->
                _state.update {
                    it.copy(
                        items = items,
                        isLoading = false
                    )
                }
            }
        }
    }

    fun onIntent(intent: TodoIntent) {
        when (intent) {
            is TodoIntent.ChangeDraftTitle -> {
                _state.update { it.copy(draftTitle = intent.title) }
            }

            is TodoIntent.Delete ->
                viewModelScope.launch(ioDispatcher) {
                    repository.delete(intent.todo)
                }

            is TodoIntent.Insert ->
                viewModelScope.launch(ioDispatcher) {
                    repository.insert(intent.todo)
                }

            is TodoIntent.Update ->
                viewModelScope.launch(ioDispatcher) {
                    repository.update(intent.todo)
                }

            is TodoIntent.SubmitTodo -> {
                val title = state.value.draftTitle
                if (title.isNotBlank()) {
                    viewModelScope.launch(ioDispatcher) {
                        repository.insert(Todo(title = title, id = 0, isSelected = false))
                        _state.update { it.copy(draftTitle = "") }
                    }
                }
            }

            is TodoIntent.ConfirmDelete -> {
                val selectionType = state.value.deleteSelection
                val totoToDelete = state.value.selectedTodo

                viewModelScope.launch(ioDispatcher) {
                    when (selectionType) {
                        TodoIntent.DeleteSelection.Single -> {
                            totoToDelete?.let {
                                repository.delete(todo = it)
                            }
                            _state.update {
                                it.copy(
                                    showDelete = false,
                                    deleteSelection = null,
                                    selectedTodo = null
                                )
                            }
                        }

                        TodoIntent.DeleteSelection.Selected -> {
                            val selectedItems = state.value.items.filter {
                                it.isSelected
                            }
                            if (selectedItems.isNotEmpty()) {
                                repository.deleteTodos(selectedItems)
                            }
                            _state.update {
                                it.copy(
                                    showDelete = false,
                                    deleteSelection = null
                                )
                            }
                        }

                        else -> {
                            //TODO showSnackBar("No item selected!")
                        }
                    }
                }
            }

            TodoIntent.DismissDeleteDialog -> {
                _state.update {
                    it.copy(
                        showDelete = false,
                        selectedTodo = null
                    )
                }
            }

            is TodoIntent.ShowDeleteDialog -> {
                _state.update {
                    it.copy(
                        showDelete = true,
                        deleteSelection = intent.selection,
                        selectedTodo = intent.todo
                    )
                }
            }

            is TodoIntent.DeleteSelected -> {
                deleteSelected()
            }
        }
    }

    fun deleteSelected() {
        val selectedItems = state.value.items.filter {
            it.isSelected
        }
        if (selectedItems.isNotEmpty()) {
            viewModelScope.launch(ioDispatcher) {
                repository.deleteTodos(selectedItems)
            }
        } else {
            //TODO showSnackBar("No item selected")
        }
    }
}
