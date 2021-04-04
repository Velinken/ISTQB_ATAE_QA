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

import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isChecked
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.example.android.architecture.blueprints.todoapp.data.Task
import com.example.android.architecture.blueprints.todoapp.data.source.TasksRepository
import com.example.android.architecture.blueprints.todoapp.tasks.TasksActivity
import com.example.android.architecture.blueprints.todoapp.util.DataBindingIdlingResource
import com.example.android.architecture.blueprints.todoapp.util.EspressoIdlingResource
import com.example.android.architecture.blueprints.todoapp.util.monitorActivity
import kotlinx.coroutines.runBlocking
import org.hamcrest.core.IsNot.not
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Large End-to-End test for the tasks module.
 *
 * UI tests usually use [ActivityTestRule] but there's no API to perform an action before
 * each test. The workaround is to use `ActivityScenario.launch()` and `ActivityScenario.close()`.
 */
/**
 * А что насчет ActivityScenarioRule?
 * Обратите внимание, что есть программа, ActivityScenarioRule которая призывает launch и close для вас.
 * Как уже упоминалось, любая настройка состояния данных, такая как добавление задач в репозиторий,
 * должна произойти доActivityScenario.launch() вызова.
 * Вызов дополнительного установочного кода, такого как сохранение задач в репозиторий,
 * в настоящее время не поддерживается ActivityScenarioRule.
 * Поэтому мы решили не использовать ActivityScenarioRule и вместо этого вручную вызываем launch и close.
 */
@RunWith(AndroidJUnit4::class)
@LargeTest
class TasksActivityTest {

    private lateinit var repository: TasksRepository

    // An Idling Resource that waits for Data Binding to have no pending bindings
    private val dataBindingIdlingResource = DataBindingIdlingResource()

    @Before
    fun init() {
        repository =
            ServiceLocator.provideTasksRepository(
                getApplicationContext()
            )
        runBlocking {
            repository.deleteAllTasks()
        }
    }

    @After
    fun reset() {
        ServiceLocator.resetRepository()
    }

