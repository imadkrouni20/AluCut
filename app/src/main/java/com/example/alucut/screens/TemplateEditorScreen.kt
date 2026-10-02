// ... (الاستيرادات كما هي)

@Composable
fun TemplateEditorScreen(...) {
    // ... (الكود كما هو حتى قسم اختيار النوع)

    // ═══ شرح الصيغة القياسية ═══
    Text("صيغة Débitage القياسية", fontWeight = FontWeight.Bold, fontSize = 16.sp)
    
    // عرض الصيغ بناءً على النوع المختار
    val formulas = formulaExplanation(type)
    formulas.forEach { (piece, formula) ->
        Row(Modifier.fillMaxWidth().padding(vertical = 2.dp)) {
            Text("• $piece:", fontWeight = FontWeight.Bold, fontSize = 13.sp, modifier = Modifier.weight(0.4f))
            Text(formula, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.weight(0.6f))
        }
    }
    
    Spacer(Modifier.height(8.dp))

    // ... (باقي الكود لملء المعاملات)
}

// دالة توضح الصيغة لكل نوع
fun formulaExplanation(type: TemplateType): List<Pair<String, String>> = when (type) {
    TemplateType.SINGLE_DOOR -> listOf(
        "Dormant (V)" to "H (الارتفاع الكلي) × 2",
        "Dormant (H)" to "L - 2×Dormant × 1",
        "Ouvrant (V)" to "(H - 2×Dormant + 2×فراغ) × 2",
        "Ouvrant (H)" to "(L - 2×Dormant + 2×فراغ) - 2×Ouvrant × 1",
        "Parclose (V)" to "(Ouvrant V) - 2×Parclose × 2",
        "Parclose (H)" to "(Ouvrant H) - 2×Parclose × 1",
        "Z (V)" to "(Ouvrant V) - 4 × 2",
        "Z (H)" to "(Ouvrant H) - 4 × 2",
        "T" to "(Ouvrant H) - 8 × 1"
    )
    TemplateType.DOUBLE_DOOR -> listOf(
        "Dormant (V)" to "H × 2",
        "Dormant (H)" to "L - 2×Dormant × 2",
        "Ouvrant (V)" to "(H - 2×Dormant) × 4",
        "Ouvrant (H)" to "((L - 2×Dormant - فراغ) / 2) - 2×Ouvrant × 4",
        "Traverse" to "((L - 2×Dormant - فراغ) / 2) - 2×Ouvrant × 2",
        "Z (V)" to "(Ouvrant V) - 4 × 4",
        "Z (H)" to "(Ouvrant H) - 4 × 4",
        "T" to "(Ouvrant H) - 8 × 1"
    )
    // ... (باقي الأنواع)
    else -> listOf("معاملات حرة" to "يمكنك إدخال أي معاملات يدوياً")
}

// ... (باقي الكود)
