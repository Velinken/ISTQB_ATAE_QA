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

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ActivityScenario.launch
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.hasSibling
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.example.android.architecture.blueprints.todoapp.R
import com.example.android.architecture.blueprints.todoapp.data.Task
import com.example.android.architecture.blueprints.todoapp.data.source.TasksRepository
// import com.example.android.architecture.blueprints.todoapp.di.TasksRepositoryModule
import com.example.android.architecture.blueprints.todoapp.launchFragmentInHiltContainer
import com.example.android.architecture.blueprints.todoapp.util.saveTaskBlocking
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
// import dagger.hilt.android.testing.UninstallModules
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.hamcrest.CoreMatchers.allOf
import org.hamcrest.Matcher
import org.hamcrest.core.IsNot.not
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import javax.inject.Inject

/**
 * Integration test for the Task List screen.
 * * Интеграционный тест для экрана списка задач.
 */
@RunWith(AndroidJUnit4::class)
@MediumTest
@ExperimentalCoroutinesApi
// @UninstallModules(TasksRepositoryModule::class)
@HiltAndroidTest
class TasksFragmentTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var repository: TasksRepository

    @Before
    fun init() {
        // Populate @Inject fields in test class
        // Заполнить поля @Inject в тестовом классе
        hiltRule.inject()
    }

    @Test
    fun displayTask_whenRepositoryHasData() {
        // GIVEN - One task already in the repository
        // GIVEN - одна задача уже находится в репозитории
        repository.saveTaskBlocking(Task("TITLE1", "DESCRIPTION1"))

        // WHEN - On startup
        // Когда-при запуске
        launchActivity()

        // THEN - Verify task is displayed on screen
        // Затем-Проверка задачи отображается на экране
        onView(withText("TITLE1")).check(matches(isDisplayed()))
    }

    @Test
    fun displayActiveTask() {
        repository.saveTaskBlocking(Task("TITLE1", "DESCRIPTION1"))

        launchActivity()

        onView(withText("TITLE1")).check(matches(isDisplayed()))

        onView(withId(R.id.menu_filter)).perform(click())
        onView(withText(R.string.nav_active)).perform(click())
        onView(withText("TITLE1")).check(matches(isDisplayed()))

        onView(withId(R.id.menu_filter)).perform(click())
        onView(withText(R.string.nav_completed)).perform(click())
        onView(withText("TITLE1")).check(matches(not(isDisplayed())))
    }
// fail 124
    @Test
    fun displayCompletedTask() {
        repository.saveTaskBlocking(Task("TITLE1", "DESCRIPTION1", true))

        launchActivity()

        onView(withText("TITLE1")).check(matches(isDisplayed()))

        onView(withId(R.id.menu_filter)).perform(click())
        onView(withText(R.string.nav_active)).perform(click())
        onView(withText("TITLE1")).check(matches(not(isDisplayed())))

        onView(withId(R.id.menu_filter)).perform(click())
        onView(withText(R.string.nav_completed)).perform(click())

        onView(withText("TITLE1")).check(matches(isDisplayed()))
    }
    //Fail
    @Test
    fun deleteOneTask() {
        repository.saveTaskBlocking(Task("TITLE1", "DESCRIPTION1"))

        launchActivity()

        // Open it in details view
        // Откройте его в подробном представлении
        onView(withText("TITLE1")).perform(click())

        // Click delete task in menu
        // Нажмите кнопку Удалить задачу в меню
        onView(withId(R.id.menu_delete)).perform(click())

        // Verify it was deleted
        // Убедитесь, что он был удален
        onView(withId(R.id.menu_filter)).perform(click())
        onView(withText(R.string.nav_all)).perform(click())
        onView(withText("TITLE1")).check(doesNotExist())
    }
