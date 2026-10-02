package com.example.alucut.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.alucut.data.*
import com.example.alucut.ui.FloatingBackButton

@Composable
fun TemplateEditorScreen(
    template: Template?,
    isNew: Boolean,
    onBack: () -> Unit,
    onSave: (Template) -> Unit
) {
    val context = LocalContext.current
    var name by remember { mutableStateOf(template?.name ?: "") }
    var type by remember { mutableStateOf(template?.type ?: TemplateType.SINGLE_DOOR) }
    var imageUri by remember { mutableStateOf(template?.imageUri ?: "") }
    val paramsMap = remember {
        mutableStateMapOf<String, String>().apply {
            val source = template?.params ?: standardParams(type)
            source.forEach { (k, v) -> put(k, v.toString()) }
        }
    }
    var error by remember { mutableStateOf("") }

    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri -> if (uri != null) imageUri = uri.toString() }

    LaunchedEffect(type) {
        paramsMap.clear()
        standardParams(type).forEach { (k, v) -> paramsMap[k] = v.toString() }
    }

    val labelMap = paramLabels()
    val category = if (type == TemplateType.SINGLE_DOOR ||
        type == TemplateType.DOUBLE_DOOR) TemplateCategory.DOOR
        else TemplateCategory.WINDOW

    Box(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(top = 70.dp, bottom = 30.dp, start = 16.dp, end = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                if (isNew) "إنشاء نوع جديد" else "تعديل النوع",
                fontSize = 20.sp, fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("اسم النوع") },
                placeholder = { Text("مثال: باب المدخل") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            // ═══ اختيار النوع ═══
            Text("النوع", fontWeight = FontWeight.Bold, fontSize = 15.sp)
            TemplateType.values().forEach { t ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(selected = type == t, onClick = { type = t })
                    Column(Modifier.weight(1f)) {
                        Text(typeLabel(t), fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Text(typeDescription(t), fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 14.sp)
                    }
                }
            }

            HorizontalDivider()

            // ═══ الرسم التوضيحي ═══
            Text("مخطط القطع", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text(
                "يوضح مكان كل قطعة في المنتج النهائي",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Card(
                modifier = Modifier.fillMaxWidth().height(240.dp),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0A0A0A))
            ) {
                TemplateDiagram(type)
            }

            // ═══ شرح الصيغة ═══
            Text("صيغة Débitage القياسية", fontWeight = FontWeight.Bold, fontSize = 15.sp)

            val formulas = formulaExplanation(type)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(Modifier.padding(10.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    formulas.forEach { (piece, formula) ->
                        Row(Modifier.fillMaxWidth()) {
                            Text("• $piece:", fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                modifier = Modifier.weight(0.4f),
                                color = MaterialTheme.colorScheme.primary)
                            Text(formula, fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.weight(0.6f),
                                lineHeight = 14.sp)
                        }
                    }
                }
            }

            HorizontalDivider()

            // ═══ معاملات Débitage ═══
            Text("المعاملات", fontWeight = FontWeight.Bold, fontSize = 15.sp)
            Text("عدّلها حسب نظام الألمنيوم الخاص بك",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant)

            paramsMap.keys.sorted().forEach { key ->
                OutlinedTextField(
                    value = paramsMap[key] ?: "",
                    onValueChange = { paramsMap[key] = it },
                    label = { Text(labelMap[key] ?: key) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    shape = RoundedCornerShape(12.dp)
                )
            }

            HorizontalDivider()

            // ═══ صورة ═══
            Text("صورة النوع (اختياري)", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Button(
                onClick = { imagePicker.launch("image/*") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Image, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text(if (imageUri.isEmpty()) "اختر صورة" else "تغيير الصورة")
            }

            if (error.isNotEmpty()) {
                Surface(
                    color = MaterialTheme.colorScheme.errorContainer,
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(error, modifier = Modifier.fillMaxWidth().padding(12.dp),
                        color = MaterialTheme.colorScheme.onErrorContainer)
                }
            }

            Spacer(Modifier.height(8.dp))

            Button(
                onClick = {
                    error = ""
                    if (name.isBlank()) { error = "الاسم مطلوب"; return@Button }
                    val parsed = mutableMapOf<String, Double>()
                    for ((k, v) in paramsMap) {
                        val d = v.toDoubleOrNull()
                        if (d == null) {
                            error = "قيمة غير صحيحة في: ${labelMap[k] ?: k}"
                            return@Button
                        }
                        parsed[k] = d
                    }
                    val t = Template(
                        id = template?.id ?: "custom_${System.currentTimeMillis()}",
                        name = name.trim(), type = type, category = category,
                        imageUri = imageUri, params = parsed
                    )
                    onSave(t)
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text(if (isNew) "إنشاء" else "حفظ",
                    fontSize = 17.sp, fontWeight = FontWeight.Bold)
            }
        }

        FloatingBackButton(onBack = onBack, modifier = Modifier.align(Alignment.TopStart))
    }
}

// ═══════════════════════════════════════════
// الرسم التوضيحي (Diagram)
// ═══════════════════════════════════════════

@Composable
fun TemplateDiagram(type: TemplateType) {
    val dormantColor = Color(0xFF4FC3F7)
    val ouvrantColor = Color(0xFF29B6F6)
    val glassColor = Color(0xFF1A3A52)
    val railColor = Color(0xFF81C784)
    val labelColor = Color.White

    Canvas(modifier = Modifier.fillMaxSize().padding(20.dp)) {
        val w = size.width
        val h = size.height
        val frame = 10.dp.toPx()
        val thinFrame = 6.dp.toPx()
        val textPaint = android.graphics.Paint().apply {
            color = android.graphics.Color.WHITE
            textSize = 9.dp.toPx()
            isAntiAlias = true
            textAlign = android.graphics.Paint.Align.CENTER
        }

        fun drawLabel(text: String, x: Float, y: Float) {
            drawContext.canvas.nativeCanvas.drawText(text, x, y, textPaint)
        }

        when (type) {
            TemplateType.SINGLE_DOOR -> {
                // زجاج
                drawRect(glassColor, Offset(frame, frame),
                    Size(w - 2 * frame, h - 2 * frame))
                // Dormant (إطار خارجي)
                drawRect(dormantColor, Offset(0f, 0f), Size(w, h),
                    style = Stroke(frame))
                // Ouvrant (الدرفة الداخلية)
                drawRect(ouvrantColor,
                    Offset(frame * 2, frame * 2),
                    Size(w - 4 * frame, h - 4 * frame),
                    style = Stroke(thinFrame))
                // مقبض
                drawCircle(Color(0xFFB0BEC5), 6.dp.toPx(),
                    Offset(w - frame * 3, h / 2))
                // تسميات
                drawLabel("Dormant", w / 2, frame / 2 + 4)
                drawLabel("Ouvrant", w / 2, frame * 3)
                drawLabel("Parclose", w / 2, h - frame * 1.5f)
            }
            TemplateType.DOUBLE_DOOR -> {
                drawRect(glassColor, Offset(frame, frame),
                    Size(w - 2 * frame, h - 2 * frame))
                drawRect(dormantColor, Offset(0f, 0f), Size(w, h),
                    style = Stroke(frame))
                // الدفة اليسرى
                drawRect(ouvrantColor, Offset(frame * 2, frame * 2),
                    Size(w / 2 - 3 * frame, h - 4 * frame),
                    style = Stroke(thinFrame))
                // الدفة اليمنى
                drawRect(ouvrantColor, Offset(w / 2 + frame, frame * 2),
                    Size(w / 2 - 3 * frame, h - 4 * frame),
                    style = Stroke(thinFrame))
                // المقابض
                drawCircle(Color(0xFFB0BEC5), 5.dp.toPx(),
                    Offset(w / 2 - frame, h / 2))
                drawCircle(Color(0xFFB0BEC5), 5.dp.toPx(),
                    Offset(w / 2 + frame, h / 2))
                drawLabel("Dormant", w / 2, frame / 2 + 4)
                drawLabel("Ouvrant 1", w / 4, frame * 3)
                drawLabel("Ouvrant 2", 3 * w / 4, frame * 3)
                drawLabel("Jeu", w / 2, h - frame * 1.5f)
            }
            TemplateType.SINGLE_WINDOW -> {
                drawRect(glassColor, Offset(frame, frame),
                    Size(w - 2 * frame, h - 2 * frame))
                drawRect(dormantColor, Offset(0f, 0f), Size(w, h),
                    style = Stroke(frame))
                drawRect(ouvrantColor,
                    Offset(frame * 2, frame * 2),
                    Size(w - 4 * frame, h - 4 * frame),
                    style = Stroke(thinFrame))
                drawCircle(Color(0xFFB0BEC5), 5.dp.toPx(),
                    Offset(w - frame * 3, h / 2))
                drawLabel("Dormant", w / 2, frame / 2 + 4)
                drawLabel("Ouvrant", w / 2, frame * 3)
                drawLabel("Parclose", w / 2, h - frame * 1.5f)
            }
            TemplateType.DOUBLE_WINDOW -> {
                drawRect(glassColor, Offset(frame, frame),
                    Size(w - 2 * frame, h - 2 * frame))
                drawRect(dormantColor, Offset(0f, 0f), Size(w, h),
                    style = Stroke(frame))
                drawRect(ouvrantColor, Offset(frame * 2, frame * 2),
                    Size(w / 2 - 3 * frame, h - 4 * frame),
                    style = Stroke(thinFrame))
                drawRect(ouvrantColor, Offset(w / 2 + frame, frame * 2),
                    Size(w / 2 - 3 * frame, h - 4 * frame),
                    style = Stroke(thinFrame))
                drawLabel("Dormant", w / 2, frame / 2 + 4)
                drawLabel("Ouvrant 1", w / 4, frame * 3)
                drawLabel("Ouvrant 2", 3 * w / 4, frame * 3)
                drawLabel("Meneau", w / 2, h / 2)
            }
            TemplateType.SLIDING_WINDOW -> {
                drawRect(glassColor, Offset(frame, frame),
                    Size(w - 2 * frame, h - 2 * frame))
                // Dormant
                drawRect(dormantColor, Offset(0f, 0f), Size(w, h),
                    style = Stroke(frame))
                // Rail (مسار علوي)
                drawRect(railColor, Offset(frame, frame * 1.5f),
                    Size(w - 2 * frame, frame * 0.7f))
                // Dormant 1 (درفة ثابتة)
                drawRect(ouvrantColor, Offset(frame * 2, frame * 3),
                    Size(w / 2 - 2 * frame, h - 5 * frame),
                    style = Stroke(thinFrame))
                // Dormant 2 (درفة منزلقة)
                drawRect(ouvrantColor, Offset(w / 2 + frame, frame * 3),
                    Size(w / 2 - 3 * frame, h - 5 * frame),
                    style = Stroke(thinFrame))
                // سهم يشير للحركة
                val arrowY = h / 2
                drawLine(railColor,
                    Offset(w / 2 - 20.dp.toPx(), arrowY),
                    Offset(w / 2 + 20.dp.toPx(), arrowY),
                    strokeWidth = 2.dp.toPx())
                drawLabel("Dormant", w / 2, frame / 2 + 4)
                drawLabel("Rail", w / 2, frame * 1.5f)
                drawLabel("Coulissant", w / 4, h / 2)
            }
            TemplateType.CUSTOM -> {
                // نجمة بسيطة
                val path = Path().apply {
                    val cx = w / 2; val cy = h / 2
                    val r1 = w * 0.4f; val r2 = w * 0.2f
                    for (i in 0 until 10) {
                        val angle = Math.PI * i / 5 - Math.PI / 2
                        val r = if (i % 2 == 0) r1 else r2
                        val x = cx + (r * Math.cos(angle)).toFloat()
                        val y = cy + (r * Math.sin(angle)).toFloat()
                        if (i == 0) moveTo(x, y) else lineTo(x, y)
                    }
                    close()
                }
                drawPath(path, Color(0xFF4FC3F7).copy(alpha = 0.3f))
                drawPath(path, Color(0xFF4FC3F7), style = Stroke(2.dp.toPx()))
                drawLabel("Sur mesure", w / 2, h / 2)
            }
        }
    }
}

// ═══════════════════════════════════════════
// الصيغ القياسية + الشروحات
// ═══════════════════════════════════════════

fun standardParams(t: TemplateType): Map<String, Double> = when (t) {
    TemplateType.SINGLE_DOOR, TemplateType.SINGLE_WINDOW -> mapOf(
        ParamKeys.BAR_LENGTH to 600.0,
        ParamKeys.KERF to 0.3,
        ParamKeys.DORMANT_WIDTH to 4.5,
        ParamKeys.OUVRANT_WIDTH to 4.0,
        ParamKeys.OUVRANT_CLEARANCE to 0.5,
        ParamKeys.PARCLOSE_WIDTH to 1.5
    )
    TemplateType.DOUBLE_DOOR, TemplateType.DOUBLE_WINDOW -> mapOf(
        ParamKeys.BAR_LENGTH to 600.0,
        ParamKeys.KERF to 0.3,
        ParamKeys.DORMANT_WIDTH to 4.5,
        ParamKeys.OUVRANT_WIDTH to 4.0,
        ParamKeys.GAP to 0.5,
        ParamKeys.PARCLOSE_WIDTH to 1.5
    )
    TemplateType.SLIDING_WINDOW -> mapOf(
        ParamKeys.BAR_LENGTH to 600.0,
        ParamKeys.KERF to 0.3,
        ParamKeys.DORMANT_WIDTH to 4.5,
        ParamKeys.OUVRANT_WIDTH to 4.0,
        ParamKeys.INNER_VERTICAL to 3.5,
        ParamKeys.TOP_BOTTOM to 2.5
    )
    TemplateType.CUSTOM -> mapOf(
        ParamKeys.BAR_LENGTH to 600.0,
        ParamKeys.KERF to 0.3,
        ParamKeys.DORMANT_WIDTH to 4.5,
        ParamKeys.OUVRANT_WIDTH to 4.0,
        ParamKeys.PARCLOSE_WIDTH to 1.5
    )
}

fun formulaExplanation(t: TemplateType): List<Pair<String, String>> = when (t) {
    TemplateType.SINGLE_DOOR, TemplateType.SINGLE_WINDOW -> listOf(
        "Dormant V" to "H (الارتفاع الكلي) × 2",
        "Dormant H" to "(L - 2×Dormant) × 1",
        "Ouvrant V" to "(H - 2×Dormant + 2×Jeu) × 2",
        "Ouvrant H" to "(L - 2×Dormant + 2×Jeu) - 2×Ouvrant",
        "Parclose V" to "(Ouvrant V) - 2×Parclose × 2",
        "Parclose H" to "(Ouvrant H) - 2×Parclose × 1"
    )
    TemplateType.DOUBLE_DOOR, TemplateType.DOUBLE_WINDOW -> listOf(
        "Dormant V" to "H × 2",
        "Dormant H" to "(L - 2×Dormant) × 2",
        "Ouvrant V" to "(H - 2×Dormant) × 4",
        "Ouvrant H" to "((L - 2×Dormant - Jeu)/2) - 2×Ouvrant × 4",
        "Parclose V" to "(Ouvrant V) - 2×Parclose × 4",
        "Parclose H" to "(Ouvrant H) - 2×Parclose × 4"
    )
    TemplateType.SLIDING_WINDOW -> listOf(
        "Dormant V" to "H × 2",
        "Dormant H" to "(L - 2×Dormant) × 1",
        "Rail" to "(L - 2×Dormant) × 1",
        "Ouvrant Coulissant V" to "(H - Dormant - Rail) × 4",
        "Ouvrant Coulissant H" to "(L/2 - Dormant) × 4"
    )
    TemplateType.CUSTOM -> listOf(
        "معاملات حرة" to "عدّل حسب نظامك"
    )
}

fun typeLabel(t: TemplateType): String = when (t) {
    TemplateType.SINGLE_DOOR -> "باب بدفة واحدة"
    TemplateType.DOUBLE_DOOR -> "باب بدفتين"
    TemplateType.SINGLE_WINDOW -> "نافذة بدفة واحدة"
    TemplateType.DOUBLE_WINDOW -> "نافذة بدفتين"
    TemplateType.SLIDING_WINDOW -> "نافذة منزلقة"
    TemplateType.CUSTOM -> "شكل مخصص"
}

fun typeDescription(t: TemplateType): String = when (t) {
    TemplateType.SINGLE_DOOR, TemplateType.SINGLE_WINDOW ->
        "Dormant + Ouvrant + Parclose"
    TemplateType.DOUBLE_DOOR, TemplateType.DOUBLE_WINDOW ->
        "Dormant + 2×Ouvrant + Parclose"
    TemplateType.SLIDING_WINDOW ->
        "Dormant + Rail + 2×Coulissant"
    TemplateType.CUSTOM -> "معاملات حرة"
}

fun paramLabels(): Map<String, String> = mapOf(
    ParamKeys.BAR_LENGTH to "طول العمود (سم)",
    ParamKeys.KERF to "سمك القرص Kerf (سم)",
    ParamKeys.DORMANT_WIDTH to "عرض Dormant (سم)",
    ParamKeys.OUVRANT_WIDTH to "عرض Ouvrant (سم)",
    ParamKeys.OUVRANT_CLEARANCE to "فراغ Ouvrant (سم)",
    ParamKeys.PARCLOSE_WIDTH to "عرض Parclose (سم)",
    ParamKeys.GAP to "الفراغ بين الدفتين (سم)",
    ParamKeys.INNER_VERTICAL to "قطاع داخلي رأسي (سم)",
    ParamKeys.TOP_BOTTOM to "قطاع علوي/سفلي (سم)"
)
