package com.example.android.architecture.blueprints.todoapp.tasks


//import androidx.test.core.app.ApplicationProvider
//import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.android.architecture.blueprints.todoapp.Event
import com.example.android.architecture.blueprints.todoapp.MainCoroutineRule
import com.example.android.architecture.blueprints.todoapp.R
import com.example.android.architecture.blueprints.todoapp.data.Task
import com.example.android.architecture.blueprints.todoapp.data.source.FakeTestRepository
import com.example.android.architecture.blueprints.todoapp.getOrAwaitValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.*
import org.junit.runner.RunWith

//import org.junit.runner.RunWith
//import org.robolectric.annotation.Config

//  . Robolectric - это библиотека, которая создает смоделированную среду Android для тестов
//  и работает быстрее, чем загрузка эмулятора или запуск на устройстве
// Always show the result of every unit test when running via command line, even if it passes.
//testOptions.unitTests { includeAndroidResources = true}
//  вместо @Config(manifest= Config.NONE)
// Если вам нужно запустить смоделированный код Android в test исходном наборе,
// вы можете добавить зависимость Robolectric и @RunWith(AndroidJUnit4::class)аннотацию.

// Поскольку вы больше не используете тестовый ApplicationProvider.getApplicationContext код AndroidX,
// вы также можете удалить @RunWith(AndroidJUnit4::class)аннотацию. (при файк репозитории)
//@RunWith(AndroidJUnit4::class)
//@Config(sdk = [Build.VERSION_CODES.O_MR1])
//@Config(manifest= Config.NONE)

/**
 * Используя внедрение зависимостей конструктора, вы удалили DefaultTasksRepository зависимость
 *  и заменили ее на вашу FakeTestRepository в тестах.
 */


class TasksViewModelTest{

    // Добавьте FakeTestRepositoryсвойство в TasksViewModelTest.
    private lateinit var tasksRepository: FakeTestRepository

   // @ExperimentalCoroutinesApi
   // val testDispatcher: TestCoroutineDispatcher = TestCoroutineDispatcher()
    // Чтобы использовать правило JUnit, вы создаете экземпляр правила и аннотируете его с помощью @get:Rule.
    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    // Executes each task synchronously using Architecture Components.
    // Выполняет каждую задачу синхронно с использованием компонентов архитектуры.
    //  Когда вы пишете тесты, включающие тестирование LiveData, используйте это правило!
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    // Subject under test
    private lateinit var tasksViewModel: TasksViewModel
 // XXXXX  val tasksViewModel = TasksViewModel(ApplicationProvider.getApplicationContext()) xxx- нужен свежий
    // Это приведет к тому, что для всех тестов будет использоваться один и тот же экземпляр. Поэтому в @Before
    // Этого следует избегать, поскольку в каждом тесте должен быть свежий экземпляр тестируемого объекта (в данном случае ViewModel).

    @Before
    fun setupViewModel() {
        // Вы создаете свой свежий tasksViewModel
        // используя ApplicationProvider.getApplicationContext() оператор AndroidX .
        //tasksViewModel = TasksViewModel(ApplicationProvider.getApplicationContext())
        // Поскольку вы больше не используете тестовый ApplicationProvider.getApplicationContext код AndroidX,
        // вы также можете удалить @RunWith(AndroidJUnit4::class)аннотацию.

        // We initialise the tasks to 3, with one active and two completed
        // Обновите setupViewModel метод, чтобы создать FakeTestRepository с тремя задачами,
        tasksRepository = FakeTestRepository()
        val task1 = Task("Title1", "Description1")
        val task2 = Task("Title2", "Description2", true)
        val task3 = Task("Title3", "Description3", true)
        tasksRepository.addTasks(task1, task2, task3)
        // а затем создайте tasksViewModel с этим репозиторием.
        tasksViewModel = TasksViewModel(tasksRepository)
        // Примечание. Нет необходимости использовать delegate свойство или a ViewModelProvider,
        // вы можете просто создать ViewModel в модульных тестах.

    }

    /*
        Эта ошибка указывает на то, что Dispatcher.Main не удалось инициализировать.
         Основная причина (не объясненная в ошибке) - отсутствие Android Looper.getMainLooper().
          Сообщение об ошибке говорит вам использовать Dispatcher.setMain from kotlinx-coroutines-test.
           Давай, сделай это!
           С MainCoroutineRule() это уже не требуется
         */
  /*  @ExperimentalCoroutinesApi
    @Before
    fun setupDispatcher() {
        Dispatchers.setMain(testDispatcher)
    }

    @ExperimentalCoroutinesApi
    @After
    fun tearDownDispatcher() {
        Dispatchers.resetMain()
        testDispatcher.cleanupTestCoroutines()
    }*/

