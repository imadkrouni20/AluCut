package com.example.alucut.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.alucut.data.ProfileConfig
import com.example.alucut.data.WindowInput

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InputScreen(
    onCalculate: (WindowInput, ProfileConfig) -> Unit
) {
    var countText by remember { mutableStateOf("1") }
    var widthText by remember { mutableStateOf("") }
    var heightText by remember { mutableStateOf("") }

    var advanced by remember { mutableStateOf(false) }
    var frameWidth by remember { mutableStateOf("3.5") }
    var innerVertical by remember { mutableStateOf("3.5") }
    var topBottom by remember { mutableStateOf("2.5") }
    var barLength by remember { mutableStateOf("600") }
    var kerf by remember { mutableStateOf("0.3") }

    var error by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("نجارة الألمنيوم", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        bottomBar = {
            Surface(color = MaterialTheme.colorScheme.surface, tonalElevation = 3.dp) {
                Button(
                    onClick = {
                        error = ""
                        val c = countText.toIntOrNull()
                        val w = widthText.toDoubleOrNull()
                        val h = heightText.toDoubleOrNull()
                        when {
                            c == null || c <= 0 -> error = "عدد النوافذ غير صحيح"
                            w == null || w <= 0 -> error = "العرض غير صحيح"
                            h == null || h <= 0 -> error = "العلو غير صحيح"
                            else -> {
                                val cfg = ProfileConfig(
                                    frameProfileWidthCm = frameWidth.toDoubleOrNull() ?: 3.5,
                                    innerVerticalWidthCm = innerVertical.toDoubleOrNull() ?: 3.5,
                                    topBottomDepthCm = topBottom.toDoubleOrNull() ?: 2.5,
                                    barLengthCm = barLength.toDoubleOrNull() ?: 600.0,
                                    kerfCm = kerf.toDoubleOrNull() ?: 0.3
                                )
                                onCalculate(WindowInput(c, w, h), cfg)
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().padding(16.dp).height(58.dp),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text("احسب", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text("مدخلات النوافذ", fontWeight = FontWeight.Bold, fontSize = 17.sp)
            OutlinedTextField(
                value = countText, onValueChange = { countText = it },
                label = { Text("عدد النوافذ") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                shape = RoundedCornerShape(12.dp)
            )
            OutlinedTextField(
                value = widthText, onValueChange = { widthText = it },
                label = { Text("العرض (سم)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                shape = RoundedCornerShape(12.dp)
            )
            OutlinedTextField(
                value = heightText, onValueChange = { heightText = it },
                label = { Text("العلو (سم)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                shape = RoundedCornerShape(12.dp)
            )

            if (error.isNotEmpty()) {
                Surface(
                    color = MaterialTheme.colorScheme.errorContainer,
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        text = error,
                        modifier = Modifier.fillMaxWidth().padding(12.dp),
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }

            HorizontalDivider()
            TextButton(onClick = { advanced = !advanced }) {
                Text(if (advanced) "▲ إخفاء الإعدادات المتقدمة" else "▼ إعدادات متقدمة")
            }

            if (advanced) {
                Text("أبعاد البروفيلات", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                OutlinedTextField(
                    value = frameWidth, onValueChange = { frameWidth = it },
                    label = { Text("عرض بروفيل الإطار (سم)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    shape = RoundedCornerShape(12.dp)
                )
                OutlinedTextField(
                    value = innerVertical, onValueChange = { innerVertical = it },
                    label = { Text("عرض Port Verreaux (سم)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    shape = RoundedCornerShape(12.dp)
                )
                OutlinedTextField(
                    value = topBottom, onValueChange = { topBottom = it },
                    label = { Text("عمق الإطار العلوي/السفلي (سم)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    shape = RoundedCornerShape(12.dp)
                )
                HorizontalDivider()
                Text("إعدادات القص", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                OutlinedTextField(
                    value = barLength, onValueChange = { barLength = it },
                    label = { Text("طول العمود (سم)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    shape = RoundedCornerShape(12.dp)
                )
                OutlinedTextField(
                    value = kerf, onValueChange = { kerf = it },
                    label = { Text("سمك المنشار (سم)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    shape = RoundedCornerShape(12.dp)
                )
            }
        }
    }
}
