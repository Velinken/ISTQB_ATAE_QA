package com.example.android.architecture.blueprints.todoapp.statistics

import com.example.android.architecture.blueprints.todoapp.data.Task
import junit.framework.TestCase.assertEquals
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.Is.`is`
import org.junit.Test

class StatisticsUtilsTest {
    // Задача оттестировать какого-то класса или модуля или файла в котором это лежит.
    // Для этого организуем тестовый класс и в него вставляем тесты.
    // А внутрь каждого теста вставляем кучу проверок.
    @Test
    fun getActiveAndCompletedStats_noCompleted_returnsHundredZero() {
        // Create an active task
        //Задание: Оттестировать функцию getActiveAndCompletedStats(tasks) на правильность подсчета активных и закрытых задачь
        val tasks = listOf<Task>(
                Task("title", "desc", isCompleted = false),
                // Итого имеем список с именем таскс из одной не законченной задачи
        )
        // Call your function мы завем функцию программиста, которую тестируем getActiveAndCompletedStats
        // и передаем ему этот список таскс из одной незаконченной задачи
        // берет этот список как буд-то от других частей программы и возвращает % законченных задачь и процент не законченных задач
        // ** на самом деле он возвращает дата класс из % % и мы его должны принять и принимаем в ответ
        // организуем пременнную result типа дата класс StatsResult и будем их проверять.
        val result = getActiveAndCompletedStats(tasks)
        // Вот она вернула этот самый класс с двумя % %.
        // Check the result WITH JUnit4
        // result.completedTasksPercent - это первое число, которое вернул и мы его проверяем
        assertEquals(result.completedTasksPercent, 0f)
        // result.activeTasksPercent - это второе число, которое вернул и мы его проверяем
        assertEquals(result.activeTasksPercent, 100f)
        val tasksh = listOf<Task>(
                Task("title", "desc", isCompleted = false),
                Task("title25", "desc", isCompleted = true)
        )
        val resulth = getActiveAndCompletedStats(tasksh)
        // WITH Hamcrest
        assertThat(resulth.activeTasksPercent, `is`(50f))
        assertThat(resulth.completedTasksPercent, `is`(50f))
    }

    // Задача оттестировать какого-то класса или модуля или файла в котором это лежит.
    /**
     * Напишите тесты, когда у вас есть обычный список задач:
     * Если есть одна завершенная задача и нет активных задач, activeTasks должен быть 0f
     * процент выполненных задач и процент выполненных задач 100f.
     */
    // Для этого организуем тестовый класс и в него вставляем тесты.
    // А внутрь каждого теста вставляем кучу проверок.
    @Test
    fun getActiveAndCompletedStats_noactiveTasks_returnsZeroHundred() {
        // Create an active task
        //Задание: Оттестировать функцию getActiveAndCompletedStats(tasks) на правильность подсчета активных и закрытых задачь
        val tasks = listOf<Task>(
                Task("title", "desc", isCompleted = true),
                // Итого имеем список с именем таскс из одной не законченной задачи
        )
        // Call your function мы завем функцию программиста, которую тестируем getActiveAndCompletedStats
        // и передаем ему этот список таскс
        val result = getActiveAndCompletedStats(tasks)
        // WITH Hamcrest
        assertThat(result.activeTasksPercent, `is`(0f))
        assertThat(result.completedTasksPercent, `is`(100f))
    }

    /**
     * Напишите тесты, когда у вас есть обычный список задач:
     * Если есть две завершенные задачи и три активных задачи, должен быть процент выполненных 40f
     * и активный процент 60f.
     */
    // Для этого организуем тестовый класс и в него вставляем тесты.
    // А внутрь каждого теста вставляем кучу проверок.
    @Test
    fun getActiveAndCompletedStats_twoCompletedAndthreeActiveTasks_returns_40_60() {
        // Create an active task
        //Задание: Оттестировать функцию getActiveAndCompletedStats(tasks) на правильность подсчета активных и закрытых задачь
        val tasks = listOf<Task>(
                Task("title1", "desc1", isCompleted = false),
                Task("title2", "desc2", isCompleted = true),
                Task("title3", "desc3", isCompleted = false),
                Task("title4", "desc4", isCompleted = true),
                Task("title5", "desc5", isCompleted = false),
                //    Task("title3", "desc3", isCompleted = false), //будет ошибка
                // Итого имеем список с именем таскс из одной не законченной задачи
        )
        // Call your function мы завем функцию программиста, которую тестируем getActiveAndCompletedStats
        // и передаем ему этот список таскс
        val result = getActiveAndCompletedStats(tasks)
        // WITH Hamcrest
        assertThat(result.activeTasksPercent, `is`(60f))
        assertThat(result.completedTasksPercent, `is`(40f))
    }


    /**
    Вместо того, чтобы начинать с исправления ошибки, вы начнете с написания тестов. Затем вы можете подтвердить, что у вас есть тесты, защищающие вас от случайного повторного появления этих ошибок в будущем.
    Если есть пустой список ( emptyList()), тогда оба процента должны быть 0f.
    Если при загрузке задач произошла ошибка, список будет null, и оба процента должны быть 0f.
    Запустите тесты и убедитесь, что они не работают :
     */
    // Для этого организуем тестовый класс и в него вставляем тесты.
    // А внутрь каждого теста вставляем кучу проверок.
    /**
     * Применяем TDD, т.е. сначала пишем тест, который не работает, а потом правим код программера, что бы тест проходил
     * Следует четко разделять два подхода:
     * 1. Обычное стандартное написание теста - тогда программист пишет объект и тестер никогда его не трогает. Наоборот иногда можно.
     * 2. TDD тестер пишет тест и программист никогда его не трогает, он пишет свой модуль так, что бы тест проходил. Наоборот иногда можно.
     * Но лучше и самое надежное что программист  никогда не трогает тест, а тестер никогда не трогает объект. Только читает.
     * Но это никогда не получается.
     * НО в данном случае, я и программист и тестер. Поэтому полезли в объект.
     */
    @Test
    fun getActiveAndCompletedStats_emptyList_returns0_0() {
        // Create an active task
        //Задание: Оттестировать функцию getActiveAndCompletedStats(tasks) на правильность подсчета активных и закрытых задачь
        val tasks = listOf<Task>()
        // Call your function мы завем функцию программиста, которую тестируем getActiveAndCompletedStats
        // и передаем ему этот список таскс
        val result = getActiveAndCompletedStats(tasks)
        // WITH Hamcrest
        assertThat(result.activeTasksPercent, `is`(0f))
        assertThat(result.completedTasksPercent, `is`(0f))
    }

    /**
     * Получив ошибку, что пустой список не дает 0/0 при TDD, я тестер пишу программеру задание
     * исправь объект, что бы проходил тест.
     * Я программер, прочитав это задание - иду в этот объект
     * и начинаю ее исправлять, что бы тест стал зеленым.
     */
    /**
     * Программер изменил код сам запустил не изменяя тест, получил зеленое, сдал код и побежал за денежкой.
     * Вывод не тестер пишет за программером, а программер пишет за тестером
     * Можно программера иметь junior+, а не сеньора-.
     * Но, как всегда обратная сторона медали, что тестер должен быть сеньором, или над ним стоит сеньор, а тестер его прекрасно понимает.
     * Тестер описывает всю структуру тестами не имея объектами.
     * Побочное явление - имена придумывает не junior, а тестер или сеньёр
     */
}