package com.example.alucut.data

object Calculator {

    fun calculate(template: Template, input: WindowInput): List<CutRequirement> {
        val p = template.params
        val w = input.widthCm
        val h = input.heightCm
        val n = input.count

        return when (template.type) {
            TemplateType.SLIDING_WINDOW -> slidingWindow(p, w, h, n)
            TemplateType.SINGLE_DOOR -> singleDoor(p, w, h, n)
            TemplateType.DOUBLE_DOOR_WINDOW -> doubleDoorWindow(p, w, h, n)
        }
    }

    // ═══════ نافذة منزلقة ═══════
    private fun slidingWindow(
        p: Map<String, Double>, w: Double, h: Double, n: Int
    ): List<CutRequirement> {
        val frame = p[ParamKeys.FRAME_THICKNESS] ?: 3.5
        val inner = p[ParamKeys.INNER_VERTICAL] ?: 3.5
        val topB = p[ParamKeys.TOP_BOTTOM] ?: 2.5

        val portRoulettes = (w - 2 * frame - 2 * inner) / 2.0
        val portVerreaux = h - 2 * topB
        val crouchement = h - 2 * topB

        return buildList {
            add(CutRequirement(CutTypes.CADRE, w, 2 * n))
            add(CutRequirement(CutTypes.CADRE, h, 2 * n))
            if (portRoulettes > 0)
                add(CutRequirement(CutTypes.PORT_ROULETTES, portRoulettes, 4 * n))
            if (portVerreaux > 0)
                add(CutRequirement(CutTypes.PORT_VERREAUX, portVerreaux, 2 * n))
            if (crouchement > 0)
                add(CutRequirement(CutTypes.CROUCHEMENT, crouchement, 2 * n))
        }
    }

    // ═══════ باب واحد ═══════
    private fun singleDoor(
        p: Map<String, Double>, w: Double, h: Double, n: Int
    ): List<CutRequirement> {
        val zV = p[ParamKeys.Z_V_OFFSET] ?: 2.5
        val zH = p[ParamKeys.Z_H_OFFSET] ?: 5.0
        val tOff = p[ParamKeys.T_OFFSET] ?: 10.0

        val zVertical = h - zV
        val zHorizontal = w - zH
        val tLength = w - tOff

        return buildList {
            add(CutRequirement(CutTypes.CADRE_OUVRANT, h, 2 * n))
            add(CutRequirement(CutTypes.CADRE_OUVRANT, w, 1 * n))
            if (zVertical > 0)
                add(CutRequirement(CutTypes.Z, zVertical, 2 * n))
            if (zHorizontal > 0)
                add(CutRequirement(CutTypes.Z, zHorizontal, 2 * n))
            if (tLength > 0)
                add(CutRequirement(CutTypes.T, tLength, 1 * n))
        }
    }

    // ═══════ نافذة ببابين ═══════
    private fun doubleDoorWindow(
        p: Map<String, Double>, w: Double, h: Double, n: Int
    ): List<CutRequirement> {
        val gap = p[ParamKeys.GAP] ?: 1.0
        val thickness = p[ParamKeys.CADRE_OUVRANT_THICKNESS] ?: 12.5
        val topB = p[ParamKeys.TOP_BOTTOM_DOUBLE] ?: 5.0

        val zHorizontal = (w - gap - 2 * thickness) / 2.0
        val zVertical = h - topB
        val tLength = h - topB

        return buildList {
            add(CutRequirement(CutTypes.CADRE_OUVRANT, w, 2 * n))
            add(CutRequirement(CutTypes.CADRE_OUVRANT, h, 2 * n))
            if (zHorizontal > 0)
                add(CutRequirement(CutTypes.Z, zHorizontal, 4 * n))
            if (zVertical > 0)
                add(CutRequirement(CutTypes.Z, zVertical, 4 * n))
            if (tLength > 0)
                add(CutRequirement(CutTypes.T, tLength, 1 * n))
        }
    }
}
