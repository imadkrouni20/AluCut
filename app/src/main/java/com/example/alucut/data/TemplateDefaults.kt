package com.example.alucut.data

object TemplateDefaults {

    fun defaultTemplates(): List<Template> = listOf(
        starterTemplate()
    )

    private fun starterTemplate(): Template = Template(
        id = "starter_single_door",
        name = "باب بدفة واحدة (تجريبي)",
        type = TemplateType.SINGLE_DOOR,
        barLengthCm = 600.0,
        kerfCm = 0.3,
        variables = listOf(
            FormulaVariable("de", "سمك Dormant", 4.5),
            FormulaVariable("dz", "سمك Z", 3.0),
            FormulaVariable("dp", "سمك Parclose", 1.5),
            FormulaVariable("g", "الفراغ Jeu", 0.5)
        ),
        pieces = listOf(
            CutPiece(1, "Cadre Fix H", "1", "l", 45, 45, "Cadre Fix"),
            CutPiece(2, "Cadre Fix V D", "1", "h", 45, 90, "Cadre Fix"),
            CutPiece(3, "Cadre Fix V G", "1", "h", 90, 45, "Cadre Fix"),
            CutPiece(4, "Z H", "2", "l - (de*2) + (g*2)", 90, 90, "Z"),
            CutPiece(5, "Z V", "2", "h - (de*2) + (g*2)", 90, 90, "Z"),
            CutPiece(6, "Parclose H", "2",
                "l - (de*2) + (g*2) - (dz*2) - (dp*2)", 90, 90, "Parclose"),
            CutPiece(7, "Parclose V", "2",
                "h - (de*2) + (g*2) - (dz*2) - (dp*2)", 90, 90, "Parclose")
        )
    )

    fun emptyTemplate(): Template = Template(
        id = "custom_${System.currentTimeMillis()}",
        name = "قالب جديد",
        type = TemplateType.SINGLE_DOOR,
        barLengthCm = 600.0,
        kerfCm = 0.3,
        variables = listOf(
            FormulaVariable("de", "سمك Dormant", 4.5),
            FormulaVariable("g", "الفراغ Jeu", 0.5)
        ),
        pieces = listOf(
            CutPiece(1, "Cadre Fix H", "1", "l", 45, 45, "Cadre Fix"),
            CutPiece(2, "Cadre Fix V", "2", "h", 45, 90, "Cadre Fix")
        )
    )
}
