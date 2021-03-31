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
package com.example.android.architecture.blueprints.todoapp.statistics

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.android.architecture.blueprints.todoapp.FakeFailingTasksRemoteDataSource
import com.example.android.architecture.blueprints.todoapp.MainCoroutineRule
import com.example.android.architecture.blueprints.todoapp.data.Task
import com.example.android.architecture.blueprints.todoapp.data.source.DefaultTasksRepository
import com.example.android.architecture.blueprints.todoapp.data.source.FakeRepository
import com.example.android.architecture.blueprints.todoapp.getOrAwaitValue
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * Unit tests for the implementation of [StatisticsViewModel]
 * Модульные тесты для реализации [StatisticsViewModel]
 */
@ExperimentalCoroutinesApi
class StatisticsViewModelTest {

    // Executes each task synchronously using Architecture Components.
    // Выполняет каждую задачу синхронно с использованием компонентов архитектуры.
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    // Subject under test
    // Испытуемый
    private lateinit var statisticsViewModel: StatisticsViewModel

    // Use a fake repository to be injected into the viewmodel
    // Использовать поддельные репозитория, чтобы быть введены в модель представления
    private lateinit var tasksRepository: FakeRepository

    // Set the main coroutines dispatcher for unit testing.
    // Установите главный диспетчер сопрограмм для модульного тестирования.
    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @Before
    fun setupStatisticsViewModel() {
        tasksRepository = FakeRepository()

        statisticsViewModel = StatisticsViewModel(tasksRepository)
    }

    @Test
    fun loadEmptyTasksFromRepository_EmptyResults() = mainCoroutineRule.runBlockingTest {
        // Given an initialized StatisticsViewModel with no tasks
        // Дана инициализированная StatisticsViewModel без каких-либо задач

        // Then the results are empty
        // Тогда результаты будут пустыми
        assertThat(statisticsViewModel.empty.getOrAwaitValue()).isTrue()
    }

    @Test
    fun loadNonEmptyTasksFromRepository_NonEmptyResults() {
        // We initialise the tasks to 3, with one active and two completed
        // Мы инициализируем задачи до 3, с одним активным и двумя завершенными
        val task1 = Task("Title1", "Description1")
        val task2 = Task("Title2", "Description2", true)
        val task3 = Task("Title3", "Description3", true)
        val task4 = Task("Title4", "Description4", true)
        tasksRepository.addTasks(task1, task2, task3, task4)

        // Then the results are not empty
        // Тогда результаты не пустые
        assertThat(statisticsViewModel.empty.getOrAwaitValue())
            .isFalse()
        assertThat(statisticsViewModel.activeTasksPercent.getOrAwaitValue())
            .isEqualTo(25f)
        assertThat(statisticsViewModel.completedTasksPercent.getOrAwaitValue())
            .isEqualTo(75f)
    }

    @Test
    fun loadStatisticsWhenTasksAreUnavailable_CallErrorToDisplay() {
        val errorViewModel = StatisticsViewModel(
            DefaultTasksRepository(
                FakeFailingTasksRemoteDataSource,
                FakeFailingTasksRemoteDataSource,
                Dispatchers.Main // Main is set in MainCoroutineRule
            )
        )

        // Then an error message is shown
        // Затем появляется сообщение об ошибке
        assertThat(errorViewModel.empty.getOrAwaitValue()).isTrue()
        assertThat(errorViewModel.error.getOrAwaitValue()).isTrue()
    }

    @Test
    fun loadTasks_loading() {
        // Pause dispatcher so we can verify initial values
        // Pause dispatcher, чтобы мы могли проверить начальные значения
        mainCoroutineRule.pauseDispatcher()

        // Load the task in the viewmodel
        statisticsViewModel.refresh()

        // Then progress indicator is shown
        // Затем отображается индикатор прогресса
        assertThat(statisticsViewModel.dataLoading.getOrAwaitValue()).isTrue()

        // Execute pending coroutines actions
        // Выполнить Отложенные действия сопрограмм
        mainCoroutineRule.resumeDispatcher()

        // Then progress indicator is hidden
        // Тогда индикатор прогресса будет скрыт
        assertThat(statisticsViewModel.dataLoading.getOrAwaitValue()).isFalse()
    }
}