    // проверит, что при вызове addNewTask метода Event запускается окно открытия новой задачи
    @Test
    fun addNewTask_setsNewTaskEvent() {

        // Given a fresh ViewModel Учитывая свежий взгляд модели
        // в @Before val tasksViewModel = TasksViewModel(ApplicationProvider.getApplicationContext())

        // When adding a new task При добавлении новой задачи
        tasksViewModel.addNewTask()

        // На этом шаге вы используете getOrAwaitValue метод и пишете инструкцию assert,
        // которая проверяет, newTaskEvent был ли запущен.
        // Then the new task event is triggered Затем запускается новое событие задачи
        // тодо test LiveData см LiveDataTestUtil
        val value = tasksViewModel.newTaskEvent.getOrAwaitValue()

        assertThat(value.getContentIfNotHandled(), not(nullValue()))
    // Что такое getContentIfNoteHandled?
    //В приложении TO-DO вы используете настраиваемый Event класс для LiveData представления одноразовых событий
    // (таких как навигация или всплывающая закусочная) getContentIfNotHandled предоставляет «разовую» возможность.
    // При первом вызове он получает содержимое файла Event.
    // Любые дополнительные вызовы getContentIfNotHandled того же контента будут возвращены null.
    // Вот как Event осуществляется доступ к данным в коде приложения, и поэтому мы используем его для тестов.
    // Вы можете узнать больше о событиях здесь .
    // https://medium.com/androiddevelopers/livedata-with-snackbar-navigation-and-other-events-the-singleliveevent-case-ac2622673150

    }
    @Test
    fun setFilterAllTasks_tasksAddViewVisible() {

        // Given a fresh ViewModel
        // в @Before val tasksViewModel = TasksViewModel(ApplicationProvider.getApplicationContext())

        // When the filter type is ALL_TASKS
        // Вы вызываете setFiltering метод, передавая ALL_TASKSтип фильтра enum.
        tasksViewModel.setFiltering(TasksFilterType.ALL_TASKS)

        // Then the "Add task" action is visible
        // Вы проверяете, что tasksAddViewVisible верно, используя getOrAwaitNextValue метод.
        assertThat(tasksViewModel.tasksAddViewVisible.getOrAwaitValue(), `is`(true))
    }

    @Test
    fun completeTask_dataAndSnackbarUpdated() {
        // Create an active task and add it to the repository.
        // Создайте активную задачу и добавьте ее в репозиторий.
        val task = Task("Title", "Description")
        tasksRepository.addTasks(task)

        // Mark the task as complete task.
        // Отметьте задачу как завершенную.
        tasksViewModel.completeTask(task, true)

        // Verify the task is completed.
        // Убедитесь, что задача выполнена.
        assertThat(tasksRepository.tasksServiceData[task.id]?.isCompleted, `is`(true))

        // Assert that the snackbar has been updated with the correct text.
        // Утверждаем, что снэк - бар был обновлен правильным текстом.
        val snackbarText: Event<Int> =  tasksViewModel.snackbarText.getOrAwaitValue()
        assertThat(snackbarText.getContentIfNotHandled(), `is`(R.string.task_marked_complete))
        /*
        Эта ошибка указывает на то, что Dispatcher.Main не удалось инициализировать.
         Основная причина (не объясненная в ошибке) - отсутствие Android Looper.getMainLooper().
          Сообщение об ошибке говорит вам использовать Dispatcher.setMain from kotlinx-coroutines-test.
           Давай, сделай это!
         */
    }
}


/* Это много шаблонного кода, чтобы увидеть сингл LiveData в тесте!
@Test
fun addNewTask_setsNewTaskEvent() {
    // Given a fresh ViewModel Учитывая свежий взгляд модели
    val tasksViewModel = TasksViewModel(ApplicationProvider.getApplicationContext())
    // Create observer - no need for it to do anything! Создайте наблюдателя - ему не нужно ничего делать!
    val observer = Observer<Event<Unit>> {}
    try {
        // Observe the LiveData forever Наблюдайте за живыми данными вечно
        tasksViewModel.newTaskEvent.observeForever(observer)
        // When adding a new task При добавлении новой задачи
        tasksViewModel.addNewTask()
        // Then the new task event is triggered Затем запускается новое событие задачи
        val value = tasksViewModel.newTaskEvent.value
        assertThat(value?.getContentIfNotHandled(), (not(nullValue())))
    } finally {
        // Whatever happens, don't forget to remove the observer! Что бы ни случилось, не забудьте убрать наблюдателя!
        tasksViewModel.newTaskEvent.removeObserver(observer)
    }
}
 */