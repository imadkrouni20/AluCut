package com.example.alucut.data

object Calculator {

    fun calculate(template: Template, input: WindowInput): List<CutRequirement> {
        val p = template.params
        val w = input.widthCm
        val h = input.heightCm
        val n = input.count

        return when (template.type) {
            TemplateType.SINGLE_DOOR,
            TemplateType.SINGLE_WINDOW -> singleLeaf(p, w, h, n)
            TemplateType.DOUBLE_DOOR,
            TemplateType.DOUBLE_WINDOW -> doubleLeaf(p, w, h, n)
            TemplateType.SLIDING_WINDOW -> slidingWindow(p, w, h, n)
            TemplateType.CUSTOM -> custom(p, w, h, n)
        }
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

    private fun singleLeaf(p: Map<String, Double>, w: Double, h: Double, n: Int): List<CutRequirement> {
        val zV = p[ParamKeys.Z_V_OFFSET] ?: 2.5
        val zH = p[ParamKeys.Z_H_OFFSET] ?: 5.0
        val tOff = p[ParamKeys.T_OFFSET] ?: 10.0

        return buildList {
            add(CutRequirement(CutTypes.CADRE_OUVRANT, h, 2 * n))
            add(CutRequirement(CutTypes.CADRE_OUVRANT, w, 1 * n))
            add(CutRequirement(CutTypes.Z, h - zV, 2 * n))
            add(CutRequirement(CutTypes.Z, w - zH, 2 * n))
            add(CutRequirement(CutTypes.T, w - tOff, 1 * n))
        }
    }

    private fun doubleLeaf(p: Map<String, Double>, w: Double, h: Double, n: Int): List<CutRequirement> {
        val gap = p[ParamKeys.GAP] ?: 1.0
        val thickness = p[ParamKeys.CADRE_OUVRANT_THICKNESS] ?: 12.5
        val topB = p[ParamKeys.TOP_BOTTOM_DOUBLE] ?: 5.0

        val zH = (w - gap - 2 * thickness) / 2.0
        val zV = h - topB

        return buildList {
            add(CutRequirement(CutTypes.CADRE_OUVRANT, w, 2 * n))
            add(CutRequirement(CutTypes.CADRE_OUVRANT, h, 2 * n))
            add(CutRequirement(CutTypes.Z, zH, 4 * n))
            add(CutRequirement(CutTypes.Z, zV, 4 * n))
            add(CutRequirement(CutTypes.T, zV, 1 * n))
        }
    }

    private fun slidingWindow(p: Map<String, Double>, w: Double, h: Double, n: Int): List<CutRequirement> {
        val frame = p[ParamKeys.FRAME_THICKNESS] ?: 3.5
        val inner = p[ParamKeys.INNER_VERTICAL] ?: 3.5
        val topB = p[ParamKeys.TOP_BOTTOM] ?: 2.5

        val portRoulettes = (w - 2 * frame - 2 * inner) / 2.0
        val pv = h - 2 * topB

        return buildList {
            add(CutRequirement(CutTypes.CADRE, w, 2 * n))
            add(CutRequirement(CutTypes.CADRE, h, 2 * n))
            if (portRoulettes > 0) add(CutRequirement(CutTypes.PORT_ROULETTES, portRoulettes, 4 * n))
            if (pv > 0) add(CutRequirement(CutTypes.PORT_VERREAUX, pv, 2 * n))
            if (pv > 0) add(CutRequirement(CutTypes.CROUCHEMENT, pv, 2 * n))
        }
    }

    private fun custom(p: Map<String, Double>, w: Double, h: Double, n: Int): List<CutRequirement> {
        val frame = p[ParamKeys.FRAME_THICKNESS] ?: 3.5
        val topB = p[ParamKeys.TOP_BOTTOM] ?: 2.5
        return buildList {
            add(CutRequirement(CutTypes.CADRE, w, 2 * n))
            add(CutRequirement(CutTypes.CADRE, h, 2 * n))
            add(CutRequirement(CutTypes.Z, w - 2 * frame, 2 * n))
            add(CutRequirement(CutTypes.Z, h - 2 * topB, 2 * n))
        }
    }
}
