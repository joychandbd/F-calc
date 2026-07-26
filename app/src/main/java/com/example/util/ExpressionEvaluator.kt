package com.example.util

import kotlin.math.abs
import kotlin.math.roundToInt

object ExpressionEvaluator {

    /**
     * Evaluates a mathematical expression string.
     * Supports digits, decimal points, +, -, × (*), ÷ (/), %, brackets (, ), and fraction divisions.
     */
    fun evaluate(expression: String): Double {
        val sanitized = sanitize(expression)
        if (sanitized.isEmpty()) return 0.0

        val tokens = tokenize(sanitized)
        val rpn = infixToRPN(tokens)
        return evaluateRPN(rpn)
    }

    private fun sanitize(expr: String): String {
        return expr
            .replace("×", "*")
            .replace("÷", "/")
            .replace("−", "-")
            .replace(" ", "")
    }

    private sealed class Token {
        data class Number(val value: Double) : Token()
        data class Operator(val symbol: Char, val precedence: Int, val isRightAssociative: Boolean = false) : Token()
        object LeftParen : Token()
        object RightParen : Token()
    }

    private fun tokenize(expr: String): List<Token> {
        val tokens = mutableListOf<Token>()
        var i = 0
        var expectUnary = true

        while (i < expr.length) {
            val c = expr[i]

            when {
                c.isDigit() || c == '.' -> {
                    val sb = StringBuilder()
                    while (i < expr.length && (expr[i].isDigit() || expr[i] == '.')) {
                        sb.append(expr[i])
                        i++
                    }
                    val num = sb.toString().toDoubleOrNull() ?: throw IllegalArgumentException("Invalid number: $sb")
                    tokens.add(Token.Number(num))
                    expectUnary = false
                }
                c == '(' -> {
                    tokens.add(Token.LeftParen)
                    i++
                    expectUnary = true
                }
                c == ')' -> {
                    tokens.add(Token.RightParen)
                    i++
                    expectUnary = false
                }
                c == '+' || c == '-' -> {
                    if (expectUnary && c == '-') {
                        // Unary minus
                        i++
                        val sb = StringBuilder("-")
                        while (i < expr.length && (expr[i].isDigit() || expr[i] == '.')) {
                            sb.append(expr[i])
                            i++
                        }
                        if (sb.length > 1) {
                            val num = sb.toString().toDoubleOrNull() ?: throw IllegalArgumentException("Invalid number: $sb")
                            tokens.add(Token.Number(num))
                            expectUnary = false
                        } else {
                            // Negative parenthesized expression like -(5) -> -1 *
                            tokens.add(Token.Number(-1.0))
                            tokens.add(Token.Operator('*', 2))
                            expectUnary = true
                        }
                    } else {
                        tokens.add(Token.Operator(c, 1))
                        i++
                        expectUnary = true
                    }
                }
                c == '*' || c == '/' || c == '%' -> {
                    val prec = if (c == '%') 2 else 2
                    tokens.add(Token.Operator(c, prec))
                    i++
                    expectUnary = true
                }
                else -> {
                    i++ // skip unrecognized
                }
            }
        }
        return tokens
    }

    private fun infixToRPN(tokens: List<Token>): List<Token> {
        val output = mutableListOf<Token>()
        val stack = ArrayDeque<Token>()

        for (token in tokens) {
            when (token) {
                is Token.Number -> output.add(token)
                is Token.Operator -> {
                    while (stack.isNotEmpty() && stack.last() is Token.Operator) {
                        val top = stack.last() as Token.Operator
                        if ((!token.isRightAssociative && token.precedence <= top.precedence) ||
                            (token.isRightAssociative && token.precedence < top.precedence)
                        ) {
                            output.add(stack.removeLast())
                        } else {
                            break
                        }
                    }
                    stack.addLast(token)
                }
                is Token.LeftParen -> stack.addLast(token)
                is Token.RightParen -> {
                    while (stack.isNotEmpty() && stack.last() !is Token.LeftParen) {
                        output.add(stack.removeLast())
                    }
                    if (stack.isNotEmpty() && stack.last() is Token.LeftParen) {
                        stack.removeLast()
                    } else {
                        throw IllegalArgumentException("Mismatched parentheses")
                    }
                }
            }
        }

        while (stack.isNotEmpty()) {
            val top = stack.removeLast()
            if (top is Token.LeftParen || top is Token.RightParen) {
                throw IllegalArgumentException("Mismatched parentheses")
            }
            output.add(top)
        }

        return output
    }

