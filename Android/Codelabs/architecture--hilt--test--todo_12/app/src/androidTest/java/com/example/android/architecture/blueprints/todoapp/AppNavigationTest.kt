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

import android.view.Gravity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.DrawerActions.open
import androidx.test.espresso.contrib.DrawerMatchers.isClosed
import androidx.test.espresso.contrib.DrawerMatchers.isOpen
import androidx.test.espresso.contrib.NavigationViewActions.navigateTo
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withContentDescription
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.example.android.architecture.blueprints.todoapp.data.Task
import com.example.android.architecture.blueprints.todoapp.data.source.TasksRepository
import com.example.android.architecture.blueprints.todoapp.di.TasksRepositoryModule
import com.example.android.architecture.blueprints.todoapp.tasks.TasksActivity
import com.example.android.architecture.blueprints.todoapp.util.EspressoIdlingResource
import com.example.android.architecture.blueprints.todoapp.util.saveTaskBlocking
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

/**
 * Tests for the [DrawerLayout] layout component in [TasksActivity] which manages
 * navigation within the app.
 * Тесты для компонента макета [DrawerLayout] в [Tasks Activity], который управляет
 * навигация внутри приложения.
 *
 * UI tests usually use [ActivityTestRule] but there's no API to perform an action before
 * each test. The workaround is to use `ActivityScenario.launch()` and `ActivityScenario.close()`.
 * Тесты пользовательского интерфейса обычно используют [ActivityTestRule], но до этого нет API для выполнения действия
 * каждое испытание. Обходной путь заключается в использовании сценария деятельности.запуск()` и `активность сценарию.закрыть()`.
 */
