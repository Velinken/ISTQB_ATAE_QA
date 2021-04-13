package com.example.android.architecture.blueprints.todoapp

import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
        assertEquals(2, 1 + 1) // This should fail
        assertEquals(6, 3 + 3) // Исправление для использования новой теперь это из ветки в девелоп
    }
}
/**
 * Создали локальную ветку. Проверим, что при слиянии ничего не пропадет.
 * Все на месте ничего не пропало.
 */