    private fun evaluateRPN(tokens: List<Token>): Double {
        val stack = ArrayDeque<Double>()

        for (token in tokens) {
            when (token) {
                is Token.Number -> stack.addLast(token.value)
                is Token.Operator -> {
                    if (stack.size < 2) throw IllegalArgumentException("Invalid syntax")
                    val b = stack.removeLast()
                    val a = stack.removeLast()
                    val res = when (token.symbol) {
                        '+' -> a + b
                        '-' -> a - b
                        '*' -> a * b
                        '/' -> {
                            if (b == 0.0) throw ArithmeticException("Division by zero")
                            a / b
                        }
                        '%' -> a % b
                        else -> throw IllegalArgumentException("Unknown operator: ${token.symbol}")
                    }
                    stack.addLast(res)
                }
                else -> {}
            }
        }

        if (stack.size != 1) throw IllegalArgumentException("Invalid expression")
        return stack.last()
    }

    /**
     * Formats a raw double value into a clean decimal string.
     */
    fun formatDecimal(value: Double): String {
        if (value.isNaN()) return "Error"
        if (value.isInfinite()) return if (value > 0) "Infinity" else "-Infinity"

        val rounded = (value * 1000000.0).roundToInt() / 1000000.0
        if (rounded == rounded.toLong().toDouble()) {
            return rounded.toLong().toString()
        }
        return rounded.toString()
    }

    /**
     * Formats a total inch value into Feet - Inches - Fraction format for carpenters.
     * E.g., 67.375 -> 5' 7 3/8"
     * Denominator precision defaults to 16 (1/16th inch precision).
     */
    fun formatFeetInches(totalInches: Double): String {
        if (totalInches.isNaN() || totalInches.isInfinite()) return ""

        val isNegative = totalInches < 0
        val absInches = abs(totalInches)

        val totalFeet = (absInches / 12.0).toInt()
        val remainingInches = absInches - (totalFeet * 12)
        val wholeInches = remainingInches.toInt()
        val fractionalPart = remainingInches - wholeInches

        // Round fractional part to nearest 1/16
        val sixteenths = (fractionalPart * 16.0).roundToInt()

        var adjustedInches = wholeInches
        var adjustedFeet = totalFeet
        var fractionStr = ""

        if (sixteenths == 16) {
            adjustedInches += 1
            if (adjustedInches == 12) {
                adjustedInches = 0
                adjustedFeet += 1
            }
        } else if (sixteenths > 0) {
            val (num, den) = simplifyFraction(sixteenths, 16)
            fractionStr = " $num/$den"
        }

        val sign = if (isNegative) "-" else ""
        val feetPart = if (adjustedFeet > 0) "${adjustedFeet}' " else ""
        val inchesPart = "${adjustedInches}${fractionStr}\""

        return "$sign$feetPart$inchesPart".trim()
    }

    private fun simplifyFraction(numerator: Int, denominator: Int): Pair<Int, Int> {
        val gcdVal = gcd(numerator, denominator)
        return Pair(numerator / gcdVal, denominator / gcdVal)
    }

    private fun gcd(a: Int, b: Int): Int {
        var x = a
        var y = b
        while (y != 0) {
            val t = y
            y = x % y
            x = t
        }
        return x
    }
}
