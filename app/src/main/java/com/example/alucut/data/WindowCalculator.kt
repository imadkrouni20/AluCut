package com.example.alucut.data

object WindowCalculator {

    fun calculateRequirements(
        input: WindowInput,
        config: ProfileConfig
    ): List<CutRequirement> {
        val w = input.widthCm
        val h = input.heightCm
        val n = input.count

        val cadreWidth = w
        val cadreHeight = h

        val portRoulettesLength = (w
                - 2 * config.frameProfileWidthCm
                - 2 * config.innerVerticalWidthCm) / 2.0

        val portVerreauxLength = h - 2 * config.topBottomDepthCm
        val crouchementLength = h - 2 * config.topBottomDepthCm

        val list = mutableListOf<CutRequirement>()
        if (cadreWidth > 0) list.add(CutRequirement(CutTypes.CADRE, cadreWidth, 2 * n))
        if (cadreHeight > 0) list.add(CutRequirement(CutTypes.CADRE, cadreHeight, 2 * n))
        if (portRoulettesLength > 0)
            list.add(CutRequirement(CutTypes.PORT_ROULETTES, portRoulettesLength, 4 * n))
        if (portVerreauxLength > 0)
            list.add(CutRequirement(CutTypes.PORT_VERREAUX, portVerreauxLength, 2 * n))
        if (crouchementLength > 0)
            list.add(CutRequirement(CutTypes.CROUCHEMENT, crouchementLength, 2 * n))

        return list
    }
}
