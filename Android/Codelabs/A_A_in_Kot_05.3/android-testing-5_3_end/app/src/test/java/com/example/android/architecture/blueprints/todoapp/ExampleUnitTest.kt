package com.example.android.architecture.blueprints.todoapp

import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 * Пример локального модульного теста, который будет выполняться на машине разработки (хосте).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
// A test class is just a normal class
// Тестовый класс-это просто обычный класс
class ExampleUnitTest {

    // Each test is annotated with @Test (this is a Junit annotation)
    // Каждом тесте есть аннотация @test (это в JUnit аннотации)
    @Test
    fun addition_isCorrect() {
        // Here you are checking that 4 is the same as 2+2
        // Здесь вы проверяете, что 4-это то же самое, что 2+2
        assertEquals(4, 2 + 2)
        // assert - Утверждение
//        assertEquals(3, 1 + 1) // This should fail
    }
}