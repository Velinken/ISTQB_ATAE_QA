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

import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.testing.FragmentScenario
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.IdlingResource
//import androidx.fragment.app.Fragment
//import androidx.fragment.app.FragmentActivity
//import androidx.fragment.app.testing.FragmentScenario
//import androidx.test.core.app.ActivityScenario
//import androidx.test.espresso.IdlingResource
import java.util.UUID

/**
 * An espresso idling resource implementation that reports idle status for all data binding
 * layouts. Data Binding uses a mechanism to post messages which Espresso doesn't track yet.
 * Эспрессо холостого хода осуществления ресурса, отчетов о состоянии холостого хода для привязки данных
 * расположения. Привязка данных использует механизм для отправки сообщений, которые Espresso еще не отслеживает.
 *
 * Since this application only uses fragments, the resource only checks the fragments and their
 * children instead of the whole view tree.
 * Поскольку это приложение использует только фрагменты, ресурс проверяет только фрагменты и их
 * Дети вместо всего дерева представлений.
 */
class DataBindingIdlingResource : IdlingResource {
    // list of registered callbacks
    // список зарегистрированных обратных вызовов
    private val idlingCallbacks = mutableListOf<IdlingResource.ResourceCallback>()
    // give it a unique id to workaround an espresso bug where you cannot register/unregister
    // an idling resource w/ the same name.
    // дайте ему уникальный идентификатор, чтобы обойти ошибку Эспрессо, когда вы не можете зарегистрироваться/отменить регистрацию
    // ресурс холостого хода с тем же именем.
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
     * Найти все классы привязки во всех доступных в данный момент фрагментах.
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
 * Устанавливает активности от [сценарий активности] с [привязка данных IdlingResource].
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
 * Задает фрагмент [сценарий фрагмент] с [привязка данных IdlingResource].
 */
//fragmentVersion = '1.3.0'
//fragmentKtxVersion = '1.3.0'
// работает с 1.2.5 а с 1.3.0 не знает it
// не используется -- комментарю
/*fun DataBindingIdlingResource.monitorFragment(
    fragmentScenario: FragmentScenario<out Fragment>
) {
    fragmentScenario.onFragment {
        this.activity = it.requireActivity()
    }
}*/
/*
Запускает данное действие в главном потоке текущего действия.
Обратите внимание, что вы никогда не должны сохранять ссылку на фрагмент, переданную в ваше действие, потому что она может быть воссоздана в любое время во время переходов состояний.
Выбрасывание исключения из действия приводит к сбою активности хоста. Вы можете проверить исключение в выходных данных logcat.
Этот метод не может быть вызван из основного потока.
 */
