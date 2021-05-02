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

package com.example.android.architecture.blueprints.todoapp.tasks

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.SavedStateHandle
import com.example.android.architecture.blueprints.todoapp.MainCoroutineRule
import com.example.android.architecture.blueprints.todoapp.R
import com.example.android.architecture.blueprints.todoapp.assertLiveDataEventTriggered
import com.example.android.architecture.blueprints.todoapp.assertSnackbarMessage
import com.example.android.architecture.blueprints.todoapp.data.Task
import com.example.android.architecture.blueprints.todoapp.data.source.FakeRepository
import com.example.android.architecture.blueprints.todoapp.getOrAwaitValue
import com.example.android.architecture.blueprints.todoapp.observeForTesting
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * Unit tests for the implementation of [TasksViewModel]
 * Модульные тесты для реализации [TasksViewModel]
 */
@ExperimentalCoroutinesApi
class TasksViewModelTest {

    // Subject under test
    // Испытуемый
    private lateinit var tasksViewModel: TasksViewModel

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

    @Before
    fun setupViewModel() {
        // We initialise the tasks to 3, with one active and two completed
        // Мы инициализируем задачи до 3, с одним активным и двумя завершенными
        tasksRepository = FakeRepository()
        val task1 = Task("Title1", "Description1")
        val task2 = Task("Title2", "Description2", true)
        val task3 = Task("Title3", "Description3", true)
        tasksRepository.addTasks(task1, task2, task3)

        tasksViewModel = TasksViewModel(tasksRepository, SavedStateHandle())
    }

    @Test
    fun loadAllTasksFromRepository_loadingTogglesAndDataLoaded() {
        // Pause dispatcher so we can verify initial values
        // Pause dispatcher, чтобы мы могли проверить начальные значения
        mainCoroutineRule.pauseDispatcher()

        // Given an initialized TasksViewModel with initialized tasks
        // Дано инициализированное TasksViewModel с инициализированными задачами
        // When loading of Tasks is requested
        // При загрузке задач запрашивается
        tasksViewModel.setFiltering(TasksFilterType.ALL_TASKS)

        // Trigger loading of tasks
        // Триггерная загрузка задач
        tasksViewModel.loadTasks(true)
        // Observe the items to keep LiveData emitting
        // Наблюдайте за элементами, чтобы сохранить излучение живых данных
        tasksViewModel.items.observeForTesting {

            // Then progress indicator is shown
            // Затем отображается индикатор прогресса
            assertThat(tasksViewModel.dataLoading.getOrAwaitValue()).isTrue()

            // Execute pending coroutines actions
            // Выполнить Отложенные действия сопрограмм
            mainCoroutineRule.resumeDispatcher()

            // Then progress indicator is hidden
            // Тогда индикатор прогресса будет скрыт
            assertThat(tasksViewModel.dataLoading.getOrAwaitValue()).isFalse()

            // And data correctly loaded
            // И данные правильно загружены
            assertThat(tasksViewModel.items.getOrAwaitValue()).hasSize(3)
        }
    }

    @Test
    fun loadActiveTasksFromRepositoryAndLoadIntoView() {
        // Given an initialized TasksViewModel with initialized tasks
        // Дано инициализированное TasksViewModel с инициализированными задачами
        // When loading of Tasks is requested
        // При загрузке задач запрашивается
        tasksViewModel.setFiltering(TasksFilterType.ACTIVE_TASKS)

        // Load tasks
        // Загрузка задач
        tasksViewModel.loadTasks(true)
        // Observe the items to keep LiveData emitting
        // Наблюдайте за элементами, чтобы сохранить излучение живых данных
        tasksViewModel.items.observeForTesting {

            // Then progress indicator is hidden
            // Тогда индикатор прогресса будет скрыт
            assertThat(tasksViewModel.dataLoading.getOrAwaitValue()).isFalse()

            // And data correctly loaded
            // И данные правильно загружены
            assertThat(tasksViewModel.items.getOrAwaitValue()).hasSize(1)
        }
    }

    @Test
    fun loadCompletedTasksFromRepositoryAndLoadIntoView() {
        // Given an initialized TasksViewModel with initialized tasks
        // Дано инициализированное TasksViewModel с инициализированными задачами
        // When loading of Tasks is requested
        // При загрузке задач запрашивается
        tasksViewModel.setFiltering(TasksFilterType.COMPLETED_TASKS)

        // Load tasks
        // Загрузка задач
        tasksViewModel.loadTasks(true)
        // Observe the items to keep LiveData emitting
        // Наблюдайте за элементами, чтобы сохранить излучение живых данных
        tasksViewModel.items.observeForTesting {

            // Then progress indicator is hidden
            // Тогда индикатор прогресса будет скрыт
            assertThat(tasksViewModel.dataLoading.getOrAwaitValue()).isFalse()

            // And data correctly loaded
            // И данные правильно загружены
            assertThat(tasksViewModel.items.getOrAwaitValue()).hasSize(2)
        }
    }

