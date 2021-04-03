package com.example.android.architecture.blueprints.todoapp.taskdetail

import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.example.android.architecture.blueprints.todoapp.R
import com.example.android.architecture.blueprints.todoapp.ServiceLocator
import com.example.android.architecture.blueprints.todoapp.data.Task
import com.example.android.architecture.blueprints.todoapp.data.source.FakeAndroidTestRepository
import com.example.android.architecture.blueprints.todoapp.data.source.TasksRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@MediumTest
@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
// Это класс для тестирования фрагмента
class TaskDetailFragmentTest {

    private lateinit var repository: TasksRepository

    @Test
    fun activeTaskDetails_DisplayedInUi() = runBlockingTest {
        // GIVEN - Add active (incomplete) task to the DB
        // Создаем задачу типа Башня в Пизе с описанием, что бы высветить ее во фрагменте
        //val activeTask = Task("Active Task", "AndroidX Rocks", false)
        val activeTask = Task("Mow the grass", "Hire a tractor, trace and pay", false)
        repository.saveTask(activeTask)

        // WHEN - Details fragment launched to display task
        // Франменту при вызове надо передать ID, тогда будет вызвана задача
        val bundle = TaskDetailFragmentArgs(activeTask.id).toBundle()
        // Фрагмент загружает, он вывестится на экране для тестового случая.
        launchFragmentInContainer<TaskDetailFragment>(bundle, R.style.AppTheme)
        //Thread.sleep(20000) //это приостановить выполнение на 20 секунд

    }

    @Before
    fun initRepository() {
        repository = FakeAndroidTestRepository()
        ServiceLocator.tasksRepository = repository
    }

    @After
    fun cleanupDb() = runBlockingTest {
        ServiceLocator.resetRepository()
    }
}