package com.example.alucut.data

enum class TemplateCategory { DOOR, WINDOW }

enum class TemplateType {
    SINGLE_DOOR,        // Porte 1 vantail battant
    DOUBLE_DOOR,        // Porte 2 vantaux battants
    SINGLE_WINDOW,      // Fenêtre 1 vantail
    DOUBLE_WINDOW,      // Fenêtre 2 vantaux
    SLIDING_WINDOW,     // Fenêtre coulissante
    CUSTOM              // Sur mesure
}

data class Template(
    val id: String,
    val name: String,
    val type: TemplateType,
    val category: TemplateCategory,
    val imageUri: String = "",
    val params: Map<String, Double> = emptyMap()
)

// ... (باقي البيانات كما هي)

object CutTypes {
    const val DORMANT_V = "Dormant (Vertical)"
    const val DORMANT_H = "Dormant (Horizontal)"
    const val OUVRANT_V = "Ouvrant (Vertical)"
    const val OUVRANT_H = "Ouvrant (Horizontal)"
    const val TRAVERSE = "Traverse"
    const val PARCLOSE = "Parclose"
    const val Z = "Z"
    const val T = "T"
    // ... (باقي الأنواع)
}

object ParamKeys {
    const val BAR_LENGTH = "barLength"
    const val KERF = "kerf"
    // Dormant (الإطار الثابت)
    const val DORMANT_WIDTH = "dormantWidth"       // عرض قطاع Dormant
    const val DORMANT_HEIGHT = "dormantHeight"     // ارتفاع قطاع Dormant
    // Ouvrant (الدرفة)
    const val OUVRANT_WIDTH = "ouvrantWidth"       // عرض قطاع Ouvrant
    const val OUVRANT_HEIGHT = "ouvrantHeight"     // ارتفاع قطاع Ouvrant
    const val OUVRANT_CLEARANCE = "ouvrantClearance" // فراغ بين Dormant و Ouvrant
    // Parclose (المثبتة الزجاج)
    const val PARCLOSE_WIDTH = "parcloseWidth"
    // Glass (الزجاج)
    const val GLASS_CLEARANCE = "glassClearance"   // فراغ الزجاج
    // Spécifique
    const val GAP = "gap"                          // فراغ بين vantaux
    const val INNER_VERTICAL = "innerVertical"     // قطاع عمودي داخلي
    const val TOP_BOTTOM = "topBottom"             // قطاع علوي/سفلي
}
