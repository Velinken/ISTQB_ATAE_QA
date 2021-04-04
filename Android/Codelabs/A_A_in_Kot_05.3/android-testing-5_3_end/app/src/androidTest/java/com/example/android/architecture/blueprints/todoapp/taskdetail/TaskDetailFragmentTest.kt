package com.example.android.architecture.blueprints.todoapp.taskdetail

import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
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
import org.hamcrest.core.IsNot.not
import org.junit.*
import org.junit.runner.RunWith

// Как правило, если вы тестируете что-то визуальное, запускайте это как инструментальный тест.
// @MediumTest- Отмечает тест как интеграционный тест «среднего времени выполнения»
// (по сравнению с @SmallTest модульными тестами и сквозными @LargeTest тестами).
// Это поможет вам сгруппировать и выбрать размер теста для запуска.
//@RunWith(AndroidJUnit4::class)—Используется в любом классе, использующем AndroidX Test.
@MediumTest
@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
class TaskDetailFragmentTest {

    private lateinit var repository: TasksRepository

    /**
     * Добавьте настройку и метод удаления, чтобы настроить FakeAndroidTestRepository перед каждым тестом
     */
    @Before
    fun initRepository() {
        repository = FakeAndroidTestRepository()
        ServiceLocator.tasksRepository = repository
    }
    //  и очищать его после каждого теста.
    @After
    fun cleanupDb() = runBlockingTest {
        ServiceLocator.resetRepository()
    }

    // В этой задаче вы собираетесь запустить TaskDetailFragment с помощью библиотеки тестирования AndroidX.
    // FragmentScenario- это класс из AndroidX Test,
    // который обертывает фрагмент и дает вам прямой контроль над жизненным циклом фрагмента для тестирования.
    // Чтобы написать тесты для фрагментов, вы создаете FragmentScenario для тестируемого фрагмента ( TaskDetailFragment).
    @Test
    fun activeTaskDetails_DisplayedInUi() = runBlockingTest{
        // GIVEN - Add active (incomplete) task to the DB Создает задачу.
        val activeTask = Task("Active Task", "AndroidX Rocks", false)
        repository.saveTask(activeTask)

        // WHEN - Details fragment launched to display task
        // Создает a Bundle, который представляет аргументы фрагмента для задачи, которые передаются во фрагмент).
        val bundle = TaskDetailFragmentArgs(activeTask.id).toBundle()
        // Функция создает FragmentScenario, с этим пучком и темой.
        launchFragmentInContainer<TaskDetailFragment>(bundle, R.style.AppTheme)
       // Thread.sleep(2000)
    // Примечание. Предоставление темы необходимо, поскольку фрагменты обычно получают свою тематику из родительской активности.
    // При использовании FragmentScenario ваш фрагмент запускается внутри общего пустого действия,
    // чтобы он был должным образом изолирован от кода действия
    // (вы просто тестируете код фрагмента, а не связанную с ним активность).
    // Параметр темы позволяет указать правильную тему.

        // THEN - Task details are displayed on the screen
        // make sure that the title/description are both shown and correct
        onView(withId(R.id.task_detail_title_text)).check(matches(isDisplayed()))
        onView(withId(R.id.task_detail_title_text)).check(matches(withText("Active Task")))
        onView(withId(R.id.task_detail_description_text)).check(matches(isDisplayed()))
        onView(withId(R.id.task_detail_description_text)).check(matches(withText("AndroidX Rocks")))
        // and make sure the "active" checkbox is shown unchecked
        onView(withId(R.id.task_detail_complete_checkbox)).check(matches(isDisplayed()))
        onView(withId(R.id.task_detail_complete_checkbox)).check(matches(not(isChecked())))

    }
    // Почему нет данных? Это потому, что вы создали задачу, но не сохранили ее в репозиторий.
    // Он у вас есть FakeTestRepository, но вам нужен способ заменить реальный репозиторий на поддельный для вашего фрагмента.
// Вы сделаете это дальше! repository.saveTask(activeTask) см выше
// Как и раньше, вы должны увидеть фрагмент, но на этот раз, поскольку вы правильно настроили репозиторий,
// теперь он показывает информацию о задаче.

    @Test
    fun completedTaskDetails_DisplayedInUi() = runBlockingTest{
        // GIVEN - Add completed task to the DB
        val completedTask = Task("Completed Task", "AndroidX Rocks", true)
        repository.saveTask(completedTask)

        // WHEN - Details fragment launched to display task
        val bundle = TaskDetailFragmentArgs(completedTask.id).toBundle()
        launchFragmentInContainer<TaskDetailFragment>(bundle, R.style.AppTheme)
       // Thread.sleep(2000)
        // THEN - Task details are displayed on the screen
        // make sure that the title/description are both shown and correct
        onView(withId(R.id.task_detail_title_text)).check(matches(isDisplayed()))
        onView(withId(R.id.task_detail_title_text)).check(matches(withText("Completed Task")))
        onView(withId(R.id.task_detail_description_text)).check(matches(isDisplayed()))
        onView(withId(R.id.task_detail_description_text)).check(matches(withText("AndroidX Rocks")))
        // and make sure the "active" checkbox is shown unchecked
        onView(withId(R.id.task_detail_complete_checkbox)).check(matches(isDisplayed()))
        onView(withId(R.id.task_detail_complete_checkbox)).check(matches(isChecked()))
    }
}

/**
 * 10. Задача: написание первого интеграционного теста с эспрессо.
 *   Эспрессо помогает:
 * Взаимодействуйте с представлениями, например нажимая кнопки, перемещая панель или прокручивая экран вниз.
 * Утверждение, что определенные представления отображаются на экране или находятся в определенном состоянии (например, содержат определенный текст, или что установлен флажок и т. Д.).
 * Шаг 2. Отключите анимацию.
 * Тесты эспрессо проводятся на реальном устройстве и, следовательно, по своей природе являются инструментальными тестами.
 * Одна из возникающих проблем - это анимация:
 * если анимация задерживается и вы пытаетесь проверить, отображается ли представление на экране,
 * но оно все еще анимируется, Espresso может случайно не пройти тест.
 * Это может сделать тест на эспрессо нестабильным.
 *
 * onView(withId(R.id.task_detail_complete_checkbox)).perform(click()).check(matches(isChecked()))
 * Этот оператор находит представление флажка с идентификатором task_detail_complete_checkbox,
 * щелкает его, а затем утверждает, что он отмечен .
 * Большинство заявлений об эспрессо состоит из четырех частей:
 * Статический метод эспрессо
 * onView
 * onView представляет собой пример статического метода Espresso, который запускает оператор Espresso.
 * onView является одним из самых распространенных, но есть и другие варианты, например onData.
 * ViewMatcher
 * withId(R.id.task_detail_title_text)
 * withId- это пример объекта, ViewMatcher который получает представление по его идентификатору.
 * Существуют и другие сопоставители представлений, которые вы можете найти в документации .
 * ViewAction
 * perform(click())
 * perform Метод , который принимает ViewAction.
 * A ViewAction- это что-то, что можно сделать с представлением, например, здесь это щелчок по представлению.
 * ViewAssertion
 * check(matches(isChecked()))
 * check который занимает ViewAssertion.
 * ViewAssertions проверяет или утверждает что-то о представлении.
 * Чаще всего ViewAssertion вы будете использовать matchesassertion.
 * Чтобы завершить утверждение ViewMatcher, в данном случае используйте другое isChecked.
 *
 * Обратите внимание, что вы не всегда вызываете оба perform и check в операторе Espresso.
 * У вас могут быть операторы, которые просто утверждают, используя check или просто ViewAction используют perform.
 */
