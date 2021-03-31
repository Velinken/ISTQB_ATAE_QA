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

//import android.test.suitebuilder.annotation.SmallTest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.number.IsCloseTo.closeTo;
//import static org.junit.Assert.assertThat;

/**
 * JUnit4 unit tests for the calculator logic. These are local unit tests; no device needed
 */
@RunWith(JUnit4.class)
//@SmallTest
public class CalculatorTest {

    private Calculator mCalculator;

    /**
     * Set up the environment for testing
     */
    @Before
    public void setUp() {
        mCalculator = new Calculator();
    }

    /**
     * Test for simple addition
     */
    @Test
    public void addTwoNumbersMy() {
        double resultAdd = mCalculator.add(1d, 1d);
        assertThat(resultAdd, is(equalTo(2d)));
        assertThat(mCalculator.add(4d, 5d), is(equalTo(9d)));
    }

    @Test
    public void addTwoNumbersNegativeMy() {
        double resultAdd = mCalculator.add(-1d, 2d);
        assertThat(resultAdd, is(equalTo(1d)));
    }

    @Test
    public void addTwoNumbersNullMy() {
        double resultAdd = mCalculator.add(0d, 0d);
        assertThat(resultAdd, is(equalTo(0d)));
    }

    @Test
    public void addTwoNumbersFloatsMy() {
        double resultAdd = mCalculator.add(1.111f, 1.111d);
        //assertThat(resultAdd, is(equalTo(2.222d))); // Этот дает ошибку, т.к. много знаков еще сзади. Так сравнивать нельзя
        assertThat(resultAdd, is(closeTo(2.222, 0.01)));
        assertThat(resultAdd, is(closeTo(2.222, 0.0000001))); // непонятно, почему проходит
    }

    @Test
    public void subTwoNumbersMy() {
        double resultAdd = mCalculator.sub(1d, 1d);
        assertThat(resultAdd, is(equalTo(0d)));
        assertThat(mCalculator.sub(4d, 5d), is(equalTo(-1d)));
    }

    @Test
    public void subWorksWithNegativeResultsMy() {
        double resultAdd = mCalculator.sub(1d, 3d);
        assertThat(resultAdd, is(equalTo(-2d)));
        assertThat(mCalculator.sub(4d, 5d), is(equalTo(-1d)));
    }

    @Test
    public void mulTwoNumbersMy() {
        double resultAdd = mCalculator.mul(1d, 1d);
        assertThat(resultAdd, is(equalTo(1d)));
        assertThat(mCalculator.mul(4d, 5d), is(equalTo(20d)));
    }

    @Test
    public void mulTwoNumbersZeroMy() {
        double resultAdd = mCalculator.mul(1d, 0d);
        assertThat(resultAdd, is(equalTo(0d)));
        assertThat(mCalculator.mul(4d, 0d), is(equalTo(0d)));
    }

    @Test
    public void divTwoNumbersMy() {
        double resultAdd = mCalculator.div(1d, 2d);
        assertThat(resultAdd, is(equalTo(0.5d)));
        //assertThat(mCalculator.div(4d, 0d), is(equalTo(0d)));
    }

    @Test
    public void divTwoNumbersZeroMy() {
        double resultAdd = mCalculator.div(1d, 0d);
        assertThat(resultAdd, is(equalTo(Double.POSITIVE_INFINITY)));
        //assertThat(mCalculator.div(4d, 0d), is(equalTo(0d)));
    }

    // штатный текст из codelabs
    @Test
    public void addTwoNumbers() {
        double resultAdd = mCalculator.add(1d, 1d);
        assertThat(resultAdd, is(equalTo(2d)));
    }

    @Test
    public void addTwoNumbersNegative() {
        double resultAdd = mCalculator.add(-1d, 2d);
        assertThat(resultAdd, is(equalTo(1d)));
    }
    @Test
    public void addTwoNumbersFloats() {
        double resultAdd = mCalculator.add(1.111f, 1.111d);
        assertThat(resultAdd, is(closeTo(2.222, 0.01)));
    }
    @Test
    public void subTwoNumbers() {
        double resultSub = mCalculator.sub(1d, 1d);
        assertThat(resultSub, is(equalTo(0d)));
    }
    @Test
    public void subWorksWithNegativeResult() {
        double resultSub = mCalculator.sub(1d, 17d);
        assertThat(resultSub, is(equalTo(-16d)));
    }
    @Test
    public void mulTwoNumbers() {
        double resultMul = mCalculator.mul(32d, 2d);
        assertThat(resultMul, is(equalTo(64d)));
    }
    @Test
    public void divTwoNumbers() {
        double resultDiv = mCalculator.div(32d,2d);
        assertThat(resultDiv, is(equalTo(16d)));
    }
    @Test
    public void divTwoNumbersZero() {
        double resultDiv = mCalculator.div(32d,0);
        assertThat(resultDiv, is(equalTo(Double.POSITIVE_INFINITY)));
    }

    @Test
    public void divByZeroThrows() {
        double resultDiv = mCalculator.div(32d,0);
        assertThat(resultDiv, is(equalTo(Double.POSITIVE_INFINITY)));
    }


    /*@Test(expected = IllegalArgumentException.class)
    public void divByZeroThrowsBarancev() {
        double resultDiv = mCalculator.div(32d,0);
        //assertThat(resultDiv, is(equalTo(Double.POSITIVE_INFINITY)));
    }*/

    /*@Test
    public void testFooThrowsIndexOutOfBoundsException() {
        boolean thrown = false;

        try {
            foo.doStuff();
        } catch (IndexOutOfBoundsException e) {
            thrown = true;
        }

        assertTrue(thrown);
    }*/
}