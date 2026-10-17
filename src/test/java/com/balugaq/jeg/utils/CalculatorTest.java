/*
 * Copyright (c) 2024-2026 balugaq
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *
 */

package com.balugaq.jeg.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * {@link Calculator} 的求值测试，重点覆盖 Issue #81：科学计数法支持。
 *
 * @author balugaq
 * @since 2.1
 */
class CalculatorTest {
    private static void assertCalc(String expression, String expected) {
        BigDecimal actual = Calculator.calculate(expression);
        assertEquals(0, actual.compareTo(new BigDecimal(expected)),
            expression + " 应等于 " + expected + "，实际为 " + actual.toPlainString());
    }

    @Test
    @DisplayName("科学计数法：整数指数")
    void scientificNotation() {
        assertCalc("1e3", "1000");
        assertCalc("2E5", "200000");
        assertCalc("1.5e3", "1500");
        assertCalc("1.5E2", "150");
        assertCalc("1e0", "1");
        assertCalc("3e1", "30");
    }

    @Test
    @DisplayName("科学计数法：显式正负指数")
    void scientificNotationWithSign() {
        assertCalc("1e+3", "1000");
        assertCalc("1e-3", "0.001");
        assertCalc("2.5E-2", "0.025");
        assertCalc("1.5e+2", "150");
    }

    @Test
    @DisplayName("科学计数法：小数点缺省形态")
    void scientificNotationEdgeForms() {
        assertCalc(".5e3", "500");
        assertCalc("1.e3", "1000");
    }

    @Test
    @DisplayName("科学计数法：一元正负号")
    void scientificNotationWithUnarySign() {
        assertCalc("-1.5e3", "-1500");
        assertCalc("-2e-3", "-0.002");
        assertCalc("+2e2", "200");
    }

    @Test
    @DisplayName("科学计数法：参与四则与括号运算")
    void scientificNotationInExpressions() {
        assertCalc("1e3+1", "1001");
        assertCalc("2e3*2", "4000");
        assertCalc("1e6/4", "250000");
        assertCalc("(1e3+1)*2", "2002");
        assertCalc("1e3-5e2", "500");
    }

    @Test
    @DisplayName("科学计数法：与单位后缀、百分号、下划线混用")
    void scientificNotationWithUnitsAndPercentage() {
        assertCalc("2e3k", "2000000");
        assertCalc("1e3m", "1000000000");
        assertCalc("1.5e2%", "1.5");
        assertCalc("1_0e2", "1000");
    }

    @Test
    @DisplayName("科学计数法：调用方使用的 intValue 路径")
    void scientificNotationIntValue() {
        assertEquals(200000, Calculator.calculate("2e5").intValue());
        assertEquals(-1500, Calculator.calculate("-1.5e3").intValue());
    }

    @Test
    @DisplayName("非法科学计数法：e 后无数字按非法表达式报错")
    void invalidScientificNotation() {
        assertThrows(NumberFormatException.class, () -> Calculator.calculate("1e"));
        assertThrows(NumberFormatException.class, () -> Calculator.calculate("1e+"));
        assertThrows(NumberFormatException.class, () -> Calculator.calculate("e3"));
        assertThrows(NumberFormatException.class, () -> Calculator.calculate("1.2.3e2"));
        assertThrows(NumberFormatException.class, () -> Calculator.calculate(""));
    }

    @Test
    @DisplayName("回归：既有求值行为不受影响")
    void existingBehaviorRegression() {
        assertCalc("1+2", "3");
        assertCalc("2*3+4", "10");
        assertCalc("(1+2)*3", "9");
        assertCalc("（1+2）*3", "9");
        assertCalc("2*(3+4)", "14");
        assertCalc("((1+2))*3", "9");
        assertCalc("(-1+2)", "1");
        assertCalc("2*-3", "-6");
        assertCalc("2**10", "1024");
        assertCalc("50%", "0.5");
        assertCalc("1k", "1000");
        assertCalc("1w", "10000");
        assertCalc("1m+1", "1000001");
        assertCalc("1_000+1", "1001");
        assertCalc("1/4", "0.25");
        assertCalc("-5+10", "5");
        assertCalc("~0", "-1");
        assertCalc("1<<3", "8");
        assertCalc("2+3*4", "14");
    }
}
