package com.example.alucut.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.alucut.data.*
import com.example.alucut.ui.FloatingBackButton
import com.example.alucut.ui.colorForType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TemplateEditorScreen(
    template: Template?,
    isNew: Boolean,
    onBack: () -> Unit,
    onSave: (Template) -> Unit
) {
    var name by remember { mutableStateOf(template?.name ?: "") }
    var type by remember { mutableStateOf(template?.type ?: TemplateType.SINGLE_DOOR) }
    var barLength by remember { mutableStateOf((template?.barLengthCm ?: 600.0).toString()) }
    var kerf by remember { mutableStateOf((template?.kerfCm ?: 0.3).toString()) }

    val vars = remember {
        mutableStateListOf<FormulaVariable>().apply {
            addAll(template?.variables ?: VariablesCatalog.suggestedVars())
        }
    }
    val pieces = remember {
        mutableStateListOf<CutPiece>().apply {
            addAll(template?.pieces ?: emptyList())
        }
    }

    // معاينة مباشرة
    var previewL by remember { mutableStateOf("120") }
    var previewH by remember { mutableStateOf("100") }
    var previewResult by remember { mutableStateOf<List<Pair<CutPiece, Pair<Int, Double>>>>(emptyList()) }
    var previewError by remember { mutableStateOf("") }

    var showVarDialog by remember { mutableStateOf(false) }
    var showPieceDialog by remember { mutableStateOf(false) }
    var editingVarIndex by remember { mutableStateOf(-1) }
    var editingPieceIndex by remember { mutableStateOf(-1) }

    var error by remember { mutableStateOf("") }

    Box(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(top = 70.dp, bottom = 30.dp, start = 14.dp, end = 14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(if (isNew) "إنشاء قالب جديد" else "تعديل القالب",
                fontSize = 20.sp, fontWeight = FontWeight.Bold)

            // ═══ المعلومات الأساسية ═══
            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = name, onValueChange = { name = it },
                        label = { Text("اسم القالب") },
                        modifier = Modifier.fillMaxWidth(), singleLine = true,
                        shape = RoundedCornerShape(10.dp)
                    )
                    Text("النوع", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    var expanded by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = it }
                    ) {
                        OutlinedTextField(
                            value = type.label,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("النوع") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                            modifier = Modifier.fillMaxWidth().menuAnchor(),
                            shape = RoundedCornerShape(10.dp)
                        )
                        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                            TemplateType.values().forEach { t ->
                                DropdownMenuItem(
                                    text = { Text("${t.label} (${t.cat.name})") },
                                    onClick = { type = t; expanded = false }
                                )
                            }
                        }
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = barLength, onValueChange = { barLength = it },
                            label = { Text("طول العمود سم") },
                            modifier = Modifier.weight(1f), singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            shape = RoundedCornerShape(10.dp)
                        )
                        OutlinedTextField(
                            value = kerf, onValueChange = { kerf = it },
                            label = { Text("سمك القرص سم") },
                            modifier = Modifier.weight(1f), singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            shape = RoundedCornerShape(10.dp)
                        )
                    }
                }
            }

            // ═══ المتغيرات ═══
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("📌 المتغيرات (${vars.size})",
                    fontWeight = FontWeight.Bold, fontSize = 16.sp,
                    modifier = Modifier.weight(1f))
                FilledTonalIconButton(onClick = {
                    editingVarIndex = -1
                    showVarDialog = true
                }) { Icon(Icons.Default.Add, "إضافة متغير") }
            }

            vars.forEachIndexed { idx, v ->
                Card(colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                    Row(Modifier.fillMaxWidth().padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically) {
                        Text("${v.key} = ${v.defaultValue}",
                            fontWeight = FontWeight.Bold, fontSize = 14.sp,
                            modifier = Modifier.width(120.dp))
                        Text(v.label, fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.weight(1f))
                        IconButton(onClick = {
                            editingVarIndex = idx
                            showVarDialog = true
                        }, modifier = Modifier.size(32.dp)) {
                            Icon(Icons.Default.Add, "تعديل",
                                modifier = Modifier.size(16.dp))
                        }
                        IconButton(onClick = { vars.removeAt(idx) },
                            modifier = Modifier.size(32.dp)) {
                            Icon(Icons.Default.Delete, "حذف",
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }

            HorizontalDivider()

            // ═══ القطع ═══
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("✂️ القطع (${pieces.size})",
                    fontWeight = FontWeight.Bold, fontSize = 16.sp,
                    modifier = Modifier.weight(1f))
                FilledTonalIconButton(onClick = {
                    editingPieceIndex = -1
                    showPieceDialog = true
                }) { Icon(Icons.Default.Add, "إضافة قطعة") }
            }

            pieces.forEachIndexed { idx, p ->
                Card(colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                    Column(Modifier.fillMaxWidth().padding(10.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(Modifier.size(10.dp)
                                .background(colorForType(p.barType),
                                    RoundedCornerShape(3.dp)))
                            Spacer(Modifier.width(6.dp))
                            Text(p.name, fontWeight = FontWeight.Bold,
                                fontSize = 14.sp, modifier = Modifier.weight(1f))
                            IconButton(onClick = {
                                editingPieceIndex = idx
                                showPieceDialog = true
                            }, modifier = Modifier.size(28.dp)) {
                                Icon(Icons.Default.Add, "تعديل",
                                    modifier = Modifier.size(14.dp))
                            }
                            IconButton(onClick = { pieces.removeAt(idx) },
                                modifier = Modifier.size(28.dp)) {
                                Icon(Icons.Default.Delete, "حذف",
                                    tint = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.size(14.dp))
                            }
                        }
                        Text("العدد: ${p.countFormula}  |  الطول: ${p.lengthFormula}",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("زوايا: ${p.angle1}° / ${p.angle2}°  |  عمود: ${p.barType}",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }

            HorizontalDivider()

            // ═══ المعاينة ═══
            Text("🔍 معاينة مباشرة", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = previewL, onValueChange = { previewL = it },
                    label = { Text("عرض تجريبي") },
                    modifier = Modifier.weight(1f), singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    shape = RoundedCornerShape(10.dp)
                )
                OutlinedTextField(
                    value = previewH, onValueChange = { previewH = it },
                    label = { Text("ارتفاع تجريبي") },
                    modifier = Modifier.weight(1f), singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    shape = RoundedCornerShape(10.dp)
                )
            }

            Button(
                onClick = {
                    previewError = ""
                    val lVal = previewL.toDoubleOrNull()
                    val hVal = previewH.toDoubleOrNull()
                    if (lVal == null || hVal == null) {
                        previewError = "أدخل قيم صحيحة"
                        return@Button
                    }
                    val tempTemplate = Template(
                        id = "preview", name = name, type = type,
                        barLengthCm = barLength.toDoubleOrNull() ?: 600.0,
                        kerfCm = kerf.toDoubleOrNull() ?: 0.3,
                        variables = vars.toList(), pieces = pieces.toList()
                    )
                    val varsMap = mutableMapOf<String, Double>()
                    varsMap["l"] = lVal
                    varsMap["h"] = hVal
                    varsMap["n"] = 1.0
                    vars.forEach { varsMap[it.key] = it.defaultValue }

                    val results = mutableListOf<Pair<CutPiece, Pair<Int, Double>>>()
                    for (p in pieces) {
                        try {
                            val c = FormulaEngine.evaluate(p.countFormula, varsMap).toInt()
                            val ln = FormulaEngine.evaluate(p.lengthFormula, varsMap)
                            results.add(p to (c to ln))
                        } catch (e: Exception) {
                            previewError = "خطأ في \"${p.name}\": ${e.message}"
                            return@Button
                        }
                    }
                    previewResult = results
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp)
            ) { Text("احسب المعاينة") }

            if (previewError.isNotEmpty()) {
                Surface(color = MaterialTheme.colorScheme.errorContainer,
                    shape = RoundedCornerShape(8.dp)) {
                    Text(previewError, Modifier.padding(10.dp),
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        fontSize = 12.sp)
                }
            }

            if (previewResult.isNotEmpty()) {
                Card(colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF0A0A0A))) {
                    Column(Modifier.padding(10.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        previewResult.forEach { (p, vals) ->
                            Row(Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(Modifier.size(8.dp).background(
                                        colorForType(p.barType), RoundedCornerShape(2.dp)))
                                    Spacer(Modifier.width(6.dp))
                                    Text(p.name, fontSize = 12.sp, color = Color.White)
                                }
                                Text("${vals.first} × %.1f سم".format(vals.second),
                                    fontSize = 12.sp, fontWeight = FontWeight.Bold,
                                    color = colorForType(p.barType))
                            }
                        }
                    }
                }
            }

            if (error.isNotEmpty()) {
                Surface(color = MaterialTheme.colorScheme.errorContainer,
                    shape = RoundedCornerShape(8.dp)) {
                    Text(error, Modifier.padding(10.dp),
                        color = MaterialTheme.colorScheme.onErrorContainer)
                }
            }

            Button(
                onClick = {
                    error = ""
                    if (name.isBlank()) { error = "الاسم مطلوب"; return@Button }
                    if (pieces.isEmpty()) { error = "أضف قطعة على الأقل"; return@Button }
                    val bl = barLength.toDoubleOrNull()
                    val k = kerf.toDoubleOrNull()
                    if (bl == null || k == null) { error = "قيم غير صحيحة"; return@Button }
                    onSave(Template(
                        id = template?.id ?: "t_${System.currentTimeMillis()}",
                        name = name.trim(), type = type,
                        barLengthCm = bl, kerfCm = k,
                        variables = vars.toList(), pieces = pieces.toList()
                    ))
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text(if (isNew) "إنشاء" else "حفظ التعديلات",
                    fontSize = 17.sp, fontWeight = FontWeight.Bold)
            }
        }

        FloatingBackButton(onBack = onBack, modifier = Modifier.align(Alignment.TopStart))
    }

    // ═══ نافذة إضافة/تعديل متغير ═══
    if (showVarDialog) {
        VariableDialog(
            existing = if (editingVarIndex >= 0) vars[editingVarIndex] else null,
            onDismiss = { showVarDialog = false },
            onSave = { v ->
                if (editingVarIndex >= 0) vars[editingVarIndex] = v
                else vars.add(v)
                showVarDialog = false
            }
        )
    }

    // ═══ نافذة إضافة/تعديل قطعة ═══
    if (showPieceDialog) {
        PieceDialog(
            existing = if (editingPieceIndex >= 0) pieces[editingPieceIndex] else null,
            vars = vars.toList(),
            onDismiss = { showPieceDialog = false },
            onSave = { p ->
                if (editingPieceIndex >= 0) pieces[editingPieceIndex] = p
                else pieces.add(p.copy(id = (pieces.maxOfOrNull { it.id } ?: 0) + 1))
                showPieceDialog = false
            }
        )
    }
}

// ═══════════════════════════════════════════
// نافذة المتغير
// ═══════════════════════════════════════════

@Composable
fun VariableDialog(
    existing: FormulaVariable?,
    onDismiss: () -> Unit,
    onSave: (FormulaVariable) -> Unit
) {
    var key by remember { mutableStateOf(existing?.key ?: "") }
    var label by remember { mutableStateOf(existing?.label ?: "") }
    var value by remember { mutableStateOf(existing?.defaultValue?.toString() ?: "") }
    var err by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (existing == null) "متغير جديد" else "تعديل المتغير") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = key, onValueChange = { key = it },
                    label = { Text("الرمز (مثل de)") }, singleLine = true)
                OutlinedTextField(value = label, onValueChange = { label = it },
                    label = { Text("الوصف") }, singleLine = true)
                OutlinedTextField(value = value, onValueChange = { value = it },
                    label = { Text("القيمة الافتراضية") }, singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal))
                Text("متاح: ${VariablesCatalog.ALL_KEYS.joinToString(", ") { it.first }}",
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
                if (err.isNotEmpty()) Text(err,
                    color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
            }
        },
        confirmButton = {
            TextButton(onClick = {
                err = ""
                val k = key.trim().lowercase()
                val v = value.toDoubleOrNull()
                if (k.isEmpty()) { err = "الرمز مطلوب"; return@TextButton }
                if (!k.first().isLetter()) { err = "يبدأ بحرف"; return@TextButton }
                if (v == null) { err = "قيمة غير صحيحة"; return@TextButton }
                onSave(FormulaVariable(k, label.trim().ifEmpty { k }, v))
            }) { Text("حفظ") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("إلغاء") } }
    )
}

