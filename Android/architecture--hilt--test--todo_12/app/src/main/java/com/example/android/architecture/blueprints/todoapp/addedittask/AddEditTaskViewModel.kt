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

package com.example.android.architecture.blueprints.todoapp.addedittask

//import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.architecture.blueprints.todoapp.Event
import com.example.android.architecture.blueprints.todoapp.R
import com.example.android.architecture.blueprints.todoapp.data.Result.Success
import com.example.android.architecture.blueprints.todoapp.data.Task
import com.example.android.architecture.blueprints.todoapp.data.source.TasksRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/*
Идентифицирует жизненный цикл androidx.Конструктор ViewModel для инъекций.
Похоже на javax.inject.Inject, ViewModel, содержащий конструктор, аннотированный ViewModelInject, будет иметь свои зависимости, определенные в параметрах конструктора, введенных Рукоятью кинжала. ViewModel будет доступен для создания HiltViewModelFactory и может быть извлечен по умолчанию в действии или фрагменте, аннотированном AndroidEntryPoint .
Пример:
публичный класс DonutViewModel расширяет ViewModel {
@ViewModelInject
public DonutViewModel(@Assisted SavedStateHandle handle, RecipeRepository repository) {
// ...
}
}

@AndroidEntryPoint
public class CookingActivity расширяет AppCompatActivity {
public void onCreate(Bundle savedInstanceState) {
DonutViewModel vm = new ViewModelProvider(this).get(DonutViewModel.class);
}
}

Только один конструктор в ViewModel должен быть аннотирован с помощью ViewModelInject. Конструктор может дополнительно определить androidx.hilt.Assisted-аннотированный androidx.lifecycle.Параметр SavedStateHandle вместе с любой другой зависимостью. SavedStateHandle не должен быть параметром типа javax.inject.Поставщик ни ленив и не должен быть квалифицированным.
Только зависимости, доступные в ActivityRetainedComponent, могут быть введены в ViewModel.
Осуждаемый
Используйте HiltViewModel .
 */
/**
 * ViewModel for the Add/Edit screen.
 * ViewModel для экрана добавления / редактирования.
 */
// commit 2021
@HiltViewModel
class AddEditTaskViewModel @Inject constructor(
    private val tasksRepository: TasksRepository
) : ViewModel() {

    // Two-way databinding, exposing MutableLiveData
    // Двусторонняя привязка данных, предоставляющая изменяемые живые данные
    val title = MutableLiveData<String>()

    // Two-way databinding, exposing MutableLiveData
    // Двусторонняя привязка данных, предоставляющая изменяемые живые данные
    val description = MutableLiveData<String>()

    private val _dataLoading = MutableLiveData<Boolean>()
    val dataLoading: LiveData<Boolean> = _dataLoading

    private val _snackbarText = MutableLiveData<Event<Int>>()
    val snackbarText: LiveData<Event<Int>> = _snackbarText

    private val _taskUpdatedEvent = MutableLiveData<Event<Unit>>()
    val taskUpdatedEvent: LiveData<Event<Unit>> = _taskUpdatedEvent

    private var taskId: String? = null

    private var isNewTask: Boolean = false

    private var isDataLoaded = false

    private var taskCompleted = false

    fun start(taskId: String?) {
        if (_dataLoading.value == true) {
            return
        }

        this.taskId = taskId
        if (taskId == null) {
            // No need to populate, it's a new task
            // Нет необходимости заполнять, это новая задача
            isNewTask = true
            return
        }
        if (isDataLoaded) {
            // No need to populate, already have data.
            // Нет необходимости заполнять, уже есть данные.
            return
        }

        isNewTask = false
        _dataLoading.value = true

        viewModelScope.launch {
            tasksRepository.getTask(taskId).let { result ->
                if (result is Success) {
                    onTaskLoaded(result.data)
                } else {
                    onDataNotAvailable()
                }
            }
        }
    }

    private fun onTaskLoaded(task: Task) {
        title.value = task.title
        description.value = task.description
        taskCompleted = task.isCompleted
        _dataLoading.value = false
        isDataLoaded = true
    }

    private fun onDataNotAvailable() {
        _dataLoading.value = false
    }

    // Called when clicking on fab.
    // Вызывается при нажатии на fab.
    fun saveTask() {
        val currentTitle = title.value
        val currentDescription = description.value

        if (currentTitle == null || currentDescription == null) {
            _snackbarText.value = Event(R.string.empty_task_message)
            return
        }
        if (Task(currentTitle, currentDescription).isEmpty) {
            _snackbarText.value = Event(R.string.empty_task_message)
            return
        }

        val currentTaskId = taskId
        if (isNewTask || currentTaskId == null) {
            createTask(Task(currentTitle, currentDescription))
        } else {
            val task = Task(currentTitle, currentDescription, taskCompleted, currentTaskId)
            updateTask(task)
        }
    }

    private fun createTask(newTask: Task) = viewModelScope.launch {
        tasksRepository.saveTask(newTask)
        _taskUpdatedEvent.value = Event(Unit)
    }

    private fun updateTask(task: Task) {
        if (isNewTask) {
            throw RuntimeException("updateTask() was called but task is new.")
        }
        viewModelScope.launch {
            tasksRepository.saveTask(task)
            _taskUpdatedEvent.value = Event(Unit)
        }
    }
}
