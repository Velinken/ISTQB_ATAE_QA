package com.example.android.architecture.blueprints.todoapp.data.source

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import com.example.android.architecture.blueprints.todoapp.data.Result
import com.example.android.architecture.blueprints.todoapp.data.Task
import kotlinx.coroutines.runBlocking
/**
 * Implementation of a remote data source with static access to the data for easy testing.
 * Реализация удаленного источника данных со статическим доступом к данным для удобства тестирования.
 */
// TasksRepositor - интерфейс созданный из DefaultTasksRepository refactor ...
//  FakeTestRepository не нужно использовать FakeDataSources или что-то подобное;
//  ему просто нужно возвращать реалистичные фальшивые выходные данные с учетом входных данных.

// runBlocking против runBlockingTests
// Когда вы находитесь в тестовых классах, то есть в классах с @Test функциями,
// используйте runBlockingTest для получения детерминированного поведения.

// Теперь вы можете использовать FakeTestRepository
// вместо реального репозитория в TasksFragment и TaskDetailFragment.

// поддельный репозиторий для тестирования:
class FakeTestRepository : TasksRepository {
    // Вы будете использовать a LinkedHashMap для хранения списка задач
    var tasksServiceData: LinkedHashMap<String, Task> = LinkedHashMap()
    // и MutableLiveData для наблюдаемых задач.
    private val observableTasks = MutableLiveData<Result<List<Task>>>()

    /**
     * вам нужно искусственно вызвать ошибочную ситуацию.
     * Один из способов сделать это - обновить ваши тестовые двойники,
     * чтобы вы могли «установить» их в состояние ошибки, используя флаг.
     * Если флаг установлен false, тестовый двойной функционирует как обычно.
     * Но если флаг установлен true, то тестовый двойник вернет реалистичную ошибку;
     * например, он может вернуть ошибку с ошибкой загрузки данных.
     * Обновите FakeTestRepository, чтобы включить флаг ошибки, который,
     * если он установлен true, заставляет код возвращать реалистичную ошибку.
     */
    private var shouldReturnError = false

    // метод, который изменяет, должен ли репозиторий возвращать ошибки:
    fun setReturnError(value: Boolean) {
        shouldReturnError = value
    }

// getTasks—Этот метод должен взять tasksServiceData и превратить его в список
// с помощью, tasksServiceData.values.toList() а затем вернуть это как Success результат.
    override suspend fun getTasks(forceUpdate: Boolean): Result<List<Task>> {
    if (shouldReturnError) {
        return Result.Error(Exception("Test exception"))
    }
    return Result.Success(tasksServiceData.values.toList())
    }

    // refreshTasks- обновляет значение observableTasksдо того, что возвращает getTasks().
    override suspend fun refreshTasks() {
        observableTasks.value = getTasks()
    }

// observeTasks—Создает сопрограмму с использованием runBlocking и запуском refreshTasks, затем возвращается observableTasks.
    override fun observeTasks(): LiveData<Result<List<Task>>> {
    runBlocking { refreshTasks() }
    return observableTasks
    }

// При тестировании лучше иметь некоторые из них Tasks уже в вашем репозитории.
// Вы можете вызывать saveTask несколько раз, но чтобы упростить задачу,
// добавьте вспомогательный метод специально для тестов, позволяющий добавлять задачи.
// Добавьте addTasks метод, который принимает несколько vararg задач, добавляет каждую из них HashMap, а затем обновляет задачи.
    fun addTasks(vararg tasks: Task) {
        for (task in tasks) {
            tasksServiceData[task.id] = task
        }
        runBlocking { refreshTasks() }
    }

    override suspend fun refreshTask(taskId: String) {
        refreshTasks()
    }

    override fun observeTask(taskId: String): LiveData<Result<Task>> {
        runBlocking { refreshTasks() }
        return observableTasks.map { tasks ->
            when (tasks) {
                is Result.Loading -> Result.Loading
                is Result.Error -> Result.Error(tasks.exception)
                is Result.Success -> {
                    val task = tasks.data.firstOrNull { it.id == taskId }
                        ?: return@map Result.Error(Exception("Not found"))
                    Result.Success(task)
                }
            }
        }
    }

    override suspend fun getTask(taskId: String, forceUpdate: Boolean): Result<Task> {
        if (shouldReturnError) {
            return Result.Error(Exception("Test exception"))
        }
        tasksServiceData[taskId]?.let {
            return Result.Success(it)
        }
        return Result.Error(Exception("Could not find task"))
    }

    override suspend fun saveTask(task: Task) {
        tasksServiceData[task.id] = task
    }

    override suspend fun completeTask(task: Task) {
        val completedTask = task.copy(isCompleted = true)
        tasksServiceData[task.id] = completedTask
        refreshTasks()
    }

    override suspend fun completeTask(taskId: String) {
        // Not required for the remote data source.
        throw NotImplementedError()
    }

    override suspend fun activateTask(task: Task) {
        val activeTask = task.copy(isCompleted = false)
        tasksServiceData[task.id] = activeTask
        refreshTasks()
    }

    override suspend fun activateTask(taskId: String) {
        throw NotImplementedError()
    }

    override suspend fun clearCompletedTasks() {
        tasksServiceData = tasksServiceData.filterValues {
            !it.isCompleted
        } as java.util.LinkedHashMap<String, Task>
    }

    override suspend fun deleteAllTasks() {
        tasksServiceData.clear()
        refreshTasks()
    }

    override suspend fun deleteTask(taskId: String) {
        tasksServiceData.remove(taskId)
        refreshTasks()
    }
}
// На данный момент у вас есть поддельный репозиторий для тестирования,
// в котором реализовано несколько ключевых методов. Затем используйте это в своих тестах!
