package com.example.android.architecture.blueprints.todoapp.data.source

//import com.example.android.architecture.blueprints.todoapp.MainCoroutineRule
import com.example.android.architecture.blueprints.todoapp.data.Result
import com.example.android.architecture.blueprints.todoapp.data.Task
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.IsEqual
import org.junit.*
//import org.junit.Assert.*

// Используйте runBlockingTest в своих тестовых классах при вызове suspend функции.
@ExperimentalCoroutinesApi
class DefaultTasksRepositoryTest {

    // FakeDataSource переменные-члены (по одной для каждого источника данных для вашего репозитория)
    private lateinit var tasksRemoteDataSource: FakeDataSource
    private lateinit var tasksLocalDataSource: FakeDataSource

    // Class under test
    // переменную, для DefaultTasksRepository которой вы будете тестировать.
    private lateinit var tasksRepository: DefaultTasksRepository

    private val task1 = Task("Title1", "Description1")
    private val task2 = Task("Title2", "Description2")
    private val task3 = Task("Title3", "Description3")
    private val remoteTasks = listOf(task1, task2).sortedBy { it.id }
    private val localTasks = listOf(task3).sortedBy { it.id }
    private val newTasks = listOf(task3,task1).sortedBy { it.id }


    // Set the main coroutines dispatcher for unit testing.
    // Установите главный диспетчер сопрограмм для модульного тестирования.
    @ExperimentalCoroutinesApi
   // @get:Rule
    //var mainCoroutineRule = MainCoroutineRule()

    // Создайте метод настройки и инициализации тестируемого объекта DefaultTasksRepository.
    // Это DefaultTasksRepository будет использовать ваш тестовый двойник FakeDataSource.
    // Создайте метод с именем createRepositoryи аннотируйте его с помощью @Before.
    @Before
    fun createRepository() {
        tasksRemoteDataSource = FakeDataSource(remoteTasks.toMutableList())
        tasksLocalDataSource = FakeDataSource(localTasks.toMutableList())
        // Get a reference to the class under test Получите ссылку на тестируемый класс
        tasksRepository = DefaultTasksRepository(
                // TODO Dispatchers.Unconfined should be replaced with Dispatchers.Main
                //  this requires understanding more about coroutines + testing
                //  so we will keep this as Unconfined for now.
                // Диспетчеры TODO.Неограниченные должны быть заменены диспетчерами.Главная
                // это требует большего понимания сопрограмм + тестирования
                //- так что пока мы будем держать это в секрете.

                //  tasksRemoteDataSource, tasksLocalDataSource, Dispatchers.Unconfined
                /*
                Используйте Dispatcher.Mainв место Dispatcher.Unconfined при определении тестируемого репозитория.
                 Аналогично TestCoroutineDispatcher, Dispatchers.Unconfined выполняет задачи немедленно.
                  Но он не включает в себя все другие преимущества тестирования TestCoroutineDispatcher,
                   такие как возможность приостановить выполнение:
                 */
                // HERE Swap Dispatcher.Unconfined Вот и Поменяйся диспетчером.Неограниченный
                tasksRemoteDataSource, tasksLocalDataSource, Dispatchers.Main

        )
    }

    @After
    fun tearDown() {
    }

    //  runBlockingTest - функция, предоставляемая тестовой библиотекой сопрограмм
    //  Он принимает блок кода, а затем запускает этот блок кода в специальном контексте сопрограммы,
    //  который выполняется синхронно и немедленно, что означает, что действия будут происходить в детерминированном порядке.
    //  По сути, это заставляет ваши сопрограммы работать как не сопрограммы, поэтому они предназначены для тестирования кода