    @Test
    fun loadTasks_error() {
        // Make the repository return errors
        // Сделать так, чтобы репозиторий возвращал ошибки
        tasksRepository.setReturnError(true)

        // Load tasks
        // Загрузка задач
        tasksViewModel.loadTasks(true)
        // Observe the items to keep LiveData emitting
        // Наблюдайте за элементами, чтобы сохранить излучение живых данных
        tasksViewModel.items.observeForTesting {

            // Then progress indicator is hidden
            // Тогда индикатор прогресса будет скрыт
            assertThat(tasksViewModel.dataLoading.getOrAwaitValue()).isFalse()

            // And the list of items is empty
            // И список элементов пуст
            assertThat(tasksViewModel.items.getOrAwaitValue()).isEmpty()

            // And the snackbar updated
            // И закусочная обновилась
            assertSnackbarMessage(tasksViewModel.snackbarText, R.string.loading_tasks_error)
        }
    }

    @Test
    fun clickOnFab_showsAddTaskUi() {
        // When adding a new task
        // При добавлении новой задачи
        tasksViewModel.addNewTask()

        // Then the event is triggered
        // Затем событие срабатывает
        val value = tasksViewModel.newTaskEvent.getOrAwaitValue()
        assertThat(value.getContentIfNotHandled()).isNotNull()
    }

    @Test
    fun clickOnOpenTask_setsEvent() {
        // When opening a new task
        // При открытии новой задачи
        val taskId = "42"
        tasksViewModel.openTask(taskId)

        // Then the event is triggered
        // Затем событие срабатывает
        assertLiveDataEventTriggered(tasksViewModel.openTaskEvent, taskId)
    }

    @Test
    fun clearCompletedTasks_clearsTasks() = mainCoroutineRule.runBlockingTest {
        // When completed tasks are cleared
        // Когда завершенные задачи будут очищены
        tasksViewModel.clearCompletedTasks()

        // Fetch tasks
        // Задачи выборки
        tasksViewModel.loadTasks(true)

        // Fetch tasks
        // Задачи выборки
        val allTasks = tasksViewModel.items.getOrAwaitValue()
        val completedTasks = allTasks.filter { it.isCompleted }

        // Verify there are no completed tasks left
        // Убедитесь, что завершенных задач не осталось
        assertThat(completedTasks).isEmpty()

        // Verify active task is not cleared
        // Убедитесь, что активная задача не очищена
        assertThat(allTasks).hasSize(1)

        // Verify snackbar is updated
        // Убедитесь, что снэк-бар обновлен
        assertSnackbarMessage(
            tasksViewModel.snackbarText, R.string.completed_tasks_cleared
        )
    }

    @Test
    fun showEditResultMessages_editOk_snackbarUpdated() {
        // When the viewmodel receives a result from another destination
        // Когда viewmodel получает результат из другого места назначения
        tasksViewModel.showEditResultMessage(EDIT_RESULT_OK)

        // The snackbar is updated
        // Снэк-бар обновлен
        assertSnackbarMessage(
            tasksViewModel.snackbarText, R.string.successfully_saved_task_message
        )
    }

    @Test
    fun showEditResultMessages_addOk_snackbarUpdated() {
        // When the viewmodel receives a result from another destination
        // Когда viewmodel получает результат из другого места назначения
        tasksViewModel.showEditResultMessage(ADD_EDIT_RESULT_OK)

        // The snackbar is updated
        // Снэк-бар обновлен
        assertSnackbarMessage(
            tasksViewModel.snackbarText, R.string.successfully_added_task_message
        )
    }

    @Test
    fun showEditResultMessages_deleteOk_snackbarUpdated() {
        // When the viewmodel receives a result from another destination
        // Когда viewmodel получает результат из другого места назначения
        tasksViewModel.showEditResultMessage(DELETE_RESULT_OK)

        // The snackbar is updated
        // Снэк-бар обновлен
        assertSnackbarMessage(tasksViewModel.snackbarText, R.string.successfully_deleted_task_message)
    }

    @Test
    fun completeTask_dataAndSnackbarUpdated() {
        // With a repository that has an active task
        // С репозиторием, имеющим активную задачу
        val task = Task("Title", "Description")
        tasksRepository.addTasks(task)

        // Complete task
        // Завершенная задача
        tasksViewModel.completeTask(task, true)

        // Verify the task is completed
        // Убедитесь, что задача выполнена
        assertThat(tasksRepository.tasksServiceData[task.id]?.isCompleted).isTrue()

        // The snackbar is updated
        // Снэк-бар обновлен
        assertSnackbarMessage(
            tasksViewModel.snackbarText, R.string.task_marked_complete
        )
    }

    @Test
    fun activateTask_dataAndSnackbarUpdated() {
        // With a repository that has a completed task
        // С репозиторием, у которого есть завершенная задача
        val task = Task("Title", "Description", true)
        tasksRepository.addTasks(task)

        // Activate task
        // Активировать задачу
        tasksViewModel.completeTask(task, false)

        // Verify the task is active
        // Убедитесь, что задача активна
        assertThat(tasksRepository.tasksServiceData[task.id]?.isActive).isTrue()

        // The snackbar is updated
        // Снэк-бар обновлен
        assertSnackbarMessage(
            tasksViewModel.snackbarText, R.string.task_marked_active
        )
    }

    @Test
    fun getTasksAddViewVisible() {
        // When the filter type is ALL_TASKS
        // Когда тип фильтра-ALL_TASKS
        tasksViewModel.setFiltering(TasksFilterType.ALL_TASKS)

        // Then the "Add task" action is visible
        // Тогда будет видно действие "Добавить задачу"
        assertThat(tasksViewModel.tasksAddViewVisible.getOrAwaitValue()).isTrue()
    }
}
