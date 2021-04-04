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

package com.example.android.architecture.blueprints.todoapp.statistics

import com.example.android.architecture.blueprints.todoapp.data.Task

/**
 * Function that does some trivial computation. Used to showcase unit tests.
 * Функция, которая выполняет некоторые тривиальные вычисления.
 * Используется для демонстрации модульных тестов.
 *
 * getActiveAndCompletedStats Функция принимает список задач и возвращает StatsResult.
 * StatsResult это класс данных , который содержит два числа,
 * процент задач, завершены , и процент , которые являются активными .
 */
internal fun getActiveAndCompletedStatsStart(tasks: List<Task>?): StatsResult {
    val totalTasks = tasks!!.size
    val numberOfActiveTasks = tasks.count { it.isActive }
    return StatsResult(
        activeTasksPercent = 100f * numberOfActiveTasks / tasks.size,
        completedTasksPercent = 100f * (totalTasks - numberOfActiveTasks) / tasks.size
    )
}

// ERROR
// неправильно обрабатывает то, что происходит, если список пуст или равен нулю.
// В обоих случаях оба процента должны быть нулевыми.
internal fun getActiveAndCompletedStatsTDD(tasks: List<Task>?): StatsResult {

    val totalTasks = tasks!!.size
    val numberOfActiveTasks = tasks.count { it.isActive }
    val activePercent = 100 * numberOfActiveTasks / totalTasks
    val completePercent = 100 * (totalTasks - numberOfActiveTasks) / totalTasks

    return StatsResult(
        activeTasksPercent = activePercent.toFloat(),
        completedTasksPercent = completePercent.toFloat()
    )

}
// Codelab
internal fun getActiveAndCompletedStatsRR(tasks: List<Task>?): StatsResult {

    return if (tasks == null || tasks.isEmpty()) {
        StatsResult(0f, 0f)
    } else {
        val totalTasks = tasks.size
        val numberOfActiveTasks = tasks.count { it.isActive }
        StatsResult(
            activeTasksPercent = 100f * numberOfActiveTasks / tasks.size,
            completedTasksPercent = 100f * (totalTasks - numberOfActiveTasks) / tasks.size
        )
    }
}
// AS
internal fun getActiveAndCompletedStats(tasks: List<Task>?): StatsResult =
    if (tasks == null || tasks.isEmpty())
        StatsResult(0f, 0f)
        else
        StatsResult(
            activeTasksPercent      = 100f * tasks.count {  it.isActive } / tasks.size,
            completedTasksPercent   = 100f * tasks.count { !it.isActive } / tasks.size
        )

data class StatsResult(val activeTasksPercent: Float, val completedTasksPercent: Float)
