package com.example.alucut.data

object Calculator {

    fun calculate(template: Template, input: WindowInput): List<CutRequirement> {
        val vars = buildVars(template, input)
        val reqs = mutableListOf<CutRequirement>()

        for (piece in template.pieces) {
            val count = try {
                FormulaEngine.evaluate(piece.countFormula, vars).toInt()
            } catch (e: Exception) { 0 }

            val length = try {
                FormulaEngine.evaluate(piece.lengthFormula, vars)
            } catch (e: Exception) { 0.0 }

            if (count > 0 && length > 0) {
                reqs.add(CutRequirement(piece.barType, length, count))
            }
        }
        return reqs
    }

    fun calculateAll(
        items: List<InputItem>,
        templates: List<Template>
    ): List<CutRequirement> {
        val allReqs = mutableListOf<CutRequirement>()
        for (item in items) {
            val template = templates.find { it.id == item.templateId } ?: continue
            allReqs.addAll(calculate(template, WindowInput(item.count, item.widthCm, item.heightCm)))
        }
        return allReqs
            .groupBy { it.type to it.lengthCm }
            .map { (key, list) ->
                CutRequirement(key.first, key.second, list.sumOf { it.quantity })
            }
            .sortedByDescending { it.lengthCm }
    }

    private fun buildVars(template: Template, input: WindowInput): Map<String, Double> {
        val vars = mutableMapOf<String, Double>()
        // المتغيرات الأساسية
        vars[BaseVars.L] = input.widthCm
        vars[BaseVars.H] = input.heightCm
        vars[BaseVars.N] = input.count.toDouble()
        // المتغيرات المخصصة
        template.variables.forEach { v -> vars[v.key] = v.defaultValue }
        return vars
    }

    // قيمة متغير في سياق مُعطى (للمعاينة)
    fun previewVars(template: Template, sampleL: Double = 120.0, sampleH: Double = 100.0): Map<String, Double> {
        val vars = mutableMapOf<String, Double>()
        vars[BaseVars.L] = sampleL
        vars[BaseVars.H] = sampleH
        vars[BaseVars.N] = 1.0
        template.variables.forEach { v -> vars[v.key] = v.defaultValue }
        return vars
    }
}
