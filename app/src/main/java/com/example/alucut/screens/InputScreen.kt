package com.example.alucut.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.alucut.data.InputItem
import com.example.alucut.data.Template
import com.example.alucut.ui.FloatingBackButton
import com.example.alucut.ui.TemplateIllustration

@Composable
fun InputScreen(
    template: Template,
    items: List<InputItem>,
    onBack: () -> Unit,
    onAddItem: (InputItem) -> Unit,
    onRemoveItem: (Int) -> Unit,
    onAddAnotherType: () -> Unit,
    onCalculate: () -> Unit
) {
    var widthText by remember { mutableStateOf("") }
    var heightText by remember { mutableStateOf("") }
    var countText by remember { mutableStateOf("1") }
    var error by remember { mutableStateOf("") }
    var snackbar by remember { mutableStateOf<String?>(null) }

    Box(Modifier.fillMaxSize().background(Color(0xFF0A0A0A))) {
        // الرسم التوضيحي
        TemplateIllustration(
            type = template.type,
            modifier = Modifier.fillMaxSize().padding(top = 60.dp, bottom = 140.dp)
        )

        // زر الرجوع
        FloatingBackButton(onBack = onBack, modifier = Modifier.align(Alignment.TopStart))

        // اسم القالب (بدون شريط)
        Surface(
            modifier = Modifier.align(Alignment.TopCenter).padding(top = 16.dp),
            shape = RoundedCornerShape(20.dp),
            color = Color(0xCC1E1E1E)
        ) {
            Text(
                template.name,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        }

        // Chips
        if (items.isNotEmpty()) {
            Row(
                Modifier.align(Alignment.TopStart).fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(start = 70.dp, end = 10.dp, top = 60.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items.forEach { it ->
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier.height(30.dp)
                    ) {
                        Row(
                            Modifier.padding(start = 10.dp, end = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("${it.count}× (%.0f×%.0f)".format(it.widthCm, it.heightCm),
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                fontWeight = FontWeight.Bold)
                            IconButton(onClick = { onRemoveItem(it.id) },
                                modifier = Modifier.size(22.dp)) {
                                Icon(Icons.Default.Close, "حذف",
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.size(12.dp))
                            }
                        }
                    }
                }
            }
        }

        // حقول الإدخال
        FloatingInput(
            value = widthText,
            onValueChange = { widthText = it },
            label = "العرض (سم)",
            modifier = Modifier.align(Alignment.TopCenter).padding(top = 100.dp)
        )
        FloatingInput(
            value = heightText,
            onValueChange = { heightText = it },
            label = "العلو (سم)",
            modifier = Modifier.align(Alignment.CenterEnd).padding(end = 8.dp)
        )
        FloatingInput(
            value = countText,
            onValueChange = { countText = it },
            label = "العدد",
            modifier = Modifier.align(Alignment.Center)
        )

        // خطأ
        if (error.isNotEmpty()) {
            Surface(
                modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 130.dp),
                color = MaterialTheme.colorScheme.errorContainer,
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(error, Modifier.padding(10.dp),
                    color = MaterialTheme.colorScheme.onErrorContainer, fontSize = 13.sp)
            }
        }

        // Snackbar
        snackbar?.let { msg ->
            LaunchedEffect(msg) {
                kotlinx.coroutines.delay(1500)
                snackbar = null
            }
            Surface(
                modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 130.dp),
                color = MaterialTheme.colorScheme.secondary,
                shape = RoundedCornerShape(20.dp)
            ) {
                Text(msg, Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
                    color = MaterialTheme.colorScheme.onSecondary,
                    fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }
        }

        // الأزرار السفلية
        Surface(
            modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth(),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            Column(
                Modifier.fillMaxWidth().padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = {
                        error = ""
                        val w = widthText.toDoubleOrNull()
                        val h = heightText.toDoubleOrNull()
                        val c = countText.toIntOrNull()
                        when {
                            w == null || w <= 0 -> error = "العرض غير صحيح"
                            h == null || h <= 0 -> error = "العلو غير صحيح"
                            c == null || c <= 0 -> error = "العدد غير صحيح"
                            else -> {
                                onAddItem(InputItem(0, template.id, template.name,
                                    template.type, template.category, c, w, h))
                                widthText = ""
                                heightText = ""
                                countText = "1"
                                snackbar = "✓ تمت الإضافة"
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Add, null)
                    Spacer(Modifier.width(6.dp))
                    Text("إضافة المقاس", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }

                Row(Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = onAddAnotherType,
                        modifier = Modifier.weight(1f).height(48.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Layers, null, Modifier.size(18.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("نوع آخر", fontSize = 13.sp)
                    }
                    Button(
                        onClick = onCalculate,
                        enabled = items.isNotEmpty(),
                        modifier = Modifier.weight(1f).height(48.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Calculate, null, Modifier.size(18.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("احسب (${items.size})", fontSize = 13.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun FloatingInput(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.width(120.dp),
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.92f),
        shadowElevation = 6.dp
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label, fontSize = 11.sp) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = Color.Transparent,
                focusedBorderColor = MaterialTheme.colorScheme.primary
            ),
            textStyle = androidx.compose.ui.text.TextStyle(
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface
            )
        )
    }
}
