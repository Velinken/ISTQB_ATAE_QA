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

package com.example.android.architecture.blueprints.todoapp.data.source.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.android.architecture.blueprints.todoapp.data.Task

/**
 * Data Access Object for the tasks table.
 * Объект доступа к данным для таблицы задач.
 */
@Dao
interface TasksDao {

    /**
     * Observes list of tasks.
     * Наблюдает за списком задач.
     *
     * @return all tasks.
     * @вернуть все задачи.
     */
    @Query("SELECT * FROM Tasks")
    fun observeTasks(): LiveData<List<Task>>

    /**
     * Observes a single task.
     * Наблюдает за одной задачей..
     *
     * @param taskId the task id.
     * @return the task with taskId.
     * @param taskId идентификатор задачи.
     * @вернуть панель задач с идентификатор_задачи.
     */
    @Query("SELECT * FROM Tasks WHERE entryid = :taskId")
    fun observeTaskById(taskId: String): LiveData<Task>

    /**
     * Select all tasks from the tasks table.
     * Выберите все задачи из таблицы задачи.
     *
     * @return all tasks.
     * @вернуть все задачи.
     */
    @Query("SELECT * FROM Tasks")
    suspend fun getTasks(): List<Task>

    /**
     * Select a task by id.
     * Выберите задачу по идентификатору.
     *
     * @param taskId the task id.
     * @return the task with taskId.
     * @param taskId идентификатор задачи.
     * @вернуть панель задач с идентификатор_задачи.
     */
    @Query("SELECT * FROM Tasks WHERE entryid = :taskId")
    suspend fun getTaskById(taskId: String): Task?

    /**
     * Insert a task in the database. If the task already exists, replace it.
     * Вставьте задачу в базу данных. Если задача уже существует, замените ее.
     *
     * @param task the task to be inserted.
     * @param task задача, которая будет вставлена.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: Task)

    /**
     * Update a task.
     * Обновление задачи.
     *
     * @param task task to be updated
     * @return the number of tasks updated. This should always be 1.
     * @param task задача, подлежащая обновлению
     * @возвращает количество обновленных задач. Это всегда должно быть 1.
     */
    @Update
    suspend fun updateTask(task: Task): Int

    /**
     * Update the complete status of a task
     * Обновление полного статуса задачи
     *
     * @param taskId id of the task
     * @param completed status to be updated
     * @парам идентификатор_задачи идентификатор задачи
     * @парам завершен статус обновлялся
     */
    @Query("UPDATE tasks SET completed = :completed WHERE entryid = :taskId")
    suspend fun updateCompleted(taskId: String, completed: Boolean)

    /**
     * Delete a task by id.
     * Удалить задачу по идентификатору.
     *
     * @return the number of tasks deleted. This should always be 1.
     * @возвращает количество удаленных задач. Это всегда должно быть 1.
     */
    @Query("DELETE FROM Tasks WHERE entryid = :taskId")
    suspend fun deleteTaskById(taskId: String): Int

    /**
     * Delete all tasks.
     * Удалите все задачи.
     */
    @Query("DELETE FROM Tasks")
    suspend fun deleteTasks()

    /**
     * Delete all completed tasks from the table.
     * Удалите все выполненные задачи из таблицы.
     *
     * @return the number of tasks deleted.
     * @возвращает количество удаленных задач.
     */
    @Query("DELETE FROM Tasks WHERE completed = 1")
    suspend fun deleteCompletedTasks(): Int
}
