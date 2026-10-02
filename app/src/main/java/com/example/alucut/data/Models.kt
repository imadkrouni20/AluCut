package com.example.alucut.data

enum class TemplateCategory { DOOR, WINDOW, COMBINED, ACCESSORY }

enum class TemplateType(val label: String, val cat: TemplateCategory) {
    // 🚪 أبواب
    SINGLE_DOOR("باب بدفة واحدة", TemplateCategory.DOOR),
    DOUBLE_DOOR("باب بدفتين", TemplateCategory.DOOR),
    SLIDING_DOOR("باب منزلق", TemplateCategory.DOOR),
    FOLDING_DOOR("باب مطوي", TemplateCategory.DOOR),
    // 🪟 نوافذ
    FIXED_WINDOW("نافذة ثابتة", TemplateCategory.WINDOW),
    SINGLE_WINDOW("نافذة بدفة واحدة", TemplateCategory.WINDOW),
    DOUBLE_WINDOW("نافذة بدفتين", TemplateCategory.WINDOW),
    TRIPLE_WINDOW("نافذة بثلاث دفات", TemplateCategory.WINDOW),
    SLIDING_WINDOW_2("منزلقة بدفتين", TemplateCategory.WINDOW),
    SLIDING_WINDOW_3("منزلقة بثلاث دفات", TemplateCategory.WINDOW),
    SLIDING_WINDOW_4("منزلقة بأربع دفات", TemplateCategory.WINDOW),
    VERTICAL_SLIDER("منزلقة رأسياً", TemplateCategory.WINDOW),
    AWNING_WINDOW("نافذة معلقة", TemplateCategory.WINDOW),
    HOPPER_WINDOW("نافذة دوارة", TemplateCategory.WINDOW),
    ARCH_WINDOW("نافذة بأقواس", TemplateCategory.WINDOW),
    // 🚪🪟 مدمجة
    WINDOW_DOOR("نافذة مع باب", TemplateCategory.COMBINED),
    DOOR_SIDELIGHT("باب مع نافذة جانبية", TemplateCategory.COMBINED),
    DOOR_TRANSOM("باب مع نافذة علوية", TemplateCategory.COMBINED),
    BALCONY("شرفة", TemplateCategory.COMBINED),
    // 🔧 إضافات
    GRILLE("Grille (شبك)", TemplateCategory.ACCESSORY),
    ROLLER_SHUTTER("Volet roulant", TemplateCategory.ACCESSORY),
    BLIND("Store (ستارة)", TemplateCategory.ACCESSORY),
    CUSTOM("شكل مخصص", TemplateCategory.WINDOW)
}

data class FormulaVariable(
    val key: String,
    val label: String,
    val defaultValue: Double
)

data class CutPiece(
    val id: Int,
    val name: String,
    val countFormula: String,
    val lengthFormula: String,
    val angle1: Int,
    val angle2: Int,
    val barType: String
)

data class Template(
    val id: String,
    val name: String,
    val type: TemplateType,
    val imageUri: String = "",
    val barLengthCm: Double = 600.0,
    val kerfCm: Double = 0.3,
    val variables: List<FormulaVariable> = emptyList(),
    val pieces: List<CutPiece> = emptyList()
)

data class WindowInput(val count: Int, val widthCm: Double, val heightCm: Double)

data class InputItem(
    val id: Int,
    val templateId: String,
    val templateName: String,
    val count: Int,
    val widthCm: Double,
    val heightCm: Double
)

data class CutRequirement(val type: String, val lengthCm: Double, val quantity: Int)

data class CutItem(val type: String, val lengthMm: Int)

data class BarCut(
    val index: Int,
    val type: String,
    val cuts: List<CutItem>,
    val usedMm: Int,
    val wasteMm: Int
)

data class TypeSummary(
    val type: String,
    val barCount: Int,
    val usedMm: Int,
    val wasteMm: Int,
    val wastePercent: Double
)

data class CuttingResult(
    val requirements: List<CutRequirement>,
    val bars: List<BarCut>,
    val typeSummaries: List<TypeSummary>,
    val barLengthMm: Int,
    val totalBars: Int,
    val totalWasteMm: Int,
    val totalUsedMm: Int,
    val wastePercent: Double
)

object BaseVars {
    const val L = "l"
    const val H = "h"
    const val N = "n"
}

// ═══ CutTypes: أسماء القطع للعرض والرسم ═══
object CutTypes {
    const val CADRE = "Cadre"
    const val CADRE_OUVRANT = "Cadre Ouvrant"
    const val DORMANT = "Dormant"
    const val OUVRANT = "Ouvrant"
    const val PARCLOSE = "Parclose"
    const val Z = "Z"
    const val T = "T"
    const val MENEAU = "Meneau"
    const val TRAVERSE = "Traverse"
    const val SEUIL = "Seuil"
    const val RAIL = "Rail"
    const val COULISSANT = "Coulissant"
    const val PORT_ROULETTES = "Port Roulettes"
    const val PORT_VERREAUX = "Port Verreaux"
    const val CROUCHEMENT = "Crouchement"
}
