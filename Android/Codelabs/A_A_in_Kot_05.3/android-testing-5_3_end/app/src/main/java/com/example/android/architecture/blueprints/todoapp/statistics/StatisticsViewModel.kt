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

import android.app.Application
import androidx.lifecycle.*
import com.example.android.architecture.blueprints.todoapp.TodoApplication
import com.example.android.architecture.blueprints.todoapp.data.Result
import com.example.android.architecture.blueprints.todoapp.data.Result.Error
import com.example.android.architecture.blueprints.todoapp.data.Result.Success
import com.example.android.architecture.blueprints.todoapp.data.Task
import com.example.android.architecture.blueprints.todoapp.data.source.DefaultTasksRepository
import com.example.android.architecture.blueprints.todoapp.data.source.TasksRepository
import kotlinx.coroutines.launch

/**
 * ViewModel for the statistics screen.
 * ViewModel для экрана статистики.
 */
// REPLACE
//class StatisticsViewModel(application: Application) : AndroidViewModel(application) {
  //  private val tasksRepository = (application as TodoApplication).taskRepository
// WITH
class StatisticsViewModel(
    private val tasksRepository: TasksRepository
) : ViewModel() {

    // Note, for testing and architecture purposes, it's bad practice to construct the repository here.
    // We'll show you how to fix this during the codelab
    // Обратите внимание, что для целей тестирования и архитектуры создание репозитория здесь-плохая практика.
    // Мы покажем вам, как это исправить во время codelab
    //private val tasksRepository = DefaultTasksRepository.getRepository(application)
    //private val tasksRepository = (application as TodoApplication).taskRepository

    private val tasks: LiveData<Result<List<Task>>> = tasksRepository.observeTasks()
    private val _dataLoading = MutableLiveData(false)
    private val stats: LiveData<StatsResult?> = tasks.map {
        if (it is Success) {
            getActiveAndCompletedStats(it.data)
        } else {
            null
        }
    }

    val activeTasksPercent = stats.map {
        it?.activeTasksPercent ?: 0f }
    val completedTasksPercent: LiveData<Float> = stats.map { it?.completedTasksPercent ?: 0f }
    val dataLoading: LiveData<Boolean> = _dataLoading
    val error: LiveData<Boolean> = tasks.map { it is Error }
    val empty: LiveData<Boolean> = tasks.map { (it as? Success)?.data.isNullOrEmpty() }

    /**
     * Когда загружается статистика задачи, приложение отображает индикатор загрузки,
     * который исчезает, как только данные загружаются и статистические расчеты завершаются.
     * Вы напишете тест, который проверяет, что индикатор загрузки отображается во время загрузки статистики,
     * а затем исчезает после загрузки статистики.
     * refresh() Метод StatisticsViewModel контролирует , когда индикатор нагрузки отображается и исчезает:
     */
    fun refresh() {
        _dataLoading.value = true
        // YOU WANT TO CHECK HERE...
            viewModelScope.launch {
                tasksRepository.refreshTasks()
                _dataLoading.value = false
                // ...AND CHECK HERE.
            }
    }
}

// фабрика для внедрения зависимости : обновить в StatisticsFragment.kt
@Suppress("UNCHECKED_CAST")
class StatisticsViewModelFactory (
    private val tasksRepository: TasksRepository
) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>) =
        (StatisticsViewModel(tasksRepository) as T)
}
