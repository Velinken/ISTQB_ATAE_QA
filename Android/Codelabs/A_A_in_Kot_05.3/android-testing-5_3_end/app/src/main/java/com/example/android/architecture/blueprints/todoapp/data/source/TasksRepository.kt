package com.example.android.architecture.blueprints.todoapp.data.source

import androidx.lifecycle.LiveData
import com.example.android.architecture.blueprints.todoapp.data.Result
import com.example.android.architecture.blueprints.todoapp.data.Task

/**
 * Первым шагом к использованию внедрения зависимостей конструктора является создание общего интерфейса,
 * разделяемого между подделкой и реальным классом.
 * Откройте DefaultTasksRepository и щелкните правой кнопкой мыши имя класса.
 * Затем выберите Refactor -> Extract -> Interface.
 * Выберите Извлечь в отдельный файл.
 * В окне « Извлечь интерфейс» измените имя интерфейса на TasksRepository.
 * В разделе интерфейса « Члены для формирования » отметьте все элементы,
 * кроме двух сопутствующих элементов и частных методов.
 * Щелкните Refactor. Новый TasksRepository интерфейс должен появиться в пакете данных / источника.
 */
interface TasksRepository {
    suspend fun getTasks(forceUpdate: Boolean = false): Result<List<Task>>

    suspend fun refreshTasks()
    fun observeTasks(): LiveData<Result<List<Task>>>

    suspend fun refreshTask(taskId: String)
    fun observeTask(taskId: String): LiveData<Result<Task>>

    /**
     * Relies on [getTasks] to fetch data and picks the task with the same ID.
     * Полагается на [getTasks] для извлечения данных и выбирает задачу с тем же идентификатором.
     */
    suspend fun getTask(taskId: String, forceUpdate: Boolean = false): Result<Task>

    suspend fun saveTask(task: Task)

    suspend fun completeTask(task: Task)

    suspend fun completeTask(taskId: String)

    suspend fun activateTask(task: Task)

    suspend fun activateTask(taskId: String)

    suspend fun clearCompletedTasks()

    suspend fun deleteAllTasks()

    suspend fun deleteTask(taskId: String)
}