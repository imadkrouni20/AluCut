package com.example.alucut.data

object FormulaEngine {

    fun evaluate(formula: String, vars: Map<String, Double>): Double {
        if (formula.isBlank()) return 0.0
        return Parser(formula, vars).parse()
    }

    fun isValid(formula: String, vars: Map<String, Double>): Boolean {
        return try {
            evaluate(formula, vars)
            true
        } catch (e: Exception) {
            false
        }
    }

    private class Parser(val src: String, val vars: Map<String, Double>) {
        var pos = 0

        fun parse(): Double {
            val r = expr()
            skip()
            if (pos < src.length) throw Exception("حرف غير متوقع: ${src[pos]}")
            return r
        }

        fun expr(): Double {
            var left = term()
            while (true) {
                skip()
                if (pos >= src.length) break
                when (src[pos]) {
                    '+' -> { pos++; left += term() }
                    '-', '\u2212' -> { pos++; left -= term() }
                    else -> break
                }
            }
            return left
        }

        fun term(): Double {
            var left = factor()
            while (true) {
                skip()
                if (pos >= src.length) break
                when (src[pos]) {
                    '*', '\u00D7', 'x', 'X', '\u00B7' -> { pos++; left *= factor() }
                    '/', '\u00F7', ':' -> {
                        pos++
                        val d = factor()
                        if (d == 0.0) throw Exception("قسمة على صفر")
                        left /= d
                    }
                    else -> break
                }
            }
            return left
        }

        fun factor(): Double {
            skip()
            if (pos >= src.length) throw Exception("الصيغة غير مكتملة")
            val c = src[pos]
            return when {
                c == '(' -> {
                    pos++
                    val v = expr()
                    skip()
                    if (pos >= src.length || src[pos] != ')')
                        throw Exception("قوس ناقص")
                    pos++
                    v
                }
                c == '-' -> { pos++; -factor() }
                c.isDigit() || c == '.' -> {
                    val start = pos
                    while (pos < src.length &&
                        (src[pos].isDigit() || src[pos] == '.')) pos++
                    src.substring(start, pos).toDouble()
                }
                c.isLetter() -> {
                    val start = pos
                    while (pos < src.length &&
                        (src[pos].isLetterOrDigit() || src[pos] == '_')) pos++
                    val name = src.substring(start, pos).lowercase()
                    vars[name] ?: throw Exception("متغير غير معروف: $name")
                }
                else -> throw Exception("رمز غير صالح: $c")
            }
        }

        fun skip() {
            while (pos < src.length && src[pos].isWhitespace()) pos++
        }
    }
}
