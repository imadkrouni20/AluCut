package com.example.alucut.data

object Calculator {
    // ... (الدالة calculate و calculateAll كما هي) ...

    private fun slidingWindow(p: Map<String, Double>, w: Double, h: Double, n: Int): List<CutRequirement> {
        val frameThickness = p[ParamKeys.FRAME_THICKNESS] ?: 3.5
        val topBottomOffset = p[ParamKeys.TOP_BOTTOM] ?: 2.5
        // ... (استخدم المعاملات لحساب الأطوال)
        // مثال: طول القطعة الأفقية = العرض - (2 * سُمك الإطار)
        val horizontalLength = w - (2 * frameThickness)
        // ...
        return buildList { /* ... */ }
    }
    // ... (باقي الدوال)
}
