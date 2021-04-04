package com.example.android.architecture.blueprints.todoapp.statistics

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.android.architecture.blueprints.todoapp.MainCoroutineRule
import com.example.android.architecture.blueprints.todoapp.data.source.FakeTestRepository
import com.example.android.architecture.blueprints.todoapp.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
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

    // Set the main coroutines dispatcher for unit testing.
    // Установите главный диспетчер сопрограмм для модульного тестирования.
    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    // Subject under test
    // Испытуемый объект
    private lateinit var statisticsViewModel: StatisticsViewModel

    // Use a fake repository to be injected into the view model.
    // Используйте поддельный репозиторий, который будет введен в viewmodel.
    private lateinit var tasksRepository: FakeTestRepository

    @Before
    fun setupStatisticsViewModel() {
        // Initialise the repository with no tasks.
        // Инициализируйте репозиторий без каких-либо задач.
        tasksRepository = FakeTestRepository()

        statisticsViewModel = StatisticsViewModel(tasksRepository)
    }
    @Test
    fun loadTasks_loading() {

        // Pause dispatcher so you can verify initial values.
        // Приостановить диспетчер, чтобы вы могли проверить начальные значения.
        mainCoroutineRule.pauseDispatcher()


        // Load the task in the view model.
        // Загрузите задачу в модель представления.
        statisticsViewModel.refresh()

        // Then assert that the progress indicator is shown.
        // Затем подтвердите, что индикатор прогресса показан.
        assertThat(statisticsViewModel.dataLoading.getOrAwaitValue(), `is`(true))

        // Execute pending coroutines actions.
        // Выполнение ожидающих действий сопрограмм.
        mainCoroutineRule.resumeDispatcher()


        // Then assert that the progress indicator is hidden.
        // Затем утверждайте, что индикатор прогресса скрыт.
        assertThat(statisticsViewModel.dataLoading.getOrAwaitValue(), `is`(false))
    }
    /**
     * Отлично - вы научились писать тесты сопрограмм,
     * которые используют TestCoroutineDispatcher способность приостанавливать и возобновлять выполнение сопрограмм.
     * Это дает вам больше контроля при написании тестов, требующих точного времени.
     */

    /**
     * Таким образом, общая стратегия тестирования обработки ошибок заключается в изменении ваших тестовых двойников,
     * чтобы вы могли «установить» их в состояние ошибки (или различные состояния ошибки, если у вас их несколько).
     * Затем вы можете написать тесты для этих состояний ошибки. Хорошая работа!
     */
    @Test
    fun loadStatisticsWhenTasksAreUnavailable_callErrorToDisplay() {
        // Make the repository return errors.
        // Сделайте так, чтобы репозиторий возвращал ошибки.
        tasksRepository.setReturnError(true)
        statisticsViewModel.refresh()

        // Then empty and error are true (which triggers an error message to be shown).
        // Тогда empty и error равны true (что вызывает отображение сообщения об ошибке).
        assertThat(statisticsViewModel.empty.getOrAwaitValue(), `is`(true))
        assertThat(statisticsViewModel.error.getOrAwaitValue(), `is`(true))
    }
}

/**
 *Если вам нужен еще более точный контроль времени, TestCouroutineDispatcher предоставьте и это.
 *  Проверять, выписываться:
 * advanceTimeBy
 * advanceUntilIdle
 * runCurrent
 */