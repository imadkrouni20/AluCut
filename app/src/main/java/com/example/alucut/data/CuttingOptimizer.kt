package com.example.alucut.data

object CuttingOptimizer {

    fun optimize(
        requirements: List<CutRequirement>,
        barLengthCm: Double,
        kerfCm: Double
    ): CuttingResult {
        val barLengthMm = (barLengthCm * 10).toInt()
        val kerfMm = (kerfCm * 10).toInt()

        val items = mutableListOf<CutItem>()
        for (req in requirements) {
            val lengthMm = (req.lengthCm * 10).toInt()
            if (lengthMm <= 0 || lengthMm > barLengthMm) continue
            repeat(req.quantity) {
                items.add(CutItem(req.type, lengthMm))
            }
        }

        if (items.isEmpty()) {
            return CuttingResult(requirements, emptyList(), barLengthMm, 0, 0, 0, 0.0)
        }

        // BFD: ضع كل قطعة في العمود الذي يترك أقل فراغ بعد الإضافة
        items.sortByDescending { it.lengthMm }

        val bars = mutableListOf<MutableList<CutItem>>()
        val remaining = mutableListOf<Int>()

        for (item in items) {
            var bestIdx = -1
            var bestRemaining = Int.MAX_VALUE
            for (i in bars.indices) {
                val needed = if (bars[i].isEmpty()) item.lengthMm
                             else item.lengthMm + kerfMm
                if (remaining[i] >= needed) {
                    val after = remaining[i] - needed
                    if (after < bestRemaining) {
                        bestRemaining = after
                        bestIdx = i
                    }
                }
            }

            if (bestIdx >= 0) {
                val needed = if (bars[bestIdx].isEmpty()) item.lengthMm
                             else item.lengthMm + kerfMm
                bars[bestIdx].add(item)
                remaining[bestIdx] -= needed
            } else {
                bars.add(mutableListOf(item))
                remaining.add(barLengthMm - item.lengthMm)
            }
        }

        val barCuts = bars.mapIndexed { idx, cuts ->
            val used = if (cuts.isEmpty()) 0
                       else cuts.sumOf { it.lengthMm } + kerfMm * (cuts.size - 1)
            BarCut(
                index = idx + 1,
                cuts = cuts,
                usedMm = used,
                wasteMm = barLengthMm - used
            )
        }

        val totalWaste = barCuts.sumOf { it.wasteMm }
        val totalUsed = barCuts.sumOf { it.usedMm }
        val totalLength = bars.size * barLengthMm
        val percent = if (totalLength > 0) totalWaste * 100.0 / totalLength else 0.0

        return CuttingResult(
            requirements = requirements,
            bars = barCuts,
            barLengthMm = barLengthMm,
            totalBars = bars.size,
            totalWasteMm = totalWaste,
            totalUsedMm = totalUsed,
            wastePercent = percent
        )
    }
}
