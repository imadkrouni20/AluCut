package com.example.alucut.data

enum class TemplateCategory { DOOR, WINDOW }

enum class TemplateType {
    SINGLE_DOOR,
    DOUBLE_DOOR,
    SINGLE_WINDOW,
    DOUBLE_WINDOW,
    SLIDING_WINDOW,
    CUSTOM
}

data class Template(
    val id: String,
    val name: String,
    val type: TemplateType,
    val category: TemplateCategory,
    val imageUri: String = "",
    val params: Map<String, Double> = emptyMap()
)

data class WindowInput(
    val count: Int,
    val widthCm: Double,
    val heightCm: Double
)

data class InputItem(
    val id: Int,
    val templateId: String,
    val templateName: String,
    val templateType: TemplateType,
    val templateCategory: TemplateCategory,
    val count: Int,
    val widthCm: Double,
    val heightCm: Double
)

data class CutRequirement(
    val type: String,
    val lengthCm: Double,
    val quantity: Int
)

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

object CutTypes {
    const val DORMANT_V = "Dormant V"
    const val DORMANT_H = "Dormant H"
    const val OUVRANT_V = "Ouvrant V"
    const val OUVRANT_H = "Ouvrant H"
    const val TRAVERSE = "Traverse"
    const val PARCLOSE = "Parclose"
    const val RAIL = "Rail"
    const val MENEAU = "Meneau"
    const val COULISSANT = "Coulissant"
    const val CADRE = "Cadre"
    const val CADRE_OUVRANT = "Cadre Ouvrant"
    const val PORT_ROULETTES = "Port Roulettes"
    const val PORT_VERREAUX = "Port Verreaux"
    const val CROUCHEMENT = "Crouchement"
    const val Z = "Z"
    const val T = "T"
}

object ParamKeys {
    const val BAR_LENGTH = "barLength"
    const val KERF = "kerf"
    const val FRAME_THICKNESS = "frameThickness"
    const val INNER_VERTICAL = "innerVertical"
    const val TOP_BOTTOM = "topBottom"
    const val Z_V_OFFSET = "zVOffset"
    const val Z_H_OFFSET = "zHOffset"
    const val T_OFFSET = "tOffset"
    const val GAP = "gap"
    const val CADRE_OUVRANT_THICKNESS = "cadreOuvrantThickness"
    const val TOP_BOTTOM_DOUBLE = "topBottomDouble"
    const val DORMANT_WIDTH = "dormantWidth"
    const val DORMANT_HEIGHT = "dormantHeight"
    const val OUVRANT_WIDTH = "ouvrantWidth"
    const val OUVRANT_HEIGHT = "ouvrantHeight"
    const val OUVRANT_CLEARANCE = "ouvrantClearance"
    const val PARCLOSE_WIDTH = "parcloseWidth"
    const val GLASS_CLEARANCE = "glassClearance"
    const val THRESHOLD_HEIGHT = "thresholdHeight"
    const val MENEAU_WIDTH = "meneauWidth"
    const val RAIL_HEIGHT = "railHeight"
}
