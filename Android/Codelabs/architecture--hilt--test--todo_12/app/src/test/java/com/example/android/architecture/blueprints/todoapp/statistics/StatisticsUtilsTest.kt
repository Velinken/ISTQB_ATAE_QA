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

import androidx.test.espresso.matcher.ViewMatchers.assertThat
import com.example.android.architecture.blueprints.todoapp.data.Task
import org.hamcrest.core.Is.`is`
//import org.junit.Assert.assertThat
import org.junit.Test

/**
 * Unit tests for [getActiveAndCompletedStats].
 * Модульные тесты для [получить активную и завершенную статистику].
 */
class StatisticsUtilsTest {

    @Test
    fun getActiveAndCompletedStats_noCompleted() {
        val tasks = listOf(
            Task("title", "desc", isCompleted = false)
        )
        // When the list of tasks is computed with an active task
        // Когда список задач вычисляется с помощью активной задачи
        val result = getActiveAndCompletedStats(tasks)

        // Then the percentages are 100 and 0
        // Тогда проценты равны 100 и 0
        assertThat(result.activeTasksPercent, `is`(100f))
        assertThat(result.completedTasksPercent, `is`(0f))
    }

    @Test
    fun getActiveAndCompletedStats_noActive() {
        val tasks = listOf(
            Task("title", "desc", isCompleted = true)
        )
        // When the list of tasks is computed with a completed task
        // Когда список задач вычисляется вместе с завершенной задачей
        val result = getActiveAndCompletedStats(tasks)

        // Then the percentages are 0 and 100
        // Тогда проценты равны 0 и 100
        assertThat(result.activeTasksPercent, `is`(0f))
        assertThat(result.completedTasksPercent, `is`(100f))
    }

    @Test
    fun getActiveAndCompletedStats_both() {
        // Given 3 completed tasks and 2 active tasks
        // Дано 3 выполненных задания и 2 активных задания
        val tasks = listOf(
            Task("title", "desc", isCompleted = true),
            Task("title", "desc", isCompleted = true),
            Task("title", "desc", isCompleted = true),
            Task("title", "desc", isCompleted = false),
            Task("title", "desc", isCompleted = false)
        )
        // When the list of tasks is computed
        // Когда вычисляется список задач
        val result = getActiveAndCompletedStats(tasks)

        // Then the result is 40-60
        // Тогда результат будет 40-60
        assertThat(result.activeTasksPercent, `is`(40f))
        assertThat(result.completedTasksPercent, `is`(60f))
    }

    @Test
    fun getActiveAndCompletedStats_error() {
        // When there's an error loading stats
        // Когда есть ошибка загрузки статистики
        val result = getActiveAndCompletedStats(null)

        // Both active and completed tasks are 0
        // Как активные, так и завершенные задачи равны 0
        assertThat(result.activeTasksPercent, `is`(0f))
        assertThat(result.completedTasksPercent, `is`(0f))
    }

    @Test
    fun getActiveAndCompletedStats_empty() {
        // When there are no tasks
        // Когда нет задач
        val result = getActiveAndCompletedStats(emptyList())

        // Both active and completed tasks are 0
        // Как активные, так и завершенные задачи равны 0
        assertThat(result.activeTasksPercent, `is`(0f))
        assertThat(result.completedTasksPercent, `is`(0f))
    }
}
