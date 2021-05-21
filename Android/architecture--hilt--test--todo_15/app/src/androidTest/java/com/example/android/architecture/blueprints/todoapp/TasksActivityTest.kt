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
package com.example.android.architecture.blueprints.todoapp
// сбоит 2 раза после onviewcreated
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.hasSibling
import androidx.test.espresso.matcher.ViewMatchers.isChecked
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withContentDescription
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.example.android.architecture.blueprints.todoapp.R.string
import com.example.android.architecture.blueprints.todoapp.data.Task
import com.example.android.architecture.blueprints.todoapp.data.source.TasksRepository
import com.example.android.architecture.blueprints.todoapp.di.TasksRepositoryModule
import com.example.android.architecture.blueprints.todoapp.tasks.TasksActivity
import com.example.android.architecture.blueprints.todoapp.util.EspressoIdlingResource
import com.example.android.architecture.blueprints.todoapp.util.saveTaskBlocking
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
// import dagger.hilt.android.testing.UninstallModules
import org.hamcrest.Matchers.allOf
import org.hamcrest.core.IsNot.not
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

/**
 * Large End-to-End test for the tasks module.
 * Большой сквозной тест для модуля задач.
 *
 * UI tests usually use [ActivityTestRule] but there's no API to perform an action before each test.
 * The workaround is to use `ActivityScenario.launch()` and `ActivityScenario.close()`.
 * Тесты пользовательского интерфейса обычно используют [ActivityTestRule], но нет API для выполнения действия перед каждым тестом.
 * Решением проблемы является использование сценариев деятельности.запуск()` и `активность сценарию.закрыть()`.
 */
@RunWith(AndroidJUnit4::class)
@LargeTest
// @UninstallModules(TasksRepositoryModule::class)
@HiltAndroidTest
class TasksActivityTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var repository: TasksRepository

    // An Idling Resource that waits for Data Binding to have no pending bindings
    // Ресурс холостого хода, ожидающий привязки данных, не имеет ожидающих Привязок
    private val dataBindingIdlingResource = DataBindingIdlingResource()

    @Before
    fun init() {
        // Populate @Inject fields in test class
        // Заполнить поля @Inject в тестовом классе
        hiltRule.inject()
    }

    /**
     * Idling resources tell Espresso that the app is idle or busy.
     * This is needed when operations are not scheduled in the main Looper (for example when executed on a different thread).
     * Простаивания ресурсов Эспрессо сказать, что приложение находится в состоянии простоя или занят.
     * Это необходимо, когда операции не запланированы в основном Петлителе (например, при выполнении в другом потоке).
     */
    @Before
    fun registerIdlingResource() {
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
        IdlingRegistry.getInstance().register(dataBindingIdlingResource)
    }

    /**
     * Unregister your Idling Resource so it can be garbage collected and does not leak any memory.
     * Отмените регистрацию вашего ресурса холостого хода, чтобы он мог быть собран мусором и не пропускал никакой памяти.
     */
    @After
    fun unregisterIdlingResource() {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
        IdlingRegistry.getInstance().unregister(dataBindingIdlingResource)
    }