@RunWith(AndroidJUnit4::class)
@LargeTest
@UninstallModules(TasksRepositoryModule::class)
@HiltAndroidTest
class AppNavigationTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var tasksRepository: TasksRepository

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
     * Idling resources tell Espresso that the app is idle or busy. This is needed when operations
     * are not scheduled in the main Looper (for example when executed on a different thread).
     * Простаивания ресурсов Эспрессо сказать, что приложение находится в состоянии простоя или занят.
     * Это необходимо при проведении операций
     * не планируются в основном Петлителе (например, при выполнении в другом потоке).
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
// сбоит один раз сначала
    @Test
    fun drawerNavigationFromTasksToStatistics() {
        // start up Tasks screen
        // экран запуска задач
        val activityScenario = ActivityScenario.launch(TasksActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        onView(withId(R.id.drawer_layout))
            .check(matches(isClosed(Gravity.START))) // Left Drawer should be closed. Левый ящик должен быть закрыт.
            .perform(open()) // Open Drawer Открыть Ящик

        // Start statistics screen.
        // Запустить экран статистики.
        onView(withId(R.id.nav_view))
            .perform(navigateTo(R.id.statistics_fragment_dest))

        // Check that statistics screen was opened.
        // Убедитесь, что открыт экран статистики.
        onView(withId(R.id.statistics_layout)).check(matches(isDisplayed()))

        onView(withId(R.id.drawer_layout))
            .check(matches(isClosed(Gravity.START))) // Left Drawer should be closed. Левый ящик должен быть закрыт.
            .perform(open()) // Open Drawer Открыть Ящик

        // Start tasks screen.
        // Экран запуска задач
        onView(withId(R.id.nav_view))
            .perform(navigateTo(R.id.tasks_fragment_dest))

        // Check that tasks screen was opened.
        // Убедитесь, что открыт экран задач.
        onView(withId(R.id.tasks_container_layout)).check(matches(isDisplayed()))
        // When using ActivityScenario.launch, always call close()
        // При использовании сценариев деятельности.запуск, всегда вызывать close()
        activityScenario.close()
    }

    @Test
    fun tasksScreen_clickOnAndroidHomeIcon_OpensNavigation() {
        // start up Tasks screen
        // экран запуска задач
        val activityScenario = ActivityScenario.launch(TasksActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        // Check that left drawer is closed at startup
        // Убедитесь, что левый ящик закрыт при запуске
        onView(withId(R.id.drawer_layout))
            .check(matches(isClosed(Gravity.START))) // Left Drawer should be closed. Левый ящик должен быть закрыт.

        // Open Drawer Открыть Ящик
        onView(
            withContentDescription(
                activityScenario
                    .getToolbarNavigationContentDescription()
            )
        ).perform(click())

        // Check if drawer is open
        // Проверьте, открыт ли ящик
        onView(withId(R.id.drawer_layout))
            .check(matches(isOpen(Gravity.START))) // Left drawer is open open. Левый ящик открыт.
        // When using ActivityScenario.launch, always call close()
        // // При использовании сценариев деятельности.запуск, всегда вызывать close()
        activityScenario.close()
    }

    @Test
    fun statsScreen_clickOnAndroidHomeIcon_OpensNavigation() {
        // start up Tasks screen
        // экран запуска задач
        val activityScenario = ActivityScenario.launch(TasksActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        // When the user navigates to the stats screen
        // Когда пользователь переходит на экран статистики
        activityScenario.onActivity {
            it.findNavController(R.id.nav_host_fragment).navigate(R.id.statistics_fragment_dest)
        }

        // Then check that left drawer is closed at startup
        // Затем убедитесь, что левый ящик закрыт при запуске
        onView(withId(R.id.drawer_layout))
            .check(matches(isClosed(Gravity.START))) // Left Drawer should be closed. Левый ящик должен быть закрыт.

        // When the drawer is opened
        // Когда ящик открыт
        onView(
            withContentDescription(
                activityScenario
                    .getToolbarNavigationContentDescription()
            )
        ).perform(click())

        // Then check that the drawer is open
        // Затем убедитесь, что ящик открыт
        onView(withId(R.id.drawer_layout))
            .check(matches(isOpen(Gravity.START))) // Left drawer is open open.Левый ящик открыт настежь.
        // When using ActivityScenario.launch, always call close()
        // При использовании сценариев деятельности.запуск, всегда вызывать close()
        activityScenario.close()
    }

    @Test
    fun taskDetailScreen_doubleUIBackButton() {
        val task = Task("UI <- button", "Description")
        tasksRepository.saveTaskBlocking(task)

        // start up Tasks screen
        // экран запуска задач
        val activityScenario = ActivityScenario.launch(TasksActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        // Click on the task on the list
        // Нажмите на задачу в списке
        onView(withText("UI <- button")).perform(click())
        // Click on the edit task button
        // Нажмите на кнопку Изменить задачу
        onView(withId(R.id.edit_task_fab)).perform(click())

        // Confirm that if we click "<-" once, we end up back at the task details page
        // Подтвердите, что если мы нажмем "<-" один раз, то вернемся на страницу сведений о задаче
        onView(
            withContentDescription(
                activityScenario
                    .getToolbarNavigationContentDescription()
            )
        ).perform(click())
        onView(withId(R.id.task_detail_title_text)).check(matches(isDisplayed()))

        // Confirm that if we click "<-" a second time, we end up back at the home screen
        // Подтвердите, что если мы нажмем "<-" во второй раз, то снова окажемся на главном экране
        onView(
            withContentDescription(
                activityScenario
                    .getToolbarNavigationContentDescription()
            )
        ).perform(click())
        onView(withId(R.id.tasks_container_layout)).check(matches(isDisplayed()))
        // When using ActivityScenario.launch, always call close()
        // При использовании сценариев деятельности.запуск, всегда вызывать close()
        activityScenario.close()
    }

    @Test
    fun taskDetailScreen_doubleBackButton() {
        val task = Task("Back button", "Description")
        tasksRepository.saveTaskBlocking(task)

        // start up Tasks screen
        // экран запуска задач
        val activityScenario = ActivityScenario.launch(TasksActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        // Click on the task on the list
        // Нажмите на задачу в списке
        onView(withText("Back button")).perform(click())
        // Click on the edit task button
        // Нажмите на кнопку Изменить задачу
        onView(withId(R.id.edit_task_fab)).perform(click())

        // Confirm that if we click back once, we end up back at the task details page
        // Подтвердите, что если мы нажмем кнопку назад один раз, то окажемся на странице сведений о задаче
        pressBack()
        onView(withId(R.id.task_detail_title_text)).check(matches(isDisplayed()))

        // Confirm that if we click back a second time, we end up back at the home screen
        // Подтвердите, что если мы нажмем назад во второй раз, то снова окажемся на главном экране
        pressBack()
        onView(withId(R.id.tasks_container_layout)).check(matches(isDisplayed()))
        // When using ActivityScenario.launch, always call close()
        // При использовании сценариев деятельности.запуск, всегда вызывать close()
        activityScenario.close()
    }
}
