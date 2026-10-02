package com.example.alucut.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
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
import com.example.alucut.ui.TemplateIllustration

@OptIn(ExperimentalMaterial3Api::class)
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

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(template.name, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "رجوع")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        bottomBar = {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 6.dp
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(12.dp),
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
                                    onAddItem(
                                        InputItem(
                                            id = 0,
                                            templateId = template.id,
                                            templateName = template.name,
                                            templateType = template.type,
                                            templateCategory = template.category,
                                            count = c,
                                            widthCm = w,
                                            heightCm = h
                                        )
                                    )
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
                        Icon(Icons.Default.Add, contentDescription = null)
                        Spacer(Modifier.width(6.dp))
                        Text("إضافة المقاس الحالي", fontSize = 16.sp,
                            fontWeight = FontWeight.Bold)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = onAddAnotherType,
                            modifier = Modifier.weight(1f).height(52.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.Layers, contentDescription = null)
                            Spacer(Modifier.width(4.dp))
                            Text("نوع آخر", fontSize = 14.sp)
                        }

                        Button(
                            onClick = onCalculate,
                            enabled = items.isNotEmpty(),
                            modifier = Modifier.weight(1f).height(52.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.Calculate, contentDescription = null)
                            Spacer(Modifier.width(4.dp))
                            Text("احسب (${items.size})", fontSize = 14.sp)
                        }
                    }
                }
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier.fillMaxSize().padding(padding)
                .background(Color(0xFF0A0A0A))
        ) {
            TemplateIllustration(
                type = template.type,
                modifier = Modifier.fillMaxSize()
                    .padding(top = 60.dp, bottom = 40.dp)
            )

            if (items.isNotEmpty()) {
                Row(
                    modifier = Modifier.align(Alignment.TopStart)
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 10.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items.forEach { it ->
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier.height(32.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(start = 10.dp, end = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${it.count}× (%.0f×%.0f)".format(it.widthCm, it.heightCm),
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    fontWeight = FontWeight.Bold
                                )
                                IconButton(
                                    onClick = { onRemoveItem(it.id) },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(Icons.Default.Close, contentDescription = "حذف",
                                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                        modifier = Modifier.size(14.dp))
                                }
                            }
                        }
                    }
                }
            }

            FloatingInput(
                value = widthText,
                onValueChange = { widthText = it },
                label = "العرض (سم)",
                modifier = Modifier.align(Alignment.TopCenter)
                    .padding(top = if (items.isEmpty()) 16.dp else 60.dp)
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

            if (error.isNotEmpty()) {
                Surface(
                    modifier = Modifier.align(Alignment.BottomCenter)
                        .padding(bottom = 12.dp, start = 16.dp, end = 16.dp),
                    color = MaterialTheme.colorScheme.errorContainer,
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(error, modifier = Modifier.padding(10.dp),
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        fontSize = 13.sp)
                }
            }

            snackbar?.let { msg ->
                LaunchedEffect(msg) {
                    kotlinx.coroutines.delay(1500)
                    snackbar = null
                }
                Surface(
                    modifier = Modifier.align(Alignment.BottomCenter)
                        .padding(bottom = 12.dp),
                    color = MaterialTheme.colorScheme.secondary,
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text(msg, modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
                        color = MaterialTheme.colorScheme.onSecondary,
                        fontWeight = FontWeight.Bold, fontSize = 13.sp)
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