    /**
     * Idling resources tell Espresso that the app is idle or busy. This is needed when operations
     * are not scheduled in the main Looper (for example when executed on a different thread).
     * Ресурсы холостого хода говорят Эспрессо, что приложение простаивает или занято. Это необходимо при проведении операций
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


    @Test
    fun editTask() = runBlocking {
        // runBlocking используется для ожидания завершения всех приостановленных функций перед продолжением выполнения в блоке.
        // Обратите внимание, что мы используем runBlocking вместо runBlockingTest из-за ошибки .
        repository.saveTask(Task("TITLE1", "DESCRIPTION"))

        // ActivityScenario- это класс библиотеки AndroidX Testing,
        // который охватывает действие и дает вам прямой контроль над его жизненным циклом для тестирования.
        // Это похоже на FragmentScenario.
        // Start up Tasks screen
        // чтобы после запуска сценария действия вы использовали его monitorActivity
        // для связывания действия с файлом dataBindingIdlingResource.
        val activityScenario = ActivityScenario.launch(TasksActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        // Click on the task on the list and verify that all the data is correct
        onView(withText("TITLE1")).perform(click())
        onView(withId(R.id.task_detail_title_text)).check(matches(withText("TITLE1")))
        onView(withId(R.id.task_detail_description_text)).check(matches(withText("DESCRIPTION")))
        onView(withId(R.id.task_detail_complete_checkbox)).check(matches(not(isChecked())))

        // Click on the edit button, edit, and save
        onView(withId(R.id.edit_task_fab)).perform(click())
        onView(withId(R.id.add_task_title_edit_text)).perform(replaceText("NEW TITLE"))
        onView(withId(R.id.add_task_description_edit_text)).perform(replaceText("NEW DESCRIPTION"))
        onView(withId(R.id.save_task_fab)).perform(click())

        // Verify task is displayed on screen in the task list.
        onView(withText("NEW TITLE")).check(matches(isDisplayed()))
        // Verify previous task is not displayed
        onView(withText("TITLE1")).check(doesNotExist())

        // Make sure the activity is closed before resetting the db:
        activityScenario.close()
    }
    // Выполните этот тест пять раз. Обратите внимание, что тест нестабильный, то есть иногда он проходит, а иногда - нет:
    // Причина того, что тесты иногда терпят неудачу, - это проблема синхронизации и синхронизации.
    // Espresso синхронизирует действия пользовательского интерфейса и соответствующие изменения в пользовательском интерфейсе.
    // Например, скажем, вы приказываете Espresso нажать кнопку от вашего имени,
    // а затем проверяете, видны ли определенные представления:
    // Espresso будет ждать появления новых представлений после щелчка на шаге 1,
    // прежде чем проверять, есть ли текст «Следующий экран» на шаге 2.


    @Test
    fun createOneTask_deleteTask() {

        // start up Tasks screen
        val activityScenario = ActivityScenario.launch(TasksActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        // Add active task
        onView(withId(R.id.add_task_fab)).perform(click())
      //  Thread.sleep(2000)
        onView(withId(R.id.add_task_title_edit_text))
            .perform(typeText("TITLE1"), closeSoftKeyboard())
      //  Thread.sleep(2000)
        onView(withId(R.id.add_task_description_edit_text)).perform(typeText("DESCRIPTION"))
      //  Thread.sleep(2000)
        onView(withId(R.id.save_task_fab)).perform(click())
      //  Thread.sleep(2000)
        // Open it in details view
        onView(withText("TITLE1")).perform(click())
      //  Thread.sleep(2000)
        // Click delete task in menu
        onView(withId(R.id.menu_delete)).perform(click())
      //  Thread.sleep(2000)

        // Verify it was deleted
        onView(withId(R.id.menu_filter)).perform(click())
      //  Thread.sleep(2000)
        onView(withText(R.string.nav_all)).perform(click())
      //  Thread.sleep(2000)
        onView(withText("TITLE1")).check(doesNotExist())
      //  Thread.sleep(2000)
        // Make sure the activity is closed before resetting the db:
        // Перед сбросом БД убедитесь, что действие закрыто:
        activityScenario.close()
    }
}

/**
 * Обратите внимание, что в этом сквозном тесте вы вообще не проверяете
 * интеграцию с репозиторием, контроллером навигации или любыми другими компонентами.
 * Это так называемый тест черного ящика.
 * Предполагается, что тест не должен знать, как что-то реализовано внутри, только результат для данного ввода.
 */

/**
 * Однако бывают ситуации, когда встроенный механизм синхронизации Espresso не знает,
 * нужно ли ждать достаточно долго для обновления представления.
 * Например, когда вам нужно загрузить некоторые данные для представления, Espresso не знает,
 * когда эта загрузка данных завершена.
 * Espresso также не знает, когда библиотека привязки данных все еще обновляет представление.
 * В ситуациях, когда Espresso не может определить, занято ли приложение обновлением пользовательского интерфейса или нет,
 * вы можете использовать механизм синхронизации ресурсов в режиме ожидания.
 * Это способ явно указать Espresso,
 * когда приложение находится в режиме ожидания (это означает, что Espresso должен ждать)
 * или нет (это означает, что Espresso должен продолжать взаимодействовать с приложением и проверять его).
 * Общий способ использования ресурсов холостого хода выглядит следующим образом:
 * Создайте ресурс холостого хода или его подкласс как одноэлемент в коде вашего приложения.
 * В коде вашего приложения (а не в тестовом коде) добавьте логику для отслеживания того,
 *  находится ли приложение в режиме ожидания или нет, путем изменения состояния IdlingResource на бездействие или нет.
 * Звоните IdlingRegistry.getInstance().register перед каждым тестом, чтобы зарегистрировать IdlingResource.
 *  После регистрации IdlingResourceEspresso будет ждать, пока он не станет свободным,
 *  прежде чем перейти к следующему оператору Espresso.
 * Звоните IdlingRegistry.getInstance().unregister после каждого теста, чтобы отменить регистрацию IdlingResource.
 *
 * Примечание. Наличие тестового кода в вашем приложении является необычным.
 * Чтобы узнать больше о причинах и методах удаления кода ресурса простоя из рабочего кода приложения,
 * ознакомьтесь с тестированием Android с помощью ресурсов ожидания Espresso и проверкой точности
 */

/**
 * Помните, что оба countingIdlingResource и dataBindingIdlingResource контролируют код вашего приложения,
 * следя за тем, простаивает оно или нет.
 * Регистрируя эти ресурсы в ваших тестах, когда какой-либо из ресурсов занят,
 * Espresso будет ждать, пока они не станут свободными, прежде чем перейти к следующей команде.
 * Это означает, что если ваш countingIdlingResource счетчик больше нуля или есть ожидающие макеты привязки данных,
 * Espresso будет ждать.
 */