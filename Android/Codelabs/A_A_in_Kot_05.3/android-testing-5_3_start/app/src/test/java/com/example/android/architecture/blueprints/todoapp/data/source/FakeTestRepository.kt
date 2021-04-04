package com.example.android.architecture.blueprints.todoapp.data.source

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.android.architecture.blueprints.todoapp.data.Result
import com.example.android.architecture.blueprints.todoapp.data.Task
import kotlinx.coroutines.runBlocking

class FakeTestRepository : TasksRepository {
    // LinkedHashMap для хранения списка задач и a MutableLiveData для наблюдаемых задач.
    // LinkedHashMap переменную, представляющую текущий список задач
    var tasksServiceData: LinkedHashMap<String, Task> = LinkedHashMap()
    // MutableLiveData для наблюдаемых задач
    private val observableTasks = MutableLiveData<Result<List<Task>>>()

    // addTasksметод, который принимает несколько vararg задач, добавляет каждую из них HashMap,
    // а затем обновляет задачи.
    // Добавили сами для удобства тестирования: в коде программиста этой функции нет.
    fun addTasks(vararg tasks: Task) {
        for (task in tasks) {
            tasksServiceData[task.id] = task
        }
        runBlocking { refreshTasks() }
    }

    // getTasks—Этот метод должен взять tasksServiceData
    // и превратить его в список с помощью, tasksServiceData.values.toList()а
    // затем вернуть это как Success результат.
    override suspend fun getTasks(forceUpdate: Boolean): Result<List<Task>> {
        return Result.Success(tasksServiceData.values.toList())
    }
    // refreshTasks- обновляет значение observableTasksдо того, что возвращает getTasks()
    override suspend fun refreshTasks() {
        observableTasks.value = getTasks()
    }
    // observeTasks—Создает сопрограмму с использованием runBlocking
    // и запуском refreshTasks, затем возвращается observableTasks
    // runBlocking, который является более точным моделированием того, что будет делать «настоящая»
    // реализация репозитория, и он предпочтительнее для Fakes, чтобы их поведение более точно
    // соответствовало реальной реализации.
    override fun observeTasks(): LiveData<Result<List<Task>>> {
        runBlocking { refreshTasks() }
        return observableTasks

    }

    override suspend fun refreshTask(taskId: String) {
        TODO("Not yet implemented")
    }

    override fun observeTask(taskId: String): LiveData<Result<Task>> {
        TODO("Not yet implemented")
    }

    override suspend fun getTask(taskId: String, forceUpdate: Boolean): Result<Task> {
        TODO("Not yet implemented")
    }

    override suspend fun saveTask(task: Task) {
        TODO("Not yet implemented")
    }

    override suspend fun completeTask(task: Task) {
        TODO("Not yet implemented")
    }

    override suspend fun completeTask(taskId: String) {
        TODO("Not yet implemented")
    }

    override suspend fun activateTask(task: Task) {
        TODO("Not yet implemented")
    }

    override suspend fun activateTask(taskId: String) {
        TODO("Not yet implemented")
    }

    override suspend fun clearCompletedTasks() {
        TODO("Not yet implemented")
    }

    override suspend fun deleteAllTasks() {
        TODO("Not yet implemented")
    }

    override suspend fun deleteTask(taskId: String) {
        TODO("Not yet implemented")
    }
}