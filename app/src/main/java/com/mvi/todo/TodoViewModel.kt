package com.mvi.todo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mvi.todo.intent.TodoIntent
import com.mvi.todo.model.local.Todo
import com.mvi.todo.model.repository.TodoRepository
import com.mvi.todo.state.TodoState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TodoViewModel @Inject constructor(
    private val repository: TodoRepository
) : ViewModel() {
    private val _state = MutableStateFlow(TodoState())
    val state = _state.asStateFlow()

    init {
        observeTodos()
    }

    private fun observeTodos() {
        viewModelScope.launch(Dispatchers.IO) {
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
            is TodoIntent.Delete ->
                viewModelScope.launch(Dispatchers.IO) {
                    repository.delete(intent.todo)
                }

            is TodoIntent.Insert ->
                viewModelScope.launch(Dispatchers.IO) {
                    repository.insert(intent.todo)
                }

            is TodoIntent.Update ->
                viewModelScope.launch(Dispatchers.IO) {
                    repository.update(intent.todo)
                }

            is TodoIntent.SaveTodo ->
                viewModelScope.launch(Dispatchers.IO) {
                    repository.insert(Todo(title = intent.title, id = 0, isDone = false))
                }
        }
    }
}
