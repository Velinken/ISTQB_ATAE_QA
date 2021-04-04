/*
 * Copyright (C) 2019 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.architecture.blueprints.todoapp.data.source

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.room.Room
import com.example.android.architecture.blueprints.todoapp.data.Result
import com.example.android.architecture.blueprints.todoapp.data.Result.Success
import com.example.android.architecture.blueprints.todoapp.data.Task
import com.example.android.architecture.blueprints.todoapp.data.source.local.TasksLocalDataSource
import com.example.android.architecture.blueprints.todoapp.data.source.local.ToDoDatabase
import com.example.android.architecture.blueprints.todoapp.data.source.remote.TasksRemoteDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Concrete implementation to load tasks from the data sources into a cache.
 * Конкретная реализация для загрузки задач из источников данных в кэш.
 */
// class DefaultTasksRepository private constructor(application: Application) {
  /*  Без инъекции вручную первонвчальный вариант:
    private val tasksRemoteDataSource: TasksDataSource
    private val tasksLocalDataSource: TasksDataSource
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO

    companion object {
        @Volatile
        private var INSTANCE: DefaultTasksRepository? = null

        fun getRepository(app: Application): DefaultTasksRepository {
            return INSTANCE ?: synchronized(this) {
                DefaultTasksRepository(app).also {
                    INSTANCE = it
                }
            }
        }
    }

    init {
        // Строятся рабочие зависимости для данных
        val database = Room.databaseBuilder(application.applicationContext,
            ToDoDatabase::class.java, "Tasks.db")
            .build()

        tasksRemoteDataSource = TasksRemoteDataSource
        tasksLocalDataSource = TasksLocalDataSource(database.taskDao())
    }
*/
/** Теперь вы используете внедрение зависимостей конструктора!
 * С ручной инъекцией зависимостей
 * Приведенный ниже код внедряет локальный и удаленный источник данных, а также файл CoroutineDispatcher.
 * Поскольку диспетчер внедрен, вы можете использовать TestCoroutineDispatcher в своих тестах.
 * Внедрение CoroutineDispatcher, в отличие от жесткого программирования диспетчера,
 * является хорошей привычкой при использовании сопрограмм .
*/


class DefaultTasksRepository(
        private val tasksRemoteDataSource: TasksDataSource,
        private val tasksLocalDataSource: TasksDataSource,
        private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO) : TasksRepository {
    // Теперь companion object не нужен - будет создаваться в APP ServiceLocator.provideTaskRepository
    // С ручной инъекцией зависимостей
  /*  companion object {
        @Volatile
        private var INSTANCE: DefaultTasksRepository? = null

        fun getRepository(app: Application): DefaultTasksRepository {
            return INSTANCE ?: synchronized(this) {
                val database = Room.databaseBuilder(app,
                        ToDoDatabase::class.java, "Tasks.db")
                        .build()
                DefaultTasksRepository(TasksRemoteDataSource, TasksLocalDataSource(database.taskDao())).also {
                    INSTANCE = it
                }
            }
        }
    }*/

    override suspend fun getTasks(forceUpdate: Boolean): Result<List<Task>> {
        if (forceUpdate) {
            try {
                updateTasksFromRemoteDataSource()
            } catch (ex: Exception) {
                return Result.Error(ex)
            }
        }
        return tasksLocalDataSource.getTasks()
    }

    override suspend fun refreshTasks() {
        updateTasksFromRemoteDataSource()
    }

    override fun observeTasks(): LiveData<Result<List<Task>>> {
        return tasksLocalDataSource.observeTasks()
    }

    override suspend fun refreshTask(taskId: String) {
        updateTaskFromRemoteDataSource(taskId)
    }

    private suspend fun updateTasksFromRemoteDataSource() {
        val remoteTasks = tasksRemoteDataSource.getTasks()

        if (remoteTasks is Success) {
            // Real apps might want to do a proper sync.
            // Реальные приложения могут захотеть сделать правильную синхронизацию.
            tasksLocalDataSource.deleteAllTasks()
            remoteTasks.data.forEach { task ->
                tasksLocalDataSource.saveTask(task)
            }
        } else if (remoteTasks is Result.Error) {
            throw remoteTasks.exception
        }
    }

    override fun observeTask(taskId: String): LiveData<Result<Task>> {
        return tasksLocalDataSource.observeTask(taskId)
    }

    private suspend fun updateTaskFromRemoteDataSource(taskId: String) {
        val remoteTask = tasksRemoteDataSource.getTask(taskId)

        if (remoteTask is Success) {
            tasksLocalDataSource.saveTask(remoteTask.data)
        }
    }

    /**
     * Relies on [getTasks] to fetch data and picks the task with the same ID.
     * Полагается на [getTasks] для извлечения данных и выбирает задачу с тем же идентификатором.
     */
    override suspend fun getTask(taskId: String, forceUpdate: Boolean): Result<Task> {
        if (forceUpdate) {
            updateTaskFromRemoteDataSource(taskId)
        }
        return tasksLocalDataSource.getTask(taskId)
    }

    override suspend fun saveTask(task: Task) {
        coroutineScope {
            launch { tasksRemoteDataSource.saveTask(task) }
            launch { tasksLocalDataSource.saveTask(task) }
        }
    }

    override suspend fun completeTask(task: Task) {
        coroutineScope {
            launch { tasksRemoteDataSource.completeTask(task) }
            launch { tasksLocalDataSource.completeTask(task) }
        }
    }

    override suspend fun completeTask(taskId: String) {
        withContext(ioDispatcher) {
            (getTaskWithId(taskId) as? Success)?.let { it ->
                completeTask(it.data)
            }
        }
    }

    override suspend fun activateTask(task: Task) = withContext<Unit>(ioDispatcher) {
        coroutineScope {
            launch { tasksRemoteDataSource.activateTask(task) }
            launch { tasksLocalDataSource.activateTask(task) }
        }
    }

    override suspend fun activateTask(taskId: String) {
        withContext(ioDispatcher) {
            (getTaskWithId(taskId) as? Success)?.let { it ->
                activateTask(it.data)
            }
        }
    }

    override suspend fun clearCompletedTasks() {
        coroutineScope {
            launch { tasksRemoteDataSource.clearCompletedTasks() }
            launch { tasksLocalDataSource.clearCompletedTasks() }
        }
    }

    override suspend fun deleteAllTasks() {
        withContext(ioDispatcher) {
            coroutineScope {
                launch { tasksRemoteDataSource.deleteAllTasks() }
                launch { tasksLocalDataSource.deleteAllTasks() }
            }
        }
    }

    override suspend fun deleteTask(taskId: String) {
        coroutineScope {
            launch { tasksRemoteDataSource.deleteTask(taskId) }
            launch { tasksLocalDataSource.deleteTask(taskId) }
        }
    }

    private suspend fun getTaskWithId(id: String): Result<Task> {
        return tasksLocalDataSource.getTask(id)
    }
}
