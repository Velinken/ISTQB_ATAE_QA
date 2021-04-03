package com.example.android.architecture.blueprints.todoapp.tasks

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.example.android.architecture.blueprints.todoapp.R
import com.example.android.architecture.blueprints.todoapp.ServiceLocator
import com.example.android.architecture.blueprints.todoapp.data.Task
import com.example.android.architecture.blueprints.todoapp.data.source.TasksRepository
import com.example.android.architecture.blueprints.todoapp.data.source.FakeAndroidTestRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Before
import org.junit.runner.RunWith
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
/**
 * Integration test for the Task List screen.
 * Интеграционный тест для экрана списка задач.
 */
// TODO - Use FragmentScenario, see: https://github.com/android/android-test/issues/291

@RunWith(AndroidJUnit4::class)
@MediumTest
@ExperimentalCoroutinesApi
class TasksFragmentTest {

    private lateinit var repository: TasksRepository

    @Before
    fun initRepository() {
        repository = FakeAndroidTestRepository()
        ServiceLocator.tasksRepository = repository
    }

    @After
    fun cleanupDb() = runBlockingTest {
        ServiceLocator.resetRepository()
    }

    // Добавьте тест навигации, чтобы проверить, что при нажатии на задачу в списке задач
// вы переходите к правильному пути TaskDetailFragment.
    @Test
    fun clickTask_navigateToDetailFragmentOne() = runBlockingTest {
        repository.saveTask(Task("TITLE1", "DESCRIPTION1", false, "id1"))
        repository.saveTask(Task("TITLE2", "DESCRIPTION2", true, "id2"))

        // GIVEN - On the home screen
        val scenario = launchFragmentInContainer<TasksFragment>(Bundle(), R.style.AppTheme)

        // Используйте mock функцию Mockito, чтобы создать макет.
        val navController = mock(NavController::class.java)
        // Чтобы поиздеваться в Mockito, перейдите в класс, над которым хотите поиздеваться.

        // Сделайте свой новый макет фрагмента NavController.
        scenario.onFragment {
            Navigation.setViewNavController(it.view!!, navController)
        }
        // Thread.sleep(20000) //это приостановить выполнение на 20 секунд
        // WHEN - Click on the first list item
        // Добавьте код, чтобы щелкнуть по элементу RecyclerView с текстом «TITLE1».
        onView(withId(R.id.tasks_list))
            .perform(
                RecyclerViewActions.actionOnItem<RecyclerView.ViewHolder>(
                    hasDescendant(withText("TITLE1")), click()))
        // RecyclerViewActions является частью espresso-contrib библиотеки и позволяет выполнять действия Espresso в RecyclerView
        // Thread.sleep(20000) //это приостановить выполнение на 20 секунд

        // THEN - Verify that we navigate to the first detail screen
        // Убедитесь, что это navigate было вызвано, с правильным аргументом.
        verify(navController).navigate(
            TasksFragmentDirections.actionTasksFragmentToTaskDetailFragment( "id1")
            // Метод Mockito - verify это то, что делает это имитацией - вы можете подтвердить фиктивный метод,
            // navController вызываемый определенным методом
            // ( navigate) с параметром ( actionTasksFragmentToTaskDetailFragment с идентификатором «id1»).
        )
    }
    // Напишите тест, clickAddTaskButton_navigateToAddEditFragment который проверяет,
// что если вы нажмете + FAB, вы перейдете к файлу AddEditTaskFragment.
    @Test
    fun clickAddTaskButton_navigateToAddEditFragment() {
        // GIVEN - On the home screen
        val scenario = launchFragmentInContainer<TasksFragment>(Bundle(), R.style.AppTheme)
        val navController = mock(NavController::class.java)
        scenario.onFragment {
            Navigation.setViewNavController(it.view!!, navController)
        }

        // WHEN - Click on the "+" button
        onView(withId(R.id.add_task_fab)).perform(click())

        // THEN - Verify that we navigate to the add screen
        verify(navController).navigate(
            TasksFragmentDirections.actionTasksFragmentToAddEditTaskFragment(
                null, getApplicationContext<Context>().getString(R.string.add_task)
            )
        )
    }
}

    @After
    fun cleanupDb() = runBlockingTest {
        ServiceLocator.resetRepository()
    }

