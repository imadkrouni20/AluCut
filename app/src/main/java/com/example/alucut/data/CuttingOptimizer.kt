package com.example.alucut.data

object CuttingOptimizer {

    fun optimize(
        requirements: List<CutRequirement>,
        barLengthCm: Double,
        kerfCm: Double
    ): CuttingResult {
        val barLengthMm = (barLengthCm * 10).toInt()
        val kerfMm = (kerfCm * 10).toInt()

        // ⚡ نقسم القطع حسب النوع - كل نوع له أعمدة منفصلة
        val byType = requirements.groupBy { it.type }

        val allBars = mutableListOf<BarCut>()
        val summaries = mutableListOf<TypeSummary>()

        for ((type, reqs) in byType) {
            val typeBars = optimizeSingleType(type, reqs, barLengthMm, kerfMm)
            allBars.addAll(typeBars)

            val used = typeBars.sumOf { it.usedMm }
            val waste = typeBars.sumOf { it.wasteMm }
            val total = typeBars.size * barLengthMm
            summaries.add(
                TypeSummary(
                    type = type,
                    barCount = typeBars.size,
                    usedMm = used,
                    wasteMm = waste,
                    wastePercent = if (total > 0) waste * 100.0 / total else 0.0
                )
            )
        }

        val totalWaste = allBars.sumOf { it.wasteMm }
        val totalUsed = allBars.sumOf { it.usedMm }
        val totalLength = allBars.size * barLengthMm
        val percent = if (totalLength > 0) totalWaste * 100.0 / totalLength else 0.0

        return CuttingResult(
            requirements = requirements,
            bars = allBars,
            typeSummaries = summaries,
            barLengthMm = barLengthMm,
            totalBars = allBars.size,
            totalWasteMm = totalWaste,
            totalUsedMm = totalUsed,
            wastePercent = percent
        )
    }

    // تقطيع نوع واحد فقط (Best Fit Decreasing)
    private fun optimizeSingleType(
        type: String,
        reqs: List<CutRequirement>,
        barLengthMm: Int,
        kerfMm: Int
    ): List<BarCut> {
        val items = mutableListOf<Int>()
        for (req in reqs) {
            val lengthMm = (req.lengthCm * 10).toInt()
            if (lengthMm <= 0 || lengthMm > barLengthMm) continue
            repeat(req.quantity) { items.add(lengthMm) }
        }
        if (items.isEmpty()) return emptyList()

        items.sortDescending()

        val bars = mutableListOf<MutableList<Int>>()
        val remaining = mutableListOf<Int>()

        for (item in items) {
            var bestIdx = -1
            var bestRemaining = Int.MAX_VALUE
            for (i in bars.indices) {
                val needed = if (bars[i].isEmpty()) item else item + kerfMm
                if (remaining[i] >= needed) {
                    val after = remaining[i] - needed
                    if (after < bestRemaining) {
                        bestRemaining = after
                        bestIdx = i
                    }
                }
            }

            if (bestIdx >= 0) {
                val needed = if (bars[bestIdx].isEmpty()) item else item + kerfMm
                bars[bestIdx].add(item)
                remaining[bestIdx] -= needed
            } else {
                bars.add(mutableListOf(item))
                remaining.add(barLengthMm - item)
            }
        }

        return bars.mapIndexed { idx, cuts ->
            val used = if (cuts.isEmpty()) 0
                       else cuts.sum() + kerfMm * (cuts.size - 1)
            BarCut(
                index = idx + 1,
                type = type,
                cuts = cuts.map { CutItem(type, it) },
                usedMm = used,
                wasteMm = barLengthMm - used
            )
        }
    }
}
