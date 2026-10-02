package com.example.alucut.data

enum class TemplateCategory { DOOR, WINDOW }

enum class TemplateType {
    SLIDING_WINDOW,
    SINGLE_DOOR,
    DOUBLE_DOOR_WINDOW
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

data class CutRequirement(
    val type: String,
    val lengthCm: Double,
    val quantity: Int
)

data class CutItem(val type: String, val lengthMm: Int)

data class BarCut(
    val index: Int,
    val cuts: List<CutItem>,
    val usedMm: Int,
    val wasteMm: Int
)

data class CuttingResult(
    val requirements: List<CutRequirement>,
    val bars: List<BarCut>,
    val barLengthMm: Int,
    val totalBars: Int,
    val totalWasteMm: Int,
    val totalUsedMm: Int,
    val wastePercent: Double
)

object CutTypes {
    const val CADRE = "Cadre"
    const val CADRE_OUVRANT = "Cadre Ouvrant"
    const val PORT_ROULETTES = "Port Roulettes"
    const val PORT_VERREAUX = "Port Verreaux"
    const val CROUCHEMENT = "Crouchement"
    const val Z = "Z"
    const val T = "T"
}

object ParamKeys {
    // Common
    const val BAR_LENGTH = "barLength"
    const val KERF = "kerf"
    // Sliding window
    const val FRAME_THICKNESS = "frameThickness"
    const val INNER_VERTICAL = "innerVertical"
    const val TOP_BOTTOM = "topBottom"
    // Single door
    const val Z_V_OFFSET = "zVOffset"
    const val Z_H_OFFSET = "zHOffset"
    const val T_OFFSET = "tOffset"
    // Double door window
    const val GAP = "gap"
    const val CADRE_OUVRANT_THICKNESS = "cadreOuvrantThickness"
    const val TOP_BOTTOM_DOUBLE = "topBottomDouble"
}
