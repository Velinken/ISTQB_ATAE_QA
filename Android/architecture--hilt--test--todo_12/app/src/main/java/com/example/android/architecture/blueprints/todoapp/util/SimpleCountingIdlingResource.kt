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
package com.example.android.architecture.blueprints.todoapp.util

import androidx.test.espresso.IdlingResource

import java.util.concurrent.atomic.AtomicInteger

/**
 * An simple counter implementation of [IdlingResource] that determines idleness by
 * maintaining an internal counter.
 * When the counter is 0 - it is considered to be idle, when it is non-zero it is not idle.
 * This is very similar to the way a [java.util.concurrent.Semaphore] behaves.
 *
 * Простая реализация счетчика [IdlingResource], которая определяет праздность путем
 * поддержания внутреннего счетчика.
 * Когда счетчик равен 0 - он считается бездействующим, когда он ненулевой-он не бездействует.
 * Это очень похоже на то, как ведет себя [java.util.concurrent.Semaphore].
 *
 *
 * This class can then be used to wrap up operations that while in progress should block tests from accessing the UI.
 * Затем этот класс можно использовать для завершения операций,
 * которые во время выполнения должны блокировать доступ тестов к пользовательскому интерфейсу.
 */
class SimpleCountingIdlingResource(private val resourceName: String) : IdlingResource {

    private val counter = AtomicInteger(0)

    // written from main thread, read from any thread.
    // записывается из основного потока, читается из любого потока.
    @Volatile
    private var resourceCallback: IdlingResource.ResourceCallback? = null

    override fun getName() = resourceName

    override fun isIdleNow() = counter.get() == 0

    override fun registerIdleTransitionCallback(resourceCallback: IdlingResource.ResourceCallback) {
        this.resourceCallback = resourceCallback
    }

    /**
     * Increments the count of in-flight transactions to the resource being monitored.
     * Увеличивает количество транзакций в полете до отслеживаемого ресурса.
     */
    fun increment() {
        counter.getAndIncrement()
    }

    /**
     * Decrements the count of in-flight transactions to the resource being monitored.
     * If this operation results in the counter falling below 0 - an exception is raised.
     * Уменьшает количество транзакций в полете для отслеживаемого ресурса.
     * Если эта операция приводит к падению счетчика ниже 0 - возникает исключение.
     *
     * @throws IllegalStateException if the counter is below 0.
     * @выбрасывает исключение IllegalStateException, если счетчик ниже 0.
     */
    fun decrement() {
        val counterVal = counter.decrementAndGet()
        if (counterVal == 0) {
            // we've gone from non-zero to zero. That means we're idle now! Tell espresso.
            // мы перешли от ненулевого к нулевому. Это значит, что мы теперь бездельничаем! Скажи эспрессо.
            resourceCallback?.onTransitionToIdle()
        } else if (counterVal < 0) {
            throw IllegalStateException("Counter has been corrupted!")
        }
    }
}
