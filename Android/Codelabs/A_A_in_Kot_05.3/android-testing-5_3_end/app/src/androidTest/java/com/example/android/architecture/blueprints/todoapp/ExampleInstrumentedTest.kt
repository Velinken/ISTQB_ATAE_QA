package com.example.android.architecture.blueprints.todoapp

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*

/**
 * Instrumented test, which will execute on an Android device.
 * Инструментальный тест, который будет выполняться на Android-устройстве.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun useAppContext() {
        // Context of the app under test.
        // Контекст тестируемого приложения.  InstrumentationRegistry
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        //Thread.sleep(20000)
        //assertEquals("com.example.android.architecture.blueprints.todoapp", appContext.packageName)
        assertEquals("com.example.android.architecture.blueprints.reactive", appContext.packageName)
    }
}