package com.example.android.architecture.blueprints.todoapp

import android.content.Context
import androidx.annotation.VisibleForTesting
import androidx.room.Room
import com.example.android.architecture.blueprints.todoapp.data.source.DefaultTasksRepository
import com.example.android.architecture.blueprints.todoapp.data.source.TasksDataSource
import com.example.android.architecture.blueprints.todoapp.data.source.TasksRepository
import com.example.android.architecture.blueprints.todoapp.data.source.local.TasksLocalDataSource
import com.example.android.architecture.blueprints.todoapp.data.source.local.ToDoDatabase
import com.example.android.architecture.blueprints.todoapp.data.source.remote.TasksRemoteDataSource
import kotlinx.coroutines.runBlocking

// Это класс - экземпляр сразу, но только один объект, он создастся сразу. Но его нельзя больше создавать
// второй и далее раз. Специальная конструкция в Котлин, поэтому объект, а не класс
object ServiceLocator {
    // Программист - тестер снаружи может только вызвать функцию provideTasksRepository и
    // обращаться к tasksRepository. все остальное привейт

    // ссылка на базу данных, пока никуда, она нужна без нее не будут работать тест.
    private var database: ToDoDatabase? = null
    private val lock = Any()
    @Volatile
    // т.к. может использоваться несколькими потоками
    // ссылка на репозиторий, пока нулл
    // когда локатор вызывается в рабочей программе, то мы сюда загоним ссылку на реальный
    // репозиторий, а когда в тестовый, то на фейковый
    var tasksRepository: TasksRepository? = null
        // включаем аннотацию VisibleForTesting
        @VisibleForTesting set
    // А теперь ее используем для этой функции, которая за нйе стоит
    @VisibleForTesting
    fun resetRepository() {
        synchronized(lock) {
            runBlocking {
                TasksRemoteDataSource.deleteAllTasks()
            }
            // Clear all data to avoid test pollution.
            database?.apply {
                clearAllTables()
                close()
            }
            database = null
            tasksRepository = null
        }
    }
    // при обращении к этой функции сюда контекст передается, он создаст тасксрепозиторий новый класс
    // но если он сущестивует, то вернет ссылку на существующий.
    // Функция - дай репозиторий, а если его нет, то создай новый
    fun provideTasksRepository(context: Context): TasksRepository {
        synchronized(this) {
            return tasksRepository ?: createTasksRepository(context)
        }
    }
    // Создание нового репозитория вызывается из функции выше
    private fun createTasksRepository(context: Context): TasksRepository {
        val newRepo = DefaultTasksRepository(TasksRemoteDataSource, createTaskLocalDataSource(context))
        tasksRepository = newRepo
        return newRepo
    }

    // Такой же фокус или создать новый или вернуть существующий для TaskLocalDataSource
    private fun createTaskLocalDataSource(context: Context): TasksDataSource {
        val database = database ?: createDataBase(context)
        return TasksLocalDataSource(database.taskDao())
    }

    // Вызывается из верхнего, при создании новой
    private fun createDataBase(context: Context): ToDoDatabase {
        val result = Room.databaseBuilder(
            context.applicationContext,
            ToDoDatabase::class.java, "Tasks.db"
        ).build()
        database = result
        return result
    }
}