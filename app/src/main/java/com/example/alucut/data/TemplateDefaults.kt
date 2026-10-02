package com.example.alucut.data

object TemplateDefaults {

    fun defaultTemplates(): List<Template> = listOf(
        Template(
            id = "single_door_1",
            name = "باب بدفة واحدة",
            type = TemplateType.SINGLE_DOOR,
            category = TemplateCategory.DOOR,
            params = mapOf(
                ParamKeys.BAR_LENGTH to 600.0,
                ParamKeys.KERF to 0.3,
                ParamKeys.Z_V_OFFSET to 2.5,
                ParamKeys.Z_H_OFFSET to 5.0,
                ParamKeys.T_OFFSET to 10.0
            )
        ),
        Template(
            id = "double_door_1",
            name = "باب بدفتين",
            type = TemplateType.DOUBLE_DOOR,
            category = TemplateCategory.DOOR,
            params = mapOf(
                ParamKeys.BAR_LENGTH to 600.0,
                ParamKeys.KERF to 0.3,
                ParamKeys.GAP to 1.0,
                ParamKeys.CADRE_OUVRANT_THICKNESS to 12.5,
                ParamKeys.TOP_BOTTOM_DOUBLE to 5.0
            )
        ),
        Template(
            id = "single_window_1",
            name = "نافذة بدفة واحدة",
            type = TemplateType.SINGLE_WINDOW,
            category = TemplateCategory.WINDOW,
            params = mapOf(
                ParamKeys.BAR_LENGTH to 600.0,
                ParamKeys.KERF to 0.3,
                ParamKeys.Z_V_OFFSET to 2.5,
                ParamKeys.Z_H_OFFSET to 5.0,
                ParamKeys.T_OFFSET to 10.0
            )
        ),
        Template(
            id = "double_window_1",
            name = "نافذة بدفتين",
            type = TemplateType.DOUBLE_WINDOW,
            category = TemplateCategory.WINDOW,
            params = mapOf(
                ParamKeys.BAR_LENGTH to 600.0,
                ParamKeys.KERF to 0.3,
                ParamKeys.GAP to 1.0,
                ParamKeys.CADRE_OUVRANT_THICKNESS to 12.5,
                ParamKeys.TOP_BOTTOM_DOUBLE to 5.0
            )
        ),
        Template(
            id = "sliding_window_1",
            name = "نافذة بدفتين منزلقتين",
            type = TemplateType.SLIDING_WINDOW,
            category = TemplateCategory.WINDOW,
            params = mapOf(
                ParamKeys.BAR_LENGTH to 600.0,
                ParamKeys.KERF to 0.3,
                ParamKeys.FRAME_THICKNESS to 3.5,
                ParamKeys.INNER_VERTICAL to 3.5,
                ParamKeys.TOP_BOTTOM to 2.5
            )
        ),
        Template(
            id = "custom_1",
            name = "شكل مخصص",
            type = TemplateType.CUSTOM,
            category = TemplateCategory.WINDOW,
            params = mapOf(
                ParamKeys.BAR_LENGTH to 600.0,
                ParamKeys.KERF to 0.3,
                ParamKeys.FRAME_THICKNESS to 3.5,
                ParamKeys.TOP_BOTTOM to 2.5
            )
        )
    )
}
