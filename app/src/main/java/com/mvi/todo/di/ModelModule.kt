package com.mvi.todo.di

import android.content.Context
import com.mvi.todo.TodoViewModel
import com.mvi.todo.model.local.TodoDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import com.mvi.todo.model.local.TodoDatabase
import com.mvi.todo.model.repository.TodoRepoImpl
import com.mvi.todo.model.repository.TodoRepository


@Module
@InstallIn(SingletonComponent::class)
object ModelModule {

    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext context: Context): TodoDatabase {
        return TodoDatabase.getInstance(context)
    }

    @Provides
    fun provideDao(todoDatabase: TodoDatabase): TodoDao {
        return todoDatabase.todoDao()
    }

    @Provides
    fun provideRepository(todoDao: TodoDao): TodoRepository {
        return TodoRepoImpl(todoDao)
    }

    @Provides
    fun provideViewModel(repo: TodoRepository): TodoViewModel {
        return TodoViewModel(repo)
    }
}