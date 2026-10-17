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

    @SuppressWarnings({"ConstantValue"})
    public static BigDecimal calculate(String expression) throws NumberFormatException {
        if (expression == null || expression.trim().isEmpty()) {
            throw new NumberFormatException("Empty expression");
        }

        String expr = replaceBrackets(expression);
        expr = replaceUnits(expr);

        expr = expr.replaceAll("\\s+", "");
        expr = expr.replace("_", "");

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
                int j = scanNumberEnd(expr, i);

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
            } else if (c == '(') {
                opStack.push(String.valueOf(c));
                i++;
            } else if (c == ')') {
                // 依次弹出运算符求值，直到遇到对应的 "("；
                // 注意 peek 必须在循环内重新取，否则永远停不下来（历史 bug）
                while (!opStack.isEmpty() && !opStack.peek().equals("(")) {
                    calculateTop(numStack, opStack);
                }

                String pk = opStack.peek();
                if (pk == null) {
                    throw new NumberFormatException("Brackets doesn't match: " + expression);
                }

                opStack.pop();
                i++;
            } else if (i + 1 < n && expr.charAt(i + 1) == c && (c == '<' || c == '>' || c == '*')) {
                // 双字符运算符：<<、>>、**
                String op = expr.substring(i, i + 2);
                while (!opStack.isEmpty() && getPriority(opStack.peek()) >= getPriority(op)) {
                    calculateTop(numStack, opStack);
                }
                opStack.push(op);
                i += 2;
            } else if (c == '~' || c == '!') {
                String op = String.valueOf(c);
                opStack.push(op);
                i++;
            } else if (isOperator(String.valueOf(c))) {
                if (c == '+' && (i == 0 || expr.charAt(i - 1) == '(' || isOperator(String.valueOf(expr.charAt(i - 1))))) {
                    if (i + 1 >= n || (!Character.isDigit(expr.charAt(i + 1)) && expr.charAt(i + 1) != '.')) {
                        throw new NumberFormatException("Invalid positive signature: " + expression);
                    }

                    int j = scanNumberEnd(expr, i + 1);

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
                } else if (c == '-' && (i == 0 || expr.charAt(i - 1) == '(' || isOperator(String.valueOf(expr.charAt(i - 1))))) {
                    if (i + 1 >= n || (!Character.isDigit(expr.charAt(i + 1)) && expr.charAt(i + 1) != '.')) {
                        throw new NumberFormatException("Invalid negative signature: " + expression);
                    }

                    int j = scanNumberEnd(expr, i + 1);

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
            } else {
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
        } else if (diff < 0) {
            expr = "(".repeat(Math.max(0, -diff)) + expr;
        }

        return expr;
    }

    /**
     * 扫描从 {@code start} 开始的数字字面量的结束位置（不含）。
     * 除普通小数外，支持科学计数法：{@code e}/{@code E} 后跟可选正负号和至少一位数字
     * （如 {@code 1e3}、{@code 1.5E-2}）；不满足该格式时 {@code e} 不被吞入，
     * 由后续字符处理逻辑按非法字符报错。
     */
    private static int scanNumberEnd(String expr, int start) {
        int n = expr.length();
        int j = start;
        while (j < n && (Character.isDigit(expr.charAt(j)) || expr.charAt(j) == '.')) {
            j++;
        }

        if (j < n && (expr.charAt(j) == 'e' || expr.charAt(j) == 'E')) {
            int k = j + 1;
            if (k < n && (expr.charAt(k) == '+' || expr.charAt(k) == '-')) {
                k++;
            }

            if (k < n && Character.isDigit(expr.charAt(k))) {
                while (k < n && Character.isDigit(expr.charAt(k))) {
                    k++;
                }
                j = k;
            }
        }

        return j;
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
                case "~" -> ~aLong;
                case "!" -> (aLong == 0) ? 1 : 0;
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