// fail  119 иногда
    @Test
    fun editTask() {
        repository.saveTaskBlocking(Task("TITLE1", "DESCRIPTION"))

        // start up Tasks screen
        // экран запуска задач
        val activityScenario = ActivityScenario.launch(TasksActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        // Click on the task on the list and verify that all the data is correct
        // Нажмите на задачу в списке и убедитесь, что все данные верны
        onView(withText("TITLE1")).perform(click())
        onView(withId(R.id.task_detail_title_text)).check(matches(withText("TITLE1")))
        onView(withId(R.id.task_detail_description_text)).check(matches(withText("DESCRIPTION")))
        onView(withId(R.id.task_detail_complete_checkbox)).check(matches(not(isChecked())))

        // Click on the edit button, edit, and save
        // Нажмите на кнопку Изменить, отредактируйте и сохраните
        onView(withId(R.id.edit_task_fab)).perform(click())
        onView(withId(R.id.add_task_title_edit_text)).perform(replaceText("NEW TITLE"))
        onView(withId(R.id.add_task_description_edit_text)).perform(replaceText("NEW DESCRIPTION"))
        onView(withId(R.id.save_task_fab)).perform(click())

        // Verify task is displayed on screen in the task list.
        // Проверка задачи отображается на экране в списке задач.
        onView(withText("NEW TITLE")).check(matches(isDisplayed()))
        // Verify previous task is not displayed
        // Проверка того, что предыдущая задача не отображается
        onView(withText("TITLE1")).check(doesNotExist())
        // Make sure the activity is closed before resetting the db:
        // Перед сбросом БД убедитесь, что действие закрыто:
        activityScenario.close()
    }
// сбоит 2 раза после onviewcreated Исправил слушателя +
    @Test
    fun createOneTask_deleteTask() {

        // start up Tasks screen
        // экран запуска задач
        val activityScenario = ActivityScenario.launch(TasksActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        // Add active task
        // Добавить активную задачу
        onView(withId(R.id.add_task_fab)).perform(click())
        onView(withId(R.id.add_task_title_edit_text))
            .perform(typeText("TITLE1"), closeSoftKeyboard())
        onView(withId(R.id.add_task_description_edit_text)).perform(typeText("DESCRIPTION"))
        onView(withId(R.id.save_task_fab)).perform(click())

        // Open it in details view
        // Откройте его в подробном представлении
        onView(withText("TITLE1")).perform(click())
        // Click delete task in menu
        // Нажмите кнопку Удалить задачу в меню
        onView(withId(R.id.menu_delete)).perform(click())

        // Verify it was deleted
        // Убедитесь, что он был удален
        onView(withId(R.id.menu_filter)).perform(click())
        onView(withText(string.nav_all)).perform(click())
        onView(withText("TITLE1")).check(doesNotExist())
        // Make sure the activity is closed before resetting the db:
        // Перед сбросом БД убедитесь, что действие закрыто:
        activityScenario.close()
    }
// fail 187 через раз
    @Test
    fun createTwoTasks_deleteOneTask() {
        repository.saveTaskBlocking(Task("TITLE1", "DESCRIPTION"))
        repository.saveTaskBlocking(Task("TITLE2", "DESCRIPTION"))

        // start up Tasks screen
        // экран запуска задач
        val activityScenario = ActivityScenario.launch(TasksActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        // Open the second task in details view
        // Откройте вторую задачу в подробном представлении
        onView(withText("TITLE2")).perform(click())
        // Click delete task in menu
        // Нажмите кнопку Удалить задачу в меню
        onView(withId(R.id.menu_delete)).perform(click())

        // Verify only one task was deleted
        // Убедитесь, что удалена только одна задача
        onView(withId(R.id.menu_filter)).perform(click())
        onView(withText(string.nav_all)).perform(click())
        onView(withText("TITLE1")).check(matches(isDisplayed()))
        onView(withText("TITLE2")).check(doesNotExist())
        // Make sure the activity is closed before resetting the db:
        // Перед сбросом БД убедитесь, что действие закрыто:
        activityScenario.close()
    }
// fail 217 не всегда
    @Test
    fun markTaskAsCompleteOnDetailScreen_taskIsCompleteInList() {
        // Add 1 active task
        // Добавить 1 активную задачу
        val taskTitle = "COMPLETED"
        repository.saveTaskBlocking(Task(taskTitle, "DESCRIPTION"))

        // start up Tasks screen
        // экран запуска задач
        val activityScenario = ActivityScenario.launch(TasksActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        // Click on the task on the list
        // Нажмите на задачу в списке
        onView(withText(taskTitle)).perform(click())

        // Click on the checkbox in task details screen
        // Нажмите на флажок в окне сведений о задаче
        onView(withId(R.id.task_detail_complete_checkbox)).perform(click())

        // Click on the navigation up button to go back to the list
        // Нажмите на кнопку навигации вверх, чтобы вернуться к списку
        onView(
            withContentDescription(
                activityScenario.getToolbarNavigationContentDescription()
            )
        ).perform(click())

        // Check that the task is marked as completed
        // Убедитесь, что задача помечена как выполненная
        onView(allOf(withId(R.id.complete_checkbox), hasSibling(withText(taskTitle))))
            .check(matches(isChecked()))
        // Make sure the activity is closed before resetting the db:
        // Перед сбросом БД убедитесь, что действие закрыто:
        activityScenario.close()
    }

    @Test
    fun markTaskAsActiveOnDetailScreen_taskIsActiveInList() {
        // Add 1 completed task
        // Добавить 1 выполненную задачу:
        val taskTitle = "ACTIVE"
        repository.saveTaskBlocking(Task(taskTitle, "DESCRIPTION", true))

        // start up Tasks screen
        // экран запуска задач
        val activityScenario = ActivityScenario.launch(TasksActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        // Click on the task on the list
        // Нажмите на задачу в списке
        onView(withText(taskTitle)).perform(click())
        // Click on the checkbox in task details screen
        // Нажмите на флажок в окне сведений о задаче
        onView(withId(R.id.task_detail_complete_checkbox)).perform(click())

        // Click on the navigation up button to go back to the list
        // Нажмите на кнопку навигации вверх, чтобы вернуться к списку
        onView(
            withContentDescription(
                activityScenario.getToolbarNavigationContentDescription()
            )
        ).perform(click())

        // Check that the task is marked as active
        // Убедитесь, что задача помечена как активная
        onView(allOf(withId(R.id.complete_checkbox), hasSibling(withText(taskTitle))))
            .check(matches(not(isChecked())))
        // Make sure the activity is closed before resetting the db:
        // Перед сбросом БД убедитесь, что действие закрыто:
        activityScenario.close()
    }

    @Test
    fun markTaskAsCompleteAndActiveOnDetailScreen_taskIsActiveInList() {
        // Add 1 active task
        // Добавить 1 активную задачу
        val taskTitle = "ACT-COMP"
        repository.saveTaskBlocking(Task(taskTitle, "DESCRIPTION"))

        // start up Tasks screen
        // экран запуска задач
        val activityScenario = ActivityScenario.launch(TasksActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        // Click on the task on the list
        // Нажмите на задачу в списке
        onView(withText(taskTitle)).perform(click())
        // Click on the checkbox in task details screen
        // Нажмите на флажок в окне сведений о задаче
        onView(withId(R.id.task_detail_complete_checkbox)).perform(click())
        // Click again to restore it to original state
        // Нажмите еще раз, чтобы восстановить его в исходное состояние
        onView(withId(R.id.task_detail_complete_checkbox)).perform(click())

        // Click on the navigation up button to go back to the list
        // Нажмите на кнопку навигации вверх, чтобы вернуться к списку
        onView(
            withContentDescription(
                activityScenario.getToolbarNavigationContentDescription()
            )
        ).perform(click())

        // Check that the task is marked as active
        // Убедитесь, что задача помечена как активная
        onView(allOf(withId(R.id.complete_checkbox), hasSibling(withText(taskTitle))))
            .check(matches(not(isChecked())))
        // Make sure the activity is closed before resetting the db:
        // Перед сбросом БД убедитесь, что действие закрыто:
        activityScenario.close()
    }
    //Fail 329 отдельно не сбоит
    @Test
    fun markTaskAsActiveAndCompleteOnDetailScreen_taskIsCompleteInList() {
        // Add 1 completed task
        // Добавить 1 выполненную задачу
        val taskTitle = "COMP-ACT"
        repository.saveTaskBlocking(Task(taskTitle, "DESCRIPTION", true))

        // start up Tasks screen
        // экран запуска задач
        val activityScenario = ActivityScenario.launch(TasksActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        // Click on the task on the list
        // Нажмите на задачу в списке
        onView(withText(taskTitle)).perform(click())
        // Click on the checkbox in task details screen
        // Нажмите на флажок в окне сведений о задаче
        onView(withId(R.id.task_detail_complete_checkbox)).perform(click())
        // Click again to restore it to original state
        // Нажмите еще раз, чтобы восстановить его в исходное состояние
        onView(withId(R.id.task_detail_complete_checkbox)).perform(click())

        // Click on the navigation up button to go back to the list
        // Нажмите на кнопку навигации вверх, чтобы вернуться к списку
        onView(
            withContentDescription(
                activityScenario.getToolbarNavigationContentDescription()
            )
        ).perform(click())

        // Check that the task is marked as active
        // Убедитесь, что задача помечена как активная
        onView(allOf(withId(R.id.complete_checkbox), hasSibling(withText(taskTitle))))
            .check(matches(isChecked()))
        // Make sure the activity is closed before resetting the db:
        // Перед сбросом БД убедитесь, что действие закрыто:
        activityScenario.close()
    }
// Fail // сбоит 2 раза после onviewcreated Исправил слушателя +
    @Test
    fun createTask() {
        // start up Tasks screen
        // экран запуска задач
        val activityScenario = ActivityScenario.launch(TasksActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        // Click on the "+" button, add details, and save
        // Нажмите на кнопку"+", добавьте детали и сохраните
        onView(withId(R.id.add_task_fab)).perform(click())
        onView(withId(R.id.add_task_title_edit_text))
            .perform(typeText("title"), closeSoftKeyboard())
        onView(withId(R.id.add_task_description_edit_text)).perform(typeText("description"))
        onView(withId(R.id.save_task_fab)).perform(click())

        // Then verify task is displayed on screen
        // Затем проверьте, что задача отображается на экране
        onView(withText("title")).check(matches(isDisplayed()))
        // Make sure the activity is closed before resetting the db:
        // Перед сбросом БД убедитесь, что действие закрыто:
        activityScenario.close()
    }
}
