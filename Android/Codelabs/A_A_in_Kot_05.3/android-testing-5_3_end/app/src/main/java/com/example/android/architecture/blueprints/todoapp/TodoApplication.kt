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

package com.example.android.architecture.blueprints.todoapp

import android.app.Application
import androidx.databinding.ktx.BuildConfig
import com.example.android.architecture.blueprints.todoapp.data.source.TasksRepository
import timber.log.Timber
import timber.log.Timber.DebugTree

/**
 * An application that lazily provides a repository. Note that this Service Locator pattern is
 * used to simplify the sample. Consider a Dependency Injection framework.
 * Приложение, которое лениво предоставляет репозиторий. Обратите внимание, что этот шаблон локатора служб
 * используется для упрощения выборки. Рассмотрим фреймворк внедрения зависимостей.
 *
 * Also, sets up Timber in the DEBUG BuildConfig. Read Timber's documentation for production setups.
 * Кроме того, настраивает древесину в конфигурации DEBUG Build config.
 * Ознакомьтесь с документацией Timber'S для производственных установок.
 */
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
