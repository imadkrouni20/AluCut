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

    // ═══════════════════════════════════════════
    // 🚪 PORTE 1 VANTAIL
    // Dormant: 2V + 1H (عتبة سفلية مختلفة)
    // Ouvrant: 2V + 2H (علوي + سفلي)
    // ═══════════════════════════════════════════
    private fun singleDoor(p: Map<String, Double>, L: Double, H: Double, n: Int): List<CutRequirement> {
        val dw = p[ParamKeys.DORMANT_WIDTH] ?: 5.0
        val ow = p[ParamKeys.OUVRANT_WIDTH] ?: 5.0
        val clr = p[ParamKeys.OUVRANT_CLEARANCE] ?: 0.3
        val thr = p[ParamKeys.THRESHOLD_HEIGHT] ?: 2.0  // عتبة سفلية

        val ouvrH = H - 2 * dw + 2 * clr
        val ouvrW = L - 2 * dw + 2 * clr

        return buildList {
            // Dormant
            add(CutRequirement(CutTypes.DORMANT_V, H, 2 * n))
            add(CutRequirement(CutTypes.DORMANT_H, L - 2 * dw, 1 * n))
            add(CutRequirement("Seuil (Threshold)", L - 2 * dw, 1 * n))
            // Ouvrant
            add(CutRequirement(CutTypes.OUVRANT_V, ouvrH, 2 * n))
            add(CutRequirement("Ouvrant H (haut)", ouvrW - 2 * ow, 1 * n))
            add(CutRequirement("Ouvrant H (bas)", ouvrW - 2 * ow + thr, 1 * n))
        }
    }

    // ═══════════════════════════════════════════
    // 🚪 PORTE 2 VANTAUX
    // Dormant: 2V + 1H + Seuil
    // Ouvrant: 4V + 4H (علوي + سفلي لكل دفة)
    // ═══════════════════════════════════════════
    private fun doubleDoor(p: Map<String, Double>, L: Double, H: Double, n: Int): List<CutRequirement> {
        val dw = p[ParamKeys.DORMANT_WIDTH] ?: 5.0
        val ow = p[ParamKeys.OUVRANT_WIDTH] ?: 5.0
        val clr = p[ParamKeys.OUVRANT_CLEARANCE] ?: 0.3
        val thr = p[ParamKeys.THRESHOLD_HEIGHT] ?: 2.0
        val gap = p[ParamKeys.GAP] ?: 0.3

        val ouvrW = (L - 2 * dw - gap) / 2.0
        val ouvrH = H - 2 * dw + 2 * clr

        return buildList {
            add(CutRequirement(CutTypes.DORMANT_V, H, 2 * n))
            add(CutRequirement(CutTypes.DORMANT_H, L - 2 * dw, 1 * n))
            add(CutRequirement("Seuil (Threshold)", L - 2 * dw, 1 * n))
            add(CutRequirement(CutTypes.OUVRANT_V, ouvrH, 4 * n))
            add(CutRequirement("Ouvrant H (haut)", ouvrW - 2 * ow, 2 * n))
            add(CutRequirement("Ouvrant H (bas)", ouvrW - 2 * ow + thr, 2 * n))
        }
    }

    // ═══════════════════════════════════════════
    // 🪟 FENÊTRE 1 VANTAIL
    // Dormant: 2V + 2H (علوي + سفلي)
    // Ouvrant: 2V + 2H
    // Parclose: 2V + 2H (4 قطع لتثبيت الزجاج)
    // ═══════════════════════════════════════════
    private fun singleWindow(p: Map<String, Double>, L: Double, H: Double, n: Int): List<CutRequirement> {
        val dw = p[ParamKeys.DORMANT_WIDTH] ?: 4.5
        val ow = p[ParamKeys.OUVRANT_WIDTH] ?: 4.0
        val clr = p[ParamKeys.OUVRANT_CLEARANCE] ?: 0.3
        val pw = p[ParamKeys.PARCLOSE_WIDTH] ?: 1.5

        val ouvrH = H - 2 * dw + 2 * clr
        val ouvrW = L - 2 * dw + 2 * clr

        return buildList {
            // Dormant
            add(CutRequirement(CutTypes.DORMANT_V, H, 2 * n))
            add(CutRequirement(CutTypes.DORMANT_H, L - 2 * dw, 2 * n))
            // Ouvrant
            add(CutRequirement(CutTypes.OUVRANT_V, ouvrH, 2 * n))
            add(CutRequirement(CutTypes.OUVRANT_H, ouvrW - 2 * ow, 2 * n))
            // Parclose
            add(CutRequirement(CutTypes.PARCLOSE, ouvrH - 2 * pw, 2 * n))
            add(CutRequirement(CutTypes.PARCLOSE, ouvrW - 2 * pw, 2 * n))
        }
    }

    // ═══════════════════════════════════════════
    // 🪟 FENÊTRE 2 VANTAUX
    // Dormant: 2V + 2H
    // Meneau: 1V (عمودي وسطي ثابت)
    // Ouvrant: 4V + 4H
    // Parclose: 4V + 4H
    // ═══════════════════════════════════════════
    private fun doubleWindow(p: Map<String, Double>, L: Double, H: Double, n: Int): List<CutRequirement> {
        val dw = p[ParamKeys.DORMANT_WIDTH] ?: 4.5
        val ow = p[ParamKeys.OUVRANT_WIDTH] ?: 4.0
        val clr = p[ParamKeys.OUVRANT_CLEARANCE] ?: 0.3
        val pw = p[ParamKeys.PARCLOSE_WIDTH] ?: 1.5
        val mw = p[ParamKeys.MENEAU_WIDTH] ?: 5.0

        val meneauH = H - 2 * dw
        val ouvrH = H - 2 * dw + 2 * clr
        val ouvrW = (L - 2 * dw - mw) / 2.0 + 2 * clr

        return buildList {
            // Dormant
            add(CutRequirement(CutTypes.DORMANT_V, H, 2 * n))
            add(CutRequirement(CutTypes.DORMANT_H, L - 2 * dw, 2 * n))
            // Meneau (فاصل عمودي ثابت في المنتصف)
            add(CutRequirement(CutTypes.MENEAU, meneauH, 1 * n))
            // Ouvrant
            add(CutRequirement(CutTypes.OUVRANT_V, ouvrH, 4 * n))
            add(CutRequirement(CutTypes.OUVRANT_H, ouvrW - 2 * ow, 4 * n))
            // Parclose
            add(CutRequirement(CutTypes.PARCLOSE, ouvrH - 2 * pw, 4 * n))
            add(CutRequirement(CutTypes.PARCLOSE, ouvrW - 2 * pw, 4 * n))
        }
    }

    // ═══════════════════════════════════════════
    // 🔄 FENÊTRE COULISSANTE
    // Dormant: 2V + 2H
    // Rail: 2 قطع (مسار علوي + سفلي)
    // Coulissant: 4V + 4H (دفة علوية + سفلية، كل واحدة 2V + 2H)
    // Parclose: 4V + 4H
    // ═══════════════════════════════════════════
    private fun slidingWindow(p: Map<String, Double>, L: Double, H: Double, n: Int): List<CutRequirement> {
        val dw = p[ParamKeys.DORMANT_WIDTH] ?: 4.5
        val ow = p[ParamKeys.OUVRANT_WIDTH] ?: 4.0
        val clr = p[ParamKeys.OUVRANT_CLEARANCE] ?: 0.3
        val pw = p[ParamKeys.PARCLOSE_WIDTH] ?: 1.5
        val rh = p[ParamKeys.RAIL_HEIGHT] ?: 3.0

        // كل دفة منزلقة: ارتفاعها = H - 2*Dormant - 2*Rail + 2*clearance
        val coulH = H - 2 * dw - 2 * rh + 2 * clr
        // كل دفة: عرضها = (L - 2*Dormant) / 2 + تراكب
        val coulW = (L - 2 * dw) / 2.0 + 2 * clr

        return buildList {
            // Dormant
            add(CutRequirement(CutTypes.DORMANT_V, H, 2 * n))
            add(CutRequirement(CutTypes.DORMANT_H, L - 2 * dw, 2 * n))
            // Rails (مسار علوي + سفلي)
            add(CutRequirement(CutTypes.RAIL, L - 2 * dw, 2 * n))
            // Coulissant (دفة منزلقة) - 2 دفات × (2V + 2H)
            add(CutRequirement(CutTypes.COULISSANT, coulH, 4 * n))
            add(CutRequirement(CutTypes.COULISSANT, coulW - 2 * ow, 4 * n))
            // Parclose
            add(CutRequirement(CutTypes.PARCLOSE, coulH - 2 * pw, 4 * n))
            add(CutRequirement(CutTypes.PARCLOSE, coulW - 2 * pw, 4 * n))
        }
    }

    private fun custom(p: Map<String, Double>, L: Double, H: Double, n: Int): List<CutRequirement> {
        val dw = p[ParamKeys.DORMANT_WIDTH] ?: 4.5
        val pw = p[ParamKeys.PARCLOSE_WIDTH] ?: 1.5
        return buildList {
            add(CutRequirement(CutTypes.DORMANT_V, H, 2 * n))
            add(CutRequirement(CutTypes.DORMANT_H, L - 2 * dw, 2 * n))
            add(CutRequirement(CutTypes.PARCLOSE, H - 2 * dw - 2 * pw, 2 * n))
            add(CutRequirement(CutTypes.PARCLOSE, L - 2 * dw - 2 * pw, 2 * n))
        }
    }
}