// fail 164 через раз
    @Test
    fun deleteOneOfTwoTasks() {
        repository.saveTaskBlocking(Task("TITLE1", "DESCRIPTION1"))
        repository.saveTaskBlocking(Task("TITLE2", "DESCRIPTION2"))

        launchActivity()

        // Open it in details view
        // Откройте его в подробном представлении
        onView(withText("TITLE1")).perform(click())

        // Click delete task in menu
        // Нажмите кнопку Удалить задачу в меню
        onView(withId(R.id.menu_delete)).perform(click())

        // Verify it was deleted
        // Убедитесь, что он был удален
        onView(withId(R.id.menu_filter)).perform(click())
        onView(withText(R.string.nav_all)).perform(click())
        onView(withText("TITLE1")).check(doesNotExist())
        // but not the other one
        // но не тот, другой
        onView(withText("TITLE2")).check(matches(isDisplayed()))
    }

    @Test
    fun markTaskAsComplete() {
        repository.saveTaskBlocking(Task("TITLE1", "DESCRIPTION1"))

        launchActivity()

        // Mark the task as complete
        // Отметьте задачу как завершенную
        onView(checkboxWithText("TITLE1")).perform(click())

        // Verify task is shown as complete
        // Проверка задачи отображается как завершенная
        onView(withId(R.id.menu_filter)).perform(click())
        onView(withText(R.string.nav_all)).perform(click())
        onView(withText("TITLE1")).check(matches(isDisplayed()))
        onView(withId(R.id.menu_filter)).perform(click())
        onView(withText(R.string.nav_active)).perform(click())
        onView(withText("TITLE1")).check(matches(not(isDisplayed())))
        onView(withId(R.id.menu_filter)).perform(click())
        onView(withText(R.string.nav_completed)).perform(click())
        onView(withText("TITLE1")).check(matches(isDisplayed()))
    }

    @Test
    fun markTaskAsActive() {
        repository.saveTaskBlocking(Task("TITLE1", "DESCRIPTION1", true))

        launchActivity()

        // Mark the task as active
        // Отметьте задачу как активную
        onView(checkboxWithText("TITLE1")).perform(click())

        // Verify task is shown as active
        // Проверка задачи отображается как активная
        onView(withId(R.id.menu_filter)).perform(click())
        onView(withText(R.string.nav_all)).perform(click())
        onView(withText("TITLE1")).check(matches(isDisplayed()))
        onView(withId(R.id.menu_filter)).perform(click())
        onView(withText(R.string.nav_active)).perform(click())
        onView(withText("TITLE1")).check(matches(isDisplayed()))
        onView(withId(R.id.menu_filter)).perform(click())
        onView(withText(R.string.nav_completed)).perform(click())
        onView(withText("TITLE1")).check(matches(not(isDisplayed())))
    }
    // fail
    @Test
    fun showAllTasks() {
        // Add one active task and one completed task
        // Добавить одна задача и одно выполненное задание
        repository.saveTaskBlocking(Task("TITLE1", "DESCRIPTION1"))
        repository.saveTaskBlocking(Task("TITLE2", "DESCRIPTION2", true))

        launchActivity()

        // Verify that both of our tasks are shown
        // Убедитесь, что показаны обе наши задачи
        onView(withId(R.id.menu_filter)).perform(click())
        onView(withText(R.string.nav_all)).perform(click())
        onView(withText("TITLE1")).check(matches(isDisplayed()))
        onView(withText("TITLE2")).check(matches(isDisplayed()))
    }
// fail Похоже не всегда успевает убрать третью задачу и в 259 строке ее находит
    // отдельно иногда проходит иногда нет 50/50
    @Test
    fun showActiveTasks() {
        // Add 2 active tasks and one completed task
        // Добавить 2 активные задачи и одну завершенную задачу
        repository.saveTaskBlocking(Task("TITLE1", "DESCRIPTION1"))
        repository.saveTaskBlocking(Task("TITLE2", "DESCRIPTION2"))
        repository.saveTaskBlocking(Task("TITLE3", "DESCRIPTION3", true))

        launchActivity()

        // Verify that the active tasks (but not the completed task) are shown
        // Убедитесь, что отображаются активные задачи (но не завершенные задачи).
        onView(withId(R.id.menu_filter)).perform(click())
        onView(withText(R.string.nav_active)).perform(click())
        onView(withText("TITLE1")).check(matches(isDisplayed()))
        onView(withText("TITLE2")).check(matches(isDisplayed()))
        onView(withText("TITLE3")).check(doesNotExist())
    }
