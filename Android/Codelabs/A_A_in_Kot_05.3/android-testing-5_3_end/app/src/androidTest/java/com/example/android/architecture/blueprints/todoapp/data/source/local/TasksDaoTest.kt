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
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.example.android.architecture.blueprints.todoapp.data.Task
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * В какой исходный набор следует поместить тесты базы данных?
 * Обратите внимание, что, как правило, делайте тесты базы данных инструментальными тестами,
 * то есть они будут в androidTest исходном наборе.
 * Это связано с тем, что если вы запустите эти тесты локально,
 * они будут использовать любую версию SQLite, установленную на вашем локальном компьютере,
 * которая может сильно отличаться от версии SQLite, поставляемой с вашим устройством Android!
 * Различные устройства Android также поставляются с разными версиями SQLite,
 * поэтому также полезно иметь возможность запускать эти тесты как инструментальные тесты на разных устройствах.
 */
/**
 * @ExperimentalCoroutinesApi- вы будете использовать runBlockingTest,
 *   который является частью kotlinx-coroutines-test, поэтому вам понадобится эта аннотация.
 * @SmallTest- Помечает тест как интеграционный тест «малого времени выполнения»
 *   (по сравнению с @MediumTestинтеграционными тестами и @LargeTestсквозными тестами).
 *   Это поможет вам сгруппировать и выбрать тест размера для запуска.
 *   Тесты DAO считаются модульными тестами, поскольку вы тестируете только DAO, поэтому вы можете называть их небольшими тестами.
 * @RunWith(AndroidJUnit4::class)—Используется в любом классе, использующем AndroidX Test.
 * Это было рассмотрено в первой лабораторной работе .
 */
@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@SmallTest
class TasksDaoTest {

    // Чтобы получить доступ к экземпляру вашего DAO, вам необходимо создать экземпляр вашей базы данных.
    // Для этого в ваших тестах сделайте следующее:
    private lateinit var database: ToDoDatabase

    // Executes each task synchronously using Architecture Components.
    // Выполняет каждую задачу синхронно с использованием компонентов архитектуры.
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun initDb() {
        // using an in-memory database because the information stored here disappears when the process is killed
        // Выполняет каждую задачу синхронно с использованием компонентов архитектуры, используя базу данных в памяти,
        // поскольку хранящаяся здесь информация исчезает при завершении процесса
        database = Room.inMemoryDatabaseBuilder(
            getApplicationContext(),
            ToDoDatabase::class.java
        ).build()
        //  Всегда используйте базу данных в памяти для ваших тестов.
        // Используйте метод библиотек AndroidX Test, ApplicationProvider.getApplicationContext()
        // чтобы получить контекст приложения.
    }
    // Создайте @After метод очистки базы данных с помощью database.close().
    @After
    fun closeDb() = database.close()

    @Test
    fun insertTaskAndGetById() = runBlockingTest {
        // Вы запускаете тест, используя, runBlockingTest
        // потому что обе функции insertTask и getTaskById являются приостановленными функциями.
        //Вы используете DAO как обычно, обращаясь к нему из своего экземпляра базы данных.

        // GIVEN - insert a task
        // Создает задачу и вставляет ее в базу данных.
        val task = Task("title", "description")
        database.taskDao().insertTask(task)

        // WHEN - Get the task by id from the database
        // Получает задачу по ее идентификатору.
        val loaded = database.taskDao().getTaskById(task.id)

        // THEN - The loaded data contains the expected values
        // Утверждает, что эта задача была получена, и что все ее свойства соответствуют вставленной задаче.
        assertThat(loaded as Task, notNullValue())
        assertThat(loaded.id, `is`(task.id))
        assertThat(loaded.title, `is`(task.title))
        assertThat(loaded.description, `is`(task.description))
        assertThat(loaded.isCompleted, `is`(task.isCompleted))
    }

    @Test
    fun updateTaskAndGetById() = runBlockingTest {
        // When inserting a task
        val originalTask = Task("title", "description")
        database.taskDao().insertTask(originalTask)

        // When the task is updated
        val updatedTask = Task("new title", "new description", true, originalTask.id)
        database.taskDao().updateTask(updatedTask)

        // THEN - The loaded data contains the expected values
        val loaded = database.taskDao().getTaskById(originalTask.id)
        assertThat(loaded?.id, `is`(originalTask.id))
        assertThat(loaded?.title, `is`("new title"))
        assertThat(loaded?.description, `is`("new description"))
        assertThat(loaded?.isCompleted, `is`(true))
    }
}
