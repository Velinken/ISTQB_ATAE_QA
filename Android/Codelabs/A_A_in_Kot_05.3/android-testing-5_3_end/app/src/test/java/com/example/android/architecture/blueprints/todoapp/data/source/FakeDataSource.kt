package com.example.android.architecture.blueprints.todoapp.data.source

import androidx.lifecycle.LiveData
import com.example.android.architecture.blueprints.todoapp.data.Result
import com.example.android.architecture.blueprints.todoapp.data.Result.Error
import com.example.android.architecture.blueprints.todoapp.data.Result.Success
import com.example.android.architecture.blueprints.todoapp.data.Task

/**
 * FakeDataSource- это особый тип тестового двойника, который называется подделкой.
 * Подделка - это тестовый двойник, у которого есть «рабочая» реализация класса,
 * но он реализован таким образом, что он удобен для тестов, но не подходит для производства.
 * «Рабочая» реализация означает, что класс будет выдавать реалистичные результаты с учетом входных данных.
 *
 * Например, ваш поддельный источник данных не будет подключаться к сети и ничего не сохранять в базе данных
 * - вместо этого он будет просто использовать список в памяти.
 * Это будет «работать так, как вы могли ожидать»,
 * поскольку методы получения или сохранения задач будут возвращать ожидаемые результаты,
 * но вы никогда не сможете использовать эту реализацию в производственной среде,
 * потому что она не сохраняется на сервере или в базе данных.
 */

/**
 * позволяет тестировать код DefaultTasksRepository без необходимости полагаться на реальную базу данных или сеть.
 * предоставляет "достаточно реальную" реализацию для тестов.
 * var tasks - список задач, которые "подделывают" ответ базы данных или сервера
 */

/**
 * Прямо сейчас зависимости строятся внутри init метода DefaultTasksRepository.
 * Поскольку вы создаете, назначаете taskLocalDataSource и tasksRemoteDataSource внутри DefaultTasksRepository,
 * они по сути жестко запрограммированы. В вашем тестовом двойнике нет возможности поменять местами.
 * Вместо этого вы хотите предоставить эти источники данных классу, а не жестко их кодировать.
 * Предоставление зависимостей известно как внедрение зависимостей.
 * Существуют разные способы предоставления зависимостей и, следовательно, разные типы внедрения зависимостей.
 * Внедрение зависимостей конструктора позволяет вам заменить тестовый двойник, передав его в конструктор.
 */
// Теперь вы используете внедрение зависимостей конструктора!
class FakeDataSource(var tasks: MutableList<Task>? = mutableListOf()) : TasksDataSource {

    override fun observeTasks(): LiveData<Result<List<Task>>> {
        TODO("Not yet implemented")
    }

    /**
     *  На данный момент цель состоит в том, чтобы протестировать метод репозитория getTasks.
     *  Это вызывает в источнике данных getTasks , deleteAllTasks и saveTask методу
     *  DefaultTaskRepository getTasks и updateTasksFromRemoteDataSource методов.
     *  Методы getTasks, deleteAllTasks и saveTaskвсе называют либо на локальном или удаленном источнике данных.
     */

    override suspend fun getTasks(): Result<List<Task>> {
        /**
         * Напишите фальшивую версию этих методов:
         * Напишите getTasks: Если tasks не null, вернуть Success результат. Если tasks есть null, вернуть Error результат.
         * Запись deleteAllTasks: очистить список изменяемых задач.
         * Написать saveTask: добавить задачу в список.
         * Это похоже на то, как работают фактические локальные и удаленные источники данных.
         */
        tasks?.let { return Success(ArrayList(it)) }
        return Error(
                Exception("Tasks not found")
        )
    }

    override suspend fun refreshTasks() {
        TODO("Not yet implemented")
    }

    override fun observeTask(taskId: String): LiveData<Result<Task>> {
        TODO("Not yet implemented")
    }

    override suspend fun getTask(taskId: String): Result<Task> {
        TODO("Not yet implemented")
    }

    override suspend fun refreshTask(taskId: String) {
        TODO("Not yet implemented")
    }

    override suspend fun saveTask(task: Task) {
        tasks?.add(task)
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
        tasks?.clear()
    }

    override suspend fun deleteTask(taskId: String) {
        TODO("Not yet implemented")
    }
}