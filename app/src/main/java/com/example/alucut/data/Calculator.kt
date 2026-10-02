package com.example.alucut.data

object Calculator {

    fun calculate(template: Template, input: WindowInput): List<CutRequirement> {
        val p = template.params
        val L = input.widthCm
        val H = input.heightCm
        val n = input.count

        return when (template.type) {
            TemplateType.SINGLE_DOOR -> singleDoor(p, L, H, n)
            TemplateType.DOUBLE_DOOR -> doubleDoor(p, L, H, n)
            TemplateType.SINGLE_WINDOW -> singleWindow(p, L, H, n)
            TemplateType.DOUBLE_WINDOW -> doubleWindow(p, L, H, n)
            TemplateType.SLIDING_WINDOW -> slidingWindow(p, L, H, n)
            TemplateType.CUSTOM -> custom(p, L, H, n)
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

    private fun singleDoor(p: Map<String, Double>, L: Double, H: Double, n: Int): List<CutRequirement> {
        val dw = p[ParamKeys.DORMANT_WIDTH] ?: 4.5
        val ow = p[ParamKeys.OUVRANT_WIDTH] ?: 4.0
        val clr = p[ParamKeys.OUVRANT_CLEARANCE] ?: 0.5
        val pw = p[ParamKeys.PARCLOSE_WIDTH] ?: 1.5

        val ouvrH = H - 2 * (dw - clr)
        val ouvrW = L - 2 * (dw - clr)

        return buildList {
            add(CutRequirement(CutTypes.DORMANT_V, H, 2 * n))
            add(CutRequirement(CutTypes.DORMANT_H, L - 2 * dw, 1 * n))
            add(CutRequirement(CutTypes.OUVRANT_V, ouvrH, 2 * n))
            add(CutRequirement(CutTypes.OUVRANT_H, ouvrW - 2 * ow, 1 * n))
            add(CutRequirement(CutTypes.PARCLOSE, ouvrH - 2 * pw, 2 * n))
            add(CutRequirement(CutTypes.PARCLOSE, ouvrW - 2 * pw, 1 * n))
        }
    }

    private fun doubleDoor(p: Map<String, Double>, L: Double, H: Double, n: Int): List<CutRequirement> {
        val dw = p[ParamKeys.DORMANT_WIDTH] ?: 4.5
        val ow = p[ParamKeys.OUVRANT_WIDTH] ?: 4.0
        val gap = p[ParamKeys.GAP] ?: 0.5
        val pw = p[ParamKeys.PARCLOSE_WIDTH] ?: 1.5

        val ouvrW = (L - 2 * dw - gap) / 2.0
        val ouvrH = H - 2 * dw

        return buildList {
            add(CutRequirement(CutTypes.DORMANT_V, H, 2 * n))
            add(CutRequirement(CutTypes.DORMANT_H, L - 2 * dw, 1 * n))
            add(CutRequirement(CutTypes.OUVRANT_V, ouvrH, 4 * n))
            add(CutRequirement(CutTypes.OUVRANT_H, ouvrW - 2 * ow, 4 * n))
            add(CutRequirement(CutTypes.TRAVERSE, ouvrW - 2 * ow, 2 * n))
            add(CutRequirement(CutTypes.PARCLOSE, ouvrH - 2 * pw, 4 * n))
            add(CutRequirement(CutTypes.PARCLOSE, ouvrW - 2 * pw, 4 * n))
        }
    }

    private fun singleWindow(p: Map<String, Double>, L: Double, H: Double, n: Int): List<CutRequirement> {
        val dw = p[ParamKeys.DORMANT_WIDTH] ?: 4.5
        val ow = p[ParamKeys.OUVRANT_WIDTH] ?: 4.0
        val pw = p[ParamKeys.PARCLOSE_WIDTH] ?: 1.5

        val ouvrH = H - 2 * dw
        val ouvrW = L - 2 * dw

        return buildList {
            add(CutRequirement(CutTypes.DORMANT_V, H, 2 * n))
            add(CutRequirement(CutTypes.DORMANT_H, L - 2 * dw, 1 * n))
            add(CutRequirement(CutTypes.OUVRANT_V, ouvrH, 2 * n))
            add(CutRequirement(CutTypes.OUVRANT_H, ouvrW - 2 * ow, 2 * n))
            add(CutRequirement(CutTypes.PARCLOSE, ouvrH - 2 * pw, 2 * n))
            add(CutRequirement(CutTypes.PARCLOSE, ouvrW - 2 * pw, 2 * n))
        }
    }

    private fun doubleWindow(p: Map<String, Double>, L: Double, H: Double, n: Int): List<CutRequirement> {
        val dw = p[ParamKeys.DORMANT_WIDTH] ?: 4.5
        val ow = p[ParamKeys.OUVRANT_WIDTH] ?: 4.0
        val pw = p[ParamKeys.PARCLOSE_WIDTH] ?: 1.5
        val gap = p[ParamKeys.GAP] ?: 0.5

        val ouvrW = (L - 2 * dw - gap) / 2.0
        val ouvrH = H - 2 * dw

        return buildList {
            add(CutRequirement(CutTypes.DORMANT_V, H, 2 * n))
            add(CutRequirement(CutTypes.DORMANT_H, L - 2 * dw, 1 * n))
            add(CutRequirement(CutTypes.OUVRANT_V, ouvrH, 4 * n))
            add(CutRequirement(CutTypes.OUVRANT_H, ouvrW - 2 * ow, 4 * n))
            add(CutRequirement(CutTypes.TRAVERSE, ouvrW - 2 * ow, 2 * n))
            add(CutRequirement(CutTypes.PARCLOSE, ouvrH - 2 * pw, 4 * n))
            add(CutRequirement(CutTypes.PARCLOSE, ouvrW - 2 * pw, 4 * n))
        }
    }

    private fun slidingWindow(p: Map<String, Double>, L: Double, H: Double, n: Int): List<CutRequirement> {
        val dw = p[ParamKeys.DORMANT_WIDTH] ?: 4.5
        val ow = p[ParamKeys.OUVRANT_WIDTH] ?: 4.0
        val rail = p[ParamKeys.TOP_BOTTOM] ?: 2.5

        val coulH = H - dw - rail - 2 * ow
        val coulW = (L / 2) - dw - 2 * ow

        return buildList {
            add(CutRequirement(CutTypes.DORMANT_V, H, 2 * n))
            add(CutRequirement(CutTypes.DORMANT_H, L - 2 * dw, 1 * n))
            add(CutRequirement(CutTypes.RAIL, L - 2 * dw, 1 * n))
            add(CutRequirement(CutTypes.COULISSANT, coulH, 4 * n))
            add(CutRequirement(CutTypes.COULISSANT, coulW, 4 * n))
        }
    }

    private fun custom(p: Map<String, Double>, L: Double, H: Double, n: Int): List<CutRequirement> {
        val dw = p[ParamKeys.DORMANT_WIDTH] ?: 4.5
        val pw = p[ParamKeys.PARCLOSE_WIDTH] ?: 1.5
        return buildList {
            add(CutRequirement(CutTypes.DORMANT_V, H, 2 * n))
            add(CutRequirement(CutTypes.DORMANT_H, L - 2 * dw, 1 * n))
            add(CutRequirement(CutTypes.PARCLOSE, H - 2 * dw - 2 * pw, 2 * n))
            add(CutRequirement(CutTypes.PARCLOSE, L - 2 * dw - 2 * pw, 2 * n))
        }
    }
}