// ═══════════════════════════════════════════
// نافذة القطعة
// ═══════════════════════════════════════════

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PieceDialog(
    existing: CutPiece?,
    vars: List<FormulaVariable>,
    onDismiss: () -> Unit,
    onSave: (CutPiece) -> Unit
) {
    var name by remember { mutableStateOf(existing?.name ?: "") }
    var countFormula by remember { mutableStateOf(existing?.countFormula ?: "1") }
    var lengthFormula by remember { mutableStateOf(existing?.lengthFormula ?: "l") }
    var angle1 by remember { mutableStateOf((existing?.angle1 ?: 90).toString()) }
    var angle2 by remember { mutableStateOf((existing?.angle2 ?: 90).toString()) }
    var barType by remember { mutableStateOf(existing?.barType ?: "Cadre") }
    var err by remember { mutableStateOf("") }

    var countErr by remember { mutableStateOf("") }
    var lengthErr by remember { mutableStateOf("") }

    // قائمة أسماء الأعمدة الشائعة
    val commonBars = listOf("Cadre", "Cadre Fix", "Ouvrant", "Z", "T",
        "Parclose", "Crouchement", "Meneau", "Rail", "Coulissant", "Seuil")

    val varsMap = mutableMapOf<String, Double>()
    varsMap["l"] = 120.0
    varsMap["h"] = 100.0
    varsMap["n"] = 1.0
    vars.forEach { varsMap[it.key] = it.defaultValue }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (existing == null) "قطعة جديدة" else "تعديل القطعة") },
        text = {
            Column(Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = name, onValueChange = { name = it },
                    label = { Text("اسم القطعة") }, singleLine = true,
                    modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = countFormula,
                    onValueChange = { countFormula = it; countErr = "" },
                    label = { Text("صيغة العدد") },
                    placeholder = { Text("1 أو 2 أو n أو 2*n") },
                    singleLine = true, modifier = Modifier.fillMaxWidth(),
                    isError = countErr.isNotEmpty())
                if (countErr.isNotEmpty()) Text(countErr, color = MaterialTheme.colorScheme.error, fontSize = 11.sp)
                OutlinedTextField(value = lengthFormula,
                    onValueChange = { lengthFormula = it; lengthErr = "" },
                    label = { Text("صيغة الطول") },
                    placeholder = { Text("h-5 أو l-(de*2)") },
                    singleLine = true, modifier = Modifier.fillMaxWidth(),
                    isError = lengthErr.isNotEmpty())
                if (lengthErr.isNotEmpty()) Text(lengthErr, color = MaterialTheme.colorScheme.error, fontSize = 11.sp)

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = angle1, onValueChange = { angle1 = it },
                        label = { Text("زاوية 1") }, singleLine = true,
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                    OutlinedTextField(value = angle2, onValueChange = { angle2 = it },
                        label = { Text("زاوية 2") }, singleLine = true,
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                }

                Text("العمود (نوع البروفيل)", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(commonBars) { b ->
                        FilterChip(
                            selected = barType == b,
                            onClick = { barType = b },
                            label = { Text(b, fontSize = 11.sp) }
                        )
                    }
                }
                OutlinedTextField(value = barType, onValueChange = { barType = it },
                    label = { Text("أو اكتب اسم عمود آخر") },
                    singleLine = true, modifier = Modifier.fillMaxWidth())

                Text("📋 المتغيرات المتاحة:",
                    fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Text(
                    (listOf("l", "h", "n") + vars.map { it.key }).joinToString(", "),
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                if (err.isNotEmpty()) Text(err,
                    color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
            }
        },
        confirmButton = {
            TextButton(onClick = {
                err = ""
                if (name.isBlank()) { err = "الاسم مطلوب"; return@TextButton }
                if (!FormulaEngine.isValid(countFormula, varsMap)) {
                    countErr = "صيغة العدد غير صحيحة"
                    return@TextButton
                }
                if (!FormulaEngine.isValid(lengthFormula, varsMap)) {
                    lengthErr = "صيغة الطول غير صحيحة"
                    return@TextButton
                }
                val a1 = angle1.toIntOrNull() ?: 90
                val a2 = angle2.toIntOrNull() ?: 90
                onSave(CutPiece(
                    id = existing?.id ?: 0,
                    name = name.trim(),
                    countFormula = countFormula.trim(),
                    lengthFormula = lengthFormula.trim(),
                    angle1 = a1, angle2 = a2,
                    barType = barType.trim().ifEmpty { "Cadre" }
                ))
            }) { Text("حفظ") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("إلغاء") } }
    )
}
