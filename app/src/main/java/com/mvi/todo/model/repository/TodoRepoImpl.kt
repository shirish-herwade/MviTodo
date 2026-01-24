package com.mvi.todo.model.repository

import com.mvi.todo.model.local.Todo
import com.mvi.todo.model.local.TodoDao
import kotlinx.coroutines.flow.Flow

class TodoRepoImpl(private val todoDao: TodoDao) : TodoRepository {
    override suspend fun insert(todo: Todo) {
        todoDao.insert(todo)
    }

    override suspend fun delete(todo: Todo) {
        todoDao.delete(todo)
    }

    override suspend fun deleteTodos(todos: List<Todo>) {
        todoDao.deleteTodos(todos)
    }

    override suspend fun update(todo: Todo) {
        todoDao.update(todo)
    }

    override fun getAllTodoListFlow(): Flow<List<Todo>> =
        todoDao.getAlTodoList()

//    override fun getAllTodo(): List<Todo> {
//        return todoDao.getAlTodo()
//    }
}