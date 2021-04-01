package com.example.android.architecture.blueprints.todoapp.tasks
// Это теперь наш новый тест версии 2
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.android.architecture.blueprints.todoapp.Event
import com.example.android.architecture.blueprints.todoapp.getOrAwaitValue
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TasksViewModelTest {

    // Subject under test
    private lateinit var tasksViewModel: TasksViewModel

    // что бы все выполнялось одно за другим, а не в параллель
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setupViewModel() {
        tasksViewModel = TasksViewModel(ApplicationProvider.getApplicationContext())
    }

    @Test
    fun addNewTask_setsNewTaskEvent() {
        // Given a fresh ViewModel Создаем экземпляр TasksViewModel, которую будем тестировать.
     //  val tasksViewModel = TasksViewModel(ApplicationProvider.getApplicationContext())

        // When adding a new task Это вызывам добавление новой задачи
        tasksViewModel.addNewTask()
        // В TaskDetailViewModel val task: LiveData<Task?> = _task
        // Он (список задач) LiveData и он разорется наблюдателям, что создалась новая задача или нет (молчит).

        // Теперь наша тестовая задача поставить над ним наблюдатель и спросить слышит он крик task: или нет.
        // если слышит, значит задача добавилась, если нет, то не добавилась.
        // Then the new task event is triggered
        val value = tasksViewModel.newTaskEvent.getOrAwaitValue() //Event(null )
        // Вот эта хрень тестовый слушатель, он услышит задачу или ничегто не услышит
        // если он ничего не услышит, то в value он загонит null (точнее он там останется)

        //А это мы проверяем, что этот наблюдатель услышал value.
        assertThat(value.getContentIfNotHandled(), not(nullValue()))


    }

    @Test
    fun setFilterAllTasks_tasksAddViewVisible() {

        // Given a fresh ViewModel
    //    val tasksViewModel = TasksViewModel(ApplicationProvider.getApplicationContext())

        // When the filter type is ALL_TASKS
        tasksViewModel.setFiltering(TasksFilterType.ALL_TASKS)

        // Then the "Add task" action is visible
        assertThat(tasksViewModel.tasksAddViewVisible.getOrAwaitValue(), `is`(true))
    }

}






/* первый вариант
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

//import junit.framework.TestCase
//class TasksViewModelTest : TestCase()


/**
 * добавляете тест модели представления, чтобы проверить, что при вызове addNewTaskметода Event
 * запускается окно открытия новой задачи.
 */
@RunWith(AndroidJUnit4::class)
class TasksViewModelTest {
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Test
    fun addNewTask_setsNewTaskEvent() {

        // готовим окружение для ViewModel, что бы ее создать:
        // application: Application
        // что бы сгенерировать для него фейк используем AndroidX.Test.Core + Robolectric

        // Given a fresh TasksViewModel Создать свежий экземпляр класса TasksViewModel
        // Вместо старта на эмуляторе мы создаем здесь Windows application: Application для создания ViewModel
        val tasksViewModel = TasksViewModel(ApplicationProvider.getApplicationContext())
        // class TasksViewModel(application: Application)
        // When adding a new task
        // Когда создали класс, можем вызвать ее функцию.
        tasksViewModel.addNewTask()

        // Then the new task event is triggered

    }

}
*/