    /**
     * Отличная работа! с  @get:Rule вверху и классом MainCoroutineRule можно заменить
     * Теперь вы используете TestCoroutineDispatcher в своем коде, который является предпочтительным диспетчером для тестирования.
     * Далее вы увидите, как использовать дополнительную функцию TestCoroutineDispatcher контроля времени выполнения сопрограмм.
     */
    @Test
    // REPLACE
    //fun getTasks_requestsAllTasksFromRemoteDataSource() = runBlockingTest {
    // WITH
    //fun getTasks_requestsAllTasksFromRemoteDataSource() = mainCoroutineRule.runBlockingTest {
        fun getTasks_requestsAllTasksFromRemoteDataSource() = runBlockingTest {
        // testImplementation "org.jetbrains.kotlinx:kotlinx-coroutines-test:$coroutinesVersion"
        // When tasks are requested from the tasks repository
        // Когда задачи запрашиваются из репозитория задач
        val tasks = tasksRepository.getTasks(true) as Result.Success

        // Then tasks are loaded from the remote data source
        // Затем задачи загружаются из удаленного источника данных
        assertThat(tasks.data, IsEqual(remoteTasks))
    }
    @Test
    // REPLACE
    //fun getTasks_requestsAllTasksFromRemoteDataSource() = runBlockingTest {
    // WITH
    //fun getTasks_requestsAllTasksFromRemoteDataSource() = mainCoroutineRule.runBlockingTest {
    fun getTasks_requestsAllTasksFromLocalDataSource() = runBlockingTest {
        // testImplementation "org.jetbrains.kotlinx:kotlinx-coroutines-test:$coroutinesVersion"
        // When tasks are requested from the tasks repository
        // Когда задачи запрашиваются из репозитория задач
        val tasks = tasksRepository.getTasks(false) as Result.Success

        // Then tasks are loaded from the remote data source
        // Затем задачи загружаются из удаленного источника данных
        assertThat(tasks.data, IsEqual(localTasks))
    }
    /* @Test
    // REPLACE
    //fun getTasks_requestsAllTasksFromRemoteDataSource() = runBlockingTest {
    // WITH
    //fun getTasks_requestsAllTasksFromRemoteDataSource() = mainCoroutineRule.runBlockingTest {
    fun getTasks_requestsAllTasksFromLocalDataSourceFail() = runBlockingTest {
        // testImplementation "org.jetbrains.kotlinx:kotlinx-coroutines-test:$coroutinesVersion"
        // When tasks are requested from the tasks repository
        // Когда задачи запрашиваются из репозитория задач
        val tasks = tasksRepository.getTasks(false) as Result.Success

        // Then tasks are loaded from the remote data source
        // Затем задачи загружаются из удаленного источника данных
        assertThat(tasks.data, IsEqual(newTasks))
    }*/
}

/**
 * Щелкните правой кнопкой мыши DefaultTasksRepository имя класса и выберите « Создать» , затем « Проверить».
 * Следуйте инструкции , чтобы создать DefaultTasksRepositoryTest в тестовом наборе источников.
 * Вверху вашего нового DefaultTasksRepositoryTest класса добавьте указанные ниже переменные-члены,
 * чтобы представить данные в ваших поддельных источниках данных.
 * Создайте три переменные, две FakeDataSource переменные-члены
 * (по одной для каждого источника данных для вашего репозитория)
 * и переменную, для DefaultTasksRepository которой вы будете тестировать.
 * Создайте метод с именем createRepository и аннотируйте его с помощью @Before.
 * Инстанцировать ваши источники поддельных данных, используя remoteTasks и localTasks списки.
 * Создайте экземпляр tasksRepository, используя два только что созданных поддельных источника данных и Dispatchers.Unconfined.
 */

/*
import com.example.android.architecture.blueprints.todoapp.data.Result.Success
import com.example.android.architecture.blueprints.todoapp.data.Task
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.core.IsEqual
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class DefaultTasksRepositoryTest
private val task1 = Task("Title1", "Description1")
private val task2 = Task("Title2", "Description2")
private val task3 = Task("Title3", "Description3")
private val remoteTasks = listOf(task1, task2).sortedBy { it.id }
private val localTasks = listOf(task3).sortedBy { it.id }
private val newTasks = listOf(task3).sortedBy { it.id }

private lateinit var tasksRemoteDataSource: FakeDataSource
private lateinit var tasksLocalDataSource: FakeDataSource

// Class under test
private lateinit var tasksRepository: DefaultTasksRepository

@Before
fun createRepository() {
    tasksRemoteDataSource = FakeDataSource(remoteTasks.toMutableList())
    tasksLocalDataSource = FakeDataSource(localTasks.toMutableList())
    // Get a reference to the class under test
    tasksRepository = DefaultTasksRepository(
            // TODO Dispatchers.Unconfined should be replaced with Dispatchers.Main
            //  this requires understanding more about coroutines + testing
            //  so we will keep this as Unconfined for now.
            tasksRemoteDataSource, tasksLocalDataSource, Dispatchers.Unconfined
    )
}

@Test
fun getTasks_requestsAllTasksFromRemoteDataSource(){
    // When tasks are requested from the tasks repository
    val tasks = tasksRepository.getTasks(true) as Result.Success

    // Then tasks are loaded from the remote data source
    assertThat(tasks.data, IsEqual(remoteTasks))
}*/
/**
        Пишу изменения, что бы отправить в develop И увидеть эти изменения
 */