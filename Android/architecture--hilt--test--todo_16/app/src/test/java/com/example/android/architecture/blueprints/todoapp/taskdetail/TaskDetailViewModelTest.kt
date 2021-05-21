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
package com.example.android.architecture.blueprints.todoapp.taskdetail

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.android.architecture.blueprints.todoapp.MainCoroutineRule
import com.example.android.architecture.blueprints.todoapp.R
import com.example.android.architecture.blueprints.todoapp.assertSnackbarMessage
import com.example.android.architecture.blueprints.todoapp.data.Result.Success
import com.example.android.architecture.blueprints.todoapp.data.Task
import com.example.android.architecture.blueprints.todoapp.data.source.FakeRepository
import com.example.android.architecture.blueprints.todoapp.getOrAwaitValue
import com.example.android.architecture.blueprints.todoapp.observeForTesting
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * Unit tests for the implementation of [TaskDetailViewModel]
 * Блок тестов для осуществления задач подробно посмотреть модель]
 */
@ExperimentalCoroutinesApi
class TaskDetailViewModelTest {

    // Subject under test
    // Испытуемый
    private lateinit var taskDetailViewModel: TaskDetailViewModel

    // Use a fake repository to be injected into the viewmodel
    // Использовать поддельные репозитория, чтобы быть введены в модель представления
    private lateinit var tasksRepository: FakeRepository

    // Set the main coroutines dispatcher for unit testing.
    // Установите главный диспетчер сопрограмм для модульного тестирования.
    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    // Executes each task synchronously using Architecture Components.
    // Выполняет каждую задачу синхронно с использованием компонентов архитектуры.
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    val task = Task("Title1", "Description1")

    @Before
    fun setupViewModel() {
        tasksRepository = FakeRepository()
        tasksRepository.addTasks(task)

        taskDetailViewModel = TaskDetailViewModel(tasksRepository)
    }

    @Test
    fun getActiveTaskFromRepositoryAndLoadIntoView() {
        taskDetailViewModel.start(task.id)

        // Then verify that the view was notified
        // Затем убедитесь, что представление было уведомлено
        assertThat(taskDetailViewModel.task.getOrAwaitValue()?.title).isEqualTo(task.title)
        assertThat(taskDetailViewModel.task.getOrAwaitValue()?.description)
            .isEqualTo(task.description)
    }

    @Test
    fun completeTask() {
        // Load the ViewModel
        // Загрузить ViewModel
        taskDetailViewModel.start(task.id)
        // Start observing to compute transformations
        // Начните наблюдение для вычисления преобразований
        taskDetailViewModel.task.getOrAwaitValue()

        // Verify that the task was active initially
        // Убедитесь, что задача была активна изначально
        assertThat(tasksRepository.tasksServiceData[task.id]?.isCompleted).isFalse()

        // When the ViewModel is asked to complete the task
        // Когда ViewModel просят выполнить задачу
        taskDetailViewModel.setCompleted(true)

        // Then the task is completed and the snackbar shows the correct message
        // Затем задача будет выполнена, и закусочная покажет правильное сообщение
        assertThat(tasksRepository.tasksServiceData[task.id]?.isCompleted).isTrue()
        assertSnackbarMessage(taskDetailViewModel.snackbarText, R.string.task_marked_complete)
    }

    @Test
    fun activateTask() {
        task.isCompleted = true

        // Load the ViewModel
        // Загрузить ViewModel
        taskDetailViewModel.start(task.id)
        // Start observing to compute transformations
        // Начните наблюдение для вычисления преобразований
        taskDetailViewModel.task.observeForTesting {

            // Verify that the task was completed initially
            // Убедитесь, что задача была выполнена изначально
            assertThat(tasksRepository.tasksServiceData[task.id]?.isCompleted).isTrue()

            // When the ViewModel is asked to complete the task
            // Когда ViewModel просят выполнить задачу
            taskDetailViewModel.setCompleted(false)

            mainCoroutineRule.runBlockingTest {
                // Then the task is not completed and the snackbar shows the correct message
                // Тогда задача не будет выполнена, и закусочная покажет правильное сообщение
                val newTask = (tasksRepository.getTask(task.id) as Success).data
                assertTrue(newTask.isActive)
                assertSnackbarMessage(taskDetailViewModel.snackbarText, R.string.task_marked_active)
            }
        }
    }

    @Test
    fun taskDetailViewModel_repositoryError() {
        // Given a repository that returns errors
        // Задан репозиторий, возвращающий ошибки
        tasksRepository.setReturnError(true)

        // Given an initialized ViewModel with an active task
        // Приведенный репозиторий, который возвращает ошибки
        taskDetailViewModel.start(task.id)
        // Get the computed LiveData value
        // Получить вычисленное значение LiveData
        taskDetailViewModel.task.observeForTesting {
            // Then verify that data is not available
            // Затем убедитесь, что данные недоступны
            assertThat(taskDetailViewModel.isDataAvailable.getOrAwaitValue()).isFalse()
        }
    }

    @Test
    fun updateSnackbar_nullValue() {
        // Before setting the Snackbar text, get its current value
        // Перед установкой текста Снэк-бара получите его текущее значение
        val snackbarText = taskDetailViewModel.snackbarText.value

        // Check that the value is null
        // Убедитесь, что значение равно null
        assertThat(snackbarText).isNull()
    }

    @Test
    fun clickOnEditTask_SetsEvent() {
        // When opening a new task
        // При открытии новой задачи
        taskDetailViewModel.editTask()

        // Then the event is triggered
        // Затем событие срабатывает
        val value = taskDetailViewModel.editTaskEvent.getOrAwaitValue()
        assertThat(value.getContentIfNotHandled()).isNotNull()
    }

    @Test
    fun deleteTask() {
        assertThat(tasksRepository.tasksServiceData.containsValue(task)).isTrue()
        taskDetailViewModel.start(task.id)

        // When the deletion of a task is requested
        // Когда запрашивается удаление задачи
        taskDetailViewModel.deleteTask()

        assertThat(tasksRepository.tasksServiceData.containsValue(task)).isFalse()
    }

    @Test
    fun loadTask_loading() {
        // Pause dispatcher so we can verify initial values
        // Pause dispatcher, чтобы мы могли проверить начальные значения
        mainCoroutineRule.pauseDispatcher()

        // Load the task in the viewmodel
        // Загрузите задачу в viewmodel
        taskDetailViewModel.start(task.id)
        // Start observing to compute transformations
        // Начните наблюдение для вычисления преобразований
        taskDetailViewModel.task.observeForTesting {
            // Force a refresh to show the loading indicator
            // Принудительное обновление для отображения индикатора загрузки
            taskDetailViewModel.refresh()

            // Then progress indicator is shown
            // Затем отображается индикатор прогресса
            assertThat(taskDetailViewModel.dataLoading.getOrAwaitValue()).isTrue()

            // Execute pending coroutines actions
            // Выполнить Отложенные действия сопрограмм
            mainCoroutineRule.resumeDispatcher()

            // Then progress indicator is hidden
            // Тогда индикатор прогресса будет скрыт
            assertThat(taskDetailViewModel.dataLoading.getOrAwaitValue()).isFalse()
        }
    }
}
