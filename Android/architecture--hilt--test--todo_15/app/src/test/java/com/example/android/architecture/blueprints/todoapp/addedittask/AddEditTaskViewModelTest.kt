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
package com.example.android.architecture.blueprints.todoapp.addedittask

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.android.architecture.blueprints.todoapp.MainCoroutineRule
import com.example.android.architecture.blueprints.todoapp.R.string
import com.example.android.architecture.blueprints.todoapp.assertSnackbarMessage
import com.example.android.architecture.blueprints.todoapp.data.Task
import com.example.android.architecture.blueprints.todoapp.data.source.FakeRepository
import com.example.android.architecture.blueprints.todoapp.getOrAwaitValue
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * Unit tests for the implementation of [AddEditTaskViewModel].
 * Модульные тесты для реализации [AddEditTaskViewModel].
 */
@ExperimentalCoroutinesApi
class AddEditTaskViewModelTest {

    // Subject under test
    // Испытуемый
    private lateinit var addEditTaskViewModel: AddEditTaskViewModel

    // Use a fake repository to be injected into the viewmodel
    // Использовать поддельные репозитория, чтобы быть введены в модель представления
    private lateinit var tasksRepository: FakeRepository

    // Set the main coroutines dispatcher for unit testing.
    // Установите главный диспетчер сопрограмм для модульного тестирования.
    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    // Executes each task synchronously using Architecture Components.
    // Выполняет каждую задачу синхронно с использованием компонентов архитектуры
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private val task = Task("Title1", "Description1")

    @Before
    fun setupViewModel() {
        // We initialise the repository with no tasks
        // Мы инициализируем репозиторий без каких-либо задач
        tasksRepository = FakeRepository()

        // Create class under test
        // Создать тестируемый класс
        addEditTaskViewModel = AddEditTaskViewModel(tasksRepository)
    }

    @Test
    fun saveNewTaskToRepository_showsSuccessMessageUi() {
        val newTitle = "New Task Title"
        val newDescription = "Some Task Description"
        (addEditTaskViewModel).apply {
            title.value = newTitle
            description.value = newDescription
        }
        addEditTaskViewModel.saveTask()

        val newTask = tasksRepository.tasksServiceData.values.first()

        // Then a task is saved in the repository and the view updated
        // Затем задача сохраняется в репозитории и представление обновляется
        assertThat(newTask.title).isEqualTo(newTitle)
        assertThat(newTask.description).isEqualTo(newDescription)
    }

    @Test
    fun loadTasks_loading() {
        // Pause dispatcher so we can verify initial values
        // Pause dispatcher, чтобы мы могли проверить начальные значения
        mainCoroutineRule.pauseDispatcher()

        // Load the task in the viewmodel
        // Загрузите задачу в viewmodel
        addEditTaskViewModel.start(task.id)

        // Then progress indicator is shown
        // Затем отображается индикатор прогресса
        assertThat(addEditTaskViewModel.dataLoading.getOrAwaitValue()).isTrue()

        // Execute pending coroutines actions
        // Выполнить Отложенные действия сопрограмм
        mainCoroutineRule.resumeDispatcher()

        // Then progress indicator is hidden
        // Тогда индикатор прогресса будет скрыт
        assertThat(addEditTaskViewModel.dataLoading.getOrAwaitValue()).isFalse()
    }

    @Test
    fun loadTasks_taskShown() {
        // Add task to repository
        // Добавить задачу в репозиторий
        tasksRepository.addTasks(task)

        // Load the task with the viewmodel
        // Загрузите задачу с помощью viewmodel
        addEditTaskViewModel.start(task.id)

        // Verify a task is loaded
        // Убедитесь, что задача загружена
        assertThat(addEditTaskViewModel.title.getOrAwaitValue()).isEqualTo(task.title)
        assertThat(addEditTaskViewModel.description.getOrAwaitValue()).isEqualTo(task.description)
        assertThat(addEditTaskViewModel.dataLoading.getOrAwaitValue()).isFalse()
    }

    @Test
    fun saveNewTaskToRepository_emptyTitle_error() {
        saveTaskAndAssertSnackbarError("", "Some Task Description")
    }

    @Test
    fun saveNewTaskToRepository_nullTitle_error() {
        saveTaskAndAssertSnackbarError(null, "Some Task Description")
    }

    @Test
    fun saveNewTaskToRepository_emptyDescription_error() {
        saveTaskAndAssertSnackbarError("Title", "")
    }

    @Test
    fun saveNewTaskToRepository_nullDescription_error() {
        saveTaskAndAssertSnackbarError("Title", null)
    }

    @Test
    fun saveNewTaskToRepository_nullDescriptionNullTitle_error() {
        saveTaskAndAssertSnackbarError(null, null)
    }

    @Test
    fun saveNewTaskToRepository_emptyDescriptionEmptyTitle_error() {
        saveTaskAndAssertSnackbarError("", "")
    }

    private fun saveTaskAndAssertSnackbarError(title: String?, description: String?) {
        (addEditTaskViewModel).apply {
            this.title.value = title
            this.description.value = description
        }

        // When saving an incomplete task
        // При сохранении незавершенной задачи
        addEditTaskViewModel.saveTask()

        // Then the snackbar shows an error
        // Тогда закусочная показывает ошибку
        assertSnackbarMessage(addEditTaskViewModel.snackbarText, string.empty_task_message)
    }
}
