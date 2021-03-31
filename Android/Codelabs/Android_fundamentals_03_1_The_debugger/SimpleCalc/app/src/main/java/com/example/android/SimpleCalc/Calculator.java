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

/**
 * Utility class for SimpleCalc to perform the actual calculations.
 * Служебный класс SimpleCalc для выполнения фактических вычислений.
 * все методы операций - public т.е. их можно вызывать снаружи из вне класса
 */
public class Calculator {

    // Available operations
    // операции, которые может выполнять калькулятор, определяются с помощью Operator enum
    // Переменная Operator типа enum - специальный класс перечисление
    public enum Operator {ADD, SUB, DIV, MUL}

    /**
     * Addition operation
     * метод (функция) класса Calculator принимает два плавающих числа и возвращает то же плавающее число
     */
    public double add(double firstOperand, double secondOperand) {
        return firstOperand + secondOperand;
    }

    /**
     * Subtract operation
     * метод (функция) класса Calculator принимает два плавающих числа и возвращает то же плавающее число
     */
    public double sub(double firstOperand, double secondOperand) {
        return firstOperand - secondOperand;
    }

    /**
     * Divide operation
     * метод (функция) класса Calculator принимает два плавающих числа и возвращает то же плавающее число
     */
    public double div(double firstOperand, double secondOperand) {
        return firstOperand / secondOperand;
    }

    /**
     * Multiply operation
     * метод (функция) класса Calculator принимает два плавающих числа и возвращает то же плавающее число
     */
    public double mul(double firstOperand, double secondOperand) {
        return firstOperand * secondOperand;
    }
}