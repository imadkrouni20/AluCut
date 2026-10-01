package com.example.alucut.data

data class WindowInput(
    val count: Int,
    val widthCm: Double,
    val heightCm: Double
)

data class ProfileConfig(
    val frameProfileWidthCm: Double = 3.5,
    val innerVerticalWidthCm: Double = 3.5,
    val topBottomDepthCm: Double = 2.5,
    val barLengthCm: Double = 600.0,
    val kerfCm: Double = 0.3
)

data class CutRequirement(
    val type: String,
    val lengthCm: Double,
    val quantity: Int
)

data class BarCut(
    val index: Int,
    val cuts: List<CutItem>,
    val usedMm: Int,
    val wasteMm: Int
)

data class CutItem(val type: String, val lengthMm: Int)

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
    const val PORT_ROULETTES = "Port Roulettes"
    const val PORT_VERREAUX = "Port Verreaux"
    const val CROUCHEMENT = "Crouchement"
}
