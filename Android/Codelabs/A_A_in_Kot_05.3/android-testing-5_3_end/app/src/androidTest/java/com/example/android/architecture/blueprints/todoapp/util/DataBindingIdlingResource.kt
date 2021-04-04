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


import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.testing.FragmentScenario
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.IdlingResource
import java.util.UUID

/**
 * Вы написали ресурс холостого хода, поэтому Espresso ожидает загрузки данных.
 *  Затем вы создадите собственный ресурс холостого хода для привязки данных.
 * Это необходимо сделать, потому что Espresso не работает автоматически с библиотекой привязки данных.
 * Это связано с тем, что привязка данных использует другой механизм ( класс Choreographer ) для синхронизации обновлений представления.
 * Таким образом, Espresso не может сказать, когда представление, обновленное через привязку данных, завершило обновление.
 *
 */
/**
 * An espresso idling resource implementation that reports idle status for all data binding layouts.
 * * Реализация ресурса espresso на холостом ходу, которая сообщает о состоянии простоя для всех макетов привязки данных.
 * Data Binding uses a mechanism to post messages which Espresso doesn't track yet.
 * Привязка данных использует механизм для отправки сообщений, которые Espresso еще не отслеживает.
 *
 * Since this application only uses fragments, the resource only checks the fragments and their children
 * instead of the whole view tree.
 * Поскольку это приложение использует только фрагменты, ресурс проверяет только фрагменты и их дочерние элементы,
 * а не все дерево представлений.
 */
class DataBindingIdlingResource : IdlingResource {
    // list of registered callbacks
    // список зарегистрированных обратных вызовов
    private val idlingCallbacks = mutableListOf<IdlingResource.ResourceCallback>()
    // give it a unique id to workaround an espresso bug
    // where you cannot register/unregister an idling resource w/ the same name.
    // дайте ему уникальный идентификатор, чтобы обойти ошибку Эспрессо,
    // когда вы не можете зарегистрировать/отменить регистрацию холостого ресурса с тем же именем.
    private val id = UUID.randomUUID().toString()
    // holds whether isIdle is called and the result was false. We track this to avoid calling
    // onTransitionToIdle callbacks if Espresso never thought we were idle in the first place.
    // удерживает, вызывается ли visible, и результат был ложным. Мы отслеживаем это, чтобы избежать звонков
    // при переходе на холостые обратные вызовы, если Эспрессо никогда не думал, что мы простаиваем в первую очередь.
    private var wasNotIdle = false

    lateinit var activity: FragmentActivity

    override fun getName() = "DataBinding $id"

    override fun isIdleNow(): Boolean {
        val idle = !getBindings().any { it.hasPendingBindings() }
        @Suppress("LiftReturnOrAssignment")
        if (idle) {
            if (wasNotIdle) {
                // notify observers to avoid espresso race detector
                idlingCallbacks.forEach { it.onTransitionToIdle() }
            }
            wasNotIdle = false
        } else {
            wasNotIdle = true
            // check next frame
            activity.findViewById<View>(android.R.id.content).postDelayed({
                isIdleNow
            }, 16)
        }
        return idle
    }

    override fun registerIdleTransitionCallback(callback: IdlingResource.ResourceCallback) {
        idlingCallbacks.add(callback)
    }

    /**
     * Find all binding classes in all currently available fragments.
     * Найдите все классы привязки во всех доступных в данный момент фрагментах.
     */
    private fun getBindings(): List<ViewDataBinding> {
        val fragments = (activity as? FragmentActivity)
            ?.supportFragmentManager
            ?.fragments

        val bindings =
            fragments?.mapNotNull {
                it.view?.getBinding()
            } ?: emptyList()
        val childrenBindings = fragments?.flatMap { it.childFragmentManager.fragments }
            ?.mapNotNull { it.view?.getBinding() } ?: emptyList()

        return bindings + childrenBindings
    }
}

private fun View.getBinding(): ViewDataBinding? = DataBindingUtil.getBinding(this)

/**
 * Sets the activity from an [ActivityScenario] to be used from [DataBindingIdlingResource].
 * Найдите все классы привязки во всех доступных в данный момент фрагментах.Задает действие из [сценария действия],
 * которое будет использоваться из [DataBindingIdlingResource].
 */
fun DataBindingIdlingResource.monitorActivity(
    activityScenario: ActivityScenario<out FragmentActivity>
) {
    activityScenario.onActivity {
        this.activity = it
    }
}

/**
 * Sets the fragment from a [FragmentScenario] to be used from [DataBindingIdlingResource].
 * Задает фрагмент из сценария [фрагмента], который будет использоваться из [Data Binding IdlingResource].
 */
/*
fun DataBindingIdlingResource.monitorFragment(fragmentScenario: FragmentScenario<out Fragment>) {
    fragmentScenario.onFragment {
        this.activity = it.requireActivity()
    }
}*/

/**
 * Здесь много чего происходит, но общая идея состоит в том,
 * что ViewDataBindings генерируются всякий раз, когда вы используете макеты привязки данных.
 * В ViewDataBinding«s hasPendingBindings отчеты метод резервного ли привязка данных библиотеки
 * необходимо обновить пользовательский интерфейс , чтобы отразить изменения в данных.
 * Этот ресурс холостого хода считается бездействующим, только если нет ожидающих привязок ни для одного из ViewDataBindings.
 * Наконец, функции расширения DataBindingIdlingResource.monitorFragmentи DataBindingIdlingResource.monitorActivity
 * принимают в FragmentScenarioи ActivityScenario, соответственно.
 * Затем они находят базовое действие и связывают его DataBindingIdlingResource, чтобы вы могли отслеживать состояние макета.
 * Вы должны вызвать один из этих двух методов из своих тестов, иначе DataBindingIdlingResource он ничего не узнает о вашем макете.
 */
