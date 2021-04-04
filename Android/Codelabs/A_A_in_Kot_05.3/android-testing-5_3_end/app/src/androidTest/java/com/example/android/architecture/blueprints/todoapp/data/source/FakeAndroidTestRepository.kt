package com.example.android.architecture.blueprints.todoapp.data.source

    import androidx.lifecycle.LiveData
    import androidx.lifecycle.MutableLiveData
    import androidx.lifecycle.map
    import com.example.android.architecture.blueprints.todoapp.data.Result
    import com.example.android.architecture.blueprints.todoapp.data.Result.Error
    import com.example.android.architecture.blueprints.todoapp.data.Result.Success
    import com.example.android.architecture.blueprints.todoapp.data.Task
    import kotlinx.coroutines.runBlocking
    import java.util.LinkedHashMap

/**
 * Implementation of a remote data source with static access to the data for easy testing.
 * Реализация удаленного источника данных со статическим доступом к данным для удобства тестирования.
 * У вас уже есть FakeTestRepository в тестовом наборе исходников.
 * Вы не можете разделить тестовые классы между test и androidTest исходными наборами по умолчанию.
 * Итак, вам нужно создать дубликат FakeTestRepository класса в androidTest исходном наборе и вызвать его FakeAndroidTestRepository.
 * Если вы хотите обмениваться файлами между test и androidTest исходными наборами вы можете настроить,
 * с помощью Gradle , а sharedTest папку , как показано в реактивном образце архитектуры Blueprints .
 *
 */


    class FakeAndroidTestRepository : TasksRepository {

        var tasksServiceData: LinkedHashMap<String, Task> = LinkedHashMap()

        private var shouldReturnError = false

        private val observableTasks = MutableLiveData<Result<List<Task>>>()

        fun setReturnError(value: Boolean) {
            shouldReturnError = value
        }

        override suspend fun refreshTasks() {
            observableTasks.value = getTasks()
        }

        override suspend fun refreshTask(taskId: String) {
            refreshTasks()
        }

        override fun observeTasks(): LiveData<Result<List<Task>>> {
            runBlocking { refreshTasks() }
            return observableTasks
        }

        override fun observeTask(taskId: String): LiveData<Result<Task>> {
            runBlocking { refreshTasks() }
            return observableTasks.map { tasks ->
                when (tasks) {
                    is Result.Loading -> Result.Loading
                    is Error -> Error(tasks.exception)
                    is Success -> {
                        val task = tasks.data.firstOrNull { it.id == taskId }
                            ?: return@map Error(Exception("Not found"))
                        Success(task)
                    }
                }
            }
        }

        override suspend fun getTask(taskId: String, forceUpdate: Boolean): Result<Task> {
            if (shouldReturnError) {
                return Error(Exception("Test exception"))
            }
            tasksServiceData[taskId]?.let {
                return Success(it)
            }
            return Error(Exception("Could not find task"))
        }

        override suspend fun getTasks(forceUpdate: Boolean): Result<List<Task>> {
            if (shouldReturnError) {
                return Error(Exception("Test exception"))
            }
            return Success(tasksServiceData.values.toList())
        }

        override suspend fun saveTask(task: Task) {
            tasksServiceData[task.id] = task
        }

        override suspend fun completeTask(task: Task) {
            //val completedTask = Task(task.title, task.description, true, task.id)
            //tasksServiceData[task.id] = completedTask
            val completedTask = task.copy(isCompleted = true)
            tasksServiceData[task.id] = completedTask
            refreshTasks()

        }

        override suspend fun completeTask(taskId: String) {
            // Not required for the remote data source.
            throw NotImplementedError()
        }

        override suspend fun activateTask(task: Task) {
            //val activeTask = Task(task.title, task.description, false, task.id)
            //tasksServiceData[task.id] = activeTask
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
            } as LinkedHashMap<String, Task>
        }

        override suspend fun deleteTask(taskId: String) {
            tasksServiceData.remove(taskId)
            refreshTasks()
        }

        override suspend fun deleteAllTasks() {
            tasksServiceData.clear()
            refreshTasks()
        }


        fun addTasks(vararg tasks: Task) {
            for (task in tasks) {
                tasksServiceData[task.id] = task
            }
            runBlocking { refreshTasks() }
        }
    }