// fail 274 иногда часто
    @Test
    fun showCompletedTasks() {
        // Add one active task and 2 completed tasks
        // Добавить одну активную задачу и 2 завершенные задачи
        repository.saveTaskBlocking(Task("TITLE1", "DESCRIPTION1"))
        repository.saveTaskBlocking(Task("TITLE2", "DESCRIPTION2", true))
        repository.saveTaskBlocking(Task("TITLE3", "DESCRIPTION3", true))

        launchActivity()

        // Verify that the completed tasks (but not the active task) are shown
        // Убедитесь, что отображаются выполненные задачи (но не активная задача).
        onView(withId(R.id.menu_filter)).perform(click())
        onView(withText(R.string.nav_completed)).perform(click())
        onView(withText("TITLE1")).check(doesNotExist())
        onView(withText("TITLE2")).check(matches(isDisplayed()))
        onView(withText("TITLE3")).check(matches(isDisplayed()))
    }
    // fail
    @Test
    fun clearCompletedTasks() {
        // Add one active task and one completed task
        // Добавить одна задача и одно выполненное задание
        repository.saveTaskBlocking(Task("TITLE1", "DESCRIPTION1"))
        repository.saveTaskBlocking(Task("TITLE2", "DESCRIPTION2", true))

        launchActivity()

        // Click clear completed in menu
        // Нажмите кнопку Очистить завершено в меню
        openActionBarOverflowOrOptionsMenu(getApplicationContext())
        onView(withText(R.string.menu_clear)).perform(click())

        onView(withId(R.id.menu_filter)).perform(click())
        onView(withText(R.string.nav_all)).perform(click())
        // Verify that only the active task is shown
        // Убедитесь, что отображается только активная задача
        onView(withText("TITLE1")).check(matches(isDisplayed()))
        onView(withText("TITLE2")).check(doesNotExist())
    }
// fail 307
    @Test
    fun noTasks_AllTasksFilter_AddTaskViewVisible() {
        launchActivity()

        onView(withId(R.id.menu_filter)).perform(click())
        onView(withText(R.string.nav_all)).perform(click())

        // Verify the "You have no tasks!" text is shown
        // Убедитесь, что отображается текст "у вас нет задач!".
        onView(withText("You have no tasks!")).check(matches(isDisplayed()))
    }
    //Fail 319 через раз
    @Test
    fun noTasks_CompletedTasksFilter_AddTaskViewNotVisible() {
        launchActivity()

        onView(withId(R.id.menu_filter)).perform(click())
        onView(withText(R.string.nav_completed)).perform(click())

        // Verify the "You have no completed tasks!" text is shown
        // Убедитесь, что отображается текст "у вас нет завершенных заданий!".
        onView(withText("You have no completed tasks!")).check(matches((isDisplayed())))
    }

    //Fail 332 отдельно не сбоит
    @Test
    fun noTasks_ActiveTasksFilter_AddTaskViewNotVisible() {
        launchActivity()

        onView(withId(R.id.menu_filter)).perform(click())
        onView(withText(R.string.nav_active)).perform(click())

        // Verify the "You have no active tasks!" text is shown
        // Убедитесь, что отображается текст "у вас нет активных задач!".
        onView(withText("You have no active tasks!")).check(matches((isDisplayed())))
    }
    // fail 352
    @Test
    fun clickAddTaskButton_navigateToAddEditFragment() {
        // GIVEN - On the home screen
        // GIVEN - на главном экране
        val navController = mock(NavController::class.java)

        launchFragmentInHiltContainer<TasksFragment>(Bundle(), R.style.AppTheme) {
            Navigation.setViewNavController(this.view!!, navController)
        }

        // WHEN - Click on the "+" button
        // Когда-нажмите на кнопку"+"
        onView(withId(R.id.add_task_fab)).perform(click())

        // THEN - Verify that we navigate to the add screen
        // Затем-убедитесь, что мы переходим к экрану добавления
        verify(navController).navigate(
            TasksFragmentDirections.actionTasksFragmentToAddEditTaskFragment(
                null, getApplicationContext<Context>().getString(R.string.add_task)
            )
        )
    }

    private fun launchActivity(): ActivityScenario<TasksActivity>? {
        val activityScenario = launch(TasksActivity::class.java)
        activityScenario.onActivity { activity ->
            // Disable animations in RecyclerView
            // Отключить анимацию в RecyclerView
            (activity.findViewById(R.id.tasks_list) as RecyclerView).itemAnimator = null
        }
        return activityScenario
    }

    private fun checkboxWithText(text: String): Matcher<View> {
        return allOf(withId(R.id.complete_checkbox), hasSibling(withText(text)))
    }
}
