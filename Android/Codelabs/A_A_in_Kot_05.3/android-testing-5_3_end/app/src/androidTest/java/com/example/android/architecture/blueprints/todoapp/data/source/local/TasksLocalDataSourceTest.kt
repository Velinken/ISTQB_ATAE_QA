/*
 * Copyright 2019, The Android Open Source Project
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
package com.example.android.architecture.blueprints.todoapp.data.source.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.matcher.ViewMatchers.assertThat
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.example.android.architecture.blueprints.todoapp.data.Result.Success
import com.example.android.architecture.blueprints.todoapp.data.Task
import com.example.android.architecture.blueprints.todoapp.data.succeeded
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.`is`
import org.junit.After
//import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Integration test for the [TasksDataSource].
 * TasksLocalDatasource- это класс, который принимает информацию, возвращаемую DAO,
 * и преобразует ее в формат, ожидаемый вашим классом репозитория
 * (например, он обертывает возвращаемые значения с помощью Success или Error состояния).
 */
/**
 * Вы будете писать интеграционный тест,
 * потому что вы будете тестировать как реальный TasksLocalDatasource код, так и реальный код DAO.
 */
@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@MediumTest
class TasksLocalDataSourceTest {

    // поле для двух компонентов, которые вы тестируете - TasksLocalDataSource и вашего database:
    private lateinit var localDataSource: TasksLocalDataSource
    private lateinit var database: ToDoDatabase

    // Executes each task synchronously using Architecture Components.
    // Выполняет каждую задачу синхронно с использованием компонентов архитектуры.
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    // метод инициализации вашей базы данных и источника данных.
    @Before
    fun setup() {
        // using an in-memory database for testing, since it doesn't survive killing the process
        // использование базы данных в памяти для тестирования, так как она не переживает убийства процесса
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            ToDoDatabase::class.java
        )
            .allowMainThreadQueries()
            .build()
        // Вызов allowMainThreadQueries отключает эне позволяет выполнять запросы к базе данных в основном потоке.
        // Не делайте этого в производственном коде!

        // Создайте экземпляр TasksLocalDataSource, используя свою базу данных и Dispatchers.Main.
        // Это запустит ваши запросы в основном потоке (это разрешено из-за allowMainThreadQueries).
        localDataSource =
            TasksLocalDataSource(
                database.taskDao(),
                Dispatchers.Main
            )
    }

    // Создайте @After метод очистки базы данных с помощью database.close.
    @After
    fun cleanUp() {
        database.close()
    }

    // runBlocking used here because of https://github.com/Kotlin/kotlinx.coroutines/issues/1204
    // TODO replace with runBlockingTest once issue is resolved
    @Test
    fun saveTask_retrievesTask() = runBlocking {
        // GIVEN - a new task saved in the database
        // Создает задачу и вставляет ее в базу данных.
        val newTask = Task("title", "description", false)
        localDataSource.saveTask(newTask)

        // WHEN  - Task retrieved by ID
        // Получает задачу по ее идентификатору.
        val result = localDataSource.getTask(newTask.id)

        // THEN - Same task is returned
        // Утверждает, что эта задача была получена, и что все ее свойства соответствуют вставленной задаче.
        assertThat(result.succeeded, `is`(true))
        result as Success
        assertThat(result.data.title, `is`("title"))
        assertThat(result.data.description, `is`("description"))
        assertThat(result.data.isCompleted, `is`(false))
    }

    @Test
    fun completeTask_retrievedTaskIsComplete() = runBlocking {
        // Given a new task in the persistent repository
        val newTask = Task("title")
        localDataSource.saveTask(newTask)

        // When completed in the persistent repository
        localDataSource.completeTask(newTask)
        val result = localDataSource.getTask(newTask.id)

        // Then the task can be retrieved from the persistent repository and is complete
        assertThat(result.succeeded, `is`(true))
        result as Success
        assertThat(result.data.title, `is`(newTask.title))
        assertThat(result.data.isCompleted, `is`(true))
       // Единственное реальное отличие от аналогичного теста DAO состоит в том,
       // что локальный источник данных возвращает экземпляр запечатанного Result класса,
       // который является форматом, ожидаемым репозиторием.
    }

}
