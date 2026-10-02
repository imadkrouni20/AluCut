package com.example.alucut.data

object Calculator {

    fun calculate(template: Template, input: WindowInput): List<CutRequirement> {
        val p = template.params
        val L = input.widthCm   // Largeur (العرض)
        val H = input.heightCm  // Hauteur (العلو)
        val n = input.count

        return when (template.type) {
            // ═══════ PORTE 1 VANTAIL BATTANT ═══════
            // القطع: Dormant (2V + 1H) + Ouvrant (2V + 1H) + Parclose (2V + 1H)
            //        + Traverse (1) + Z (2V + 2H) + T (1)
            TemplateType.SINGLE_DOOR -> singleDoor(p, L, H, n)

            // ═══════ PORTE 2 VANTAUX BATTANTS ═══════
            // القطع: Dormant (2V + 2H) + Ouvrant (4V + 4H) + Traverse (2)
            //        + Z (4V + 4H) + T (1) + Parclose (4V + 4H)
            TemplateType.DOUBLE_DOOR -> doubleDoor(p, L, H, n)

            // ═══════ FENÊTRE 1 VANTAIL ═══════
            // القطع: Dormant (2V + 2H) + Ouvrant (2V + 2H) + Parclose (2V + 2H)
            TemplateType.SINGLE_WINDOW -> singleWindow(p, L, H, n)

            // ═══════ FENÊTRE 2 VANTAUX ═══════
            // القطع: Dormant (2V + 2H) + Ouvrant (4V + 4H) + Parclose (4V + 4H) + Traverse (2)
            TemplateType.DOUBLE_WINDOW -> doubleWindow(p, L, H, n)

            // ═══════ FENÊTRE COULISSANTE ═══════
            // القطع: Dormant (2V + 1H) + Rail (1) + Montant (2)
            //        + Ouvrant Coulissant (4V + 4H) + Parclose (4V + 4H)
            TemplateType.SLIDING_WINDOW -> slidingWindow(p, L, H, n)

            TemplateType.CUSTOM -> custom(p, L, H, n)
        }
    }

    // ... (دالة calculateAll كما هي)

    // ═══════ PORTE 1 VANTAIL ═══════
    private fun singleDoor(p: Map<String, Double>, L: Double, H: Double, n: Int): List<CutRequirement> {
        val dw = p[ParamKeys.DORMANT_WIDTH] ?: 4.5   // عرض قطاع Dormant
        val ow = p[ParamKeys.OUVRANT_WIDTH] ?: 4.0   // عرض قطاع Ouvrant
        val clr = p[ParamKeys.OUVRANT_CLEARANCE] ?: 0.5 // فراغ
        val pw = p[ParamKeys.PARCLOSE_WIDTH] ?: 1.5

        return buildList {
            // Dormant (الإطار الثابت)
            add(CutRequirement(CutTypes.DORMANT_V, H, 2 * n))
            add(CutRequirement(CutTypes.DORMANT_H, L - 2 * dw, 1 * n))
            
            // Ouvrant (الدرفة)
            val ouvrH = H - 2 * (dw - clr) // الارتفاع: H - 2*Dormant + 2*فراغ
            val ouvrW = L - 2 * (dw - clr) // العرض: L - 2*Dormant + 2*فراغ
            add(CutRequirement(CutTypes.OUVRANT_V, ouvrH, 2 * n))
            add(CutRequirement(CutTypes.OUVRANT_H, ouvrW - 2 * ow, 1 * n))
            
            // Traverse (العارضة الوسطى للدرفة) - إذا كانت الدرفة مقسمة
            // add(CutRequirement(CutTypes.TRAVERSE, ouvrW - 2 * ow, 1 * n))
            
            // Parclose (المثبتة الزجاج)
            val parcloseV = ouvrH - 2 * pw
            val parcloseH = ouvrW - 2 * pw
            add(CutRequirement(CutTypes.PARCLOSE, parcloseV, 2 * n))
            add(CutRequirement(CutTypes.PARCLOSE, parcloseH, 1 * n))
            
            // Z و T (للتأطير الداخلي - اختياري)
            add(CutRequirement(CutTypes.Z, ouvrH - 4, 2 * n))
            add(CutRequirement(CutTypes.Z, ouvrW - 4, 2 * n))
            add(CutRequirement(CutTypes.T, ouvrW - 8, 1 * n))
        }
    }

    // ═══════ PORTE 2 VANTAUX ═══════
    private fun doubleDoor(p: Map<String, Double>, L: Double, H: Double, n: Int): List<CutRequirement> {
        val dw = p[ParamKeys.DORMANT_WIDTH] ?: 4.5
        val ow = p[ParamKeys.OUVRANT_WIDTH] ?: 4.0
        val gap = p[ParamKeys.GAP] ?: 0.5

        val ouvrW = (L - 2 * dw - gap) / 2.0 // عرض كل دفة
        val ouvrH = H - 2 * dw

        return buildList {
            // Dormant
            add(CutRequirement(CutTypes.DORMANT_V, H, 2 * n))
            add(CutRequirement(CutTypes.DORMANT_H, L - 2 * dw, 2 * n))
            
            // Ouvrant (4 قطع لكل دفة)
            add(CutRequirement(CutTypes.OUVRANT_V, ouvrH, 4 * n))
            add(CutRequirement(CutTypes.OUVRANT_H, ouvrW - 2 * ow, 4 * n))
            
            // Traverse (2)
            add(CutRequirement(CutTypes.TRAVERSE, ouvrW - 2 * ow, 2 * n))
            
            // Z و T
            add(CutRequirement(CutTypes.Z, ouvrH - 4, 4 * n))
            add(CutRequirement(CutTypes.Z, ouvrW - 4, 4 * n))
            add(CutRequirement(CutTypes.T, ouvrW - 8, 1 * n))
        }
    }

    // ... (باقي الدوال مشابهة)
}
