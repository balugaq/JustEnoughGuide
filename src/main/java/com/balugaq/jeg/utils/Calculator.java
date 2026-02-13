/*
 * Copyright (c) 2024-2026 balugaq
 *
 * This file is part of JustEnoughGuide, available under MIT license.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * - The above copyright notice and this permission notice shall be included in
 *   all copies or substantial portions of the Software.
 * - The author's name (balugaq or 大香蕉) and project name (JustEnoughGuide or JEG) shall not be
 *   removed or altered from any source distribution or documentation.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */

package com.balugaq.jeg.utils;

import org.jspecify.annotations.NullMarked;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

/**
 * @author balugaq
 * @since 2.0
 */
@NullMarked
public class Calculator {
    private static final Map<String, Integer> PRIORITY = new HashMap<>();

    static {
        PRIORITY.put("~", 4);
        PRIORITY.put("!", 4);

        PRIORITY.put("&", 3);
        PRIORITY.put("^", 3);
        PRIORITY.put("|", 3);
        PRIORITY.put("<<", 3);
        PRIORITY.put(">>", 3);

        PRIORITY.put("**", 3);
        PRIORITY.put("*", 2);
        PRIORITY.put("/", 2);
        PRIORITY.put("+", 1);
        PRIORITY.put("-", 1);
        PRIORITY.put("(", 0);
    }

    @SuppressWarnings("DuplicatedCode")
    public static BigDecimal calculate(String expression) throws NumberFormatException {
        if (expression == null || expression.trim().isEmpty()) {
            throw new NumberFormatException("Empty expression");
        }

        String expr = replaceBrackets(expression);
        expr = replaceUnits(expr);

        expr = expr.replaceAll("\\s+", "");
        expr = expr.replaceAll("_", "");

        expr = completeParentheses(expr);

        if (!isValidParentheses(expr)) {
            throw new NumberFormatException("Invalid expression");
        }

        Deque<BigDecimal> numStack = new ArrayDeque<>();
        Deque<String> opStack = new ArrayDeque<>();

        int i = 0;
        int n = expr.length();

        while (i < n) {
            char c = expr.charAt(i);

            if (Character.isDigit(c) || c == '.') {
                int j = i;
                while (j < n && (Character.isDigit(expr.charAt(j)) || expr.charAt(j) == '.')) {
                    j++;
                }

                boolean isPercentage = false;
                if (j < n && expr.charAt(j) == '%') {
                    isPercentage = true;
                    j++;
                }

                String numStr = expr.substring(i, j - (isPercentage ? 1 : 0));
                BigDecimal num = parseNumber(numStr);

                if (isPercentage) {
                    num = num.divide(new BigDecimal("100"), 10, RoundingMode.HALF_UP);
                }

                numStack.push(num);
                i = j;
            }
            else if (c == '(') {
                opStack.push(String.valueOf(c));
                i++;
            }
            else if (c == ')') {
                String pk = opStack.peek();
                if (pk == null) {
                    throw new NumberFormatException("Brackets doesn't match: " + expression);
                }
                
                while (!pk.equals("(")) {
                    calculateTop(numStack, opStack);
                }
                opStack.pop();
                i++;
            }
            else if ((c == '<' || c == '>') && i + 1 < n && expr.charAt(i + 1) == c) {
                String op = expr.substring(i, i + 2);
                while (!opStack.isEmpty() && getPriority(opStack.peek()) >= getPriority(op)) {
                    calculateTop(numStack, opStack);
                }
                opStack.push(op);
                i += 2;
            }
            else if (c == '~' || c == '!') {
                String op = String.valueOf(c);
                opStack.push(op);
                i++;
            }
            else if (isOperator(String.valueOf(c))) {
                if (c == '+' && (i == 0 || expr.charAt(i - 1) == '(' || isOperator(String.valueOf(expr.charAt(i - 1))))) {
                    if (i + 1 >= n || (!Character.isDigit(expr.charAt(i + 1)) && expr.charAt(i + 1) != '.')) {
                        throw new NumberFormatException("Invalid positive signature: " + expression);
                    }

                    int j = i + 1;
                    while (j < n && (Character.isDigit(expr.charAt(j)) || expr.charAt(j) == '.')) {
                        j++;
                    }

                    boolean isPercentage = false;
                    if (j < n && expr.charAt(j) == '%') {
                        isPercentage = true;
                        j++;
                    }

                    String numStr = expr.substring(i + 1, j - (isPercentage ? 1 : 0));
                    BigDecimal num = parseNumber(numStr);

                    if (isPercentage) {
                        num = num.divide(new BigDecimal("100"), 10, RoundingMode.HALF_UP);
                    }

                    numStack.push(num);
                    i = j;
                }
                else if (c == '-' && (i == 0 || expr.charAt(i - 1) == '(' || isOperator(String.valueOf(expr.charAt(i - 1))))) {
                    if (i + 1 >= n || (!Character.isDigit(expr.charAt(i + 1)) && expr.charAt(i + 1) != '.')) {
                        throw new NumberFormatException("Invalid negative signature: " + expression);
                    }

                    int j = i + 1;
                    while (j < n && (Character.isDigit(expr.charAt(j)) || expr.charAt(j) == '.')) {
                        j++;
                    }

                    boolean isPercentage = false;
                    if (j < n && expr.charAt(j) == '%') {
                        isPercentage = true;
                        j++;
                    }

                    String numStr = "-" + expr.substring(i + 1, j - (isPercentage ? 1 : 0));
                    BigDecimal num = parseNumber(numStr);

                    if (isPercentage) {
                        num = num.divide(new BigDecimal("100"), 10, RoundingMode.HALF_UP);
                    }

                    numStack.push(num);
                    i = j;
                } else {
                    String op = String.valueOf(c);
                    while (!opStack.isEmpty() && getPriority(opStack.peek()) >= getPriority(op)) {
                        calculateTop(numStack, opStack);
                    }
                    opStack.push(op);
                    i++;
                }
            }
            else {
                throw new NumberFormatException("Invalid character: " + c);
            }
        }

        while (!opStack.isEmpty()) {
            calculateTop(numStack, opStack);
        }

        if (numStack.size() != 1) {
            throw new NumberFormatException("Invalid expression: " + expression);
        }

        return numStack.pop();
    }

