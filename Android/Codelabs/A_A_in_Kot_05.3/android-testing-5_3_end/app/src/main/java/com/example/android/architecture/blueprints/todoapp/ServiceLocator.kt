package com.example.android.architecture.blueprints.todoapp

import android.content.Context
import androidx.annotation.VisibleForTesting
import androidx.room.Room
import com.example.android.architecture.blueprints.todoapp.data.source.DefaultTasksRepository
import com.example.android.architecture.blueprints.todoapp.data.source.TasksDataSource
import com.example.android.architecture.blueprints.todoapp.data.source.TasksRepository
import com.example.android.architecture.blueprints.todoapp.data.source.local.TasksLocalDataSource
import com.example.android.architecture.blueprints.todoapp.data.source.local.ToDoDatabase
import com.example.android.architecture.blueprints.todoapp.data.source.remote.TasksRemoteDataSource
import kotlinx.coroutines.runBlocking

/**
 * Шаблон Service Locator - альтернатива внедрению зависимостей.
 * В этой задаче вы предоставите свой поддельный репозиторий для своего фрагмента с помощью файла ServiceLocator.
 * Это позволит вам написать свой фрагмент и просмотреть тесты интеграции модели.
 *
 * Вы не можете использовать здесь внедрение зависимостей конструктора, как вы это делали раньше,
 * когда вам нужно было предоставить зависимость для модели представления или репозитория.
 * Внедрение зависимостей конструктора требует, чтобы вы построили класс.
 * Фрагменты и действия - это примеры классов, которые вы не создаете и обычно не имеете доступа к конструктору.
 * Поскольку вы не создаете фрагмент, вы не можете использовать внедрение зависимостей конструктора
 * для замены тестового репозитория double ( FakeTestRepository) на фрагмент.
 * Вместо этого используйте шаблон Service Locator. Шаблон Service Locator - альтернатива внедрению зависимостей.
 * Он включает в себя создание одноэлементного класса под названием «Service Locator»,
 * целью которого является предоставление зависимостей как для обычного, так и для тестового кода.
 * В обычном коде приложения ( main исходный набор) все эти зависимости являются обычными зависимостями приложения.
 * Для тестов вы изменяете Service Locator, чтобы предоставить тестовые двойные версии зависимостей.
 */
/**
 * A Service Locator for the [TasksRepository]. This is the prod version, with a the "real" [TasksRemoteDataSource].
 * Локатор служб для [репозитория задач]. Это версия prod, с "реальным" [удаленным источником данных задач].
 */
object ServiceLocator {

    private val lock = Any()
    private var database: ToDoDatabase? = null
    @Volatile  // потому что он может использоваться несколькими потоками
    var tasksRepository: TasksRepository? = null
        // Эта аннотация - способ выразить, что причина, по которой установщик является общедоступным, связана с тестированием.
        @VisibleForTesting set

    //  Либо предоставляет уже существующий репозиторий, либо создает новый.
    fun provideTasksRepository(context: Context): TasksRepository {
        synchronized(this) {
            return tasksRepository ?: createTasksRepository(context)
        }
    }

    // Код для создания нового репозитория. Позвоню createTaskLocalDataSource и создам новую TasksRemoteDataSource
    private fun createTasksRepository(context: Context): TasksRepository {
        val newRepo = DefaultTasksRepository(TasksRemoteDataSource, createTaskLocalDataSource(context))
        tasksRepository = newRepo
        return newRepo
    }

    // Код для создания нового локального источника данных. Позвоню createDataBase.
    private fun createTaskLocalDataSource(context: Context): TasksDataSource {
        val database = database ?: createDataBase(context)
        return TasksLocalDataSource(database.taskDao())
    }

    // Код для создания новой базы данных.
    private fun createDataBase(context: Context): ToDoDatabase {
        val result = Room.databaseBuilder(
            context.applicationContext,
            ToDoDatabase::class.java, "Tasks.db"
        ).build()
        database = result
        return result
    }

    /**
     * Важно: одним из недостатков использования локатора служб является то, что это общий синглтон.
     * Помимо необходимости сбросить состояние локатора сервисов по завершении теста, вы не можете запускать тесты параллельно.
     * Этого не происходит, когда вы используете внедрение зависимостей,
     * что является одной из причин предпочесть внедрение зависимостей конструктора,
     * когда вы можете его использовать.
     * Вы можете прочитать документацию, чтобы узнать больше о компромиссах.
     */
    // Добавьте вызываемый специфичный для тестирования метод, resetRepository
    // который очищает базу данных и устанавливает для репозитория и базы данных значение NULL.
    @VisibleForTesting
    fun resetRepository() {
        synchronized(lock) {
            runBlocking {
                TasksRemoteDataSource.deleteAllTasks()
            }
            // Clear all data to avoid test pollution.
            // Очистите все данные, чтобы избежать загрязнения теста.
            database?.apply {
                clearAllTables()
                close()
            }
            database = null
            tasksRepository = null
        }
    }
}

/*
class TodoApplication : Application() {

    // Важно, чтобы вы всегда создавали только один экземпляр класса репозитория.
    // Чтобы в этом убедиться, вы воспользуетесь локатором служб в классе TodoApplication.
    // назначьте ему репозиторий, полученный с использованием ServiceLocator.provideTaskRepository
    val taskRepository: TasksRepository
        get() = ServiceLocator.provideTasksRepository(this)

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) Timber.plant(DebugTree())
    }
}
 */
/*
fun init() {
        tasksRepository = ServiceLocator.provideTasksRepository(getApplicationContext())
    }
 */