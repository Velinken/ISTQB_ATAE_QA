/*
 * Copyright 2018, Google Inc.
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

package com.example.android.SimpleCalc;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

/**
 * SimpleCalc is the initial version of SimpleCalcTest.  It has
 * a number of intentional oversights for the student to debug/fix,
 * including input validation (no input, bad number format, div by zero)
 *
 * In addition there is only one (simple) unit test in this app.
 * All the input validation and the unit tests are added as part of the lessons.
 *
 *
 * SimpleCalc - это начальная версия SimpleCalcTest. Она имеет
 * ряд преднамеренных упущений для ученика, чтобы отладить / исправить,
 * включая проверку ввода (нет ввода, неправильный формат числа, деление на ноль)
 *
 * Кроме того, в этом приложении есть только один (простой) модульный тест.
 * Вся проверка входных данных и модульные тесты добавляются как часть уроков.
 */
public class MainActivity extends AppCompatActivity {

    private static final String TAG = "CalculatorActivity";

    // Объявляется переменная  mCalculator которая может хранить ссылку на класс калькулятор
    // (но его еще нет), т.е. в ней будет null
    private Calculator mCalculator;
    // Объявляется переменная  mOperandOneEditText которая может хранить ссылку на поле  EditText
    // (но еще не привязан), т.е. в нем будет null
    private EditText mOperandOneEditText;
    private EditText mOperandTwoEditText;

    private TextView mResultTextView;

    // Стандартная конструкция Android для раздувания макета - она обязательна
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Раздуваем макет из activity_main.xml
        setContentView(R.layout.activity_main);

        // Initialize the calculator class and all the views
        // Инициализировать класс калькулятора и все представления
        // Создать новый экземпляр класса Calculator и ссылку на него загнать в mCalculator
        mCalculator = new Calculator();
        // В ссылки созданные на верху загнать ссылки на поля xml
        mResultTextView = findViewById(R.id.operation_result_text_view);
        mOperandOneEditText = findViewById(R.id.operand_one_edit_text);
        mOperandTwoEditText = findViewById(R.id.operand_two_edit_text);
    }

    /**
     * OnClick method called when the add Button is pressed.
     * Метод OnClick вызывается при нажатии кнопки суммирования.
     * Стоит в xml в onClick поэтому и вызывается.
     */
    public void onAdd(View view) {
        // кнопка вызывает add, а add вызывает метод compute и передает ему, что хотят складывать см. ниже
        // а compute делает все остальное: берет первое число из поля, второе число из поля, складывает
        // результат запихивает в выводное поле
        compute(Calculator.Operator.ADD);
    }


    /**
     * OnClick method called when the subtract Button is pressed.
     * Метод OnClick вызывается при нажатии кнопки вычитания
     * Стоит в xml в onClick поэтому и вызывается.
     */
    public void onSub(View view) {
        compute(Calculator.Operator.SUB);
    }

    /**
     * OnClick method called when the divide Button is pressed.
     * Метод OnClick вызывается при нажатии кнопки деления.
     * Стоит в xml в onClick поэтому и вызывается.
     */
    public void onDiv(View view) {
        try {
            compute(Calculator.Operator.DIV);
        } catch (IllegalArgumentException iae) {
            Log.e(TAG, "IllegalArgumentException", iae);
            mResultTextView.setText(getString(R.string.computationError));
        }
    }

    /**
     * OnClick method called when the multiply Button is pressed.
     * Метод OnClick вызывается при нажатии кнопки умножения
     * Стоит в xml в onClick поэтому и вызывается.
     */
    public void onMul(View view) {
        compute(Calculator.Operator.MUL);
    }

    // внутренний метод и вызывается из add, sub, div, mul
    // на вход он получает какую операцию ему делать
    /*
    Все определенные android:onClick обработчики кликов вызывают закрытый compute()метод с
    именем операции в качестве одного из значений из Calculator.Operator перечисления.
compute()Метод вызывает private метод getOperand()(который , в свою очередь ,
вызывает getOperandText()) для извлечения числовых значений из EditText элементов.
Затем compute()метод использует в switch имени операнда для вызова соответствующего
метода в Calculator instance ( mCalculator).
Методы вычисления в Calculator классе выполняют фактическую арифметику и возвращают значение.
Последняя часть compute()метода обновляет TextView результат вычисления.
     */
    private void compute(Calculator.Operator operator) {
        double operandOne;
        double operandTwo;
        // читаем числа из полей через прерывание (если ошибка, то будет catch)
        try {
            // getOperand метод-функция см. ниже
            operandOne = getOperand(mOperandOneEditText);
            operandTwo = getOperand(mOperandTwoEditText);
        } catch (NumberFormatException nfe) {
            Log.e(TAG, "NumberFormatException", nfe);
            mResultTextView.setText(getString(R.string.computationError));
            return;
        }
        // Объявляется result как строка, что бы заполнить, а потом вывести.
        String result;
        // Выбор по enum что именно передано в compute
        switch (operator) {
            case ADD:
                // получив double преобразуем его в стринг и запихиваем в result
                result = String.valueOf(
                        // метод класса Calculator и ему передается два операнда, а он вернет double результат
                        mCalculator.add(operandOne, operandTwo));
                break; // выйти из switch вниз дальше, т.е. на setText
            case SUB:
                // получив double преобразуем его в стринг и запихиваем в result
                result = String.valueOf(
                        // метод класса Calculator и ему передается два операнда, а он вернет double результат
                        mCalculator.sub(operandOne, operandTwo));
                break; // выйти из switch вниз дальше, т.е. на setText
            case DIV:
                // получив double преобразуем его в стринг и запихиваем в result
                result = String.valueOf(
                        // метод класса Calculator и ему передается два операнда, а он вернет double результат
                        mCalculator.div(operandOne, operandTwo));
                break; // выйти из switch вниз дальше, т.е. на setText
            case MUL:
                // получив double преобразуем его в стринг и запихиваем в result
                result = String.valueOf(
                        // метод класса Calculator и ему передается два операнда, а он вернет double результат
                        mCalculator.mul(operandOne, operandTwo));
                break; // выйти из switch вниз дальше, т.е. на setText
            default:
                result = getString(R.string.computationError);
                break; // выйти из switch вниз дальше, т.е. на setText
        }
        // когда вышли из switch сюда с заполненным текстовым result
        // в поле TextView запихать этот текстовый result, а xml его высветит
        mResultTextView.setText(result);
    }

    /**
     * @return the operand value entered in an EditText as double.
     * значение операнда, введенное в EditText как double.
     */
    private static Double getOperand(EditText operandEditText) {
        String operandText = getOperandText(operandEditText);
        // Мое первое исправление: Если строка пустая, то будет ноль
        if (operandText.equals("")) {
                operandText="0";
        }

        return Double.valueOf(operandText);
    }

    /**
     * @return the operand text which was entered in an EditText.
     * текст операнда, введенный в EditText.
     * зовется из getOperand см. выше
     */
    private static String getOperandText(EditText operandEditText) {
        return operandEditText.getText().toString();
    }
}