    @SuppressWarnings("RegExpRedundantEscape")
    private static String replaceBrackets(String expr) {
        return expr
            .replaceAll("[\\[\\{（【《]", "(")
            .replaceAll("[\\]\\}）】》]", ")");
    }

    private static String replaceUnits(String expr) {
        return expr
            .replaceAll("[Hh]", "*100")
            .replaceAll("[Kk]", "*1000")
            .replaceAll("[Ww]", "*10000")
            .replaceAll("[Mm]", "*1_000_000")
            .replaceAll("[Bb]", "*1_000_000_000")
            .replaceAll("[Tt]", "*1_000_000_000_000")
            .replaceAll("[Qq]", "*1_000_000_000_000_000");
    }

    private static String completeParentheses(String expr) {
        int openCount = 0;
        int closeCount = 0;

        for (char c : expr.toCharArray()) {
            if (c == '(') {
                openCount++;
            } else if (c == ')') {
                closeCount++;
            }
        }

        int diff = openCount - closeCount;

        if (diff > 0) {
            expr = expr + ")".repeat(diff);
        }
        else if (diff < 0) {
            expr = "(".repeat(Math.max(0, -diff)) + expr;
        }

        return expr;
    }

    private static BigDecimal parseNumber(String numStr) throws NumberFormatException {
        try {
            if (numStr.endsWith(".")) {
                numStr += "0";
            }
            if (numStr.startsWith(".") && numStr.length() > 1) {
                numStr = "0" + numStr;
            }
            return new BigDecimal(numStr);
        } catch (NumberFormatException e) {
            throw new NumberFormatException("Invalid number: " + numStr);
        }
    }

    private static void calculateTop(Deque<BigDecimal> numStack, Deque<String> opStack) throws NumberFormatException, ArithmeticException {
        String op = opStack.pop();

        if (op.equals("~") || op.equals("!")) {
            if (numStack.isEmpty()) {
                throw new NumberFormatException("Invalid expression");
            }

            BigDecimal a = numStack.pop();
            long aLong = a.longValue();
            long resultLong = switch (op) {
                case "~" ->                    ~aLong;
                case "!" ->                    (aLong == 0) ? 1 : 0;
                default -> throw new NumberFormatException("Invalid symbol: " + op);
            };

            numStack.push(new BigDecimal(resultLong));
            return;
        }

        if (numStack.size() < 2) {
            throw new NumberFormatException("Invalid expression");
        }

        BigDecimal b = numStack.pop();
        BigDecimal a = numStack.pop();
        BigDecimal result;

        switch (op) {
            case "+":
                result = a.add(b);
                break;
            case "-":
                result = a.subtract(b);
                break;
            case "*":
                result = a.multiply(b);
                break;
            case "**":
                result = a.pow(b.intValue());
                break;
            case "/":
                if (b.compareTo(BigDecimal.ZERO) == 0) {
                    throw new ArithmeticException("Divided by 0");
                }
                result = a.divide(b, 10, RoundingMode.HALF_UP);
                break;
            case "&":
                long aLongAnd = a.longValue();
                long bLongAnd = b.longValue();
                result = new BigDecimal(aLongAnd & bLongAnd);
                break;
            case "|":
                long aLongOr = a.longValue();
                long bLongOr = b.longValue();
                result = new BigDecimal(aLongOr | bLongOr);
                break;
            case "^":
                long aLongXor = a.longValue();
                long bLongXor = b.longValue();
                result = new BigDecimal(aLongXor ^ bLongXor);
                break;
            case "<<":
                long aLongLsh = a.longValue();
                long bLongLsh = b.longValue();
                result = new BigDecimal(aLongLsh << bLongLsh);
                break;
            case ">>":
                long aLongRsh = a.longValue();
                long bLongRsh = b.longValue();
                result = new BigDecimal(aLongRsh >> bLongRsh);
                break;
            default:
                throw new NumberFormatException("Invalid symbol: " + op);
        }

        numStack.push(result);
    }

    private static int getPriority(String op) {
        return PRIORITY.getOrDefault(op, 0);
    }

    private static boolean isValidParentheses(String expr) {
        Deque<Character> stack = new ArrayDeque<>();
        for (char c : expr.toCharArray()) {
            if (c == '(') {
                stack.push(c);
            } else if (c == ')') {
                if (stack.isEmpty() || stack.pop() != '(') {
                    return false;
                }
            }
        }
        return stack.isEmpty();
    }

    private static boolean isOperator(String op) {
        return PRIORITY.containsKey(op);
    }
}