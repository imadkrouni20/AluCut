package com.example.alucut.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
    defaultType: TemplateType,
    onBack: () -> Unit,
    onSave: (Template) -> Unit
) {
    val context = LocalContext.current
    var name by remember { mutableStateOf(template?.name ?: "قالب جديد") }
    var type by remember { mutableStateOf(template?.type ?: defaultType) }
    var imageUri by remember { mutableStateOf(template?.imageUri ?: "") }
    val paramsMap = remember {
        mutableStateMapOf<String, String>().apply {
            val source = template?.params ?: defaultParams(type)
            source.forEach { (k, v) -> put(k, v.toString()) }
        }
    }
    var error by remember { mutableStateOf("") }

    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri -> if (uri != null) imageUri = uri.toString() }

    LaunchedEffect(type) {
        if (isNew) {
            paramsMap.clear()
            defaultParams(type).forEach { (k, v) -> paramsMap[k] = v.toString() }
        }
    }

    val labelMap = paramLabels()
    val category = if (type == TemplateType.SINGLE_DOOR || type == TemplateType.DOUBLE_DOOR)
        TemplateCategory.DOOR else TemplateCategory.WINDOW

    Box(Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(top = 70.dp, bottom = 30.dp, start = 16.dp, end = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("اسم القالب") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            if (isNew) {
                Text("نوع القالب", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                TemplateType.values().forEach { t ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(selected = type == t, onClick = { type = t })
                        Text(typeLabel(t), fontSize = 14.sp)
                    }
                }
            } else {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        "النوع: ${typeLabel(type)}",
                        modifier = Modifier.padding(12.dp),
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            HorizontalDivider()
            Text("الإعدادات", fontWeight = FontWeight.Bold, fontSize = 16.sp)

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
            Text("صورة القالب (اختياري)", fontWeight = FontWeight.Bold, fontSize = 14.sp)

            Button(
                onClick = { imagePicker.launch("image/*") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Image, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text(if (imageUri.isEmpty()) "اختر صورة" else "تغيير الصورة")
            }

            if (imageUri.isNotEmpty()) {
                Text("✓ تم اختيار صورة", fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.secondary)
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

            Spacer(modifier = Modifier.height(8.dp))

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
                        name = name.trim(),
                        type = type,
                        category = category,
                        imageUri = imageUri,
                        params = parsed
                    )
                    onSave(t)
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text("حفظ", fontSize = 17.sp, fontWeight = FontWeight.Bold)
            }
        }

        FloatingBackButton(onBack = onBack, modifier = Modifier.align(Alignment.TopStart))
    }
}

fun typeLabel(t: TemplateType): String = when (t) {
    TemplateType.SINGLE_DOOR -> "باب بدفة واحدة"
    TemplateType.DOUBLE_DOOR -> "باب بدفتين"
    TemplateType.SINGLE_WINDOW -> "نافذة بدفة واحدة"
    TemplateType.DOUBLE_WINDOW -> "نافذة بدفتين"
    TemplateType.SLIDING_WINDOW -> "نافذة بدفتين منزلقتين"
    TemplateType.CUSTOM -> "شكل مخصص"
}

fun defaultParams(t: TemplateType): Map<String, Double> = when (t) {
    TemplateType.SINGLE_DOOR, TemplateType.SINGLE_WINDOW -> mapOf(
        ParamKeys.BAR_LENGTH to 600.0,
        ParamKeys.KERF to 0.3,
        ParamKeys.Z_V_OFFSET to 2.5,
        ParamKeys.Z_H_OFFSET to 5.0,
        ParamKeys.T_OFFSET to 10.0
    )
    TemplateType.DOUBLE_DOOR, TemplateType.DOUBLE_WINDOW -> mapOf(
        ParamKeys.BAR_LENGTH to 600.0,
        ParamKeys.KERF to 0.3,
        ParamKeys.GAP to 1.0,
        ParamKeys.CADRE_OUVRANT_THICKNESS to 12.5,
        ParamKeys.TOP_BOTTOM_DOUBLE to 5.0
    )
    TemplateType.SLIDING_WINDOW -> mapOf(
        ParamKeys.BAR_LENGTH to 600.0,
        ParamKeys.KERF to 0.3,
        ParamKeys.FRAME_THICKNESS to 3.5,
        ParamKeys.INNER_VERTICAL to 3.5,
        ParamKeys.TOP_BOTTOM to 2.5
    )
    TemplateType.CUSTOM -> mapOf(
        ParamKeys.BAR_LENGTH to 600.0,
        ParamKeys.KERF to 0.3,
        ParamKeys.FRAME_THICKNESS to 3.5,
        ParamKeys.TOP_BOTTOM to 2.5
    )
}

fun paramLabels(): Map<String, String> = mapOf(
    ParamKeys.BAR_LENGTH to "طول العمود (سم)",
    ParamKeys.KERF to "سمك المنشار (سم)",
    ParamKeys.FRAME_THICKNESS to "سمك الإطار (سم)",
    ParamKeys.INNER_VERTICAL to "عرض Port Verreaux (سم)",
    ParamKeys.TOP_BOTTOM to "عمق علوي/سفلي (سم)",
    ParamKeys.Z_V_OFFSET to "فراغ Z العمودي (سم)",
    ParamKeys.Z_H_OFFSET to "فراغ Z الأفقي (سم)",
    ParamKeys.T_OFFSET to "فراغ T (سم)",
    ParamKeys.GAP to "الفراغ بين الدفتين (سم)",
    ParamKeys.CADRE_OUVRANT_THICKNESS to "سمك Cadre Ouvrant (سم)",
    ParamKeys.TOP_BOTTOM_DOUBLE to "عمق علوي/سفلي (سم)"
)
