package com.example.alucut.data

object VariablesCatalog {

    // المتغيرات المقترحة عند إضافة قالب جديد
    val SUGGESTED = listOf(
        FormulaVariable("de", "سمك Dormant", 4.5),
        FormulaVariable("do", "سمك Ouvrant", 4.0),
        FormulaVariable("dz", "سمك Z", 3.0),
        FormulaVariable("dt", "سمك T", 2.0),
        FormulaVariable("dp", "سمك Parclose", 1.5),
        FormulaVariable("dc", "سمك Crouchement", 3.0),
        FormulaVariable("dm", "سمك Meneau", 5.0),
        FormulaVariable("dr", "سمك Rail", 3.0),
        FormulaVariable("ds", "سمك Seuil", 2.0),
        FormulaVariable("dg", "سمك Grille", 2.0),
        FormulaVariable("g", "الفراغ Jeu", 0.5),
        FormulaVariable("e", "فاصل الدفتين", 0.3)
    )

    // عرض سريع لكل المتغيرات المتاحة
    val ALL_KEYS = listOf(
        "l" to "العرض الكلي",
        "h" to "الارتفاع الكلي",
        "n" to "عدد المنتجات",
        "de" to "سمك Dormant",
        "do" to "سمك Ouvrant",
        "dz" to "سمك Z",
        "dt" to "سمك T",
        "dp" to "سمك Parclose",
        "dc" to "سمك Crouchement",
        "dm" to "سمك Meneau",
        "dr" to "سمك Rail",
        "ds" to "سمك Seuil",
        "dg" to "سمك Grille",
        "g" to "الفراغ Jeu",
        "e" to "فاصل الدفتين",
        "k" to "سمك القرص Kerf",
        "a" to "متغير حر"
    )

    fun suggestedVars(): List<FormulaVariable> = SUGGESTED.toList()
}
