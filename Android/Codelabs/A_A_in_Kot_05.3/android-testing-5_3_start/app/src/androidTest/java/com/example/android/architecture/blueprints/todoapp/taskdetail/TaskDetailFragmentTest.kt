package com.example.android.architecture.blueprints.todoapp.taskdetail

import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isChecked
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import org.hamcrest.core.IsNot.not
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.example.android.architecture.blueprints.todoapp.R
import com.example.android.architecture.blueprints.todoapp.ServiceLocator
import com.example.android.architecture.blueprints.todoapp.data.Task
import com.example.android.architecture.blueprints.todoapp.data.source.FakeAndroidTestRepository
import com.example.android.architecture.blueprints.todoapp.data.source.TasksRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@MediumTest
@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
// Это класс для тестирования фрагмента
class TaskDetailFragmentTest {

    private lateinit var repository: TasksRepository

    @Test
    fun activeTaskDetails_DisplayedInUi() = runBlockingTest {
        // GIVEN - Add active (incomplete) task to the DB
        // Создаем задачу типа Башня в Пизе с описанием, что бы высветить ее во фрагменте
        //val activeTask = Task("Active Task", "AndroidX Rocks", false)
        val testTask = Task("Mow the grass", "Hire a tractor, trace and pay", false)
        repository.saveTask(testTask)

        // WHEN - Details fragment launched to display task
        // Франменту при вызове надо передать ID, тогда будет вызвана задача
        val bundle = TaskDetailFragmentArgs(testTask.id).toBundle()
        // Фрагмент загружает, он вывестится на экране для тестового случая.
        launchFragmentInContainer<TaskDetailFragment>(bundle, R.style.AppTheme)
        // Thread.sleep(20000) //это приостановить выполнение на 20 секунд
// THEN - Task details are displayed on the screen
        // make sure that the title/description are both shown and correct
        onView(withId(R.id.task_detail_title_text)).check(matches(isDisplayed()))
        onView(withId(R.id.task_detail_title_text)).check(matches(withText("Mow the grass")))
        onView(withId(R.id.task_detail_description_text)).check(matches(isDisplayed()))
        onView(withId(R.id.task_detail_description_text)).check(matches(withText("Hire a tractor, trace and pay")))
        // and make sure the "active" checkbox is shown unchecked
        onView(withId(R.id.task_detail_complete_checkbox)).check(matches(isDisplayed()))
        onView(withId(R.id.task_detail_complete_checkbox)).check(matches(not(isChecked())))
    }

    @Test
    fun completedTaskDetails_DisplayedInUi() = runBlockingTest {
        // GIVEN - Add active (incomplete) task to the DB
        // Создаем задачу типа Башня в Пизе с описанием, что бы высветить ее во фрагменте
        //val activeTask = Task("Active Task", "AndroidX Rocks", false)
        val testTask = Task("Mow the grass", "Hire a tractor, trace and pay", true)
        repository.saveTask(testTask)

        // WHEN - Details fragment launched to display task
        // Франменту при вызове надо передать ID, тогда будет вызвана задача
        val bundle = TaskDetailFragmentArgs(testTask.id).toBundle()
        // Фрагмент загружает, он вывестится на экране для тестового случая.
        launchFragmentInContainer<TaskDetailFragment>(bundle, R.style.AppTheme)
        //Thread.sleep(20000) //это приостановить выполнение на 20 секунд
// THEN - Task details are displayed on the screen
        // make sure that the title/description are both shown and correct
        onView(withId(R.id.task_detail_title_text)).check(matches(isDisplayed()))
        onView(withId(R.id.task_detail_title_text)).check(matches(withText("Mow the grass")))
        onView(withId(R.id.task_detail_description_text)).check(matches(isDisplayed()))
        onView(withId(R.id.task_detail_description_text)).check(matches(withText("Hire a tractor, trace and pay")))
        // and make sure the "active" checkbox is shown unchecked
        onView(withId(R.id.task_detail_complete_checkbox)).check(matches(isDisplayed()))
        onView(withId(R.id.task_detail_complete_checkbox)).check(matches(isChecked()))
    }

    @Before
    fun initRepository() {
        repository = FakeAndroidTestRepository()
        ServiceLocator.tasksRepository = repository
    }

    @After
    fun cleanupDb() = runBlockingTest {
        ServiceLocator.resetRepository()
    }
}